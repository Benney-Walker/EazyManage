package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.DTO.Auth.EnrollmentResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.concurrent.Task;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class AuthService {

    private final ApiCore apiCore;

    public AuthService(ApiCore apiCore) {
        this.apiCore = apiCore;
    }


    public void resetPassword(String staffId, String newPasswordStaffId, String newPassword,
                                Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = apiCore.ADMIN_URL + apiCore.API_VERSION_1 + "/reset-staff-password?" +
                        "staffId=" + staffId +
                        "&newPasswordStaffId=" + newPasswordStaffId +
                        "&newPassword=" + newPassword;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + apiCore.token)
                        .POST(HttpRequest.BodyPublishers.noBody())
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
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void enrollNewStaff(String firstName, String lastName, String gender,
                               LocalDate dateOfBirth, String email, String phoneNumber,
                               String institutionId, String password, List<String> roles,
                               Consumer<EnrollmentResponse> onResult,
                               Runnable onNetworkIssue) {
        Task<EnrollmentResponse> task = new Task<>() {
            @Override
            protected EnrollmentResponse call() throws Exception {
                String url = apiCore.BASE_URL + "/auth/v1/enroll-new-staff";

                JsonObject body = new JsonObject();
                body.addProperty("firstName", firstName);
                body.addProperty("lastName", lastName);
                body.addProperty("gender", gender);
                body.addProperty("dateOfBirth", dateOfBirth == null ? "" : dateOfBirth.toString());
                body.addProperty("email", email);
                body.addProperty("phoneNumber", phoneNumber);
                body.addProperty("institutionId", institutionId);
                body.addProperty("password", password);

                JsonArray rolesArr = new JsonArray();
                if (roles != null) roles.forEach(rolesArr::add);
                body.add("roles", rolesArr);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .build();

                HttpResponse<String> response =
                        apiCore.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    // Success body is the assigned staff id.
                    return new EnrollmentResponse(true, response.body());
                }
                if (response.statusCode() == 500) {
                    // Server-signalled error — body is JSON with a message field.
                    try {
                        JsonObject err = JsonParser.parseString(response.body()).getAsJsonObject();
                        return new EnrollmentResponse(false, err.get("message").getAsString());
                    } catch (Exception ignored) {
                        return new EnrollmentResponse(false, response.body());
                    }
                }
                return new EnrollmentResponse(false, response.body());
            }
        };

        task.setOnSucceeded(event -> onResult.accept(task.getValue()));
        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            String msg = ex.getMessage();
            if (ex instanceof java.net.ConnectException
                    || (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }
            onResult.accept(new EnrollmentResponse(false,
                    msg == null ? "Unknown error" : msg));
        });

        new Thread(task).start();
    }
}
