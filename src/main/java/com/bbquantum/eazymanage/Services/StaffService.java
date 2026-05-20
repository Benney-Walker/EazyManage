package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.DTO.Institution.FindStaffResponse;
import com.bbquantum.eazymanage.Utitity.ApiUtilityClass;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.concurrent.Task;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class StaffService {

    private final ApiCore core;

    private final ApiUtilityClass facade;

    public StaffService(ApiCore core, ApiUtilityClass facade) {
        this.core = core;
        this.facade = facade;
    }

    /**
     * Look up a single staff member's record.
     * Writes the result to {@code facade.findStaffResponse} so the editing
     * screen, which reads it after {@code onSuccess}, finds it in place.
     */
    public void searchStaffData(String searchId, String staffId,
                                Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Task<FindStaffResponse> task = new Task<>() {
            @Override
            protected FindStaffResponse call() throws Exception {
                String url = core.STAFF_URL + core.API_VERSION_1 + "/find-staff-by-id/" + searchId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), FindStaffResponse.class);
                }

                facade.errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            facade.findStaffResponse = task.getValue();
            if (facade.findStaffResponse != null) {
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

    /**
     * Persist edits to a staff member's record.
     *
     * <p><b>Note on the success string.</b> The backend returns 200 with no body
     * payload that callers consume; the legacy code returns the literal string
     * {@code "Successfull"} (with the typo) which is then written into
     * {@code successMessageContainer}. Preserved verbatim because some
     * controllers may compare against that exact string.
     */
    public void updateStaffInfo(FindStaffResponse info, String staffId,
                                Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_1 + "/update-staff-info";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("staffId", info.getStaffId());
                requestBody.addProperty("firstName", info.getFirstName());
                requestBody.addProperty("surname", info.getSurname());
                requestBody.addProperty("gender", info.getGender());
                requestBody.addProperty("dateOfBirth", info.getDateOfBirth());
                requestBody.addProperty("email", info.getEmail());
                requestBody.addProperty("phoneNumber", info.getPhoneNumber());

                JsonArray rolesArray = new JsonArray();
                if (info.getStaffRoles() != null) {
                    info.getStaffRoles().forEach(rolesArray::add);
                }

                requestBody.add("staffRoles", rolesArray);
                requestBody.addProperty("staffStatus", info.getStaffStatus());
                requestBody.addProperty("dateOfRegistration", info.getDateOfRegistration());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "Content-Type", "application/json",
                                "staffId", staffId)
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return "Successfull";   // legacy spelling preserved
                }

                facade.errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String result = task.getValue();
            if (result == null) {
                onFailure.run();
                return;
            }
            facade.successMessageContainer = result;
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
            facade.errorMessageContainer = "Could update info";
            onFailure.run();
        });

        new Thread(task).start();
    }
}
