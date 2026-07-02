package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.DTO.Messaging.BroadcastSmsRequest;
import com.bbquantum.eazymanage.DTO.Messaging.BroadcastEmailRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.concurrent.Task;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public class MessagingService {

    private final ApiCore core;

    public MessagingService(ApiCore core) {
        this.core = core;
    }

    public void broadcastEmail(String staffId, BroadcastEmailRequest request,
                              Consumer<String> onSuccess,
                              Runnable onFailure, Runnable onNetworkIssue) {

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_1 + "/broadcast-email";
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("subject", request.getMailSubject());
                requestBody.addProperty("body", request.getMailBody());

                JsonArray jsonArray = new JsonArray();
                if (request.getAudience() != null) request.getAudience().forEach(jsonArray::add);
                requestBody.add("audience", jsonArray);

                HttpRequest http = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId",      staffId,
                                "Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = core.httpClient.send(http, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {

                    return "Emails successfully sent";
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String result = task.getValue();
            if (result != null) {
                onSuccess.accept(result);
                return;
            }

            onFailure.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            if (ex != null) ex.printStackTrace();

            String msg = ex == null ? null : ex.getMessage();
            if (ex instanceof java.net.ConnectException
                    || (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }
            ApiContext.getInstance().errorMessageContainer = "Could not send broadcast";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void broadcastSms(String staffId, BroadcastSmsRequest request,
                              Consumer<String> onSuccess,
                              Runnable onFailure, Runnable onNetworkIssue) {

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_1 + "/broadcasts-message";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("message", request.getSmsText());

                JsonArray jsonArray = new JsonArray();
                if (request.getAudience() != null) request.getAudience().forEach(jsonArray::add);
                requestBody.add("audience", jsonArray);

                HttpRequest http = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId",      staffId,
                                "Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = core.httpClient.send(http, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return "Sms successfully sent";
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String result = task.getValue();
            if (result != null) {
                onSuccess.accept(result);
                return;
            }
            onFailure.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            if (ex != null) ex.printStackTrace();

            String msg = ex == null ? null : ex.getMessage();
            if (ex instanceof java.net.ConnectException
                    || (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }
            ApiContext.getInstance().errorMessageContainer = "Could not send broadcast";
            onFailure.run();
        });

        new Thread(task).start();
    }
}
