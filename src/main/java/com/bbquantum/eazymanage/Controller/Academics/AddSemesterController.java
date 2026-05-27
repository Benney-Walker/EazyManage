package com.bbquantum.eazymanage.Controller.Academics;

import com.bbquantum.eazymanage.Services.AcademicsService;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class AddSemesterController {

    @FXML private TextField nameField;
    @FXML private TextField  academicYearField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label statusLabel;
    @FXML private Button resetButton;
    @FXML private Button     saveButton;

    @SuppressWarnings("unused")
    private String role;
    private String staffId;

    private final ApiCore apiCore = new ApiCore();
    private final AcademicsService academicsService = new AcademicsService(apiCore);

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
    }

    @FXML
    private void onSave() {
        String name = trim(nameField.getText());
        String year = trim(academicYearField.getText());
        LocalDate start = startDatePicker.getValue();
        LocalDate end   = endDatePicker.getValue();

        if (name.isEmpty() || year.isEmpty() || start == null || end == null) {
            setStatus("Fill every field.");
            return;
        }
        if (!end.isAfter(start)) {
            setStatus("End date must be after start date.");
            return;
        }

        saveButton.setDisable(true);
        resetButton.setDisable(true);
        setStatus("Adding…");

        academicsService.addNewSemester(staffId, name, start, end, year,
                () -> Platform.runLater(() -> {
                    setStatus("Semester added.");
                    clearForm();
                    saveButton.setDisable(false);
                    resetButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err == null || err.isBlank() ? "Could not add semester." : err);
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
        nameField.clear();
        academicYearField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }

    private void setStatus(String s) { statusLabel.setText(s == null ? "" : s); }
    private static String trim(String s) { return s == null ? "" : s.trim(); }
}
