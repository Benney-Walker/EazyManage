package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.DTO.Offline.*;
import com.bbquantum.eazymanage.Offline.OfflineQueue;
import com.bbquantum.eazymanage.Utitity.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    //Identity
    private String staffId;

    //Sorted Pending list
    private List<Long> attendanceIds = new ArrayList<>();
    private List<OfflineAttendanceReplay> attendanceList = new ArrayList<>();

    private List<Long> paymentIds = new ArrayList<>();
    private List<OfflinePaymentReplay> paymentList = new ArrayList<>();

    private List<Long> scoresIds = new ArrayList<>();
    private List<OfflineScoresReplay> scoresList = new ArrayList<>();

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

    public void drainNow() {
        if (active.get()) return;
        scheduler.submit(this::drainBlocking);
    }

    private void drainBlocking() {
        Platform.runLater(() -> active.set(true));
        attendanceIds.clear();
        paymentIds.clear();
        scoresIds.clear();

        attendanceList.clear();
        paymentList.clear();
        scoresList.clear();

        try {
            List<OfflineQueue.QueuedOp> rows = OfflineQueue.getInstance().listPending();
            for (OfflineQueue.QueuedOp row : rows) {
                sortPending(row);

                /*try {


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
                }*/
            }

            if (!attendanceList.isEmpty()) {
                //Attendance replay
                try {
                    int status = replayAttendance(attendanceList);
                    if (status >= 200 && status < 300) {
                        for (Long i : attendanceIds) {
                            OfflineQueue.getInstance().markSent(i);
                        }
                    } else {
                        for (Long i : attendanceIds) {
                            OfflineQueue.getInstance().recordFailure(i,
                                    "HTTP " + status);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            if (!scoresList.isEmpty()) {
                //Scores replay
                try {
                    int status = replaySaveScores(scoresList);
                    if (status >= 200 && status < 300) {
                        for (Long i : scoresIds) {
                            OfflineQueue.getInstance().markSent(i);
                        }
                    } else {
                        for (Long i : scoresIds) {
                            OfflineQueue.getInstance().recordFailure(i,
                                    "HTTP " + status);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            if (!paymentList.isEmpty()) {
                //Payment replay
                try {
                    int status = replayFeesPayment(paymentList);
                    if (status >= 200 && status < 300) {
                        for (Long i : paymentIds) {
                            OfflineQueue.getInstance().markSent(i);
                        }
                    } else {
                        for (Long i : paymentIds) {
                            OfflineQueue.getInstance().recordFailure(i,
                                    "HTTP " + status);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            Platform.runLater(() -> active.set(false));
        }
    }

    private int replay(OfflineQueue.QueuedOp row) throws Exception {
        return switch (row.opType()) {
            case OfflineQueue.OP_ATTENDANCE   -> replayAttendance(row.payloadJson());
            case OfflineQueue.OP_SAVE_SCORES  -> replaySaveScores(row.payloadJson());
            case OfflineQueue.OP_FEES_PAYMENT -> replayFeesPayment(row.payloadJson());
            default -> throw new IllegalStateException("Unknown op type: " + row.opType());
        };
    }

    private void sortPending(OfflineQueue.QueuedOp row) {
        switch (row.opType()) {
            case OfflineQueue.OP_ATTENDANCE -> {
                AttendancePayload payload = gson.fromJson(row.payloadJson(), AttendancePayload.class);

                OfflineAttendanceReplay replay = new OfflineAttendanceReplay(
                        payload.staffId,
                        payload.studentId,
                        payload.levelId,
                        payload.status,
                        payload.dateMarked
                );

                attendanceList.add(replay);
                attendanceIds.add(row.id());
            }
            case OfflineQueue.OP_FEES_PAYMENT -> {
                FeesPaymentPayload payload = gson.fromJson(row.payloadJson(), FeesPaymentPayload.class);

                OfflinePaymentReplay replay = new OfflinePaymentReplay(
                        payload.studentId,
                        payload.classId,
                        payload.semesterId,
                        payload.amountPaid,
                        payload.payerName,
                        payload.payerPhone,
                        payload.staffId
                );

                paymentList.add(replay);
                paymentIds.add(row.id());
            }
            case OfflineQueue.OP_SAVE_SCORES -> {
                ScoresPayload payload = gson.fromJson(row.payloadJson(), ScoresPayload.class);

                OfflineScoresReplay replay = new OfflineScoresReplay(
                        payload.subjectId,
                        payload.semesterId,
                        payload.scores,
                        payload.staffId
                );

                scoresList.add(replay);
                scoresIds.add(row.id());
            }
            case null, default -> throw new IllegalStateException("Unknown op type: " + row.opType());
        }
    }

    // ====================================================================
    // Per-op-type replay (mirrors the inline HTTP code in each service).
    // ====================================================================

    private int replayAttendance(String payloadJson) throws Exception {
        AttendancePayload p = gson.fromJson(payloadJson, AttendancePayload.class);

        String url = core.STUDENT_URL + core.API_VERSION_2 + "/mark-attendance";

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("studentId", enc(p.studentId));
        requestBody.addProperty("levelId", enc(p.levelId));
        requestBody.addProperty("status", enc(p.status));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .headers(
                        "Authorization", "Bearer " + core.token,
                        "Content-Type", "application/json",
                        "staffId", enc(p.staffId),
                        "selectedDate", enc(p.dateMarked)
                )
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        return core.httpClient.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
    }

    private int replayAttendance(List<OfflineAttendanceReplay> list) throws Exception {

        String url = core.OFFLINE_URL + core.API_VERSION_1 + "/sync-attendance";

        JsonArray requestBody = new JsonArray();
        Gson gson = new Gson();
        if (list != null && !list.isEmpty()) {
            for (OfflineAttendanceReplay replay : list) {
                requestBody.add(gson.toJsonTree(replay));
            }
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .headers(
                        "Authorization", "Bearer " + core.token,
                        "Content-Type", "application/json",
                        "staffId", staffId
                )
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        return core.httpClient.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
    }

    private int replaySaveScores(String payloadJson) throws Exception {
        ScoresPayload p = gson.fromJson(payloadJson, ScoresPayload.class);

        String url = core.STUDENT_URL + core.API_VERSION_1 + "/save-subject-scores";

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

    private int replaySaveScores(List<OfflineScoresReplay> list) throws Exception {

        String url = core.OFFLINE_URL + core.API_VERSION_1 + "/sync-scores";

        JsonArray requestBody = new JsonArray();
        Gson gson = new Gson();
        if (list != null && !list.isEmpty()) {
            for (OfflineScoresReplay replay : list) {
                requestBody.add(gson.toJsonTree(replay));
            }
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .headers("Authorization", "Bearer " + core.token,
                        "Content-Type", "application/json",
                        "staffId", staffId
                )
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
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
        body.addProperty("levelId", p.classId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .headers("Authorization", "Bearer " + core.token,
                        "Content-Type", "application/json",
                        "staffId", p.staffId)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        return core.httpClient.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
    }

    private int replayFeesPayment(List<OfflinePaymentReplay> list) throws Exception {

        String url = core.OFFLINE_URL + core.API_VERSION_1 + "/sync-payment";

        JsonArray requestBody = new JsonArray();
        Gson gson = new Gson();
        if (list != null && !list.isEmpty()) {
            for (OfflinePaymentReplay replay : list) {
                requestBody.add(gson.toJsonTree(replay));
            }
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .headers("Authorization", "Bearer " + core.token,
                        "Content-Type", "application/json",
                        "staffId", staffId
                )
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();
        return core.httpClient.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    private static String enc(String s) {
        return encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
