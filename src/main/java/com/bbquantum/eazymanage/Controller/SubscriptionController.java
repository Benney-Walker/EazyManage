package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.DTO.SubscriptionResponse;
import com.bbquantum.eazymanage.Controller.EnrollStaffController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class SubscriptionController {

    // Backend configuration
    private static final String BASE_URL = "https://schoolmanagementsystem-production-afae.up.railway.app/api/v1/auth";
    private static final String SUBSCRIBE_ENDPOINT = "/school-subscription";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @FXML
    private TextField schoolNameField;

    @FXML
    private TextField subscriptionCodeField;

    @FXML
    private Label messageLabel;

    @FXML
    private Button subscribeButton;

    @FXML
    private VBox successPanel;

    @FXML
    private Label institutionIdLabel;

    @FXML
    private Hyperlink enrollPrincipalLink;

    @FXML
    private Hyperlink backToLogin;

    /**
     * Handles the subscription process
     */
    @FXML
    private void handleSubscribe() {
        // Get input values
        String schoolName = schoolNameField.getText().trim();
        String subscriptionCode = subscriptionCodeField.getText().trim();

        // Hide previous messages
        messageLabel.setVisible(false);
        successPanel.setVisible(false);

        // Validate inputs
        if (schoolName.isEmpty()) {
            showError("Please enter your school/institution name");
            return;
        }

        if (subscriptionCode.isEmpty()) {
            showError("Please enter the subscription code");
            return;
        }

        // Disable button to prevent multiple submissions
        subscribeButton.setDisable(true);
        subscribeButton.setText("Processing...");

        // Call backend asynchronously
        subscribeToBackend(schoolName, subscriptionCode);
    }

    /**
     * Subscribe to backend via REST API
     */
    private void subscribeToBackend(String schoolName, String subscriptionCode) {
        Task<SubscriptionResponse> task = new Task<>() {
            @Override
            protected SubscriptionResponse call() throws Exception {
                // Create request body to match your DTO
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("institutionName", schoolName);
                requestBody.addProperty("subscriptionCode", subscriptionCode);

                // Build HTTP request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + SUBSCRIBE_ENDPOINT))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                        .build();

                // Send request
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // Parse response based on your backend structure
                if (response.statusCode() == 200) {
                    JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);

                    String status = jsonResponse.get("status").getAsString();
                    String message = jsonResponse.get("message").getAsString();

                    if (status.equals("success")) {
                        // Success - message contains the institutionId
                        return new SubscriptionResponse(true, message, "Subscription successful");
                    } else {
                        // Failed - message contains the error
                        return new SubscriptionResponse(false, null, message);
                    }
                } else {
                    // HTTP error
                    return new SubscriptionResponse(false, null, "Server error: " + response.statusCode());
                }
            }
        };

        // Handle success
        task.setOnSucceeded(event -> {
            SubscriptionResponse response = task.getValue();
            subscribeButton.setDisable(false);
            subscribeButton.setText("Activate Subscription");

            if (response.isSuccess()) {
                showSuccess(response.getInstitutionId());
            } else {
                showError(response.getMessage());
            }
        });

        // Handle failure
        task.setOnFailed(event -> {
            subscribeButton.setDisable(false);
            subscribeButton.setText("Activate Subscription");

            Throwable exception = task.getException();

            // Check if it's a connection error
            if (exception.getMessage().contains("Connection refused") ||
                    exception.getMessage().contains("ConnectException")) {
                showError("Cannot connect to server. Please make sure the backend is running.");
            } else {
                showError("Error: " + exception.getMessage());
            }

            exception.printStackTrace();
        });

        // Run task in background thread
        new Thread(task).start();
    }

    /**
     * Shows success message with institution ID
     */
    private void showSuccess(String institutionId) {
        // Hide form elements
        schoolNameField.setDisable(true);
        subscriptionCodeField.setDisable(true);
        subscribeButton.setDisable(true);

        // Show success panel
        institutionIdLabel.setText(institutionId);
        successPanel.setVisible(true);

        // Show success message
        messageLabel.setText("✅ Subscription activated successfully!");
        messageLabel.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
        messageLabel.setVisible(true);

        //Disable back to login hyperlink
        backToLogin.setVisible(false);
    }

    /**
     * Shows error message
     */
    private void showError(String message) {
        messageLabel.setText("❌ " + message);
        messageLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
        messageLabel.setVisible(true);
    }

    /**
     * Handles enrolling the principal
     */
    @FXML
    private void handleEnrollPrincipal() {
        try {
            String institutionId = institutionIdLabel.getText();

            // Load principal enrollment view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/Authentication/enroll-new-staff.fxml"));
            Scene scene = new Scene(loader.load(), 600, 680);

            // Pass institution ID to the enrollment controller
            EnrollStaffController controller = loader.getController();
            controller.setInstitutionId(institutionId);
            controller.setRoleToPrincipal(); // Lock role to Principal

            // Create new stage
            Stage stage = new Stage();
            stage.setTitle("Enroll Principal - " + institutionId);
            stage.setScene(scene);

            // Close subscription window
            Stage currentStage = (Stage) enrollPrincipalLink.getScene().getWindow();
            currentStage.close();

            stage.show();

        } catch (IOException e) {
            showError("Error loading principal enrollment form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles going back to login
     */
    @FXML
    private void handleBackToLogin() {
        try {
            // Load login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/Authentication/login-view.fxml"));
            Scene scene = new Scene(loader.load(), 400, 680);

            // Get current stage and set new scene
            Stage stage = (Stage) schoolNameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("EAZY MANAGE - Login");

        } catch (IOException e) {
            showError("Error loading login page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}