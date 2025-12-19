package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.DTO.LoginResponse;
import com.bbquantum.eazymanage.Utitity.SSLHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.concurrent.Task;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class LoginController {

    // Backend configuration
    private static final String BASE_URL = "https://schoolmanagementsystem-production-afae.up.railway.app/api/v1/auth";
    private static final String LOGIN_ENDPOINT = "/staff-login";

    private final HttpClient httpClient = SSLHelper.createTrustAllHttpClient();
    private final Gson gson = new Gson();


    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    /**
     * Handles the login button click
     */
    @FXML
    private void handleLogin() {
        // Get the values from the form
        String staffId = usernameField.getText().trim();
        String password = passwordField.getText();

        // Hide any previous error
        errorLabel.setVisible(false);

        // Validate input
        if (staffId.isEmpty()) {
            showError("Please enter your Staff ID");
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter your password");
            return;
        }

        // Disable button to prevent multiple submissions
        loginButton.setDisable(true);
        loginButton.setText("Logging in...");

        // Authenticate with backend
        authenticateUser(staffId, password);
    }

    /**
     * Authenticate user with backend
     */
    private void authenticateUser(String staffId, String password) {
        Task<LoginResponse> task = new Task<>() {
            @Override
            protected LoginResponse call() throws Exception {
                // Create request body
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("staffId", staffId);
                requestBody.addProperty("password", password);

                // Build HTTP request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + LOGIN_ENDPOINT))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                        .build();

                // Send request
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // Parse response
                if (response.statusCode() == 200) {
                    JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);

                    String status = jsonResponse.get("status").getAsString();

                    if (status.equals("success")) {
                        String staffRole = jsonResponse.get("role").getAsString();
                        String institutionName = jsonResponse.get("institutionName").getAsString();
                        String staffFullName = jsonResponse.get("staffFullName").getAsString();
                        String returnStaffId = jsonResponse.get("returnStaffId").getAsString();
                        return new LoginResponse(status, returnStaffId, staffRole, institutionName, staffFullName);
                    } else if (status.equals("failed")) {
                        String message = jsonResponse.get("message").getAsString();
                        return new LoginResponse(status, message, null, null, null);
                    }

                } else {
                    return new LoginResponse("failed", "Server Error", null, null, null);
                }
                return null;
            }
        };

        // Handle success
        task.setOnSucceeded(event -> {
            LoginResponse response = task.getValue();
            loginButton.setDisable(false);
            loginButton.setText("Login");

            if (response.getSuccess().equals("success") && response.getStaffRole().equals("Teaching Staff")) {
                // Login successful - open appropriate dashboard based on role
                openDashboardByRole(response.getStaffRole(), response.getStaffId(), response.getInstitutionName(),
                        response.getStaffFullName());
            } else if (response.getSuccess().equals("success")) {
                openDashboardByRole(response.getStaffRole(), response.getStaffId(), response.getInstitutionName(),
                        response.getStaffFullName());
            } else if (response.getSuccess().equals("failed")) {
                showError(response.getStaffId());
            }
        });

        // Handle failure
        task.setOnFailed(event -> {
            loginButton.setDisable(false);
            loginButton.setText("Login");

            Throwable exception = task.getException();
            String errorMessage = exception.getMessage();

            // Check exception type first
            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                // Fallback if message is null
                showError("Connection error. Please check if the backend is running.");
            }
            exception.printStackTrace();
        });

        // Run task in background
        new Thread(task).start();
    }

    /**
     * Opens the appropriate dashboard based on user role
     */
    private void openDashboardByRole(String role, String staffId, String institutionName, String staffFullName) {
        try {
            String fxmlFile;
            String title;
            int width = 1200;
            int height = 800;

            // Determine which dashboard to open based on role
            switch (role) {
                case "Principal":
                    fxmlFile = "/com/bbquantum/eazymanage/Principal/principal-main-dashboard.fxml";
                    title = "EAZY MANAGE - Principal Dashboard";
                    break;

                case "Accountant":
                    fxmlFile = "/com/bbquantum/eazymanage/Accountant/accountant-dashboard.fxml";
                    title = "EAZY MANAGE - Accountant Dashboard";
                    break;

                case "Teaching Staff":
                    fxmlFile = "/com/bbquantum/eazymanage/Staff/staff-dashboard.fxml";
                    title = "EAZY MANAGE - Staff Dashboard";
                    break;

                default:
                    showError("Unknown role: " + role);
                    return;
            }


            // Load the appropriate dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), width, height);

            if (role.equals("Principal")) {
                PrincipalController principalController = loader.getController();
                principalController.setInstitutionName(institutionName);
                principalController.setStaffId(staffId);
                principalController.setPrincipalName(staffFullName);

                principalController.initializeAfterLogin();
            }

            if (role.equals("Accountant")) {
                FinanceController financeController = loader.getController();
                financeController.setInstitutionName(institutionName);
                financeController.setStaffId(staffId);
                financeController.setStaffName(staffFullName);

                financeController.initializeAfterLogin();
            }

            if (role.equals("Teaching Staff")) {
                StaffController staffController = loader.getController();
                staffController.setInstitutionName(institutionName);
                staffController.setStaffId(staffId);
                staffController.setStaffFullName(staffFullName);

                staffController.initializeAfterLogin();
            }

            // Close the login window
            Stage loginStage = (Stage) loginButton.getScene().getWindow();

            // Create new stage for dashboard
            Stage dashboardStage = new Stage();
            dashboardStage.setTitle(title);
            dashboardStage.setScene(scene);
            dashboardStage.setMaximized(true);

            // Close login and show dashboard
            loginStage.close();
            dashboardStage.show();

        } catch (Exception e) {
            showError("Error loading dashboard: ");
            e.printStackTrace();
        }
    }

    /**
     * Handles new staff enrollment
     */
    @FXML
    private void handleEnrollNewStaff() {
        try {
            // Load staff enrollment view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/Authentication/enroll-new-staff.fxml"));
            Scene scene = new Scene(loader.load(), 600, 700);

            // Get current stage
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setTitle("Enroll New Staff");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            showError("Error loading enrollment form: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Handles new school subscription
     */
    @FXML
    private void handleNewSchoolSubscription() {
        try {
            // Load subscription view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/Authentication/institution-registration-view.fxml"));
            Scene scene = new Scene(loader.load(), 500, 700);

            // Get current stage and set new scene
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Subscribe Your School - EAZY MANAGE");

        } catch (Exception e) {
            showError("Error loading subscription page: ");
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Helper method to show error messages
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}