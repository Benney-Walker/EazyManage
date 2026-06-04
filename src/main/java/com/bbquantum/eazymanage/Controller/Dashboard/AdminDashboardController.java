package com.bbquantum.eazymanage.Controller.Dashboard;

import com.bbquantum.eazymanage.DTO.Institution.LogsDetails;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Services.DashboardService;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminDashboardController {

    @FXML private Label greetingLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label statusLabel;
    @FXML private Button refreshButton;

    @FXML private Label  classesLabel;
    @FXML private Label  semestersLabel;
    @FXML private Label  staffLabel;

    @FXML private TableView<LogsDetails> activitiesTable;
    @FXML private TableColumn<LogsDetails, String> dateCol;
    @FXML private TableColumn<LogsDetails, String> timeCol;
    @FXML private TableColumn<LogsDetails, String> actorCol;
    @FXML private TableColumn<LogsDetails, String> typeCol;
    @FXML private TableColumn<LogsDetails, String> actionCol;
    @FXML private TableColumn<LogsDetails, String> statusCol;

    private String staffId;
    private String fullName;
    @SuppressWarnings("unused")
    private String organizationName;

    private final ApiCore apiCore = new ApiCore();
    private final DashboardService dashboardService = new DashboardService(apiCore);
    private final StackPane toastSink = new StackPane();

    private final ObservableList<LogsDetails> rows = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        activitiesTable.setItems(rows);
        dateCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getDate())));

        timeCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getTime())));

        actorCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getCreatedBy())));

        typeCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getType())));

        actionCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getMessage())));

        statusCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStatus())));
    }

    public void attach(String staffId, String fullName, String organizationName) {
        this.staffId          = staffId;
        this.fullName         = fullName;
        this.organizationName = organizationName;

        greetingLabel.setText(timeOfDayGreeting()
                + (fullName == null ? "" : ", " + fullName.split(" ")[0]));
        subtitleLabel.setText("System overview — "
                + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")));

        renderFromGlobals();
        onRefresh();
    }

    @FXML
    private void onRefresh() {
        refreshButton.setDisable(true);
        setStatus("Loading…");

        dashboardService.loadAdminDashboard(staffId, toastSink,
                () -> Platform.runLater(() -> {
                    refreshButton.setDisable(false);
                    renderFromGlobals();
                    setStatus("Updated.");
                }));
    }

    private void renderFromGlobals() {
        int classes  = UtilityClass.gradeCacheManager.getGradesCache() == null
                ? 0 : UtilityClass.gradeCacheManager.getGradesCache().size();

        int semesters = UtilityClass.semesterCachingManager.getCachedSemesters() == null
                ? 0 : UtilityClass.semesterCachingManager.getCachedSemesters().size();

        int staff     = UtilityClass.staffCacheManager.getCachedStaffList() == null
                ? 0 : UtilityClass.staffCacheManager.getCachedStaffList().size();

        classesLabel.setText(String.valueOf(classes));
        semestersLabel.setText(String.valueOf(semesters));
        staffLabel.setText(String.valueOf(staff));

        List<LogsDetails> logs = UtilityClass.systemLogsCacheManager.getCachedSystemLogs();
        if (logs != null) rows.setAll(logs);
        else              rows.clear();
    }

    private void setStatus(String s) { statusLabel.setText(s == null ? "" : s); }

    private static String timeOfDayGreeting() {
        int hour = LocalTime.now().getHour();
        if (hour < 12) return "Good morning";
        if (hour < 17) return "Good afternoon";
        return "Good evening";
    }
    private static String safe(String s) { return s == null ? "" : s; }
}
