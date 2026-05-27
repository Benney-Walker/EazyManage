package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.DTO.Institution.LogsDTO;
import com.google.gson.reflect.TypeToken;
import javafx.concurrent.Task;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;

public class LoggingService {

    private final ApiCore core;

    public LoggingService(ApiCore core) {
        this.core = core;
    }

    public void fetchAllLogs(String staffId,
                             Consumer<List<LogsDTO>> onResult,
                             Runnable onFailure,
                             Runnable onNetworkIssue) {
        Task<List<LogsDTO>> task = new Task<>() {
            @Override
            protected List<LogsDTO> call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_1 + "/get-all-logs";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response =
                        core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    Type listType = new TypeToken<List<LogsDTO>>(){}.getType();
                    return core.gson.fromJson(response.body(), listType);
                }

                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<LogsDTO> result = task.getValue();
            if (result == null) {
                onFailure.run();
                return;
            }
            onResult.accept(result);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();
            if (ex instanceof java.net.ConnectException
                    || (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }
            onFailure.run();
        });

        new Thread(task).start();
    }
}
