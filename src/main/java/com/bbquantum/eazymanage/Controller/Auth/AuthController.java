package com.bbquantum.eazymanage.Controller.Auth;

import com.bbquantum.eazymanage.Controller.EnrollStaffController;
import com.bbquantum.eazymanage.DTO.Auth.LoginResponse;
import com.bbquantum.eazymanage.DTO.Auth.SubscriptionResponse;
import com.bbquantum.eazymanage.Utitity.SSLHelper;
import com.bbquantum.eazymanage.Utitity.SessionManager;
import com.bbquantum.eazymanage.Utitity.ThemeManager;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

public class AuthController {
    /* ================= CONTROLLER OWN VARIABLES ================= */
    private String errorMessageContainer;

    /* ================= BACKEND CONFIG ================= */

    private static final String BASE_URL = UtilityClass.BASE_URL;

    private static final String LOGIN_ENDPOINT = "/auth/v1/staff-login";

    private static final String SUBSCRIBE_ENDPOINT = "/auth/v1/school-subscription";

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
            ThemeManager.getInstance().register(scene);

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

                if (response.statusCode() == 500) {
                    JsonObject jsonObject = JsonParser.parseString(response.body())
                                    .getAsJsonObject();

                    errorMessageContainer = jsonObject.get("message")
                                    .getAsString();
                    return null;
                }

                if (response.statusCode() == 401) {
                    JsonObject jsonObject = JsonParser.parseString(response.body())
                            .getAsJsonObject();

                    errorMessageContainer = jsonObject.get("message")
                            .getAsString();
                    return null;
                }

                if (response.statusCode() == 403) {
                    JsonObject jsonObject = JsonParser.parseString(response.body())
                            .getAsJsonObject();

                    errorMessageContainer = jsonObject.get("message")
                            .getAsString();
                    return null;
                }

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

            String rolesHeader = String.join(",", response.getRoles());

            SessionManager.clearSession();

            SessionManager.saveSession(
                    response.getAuthToken(),
                    response.getStaffId(),
                    response.getStaffName(),
                    response.getInstitutionName(),
                    rolesHeader);

            String stored = SessionManager.getActiveRole();
            String active = (stored != null && !stored.isBlank()
                    && response.getRoles().contains(stored))
                    ? stored
                    : response.getRoles().get(0);

            try {
                javafx.stage.Stage stage =
                        (javafx.stage.Stage) loginButton.getScene().getWindow();
                com.bbquantum.eazymanage.Shell.ShellLauncher.launch(
                        stage,
                        active,
                        response.getStaffId(),
                        response.getInstitutionName(),
                        response.getStaffName());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
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

                if (response.statusCode() == 200) {
                    return new SubscriptionResponse(true, response.body());
                } else if (response.statusCode() == 500) {
                    JsonObject res = JsonParser.parseString(response.body()).getAsJsonObject();
                    return new SubscriptionResponse(false, res.get("message").getAsString());
                }

                return new SubscriptionResponse(false, response.body());
            }
        };

        task.setOnSucceeded(e -> {
            subscribeButton.setDisable(false);
            subscribeButton.setText("Activate Institution");

            SubscriptionResponse response = task.getValue();

            if (response.isSuccess()) {
                showSuccess("Activation successful", response.getMessage());
            } else {
                showError(response.getMessage());
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

        showError("Contact administrator! Staffs can only be added by school's admin");
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
