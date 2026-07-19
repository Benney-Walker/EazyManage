package com.bbquantum.eazymanage.Controller.Attendance;

import com.bbquantum.eazymanage.Constants.AttendanceStatus;
import com.bbquantum.eazymanage.DTO.Caching.CachedClasses;
import com.bbquantum.eazymanage.DTO.Attendance.StudentAttendance;
import com.bbquantum.eazymanage.DTO.Caching.CachedSemesters;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Services.AttendanceService;
import com.bbquantum.eazymanage.Utitity.RadioStatusCell;
import com.bbquantum.eazymanage.Utitity.StudentAttendanceUI;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ViewAttendanceController {

    // ---- toolbar
    @FXML private ChoiceBox<CachedClasses> classChoice;
    @FXML private ChoiceBox<CachedSemesters> semesterChoice;
    @FXML private DatePicker datePicker;
    @FXML private Button fetchButton;

    // ---- info banner
    @FXML private HBox recordInfoBanner;
    @FXML private Label recordTitleLabel;
    @FXML private Label recordSubtitleLabel;
    @FXML private Label studentsPresent;
    @FXML private Label studentsAbsent;

    // ---- table
    @FXML private TableView<StudentAttendanceUI> attendanceTable;
    @FXML private TableColumn<StudentAttendanceUI, String> studentIdCol;
    @FXML private TableColumn<StudentAttendanceUI, String> nameCol;
    @FXML private TableColumn<StudentAttendanceUI, com.bbquantum.eazymanage.Constants.AttendanceStatus> absentCol;
    @FXML private TableColumn<StudentAttendanceUI, com.bbquantum.eazymanage.Constants.AttendanceStatus> presentCol;

    // ---- action bar
    @FXML private Label       statusLabel;
    @FXML private Button      cancelButton;

    // ---- identity
    @SuppressWarnings("unused")
    private String role;
    private String staffId;

    // ---- services
    private final ApiCore apiCore = new ApiCore();
    private final AttendanceService attendanceService = new AttendanceService(apiCore);

    // ---- model
    private final ObservableList<StudentAttendanceUI> rows = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        classChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(CachedClasses g) { return g == null ? "" : g.getLevelName(); }

            @Override
            public CachedClasses fromString(String s) { return null; }
        });

        semesterChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(CachedSemesters s) {
                return s == null ? "" : s.getSemesterName() + " (" + s.getAcademicYear() + ")";
            }

            @Override
            public CachedSemesters fromString(String s) { return null; }
        });

        List<CachedClasses> cachedGrades = UtilityClass.CACHED_CLASS_MANAGER.getGradesCache();
        if (cachedGrades != null) classChoice.getItems().setAll(cachedGrades);

        List<CachedSemesters> cachedSemesters = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (cachedSemesters != null) semesterChoice.getItems().setAll(cachedSemesters);

        datePicker.setValue(LocalDate.now());

        studentIdCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentId())));

        nameCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentName())));

        absentCol.setCellValueFactory(c ->
                c.getValue().statusProperty());

        absentCol.setCellFactory(col ->
                new RadioStatusCell(AttendanceStatus.ABSENT));

        presentCol.setCellValueFactory(c ->
                c.getValue().statusProperty());

        presentCol.setCellFactory(col ->
                new RadioStatusCell(AttendanceStatus.PRESENT));

        attendanceTable.setItems(rows);
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;

    }

    // ====================================================================
    // Fetch
    // ====================================================================

    @FXML
    private void onFetch() {
        CachedClasses grade = classChoice.getValue();
        if (grade == null) { setStatus("Pick a class first."); return; }
        CachedSemesters semester = semesterChoice.getValue();
        if (semester == null) { setStatus("Pick a term first."); return; }
        LocalDate date = datePicker.getValue();
        if (date == null) { setStatus("Pick a date."); return; }

        setStatus("Loading records…");
        fetchButton.setDisable(true);

        List<StudentAttendance> holder = new ArrayList<>();

        attendanceService.loadAttendanceRecords(staffId, grade.getLevelId(), semester.getSemesterId(), date.toString(),
                holder::addAll,
                () -> Platform.runLater(() -> {
                    populateTable(holder, grade.getLevelName(), date.toString());
                    fetchButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err == null || err.isBlank()
                            ? "Could not load records."
                            : err);
                }),
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    setStatus("Network unavailable.");
                }));
    }

    private void populateTable(List<StudentAttendance> list, String gradeName, String date) {
        rows.clear();

        if (list == null || list.isEmpty()) {
            recordInfoBanner.setVisible(false);
            recordInfoBanner.setManaged(false);
            setStatus("No records found for that class/date.");
            return;
        }

        int present = 0;
        int absent = 0;
        for (StudentAttendance sa : list) {
            if (sa.getStatus() == null || sa.getStatus().isBlank()) {
                sa.setStatus(AttendanceStatus.PRESENT.name());
            }

            if (sa.getStatus().equals(AttendanceStatus.ABSENT.name())) {
                absent++;
            } else {
                present++;
            }

            StudentAttendanceUI row = new StudentAttendanceUI(sa);
            rows.add(row);
        }

        recordInfoBanner.setVisible(true);
        recordInfoBanner.setManaged(true);
        studentsAbsent.setText(String.valueOf(absent));
        studentsPresent.setText(String.valueOf(present));
        recordTitleLabel.setText(safe(gradeName));
        recordSubtitleLabel.setText(date);
        setStatus("");
    }

    // ====================================================================
    // Reset
    // ====================================================================

    @FXML
    private void onReset() {
        rows.clear();
        classChoice.getSelectionModel().clearSelection();
        datePicker.setValue(LocalDate.now());
        recordInfoBanner.setVisible(false);
        recordInfoBanner.setManaged(false);
        setStatus("");
    }

    // ====================================================================
    // Helpers
    // ====================================================================

    private void setStatus(String s) { statusLabel.setText(s == null ? "" : s); }
    private static String safe(String s) { return s == null ? "" : s; }
}
