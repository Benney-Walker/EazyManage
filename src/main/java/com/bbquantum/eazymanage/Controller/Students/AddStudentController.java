package com.bbquantum.eazymanage.Controller.Students;

import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
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
import java.util.List;

public class AddStudentController {

    @FXML private TextField                firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ChoiceBox<GradeCaching> classChoice;
    @FXML private ChoiceBox<String>        genderChoice;
    @FXML private DatePicker dobPicker;
    @FXML private TextField                hometownField;
    @FXML private TextField                parentNameField;
    @FXML private TextField                guardianContactField;
    @FXML private Label statusLabel;
    @FXML private Button resetButton;
    @FXML private Button                   saveButton;

    private String role;
    private String staffId;

    private final ApiCore apiCore        = new ApiCore();
    private final StudentService studentService = new StudentService(apiCore);

    @FXML
    private void initialize() {
        classChoice.setConverter(new StringConverter<>() {
            @Override public String toString(GradeCaching g) { return g == null ? "" : g.getLevelName(); }
            @Override public GradeCaching fromString(String s) { return null; }
        });
        List<GradeCaching> grades = UtilityClass.gradeCacheManager.getGradesCache();
        if (grades != null) classChoice.getItems().setAll(grades);

        genderChoice.setItems(FXCollections.observableArrayList("MALE", "FEMALE"));
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
    }

    @FXML
    private void onSave() {
        if (!canEdit()) {
            setStatus("You don't have permission to enroll students.");
            return;
        }

        String firstName = trim(firstNameField.getText());
        String lastName  = trim(lastNameField.getText());
        GradeCaching grade = classChoice.getValue();
        String gender    = genderChoice.getValue();
        LocalDate dob    = dobPicker.getValue();
        String hometown  = trim(hometownField.getText());
        String parent    = trim(parentNameField.getText());
        String contact   = trim(guardianContactField.getText());

        if (firstName.isEmpty() || lastName.isEmpty() || grade == null || gender == null
                || dob == null || hometown.isEmpty() || parent.isEmpty() || contact.isEmpty()) {
            setStatus("Fill every field before saving.");
            return;
        }

        saveButton.setDisable(true);
        resetButton.setDisable(true);
        setStatus("Enrolling…");

        studentService.enrollNewStudent(
                staffId, firstName, lastName, grade.getLevelId(), gender,
                dob, hometown, parent, contact,
                () -> Platform.runLater(() -> {
                    setStatus("Student enrolled.");
                    clearForm();
                    saveButton.setDisable(false);
                    resetButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err == null || err.isBlank() ? "Enrollment failed." : err);
                    saveButton.setDisable(false);
                    resetButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setStatus("Network unavailable.");
                    saveButton.setDisable(false);
                    resetButton.setDisable(false);
                }));
    }

    @FXML
    private void onReset() {
        clearForm();
        setStatus("");
    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        classChoice.getSelectionModel().clearSelection();
        genderChoice.getSelectionModel().clearSelection();
        dobPicker.setValue(null);
        hometownField.clear();
        parentNameField.clear();
        guardianContactField.clear();
    }

    private boolean canEdit() {
        return "PRINCIPAL".equals(role) || "ADMINISTRATOR".equals(role);
    }

    private void setStatus(String s) { statusLabel.setText(s == null ? "" : s); }
    private static String trim(String s) { return s == null ? "" : s.trim(); }
}
