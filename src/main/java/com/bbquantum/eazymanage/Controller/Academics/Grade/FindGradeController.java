package com.bbquantum.eazymanage.Controller.Academics.Grade;

import com.bbquantum.eazymanage.DTO.Academics.Grading.SaveGradingCriteria;
import com.bbquantum.eazymanage.Services.AcademicsService;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

public class FindGradeController {

    // ---- top bar
    @FXML private ChoiceBox<SaveGradingCriteria> gradeChoice;
    @FXML private Button refreshButton;
    @FXML private Button loadButton;
    @FXML private Label statusLabel;

    // ---- form
    @FXML private ScrollPane formScroll;
    @FXML private Label      headerLabel;
    @FXML private TextField gradeField;
    @FXML private TextField  lowerRangeField;
    @FXML private TextField  upperRangeField;
    @FXML private TextField  remarkField;
    @FXML private Label      saveStatusLabel;
    @FXML private Button     saveButton;

    @SuppressWarnings("unused")
    private String role;
    private String staffId;

    private final ApiCore apiCore = new ApiCore();
    private final AcademicsService academicsService = new AcademicsService(apiCore);

    /** The original criterion that was loaded — kept around so the
     *  update payload can carry the unchanged grade letter as
     *  {@code originalGrade}, even if the admin renamed it in the form. */
    private SaveGradingCriteria loadedOriginal;

    @FXML
    private void initialize() {
        gradeChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(SaveGradingCriteria g) {
                if (g == null) return "";
                return g.getGrade() + "  (" + formatMark(g.getLowerRange())
                        + "–" + formatMark(g.getUpperRange()) + ")";
            }

            @Override
            public SaveGradingCriteria fromString(String s) { return null; }
        });

        gradeChoice.valueProperty().addListener((obs, oldVal, newVal) ->
                loadButton.setDisable(newVal == null));
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
        // Populate the choice list as soon as the tab is opened, so the
        // admin doesn't have to click Refresh first.
        refreshGradeList();
    }

    // ====================================================================
    // List
    // ====================================================================

    @FXML
    private void onRefresh() {
        refreshGradeList();
    }

    private void refreshGradeList() {
        statusLabel.setText("Loading list…");
        refreshButton.setDisable(true);
        loadButton.setDisable(true);

        academicsService.loadGradingCriteria(staffId,
                criteria -> Platform.runLater(() -> {
                    refreshButton.setDisable(false);
                    if (criteria == null || criteria.isEmpty()) {
                        gradeChoice.getItems().clear();
                        statusLabel.setText("No grading criteria configured yet. Add some first.");
                        return;
                    }
                    gradeChoice.getItems().setAll(criteria);
                    statusLabel.setText("");
                }),
                () -> { /* hook — Consumer already fired */ },
                () -> Platform.runLater(() -> {
                    refreshButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    statusLabel.setText(err == null || err.isBlank()
                            ? "Could not load grading criteria." : err);
                }),
                () -> Platform.runLater(() -> {
                    refreshButton.setDisable(false);
                    statusLabel.setText("Network unavailable.");
                }));
    }

    // ====================================================================
    // Load one for editing
    // ====================================================================

    @FXML
    private void onLoad() {
        SaveGradingCriteria picked = gradeChoice.getValue();
        if (picked == null) return;
        // The list response already carries everything we need to edit,
        // so we don't need a second round-trip to fetch one. Just clone
        // into the form.
        loadedOriginal = picked;
        gradeField.setText(safe(picked.getGrade()));
        lowerRangeField.setText(formatMark(picked.getLowerRange()));
        upperRangeField.setText(formatMark(picked.getUpperRange()));
        remarkField.setText(safe(picked.getGradeDescription()));
        headerLabel.setText("Editing: " + safe(picked.getGrade()));

        formScroll.setVisible(true);
        formScroll.setManaged(true);
        statusLabel.setText("");
        setSaveStatus("");
    }

    // ====================================================================
    // Save changes
    // ====================================================================

    @FXML
    private void onSave() {
        if (loadedOriginal == null) {
            setSaveStatus("Load a grade first.");
            return;
        }

        String grade   = trim(gradeField.getText());
        String lowerS  = trim(lowerRangeField.getText());
        String upperS  = trim(upperRangeField.getText());
        String remark  = trim(remarkField.getText());

        if (grade.isEmpty())                       { setSaveStatus("Grade letter is required."); return; }
        if (lowerS.isEmpty() || upperS.isEmpty())  { setSaveStatus("Enter both lower and upper marks."); return; }
        Double lower = parseOrNull(lowerS);
        Double upper = parseOrNull(upperS);
        if (lower == null || upper == null)        { setSaveStatus("Marks must be valid numbers."); return; }
        if (lower > upper)                         { setSaveStatus("Lower mark cannot exceed upper mark."); return; }
        if (remark.isEmpty())                      { setSaveStatus("Remark is required."); return; }

        SaveGradingCriteria payload = new SaveGradingCriteria(lower, upper, grade, remark);
        // Carry the original letter so the backend can locate the row
        // even when the admin renames it (e.g. "A" → "A+").
        payload.setId(loadedOriginal.getId());

        saveButton.setDisable(true);
        setSaveStatus("Saving…");

        academicsService.saveGradingCriteria(staffId, payload,
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    String msg = ApiContext.getInstance().successMessageContainer;
                    setSaveStatus(msg == null || msg.isBlank() ? "Saved." : msg);
                    // Reflect the new value back into the picker without
                    // a full refresh — replace the entry in place.
                    int idx = gradeChoice.getItems().indexOf(loadedOriginal);
                    if (idx >= 0) gradeChoice.getItems().set(idx, payload);
                    loadedOriginal = payload;
                    headerLabel.setText("Editing: " + grade);
                }),
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setSaveStatus(err == null || err.isBlank() ? "Save failed." : err);
                }),
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    setSaveStatus("Network unavailable. Try again when you're back online.");
                }));
    }

    // ====================================================================
    // Helpers
    // ====================================================================

    private void setSaveStatus(String s) { saveStatusLabel.setText(s == null ? "" : s); }

    private static String safe(String s) { return s == null ? "" : s; }

    private static String trim(String s) { return s == null ? "" : s.trim(); }

    private static Double parseOrNull(String s) {
        try { return Double.parseDouble(s); }
        catch (NumberFormatException e) { return null; }
    }

    /** Strip a trailing ".0" so a whole-number mark reads as "80" not "80.0". */
    private static String formatMark(Double d) {
        if (d == null) return "";
        if (d == Math.floor(d) && !Double.isInfinite(d)) {
            return Integer.toString(d.intValue());
        }
        return Double.toString(d);
    }
}
