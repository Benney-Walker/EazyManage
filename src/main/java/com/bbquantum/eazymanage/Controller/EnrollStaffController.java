package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.DTO.EnrollmentResponse;
import com.bbquantum.eazymanage.Utitity.SSLHelper;
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
import java.time.LocalDate;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class EnrollStaffController {

    // Backend configuration
    private static final String BASE_URL = "https://schoolmanagementsystem-production-afae.up.railway.app/api/v1/auth";
    private static final String ENROLL_ENDPOINT = "/enroll-new-staff";

    private final HttpClient httpClient = SSLHelper.createTrustAllHttpClient();
    private final Gson gson = new Gson();

    // Personal Info
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private RadioButton maleRadio;
    @FXML private RadioButton femaleRadio;
    @FXML private ToggleGroup genderGroup;
    @FXML private DatePicker dateOfBirthPicker;

    // Contact Info
    @FXML private TextField emailField;
    @FXML private TextField phoneNumberField;

    // Account Info
    @FXML private TextField institutionIdField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    // Message and Success Panel
    @FXML private Label messageLabel;
    @FXML private Button enrollButton;
    @FXML private VBox successPanel;
    @FXML private Label staffIdLabel;
    @FXML private Label passwordDisplayLabel;
    @FXML private Hyperlink backToLoginLink;

    private String presetInstitutionId = null;

    /**
     * Initialize method
     */
    @FXML
    public void initialize() {
        // Populate role dropdown
        roleComboBox.getItems().addAll("Principal", "Teaching Staff", "Accountant", "General Staff");

        // Hide success panel initially
        successPanel.setVisible(false);
        successPanel.setManaged(false);
    }

    /**
     * Set institution ID from subscription (called from SubscriptionController)
     */
    public void setInstitutionId(String institutionId) {
        this.presetInstitutionId = institutionId;
        institutionIdField.setText(institutionId);
        institutionIdField.setDisable(true); // Lock it so user can't change
    }

    /**
     * Set role to Principal and lock it (called from SubscriptionController)
     */
    public void setRoleToPrincipal() {
        roleComboBox.setValue("Principal");
        roleComboBox.setDisable(true); // Lock it
    }

    /**
     * Handle staff enrollment
     */
    @FXML
    private void handleEnrollStaff() {
        // Hide previous messages
        messageLabel.setVisible(false);

        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // Get values
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String gender = maleRadio.isSelected() ? "Male" : "Female";
        LocalDate dateOfBirth = dateOfBirthPicker.getValue();
        String email = emailField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();
        String institutionId = institutionIdField.getText().trim();
        String role = roleComboBox.getValue();
        String password = passwordField.getText();

        // Disable button
        enrollButton.setDisable(true);
        enrollButton.setText("Enrolling...");

        // Call backend
        enrollToBackend(firstName, lastName, gender, dateOfBirth, email, phoneNumber, institutionId, role, password);
    }

    /**
     * Validate all inputs
     */
    private boolean validateInputs() {
        // First Name
        if (firstNameField.getText().trim().isEmpty()) {
            showError("Please enter first name");
            return false;
        }

        // Last Name
        if (lastNameField.getText().trim().isEmpty()) {
            showError("Please enter last name");
            return false;
        }

        // Gender
        if (!maleRadio.isSelected() && !femaleRadio.isSelected()) {
            showError("Please select gender");
            return false;
        }

        // Date of Birth
        if (dateOfBirthPicker.getValue() == null) {
            showError("Please select date of birth");
            return false;
        }

        // Email
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showError("Please enter email address");
            return false;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Please enter a valid email address");
            return false;
        }

        // Phone Number
        if (phoneNumberField.getText().trim().isEmpty()) {
            showError("Please enter phone number");
            return false;
        }

        // Institution ID
        if (institutionIdField.getText().trim().isEmpty()) {
            showError("Please enter institution ID");
            return false;
        }

        // Role
        if (roleComboBox.getValue() == null || roleComboBox.getValue().isEmpty()) {
            showError("Please select a role");
            return false;
        }

        // Password
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (password.isEmpty()) {
            showError("Please create a password");
            return false;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return false;
        }

        if (confirmPassword.isEmpty()) {
            showError("Please confirm your password");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return false;
        }

        return true;
    }

    /**
     * Enroll staff to backend
     */
    private void enrollToBackend(String firstName, String lastName, String gender, LocalDate dateOfBirth,
                                 String email, String phoneNumber, String institutionId, String role, String password) {
        Task<EnrollmentResponse> task = new Task<>() {
            @Override
            protected EnrollmentResponse call() throws Exception {
                // Create request body matching your DTO
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("firstName", firstName);
                requestBody.addProperty("lastName", lastName);
                requestBody.addProperty("gender", gender);

                // Convert LocalDate to LocalDateTime (start of day) and format as ISO string
                String dateTimeString = dateOfBirth.atStartOfDay().toString();
                requestBody.addProperty("dateOfBirth", dateTimeString);

                requestBody.addProperty("email", email);
                requestBody.addProperty("phoneNumber", phoneNumber);
                requestBody.addProperty("institutionId", institutionId);
                requestBody.addProperty("password", password);

                // Set status based on role (adjust if needed)
                requestBody.addProperty("status", role);

                // Build HTTP request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + ENROLL_ENDPOINT))
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
                        // Success - message contains the staffId
                        return new EnrollmentResponse(true, message, "Enrollment successful", password);
                    } else {
                        // Failed - message contains the error
                        return new EnrollmentResponse(false, null, message, null);
                    }
                } else {
                    return new EnrollmentResponse(false, null, "Server error: " + response.statusCode(), null);
                }
            }
        };

        // Handle success
        task.setOnSucceeded(event -> {
            EnrollmentResponse response = task.getValue();
            enrollButton.setDisable(false);
            enrollButton.setText("Enroll Staff");

            if (response.isSuccess()) {
                showSuccess(response.getStaffId(), response.getPassword());
            } else {
                System.out.println(response.getMessage());
                showError(response.getMessage());
            }
        });

        // Handle failure
        task.setOnFailed(event -> {
            enrollButton.setDisable(false);
            enrollButton.setText("Enroll Staff");

            Throwable exception = task.getException();

            if (exception.getMessage().contains("Connection refused") ||
                    exception.getMessage().contains("ConnectException")) {
                showError("Cannot connect to server. Please make sure the backend is running.");
            } else {
                showError("Error: " + exception.getMessage());
            }

            exception.printStackTrace();
        });

        // Run task in background
        new Thread(task).start();
    }

    /**
     * Show success panel with staff ID and password
     */
    private void showSuccess(String staffId, String password) {
        // Hide form
        firstNameField.getParent().setVisible(false);
        firstNameField.getParent().setManaged(false);

        // Show success panel
        staffIdLabel.setText(staffId);
        passwordDisplayLabel.setText(password);
        successPanel.setVisible(true);
        successPanel.setManaged(true);

        // Show success message
        messageLabel.setText("✅ Staff enrolled successfully!");
        messageLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        messageLabel.setVisible(true);
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        messageLabel.setText("❌ " + message);
        messageLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
        messageLabel.setVisible(true);
    }

    /**
     * Handle back to login
     */
    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/Authentication/login-view.fxml"));
            Scene scene = new Scene(loader.load(), 400, 680);

            Stage stage = (Stage) backToLoginLink.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("EAZY MANAGE - Login");

        } catch (IOException e) {
            showError("Error loading login page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}