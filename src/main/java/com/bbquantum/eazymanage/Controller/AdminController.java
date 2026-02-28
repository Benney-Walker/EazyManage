package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.Constants.Gender;
import com.bbquantum.eazymanage.Constants.StaffRoles;
import com.bbquantum.eazymanage.Constants.StaffStatus;
import com.bbquantum.eazymanage.Constants.StudentStatus;
import com.bbquantum.eazymanage.DTO.Academics.StudentsScoresTable;
import com.bbquantum.eazymanage.DTO.Academics.SubjectsHolder;
import com.bbquantum.eazymanage.DTO.Academics.ViewClassSemesterReport;
import com.bbquantum.eazymanage.DTO.Academics.ViewStudentsSubjectsResults;
import com.bbquantum.eazymanage.DTO.Institution.*;
import com.bbquantum.eazymanage.Utitity.ApiUtilityClass;
import com.bbquantum.eazymanage.Utitity.UtilityMethods;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;


public class AdminController implements Initializable {

    //CONTROLLER OWN VARIABLES
    private ApiUtilityClass apiUtilityClass;
    private String institutionName;
    public String staffId;
    private String staffFullName;

    @FXML public StackPane notification;

    // NAV SIDE
    @FXML public Label versionLabel;

    @FXML private Button dashboardBtn;
    @FXML private Button studentNavBtn;
    @FXML private Button scoresNavBtn;
    @FXML private Button subjectNavBtn;
    @FXML private Button reportsNavBtn;
    @FXML private Button staffNavButton;
    @FXML private Button logsNavBtn;

    @FXML private Label adminNameLabel;
    @FXML private Label institutionalNameLabel;
    @FXML private Button logoutButton;

    //MAIN CONTENT
    @FXML private StackPane contentArea;

    //DASHBOARD VIEW
    @FXML private ScrollPane dashboardView;
    @FXML private Label dateTimeLabel;
    @FXML private Label activeSessionsLabel;
    @FXML private Label notificationsLabel;
    @FXML private TableView<LogsDetails> recentActivitiesTable;
    @FXML private TableColumn<LogsDetails, String> activityIdCol;
    @FXML private TableColumn<LogsDetails, String> activityTypeCol;
    @FXML private TableColumn<LogsDetails, String> createdByCol;
    @FXML private TableColumn<LogsDetails, String> activityDateCol;
    @FXML private TableColumn<LogsDetails, String> activityTimeCol;
    @FXML private TableColumn<LogsDetails, String> actionDataCol;
    @FXML private TableColumn<LogsDetails, String> activityStatusCol;

    //STUDENTS PAGE
    @FXML private ScrollPane manageStudentView;

    @FXML private Button addStudentToggleBtn;
    @FXML private Button updateStudentToggleBtn;

    @FXML private StackPane studentInfoCardContainer;

    @FXML private VBox addStudentCard;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private TextField homeTownField;
    @FXML private TextField parentNameField;
    @FXML private TextField parentPhoneField;
    @FXML private ComboBox<GradeCaching> classComboBox;
    @FXML private Button saveAndClearBtn;

    @FXML private VBox updateStudentInfoCard;
    @FXML private VBox studentSearchFieldBox;
    @FXML private ComboBox<StudentsHolder> student_SelectionBox;
    @FXML private ComboBox<StudentsHolder> student_classSelectionBox;
    @FXML private Button findStudentBtn;
    @FXML private VBox personalInfo;
    @FXML private Label studentIdLabel;
    @FXML private Label studentStatusLabel;
    @FXML private Label updateGradeNameLabel;
    @FXML private TextField updateFirstNameField;
    @FXML private TextField updateLastNameField;
    @FXML private ComboBox<String> updateGenderComboBox;
    @FXML private DatePicker updateDateOfBirthPicker;
    @FXML private TextField updateHomeTownField;
    @FXML private VBox guardianInfo;
    @FXML private TextField updateParentNameField;
    @FXML private TextField updateParentPhoneField;
    @FXML private VBox gradeInfo;
    @FXML private ComboBox<GradeCaching> updateClassComboBox;
    @FXML private VBox statusInfo;
    @FXML private ComboBox<StudentStatus> updateStatusComboBox;
    @FXML private HBox updateButtonVBox;
    @FXML private Button updateStudentInfoBtn;

