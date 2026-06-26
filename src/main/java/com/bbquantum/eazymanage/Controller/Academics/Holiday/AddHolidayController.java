package com.bbquantum.eazymanage.Controller.Academics.Holiday;

import com.bbquantum.eazymanage.DTO.Academics.Holiday.Holiday;
import com.bbquantum.eazymanage.DTO.Institution.SemesterCaching;
import com.bbquantum.eazymanage.Services.AcademicsService;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AddHolidayController {

    @FXML private ChoiceBox<String> typeChoice;
    @FXML private ChoiceBox<SemesterCaching> semesterChoice;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private CheckBox activeCheck;
    @FXML private Label statusLabel;
    @FXML private Button resetButton;
    @FXML private Button saveButton;

    @SuppressWarnings("unused")
    private String role;
    private String staffId;

    private final ApiCore apiCore = new ApiCore();
    private final AcademicsService academicsService = new AcademicsService(apiCore);

    private static final Map<String, String> TYPE_LABELS = new LinkedHashMap<>();
    static {
        TYPE_LABELS.put("Public holiday",     "PUBLIC_HOLIDAY");
        TYPE_LABELS.put("Mid-term break",     "MID_TERM_BREAK");
        TYPE_LABELS.put("School event",       "SCHOOL_EVENT");
        TYPE_LABELS.put("Emergency closure",  "EMERGENCY_CLOSURE");
    }

    @FXML
    private void initialize() {
        typeChoice.getItems().setAll(TYPE_LABELS.keySet());

        semesterChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(SemesterCaching s) {
                return s == null ? "" : s.getSemesterName() + " " + s.getAcademicYear();
            }
            @Override
            public SemesterCaching fromString(String s) { return null; }
        });

        List<SemesterCaching> semesters = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (semesters != null) semesterChoice.getItems().setAll(semesters);
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
    }

    @FXML
    private void onSave() {
        String typeLabel = typeChoice.getValue();
        SemesterCaching semester = semesterChoice.getValue();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (typeLabel == null) { setStatus("Pick a holiday type.");      return; }
        if (semester == null) {
            setStatus(semesterChoice.getItems().isEmpty()
                    ? "No semesters available. Add a semester first."
                    : "Pick a semester.");
            return;
        }
        if (start == null || end == null) { setStatus("Pick both start and end dates."); return; }
        if (end.isBefore(start)) { setStatus("End date cannot be before start date."); return; }

        Holiday holiday = new Holiday(
                TYPE_LABELS.get(typeLabel),
                start.toString(),
                end.toString(),
                semester.getSemesterId());

        holiday.setHolidayId(0);
        holiday.setSemesterName(semester.getSemesterName());
        holiday.setAcademicYear(semester.getAcademicYear());

        saveButton.setDisable(true);
        resetButton.setDisable(true);
        setStatus("Adding…");

        academicsService.addNewHoliday(staffId, holiday,
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    resetButton.setDisable(false);
                    String msg = ApiContext.getInstance().successMessageContainer;
                    setStatus(msg == null || msg.isBlank() ? "Holiday added." : msg);
                    // Invalidate the holiday cache so the Find tab picks up the
                    // new entry next time it loads.
                    UtilityClass.holidayCachingManager.clearCachedHolidays();
                    clearForm();
                }),
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    resetButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err == null || err.isBlank() ? "Could not add holiday." : err);
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
        typeChoice.setValue(null);
        semesterChoice.setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        activeCheck.setSelected(true);
    }

    private void setStatus(String s) { statusLabel.setText(s == null ? "" : s); }
}
