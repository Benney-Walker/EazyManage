package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.DTO.Attendance.DatesMarked;
import com.bbquantum.eazymanage.DTO.Attendance.StudentAttendance;
import com.bbquantum.eazymanage.DTO.Offline.AttendancePayload;
import com.bbquantum.eazymanage.Offline.OfflineMode;
import com.bbquantum.eazymanage.Offline.OfflineQueue;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;

public class AttendanceService {

    private final ApiCore apiCore;

    public AttendanceService(ApiCore apiCore) {
        this.apiCore = apiCore;
    }


    public void loadStudentsForAttendance(String staffId, String levelId, String date,
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
                                "Authorization", "Bearer " + apiCore.token,
                                "staffId", staffId
                        )
                        .GET()
                        .build();

                HttpResponse<String> response = apiCore.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return apiCore.gson.fromJson(response.body(), listType);
                }

                if (apiCore.handleSessionExpired(response)) return null;
                apiCore.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<StudentAttendance> result = task.getValue();
            if (result == null) {
                onFailure.run();
                return;
            }

            onResult.accept(result);
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
    public void markStudentAttendance(String staffId, String levelId, String currentDate,
                                      List<StudentAttendance> records,
                                      Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        if (OfflineMode.getInstance().isEnabled()) {
            try {
                for (StudentAttendance record : records) {
                    String payload = new com.google.gson.Gson().toJson(
                            new AttendancePayload(
                                    staffId,
                                    record.getStudentId(),
                                    levelId,
                                    record.getStatus(),
                                    currentDate,
                                    "mark"
                            )
                    );

                    OfflineQueue.getInstance().enqueue(OfflineQueue.OP_ATTENDANCE, payload);
                }

                ApiContext.getInstance().successMessageContainer =
                        "Saved offline — will sync when online";
                Platform.runLater(onSuccess);
            } catch (Exception e) {
                e.printStackTrace();
                ApiContext.getInstance().errorMessageContainer =
                        "Could not queue attendance: " + e.getMessage();
                Platform.runLater(onFailure);
            }
            return;
        }

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = apiCore.STUDENT_URL + apiCore.API_VERSION_2 + "/mark-attendance";

                Gson gson = new Gson();
                JsonArray jsonArray = new JsonArray();
                if (records != null) {
                    for (StudentAttendance record : records) {
                        jsonArray.add(gson.toJsonTree(record));
                    }
                }

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers(
                                "Authorization", "Bearer " + apiCore.token,
                                "staffId", staffId,
                                "selectedDate", currentDate,
                                "levelId", levelId
                        )
                        .POST(HttpRequest.BodyPublishers.ofString(jsonArray.toString()))
                        .build();

                HttpResponse<String> response = apiCore.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                if (apiCore.handleSessionExpired(response)) return null;
                apiCore.checkApiResponse(response);
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

    public void loadAttendanceRecords(String staffId, String levelId, String semesterId, String date, Consumer<List<StudentAttendance>> onResult,
                                      Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Type listType = new TypeToken<List<StudentAttendance>>() {}.getType();

        Task<List<StudentAttendance>> task = new Task<>() {
            @Override
            protected List<StudentAttendance> call() throws Exception {
                String url = apiCore.STUDENT_URL + apiCore.API_VERSION_1 + "/attendance-records?" +
                        "levelId=" + levelId +
                        "&semesterId=" + semesterId +
                        "&date=" + date;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers(
                                "Authorization", "Bearer " + apiCore.token,
                                "staffId", staffId
                        )
                        .GET()
                        .build();

                HttpResponse<String> response = apiCore.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return apiCore.gson.fromJson(response.body(), listType);
                }

                if (apiCore.handleSessionExpired(response)) return null;
                apiCore.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<StudentAttendance> result = task.getValue();

            if (result == null) {
                onFailure.run();
                return;
            }

            onResult.accept(result);
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

    public void viewDatesMarked(String staffId, String levelId,
                                Consumer<List<DatesMarked>> onResult,
                                Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Type listType = new TypeToken<List<DatesMarked>>() {}.getType();

        Task<List<DatesMarked>> task = new Task<>() {
            @Override
            protected List<DatesMarked> call() throws Exception {
                String url = apiCore.STUDENT_URL + apiCore.API_VERSION_1 + "/dates-marked/" + levelId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers(
                                "Authorization", "Bearer " + apiCore.token,
                                "staffId", staffId
                        )
                        .GET()
                        .build();

                HttpResponse<String> response = apiCore.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return apiCore.gson.fromJson(response.body(), listType);
                }

                if (apiCore.handleSessionExpired(response)) return null;
                apiCore.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<DatesMarked> result = task.getValue();
            if (result == null) {
                onFailure.run();
                return;
            }

            onResult.accept(result);
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


}