    //SUBJECT SCORES VIEW
    @FXML private ScrollPane subjectScoresView;

    @FXML private VBox subjectResultSearchBox;
    @FXML private ComboBox<SubjectsHolder> score_selectSubjectBox;
    @FXML private ComboBox<GradeCaching> score_SelectClassBox;
    @FXML private Button score_LoadStudentsBtn;

    @FXML private HBox subjectInfoBox;
    @FXML private Label subjectNameLabel;
    @FXML private Label subjectCodeLabel;

    @FXML private VBox noStudentsBox;

    @FXML private VBox resultsTableContainer;
    @FXML public TableView<StudentsScoresTable> studentsScoresTable;
    @FXML private TableColumn<StudentsScoresTable, String> studentIdColumn;
    @FXML private TableColumn<StudentsScoresTable, String> studentNameColumn;
    @FXML private TableColumn<StudentsScoresTable, String> exercise1Column;
    @FXML private TableColumn<StudentsScoresTable, String> classTestColumn;
    @FXML private TableColumn<StudentsScoresTable, String> exercise2Column;
    @FXML private TableColumn<StudentsScoresTable, String> projectColumn;
    @FXML private TableColumn<StudentsScoresTable, String> classScoreColumn;
    @FXML private TableColumn<StudentsScoresTable, String> examScoreColumn;
    @FXML private TableColumn<StudentsScoresTable, String> calExamScoreColumn;
    @FXML private Button saveScores;

    @FXML private VBox saveScoresSemesterVBox;
    @FXML private ComboBox<SemesterCaching> saveScoresSemesterComboBox;

    //SUBJECT PAGE
    @FXML private ScrollPane manageSubjectView;

    @FXML private Button addSubjectToggleBtn;
    @FXML private Button updateSubjectToggleBtn;

    @FXML private StackPane subjectCardContainer;

    @FXML private VBox addNewSubjectCard;
    @FXML private TextField subjectNameField;
    @FXML private ComboBox<GradeCaching> gradeComboBox;
    @FXML private Button saveSubjectBtn;

    @FXML private VBox updateSubjectsCard;
    @FXML private VBox subjectSearchBox;
    @FXML private ComboBox<SubjectsHolder> subjectSelectionBox;
    @FXML private ComboBox<GradeCaching> subject_gradeSelectionBox;
    @FXML public Button fetchSubjectBtn;

    @FXML private VBox subjectSearchResponsePanel;
    @FXML private Label subjectIdLabel;
    @FXML private TextField subject_updateNameField;
    @FXML private ComboBox<GradeCaching> subject_updateGradeBox;
    @FXML private Button updateSubjectBtn;
    @FXML private Button deleteSubjectBtn;

    //MANAGE REPORT VIEW
    @FXML private ScrollPane manageReportsView;
    @FXML private VBox viewResultsContainer;
    @FXML private ComboBox<GradeCaching> results_GradeSelectionBox;
    @FXML private ComboBox<SemesterCaching> results_SemesterSelectionBox;
    @FXML private Button loadClassResultsBtn;

    @FXML private VBox resultsDisplayPanel;
    @FXML private Label gradeNameLabel;
    @FXML private Label semesterNameLabel;
    @FXML private TableView<ViewClassSemesterReport> classResultsTable;
    @FXML private TableColumn<ViewClassSemesterReport, String> studentResultIdColumn;
    @FXML private TableColumn<ViewClassSemesterReport, String> studentResultNameColumn;
    @FXML private TableColumn<ViewClassSemesterReport, String> classNameColumn;
    @FXML private TableColumn<ViewClassSemesterReport, String> semesterNameColumn;
    @FXML private TableColumn<ViewClassSemesterReport, String> totalScoreColumn;
    @FXML private TableColumn<ViewClassSemesterReport, String> averageScoreColumn;
    @FXML private TableColumn<ViewClassSemesterReport, String> positionColumn;

