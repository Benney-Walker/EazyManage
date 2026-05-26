package com.bbquantum.eazymanage.Controller.Students;

import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.DTO.Institution.UpdateStudentPersonalData;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Services.StudentService;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class FindStudentController {

    @FXML private TextField searchField;
    @FXML private Label statusLabel;
    @FXML private Button searchButton;

    @FXML private ScrollPane formScroll;
    @FXML private Label                    resultHeaderLabel;
    @FXML private Label                    resultIdLabel;

    @FXML private TextField                firstNameField;
    @FXML private TextField                lastNameField;
    @FXML private ChoiceBox<GradeCaching> classChoice;
    @FXML private ChoiceBox<String>        genderChoice;
    @FXML private DatePicker               dobPicker;
    @FXML private TextField                hometownField;
    @FXML private TextField                parentNameField;
    @FXML private TextField                guardianContactField;
    @FXML private ChoiceBox<String>        statusChoice;

    @FXML private Label                    saveStatusLabel;
    @FXML private Button                   resetButton;
    @FXML private Button                   saveButton;

    private String role;
    private String staffId;

    private final ApiCore apiCore = new ApiCore();
    private final StudentService studentService = new StudentService(apiCore);

    /** The student we loaded — used to preserve the studentId on save. */
    private UpdateStudentPersonalData currentStudent;

    @FXML
    private void initialize() {
        classChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(GradeCaching g) { return g == null ? "" : g.getLevelName(); }

            @Override
            public GradeCaching fromString(String s) { return null; }
        });

        List<GradeCaching> grades = UtilityClass.gradeCacheManager.getGradesCache();
        if (grades != null) classChoice.getItems().setAll(grades);

        genderChoice.setItems(FXCollections.observableArrayList("MALE", "FEMALE"));
        // Status values match the backend's enum; mirror the legacy set.
        statusChoice.setItems(FXCollections.observableArrayList(
                "ACTIVE", "INACTIVE", "GRADUATED"));
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
        applyRoleFilter();
    }

    public void prefillAndSearch(String id) {
        if (id == null || id.isBlank()) return;
        searchField.setText(id);
        onSearch();
    }

    /** Show / hide the Save button based on role. */
    private void applyRoleFilter() {
        boolean canSave = "PRINCIPAL".equals(role) || "ADMINISTRATOR".equals(role);
        saveButton.setVisible(canSave);
        saveButton.setManaged(canSave);

        // For view-only roles, lock all fields so the user understands.
        boolean readOnly = !canSave;
        for (javafx.scene.control.Control c : new javafx.scene.control.Control[]{
                firstNameField, lastNameField, classChoice, genderChoice,
                dobPicker, hometownField, parentNameField, guardianContactField, statusChoice}) {
            c.setDisable(readOnly);
        }
    }

    // ====================================================================
    // Search
    // ====================================================================

    @FXML
    private void onSearch() {
        String id = trim(searchField.getText());
        if (id.isEmpty()) {
            setStatus("Enter a student id.");
            return;
        }

        searchButton.setDisable(true);
        setStatus("Searching…");
        hideForm();

        studentService.searchStudentData(staffId, id,
                () -> Platform.runLater(() -> {
                    searchButton.setDisable(false);
                    UpdateStudentPersonalData data = UtilityClass.updateStudentPersonalData;
                    if (data == null) {
                        setStatus("No student found with id " + id + ".");
                        return;
                    }
                    populateForm(data);
                    setStatus("");
                }),
                () -> Platform.runLater(() -> {
                    searchButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err == null || err.isBlank() ? "Could not find that student." : err);
                }),
                () -> Platform.runLater(() -> {
                    searchButton.setDisable(false);
                    setStatus("Network unavailable.");
                }));
    }

    private void populateForm(UpdateStudentPersonalData data) {
        currentStudent = data;

        firstNameField.setText(safe(data.getFirstName()));
        lastNameField.setText(safe(data.getLastName()));
        hometownField.setText(safe(data.getHomeTown()));
        parentNameField.setText(safe(data.getParentName()));
        guardianContactField.setText(safe(data.getParentPhoneNumber()));

        genderChoice.setValue(safe(data.getGender()));
        statusChoice.setValue(safe(data.getStatus()));

        // Find the matching grade from the cached list.
        if (data.getGradeId() != null) {
            for (GradeCaching g : classChoice.getItems()) {
                if (data.getGradeId().equals(g.getLevelId())) {
                    classChoice.setValue(g);
                    break;
                }
            }
        }

        // Parse the DTO's String date into a LocalDate for the picker.
        if (data.getDateOfBirth() != null && !data.getDateOfBirth().isBlank()) {
            try {
                dobPicker.setValue(LocalDate.parse(data.getDateOfBirth()));
            } catch (DateTimeParseException ignored) {
                dobPicker.setValue(null);   // legacy backends sometimes ship dates in other formats
            }
        } else {
            dobPicker.setValue(null);
        }

        resultHeaderLabel.setText(safe(data.getFirstName()) + " " + safe(data.getLastName()));
        resultIdLabel.setText("ID: " + safe(data.getStudentId()));

        formScroll.setVisible(true);
        formScroll.setManaged(true);
    }

    // ====================================================================
    // Save
    // ====================================================================

    @FXML
    private void onSave() {
        if (!canEdit()) {
            setSaveStatus("You don't have permission to edit students.");
            return;
        }
        if (currentStudent == null) {
            setSaveStatus("Search a student first.");
            return;
        }

        // Read form back into a fresh DTO so a partial save doesn't corrupt
        // the cached one.
        UpdateStudentPersonalData out = new UpdateStudentPersonalData();
        out.setStudentId(currentStudent.getStudentId());
        out.setFirstName(trim(firstNameField.getText()));
        out.setLastName(trim(lastNameField.getText()));
        GradeCaching grade = classChoice.getValue();
        out.setGradeId(grade == null ? "" : grade.getLevelId());
        out.setGender(genderChoice.getValue());
        out.setDateOfBirth(dobPicker.getValue() == null ? "" : dobPicker.getValue().toString());
        out.setHomeTown(trim(hometownField.getText()));
        out.setParentName(trim(parentNameField.getText()));
        out.setParentPhoneNumber(trim(guardianContactField.getText()));
        out.setStatus(statusChoice.getValue());

        // Light validation.
        if (out.getFirstName().isEmpty() || out.getLastName().isEmpty()
                || out.getGradeId().isEmpty() || out.getGender() == null
                || out.getDateOfBirth().isEmpty() || out.getStatus() == null) {
            setSaveStatus("All fields must be filled.");
            return;
        }

        saveButton.setDisable(true);
        resetButton.setDisable(true);
        setSaveStatus("Saving…");

        studentService.updateStudentPersonalData(out, staffId,
                () -> Platform.runLater(() -> {
                    setSaveStatus("Saved.");
                    saveButton.setDisable(false);
                    resetButton.setDisable(false);
                    currentStudent = out;   // refresh local snapshot
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

    @FXML
    private void onResetForm() {
        if (currentStudent != null) {
            populateForm(currentStudent);
            setSaveStatus("Reverted to last loaded values.");
        } else {
            hideForm();
            setSaveStatus("");
        }
    }

    private void hideForm() {
        formScroll.setVisible(false);
        formScroll.setManaged(false);
        currentStudent = null;
    }

    private boolean canEdit() {
        return "PRINCIPAL".equals(role) || "ADMINISTRATOR".equals(role);
    }

    private void setStatus(String s)      { statusLabel.setText(s == null ? "" : s); }
    private void setSaveStatus(String s)  { saveStatusLabel.setText(s == null ? "" : s); }
    private static String trim(String s)  { return s == null ? "" : s.trim(); }
    private static String safe(String s)  { return s == null ? "" : s; }
}
