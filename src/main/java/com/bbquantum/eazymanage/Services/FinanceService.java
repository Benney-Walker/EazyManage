package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.DTO.Finance.*;
import com.bbquantum.eazymanage.DTO.Offline.FeesPaymentPayload;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.concurrent.Task;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;

public class FinanceService {

    private final ApiCore core;

    public FinanceService(ApiCore core) {
        this.core   = core;
    }

    /** Search a student's payment history for one fees/semester combination. */
    public void fetchStudentPaymentRecords(String staffId, String studentId, String semesterId, String gradeId,
                                           Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<StudentFeesPaymentDisplay> task = new Task<>() {
            @Override
            protected StudentFeesPaymentDisplay call() throws Exception {
                String url = core.FINANCE_URL + core.API_VERSION_1 + "/search-fees-report" +
                        "?studentId=" + studentId +
                        "&semesterId=" + semesterId +
                        "&gradeId=" + gradeId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    System.out.println("Response: " + response.body());
                    return core.gson.fromJson(response.body(), StudentFeesPaymentDisplay.class);
                }

                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            UtilityClass.paymentRecords = task.getValue();
            if (UtilityClass.paymentRecords != null) {
                ApiContext.getInstance().successMessageContainer = "Successfully loaded report";
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
            ApiContext.getInstance().errorMessageContainer = "Could not fetch report";
            onFailure.run();
        });

        new Thread(task).start();
    }

    /**
     * Filtered fees-summary load for the principal report screen.
     * Result is delivered via {@code onResult}; the facade passes the
     * {@code principalController.feesSummaryTable} setter.
     */
    public void getFeesFilterData(String ownUrl,
                                  Consumer<List<FeesSummaryTable>> onResult,
                                  Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Type listType = new TypeToken<List<FeesSummaryTable>>() {}.getType();

        Task<List<FeesSummaryTable>> task = new Task<>() {
            @Override
            protected List<FeesSummaryTable> call() throws Exception {
                String url = core.FINANCE_URL + core.API_VERSION_1 + ownUrl;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + core.token)
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
            List<FeesSummaryTable> result = task.getValue();
            onResult.accept(result);
            if (result != null) {
                ApiContext.getInstance().successMessageContainer = "Successfully loaded report";
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
            ApiContext.getInstance().errorMessageContainer = "Could not load report";
            onFailure.run();
        });

        new Thread(task).start();
    }