    @FXML private VBox resultsDetailsBox;
    @FXML private TableView<ViewStudentsSubjectsResults> subjectBreakdownTable;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> subjectNameCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> classScoreCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> exercise1Col;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> classTestCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> exercise2Col;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> projectScoreCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> examScoreCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> calculatedExamScoreCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> totalCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> gradeCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> remarkCol;

    @FXML private Button generateReportBtn;

    //MANAGE STAFF VIEW
    @FXML private ScrollPane manageStaffView;
    @FXML private VBox staffListTableContainer;
    @FXML public TableView<ViewStaffList> staffListTable;
    @FXML private TableColumn<ViewStaffList, String> staffIdColumn;
    @FXML private TableColumn<ViewStaffList, String> staffNameColumn;
    @FXML private TableColumn<ViewStaffList, String> staffRoleColumn;
    @FXML private TableColumn<ViewStaffList, String> staffGradeColumn;

    @FXML private VBox staffDetailsPanel;
    @FXML private Label staffFullNameLabel;
    @FXML private Label staffIdLabel;
    @FXML private TextField editFirstNameField;
    @FXML private TextField editLastNameField;
    @FXML private TextField editEmailField;
    @FXML private TextField editPhoneField;
    @FXML private DatePicker editDateOfBirthPicker;
    @FXML private ComboBox<Gender> editGenderComboBox;
    @FXML private ComboBox<StaffRoles> editRoleComboBox;
    @FXML private ComboBox<StaffStatus> editStatusComboBox;

    @FXML private Button resetPasswordBtn;
    @FXML private Button updateStaffInfoBtn;

    //SYSTEM LOGS
    @FXML private ScrollPane systemLogsView;
    @FXML private DatePicker filterDatePicker;
    @FXML private ComboBox<StaffCaching> filterStaffBox;

    @FXML private VBox activitiesListPanel;
    @FXML private TableView<LogsDetails> activitiesTable;
    @FXML private TableColumn<LogsDetails, String> filter_activitiesIdCol;
    @FXML private TableColumn<LogsDetails, String> filter_activityTypeCol;
    @FXML private TableColumn<LogsDetails, String> filter_createdByCol;
    @FXML private TableColumn<LogsDetails, String> filter_activityDateCol;
    @FXML private TableColumn<LogsDetails, String> filter_activityTimeCol;
    @FXML private TableColumn<LogsDetails, String> filter_actionDataCol;
    @FXML private TableColumn<LogsDetails, String> filter_activityStatusCol;

    @FXML private VBox viewLogDetailsPanel;
    @FXML private Label activityIdField;
    @FXML private Label activityDateField;
    @FXML private Label activityTimeField;
    @FXML private Label activityTypeField;
    @FXML private Label activityOwnerField;
    @FXML private Label activityStatusField;
    @FXML private TextArea activityDataField;

