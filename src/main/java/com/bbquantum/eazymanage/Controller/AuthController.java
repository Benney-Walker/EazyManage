package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.Constants.StaffRoles;
import com.bbquantum.eazymanage.DTO.Institution.LoginResponse;
import com.bbquantum.eazymanage.DTO.Institution.SubscriptionResponse;
import com.bbquantum.eazymanage.Utitity.SSLHelper;
import com.bbquantum.eazymanage.Utitity.SessionManager;
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
import java.util.List;
import java.util.stream.Collectors;

public class AuthController {
    /* ================= CONTROLLER OWN VARIABLES ================= */
    private String errorMessageContainer;

    /* ================= BACKEND CONFIG ================= */

    private static final String BASE_URL = "https://eazymanagesoftware.up.railway.app/api/auth";
    private static final String LOGIN_ENDPOINT = "/v1/staff-login";

    private static final String SUBSCRIBE_ENDPOINT = "/v1/school-subscription";

    private final HttpClient httpClient = SSLHelper.createTrustAllHttpClient();
    private final Gson gson = new Gson();

    /* ================= LOGIN FIELDS ================= */

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private CheckBox showPasswordTick;
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

    @FXML
    private void initialize() {
        // Enter on username → move focus to password
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                // Focus whichever password field is currently visible
                if (passwordField.isVisible()) {
                    passwordField.requestFocus();
                } else {
                    passwordTextField.requestFocus();
                }
            }
        });

        // Enter on passwordField → trigger login
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                handleLogin();
            }
        });

        // Enter on passwordTextField (show password mode) → trigger login
        passwordTextField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                handleLogin();
            }
        });
    }

    /* ================= FORM SWITCHING ================= */

    @FXML
    private void switchToSubscription() {
        animateSwap(loginForm, subscriptionForm);
    }

    @FXML
    private void switchToLogin() {
        animateSwap(subscriptionForm, loginForm);
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
        loginButton.setText("Authenticating...");

        authenticateUser(staffId, password);
    }

    private void authenticateUser(String staffId, String password) {
        Task<LoginResponse> task = new Task<>() {
            @Override
            protected LoginResponse call() throws Exception {
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("staffId", staffId);
                requestBody.addProperty("password", password);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + LOGIN_ENDPOINT))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response =
                        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), LoginResponse.class);
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            loginButton.setDisable(false);
            loginButton.setText("SIGN IN");

            LoginResponse response = task.getValue();
            if (response == null) {
                showError(errorMessageContainer);
                return;
            }

            String rolesHeader = response.getRoles().stream()
                            .collect(Collectors.joining(","));

            SessionManager.clearSession();

            SessionManager.saveSession(
                    response.getAuthToken(),
                    response.getStaffId(),
                    response.getStaffName(),
                    response.getInstitutionName(),
                    rolesHeader);

                openDashboardByRole(
                        response.getRoles(),
                        response.getStaffId(),
                        response.getInstitutionName(),
                        response.getStaffName()
                );
        });

        task.setOnFailed(e -> {
            loginButton.setDisable(false);
            loginButton.setText("SIGN IN");
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
                        .uri(URI.create(BASE_URL + SUBSCRIBE_ENDPOINT))
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

    public void openDashboardByRole(List<String> roles, String staffId, String institutionName, String staffFullName) {
        String role = roles.getFirst();

        try {
            String fxmlFile;
            int width = 1200;
            int height = 800;

            // Determine which dashboard to open based on role
            switch (role) {
                case "PRINCIPAL":
                    fxmlFile = "/com/bbquantum/eazymanage/Principal/principal-main-dashboard.fxml";
                    break;

                case "ACCOUNTANT":
                    fxmlFile = "/com/bbquantum/eazymanage/Accountant/accountant-dashboard.fxml";
                    break;

                case "TEACHING_STAFF":
                    fxmlFile = "/com/bbquantum/eazymanage/Staff/staff-dashboard.fxml";
                    break;

                case "ADMINISTRATOR":
                    fxmlFile = "/com/bbquantum/eazymanage/Admin/Admin-dashboard.fxml";
                    break;

                default:
                    showError("Staff not allowed on system " + role);
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
            dashboardStage.setTitle("Eazy Manage");
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

    public static void findController(String role, String staffId, String institutionName, String staffFullName, FXMLLoader loader) {
        if (role.equals("PRINCIPAL")) {
            PrincipalController principalController = loader.getController();
            principalController.setInstitutionName(institutionName);
            principalController.setStaffId(staffId);
            principalController.setPrincipalName(staffFullName);
            principalController.setStaffRole(role);

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

        if (role.equals("ADMINISTRATOR")) {
            AdminController adminController = loader.getController();
            adminController.setInstitutionName(institutionName);
            adminController.setStaffId(staffId);
            adminController.setStaffFullName(staffFullName);

            adminController.initializeAfterLogin();
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

    @FXML
    private void togglePassword() {
        if (showPasswordTick.isSelected()) {
            passwordTextField.setText(passwordField.getText());
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);

            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);

            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
        }
    }

}
