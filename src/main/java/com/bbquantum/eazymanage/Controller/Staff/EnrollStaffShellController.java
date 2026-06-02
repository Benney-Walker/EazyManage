package com.bbquantum.eazymanage.Controller.Staff;

import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Services.AuthService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnrollStaffShellController {

    @FXML private TextField firstNameField;
    @FXML private TextField     lastNameField;
    @FXML private ChoiceBox<String> genderChoice;
    @FXML private DatePicker dobPicker;
    @FXML private TextField     emailField;
    @FXML private TextField     phoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private CheckBox rolePrincipal;
    @FXML private CheckBox roleAdministrator;
    @FXML private CheckBox roleAccountant;
    @FXML private CheckBox roleTeachingStaff;
    @FXML private CheckBox roleGeneralStaff;

    @FXML private Label  statusLabel;
    @FXML private Button resetButton;
    @FXML private Button enrollButton;

    @FXML private HBox successBanner;
    @FXML private Label successTitleLabel;
    @FXML private Label successMessageLabel;

    @SuppressWarnings("unused")
    private String role;
    @SuppressWarnings("unused")
    private String staffId;
    private String organizationId;

    private final ApiCore apiCore = new ApiCore();
    private final AuthService authService = new AuthService(apiCore);

    @FXML
    private void initialize() {
        genderChoice.setItems(FXCollections.observableArrayList("MALE", "FEMALE"));
    }

    public void attach(String role, String staffId, String organizationId) {
        this.role           = role;
        this.staffId        = staffId;
        this.organizationId = organizationId;
    }

    // ====================================================================
    // Enroll
    // ====================================================================

    @FXML
    private void onEnroll() {
        String firstName = trim(firstNameField.getText());
        String lastName  = trim(lastNameField.getText());
        String gender    = genderChoice.getValue();
        LocalDate dob    = dobPicker.getValue();
        String email     = trim(emailField.getText());
        String phone     = trim(phoneField.getText());
        String password  = passwordField.getText();
        String confirm   = confirmPasswordField.getText();
        List<String> roles = collectRoles();

        if (firstName.isEmpty() || lastName.isEmpty() || gender == null || dob == null
                || email.isEmpty() || password == null || password.isEmpty()) {
            setStatus("Fill every field before enrolling.");
            return;
        }
        if (phone.length() != 10 || phone.matches("\\d+")) {
            setStatus("Phone number is invalid.");
            return;
        }
        if (!password.equals(confirm)) {
            setStatus("Passwords do not match.");
            return;
        }
        if (roles.isEmpty()) {
            setStatus("Pick at least one role for this staff member.");
            return;
        }

        enrollButton.setDisable(true);
        resetButton.setDisable(true);
        setStatus("Enrolling…");
        hideSuccessBanner();

        authService.enrollNewStaff(firstName, lastName, gender, dob, email, phone,
                password, roles, staffId,
                response -> Platform.runLater(() -> {
                    enrollButton.setDisable(false);
                    resetButton.setDisable(false);
                    if (response.isSuccess()) {
                        showSuccessBanner(response.getMessage());
                        clearForm();
                        setStatus("");
                    } else {
                        setStatus(response.getMessage() == null
                                ? "Enrollment failed."
                                : response.getMessage());
                    }
                }),
                () -> Platform.runLater(() -> {
                    enrollButton.setDisable(false);
                    resetButton.setDisable(false);
                    setStatus("Network unavailable.");
                }));
    }

    private List<String> collectRoles() {
        List<String> roles = new ArrayList<>();
        if (rolePrincipal.isSelected())     roles.add("PRINCIPAL");
        if (roleAdministrator.isSelected()) roles.add("ADMINISTRATOR");
        if (roleAccountant.isSelected())    roles.add("ACCOUNTANT");
        if (roleTeachingStaff.isSelected()) roles.add("TEACHING_STAFF");
        if (roleGeneralStaff.isSelected())  roles.add("GENERAL_STAFF");
        return roles;
    }

    private void showSuccessBanner(String message) {
        successTitleLabel.setText("Staff enrolled");
        // The success body from the backend is the assigned staff id;
        // show it so the enroller can write it down or share with the
        // new colleague.
        successMessageLabel.setText("Assigned id / message: " + (message == null ? "" : message));
        successBanner.setVisible(true);
        successBanner.setManaged(true);
    }

    private void hideSuccessBanner() {
        successBanner.setVisible(false);
        successBanner.setManaged(false);
    }

    @FXML
    private void onReset() {
        clearForm();
        hideSuccessBanner();
        setStatus("");
    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        genderChoice.getSelectionModel().clearSelection();
        dobPicker.setValue(null);
        emailField.clear();
        phoneField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        rolePrincipal.setSelected(false);
        roleAdministrator.setSelected(false);
        roleAccountant.setSelected(false);
        roleTeachingStaff.setSelected(false);
        roleGeneralStaff.setSelected(false);
    }

    private void setStatus(String s) { statusLabel.setText(s == null ? "" : s); }
    private static String trim(String s) { return s == null ? "" : s.trim(); }
}