    //LOADING OVERLAY
    @FXML private VBox loadingOverlay;
    @FXML private ProgressIndicator loadingSpinner;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //DASHBOARD ACTIVITY TABLE
        recentActivitiesTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        activityIdCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getActionId()));

        activityTypeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getActionType()));

        createdByCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCreatedBy()));

        activityDateCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getActionDate()));

        activityTimeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getActionTime()));

        actionDataCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getActionData()));

        activityStatusCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getActionStatus()));


        //STUDENT SCORES TABLE
        studentIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentId()));

        studentNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentName()));

        classScoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClassScore()));

        calExamScoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCalculatedExamScore()));

        //Editable columns
        exercise1Column.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getExercise1Score()));

        classTestColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClassTestScore()));

        exercise2Column.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getExercise2Score()));

        projectColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProjectScore()));

        examScoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getExamScore()));

        //Uneditable columns
        studentIdColumn.setEditable(false);
        studentNameColumn.setEditable(false);
        classScoreColumn.setEditable(false);
        calExamScoreColumn.setEditable(false);

        exercise1Column.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        classTestColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        exercise2Column.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        projectColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        examScoreColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));

        exercise1Column.setEditable(true);
        classTestColumn.setEditable(true);
        exercise2Column.setEditable(true);
        projectColumn.setEditable(true);
        examScoreColumn.setEditable(true);
        studentsScoresTable.setEditable(true);
        studentsScoresTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        exercise1Column.setOnEditCommit(event -> {
            if (!event.getNewValue().matches("\\d+(\\.\\d+)?")) {
                UtilityMethods.showError(notification, "Only numbers allowed");
                studentsScoresTable.refresh();
                return;
            }

            if (Double.parseDouble(event.getNewValue()) > 20) {
                UtilityMethods.showError(notification, "Exercise 1 shouldn't be more than 20");
                studentsScoresTable.refresh();
                return;
            }

            StudentsScoresTable row = event.getRowValue();
            row.setExercise1Score(event.getNewValue());
            recalculateClassScore(row);
            studentsScoresTable.refresh();
        });

        classTestColumn.setOnEditCommit(event -> {
            if (!event.getNewValue().matches("\\d+(\\.\\d+)?")) {
                UtilityMethods.showError(notification, "Only numbers allowed");
                studentsScoresTable.refresh();
                return;
            }

            if (Double.parseDouble(event.getNewValue()) > 30) {
                UtilityMethods.showError(notification, "Class Test shouldn't be more than 30");
                studentsScoresTable.refresh();
                return;
            }

            StudentsScoresTable row = event.getRowValue();
            row.setClassTestScore(event.getNewValue());
            recalculateClassScore(row);
            studentsScoresTable.refresh();
        });

        exercise2Column.setOnEditCommit(event -> {
            if (!event.getNewValue().matches("\\d+(\\.\\d+)?")) {
                UtilityMethods.showError(notification, "Only numbers allowed");
                studentsScoresTable.refresh();
                return;
            }

            if (Double.parseDouble(event.getNewValue()) > 20) {
                UtilityMethods.showError(notification, "Exercise 2 shouldn't be more than 20");
                studentsScoresTable.refresh();
                return;
            }

            StudentsScoresTable row = event.getRowValue();
            row.setExercise2Score(event.getNewValue());
            recalculateClassScore(row);
            studentsScoresTable.refresh();
        });

        projectColumn.setOnEditCommit(event -> {
            if (!event.getNewValue().matches("\\d+(\\.\\d+)?")) {
                UtilityMethods.showError(notification, "Only numbers allowed");
                studentsScoresTable.refresh();
                return;
            }

            if (Double.parseDouble(event.getNewValue()) > 30) {
                UtilityMethods.showError(notification, "Project score shouldn't be more than 30");
                studentsScoresTable.refresh();
                return;
            }

            StudentsScoresTable row = event.getRowValue();
            row.setProjectScore(event.getNewValue());
            recalculateClassScore(row);
            studentsScoresTable.refresh();
        });

        examScoreColumn.setOnEditCommit(event -> {
            if (!event.getNewValue().matches("\\d+(\\.\\d+)?")) {
                UtilityMethods.showError(notification, "Only numbers allowed");
                studentsScoresTable.refresh();
                return;
            }

            StudentsScoresTable row = event.getRowValue();
            row.setExamScore(event.getNewValue());
            reCalculateExamScore(row);
            studentsScoresTable.refresh();
        });


        //CLASS RESULTS TABLE
        studentResultIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentId()));

        studentResultNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentName()));

        classNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClassName()));

        semesterNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSemesterName()));

        totalScoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTotal()));

        averageScoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAverage()));

        positionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClassPosition()));



        subjectNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSubjectName()));

        exercise1Col.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getExercise1Score()));

        classTestCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClassTestScore()));

        exercise2Col.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getExercise2Score()));

        projectScoreCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProjectScore()));

        classScoreCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClassScore()));

        examScoreCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getExamScore()));

        calculatedExamScoreCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCalculatedExamScore()));

        totalCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTotal()));

        gradeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getGrade()));

        remarkCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription()));



        classResultsTable.setRowFactory(tableView -> {
            TableRow<ViewClassSemesterReport> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    ViewClassSemesterReport report = row.getItem();
                    List<ViewStudentsSubjectsResults> clicked = report.getSubjects();

                    if (resultsDetailsBox.isVisible() && subjectBreakdownTable.getUserData() == clicked) {
                        resultsDetailsBox.setVisible(false);
                        resultsDetailsBox.setManaged(false);

                        subjectBreakdownTable.getItems().clear();
                        subjectBreakdownTable.setUserData(null);
                    } else {
                        subjectBreakdownTable.setItems(
                                FXCollections.observableArrayList(clicked)
                        );
                        subjectBreakdownTable.setUserData(clicked);

                        resultsDetailsBox.setVisible(true);
                        resultsDetailsBox.setManaged(true);
                    }
                }
            });
            return row;
        });


        //STAFF LIST TABLE
        staffIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStaffId()));

        staffNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStaffName()));

        staffRoleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStaffRole()));

        staffGradeColumn.setCellValueFactory(cellData -> {
            List<String> levels = cellData.getValue().getAssignedLevel();

            if(levels == null || levels.isEmpty()) {
                return new SimpleStringProperty("");
            }

            String joinLevels = String.join(", ", levels);
            return new SimpleStringProperty(joinLevels);
        });

        staffListTable.setRowFactory(tableView -> {
            TableRow<ViewStaffList> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    ViewStaffList staff = row.getItem();
                    getStaffDetails(staff.getStaffId());
                }
            });

            return row;
        });


        //SYSTEM LOGS TABLE
        filter_activitiesIdCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getActionId()));

        filter_activityTypeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getActionType()));

        filter_createdByCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCreatedBy()));

        filter_activityDateCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getActionDate()));

        filter_activityTimeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getActionTime()));

        filter_actionDataCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getActionData()));

        filter_activityStatusCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getActionStatus()));


        activitiesTable.setRowFactory(tableView -> {
            TableRow<LogsDetails> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    LogsDetails logsDetails = row.getItem();

                    fillLogsPanel(logsDetails);

                    activitiesListPanel.setVisible(false);
                    activitiesListPanel.setManaged(false);

                    viewLogDetailsPanel.setVisible(true);
                    viewLogDetailsPanel.setManaged(true);
                }
            });

            return row;
        });
    }

    @FXML
    public void showDashboard() {
        List<LogsDetails> cachedLogs = ApiUtilityClass.systemLogsCacheManager.getCachedSystemLogs();
        if (cachedLogs != null) {
            recentActivitiesTable.getItems().addAll(cachedLogs);
        }

        switchView(dashboardBtn, dashboardView);
    }

    @FXML
    public void showManageStudent() {
        switchView(studentNavBtn, manageStudentView);
    }

    @FXML
    public void showManageScores() {
        switchView(scoresNavBtn, subjectScoresView);
    }

    @FXML
    public void showManageSubjects() {
        switchView(subjectNavBtn, manageSubjectView);
    }

    @FXML
    public void showManageResults() {
        switchView(reportsNavBtn, manageReportsView);
    }

    @FXML
    public void showManageStaffs() {
        switchView(staffNavButton, manageStaffView);
    }

    @FXML
    public void showSystemLogs() {
        switchView(logsNavBtn, systemLogsView);
    }

    @FXML
    public void handleLogout() throws IOException {
        UtilityMethods.clearSession();

        Stage adminDashboard = (Stage) logoutButton.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/Authentication/auth-container.fxml"));
        Scene scene = new Scene(loader.load());

        // Create new stage for dashboard
        Stage loginStage = new Stage();
        loginStage.setTitle("EazyManage - Authentication");
        loginStage.setScene(scene);
        loginStage.sizeToScene();
        loginStage.setResizable(false);

        adminDashboard.close();
        loginStage.show();
    }

    @FXML
    public void refreshDashboard() {
        showLoading();
    }

    @FXML
    public void showAddStudentCard() {
        UtilityMethods.switchPageCards(addStudentCard, updateStudentInfoCard,
                addStudentToggleBtn, updateStudentToggleBtn);
    }

    @FXML
    public void showUpdateInfoCard() {
        UtilityMethods.switchPageCards(updateStudentInfoCard, addStudentCard,
                updateStudentToggleBtn, addStudentToggleBtn);
    }

    @FXML
    public void LoadClassStudent() {

    }

    @FXML
    public void handleFindStudent() {

    }

    @FXML
    public void handleUpdateStudentInfo() {

    }

    @FXML
    public void handleCancelInfoUpdate() {

    }

    @FXML
    public void fetchClassSubjects() {

    }

    @FXML
    public void handleLoadStudentsForScores() {

    }

    @FXML
    public void handleClearScoresTable() {

    }

    @FXML
    public void handleSaveScores() {

    }

    @FXML
    public void showAddSubjectCard() {
        UtilityMethods.switchPageCards(addNewSubjectCard, updateSubjectsCard,
                addSubjectToggleBtn, updateSubjectToggleBtn);
    }

    @FXML
    public void showUpdateSubjectCard() {
        UtilityMethods.switchPageCards(updateSubjectsCard, addNewSubjectCard,
                updateSubjectToggleBtn, addSubjectToggleBtn);
    }

    @FXML
    public void handleAddSubject() {

    }

    @FXML
    public void handleFetchSubjects() {

    }

    @FXML
    public void handleLoadSubjectData() {

    }

    @FXML
    public void handleUpdateSubjectData() {

    }

    @FXML
    public void handleDeleteSubject() {

    }

    @FXML
    public void handleLoadClassResults() {

    }

    @FXML
    public void handleDownloadReports() {

    }

    @FXML
    public void handleClearResults() {

    }

    @FXML
    public void handleResetPassword() {

    }

    @FXML
    public void handleCancelUpdate() {

    }

    @FXML
    public void handleUpdateStaff() {

    }

    @FXML
    public void backToLogsList() {

    }

    @FXML
    public void handleSaveAndClear() {

    }


    /** ---------------UTILITY METHODS FOR THIS CLASS--------------- **/

    public void initializeAfterLogin() {
        apiUtilityClass = new ApiUtilityClass(this);
        setActiveButton(dashboardBtn);
        loadStaffInfo();
    }

    private void loadStaffInfo() {
        if (institutionName != null) {
            institutionalNameLabel.setText(institutionName);
        }
        if (staffFullName != null) {
            adminNameLabel.setText(staffFullName);
        }

        versionLabel.setText(apiUtilityClass.applicationVersion);
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public void setStaffFullName(String staffFullName) {
        this.staffFullName = staffFullName;
    }

    private void showLoading() {
        loadingOverlay.setVisible(true);
        loadingOverlay.setManaged(true);

        contentArea.setDisable(true);
    }

    private void hideLoading() {
        loadingOverlay.setVisible(false);
        loadingOverlay.setManaged(false);

        contentArea.setDisable(false);
    }

    private void switchView(Button activeButton, ScrollPane viewToShow) {

        dashboardView.setVisible(false);
        dashboardView.setManaged(false);

        manageStudentView.setVisible(false);
        manageStudentView.setManaged(false);

        subjectScoresView.setVisible(false);
        subjectScoresView.setManaged(false);

        manageSubjectView.setVisible(false);
        manageSubjectView.setManaged(false);

        manageReportsView.setVisible(false);
        manageReportsView.setManaged(false);

        manageStaffView.setVisible(false);
        manageStaffView.setManaged(false);

        systemLogsView.setVisible(false);
        systemLogsView.setManaged(false);

        setActiveButton(activeButton);
        viewToShow.setVisible(true);
        viewToShow.setManaged(true);
    }

    private void setActiveButton(Button activeButton) {
        String INACTIVE_BUTTON = "-fx-background-color: transparent; -fx-text-fill: #ecf0f1; " +
                "-fx-font-size: 14px; -fx-padding: 15 20 15 20; -fx-background-radius: 8; " +
                "-fx-cursor: hand; -fx-border-color: transparent;";

        String ACTIVE_BUTTON = "-fx-background-color: #667eea; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 15 20 15 20; " +
                "-fx-background-radius: 8; -fx-cursor: hand;";


        dashboardBtn.setStyle(INACTIVE_BUTTON);
        studentNavBtn.setStyle(INACTIVE_BUTTON);
        scoresNavBtn.setStyle(INACTIVE_BUTTON);
        subjectNavBtn.setStyle(INACTIVE_BUTTON);
        reportsNavBtn.setStyle(INACTIVE_BUTTON);
        staffNavButton.setStyle(INACTIVE_BUTTON);
        logsNavBtn.setStyle(INACTIVE_BUTTON);

        activeButton.setStyle(ACTIVE_BUTTON);
    }

    private void fillLogsPanel(LogsDetails log) {
        activityIdField.setText(log.getActionId());
        activityDateField.setText(log.getActionDate());
        activityTimeField.setText(log.getActionTime());

        activityTypeField.setText(log.getActionType());
        activityOwnerField.setText(log.getCreatedBy());
        activityStatusField.setText(log.getActionStatus());
        if (log.getActionStatus().equals("SUCCESS") ) {
            activityStatusField.setTextFill(Color.web("#16A34A"));
        } else {
            activityStatusField.setTextFill(Color.web("#DC2626"));
        }

        activityDataField.setText(log.getActionData());
    }

    private void getStaffDetails(String staffId) {
        apiUtilityClass.searchStaffData(staffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        fillStaffData(apiUtilityClass.findStaffResponse);

                        staffListTableContainer.setVisible(false);
                        staffListTableContainer.setManaged(false);

                        staffDetailsPanel.setVisible(true);
                        staffDetailsPanel.setManaged(true);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.showError(notification, "Network Error! Check your connection and try again");
                    });
                });
    }

    private void fillStaffData(FindStaffResponse data) {
        staffFullNameLabel.setText(data.getFirstName() + " " + data.getSurname());
        staffIdLabel.setText(data.getStaffId());

        editFirstNameField.setText(data.getFirstName());
        editLastNameField.setText(data.getSurname());

        editEmailField.setText(data.getEmail());
        editPhoneField.setText(data.getPhoneNumber());
        editDateOfBirthPicker.setValue(LocalDate.parse(data.getDateOfBirth()));

        editGenderComboBox.getItems().addAll(Gender.values());
        editGenderComboBox.setValue(Gender.valueOf(data.getGender()));

        editRoleComboBox.getItems().addAll(StaffRoles.values());
        editRoleComboBox.setValue(StaffRoles.valueOf(data.getStaffRole()));

        editStatusComboBox.getItems().addAll(StaffStatus.values());
        editStatusComboBox.setValue(StaffStatus.valueOf(data.getStaffStatus()));
    }

    private double parseDouble(String input) {
        if (input == null || input.isBlank()) return 0;
        return Double.parseDouble(input);
    }

    private void recalculateClassScore(StudentsScoresTable row) {
        try {
            double ex1 = parseDouble(row.getExercise1Score());
            double ex2 = parseDouble(row.getExercise2Score());
            double test = parseDouble(row.getClassTestScore());
            double project = parseDouble(row.getProjectScore());

            double totalScore = ex1 + ex2 + test + project;

            double classScore = (totalScore/100) * 50;

            row.setClassScore(String.format("%.1f", classScore));
        } catch (Exception e) {

            row.setClassScore("0");
        }
    }

    private void reCalculateExamScore(StudentsScoresTable row) {
        try {
            double examScore = parseDouble(row.getExamScore());

            double calculatedScore = (examScore/100) * 50;

            row.setCalculatedExamScore(String.format("%.1f", calculatedScore));
        } catch (Exception e) {
            row.setCalculatedExamScore("0");
        }
    }
}
