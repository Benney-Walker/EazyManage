package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.Constants.StaffRoles;
import com.bbquantum.eazymanage.DTO.Institution.EnrollmentResponse;
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
    private static final String BASE_URL = "https://eazymanagesoftware.up.railway.app/api/v1/auth";
    private static final String ENROLL_ENDPOINT = "/enroll-new-staff";

    private final HttpClient httpClient = SSLHelper.createTrustAllHttpClient();
    private final Gson gson = new Gson();

    // Personal Info
    @FXML private VBox formPanel;
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
    @FXML private ComboBox<StaffRoles> roleComboBox;
    @FXML private PasswordField enrollmentPasswordField;
    @FXML private PasswordField confirmPasswordField;

    // Message and Success Panel
    @FXML private Label enrollmentMessageLabel;
    @FXML private Button enrollButton;
    @FXML private VBox successPanel;
    @FXML private Label staffIdLabel;
    @FXML private Hyperlink backToLoginLink;

    /**
     * Initialize method
     */
    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll(StaffRoles.values());

        successPanel.setVisible(false);
        successPanel.setManaged(false);
        enrollmentMessageLabel.setVisible(false);
    }

    /**
     * Set institution ID from subscription (called from SubscriptionController)
     */
    public void setInstitutionId(String institutionId) {
        institutionIdField.setText(institutionId);
        institutionIdField.setDisable(true); // Lock it so user can't change
    }

    /**
     * Set role to Principal and lock it (called from SubscriptionController)
     */
    public void setRoleToPrincipal() {
        roleComboBox.setValue(StaffRoles.PRINCIPAL);
        roleComboBox.setDisable(true); // Lock it
    }

    /**
     * Handle staff enrollment
     */
    @FXML
    private void handleEnrollStaff() {
        clearMessage();

        if (!validateInputs()) return;

        setLoadingState(true);

        enrollToBackend(
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                getSelectedGender(),
                dateOfBirthPicker.getValue(),
                emailField.getText().trim(),
                phoneNumberField.getText().trim(),
                institutionIdField.getText().trim(),
                String.valueOf(roleComboBox.getValue()),
                enrollmentPasswordField.getText()
        );
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
        if (roleComboBox.getValue() == null) {
            showError("Please select a role");
            return false;
        }

        // Password
        String password = enrollmentPasswordField.getText();
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

                requestBody.addProperty("dateOfBirth", String.valueOf(dateOfBirth));

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
                HttpResponse<String> response =
                        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // Parse response based on your backend structure
                if (response.statusCode() == 200) {
                    JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);

                    String status = jsonResponse.get("status").getAsString();
                    String message = jsonResponse.get("message").getAsString();

                    return new EnrollmentResponse(true, message, "Enrollment successful", password);

                } else {
                    return new EnrollmentResponse(false, null, response.body(), null);
                }
            }
        };

        // Handle success
        task.setOnSucceeded(event -> {
            setLoadingState(false);
            EnrollmentResponse response = task.getValue();

            if (!response.isSuccess()) {
                showError(response.getMessage());
                return;
            }

            showSuccess(response.getStaffId());
        });

        // Handle failure
        task.setOnFailed(e -> {
            setLoadingState(false);
            showError("Cannot connect to server. Please try again.");
            task.getException().printStackTrace();
        });

        // Run task in background
        new Thread(task).start();
    }

    /**
     * Show success panel with staff ID and password
     */
    private void showSuccess(String staffId) {
        formPanel.setVisible(false);
        formPanel.setManaged(false);

        staffIdLabel.setText(staffId);
        successPanel.setVisible(true);
        successPanel.setManaged(true);;
    }


    /**
     * Show error message
     */
    private void showError(String message) {
        setMessage("message-error", "❌ " + message);
    }

    private void setMessage(String style, String text) {
        enrollmentMessageLabel.getStyleClass().removeAll(
                "message-success", "message-error"
        );
        enrollmentMessageLabel.getStyleClass().add(style);
        enrollmentMessageLabel.setText(text);
        enrollmentMessageLabel.setVisible(true);
        enrollmentMessageLabel.setManaged(true);
    }

    private void clearMessage() {
        enrollmentMessageLabel.setVisible(false);
        enrollmentMessageLabel.setManaged(false);
    }

    private void setLoadingState(boolean loading) {
        enrollButton.setDisable(loading);
        enrollButton.setText(loading ? "Enrolling..." : "Enroll Staff");
    }

    private boolean isEmpty(TextField field) {
        return field.getText().trim().isEmpty();
    }

    private String getSelectedGender() {
        return maleRadio.isSelected() ? "Male" : "Female";
    }


    /**
     * Handle back to login
     */
    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/Authentication/auth-container.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) backToLoginLink.getScene().getWindow();

            stage.setTitle("EAZY MANAGE - Authentication");
            stage.setScene(scene);
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.setResizable(false);

        } catch (IOException e) {
            showError("Error loading login page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}