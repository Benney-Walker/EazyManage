package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.DTO.Academics.GradeCaching;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class AddStudentController implements Initializable {

    private UtilityClass utilityClass;
    private String staffId;
    private String studentId;

    // ADD STUDENT VIEW
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private TextField homeTownField;
    @FXML private TextField parentNameField;
    @FXML private TextField parentPhoneField;
    @FXML private ComboBox<GradeCaching> classComboBox;
    @FXML private Label messageLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        utilityClass = new UtilityClass(this);

        // Populate gender ComboBox
        genderComboBox.getItems().addAll("Male", "Female");

        // Populate class ComboBox from cache
        List<GradeCaching> grades = utilityClass.gradeCacheManager.getGradeCache();
        if (grades != null && !grades.isEmpty()) {
            classComboBox.getItems().addAll(grades);
        }
    }

    @FXML
    public void handleSaveAndClose() {
        if (!validateForm()) return;

        saveStudent(() -> {
            // This runs AFTER the backend responds
            Platform.runLater(() -> {
                utilityClass.showSuccess("Student saved successfully! ID: " + studentId);
                Stage stage = (Stage) firstNameField.getScene().getWindow();
                stage.close();
            });
        }, () -> {
            // On failure - form stays open
        });
    }

    @FXML
    public void handleSaveAndClear() {
        if (!validateForm()) return;

        saveStudent(() -> {
            Platform.runLater(() -> {
                utilityClass.showSuccess("Successfully saved student with ID: " + studentId);
                clearForm();
            });
        }, () -> {
            // On failure
        });
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    private void saveStudent(Runnable onSuccess, Runnable onFailure) {
        // Get values
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String gender = genderComboBox.getValue();
        LocalDate dob = dateOfBirthPicker.getValue();
        String homeTown = homeTownField.getText().trim();
        String parentName = parentNameField.getText().trim();
        String parentPhone = parentPhoneField.getText().trim();

        GradeCaching selectedClass = classComboBox.getSelectionModel().getSelectedItem();
        String selectedGradeId = null;
        if (selectedClass != null) {
            selectedGradeId = selectedClass.getLevelId();
        }

        utilityClass.enrollNewStudent(firstName, lastName, selectedGradeId, gender,
                dob, homeTown, parentName, parentPhone, staffId, onSuccess, onFailure);
    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        genderComboBox.getSelectionModel().clearSelection();
        dateOfBirthPicker.setValue(null);
        homeTownField.clear();
        parentNameField.clear();
        parentPhoneField.clear();
        classComboBox.getSelectionModel().clearSelection();
    }

    private boolean validateForm() {
        if (firstNameField.getText().trim().isEmpty()) {
            utilityClass.showError("First name is required");
            return false;
        }
        if (lastNameField.getText().trim().isEmpty()) {
            utilityClass.showError("Last name is required");
            return false;
        }
        if (genderComboBox.getValue() == null) {
            utilityClass.showError("Please select a gender");
            return false;
        }
        if (dateOfBirthPicker.getValue() == null) {
            utilityClass.showError("Date of birth is required");
            return false;
        }
        if (classComboBox.getValue() == null) {
            utilityClass.showError("Please select a class");
            return false;
        }
        return true;
    }
}