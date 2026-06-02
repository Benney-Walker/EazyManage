package com.bbquantum.eazymanage.Controller.Dashboard;

import com.bbquantum.eazymanage.DTO.Academics.Classes.ClassInformation;
import com.bbquantum.eazymanage.DTO.Academics.Students.StudentsRoaster;
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
import java.util.function.Consumer;

public class StaffDashboardController {

    /** Max rows shown on the preview roster table. */
    private static final int PREVIEW_LIMIT = 8;

    @FXML private Label greetingLabel;
    @FXML private Label  subtitleLabel;
    @FXML private Label  statusLabel;
    @FXML private Button refreshButton;

    @FXML private Label  classNameLabel;
    @FXML private Label  classHintLabel;
    @FXML private Label  studentCountLabel;

    @FXML private Label     rosterHintLabel;
    @FXML private Hyperlink fullRosterLink;

    @FXML private TableView<StudentsRoaster> rosterTable;
    @FXML private TableColumn<StudentsRoaster, String> studentIdCol;
    @FXML private TableColumn<StudentsRoaster, String> studentNameCol;
    @FXML private TableColumn<StudentsRoaster, String> genderCol;

    private String staffId;
    @SuppressWarnings("unused")
    private String fullName;
    @SuppressWarnings("unused")
    private String organizationName;
    /** May be null; the "View full roster" link is hidden when absent. */
    private Consumer<String> navigateToTab;

    private final ApiCore apiCore = new ApiCore();
    private final DashboardService dashboardService = new DashboardService(apiCore);
    private final StackPane toastSink = new StackPane();

    private final ObservableList<StudentsRoaster> rows = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        rosterTable.setItems(rows);
        studentIdCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentId())));

        studentNameCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentName())));

        genderCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentGender())));
    }

    public void attach(String staffId, String fullName, String organizationName,
                       Consumer<String> navigateToTab) {
        this.staffId          = staffId;
        this.fullName         = fullName;
        this.organizationName = organizationName;
        this.navigateToTab    = navigateToTab;

        greetingLabel.setText(timeOfDayGreeting()
                + (fullName == null ? "" : ", " + fullName.split(" ")[0]));
        subtitleLabel.setText("Your class today — "
                + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")));

        // Hide the navigation link entirely if the host didn't supply
        // a callback (defensive — the link wouldn't do anything anyway).
        if (navigateToTab == null) {
            fullRosterLink.setVisible(false);
            fullRosterLink.setManaged(false);
        }

        renderFromGlobals();
        onRefresh();
    }

    @FXML
    private void onRefresh() {
        refreshButton.setDisable(true);
        setStatus("Loading…");

        dashboardService.loadStaffDashboardData(staffId, toastSink,
                () -> Platform.runLater(() -> {
                    refreshButton.setDisable(false);
                    renderFromGlobals();
                    setStatus("Updated.");
                }));
    }

    @FXML
    private void onOpenFullRoster() {
        if (navigateToTab != null) navigateToTab.accept("students");
    }

    private void renderFromGlobals() {
        List<ClassInformation> grades = UtilityClass.gradeInfoCacheManager.getGradesInformation();
        if (grades == null || grades.isEmpty()) {
            classNameLabel.setText("No class assigned");
            classHintLabel.setText("Contact your administrator to be assigned a class.");
            studentCountLabel.setText("0");
            rows.clear();
            rosterHintLabel.setText("No roster to preview yet.");
            return;
        }

        // Teaching staff are typically assigned one class — take the first.
        // If multiple are ever assigned, the others are visible via the
        // Students tab.
        ClassInformation grade = grades.get(0);
        classNameLabel.setText(safe(grade.getGradeName()));
        classHintLabel.setText("Roster size: " + safe(grade.getGradeSize()) + " students");
        studentCountLabel.setText(safe(grade.getGradeSize()));

        List<StudentsRoaster> roster = grade.getStudentRoasters();
        if (roster == null || roster.isEmpty()) {
            rows.clear();
            rosterHintLabel.setText("No students enrolled in this class yet.");
            return;
        }

        // Cap the preview. Hint reflects whether we truncated.
        int shown = Math.min(PREVIEW_LIMIT, roster.size());
        rows.setAll(roster.subList(0, shown));
        if (roster.size() > PREVIEW_LIMIT) {
            rosterHintLabel.setText("Showing " + shown + " of " + roster.size()
                    + " students. Use the Students tab for the full roster.");
        } else {
            rosterHintLabel.setText("All " + roster.size() + " students.");
        }
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
