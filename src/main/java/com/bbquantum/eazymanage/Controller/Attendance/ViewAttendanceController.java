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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewAttendanceController {

    // ---- toolbar
    @FXML private ChoiceBox<GradeCaching> classChoice;
    @FXML private DatePicker datePicker;
    @FXML private Button fetchButton;

    // ---- info banner
    @FXML private HBox recordInfoBanner;
    @FXML private Label recordTitleLabel;
    @FXML private Label recordSubtitleLabel;

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

    // ---- identity
    @SuppressWarnings("unused")
    private String role;
    private String staffId;

    // ---- services
    private final ApiCore apiCore = new ApiCore();
    private final AttendanceService attendanceService = new AttendanceService(apiCore);

    // ---- model
    private final ObservableList<StudentAttendanceUI> rows = FXCollections.observableArrayList();
    /** Map studentId → status string as it came from the backend. Used for
     *  dirty-row detection at save time. */
    private final Map<String, String> originalStatus = new HashMap<>();
    /** Filled at save time with only the rows whose status changed. */
    private final List<StudentAttendance> pendingUpdates = new ArrayList<>();
    private int currentUploadIndex = 0;
    private GradeCaching currentClass;
    private LocalDate currentDate;

    @FXML
    private void initialize() {
        classChoice.setConverter(new StringConverter<>() {
            @Override public String toString(GradeCaching g) { return g == null ? "" : g.getLevelName(); }
            @Override public GradeCaching fromString(String s) { return null; }
        });

        List<GradeCaching> cached = UtilityClass.gradeCacheManager.getGradesCache();
        if (cached != null) classChoice.getItems().setAll(cached);

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

        // Recompute dirty count whenever any row's status changes.
        // We attach the listener on each row when it's added (see populateTable).
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
        GradeCaching grade = classChoice.getValue();
        if (grade == null) { setStatus("Pick a class first."); return; }
        LocalDate date = datePicker.getValue();
        if (date == null) { setStatus("Pick a date."); return; }

        currentClass = grade;
        currentDate  = date;

        setStatus("Loading records…");
        fetchButton.setDisable(true);

        final List<StudentAttendance>[] holder = new List[]{null};

        attendanceService.loadStudentsForAttendance(staffId, grade.getLevelId(), date.toString(),
                list -> holder[0] = list,
                () -> Platform.runLater(() -> {
                    populateTable(holder[0]);
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

    private void populateTable(List<StudentAttendance> list) {
        rows.clear();
        originalStatus.clear();
        pendingUpdates.clear();
        currentUploadIndex = 0;

        if (list == null || list.isEmpty()) {
            recordInfoBanner.setVisible(false);
            recordInfoBanner.setManaged(false);
            setStatus("No records found for that class/date.");
            return;
        }

        for (StudentAttendance sa : list) {
            if (sa.getStatus() == null || sa.getStatus().isBlank()) {
                sa.setStatus(AttendanceStatus.PRESENT.name());
            }
            StudentAttendanceUI row = new StudentAttendanceUI(sa);
            rows.add(row);
            originalStatus.put(sa.getStudentId(), sa.getStatus());

            // Recompute dirty count whenever this row's status changes.
            row.statusProperty().addListener((obs, oldV, newV) -> refreshDirtyCount());
        }

        recordInfoBanner.setVisible(true);
        recordInfoBanner.setManaged(true);
        recordTitleLabel.setText(safe(currentClass.getLevelName()));
        recordSubtitleLabel.setText(currentDate.toString());
        refreshDirtyCount();
        setStatus("");
    }

    private void refreshDirtyCount() {
        int dirty = 0;
        for (StudentAttendanceUI row : rows) {
            String orig = originalStatus.get(row.getStudentId());
            String now  = row.getStatus() == null ? "" : row.getStatus().name();
            if (orig != null && !orig.equals(now)) dirty++;
        }
    }

    // ====================================================================
    // Reset
    // ====================================================================

    @FXML
    private void onReset() {
        rows.clear();
        originalStatus.clear();
        pendingUpdates.clear();
        currentUploadIndex = 0;
        classChoice.getSelectionModel().clearSelection();
        datePicker.setValue(LocalDate.now());
        currentClass = null;
        currentDate  = null;
        recordInfoBanner.setVisible(false);
        recordInfoBanner.setManaged(false);
        progressBox.setVisible(false);
        progressBox.setManaged(false);
        setStatus("");
    }

    // ====================================================================
    // Helpers
    // ====================================================================

    private void setStatus(String s) { statusLabel.setText(s == null ? "" : s); }
    private static String safe(String s) { return s == null ? "" : s; }
}
