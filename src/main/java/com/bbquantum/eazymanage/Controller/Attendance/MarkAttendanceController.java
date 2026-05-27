package com.bbquantum.eazymanage.Controller.Attendance;

import com.bbquantum.eazymanage.Constants.AttendanceStatus;
import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.DTO.Institution.StudentAttendance;
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
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

public class MarkAttendanceController {

    // ---- toolbar
    @FXML private ChoiceBox<GradeCaching> classChoice;
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
    @FXML private TableColumn<StudentAttendanceUI, com.bbquantum.eazymanage.Constants.AttendanceStatus> absentCol;
    @FXML private TableColumn<StudentAttendanceUI, com.bbquantum.eazymanage.Constants.AttendanceStatus> presentCol;

    // ---- action bar
    @FXML private VBox progressBox;
    @FXML private Label       progressLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Label       statusLabel;
    @FXML private Button      cancelButton;
    @FXML private Button      saveButton;

    // ---- identity
    @SuppressWarnings("unused")
    private String role;
    private String staffId;

    // ---- services
    private final ApiCore apiCore           = new ApiCore();
    private final AttendanceService attendanceService = new AttendanceService(apiCore);

    // ---- model
    private final ObservableList<StudentAttendanceUI> rows = FXCollections.observableArrayList();
    /** Snapshot of rows at save-time, in upload order. Holds the
     *  remaining-to-upload list so retries resume from the failed row. */
    private final List<StudentAttendance> pending = new ArrayList<>();
    private int currentUploadIndex = 0;
    private GradeCaching currentClass;

    @FXML
    private void initialize() {
        classChoice.setConverter(new StringConverter<>() {
            @Override public String toString(GradeCaching g) { return g == null ? "" : g.getLevelName(); }
            @Override public GradeCaching fromString(String s) { return null; }
        });

        // Populate the choice from the cached grade list.
        List<GradeCaching> cached = UtilityClass.gradeCacheManager.getGradesCache();
        if (cached != null) classChoice.getItems().setAll(cached);

        // Table — first two columns are plain strings; the last two render
        // mutually-exclusive radio buttons backed by the same status property.
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
        rows.addListener((javafx.collections.ListChangeListener<StudentAttendanceUI>) c ->
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
        GradeCaching grade = classChoice.getValue();
        if (grade == null) {
            setStatus("Pick a class first.");
            return;
        }
        currentClass = grade;
        setStatus("Loading students…");
        fetchButton.setDisable(true);

        // Service delivers the result through a Consumer; we stash it
        // locally and apply it in onSuccess. This avoids a global field
        // and means our state is scoped to this controller instance.
        final List<StudentAttendance>[] holder = new List[]{null};

        attendanceService.loadStudentsForAttendance(grade.getLevelId(), "no_date",
                list -> holder[0] = list,
                () -> Platform.runLater(() -> {
                    populateTable(holder[0]);
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
        pending.clear();
        currentUploadIndex = 0;

        if (list == null || list.isEmpty()) {
            classInfoBanner.setVisible(false);
            classInfoBanner.setManaged(false);
            setStatus("No students on this class.");
            return;
        }

        for (StudentAttendance sa : list) {
            // Backend's status defaults to PRESENT; if it's blank for some
            // reason we still want a sensible default rather than a NullPointerException.
            if (sa.getStatus() == null || sa.getStatus().isBlank()) {
                sa.setStatus(AttendanceStatus.PRESENT.name());
            }
            rows.add(new StudentAttendanceUI(sa));
        }

        classInfoBanner.setVisible(true);
        classInfoBanner.setManaged(true);
        classNameLabel.setText(safe(currentClass.getLevelName()));
        classMetaLabel.setText("Today");
        rowCountLabel.setText(rows.size() + " student" + (rows.size() == 1 ? "" : "s"));
        setStatus("");
    }

    // ====================================================================
    // Save (serial upload with retry-from-failed-row)
    // ====================================================================

    @FXML
    private void onSave() {
        if (rows.isEmpty()) return;

        // First-time save: snapshot the rows into `pending` for serial upload.
        // Retry path: if `pending` is non-empty and `currentUploadIndex > 0`
        // we leave it alone — the upload picks up where it failed.
        if (pending.isEmpty() || currentUploadIndex >= pending.size()) {
            pending.clear();
            for (StudentAttendanceUI row : rows) {
                pending.add(row.toStudentAttendance());
            }
            currentUploadIndex = 0;
        }

        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        fetchButton.setDisable(true);
        classChoice.setDisable(true);

        progressBox.setVisible(true);
        progressBox.setManaged(true);
        progressLabel.setText("Saving…");
        updateProgress();

        uploadNext();
    }

    private void uploadNext() {
        if (currentUploadIndex >= pending.size()) {
            onSaveComplete();
            return;
        }

        StudentAttendance record = pending.get(currentUploadIndex);

        attendanceService.markStudentAttendance(
                record.getStudentId(),
                staffId,
                record.getLevelId(),
                record.getStatus(),
                () -> Platform.runLater(() -> {
                    currentUploadIndex++;
                    updateProgress();
                    uploadNext();
                }),
                () -> Platform.runLater(() -> {
                    setStatus("Save paused — backend rejected row "
                            + (currentUploadIndex + 1) + ". "
                            + ApiContext.getInstance().errorMessageContainer
                            + " Click 'Continue' to retry.");
                    saveButton.setText("Continue");
                    saveButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setStatus("Save paused — network unavailable at row "
                            + (currentUploadIndex + 1) + ". Fix connection and click 'Continue'.");
                    saveButton.setText("Continue");
                    saveButton.setDisable(false);
                }));
    }

    private void updateProgress() {
        if (pending.isEmpty()) {
            progressBar.setProgress(0);
            return;
        }
        double frac = (double) currentUploadIndex / pending.size();
        progressBar.setProgress(frac);
        progressLabel.setText("Saved " + currentUploadIndex + " of " + pending.size());
    }

    private void onSaveComplete() {
        progressLabel.setText("All saved.");
        progressBar.setProgress(1);
        setStatus("Attendance saved.");

        // Reset for the next class
        pending.clear();
        currentUploadIndex = 0;
        saveButton.setText("Save attendance");
        saveButton.setDisable(false);
        cancelButton.setDisable(false);
        fetchButton.setDisable(false);
        classChoice.setDisable(false);
    }

    // ====================================================================
    // Reset
    // ====================================================================

    @FXML
    private void onReset() {
        rows.clear();
        pending.clear();
        currentUploadIndex = 0;
        classChoice.getSelectionModel().clearSelection();
        currentClass = null;
        classInfoBanner.setVisible(false);
        classInfoBanner.setManaged(false);
        progressBox.setVisible(false);
        progressBox.setManaged(false);
        saveButton.setText("Save attendance");
        saveButton.setDisable(true);
        setStatus("");
    }

    // ====================================================================
    // Helpers
    // ====================================================================

    private void setStatus(String s) { statusLabel.setText(s == null ? "" : s); }
    private static String safe(String s) { return s == null ? "" : s; }
}
