package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.Utitity.ApiUtilityClass;
import javafx.concurrent.Task;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthService {

    private final ApiCore apiCore;

    private final ApiUtilityClass facade;

    public AuthService(ApiCore apiCore, ApiUtilityClass facade) {
        this.apiCore = apiCore;
        this.facade = facade;
    }

    /**
     * Reset another staff member's password.
     *
     * <p>The {@code staffId} header identifies the <i>actor</i> (typically an
     * Administrator), while {@code newPasswordStaffId} names the account being
     * reset. The two are deliberately distinct: an administrator resetting a
     * teacher's password is a common workflow and the backend logs both
     * identities for audit.
     */
    public void recoverPassword(String staffId, String newPasswordStaffId, String newPassword,
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

                facade.errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String status = task.getValue();
            if (status == null) {
                onFailure.run();
                return;
            }
            facade.successMessageContainer = status;
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
