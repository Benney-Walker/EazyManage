package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.DTO.Institution.LoginResponse;
import com.bbquantum.eazymanage.DTO.Institution.SubscriptionResponse;
import com.bbquantum.eazymanage.Utitity.SSLHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Unified Authentication Controller
 * Handles:
 *  - Staff Login
 *  - Institution Subscription
 *  - Animated switching between forms
 */
public class AuthController {

    /* ================= BACKEND CONFIG ================= */

    private static final String LOGIN_BASE_URL = "http://localhost:8080/api/v1/auth";
    private static final String LOGIN_ENDPOINT = "/staff-login";

    private static final String SUBSCRIBE_ENDPOINT = "/school-subscription";

    private final HttpClient httpClient = SSLHelper.createTrustAllHttpClient();
    private final Gson gson = new Gson();

    /* ================= LOGIN FIELDS ================= */

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    /* ================= SUBSCRIPTION FIELDS ================= */

    @FXML private TextField schoolNameField;
    @FXML private TextField subscriptionCodeField;
    @FXML private Button subscribeButton;
    @FXML private Label messageLabel;
    @FXML private Label institutionIdLabel;
    @FXML private Hyperlink backToLoginLink;

    /* ================= FORMS ================= */

    @FXML private VBox loginForm;
    @FXML private VBox subscriptionForm;
    @FXML private VBox messageBox;
    @FXML private VBox info;
    @FXML private Hyperlink enrollPrincipal;

    /* ================= FORM SWITCHING ================= */

    @FXML
    private void switchToSubscription() {
        animateSwap(loginForm, subscriptionForm);
    }

    @FXML
    private void switchToLogin() {
        animateSwap(subscriptionForm, loginForm);
    }

