package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.DTO.Institution.UpdateStudentPersonalData;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import com.google.gson.JsonObject;
import javafx.concurrent.Task;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

public class StudentService {

    private final ApiCore core;

    public StudentService(ApiCore core) {
        this.core   = core;
    }

    /** Enroll a new student. */
    public void enrollNewStudent(String staffId, String firstName, String lastName, String levelId, String gender,
                                 LocalDate dateOfBirth, String hometown, String parentName, String guardianContact,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.STUDENT_URL + core.API_VERSION_1 + "/add-new-student";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("firstName", firstName);
                requestBody.addProperty("lastName", lastName);
                requestBody.addProperty("levelId", levelId);
                requestBody.addProperty("gender", gender);
                requestBody.addProperty("dateOfBirth", dateOfBirth.toString());
                requestBody.addProperty("hometown", hometown);
                requestBody.addProperty("parentName", parentName);
                requestBody.addProperty("guardianContact", guardianContact);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "Content-Type", "application/json",
                                "staffId", staffId)
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response != null) {
                ApiContext.getInstance().successMessageContainer = response;
                onSuccess.run();
                return;
            }
            onFailure.run();
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


    public void searchStudentData(String staffId, String studentId,
                                  Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Task<UpdateStudentPersonalData> task = new Task<>() {
            @Override
            protected UpdateStudentPersonalData call() throws Exception {
                String url = core.STUDENT_URL + core.API_VERSION_1 + "/find-student/" + studentId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), UpdateStudentPersonalData.class);
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            UtilityClass.updateStudentPersonalData = task.getValue();
            if (UtilityClass.updateStudentPersonalData == null) {
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
            ApiContext.getInstance().errorMessageContainer = "Could load student data";
            onFailure.run();
        });

        new Thread(task).start();
    }

    /** Persist edits to a student's personal record. */
    public void updateStudentPersonalData(UpdateStudentPersonalData data, String staffId,
                                          Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.STUDENT_URL + core.API_VERSION_1 + "/update-student-data";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("studentId", data.getStudentId());
                requestBody.addProperty("gender", data.getGender());
                requestBody.addProperty("dateOfBirth", data.getDateOfBirth());
                requestBody.addProperty("parentName", data.getParentName());
                requestBody.addProperty("parentPhoneNumber", data.getParentPhoneNumber());
                requestBody.addProperty("status", data.getStatus());
                requestBody.addProperty("firstName", data.getFirstName());
                requestBody.addProperty("lastName", data.getLastName());
                requestBody.addProperty("gradeId", data.getGradeId());
                requestBody.addProperty("homeTown", data.getHomeTown());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "Content-Type", "application/json",
                                "staffId", staffId)
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String status = task.getValue();
            if (status != null) {
                ApiContext.getInstance().successMessageContainer = status;
                onSuccess.run();
                return;
            }
            onFailure.run();
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
