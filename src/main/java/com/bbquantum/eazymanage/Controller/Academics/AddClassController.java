package com.bbquantum.eazymanage.Controller.Academics;

import com.bbquantum.eazymanage.DTO.Institution.StaffCaching;
import com.bbquantum.eazymanage.Services.AcademicsService;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.util.List;

public class AddClassController {

    @FXML private TextField classNameField;
    @FXML private ChoiceBox<StaffCaching> instructorChoice;
    @FXML private Label statusLabel;
    @FXML private Button resetButton;
    @FXML private Button saveButton;

    @SuppressWarnings("unused")
    private String role;
    private String staffId;

    private final ApiCore apiCore = new ApiCore();
    private final AcademicsService academicsService = new AcademicsService(apiCore);

    @FXML
    private void initialize() {
        instructorChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(StaffCaching s) {
                if (s == null) return "";
                return safe(s.getStaffName()) + " (" + safe(s.getStaffId()) + ")";
            }

            @Override
            public StaffCaching fromString(String s) { return null; }
        });

        List<StaffCaching> cached = UtilityClass.staffCacheManager.getCachedStaffList();
        if (cached != null) instructorChoice.getItems().setAll(cached);
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
    }

    @FXML
    private void onSave() {
        String className = trim(classNameField.getText());

        // Legacy validation: name must contain '_' and must not contain spaces.
        if (className.isEmpty()) {
            setStatus("Class name is required.");
            return;
        }
        if (!className.contains("_") || className.contains(" ")) {
            setStatus("Class name must contain '_' and no spaces (e.g. Grade_1).");
            return;
        }
        StaffCaching instructor = instructorChoice.getValue();
        if (instructor == null) {
            setStatus("Pick a class teacher.");
            return;
        }

        saveButton.setDisable(true);
        resetButton.setDisable(true);
        setStatus("Adding…");

        academicsService.addNewGrade(staffId, className, instructor.getStaffId(),
                () -> Platform.runLater(() -> {
                    setStatus("Class added.");
                    clearForm();
                    saveButton.setDisable(false);
                    resetButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err == null || err.isBlank() ? "Could not add class." : err);
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
        classNameField.clear();
        instructorChoice.getSelectionModel().clearSelection();
    }

    private void setStatus(String s) { statusLabel.setText(s == null ? "" : s); }
    private static String trim(String s) { return s == null ? "" : s.trim(); }
    private static String safe(String s) { return s == null ? "" : s; }
}
