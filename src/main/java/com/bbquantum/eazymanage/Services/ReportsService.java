package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.DTO.Academics.MasterScoreSheet;
import com.bbquantum.eazymanage.DTO.Academics.SbaRecord;
import com.bbquantum.eazymanage.Utitity.ApiUtilityClass;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ReportsService {

    private final ApiCore core;
    private final ApiUtilityClass facade;

    public ReportsService(ApiCore core, ApiUtilityClass facade) {
        this.core   = core;
        this.facade = facade;
    }

    public void loadSbaRecords(String levelId, String subjectId, String semesterId, String staffId,
                               Consumer<List<SbaRecord>> onResult,
                               Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Type listType = new TypeToken<List<SbaRecord>>() {}.getType();

        Task<List<SbaRecord>> task = new Task<>() {
            @Override
            protected List<SbaRecord> call() throws Exception {
                String url = core.STAFF_URL + core.API_VERSION_1
                        + "/sba/" + levelId + "/" + subjectId + "/" + semesterId;

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
                facade.errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<SbaRecord> result = task.getValue();
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
            facade.errorMessageContainer = "Could not load SBA records";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void downloadSbaReport(String staffId, String levelId, String subjectId, String semesterId,
                                  String suggestedFileName,
                                  Supplier<Window> parentWindow,
                                  Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<byte[]> task = new Task<>() {
            @Override
            protected byte[] call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_2 + "/generate-sba-report?"
                        + "staffId=" + staffId
                        + "&levelId=" + levelId
                        + "&subjectId=" + subjectId
                        + "&semesterId=" + semesterId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + core.token)
                        .GET()
                        .build();

                HttpResponse<byte[]> response = core.httpClient
                        .send(request, HttpResponse.BodyHandlers.ofByteArray());

                if (response.statusCode() == 200) {
                    return response.body();
                }
                facade.errorMessageContainer = new String(response.body(), StandardCharsets.UTF_8);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            byte[] pdfBytes = task.getValue();
            if (pdfBytes == null) {
                onFailure.run();
                return;
            }

            Platform.runLater(() -> {
                try {
                    FileChooser chooser = new FileChooser();
                    chooser.setTitle("Save SBA Report");
                    chooser.getExtensionFilters().add(
                            new FileChooser.ExtensionFilter("PDF", "*.pdf")
                    );
                    chooser.setInitialFileName(
                            (suggestedFileName == null || suggestedFileName.isBlank())
                                    ? "SBA Report.pdf"
                                    : suggestedFileName);

                    Window window = parentWindow.get();
                    File file = chooser.showSaveDialog(window);

                    if (file != null) {
                        Files.write(file.toPath(), pdfBytes);
                        onSuccess.run();
                    } else {
                        onFailure.run();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    facade.errorMessageContainer = e.getMessage();
                    onFailure.run();
                }
            });
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

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void loadMasterSheet(String levelId, String semesterId, String staffId,
                                Consumer<MasterScoreSheet> onResult,
                                Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Task<MasterScoreSheet> task = new Task<>() {
            @Override
            protected MasterScoreSheet call() throws Exception {
                String url = core.STAFF_URL + core.API_VERSION_1
                        + "/master-sheet/" + levelId + "/" + semesterId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), MasterScoreSheet.class);
                }
                facade.errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            MasterScoreSheet result = task.getValue();
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
            facade.errorMessageContainer = "Could not load master score sheet";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void downloadMasterSheet(String staffId, String levelId, String semesterId,
                                    String suggestedFileName,
                                    Supplier<Window> parentWindow,
                                    Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<byte[]> task = new Task<>() {
            @Override
            protected byte[] call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_2 + "/generate-master-sheet?"
                        + "staffId=" + staffId
                        + "&levelId=" + levelId
                        + "&semesterId=" + semesterId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + core.token)
                        .GET()
                        .build();

                HttpResponse<byte[]> response = core.httpClient
                        .send(request, HttpResponse.BodyHandlers.ofByteArray());

                if (response.statusCode() == 200) {
                    return response.body();
                }
                facade.errorMessageContainer = new String(response.body(), StandardCharsets.UTF_8);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            byte[] pdfBytes = task.getValue();
            if (pdfBytes == null) {
                onFailure.run();
                return;
            }

            Platform.runLater(() -> {
                try {
                    FileChooser chooser = new FileChooser();
                    chooser.setTitle("Save Master Score Sheet");
                    chooser.getExtensionFilters().add(
                            new FileChooser.ExtensionFilter("PDF", "*.pdf")
                    );
                    chooser.setInitialFileName(
                            (suggestedFileName == null || suggestedFileName.isBlank())
                                    ? "Master Score Sheet.pdf"
                                    : suggestedFileName);

                    Window window = parentWindow.get();
                    File file = chooser.showSaveDialog(window);

                    if (file != null) {
                        Files.write(file.toPath(), pdfBytes);
                        onSuccess.run();
                    } else {
                        onFailure.run();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    facade.errorMessageContainer = e.getMessage();
                    onFailure.run();
                }
            });
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

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}
