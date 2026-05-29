package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.DTO.Messaging.BroadcastSendRequest;
import com.bbquantum.eazymanage.DTO.Messaging.BroadcastSendResponse;
import javafx.concurrent.Task;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public class MessagingService {

    private final ApiCore core;

    private final String MESSAGING_URL;

    public MessagingService(ApiCore core) {
        this.core = core;
        this.MESSAGING_URL = core.BASE_URL + "/messaging";
    }

    public void sendBroadcast(String staffId,
                              BroadcastSendRequest request,
                              Consumer<BroadcastSendResponse> onSuccess,
                              Runnable onFailure,
                              Runnable onNetworkIssue) {

        Task<BroadcastSendResponse> task = new Task<>() {
            @Override
            protected BroadcastSendResponse call() throws Exception {
                String url = MESSAGING_URL + core.API_VERSION_1 + "/broadcasts/send";
                String body = core.gson.toJson(request);

                HttpRequest http = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId",      staffId,
                                "Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();

                HttpResponse<String> response = core.httpClient.send(http, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), BroadcastSendResponse.class);
                }

                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            BroadcastSendResponse result = task.getValue();
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
