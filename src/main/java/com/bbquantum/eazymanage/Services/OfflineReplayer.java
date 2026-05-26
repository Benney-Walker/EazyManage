package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.DTO.Offline.AttendancePayload;
import com.bbquantum.eazymanage.DTO.Offline.FeesPaymentPayload;
import com.bbquantum.eazymanage.DTO.Offline.ScoresPayload;
import com.bbquantum.eazymanage.Utitity.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.net.URLEncoder.encode;

public class OfflineReplayer {

    private static final OfflineReplayer INSTANCE = new OfflineReplayer();

    public static OfflineReplayer getInstance() {
        return INSTANCE;
    }

    private final ApiCore core = new ApiCore();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    private final BooleanProperty active = new SimpleBooleanProperty(false);

    private final ScheduledExecutorService scheduler;

    private OfflineReplayer() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "eazymanage-offline-replay");
            t.setDaemon(true);
            return t;
        });
    }

    public ReadOnlyBooleanProperty activeProperty() {
        return active;
    }

    /**
     * Trigger a drain. Returns immediately; the drain runs on the replayer's
     * background thread. Calling while a drain is already in flight is a no-op.
     */
    public void drainNow() {
        if (active.get()) return;
        scheduler.submit(this::drainBlocking);
    }

    /**
     * Implementation: loop through pending rows, attempt each, update the
     * row's state. Stops early if one operation throws an unexpected
     * exception (vs. a non-2xx response, which is just recorded as a failure
     * and replay continues to the next row).
     */
    private void drainBlocking() {
        Platform.runLater(() -> active.set(true));
        try {
            List<OfflineQueue.QueuedOp> rows = OfflineQueue.getInstance().listPending();
            for (OfflineQueue.QueuedOp row : rows) {
                try {
                    int status = replay(row);
                    if (status >= 200 && status < 300) {
                        OfflineQueue.getInstance().markSent(row.id());
                    } else {
                        OfflineQueue.getInstance().recordFailure(row.id(),
                                "HTTP " + status);
                    }
                } catch (Exception ex) {
                    OfflineQueue.getInstance().recordFailure(row.id(),
                            ex.getClass().getSimpleName() + ": " + ex.getMessage());
                    // Keep going — one bad row shouldn't stall the whole drain.
                }
            }
        } finally {
            Platform.runLater(() -> active.set(false));
        }
    }

    /**
     * Dispatch one queued row to the right HTTP call. Returns the HTTP status
     * code so the caller can decide whether to mark sent or record failure.
     */
    private int replay(OfflineQueue.QueuedOp row) throws Exception {
        return switch (row.opType()) {
            case OfflineQueue.OP_ATTENDANCE   -> replayAttendance(row.payloadJson());
            case OfflineQueue.OP_SAVE_SCORES  -> replaySaveScores(row.payloadJson());
            case OfflineQueue.OP_FEES_PAYMENT -> replayFeesPayment(row.payloadJson());
            default -> throw new IllegalStateException("Unknown op type: " + row.opType());
        };
    }

    // ====================================================================
    // Per-op-type replay (mirrors the inline HTTP code in each service).
    // ====================================================================

    private int replayAttendance(String payloadJson) throws Exception {
        AttendancePayload p = gson.fromJson(payloadJson, AttendancePayload.class);

        String url = core.STAFF_URL + core.API_VERSION_1 + "/update-attendance?" +
                "studentId=" + enc(p.studentId) +
                "&staffId="    + enc(p.staffId) +
                "&levelId="    + enc(p.levelId) +
                "&status="     + enc(p.status) +
                "&dateMarked=" + enc(p.dateMarked);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + core.token)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        return core.httpClient.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
    }

    private int replaySaveScores(String payloadJson) throws Exception {
        ScoresPayload p = gson.fromJson(payloadJson, ScoresPayload.class);

        String url = core.STUDENT_URL + core.API_VERSION_1 + "/save-subject-scores";

        // Hand-build the JSON so the wire format matches what
        // AcademicsService.saveStudentScores sends. Field set tracks the
        // Phase 4 rename — keep these keys aligned with the live call.
        JsonObject body = new JsonObject();
        body.addProperty("studentId",          p.scores.getStudentId());
        body.addProperty("classTest1Score",    p.scores.getClassTest1Score());
        body.addProperty("groupWorkScore",     p.scores.getGroupWorkScore());
        body.addProperty("classTest2Score",    p.scores.getClassTest2Score());
        body.addProperty("projectScore",       p.scores.getProjectScore());
        body.addProperty("classScore",         p.scores.getClassScore());
        body.addProperty("examScore",          p.scores.getExamScore());
        body.addProperty("calculatedExamScore",p.scores.getCalculatedExamScore());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .headers("Authorization", "Bearer " + core.token,
                        "Content-Type", "application/json",
                        "subjectId",  p.subjectId,
                        "semesterId", p.semesterId,
                        "staffId",    p.staffId)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        return core.httpClient.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
    }

    private int replayFeesPayment(String payloadJson) throws Exception {
        FeesPaymentPayload p = gson.fromJson(payloadJson, FeesPaymentPayload.class);

        String url = core.FINANCE_URL + core.API_VERSION_2 + "/add-fees-payment";

        JsonObject body = new JsonObject();
        body.addProperty("studentId",  p.studentId);
        body.addProperty("semesterId", p.semesterId);
        body.addProperty("amountPaid", p.amountPaid);
        body.addProperty("payerName",  p.payerName);
        body.addProperty("payerPhone", p.payerPhone);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .headers("Authorization", "Bearer " + core.token,
                        "Content-Type", "application/json",
                        "staffId", p.staffId)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        return core.httpClient.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
    }

    private static String enc(String s) {
        return encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
