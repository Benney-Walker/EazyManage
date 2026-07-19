package com.bbquantum.eazymanage.Controller.Attendance;

import com.bbquantum.eazymanage.Constants.AttendanceStatus;
import com.bbquantum.eazymanage.DTO.Caching.CachedClasses;
import com.bbquantum.eazymanage.DTO.Attendance.StudentAttendance;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Services.AttendanceService;
import com.bbquantum.eazymanage.Utitity.RadioStatusCell;
import com.bbquantum.eazymanage.Utitity.StudentAttendanceUI;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MarkAttendanceController {

    // ---- toolbar
    @FXML private ChoiceBox<CachedClasses> classChoice;
    @FXML private DatePicker datePicker;
    @FXML private Button fetchButton;

    // ---- class info banner
    @FXML private HBox classInfoBanner;
    @FXML private Label classNameLabel;
    @FXML private Label classMetaLabel;
    @FXML private Label rowCountLabel;

    // ---- table
    @FXML private TableView<StudentAttendanceUI> attendanceTable;
    @FXML private TableColumn<StudentAttendanceUI, String> studentIdCol;
    @FXML private TableColumn<StudentAttendanceUI, String> nameCol;
    @FXML private TableColumn<StudentAttendanceUI, AttendanceStatus> absentCol;
    @FXML private TableColumn<StudentAttendanceUI, AttendanceStatus> presentCol;

    // ---- action bar
    @FXML private Button markPresentButton;
    @FXML private Label statusLabel;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    // ---- identity
    @SuppressWarnings("unused")
    private String role;
    private String staffId;

    // ---- services
    private final ApiCore apiCore = new ApiCore();
    private final AttendanceService attendanceService = new AttendanceService(apiCore);

    // ---- model
    private final ObservableList<StudentAttendanceUI> rows = FXCollections.observableArrayList();

    private CachedClasses currentClass;
    private LocalDate currentDate;

    @FXML
    private void initialize() {
        classChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(CachedClasses g) { return g == null ? "" : g.getLevelName(); }

            @Override
            public CachedClasses fromString(String s) { return null; }
        });

        // Populate the choice from the cached grade list.
        List<CachedClasses> cached = UtilityClass.CACHED_CLASS_MANAGER.getGradesCache();
        if (cached != null) classChoice.getItems().setAll(cached);

        datePicker.setValue(LocalDate.now());

        studentIdCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentId())));
        nameCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentName())));
        absentCol.setCellValueFactory(c
                -> c.getValue().statusProperty());
        absentCol.setCellFactory(col ->
                new RadioStatusCell(AttendanceStatus.ABSENT));
        presentCol.setCellValueFactory(c -> c.getValue().statusProperty());
        presentCol.setCellFactory(col -> new RadioStatusCell(AttendanceStatus.PRESENT));

        attendanceTable.setItems(rows);

        // Save button stays disabled until rows exist.
        rows.addListener((ListChangeListener<StudentAttendanceUI>) c ->
                saveButton.setDisable(rows.isEmpty()));
    }

    /** Inject identity from the host controller. */
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
        if (grade == null) {
            setStatus("Pick a class first.");
            return;
        }

        LocalDate date = datePicker.getValue();
        if (date == null) {
            setStatus("Pick a date.");
            return;
        }

        currentClass = grade;
        currentDate = date;

        setStatus("Loading students…");
        fetchButton.setDisable(true);

        List<StudentAttendance> holder = new ArrayList<>();

        attendanceService.loadStudentsForAttendance(staffId, grade.getLevelId(), currentDate.toString(),
                holder::addAll,
                () -> Platform.runLater(() -> {
                    populateTable(holder);
                    fetchButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err == null || err.isBlank()
                            ? "Could not load students."
                            : err);
                }),
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    setStatus("Network unavailable.");
                }));
    }

    private void populateTable(List<StudentAttendance> list) {
        rows.clear();

        if (list == null || list.isEmpty()) {
            classInfoBanner.setVisible(false);
            classInfoBanner.setManaged(false);
            setStatus("No students on this class.");
            return;
        }

        for (StudentAttendance sa : list) {
            if (sa.getStatus() == null || sa.getStatus().isBlank()) {
                sa.setStatus(AttendanceStatus.ABSENT.name());
            }
            rows.add(new StudentAttendanceUI(sa));
        }

        classInfoBanner.setVisible(true);
        classInfoBanner.setManaged(true);
        classNameLabel.setText(safe(currentClass.getLevelName()));
        classMetaLabel.setText(currentDate.getDayOfWeek().name());
        rowCountLabel.setText(rows.size() + " student" + (rows.size() == 1 ? "" : "s"));
        markPresentButton.setDisable(false);
        cancelButton.setDisable(false);
        saveButton.setDisable(false);
        setStatus("");
    }

    @FXML
    private void onMarkAllPresent() {
        if (!rows.isEmpty()) {
            for (StudentAttendanceUI sa : rows) {
                sa.statusProperty().set(AttendanceStatus.PRESENT);
            }
        }
    }

    @FXML
    private void onSave() {
        if (rows.isEmpty() || currentDate == null || currentClass == null) return;

        List<StudentAttendance> holder = new ArrayList<>();
        for (StudentAttendanceUI row : rows) {
            holder.add(row.toStudentAttendance());
        }

        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        fetchButton.setDisable(true);
        classChoice.setDisable(true);
        markPresentButton.setDisable(true);
        setStatus("Saving....");

        attendanceService.markStudentAttendance(staffId, currentClass.getLevelId(), currentDate.toString(),
                holder,
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    cancelButton.setDisable(false);
                    fetchButton.setDisable(false);
                    classChoice.setDisable(false);
                    markPresentButton.setDisable(false);

                    classInfoBanner.setVisible(false);
                    classInfoBanner.setManaged(false);
                    setStatus(ApiContext.getInstance().successMessageContainer);
                    rows.clear();
                }),
                () -> Platform.runLater(() -> {
                    setStatus(ApiContext.getInstance().errorMessageContainer);
                    saveButton.setDisable(false);
                    cancelButton.setDisable(false);
                    fetchButton.setDisable(false);
                    classChoice.setDisable(false);
                    markPresentButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {

                    saveButton.setDisable(false);
                    cancelButton.setDisable(false);
                    fetchButton.setDisable(false);
                    classChoice.setDisable(false);
                    markPresentButton.setDisable(false);
                    setStatus("Network unavailable.");
                }));
    }

    // ====================================================================
    // Reset
    // ====================================================================

    @FXML
    private void onReset() {
        rows.clear();
        classChoice.getSelectionModel().clearSelection();
        currentClass = null;
        classInfoBanner.setVisible(false);
        classInfoBanner.setManaged(false);
        saveButton.setDisable(true);
        setStatus("");
    }

    // ====================================================================
    // Helpers
    // ====================================================================

    private void setStatus(String s) { statusLabel.setText(s == null ? "" : s); }
    private static String safe(String s) { return s == null ? "" : s; }
}
