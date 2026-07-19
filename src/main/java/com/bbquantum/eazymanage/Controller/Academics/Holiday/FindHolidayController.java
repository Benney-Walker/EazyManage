package com.bbquantum.eazymanage.Controller.Academics.Holiday;

import com.bbquantum.eazymanage.DTO.Academics.Holiday.Holiday;
import com.bbquantum.eazymanage.DTO.Caching.CachedSemesters;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FindHolidayController {

    // ---- top bar
    @FXML private ChoiceBox<Holiday> holidayChoice;
    @FXML private Button refreshButton;
    @FXML private Button loadButton;
    @FXML private Label statusLabel;

    // ---- form
    @FXML private ScrollPane formScroll;
    @FXML private Label headerLabel;
    @FXML private ChoiceBox<String>          typeChoice;
    @FXML private ChoiceBox<CachedSemesters> semesterChoice;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private CheckBox   activeCheck;
    @FXML private Label      saveStatusLabel;
    @FXML private Button     saveButton;

    @SuppressWarnings("unused")
    private String role;
    private String staffId;

    private final ApiCore apiCore = new ApiCore();
    private final AcademicsService academicsService = new AcademicsService(apiCore);

    private Holiday loadedOriginal;

    /** Display label → wire value, same mapping as AddHolidayController. */
    private static final Map<String, String> TYPE_LABELS = new LinkedHashMap<>();
    /** Reverse: wire value → display label, for prefilling the form. */
    private static final Map<String, String> TYPE_BY_VALUE = new LinkedHashMap<>();
    static {
        TYPE_LABELS.put("Public holiday",    "PUBLIC_HOLIDAY");
        TYPE_LABELS.put("Mid-term break",    "MID_TERM_BREAK");
        TYPE_LABELS.put("School event",      "SCHOOL_EVENT");
        TYPE_LABELS.put("Emergency closure", "EMERGENCY_CLOSURE");
        for (Map.Entry<String, String> e : TYPE_LABELS.entrySet()) {
            TYPE_BY_VALUE.put(e.getValue(), e.getKey());
        }
    }

    @FXML
    private void initialize() {
        // Type choices (fixed enum) and semester choices (from cache).
        typeChoice.getItems().setAll(TYPE_LABELS.keySet());

        semesterChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(CachedSemesters s) {
                return s == null ? "" : s.getSemesterName() + " " + s.getAcademicYear();
            }
            @Override
            public CachedSemesters fromString(String s) { return null; }
        });
        List<CachedSemesters> semesters = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (semesters != null) semesterChoice.getItems().setAll(semesters);

        // Top picker — render each holiday distinctly.
        holidayChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Holiday h) {
                if (h == null) return "";
                String label = TYPE_BY_VALUE.getOrDefault(h.getHolidayName(), h.getHolidayName());
                StringBuilder sb = new StringBuilder(label);
                sb.append("  (").append(h.getStartDate());
                if (h.getEndDate() != null && !h.getEndDate().equals(h.getStartDate())) {
                    sb.append(" → ").append(h.getEndDate());
                }
                sb.append(")");
                if (h.getSemesterName() != null && !h.getSemesterName().isBlank()) {
                    sb.append(" — ").append(h.getSemesterName());
                }
                return sb.toString();
            }
            @Override
            public Holiday fromString(String s) { return null; }
        });

        holidayChoice.valueProperty().addListener((obs, oldVal, newVal) ->
                loadButton.setDisable(newVal == null));
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
        // Try the cache first; only call the network on miss.
        List<Holiday> cached = UtilityClass.holidayCachingManager.getCachedHolidays();
        if (cached != null) {
            populateChoice(cached);
        } else {
            refreshHolidayList();
        }
    }

    // ====================================================================
    // List
    // ====================================================================

    @FXML
    private void onRefresh() {
        refreshHolidayList();
    }

    private void refreshHolidayList() {
        statusLabel.setText("Loading list…");
        refreshButton.setDisable(true);
        loadButton.setDisable(true);

        academicsService.loadAllHolidays(staffId,
                holidays -> Platform.runLater(() -> {
                    refreshButton.setDisable(false);
                    //if (holidays == null) holidays = Collections.emptyList();
                    UtilityClass.holidayCachingManager.setCachedHolidays(holidays);
                    populateChoice(holidays);
                }),
                () -> { /* hook — Consumer already fired */ },
                () -> Platform.runLater(() -> {
                    refreshButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    statusLabel.setText(err);
                }),
                () -> Platform.runLater(() -> {
                    refreshButton.setDisable(false);
                    statusLabel.setText("Network unavailable.");
                }));
    }

    private void populateChoice(List<Holiday> holidays) {
        if (holidays.isEmpty()) {
            holidayChoice.getItems().clear();
            statusLabel.setText("No holidays configured yet. Add one first.");
            return;
        }
        holidayChoice.getItems().setAll(holidays);
        statusLabel.setText("");
    }

    // ====================================================================
    // Load one
    // ====================================================================

    @FXML
    private void onLoad() {
        Holiday picked = holidayChoice.getValue();
        if (picked == null) return;

        loadedOriginal = picked;

        typeChoice.setValue(TYPE_BY_VALUE.get(picked.getHolidayName()));
        // Match the semester in the cache by id so the ChoiceBox sees
        // the same reference it was populated with.
        CachedSemesters matched = findSemesterById(picked.getSemesterId());
        semesterChoice.setValue(matched);

        startDatePicker.setValue(parseDate(picked.getStartDate()));
        endDatePicker.setValue(parseDate(picked.getEndDate()));

        String headerLabelText = (typeChoice.getValue() == null ? "Holiday" : typeChoice.getValue());
        headerLabel.setText("Editing: " + headerLabelText + "  (" + picked.getStartDate() + ")");

        formScroll.setVisible(true);
        formScroll.setManaged(true);
        statusLabel.setText("");
        setSaveStatus("");
    }

    // ====================================================================
    // Save
    // ====================================================================

    @FXML
    private void onSave() {
        if (loadedOriginal == null) { setSaveStatus("Load a holiday first."); return; }

        String          typeLabel = typeChoice.getValue();
        CachedSemesters semester  = semesterChoice.getValue();
        LocalDate       start     = startDatePicker.getValue();
        LocalDate       end       = endDatePicker.getValue();
        boolean         active    = activeCheck.isSelected();

        if (typeLabel == null) { setSaveStatus("Pick a holiday type.");      return; }
        if (semester == null) { setSaveStatus("Pick a semester.");          return; }
        if (start == null || end == null) { setSaveStatus("Pick both start and end dates."); return; }
        if (end.isBefore(start)) { setSaveStatus("End date cannot be before start date."); return; }

        Holiday payload = new Holiday(
                TYPE_LABELS.get(typeLabel),
                start.toString(),
                end.toString(),
                semester.getSemesterId());

        payload.setHolidayId(loadedOriginal.getHolidayId());
        payload.setSemesterName(semester.getSemesterName());
        payload.setAcademicYear(semester.getAcademicYear());
        saveButton.setDisable(true);
        setSaveStatus("Saving…");

        academicsService.updateHoliday(staffId, payload,
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    String msg = ApiContext.getInstance().successMessageContainer;
                    setSaveStatus(msg == null || msg.isBlank() ? "Saved." : msg);
                    // Replace the entry in place so the picker reflects the
                    // new label without a full refresh.
                    int idx = holidayChoice.getItems().indexOf(loadedOriginal);
                    if (idx >= 0) holidayChoice.getItems().set(idx, payload);
                    loadedOriginal = payload;
                    // Cache is now stale — invalidate so the next visit refetches.
                    UtilityClass.holidayCachingManager.clearCachedHolidays();
                    headerLabel.setText("Editing: " + typeLabel + "  (" + payload.getStartDate() + ")");
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

    private CachedSemesters findSemesterById(String id) {
        if (id == null) return null;
        for (CachedSemesters s : semesterChoice.getItems()) {
            if (id.equals(s.getSemesterId())) return s;
        }
        return null;
    }

    private static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s); }
        catch (DateTimeParseException e) { return null; }
    }

    private void setSaveStatus(String s) { saveStatusLabel.setText(s == null ? "" : s); }
}