    /**
     * Load fees details for one (semester, level) pair.
     * Result delivered via {@code onResult}.
     */
    public void fetchFeesDetails(String staffId, String semesterId, String levelId,
                                 Consumer<FetchFeesDetails> onResult,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<FetchFeesDetails> task = new Task<>() {
            @Override
            protected FetchFeesDetails call() throws Exception {
                String url = core.FINANCE_URL + core.API_VERSION_1 + "/fetch-fees-details/" +
                        semesterId + "/" + levelId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), FetchFeesDetails.class);
                }

                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            FetchFeesDetails result = task.getValue();
            onResult.accept(result);
            if (result == null) {
                onFailure.run();
                return;
            }
            ApiContext.getInstance().successMessageContainer = "Fees Details fetched successfully";
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
            ApiContext.getInstance().errorMessageContainer = "Could not fetch fees details";
            onFailure.run();
        });

        new Thread(task).start();
    }

    /**
     * Load all payments for one (student, level, semester) triple.
     * Result delivered via {@code onResult}.
     */
    public void fetchPaymentRecord(String staffId, String studentId, String levelId, String semesterId,
                                   Consumer<List<StudentPaymentRecords>> onResult,
                                   Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Type listType = new TypeToken<List<StudentPaymentRecords>>() {}.getType();

        Task<List<StudentPaymentRecords>> task = new Task<>() {
            @Override
            protected List<StudentPaymentRecords> call() throws Exception {
                String url = core.FINANCE_URL + core.API_VERSION_1 + "/fetch-payment-records?"
                        + "studentId=" + studentId
                        + "&levelId=" + levelId
                        + "&semesterId=" + semesterId;

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
            List<StudentPaymentRecords> result = task.getValue();
            onResult.accept(result);
            if (result == null) {
                onFailure.run();
                return;
            }
            ApiContext.getInstance().successMessageContainer = "Payment records fetched successfully";
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
            ApiContext.getInstance().errorMessageContainer = "Could not fetch payment records";
            onFailure.run();
        });

        new Thread(task).start();
    }

    /**
     * Per-grade fees aggregation report. Result delivered via {@code onResult}.
     */
    public void fetchGradeFeesRecords(String staffId, String levelId, String semesterId,
                                      Consumer<List<GradeFeesReport>> onResult,
                                      Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Type listType = new TypeToken<List<GradeFeesReport>>() {}.getType();

        Task<List<GradeFeesReport>> task = new Task<>() {
            @Override
            protected List<GradeFeesReport> call() throws Exception {
                String url = core.FINANCE_URL + core.API_VERSION_1 + "/fetch-grade-fees-report/"
                        + levelId + "/" + semesterId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffid", staffId)
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
            List<GradeFeesReport> result = task.getValue();
            onResult.accept(result);
            if (result == null) {
                onFailure.run();
                return;
            }
            ApiContext.getInstance().successMessageContainer = "Payment records Loaded Successfully";
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
            ApiContext.getInstance().errorMessageContainer = "Could not get Records";
            onFailure.run();
        });

        new Thread(task).start();
    }

    // ====================================================================
    // Writes
    // ====================================================================

    /** Create a new fees record for a (grade, semester) pair. */
    public void addNewFees(String staffId, String gradeId, String semesterId, String feesAmount,
                           Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.FINANCE_URL + core.API_VERSION_1 + "/add-new-fees" +
                        "?gradeId=" + gradeId +
                        "&semesterId=" + semesterId +
                        "&feesAmount=" + feesAmount;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .POST(HttpRequest.BodyPublishers.noBody())
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
            String result = task.getValue();
            if (result != null) {
                ApiContext.getInstance().successMessageContainer = result;
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
            ApiContext.getInstance().errorMessageContainer = "Could not save Fees";
            onFailure.run();
        });

        new Thread(task).start();
    }

    /** Record a new fees payment for a student. */
    public void addNewFeesPayment(String staffId, String studentId, String semesterId,
                                  String amountPaid, String payerName, String payerPhone,
                                  Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        // Phase 7: short-circuit to the offline queue when in offline mode.
        if (OfflineMode.getInstance().isEnabled()) {
            try {
                String payload = new com.google.gson.Gson().toJson(
                        new FeesPaymentPayload(
                                staffId, studentId, semesterId, amountPaid, payerName, payerPhone));
                OfflineQueue.getInstance().enqueue(OfflineQueue.OP_FEES_PAYMENT, payload);
                ApiContext.getInstance().successMessageContainer = "Saved offline — will sync when online";
                javafx.application.Platform.runLater(onSuccess);
            } catch (Exception e) {
                e.printStackTrace();
                ApiContext.getInstance().errorMessageContainer = "Could not queue payment: " + e.getMessage();
                javafx.application.Platform.runLater(onFailure);
            }
            return;
        }

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.FINANCE_URL + core.API_VERSION_2 + "/add-fees-payment";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("studentId", studentId);
                requestBody.addProperty("semesterId", semesterId);
                requestBody.addProperty("amountPaid", amountPaid);
                requestBody.addProperty("payerName", payerName);
                requestBody.addProperty("payerPhone", payerPhone);

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
            ApiContext.getInstance().successMessageContainer = status;
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
            ApiContext.getInstance().errorMessageContainer = "Could not add Payment";
            onFailure.run();
        });

        new Thread(task).start();
    }

    /** Update an existing semester-fees amount. */
    public void updateSemesterFees(String staffId, FetchFeesDetails update,
                                   Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.FINANCE_URL + core.API_VERSION_1 + "/update-semester-fees";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("amount", update.getAmount());
                requestBody.addProperty("semesterId", update.getSemesterId());
                requestBody.addProperty("levelId", update.getLevelId());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "Content-Type", "application/json",
                                "staffid", staffId)        // lower-case header preserved from legacy
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
            String response = task.getValue();
            if (response == null) {
                onFailure.run();
                return;
            }
            ApiContext.getInstance().successMessageContainer = response;
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
            ApiContext.getInstance().errorMessageContainer = "Could not update Fees details";
            onFailure.run();
        });

        new Thread(task).start();
    }

    /** Delete a fees payment record by transaction id. */
    public void deletePaymentRecord(String staffId, String transactionId,
                                    Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.FINANCE_URL + core.API_VERSION_1 + "/delete-payment-record/"
                        + transactionId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .DELETE()
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
            String response = task.getValue();
            if (response == null) {
                onFailure.run();
                return;
            }
            ApiContext.getInstance().successMessageContainer = response;
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
            ApiContext.getInstance().errorMessageContainer = "Could not delete record";
            onFailure.run();
        });

        new Thread(task).start();
    }

    /** Update an existing payment record. */
    public void updatePaymentDetails(String staffId, StudentPaymentRecords update,
                                     Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.FINANCE_URL + core.API_VERSION_1 + "/update-payment-details";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("paymentId", update.getPaymentId());
                requestBody.addProperty("dateOfPayment", update.getDateOfPayment());
                requestBody.addProperty("studentId", update.getStudentId());
                requestBody.addProperty("studentName", update.getStudentName());
                requestBody.addProperty("amount", update.getAmount());
                requestBody.addProperty("semesterId", update.getSemesterId());
                requestBody.addProperty("payerName", update.getPayerName());
                requestBody.addProperty("payerContact", update.getPayerContact());

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
            String response = task.getValue();
            if (response == null) {
                onFailure.run();
                return;
            }
            ApiContext.getInstance().successMessageContainer = response;
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
            ApiContext.getInstance().errorMessageContainer = "Could not update payment records";
            onFailure.run();
        });

        new Thread(task).start();
    }
}
