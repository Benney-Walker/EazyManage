package com.bbquantum.eazymanage.Controller.Academics.Semester;

import com.bbquantum.eazymanage.Constants.TERMS;
import com.bbquantum.eazymanage.DTO.Institution.FindSemester;
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
import java.time.format.DateTimeParseException;
import java.util.List;

public class FindSemesterController {

    @FXML private ChoiceBox<SemesterCaching> semesterChoice;
    @FXML private Button loadButton;
    @FXML private Label statusLabel;

    @FXML private ScrollPane formScroll;
    @FXML private Label      headerLabel;
    @FXML private ChoiceBox<TERMS> semesterNumberChoice;
    @FXML private TextField  academicYearField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label      saveStatusLabel;
    @FXML private Button     saveButton;

    @SuppressWarnings("unused")
    private String role;
    @SuppressWarnings("unused")
    private String staffId;

    private final ApiCore apiCore = new ApiCore();
    private final AcademicsService academicsService = new AcademicsService(apiCore);

    private FindSemester currentSemester;

    @FXML
    private void initialize() {
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

        semesterChoice.valueProperty().addListener((obs, oldVal, newVal) ->
                loadButton.setDisable(newVal == null));

        semesterNumberChoice.getItems().addAll(TERMS.values());
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
    }

    @FXML
    private void onLoad() {
        SemesterCaching picked = semesterChoice.getValue();
        if (picked == null) return;

        loadButton.setDisable(true);
        setStatus("Loading…");
        hideForm();

        academicsService.searchSemester(picked.getSemesterId(), staffId,
                result -> Platform.runLater(() -> {
                    loadButton.setDisable(false);
                    if (result == null) {
                        setStatus("Semester not found.");
                        return;
                    }
                    populateForm(result);
                    setStatus("");
                }),
                () -> { /* onSuccess hook — Consumer already fired */ },
                () -> Platform.runLater(() -> {
                    loadButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err == null || err.isBlank() ? "Could not load semester." : err);
                }),
                () -> Platform.runLater(() -> {
                    loadButton.setDisable(false);
                    setStatus("Network unavailable.");
                }));
    }

    private void populateForm(FindSemester data) {
        currentSemester = data;

        String[] semesterNameSplit = data.getSemesterName().split("_");
        String name = "";
        if (semesterNameSplit.length == 2) {
            name = semesterNameSplit[0];
        }
        semesterNumberChoice.getSelectionModel().select(TERMS.valueOf(name));
        academicYearField.setText(safe(data.getAcademicYear()));
        startDatePicker.setValue(parse(data.getSemesterStartDate()));
        endDatePicker.setValue(parse(data.getSemesterEndDate()));
        headerLabel.setText("Editing: " + safe(data.getSemesterName()));

        formScroll.setVisible(true);
        formScroll.setManaged(true);
    }

    private static LocalDate parse(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s); }
        catch (DateTimeParseException ignored) { return null; }
    }

    @FXML
    private void onSave() {
        if (currentSemester == null) {
            setSaveStatus("Load a semester first.");
            return;
        }
        TERMS selectedNumber  = semesterNumberChoice.getValue();
        String year  = trim(academicYearField.getText());
        LocalDate s  = startDatePicker.getValue();
        LocalDate e  = endDatePicker.getValue();

        if (selectedNumber == null || year.isEmpty() || s == null || e == null) {
            setSaveStatus("All fields are required.");
            return;
        }
        if (!e.isAfter(s)) {
            setSaveStatus("End date must be after start date.");
            return;
        }

        String name = selectedNumber.name() + "_" + "TERM";

        FindSemester semesterInfo = new FindSemester();
        semesterInfo.setSemesterID(currentSemester.getSemesterID());
        semesterInfo.setSemesterName(name);
        semesterInfo.setAcademicYear(year);
        semesterInfo.setSemesterStartDate(s.toString());
        semesterInfo.setSemesterEndDate(e.toString());

        saveButton.setDisable(true);
        setSaveStatus("Saving…");

        academicsService.updateSemesterInfo(semesterInfo, staffId,
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    setSaveStatus("Saved.");
                    currentSemester = semesterInfo;
                }),
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setSaveStatus(err == null || err.isBlank() ? "Save failed." : err);
                }),
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    setSaveStatus("Network unavailable.");
                }));
    }

    private void hideForm() {
        formScroll.setVisible(false);
        formScroll.setManaged(false);
        currentSemester = null;
    }

    private void setStatus(String s)     { statusLabel.setText(s == null ? "" : s); }
    private void setSaveStatus(String s) { saveStatusLabel.setText(s == null ? "" : s); }
    private static String trim(String s) { return s == null ? "" : s.trim(); }
    private static String safe(String s) { return s == null ? "" : s; }
}
