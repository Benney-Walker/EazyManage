package com.bbquantum.eazymanage.Controller.Dashboard;

import com.bbquantum.eazymanage.DTO.Attendance.AbsenteesView;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Services.DashboardService;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PrincipalDashboardController {

    @FXML private Label greetingLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label  statusLabel;
    @FXML private Button refreshButton;

    @FXML private Label totalStudentsLabel;
    @FXML private Label totalStaffLabel;
    @FXML private Label totalTeachingStaffLabel;
    @FXML private Label semesterFeesLabel;
    @FXML private Label feesPaidLabel;
    @FXML private Label feesPaidHintLabel;
    @FXML private Label feesPercentLabel;
    @FXML private ProgressBar feesProgressBar;

    @FXML private TableView<AbsenteesView> absenteesTable;
    @FXML private TableColumn<AbsenteesView, String> studentIdCol;
    @FXML private TableColumn<AbsenteesView, String> studentNameCol;
    @FXML private TableColumn<AbsenteesView, String> studentLevelCol;
    @FXML private TableColumn<AbsenteesView, String> instructorNameCol;

    private String staffId;
    private String fullName;
    @SuppressWarnings("unused")
    private String organizationName;

    private final ApiCore apiCore = new ApiCore();
    private final DashboardService dashboardService = new DashboardService(apiCore);
    /** Off-scene pane satisfies the legacy DashboardService signature
     *  without ever rendering toast notifications. */
    private final StackPane toastSink = new StackPane();

    private final ObservableList<AbsenteesView> rows = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        absenteesTable.setItems(rows);
        studentIdCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentId())));

        studentNameCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentName())));

        studentLevelCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentGrade())));

        instructorNameCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getInstructorName())));
    }

    public void attach(String staffId, String fullName, String organizationName) {
        this.staffId          = staffId;
        this.fullName         = fullName;
        this.organizationName = organizationName;

        greetingLabel.setText(timeOfDayGreeting() + (fullName == null ? "" : ", " + fullName.split(" ")[0]));
        subtitleLabel.setText("Here's your school at a glance — "
                + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")));

        // Render once from any pre-cached values so the screen isn't empty
        // during the first load; then trigger a refresh to refill.
        renderFromGlobals();
        onRefresh();
    }

    @FXML
    private void onRefresh() {
        refreshButton.setDisable(true);
        setStatus("Loading…");

        UtilityClass.clearSessionForRefresh();
        dashboardService.loadPrincipalData(staffId, toastSink,
                () -> Platform.runLater(() -> {
                    refreshButton.setDisable(false);
                    renderFromGlobals();
                    setStatus("Updated.");
                }));
    }

    /**
     * Refresh visible fields from the {@code UtilityClass} globals the
     * legacy controllers also read. Safe to call multiple times; idempotent.
     */
    private void renderFromGlobals() {
        long totalStudents = UtilityClass.totalStudentsFromBackend == null ? 0L : UtilityClass.totalStudentsFromBackend;

        long totalStaff    = UtilityClass.totalStaffFromBackend == null ? 0L : UtilityClass.totalStaffFromBackend;

        long totalTeachingStaff = UtilityClass.totalTeachingStaff == null ? 0L : UtilityClass.totalTeachingStaff;

        double totalFees   = UtilityClass.totalSemesterFeesFromBackend == null ? 0d : UtilityClass.totalSemesterFeesFromBackend;

        double feesPaid    = UtilityClass.feesPaidFromBackend         == null ? 0d : UtilityClass.feesPaidFromBackend;

        totalStudentsLabel.setText(String.valueOf(totalStudents));
        totalStaffLabel.setText(String.valueOf(totalStaff));
        totalTeachingStaffLabel.setText(totalTeachingStaff + " :");
        semesterFeesLabel.setText("GH₵ " + String.format("%.2f", totalFees));
        feesPaidLabel.setText("GH₵ " + String.format("%.2f", feesPaid));

        double progress;
        if (totalFees > 0) progress = Math.min(1.0, feesPaid / totalFees);
        else               progress = 0.0;
        feesProgressBar.setProgress(progress);
        feesPercentLabel.setText(String.format("%.1f", progress * 100) + "%");
        feesPaidHintLabel.setText(String.format("%.1f", progress * 100) + "% of expected revenue");

        List<AbsenteesView> absentees = UtilityClass.cacheManager.getCachedAbsenteeStudents();
        if (absentees != null) rows.setAll(absentees);
        else                   rows.clear();
    }

    private void setStatus(String s) { statusLabel.setText(s == null ? "" : s); }

    private static String timeOfDayGreeting() {
        int hour = java.time.LocalTime.now().getHour();
        if (hour < 12) return "Good morning";
        if (hour < 17) return "Good afternoon";
        return "Good evening";
    }
    private static String safe(String s) { return s == null ? "" : s; }
}
