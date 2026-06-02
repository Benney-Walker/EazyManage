package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.DTO.Academics.Conduct.StudentConductRecord;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.concurrent.Task;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;

public class ConductService {

    private final ApiCore core;

    public ConductService(ApiCore core) {
        this.core   = core;
    }

    public void loadClassConduct(String levelId, String semesterId, String staffId,
                                 Consumer<List<StudentConductRecord>> onResult,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Type listType = new TypeToken<List<StudentConductRecord>>() {}.getType();

        Task<List<StudentConductRecord>> task = new Task<>() {
            @Override
            protected List<StudentConductRecord> call() throws Exception {
                String url = core.STAFF_URL + core.API_VERSION_1 + "/conduct-records?" +
                        "levelId=" + levelId +
                        "&semesterId=" + semesterId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), listType);
                }
                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<StudentConductRecord> result = task.getValue();
            onResult.accept(result);
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
            ApiContext.getInstance().errorMessageContainer = "Could not load conduct records";
            onFailure.run();
        });

        new Thread(task).start();
    }


    public void saveStudentConduct(StudentConductRecord record, String staffId,
                                   Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.STAFF_URL + core.API_VERSION_1 + "/conduct-record";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("studentId", record.getStudentId());
                requestBody.addProperty("studentName", record.getStudentName());
                requestBody.addProperty("semesterId", record.getSemesterId());
                requestBody.addProperty("regular", record.getRegular());
                requestBody.addProperty("punctual", record.getPunctual());
                requestBody.addProperty("physicalAppearance", record.getPhysicalAppearance());
                requestBody.addProperty("social", record.getSocial());
                requestBody.addProperty("emotional", record.getEmotional());
                requestBody.addProperty("cognitiveSkills",    record.getCognitiveSkills());
                requestBody.addProperty("conductRemark", record.getConductRemark());

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
                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String status = task.getValue();
            if (status == null) {
                onFailure.run();
                return;
            }
            ApiContext.getInstance().successMessageContainer = "Conduct record saved";
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
            ApiContext.getInstance().errorMessageContainer = "Could not save conduct record";
            onFailure.run();
        });

        new Thread(task).start();
    }

    private static void addNullable(JsonObject obj, String key, Boolean value) {
        if (value == null) {
            obj.add(key, com.google.gson.JsonNull.INSTANCE);
        } else {
            obj.addProperty(key, value);
        }
    }
}
