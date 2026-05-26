package com.bbquantum.eazymanage.Controller.Staff;

import com.bbquantum.eazymanage.DTO.Institution.FindStaffResponse;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Services.StaffService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class FindStaffShellController {

    @FXML private TextField searchField;
    @FXML private Label statusLabel;
    @FXML private Button searchButton;

    @FXML private ScrollPane formScroll;
    @FXML private Label         resultHeaderLabel;
    @FXML private Label         resultIdLabel;

    @FXML private TextField     firstNameField;
    @FXML private TextField     surnameField;
    @FXML private ChoiceBox<String> genderChoice;
    @FXML private DatePicker    dobPicker;
    @FXML private TextField     emailField;
    @FXML private TextField     phoneField;
    @FXML private ChoiceBox<String> statusChoice;
    @FXML private TextField     dateOfRegistrationField;

    @FXML private CheckBox rolePrincipal;
    @FXML private CheckBox roleAdministrator;
    @FXML private CheckBox roleAccountant;
    @FXML private CheckBox roleTeachingStaff;
    @FXML private CheckBox roleGeneralStaff;

    @FXML private Label  saveStatusLabel;
    @FXML private Button resetButton;
    @FXML private Button saveButton;

    @SuppressWarnings("unused")
    private String role;
    private String staffId;

    private final ApiCore apiCore = new ApiCore();
    private final StaffService staffService = new StaffService(apiCore);

    private FindStaffResponse currentStaff;

    @FXML
    private void initialize() {
        genderChoice.setItems(FXCollections.observableArrayList("MALE", "FEMALE"));
        // Staff statuses observed across the legacy code. Edit here if your
        // backend uses different names.
        statusChoice.setItems(FXCollections.observableArrayList(
                "ACTIVE", "INACTIVE", "SUSPENDED"));
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
    }

    // ====================================================================
    // Search
    // ====================================================================

    @FXML
    private void onSearch() {
        String id = trim(searchField.getText());
        if (id.isEmpty()) {
            setStatus("Enter a staff id.");
            return;
        }

        searchButton.setDisable(true);
        setStatus("Searching…");
        hideForm();

        staffService.searchStaffData(id, staffId,
                () -> Platform.runLater(() -> {
                    searchButton.setDisable(false);
                    FindStaffResponse data = ApiContext.getInstance().findStaffResponse;
                    if (data == null) {
                        setStatus("No staff found with id " + id + ".");
                        return;
                    }
                    populateForm(data);
                    setStatus("");
                }),
                () -> Platform.runLater(() -> {
                    searchButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err == null || err.isBlank() ? "Could not find that staff." : err);
                }),
                () -> Platform.runLater(() -> {
                    searchButton.setDisable(false);
                    setStatus("Network unavailable.");
                }));
    }

    private void populateForm(FindStaffResponse data) {
        currentStaff = data;

        firstNameField.setText(safe(data.getFirstName()));
        surnameField.setText(safe(data.getSurname()));
        emailField.setText(safe(data.getEmail()));
        phoneField.setText(safe(data.getPhoneNumber()));
        dateOfRegistrationField.setText(safe(data.getDateOfRegistration()));

        genderChoice.setValue(safe(data.getGender()));
        statusChoice.setValue(safe(data.getStaffStatus()));

        // Parse date of birth string into LocalDate. Catch parse errors so
        // a malformed legacy value just clears the picker rather than
        // breaking the screen.
        if (data.getDateOfBirth() != null && !data.getDateOfBirth().isBlank()) {
            try {
                dobPicker.setValue(LocalDate.parse(data.getDateOfBirth()));
            } catch (DateTimeParseException ignored) {
                dobPicker.setValue(null);
            }
        } else {
            dobPicker.setValue(null);
        }

        // Roles: tick every checkbox whose label maps to a role on the record.
        List<String> roles = data.getStaffRoles() == null ? List.of() : data.getStaffRoles();
        rolePrincipal.setSelected(roles.contains("PRINCIPAL"));
        roleAdministrator.setSelected(roles.contains("ADMINISTRATOR"));
        roleAccountant.setSelected(roles.contains("ACCOUNTANT"));
        roleTeachingStaff.setSelected(roles.contains("TEACHING_STAFF"));
        roleGeneralStaff.setSelected(roles.contains("GENERAL_STAFF"));

        resultHeaderLabel.setText(safe(data.getFirstName()) + " " + safe(data.getSurname()));
        resultIdLabel.setText("ID: " + safe(data.getStaffId()));

        formScroll.setVisible(true);
        formScroll.setManaged(true);
    }

    // ====================================================================
    // Save
    // ====================================================================

    @FXML
    private void onSave() {
        if (currentStaff == null) {
            setSaveStatus("Search a staff first.");
            return;
        }

        // Rebuild the DTO from the form so partial edits don't corrupt the
        // cached baseline.
        FindStaffResponse out = new FindStaffResponse();
        out.setStaffId(currentStaff.getStaffId());
        out.setFirstName(trim(firstNameField.getText()));
        out.setSurname(trim(surnameField.getText()));
        out.setGender(genderChoice.getValue());
        out.setDateOfBirth(dobPicker.getValue() == null ? "" : dobPicker.getValue().toString());
        out.setEmail(trim(emailField.getText()));
        out.setPhoneNumber(trim(phoneField.getText()));
        out.setStaffStatus(statusChoice.getValue());
        out.setDateOfRegistration(currentStaff.getDateOfRegistration());
        out.setStaffRoles(collectRoles());

        // Validation
        if (out.getFirstName().isEmpty() || out.getSurname().isEmpty()
                || out.getGender() == null || out.getEmail().isEmpty()
                || out.getPhoneNumber().isEmpty() || out.getStaffStatus() == null) {
            setSaveStatus("All fields must be filled.");
            return;
        }
        if (out.getStaffRoles().isEmpty()) {
            setSaveStatus("Pick at least one role.");
            return;
        }

        saveButton.setDisable(true);
        resetButton.setDisable(true);
        setSaveStatus("Saving…");

        staffService.updateStaffInfo(out, staffId,
                () -> Platform.runLater(() -> {
                    setSaveStatus("Saved.");
                    saveButton.setDisable(false);
                    resetButton.setDisable(false);
                    currentStaff = out;
                }),
                () -> Platform.runLater(() -> {
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setSaveStatus(err == null || err.isBlank() ? "Save failed." : err);
                    saveButton.setDisable(false);
                    resetButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setSaveStatus("Network unavailable.");
                    saveButton.setDisable(false);
                    resetButton.setDisable(false);
                }));
    }

    private List<String> collectRoles() {
        List<String> out = new ArrayList<>();
        if (rolePrincipal.isSelected())     out.add("PRINCIPAL");
        if (roleAdministrator.isSelected()) out.add("ADMINISTRATOR");
        if (roleAccountant.isSelected())    out.add("ACCOUNTANT");
        if (roleTeachingStaff.isSelected()) out.add("TEACHING_STAFF");
        if (roleGeneralStaff.isSelected())  out.add("GENERAL_STAFF");
        return out;
    }

    @FXML
    private void onResetForm() {
        if (currentStaff != null) {
            populateForm(currentStaff);
            setSaveStatus("Reverted to last loaded values.");
        } else {
            hideForm();
            setSaveStatus("");
        }
    }

    private void hideForm() {
        formScroll.setVisible(false);
        formScroll.setManaged(false);
        currentStaff = null;
    }

    private void setStatus(String s)     { statusLabel.setText(s == null ? "" : s); }
    private void setSaveStatus(String s) { saveStatusLabel.setText(s == null ? "" : s); }
    private static String trim(String s) { return s == null ? "" : s.trim(); }
    private static String safe(String s) { return s == null ? "" : s; }
}
