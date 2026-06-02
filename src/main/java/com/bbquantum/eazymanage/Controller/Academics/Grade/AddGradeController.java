package com.bbquantum.eazymanage.Controller.Academics.Grade;

import com.bbquantum.eazymanage.DTO.Academics.Grading.SaveGradingCriteria;
import com.bbquantum.eazymanage.Services.AcademicsService;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddGradeController {

    @FXML private TextField gradeField;
    @FXML private TextField lowerRangeField;
    @FXML private TextField upperRangeField;
    @FXML private TextField remarkField;
    @FXML private Label statusLabel;
    @FXML private Button saveButton;
    @FXML private Button    resetButton;

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
        String grade  = trim(gradeField.getText());
        String lowerS = trim(lowerRangeField.getText());
        String upperS = trim(upperRangeField.getText());
        String remark = trim(remarkField.getText());

        if (grade.isEmpty())             { setStatus("Grade letter is required."); return; }
        if (lowerS.isEmpty() || upperS.isEmpty()) { setStatus("Enter both lower and upper marks."); return; }
        Double lower = parseOrNull(lowerS);
        Double upper = parseOrNull(upperS);
        if (lower == null || upper == null) { setStatus("Marks must be valid numbers.");        return; }
        if (lower > upper)                  { setStatus("Lower mark cannot exceed upper mark."); return; }
        if (remark.isEmpty())               { setStatus("Remark is required.");                 return; }

        SaveGradingCriteria criterion = new SaveGradingCriteria(lower, upper, grade, remark);
        criterion.setId(0);

        saveButton.setDisable(true);
        resetButton.setDisable(true);
        setStatus("Saving…");

        academicsService.saveGradingCriteria(staffId, criterion,
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    resetButton.setDisable(false);
                    String msg = ApiContext.getInstance().successMessageContainer;
                    setStatus(msg == null || msg.isBlank() ? "Grade added." : msg);
                    clearForm();
                }),
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    resetButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err == null || err.isBlank() ? "Could not add grade." : err);
                }),
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    resetButton.setDisable(false);
                    setStatus("Network unavailable. Try again when you're back online.");
                }));
    }

    @FXML
    private void onReset() {
        clearForm();
        setStatus("");
    }

    private void clearForm() {
        gradeField.clear();
        lowerRangeField.clear();
        upperRangeField.clear();
        remarkField.clear();
    }

    private void setStatus(String s) { statusLabel.setText(s == null ? "" : s); }
    private static String trim(String s) { return s == null ? "" : s.trim(); }
    private static Double parseOrNull(String s) {
        try { return Double.parseDouble(s); }
        catch (NumberFormatException e) { return null; }
    }
}
