package com.bbquantum.eazymanage.Shell;

import com.bbquantum.eazymanage.Services.OfflineMode;
import com.bbquantum.eazymanage.Services.OfflineQueue;
import com.bbquantum.eazymanage.Services.OfflineReplayer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OfflineSettingsController {

    @FXML private ToggleButton offlineToggle;
    @FXML private Label toggleHintLabel;
    @FXML private Label pendingCountLabel;
    @FXML private Label lastSyncLabel;
    @FXML private Button drainButton;
    @FXML private Button refreshButton;
    @FXML private Label  drainHintLabel;

    @FXML private TableView<OfflineQueue.QueuedOp> pendingTable;
    @FXML private TableColumn<OfflineQueue.QueuedOp, String> opTypeCol;
    @FXML private TableColumn<OfflineQueue.QueuedOp, String> createdAtCol;
    @FXML private TableColumn<OfflineQueue.QueuedOp, String> retryCountCol;
    @FXML private TableColumn<OfflineQueue.QueuedOp, String> lastAttemptCol;
    @FXML private TableColumn<OfflineQueue.QueuedOp, String> lastErrorCol;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private final ObservableList<OfflineQueue.QueuedOp> rows = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Toggle ↔ OfflineMode
        offlineToggle.setSelected(OfflineMode.getInstance().isEnabled());
        offlineToggle.setText(OfflineMode.getInstance().isEnabled() ? "On" : "Off");
        offlineToggle.selectedProperty().bindBidirectional(OfflineMode.getInstance().enabledProperty());
        offlineToggle.selectedProperty().addListener((obs, oldVal, newVal) ->
                offlineToggle.setText(newVal ? "On" : "Off"));

        // Pending count — bound straight to the property; updates live.
        pendingCountLabel.textProperty().bind(
                Bindings.format("%d", OfflineQueue.getInstance().pendingCountProperty()));

        // Drain button disabled when there's nothing to drain or a drain is
        // already in flight.
        drainButton.disableProperty().bind(Bindings.or(
                OfflineQueue.getInstance().pendingCountProperty().isEqualTo(0),
                OfflineReplayer.getInstance().activeProperty()));

        // Table
        opTypeCol.setCellValueFactory(c -> new SimpleStringProperty(prettyOpType(c.getValue().opType())));
        createdAtCol.setCellValueFactory(c -> new SimpleStringProperty(
                FMT.format(Instant.ofEpochMilli(c.getValue().createdAtEpochMs()))));
        retryCountCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().retryCount())));
        lastAttemptCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().lastAttemptEpochMs() == null
                        ? "—"
                        : FMT.format(Instant.ofEpochMilli(c.getValue().lastAttemptEpochMs()))));
        lastErrorCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().lastError() == null ? "" : c.getValue().lastError()));
        pendingTable.setItems(rows);

        // First load
        refreshRows();
    }

    @FXML
    private void onRefresh() {
        refreshRows();
    }

    @FXML
    private void onDrain() {
        // Run the drain itself on the replayer's own thread; refresh the
        // table from a small follow-up Task so the UI updates without
        // blocking.
        Task<Void> waiter = new Task<>() {
            @Override
            protected Void call() {
                OfflineReplayer.getInstance().drainNow();
                // Poll until the replayer reports it's no longer active.
                while (OfflineReplayer.getInstance().activeProperty().get()) {
                    try { Thread.sleep(150); }
                    catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
                }
                return null;
            }
        };
        waiter.setOnSucceeded(e -> {
            refreshRows();
            lastSyncLabel.setText(FMT.format(Instant.now()));
        });
        Thread t = new Thread(waiter, "eazymanage-offline-drain-waiter");
        t.setDaemon(true);
        t.start();
    }

    private void refreshRows() {
        // Always refresh from the FX thread so the binding listeners fire on
        // the same thread that subsequently renders the table.
        if (Platform.isFxApplicationThread()) {
            doRefresh();
        } else {
            Platform.runLater(this::doRefresh);
        }
    }

    private void doRefresh() {
        List<OfflineQueue.QueuedOp> pending = OfflineQueue.getInstance().listPending();
        rows.setAll(pending);
    }

    private static String prettyOpType(String op) {
        if (op == null) return "";
        return switch (op) {
            case OfflineQueue.OP_ATTENDANCE   -> "Attendance";
            case OfflineQueue.OP_SAVE_SCORES  -> "Score save";
            case OfflineQueue.OP_FEES_PAYMENT -> "Fees payment";
            default -> op;
        };
    }
}
