package com.bbquantum.eazymanage.Offline;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OfflineQueue {

    private static final OfflineQueue INSTANCE = new OfflineQueue();

    public static OfflineQueue getInstance() {
        return INSTANCE;
    }

    // Op type constants — exposed so callers and the replayer agree on
    // exact strings. Don't rename without a migration.
    public static final String OP_ATTENDANCE   = "attendance";

    public static final String OP_SAVE_SCORES  = "scores";

    public static final String OP_FEES_PAYMENT = "fees-payment";

    private final Connection connection;

    private final IntegerProperty pendingCount = new SimpleIntegerProperty(0);

    private OfflineQueue() {
        try {
            // Ensure ~/.eazymanage exists and open the database file there.
            Path home = Path.of(System.getProperty("user.home"), ".eazymanage");
            Files.createDirectories(home);
            File dbFile = home.resolve("queue.db").toFile();

            this.connection = DriverManager.getConnection(
                    "jdbc:sqlite:" + dbFile.getAbsolutePath());
            this.connection.setAutoCommit(true);

            initSchema();
            purgeSent();        // discard previous-session sent rows
            refreshCount();
        } catch (Exception e) {
            // The queue is critical to Phase 7 correctness; surfacing the
            // exception so the offline UI can display a clear error is
            // better than silently dropping queued writes.
            throw new IllegalStateException("Could not initialise offline queue", e);
        }
    }

    private void initSchema() throws SQLException {
        try (Statement s = connection.createStatement()) {
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS queued_operations (
                    id              INTEGER PRIMARY KEY AUTOINCREMENT,
                    op_type         TEXT    NOT NULL,
                    payload         TEXT    NOT NULL,
                    created_at      INTEGER NOT NULL,
                    retry_count     INTEGER NOT NULL DEFAULT 0,
                    last_attempt_at INTEGER,
                    last_error      TEXT,
                    sent            INTEGER NOT NULL DEFAULT 0
                )
                """);
            s.executeUpdate("CREATE INDEX IF NOT EXISTS idx_pending ON queued_operations(sent, id)");
        }
    }

    /**
     * Append a new operation to the queue. Returns the assigned row id.
     */
    public synchronized long enqueue(String opType, String payloadJson) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO queued_operations(op_type, payload, created_at) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, opType);
            ps.setString(2, payloadJson);
            ps.setLong(3, Instant.now().toEpochMilli());
            ps.executeUpdate();
            long id;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                id = keys.next() ? keys.getLong(1) : -1;
            }
            refreshCount();
            return id;
        } catch (SQLException e) {
            throw new IllegalStateException("Could not enqueue " + opType, e);
        }
    }

    /** Pending rows in insertion order. */
    public synchronized List<QueuedOp> listPending() {
        List<QueuedOp> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT id, op_type, payload, created_at, retry_count, last_attempt_at, last_error " +
                        "FROM queued_operations WHERE sent = 0 ORDER BY id ASC")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(readRow(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not list pending operations", e);
        }
        return result;
    }

    private static QueuedOp readRow(ResultSet rs) throws SQLException {
        long id          = rs.getLong("id");
        String type      = rs.getString("op_type");
        String payload   = rs.getString("payload");
        long createdAt   = rs.getLong("created_at");
        int retryCount   = rs.getInt("retry_count");
        Long lastAttempt = rs.getObject("last_attempt_at") == null ? null : rs.getLong("last_attempt_at");
        String lastErr   = rs.getString("last_error");
        return new QueuedOp(id, type, payload, createdAt, retryCount, lastAttempt, lastErr);
    }

    // ====================================================================
    // Replay book-keeping (called by OfflineReplayer)
    // ====================================================================

    /** Mark a row as successfully sent. The row is retained until the next
     *  app startup (or an explicit {@link #purgeSent()} call). */
    public synchronized void markSent(long id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE queued_operations SET sent = 1, last_attempt_at = ?, last_error = NULL WHERE id = ?")) {
            ps.setLong(1, Instant.now().toEpochMilli());
            ps.setLong(2, id);
            ps.executeUpdate();
            refreshCount();
        } catch (SQLException e) {
            throw new IllegalStateException("Could not mark sent " + id, e);
        }
    }

    /** Increment retry count and stash the error text. The row stays pending. */
    public synchronized void recordFailure(long id, String errorMessage) {
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE queued_operations SET retry_count = retry_count + 1, " +
                        "last_attempt_at = ?, last_error = ? WHERE id = ?")) {
            ps.setLong(1, Instant.now().toEpochMilli());
            ps.setString(2, truncate(errorMessage, 500));
            ps.setLong(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Could not record failure for " + id, e);
        }
    }

    /** Remove all rows previously marked sent. Run at startup. */
    public synchronized int purgeSent() {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM queued_operations WHERE sent = 1")) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Could not purge sent rows", e);
        }
    }

    /** Total pending count, computed live. */
    public int pendingCount() { return pendingCount.get(); }
    public ReadOnlyIntegerProperty pendingCountProperty() { return pendingCount; }

    private void refreshCount() {
        int count = countPending();
        if (Platform.isFxApplicationThread()) {
            pendingCount.set(count);
        } else {
            Platform.runLater(() -> pendingCount.set(count));
        }
    }

    private int countPending() {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT COUNT(*) FROM queued_operations WHERE sent = 0")) {
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            return 0;
        }
    }

    private static String truncate(String s, int maxLen) {
        if (s == null) return null;
        return s.length() <= maxLen ? s : s.substring(0, maxLen);
    }

    // ====================================================================
    // QueuedOp record — the public shape returned by listPending()
    // ====================================================================

    public record QueuedOp(
            long id,
            String opType,
            String payloadJson,
            long createdAtEpochMs,
            int retryCount,
            Long lastAttemptEpochMs,
            String lastError
    ) {
        public Timestamp createdAt() { return new Timestamp(createdAtEpochMs); }
        public Timestamp lastAttempt() {
            return lastAttemptEpochMs == null ? null : new Timestamp(lastAttemptEpochMs);
        }
    }
}
