package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.DTO.Institution.StudentAttendance;
import com.bbquantum.eazymanage.DTO.Offline.AttendancePayload;
import com.bbquantum.eazymanage.Utitity.ApiUtilityClass;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.concurrent.Task;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;

public class AttendanceService {

    private final ApiCore apiCore;

    private final ApiUtilityClass facade;

    public AttendanceService(ApiCore apiCore, ApiUtilityClass facade) {
        this.apiCore = apiCore;
        this.facade = facade;
    }

    /**
     * Fetch the list of students for an attendance session.
     * Result is delivered via {@code onResult} on the FX thread before
     * {@code onSuccess} fires.
     */
    public void loadStudentsForAttendance(String levelId, String date,
                                          Consumer<List<StudentAttendance>> onResult,
                                          Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Type listType = new TypeToken<List<StudentAttendance>>() {}.getType();

        Task<List<StudentAttendance>> task = new Task<>() {
            @Override
            protected List<StudentAttendance> call() throws Exception {
                String url = apiCore.STUDENT_URL + apiCore.API_VERSION_1 + "/load-students-for-attendance/" +
                        levelId + "/" + date;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers(
                                "Authorization", "Bearer " + apiCore.token
                        )
                        .GET()
                        .build();

                HttpResponse<String> response = apiCore.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return apiCore.gson.fromJson(response.body(), listType);
                }

                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<StudentAttendance> result = task.getValue();
            onResult.accept(result);            // controller field assignment happens in the consumer
            if (result == null) {
                onFailure.run();
                return;
            }
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();
            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }
            onFailure.run();
        });

        new Thread(task).start();
    }

    /** Mark a student's attendance for the current day's session. */
    public void markStudentAttendance(String studentId, String staffId, String levelId, String status,
                                      Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = apiCore.STAFF_URL + apiCore.API_VERSION_1 + "/mark-attendance";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("studentId", studentId);
                requestBody.addProperty("staffId", staffId);
                requestBody.addProperty("levelId", levelId);
                requestBody.addProperty("status", status);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + apiCore.token,
                                "Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = apiCore.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response == null) {
                onFailure.run();
                return;
            }
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();
            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }
            onFailure.run();
        });

        new Thread(task).start();
    }

    /** Update a previously marked attendance entry. */
    public void updateAttendance(String studentId, String staffId, String levelId, String status, String dateMarked,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        // Phase 7: if offline-mode is on, persist to the local queue and
        // synthesise an immediate success. The UI sees the same callback
        // semantics it would have seen from a live backend.
        if (OfflineMode.getInstance().isEnabled()) {
            try {
                String payload = new com.google.gson.Gson().toJson(
                        new AttendancePayload(
                                studentId, staffId, levelId, status, dateMarked));
                OfflineQueue.getInstance().enqueue(OfflineQueue.OP_ATTENDANCE, payload);
                ApiContext.getInstance().successMessageContainer = "Saved offline — will sync when online";
                javafx.application.Platform.runLater(onSuccess);
            } catch (Exception e) {
                e.printStackTrace();
                ApiContext.getInstance().errorMessageContainer = "Could not queue attendance: " + e.getMessage();
                javafx.application.Platform.runLater(onFailure);
            }
            return;
        }

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = apiCore.STAFF_URL + apiCore.API_VERSION_1 + "/update-attendance?" +
                        "studentId=" + studentId +
                        "&staffId=" + staffId +
                        "&levelId=" + levelId +
                        "&status=" + status +
                        "&dateMarked=" + dateMarked;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + apiCore.token)
                        .PUT(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = apiCore.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }
                ApiContext.getInstance().errorMessageContainer = response.body();
                System.out.println(ApiContext.getInstance().errorMessageContainer);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response == null) {
                onFailure.run();
                return;
            }
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();
            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
            } else {
                // Preserves legacy behaviour: non-network failures also route to onNetworkIssue.
                onNetworkIssue.run();
            }
        });

        new Thread(task).start();
    }
}