    private void animateSwap(VBox hide, VBox show) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), hide);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(e -> {
            hide.setVisible(false);
            hide.setManaged(false);

            show.setOpacity(0);
            show.setVisible(true);
            show.setManaged(true);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), show);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });

        fadeOut.play();
    }

    /* ================= LOGIN LOGIC ================= */

    @FXML
    private void handleLogin() {
        String staffId = usernameField.getText().trim();
        String password = passwordField.getText();

        if (staffId.isEmpty()) {
            showError("Please enter your Staff ID");
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter your password");
            return;
        }

        loginButton.setDisable(true);
        loginButton.setText("Logging in...");

        authenticateUser(staffId, password);
    }

    @FXML
    private void handleEnrollPrincipal() {
        try {
            String institutionId = institutionIdLabel.getText();

            // Load principal enrollment view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/Authentication/enroll-staff.fxml"));
            Scene scene = new Scene(loader.load(), 600, 700);

            // Pass institution ID to the enrollment controller
            EnrollStaffController controller = loader.getController();
            controller.setInstitutionId(institutionId);
            controller.setRoleToPrincipal(); // Lock role to Principal

            // Create new stage
            Stage stage = new Stage();
            stage.getIcons().add(
                    new Image(getClass().getResourceAsStream(
                            "/com/bbquantum/eazymanage/Assets/EazyManage.png"
                    ))
            );
            stage.setTitle("Enroll Principal");
            stage.setScene(scene);

            // Close subscription window
            Stage currentStage = (Stage) enrollPrincipal.getScene().getWindow();
            currentStage.close();

            stage.show();

        } catch (IOException e) {
            showError("Error loading principal enrollment form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void authenticateUser(String staffId, String password) {
        Task<LoginResponse> task = new Task<>() {
            @Override
            protected LoginResponse call() throws Exception {
                JsonObject body = new JsonObject();
                body.addProperty("staffId", staffId);
                body.addProperty("password", password);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(LOGIN_BASE_URL + LOGIN_ENDPOINT))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                        .build();

                HttpResponse<String> response =
                        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JsonObject json = gson.fromJson(response.body(), JsonObject.class);
                    String status = json.get("status").getAsString();

                    if ("success".equals(status)) {
                        return new LoginResponse(
                                status,
                                json.get("returnStaffId").getAsString(),
                                json.get("role").getAsString(),
                                json.get("institutionName").getAsString(),
                                json.get("staffFullName").getAsString()
                        );
                    }
                    return new LoginResponse("failed",
                            json.get("message").getAsString(), null, null, null);
                }
                return new LoginResponse("failed", "Server error", null, null, null);
            }
        };

        task.setOnSucceeded(e -> {
            loginButton.setDisable(false);
            loginButton.setText("Sign In");

            LoginResponse response = task.getValue();
            if ("success".equals(response.getSuccess())) {
                openDashboardByRole(
                        response.getStaffRole(),
                        response.getStaffId(),
                        response.getInstitutionName(),
                        response.getStaffFullName()
                );
            } else {
                showError(response.getStaffId());
            }
        });

        task.setOnFailed(e -> {
            loginButton.setDisable(false);
            loginButton.setText("Sign In");
            showError("Connection error. Please try again.");
        });

        new Thread(task).start();
    }

    /* ================= SUBSCRIPTION LOGIC ================= */

    @FXML
    private void handleSubscribe() {
        String schoolName = schoolNameField.getText().trim();
        String subscriptionCode = subscriptionCodeField.getText().trim();

        messageLabel.setVisible(false);

        if (schoolName.isEmpty() || subscriptionCode.isEmpty()) {
            showError("All fields are required");
            return;
        }

        subscribeButton.setDisable(true);
        subscribeButton.setText("Processing...");

        subscribeToBackend(schoolName, subscriptionCode);
    }

    private void subscribeToBackend(String name, String code) {
        Task<SubscriptionResponse> task = new Task<>() {
            @Override
            protected SubscriptionResponse call() throws Exception {
                JsonObject body = new JsonObject();
                body.addProperty("institutionName", name);
                body.addProperty("subscriptionCode", code);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(LOGIN_BASE_URL + SUBSCRIBE_ENDPOINT))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                        .build();

                HttpResponse<String> response =
                        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                JsonObject json = gson.fromJson(response.body(), JsonObject.class);

                return "success".equals(json.get("status").getAsString())
                        ? new SubscriptionResponse(true, json.get("message").getAsString(), "OK")
                        : new SubscriptionResponse(false, null, json.get("message").getAsString());
            }
        };

        task.setOnSucceeded(e -> {
            subscribeButton.setDisable(false);
            subscribeButton.setText("Activate Institution");

            SubscriptionResponse response = task.getValue();

            if (response.isSuccess()) {
                showSuccess("Activation successful", response.getInstitutionId());
            } else {
                showError("Failed to activate Institution");
            }
        });

        task.setOnFailed(e -> {
            subscribeButton.setDisable(false);
            subscribeButton.setText("Activate Institution");
            showError("Subscription failed");
        });

        new Thread(task).start();
    }

    /* ================= ENROLL STAFF ================= */

    @FXML
    private void handleEnrollNewStaff() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/bbquantum/eazymanage/Authentication/enroll-staff.fxml"));
            Scene scene = new Scene(loader.load(), 600, 600);

            Stage stage = (Stage) loginForm.getScene().getWindow();
            stage.setScene(scene);
            stage.getIcons().add(
                    new Image(getClass().getResourceAsStream(
                            "/com/bbquantum/eazymanage/Assets/EazyManage.png"
                    ))
            );
            stage.setTitle("Enroll New Staff");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================= DASHBOARD ROUTING ================= */

    private void openDashboardByRole(String role, String staffId, String institutionName, String staffFullName) {
        try {
            String fxmlFile;
            String title;
            int width = 1200;
            int height = 800;

            // Determine which dashboard to open based on role
            switch (role) {
                case "PRINCIPAL":
                    fxmlFile = "/com/bbquantum/eazymanage/Principal/principal-main-dashboard.fxml";
                    title = "EAZY MANAGE - Principal Dashboard";
                    break;

                case "ACCOUNTANT":
                    fxmlFile = "/com/bbquantum/eazymanage/Accountant/accountant-dashboard.fxml";
                    title = "EAZY MANAGE - Accountant Dashboard";
                    break;

                case "TEACHING_STAFF":
                    fxmlFile = "/com/bbquantum/eazymanage/Staff/staff-dashboard.fxml";
                    title = "EAZY MANAGE - Staff Dashboard";
                    break;

                default:
                    showError("You are not allowed to access this system " + role);
                    return;
            }


            // Load the appropriate dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), width, height);

            findController(role, staffId, institutionName, staffFullName, loader);

            // Close the login window
            Stage loginStage = (Stage) loginButton.getScene().getWindow();

            // Create new stage for dashboard
            Stage dashboardStage = new Stage();
            dashboardStage.getIcons().add(
                    new Image(getClass().getResourceAsStream(
                            "/com/bbquantum/eazymanage/Assets/EazyManage.png"
                    ))
            );
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

    private static void findController(String role, String staffId, String institutionName, String staffFullName, FXMLLoader loader) {
        if (role.equals("PRINCIPAL")) {
            PrincipalController principalController = loader.getController();
            principalController.setInstitutionName(institutionName);
            principalController.setStaffId(staffId);
            principalController.setPrincipalName(staffFullName);

            principalController.initializeAfterLogin();
        }

        if (role.equals("ACCOUNTANT")) {
            FinanceController financeController = loader.getController();
            financeController.setInstitutionName(institutionName);
            financeController.setStaffId(staffId);
            financeController.setStaffName(staffFullName);

            financeController.initializeAfterLogin();
        }

        if (role.equals("TEACHING_STAFF")) {
            StaffController staffController = loader.getController();
            staffController.setInstitutionName(institutionName);
            staffController.setStaffId(staffId);
            staffController.setStaffFullName(staffFullName);

            staffController.initializeAfterLogin();
        }
    }

    private void showError(String msg) {

        // Set error text (palette: red / warning tone)
        errorLabel.setText(msg);

        // Hide other UI elements
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
        institutionIdLabel.setVisible(false);
        institutionIdLabel.setManaged(false);

        info.setVisible(false);
        info.setManaged(false);

        // Show error box
        messageBox.setVisible(true);
        messageBox.setManaged(true);

        // Auto-hide after 1 second (NON-BLOCKING)
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> {
            messageBox.setVisible(false);
            messageBox.setManaged(false);

            info.setVisible(true);
            info.setManaged(true);
        });

        delay.play();
    }

    private void showSuccess(String msg, String institutionId) {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        backToLoginLink.setVisible(false);
        backToLoginLink.setManaged(false);


        messageLabel.setText(msg);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);

        institutionIdLabel.setText(institutionId);
        institutionIdLabel.setVisible(true);
        institutionIdLabel.setManaged(true);

        enrollPrincipal.setVisible(true);
        enrollPrincipal.setManaged(true);

        info.setVisible(false);
        info.setManaged(false);

        messageBox.setVisible(true);
        messageBox.setManaged(true);

        subscribeButton.setDisable(true);

    }

}
