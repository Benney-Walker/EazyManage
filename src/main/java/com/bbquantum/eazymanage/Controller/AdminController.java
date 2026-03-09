package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.Constants.Gender;
import com.bbquantum.eazymanage.Constants.StaffRoles;
import com.bbquantum.eazymanage.Constants.StaffStatus;
import com.bbquantum.eazymanage.Constants.StudentStatus;
import com.bbquantum.eazymanage.DTO.Academics.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class AdminController implements Initializable {

    //CONTROLLER OWN VARIABLES
    private ApiUtilityClass apiUtilityClass;
    private String institutionName;
    public String staffId;
    private String staffFullName;

    // Holds subject Id for scores
    private String subjectId = "";
    private int currentUploadIndex = 0;

    // Holds results parameters
    private GradeCaching resultGrade;
    private SemesterCaching resultSemester;

    //Holds semester information for update
    public FindSemester semester;

    @FXML public StackPane notification;

    // NAV SIDE
    @FXML public Label versionLabel;

    @FXML private Button dashboardBtn;
    @FXML private Button studentNavBtn;
    @FXML private Button gradeCriteriaNavBtn;
    @FXML private Button scoresNavBtn;
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

    //ADD CLASS PAGE
    @FXML private ScrollPane manageClassView;

    @FXML private Button addClassToggleBtn;
    @FXML private Button updateClassInfoToggleBtn;

    @FXML private StackPane classCardContainer;

    @FXML private VBox addClassCard;
    @FXML private TextField addClassNameField;
    @FXML private ComboBox<StaffCaching> addClassStaffComboBox;
    @FXML private Button saveClassBtn;

    @FXML private VBox updateClassInfoCard;
    @FXML private VBox updateClassSearchArea;
    @FXML private ComboBox<GradeCaching> updateClassSearchBox;
    @FXML private Button searchClassBtn;

    @FXML private VBox updateClassInfoDisplayArea;
    @FXML private TextField updateClassInfoNameField;
    @FXML private ComboBox<StaffCaching> updateClassInfoStaffComboBox;
    @FXML private Button updateClassInfoBtn;

    //STUDENTS PAGE
    @FXML private ScrollPane manageStudentView;

    @FXML private Button addStudentToggleBtn;
    @FXML private Button updateStudentToggleBtn;

    @FXML private StackPane studentInfoCardContainer;

    @FXML private VBox addStudentCard;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<Gender> genderComboBox;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private TextField homeTownField;
    @FXML private TextField parentNameField;
    @FXML private TextField parentPhoneField;
    @FXML private ComboBox<GradeCaching> classComboBox;
    @FXML private Button saveAndClearBtn;

    @FXML private VBox updateStudentInfoCard;
    @FXML private VBox studentSearchFieldBox;
    @FXML private ComboBox<StudentsHolder> student_SelectionBox;
    @FXML private ComboBox<GradeCaching> student_classSelectionBox;
    @FXML private Button findStudentBtn;
    @FXML private VBox studentInfoPanel;
    @FXML private Label studentIdLabel;
    @FXML private Label studentStatusLabel;
    @FXML private Label updateGradeNameLabel;
    @FXML private TextField updateFirstNameField;
    @FXML private TextField updateLastNameField;
    @FXML private ComboBox<Gender> updateGenderComboBox;
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

    //GRADING CRITERIA PAGE
    @FXML private ScrollPane gradingCriteriaView;
    @FXML private TextField gradeCriteriaField;
    @FXML private TextField lowerRangeField;
    @FXML private TextField upperRangeField;
    @FXML private TextField gradeRemarksField;
    @FXML private Button saveGradeBtn;

    //SUBJECT SCORES VIEW
    @FXML private ScrollPane subjectScoresView;

    @FXML private VBox subjectResultSearchBox;
    @FXML private ComboBox<SubjectsHolder> score_selectSubjectBox;
    @FXML private ComboBox<GradeCaching> score_SelectClassBox;
    @FXML private Button score_LoadStudentsBtn;

    @FXML private HBox subjectInfoBox;
    @FXML private Label subjectNameLabel;
    @FXML private Label subjectCodeLabel;

    @FXML private VBox resultsTableContainer;

    @FXML private VBox scores_SaveProgressBarVBox;
    @FXML private ProgressBar score_SaveProgressBar;

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
    @FXML private Button saveScoresBtn;

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
    @FXML private VBox viewResultsFilterContainer;
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

    @FXML private VBox resultsPassMarkBox;
    @FXML private TextField passScoreField;
    @FXML private ComboBox<GradeCaching> promotionGradeComboBox;
    @FXML private Button generateReportBtn;

    //MANAGE STAFF VIEW
    @FXML private ScrollPane manageStaffView;
    @FXML private VBox staffListTableContainer;
    @FXML private StackPane staffManagementContainer;
    @FXML public TableView<ViewStaffList> staffListTable;
    @FXML private TableColumn<ViewStaffList, String> staffIdColumn;
    @FXML private TableColumn<ViewStaffList, String> staffNameColumn;
    @FXML private TableColumn<ViewStaffList, String> staffRoleColumn;

    @FXML private VBox staffDetailsPanel;
    @FXML private Label staffFullNameLabel;
    @FXML private Label staffIdLabel;
    @FXML private TextField editFirstNameField;
    @FXML private TextField editLastNameField;
    @FXML private TextField editEmailField;
    @FXML private TextField editPhoneField;
    @FXML private DatePicker editDateOfBirthPicker;
    @FXML private ComboBox<Gender> editGenderComboBox;
    @FXML private CheckBox role1;
    @FXML private CheckBox role2;
    @FXML private CheckBox role3;
    @FXML private CheckBox role4;
    @FXML private CheckBox role5;
    @FXML private ComboBox<StaffStatus> editStatusComboBox;

    @FXML private Button resetPasswordBtn;
    @FXML private Button updateStaffInfoBtn;

    @FXML private VBox passwordResetBox;
    @FXML private PasswordField passwordField;
    @FXML private TextField newPasswordField;
    @FXML private Button okResetBtn;

    //SYSTEM LOGS
    @FXML private ScrollPane systemLogsView;
    @FXML private VBox logsSearchArea;
    @FXML private DatePicker filterDatePicker;
    @FXML private ComboBox<StaffCaching> filterStaffBox;
    @FXML private Button fetchSystemLogsBtn;
    @FXML private StackPane logsCardContainer;

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


    //MANAGE SEMESTER VIEW
    @FXML private ScrollPane manageSemesterView;

    @FXML private Button addSemesterToggleBtn;
    @FXML private Button updateSemesterToggleBtn;

    @FXML private VBox addNewSemesterPanel;
    @FXML private TextField semesterNameField;
    @FXML private DatePicker semesterStartDate;
    @FXML private DatePicker semesterEndDate;
    @FXML private TextField semesterAcademicYearField;
    @FXML private Button addSemesterBtn;

    @FXML private VBox updateSemesterPanel;
    @FXML private VBox updateSemesterSearchBox;
    @FXML private ComboBox<SemesterCaching> semester_searchInfoBox;
    @FXML private Button update_searchSemesterBtn;

    @FXML private VBox updateSemesterResponsePanel;
    @FXML private Label updateSemesterIdLabel;
    @FXML private TextField updateSemesterNameField;
    @FXML private DatePicker updateSemesterStartDate;
    @FXML private DatePicker updateSemesterEndDate;
    @FXML private TextField updateSemesterAcademicYearField;
    @FXML private Button updateSemesterBtn;

    //LOADING OVERLAY
    @FXML private VBox loadingOverlay;
    @FXML private ProgressIndicator loadingSpinner;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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

        staffRoleColumn.setCellValueFactory(cellData -> {
            List<String> staffRoles = cellData.getValue().getStaffRoles();

            if (staffRoles == null || staffRoles.isEmpty()) {
                return new SimpleStringProperty("");
            }

            String joinRoles = staffRoles.stream().collect(Collectors.joining(","));
            return new SimpleStringProperty(joinRoles);
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
        loadDashboardData();

        switchView(dashboardBtn, dashboardView);
    }

    @FXML
    public void showManageStudent() {
        switchView(studentNavBtn, manageStudentView);
        showAddStudentCard();
    }

    @FXML
    public void showGradingCriteria() {
        switchView(gradeCriteriaNavBtn, gradingCriteriaView);
    }

    @FXML
    public void showManageScores() {
        score_selectSubjectBox.setDisable(true);

        List<GradeCaching> cachedGrades = UtilityMethods.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (score_SelectClassBox.getItems().isEmpty()) {
                score_SelectClassBox.getItems().addAll(cachedGrades);
            }
        }

        switchView(scoresNavBtn, subjectScoresView);
    }

    @FXML
    public void showManageSemester() {
        switchView(dashboardBtn, manageSemesterView);

        showAddSemesterCard();
    }

    public void showManageClasses() {
        switchView(dashboardBtn, manageClassView);

        showAddClassCard();
    }

    @FXML
    public void showManageSubjects() {
        switchView(dashboardBtn, manageSubjectView);

        showAddSubjectCard();
    }

    @FXML
    public void showManageResults() {
        List<GradeCaching> cachedGrades = UtilityMethods.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (results_GradeSelectionBox.getItems().isEmpty()) {
                results_GradeSelectionBox.getItems().addAll(cachedGrades);
            }
        }

        List<SemesterCaching> cachedSemesters = UtilityMethods.semesterCachingManager.getCachedSemesters();
        if (cachedSemesters != null && !cachedSemesters.isEmpty()) {
            if (results_SemesterSelectionBox.getItems().isEmpty()) {
                results_SemesterSelectionBox.getItems().addAll(cachedSemesters);
            }
        }

        resultsDisplayPanel.setVisible(false);
        resultsDisplayPanel.setManaged(false);

        switchView(reportsNavBtn, manageReportsView);
    }

    @FXML
    public void showManageStaffs() {
        staffDetailsPanel.setVisible(false);
        staffDetailsPanel.setManaged(false);

        List<ViewStaffList> cachedStaffList = UtilityMethods.staffListCacheManager.getCachedStaffList();
        if (cachedStaffList != null && !cachedStaffList.isEmpty()) {
            if (staffListTable.getItems().isEmpty()) {
                staffListTable.getItems().addAll(cachedStaffList);
            }
        }

        switchView(staffNavButton, manageStaffView);
    }

    @FXML
    public void showSystemLogs() {
        viewLogDetailsPanel.setVisible(false);
        viewLogDetailsPanel.setManaged(false);

        activitiesListPanel.setVisible(true);
        activitiesListPanel.setManaged(true);

        List<StaffCaching> cachedStaffList = UtilityMethods.staffCacheManager.getCachedStaffList();
        if (cachedStaffList != null && !cachedStaffList.isEmpty()) {
            if (filterStaffBox.getItems().isEmpty()) {
                filterStaffBox.getItems().addAll(cachedStaffList);
            }
        }

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
        apiUtilityClass.loadAdminDashboard(staffId, notification,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        loadDashboardData();

                        hideLoading();
                    });
                });
    }

    @FXML
    public void showAddClassCard() {
        List<StaffCaching> cachedStaffList = UtilityMethods.staffCacheManager.getCachedStaffList();
        if (cachedStaffList != null && !cachedStaffList.isEmpty()) {
            if (addClassStaffComboBox.getItems().isEmpty()) {
                addClassStaffComboBox.getItems().addAll(cachedStaffList);
            }
        }

        addClassNameField.clear();
        addClassStaffComboBox.getSelectionModel().clearSelection();

        UtilityMethods.switchPageCards(addClassCard, updateClassInfoCard,
                addClassToggleBtn, updateClassInfoToggleBtn);
    }

    @FXML
    public void showUpdateClassInfoCard() {
        List<GradeCaching> cachedGrades = UtilityMethods.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (updateClassSearchBox.getItems().isEmpty()) {
                updateClassSearchBox.getItems().addAll(cachedGrades);
            }
        }

        updateClassInfoDisplayArea.setVisible(false);
        updateClassInfoDisplayArea.setManaged(false);

        updateClassSearchArea.setVisible(true);
        updateClassSearchArea.setManaged(true);

        UtilityMethods.switchPageCards(updateClassInfoCard, addClassCard,
                updateClassInfoToggleBtn, addClassToggleBtn);
    }

    @FXML
    public void handleAddClass() {
        String className = addClassNameField.getText().trim();
        if (className.isEmpty()) {
            UtilityMethods.showError(notification, "Class name is required");
            return;
        }

        if (!className.contains("_") || className.contains(" ")) {
            UtilityMethods.showError(notification, "Class name must contain '_' eg.Grade_1");
            return;
        }

        StaffCaching staffCaching = addClassStaffComboBox.getSelectionModel().getSelectedItem();
        if (staffCaching == null) {
            UtilityMethods.showError(notification, "Please select a staff cache");
            return;
        }

        String staffId = staffCaching.getStaffId();
        UtilityMethods.processingButton(saveClassBtn);

        apiUtilityClass.addNewGrade(className, staffId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(saveClassBtn, "ADD CLASS");
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                    });
                }, () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(saveClassBtn, "ADD CLASS");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                }, () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(saveClassBtn, "ADD CLASS");
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleFindClass() {
        GradeCaching selectedGrade = updateClassSearchBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            UtilityMethods.showError(notification, "Please select a grade from the grades list");
            return;
        }

        String levelId = selectedGrade.getLevelId();
        UtilityMethods.processingButton(searchClassBtn);

        apiUtilityClass.findClassInfo(levelId, staffId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        populateClassInfoArea();

                        updateClassSearchArea.setVisible(false);
                        updateClassSearchArea.setManaged(false);
                        UtilityMethods.notProcessingButton(searchClassBtn, "FETCH GRADE INFO");
                        UtilityMethods.showSuccess(notification, "Class information found");
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(searchClassBtn, "FIND CLASS");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(searchClassBtn, "FIND CLASS");
                        UtilityMethods.showError(notification, "Network error! Check your connection and try again");
                    });
                });
    }

    @FXML
    public void handleUpdateClassInfo() {
        String newClassName = updateClassInfoNameField.getText().trim();
        if (newClassName.isEmpty()) {
            UtilityMethods.showError(notification, "Please enter class name");
            return;
        }

        if (!newClassName.contains("_") || newClassName.contains(" ")) {
            UtilityMethods.showError(notification, "Class name must contain '_' eg.Grade_1");
            return;
        }

        StaffCaching staffCaching = updateClassInfoStaffComboBox.getSelectionModel().getSelectedItem();
        if (staffCaching == null) {
            UtilityMethods.showError(notification, "Please select a staff");
            return;
        }

        UtilityMethods.processingButton(updateClassInfoBtn);
        UtilityMethods.findAndUpdateClassInfo.setLevelName(newClassName);
        UtilityMethods.findAndUpdateClassInfo.setStaffId(staffCaching.getStaffId());

        apiUtilityClass.updateClassInfo(UtilityMethods.findAndUpdateClassInfo,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(updateClassInfoBtn, "SAVE CHANGES");
                        updateClassInfoDisplayArea.setVisible(false);
                        updateClassInfoDisplayArea.setManaged(false);

                        updateClassSearchArea.setVisible(true);
                        updateClassSearchArea.setManaged(true);
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(updateClassInfoBtn, "SAVE CHANGES");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(updateClassInfoBtn, "SAVE CHANGES");
                        UtilityMethods.showError(notification, "Network Error! Check your connection and try again");
                    });
                });
    }

    @FXML
    public void showAddStudentCard() {
        List<GradeCaching> cachedGrades = UtilityMethods.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (classComboBox.getItems().isEmpty()) {
                classComboBox.getItems().addAll(cachedGrades);
            }
        }

        if (genderComboBox.getItems().isEmpty()) {
            genderComboBox.getItems().addAll(Gender.values());
        }

        UtilityMethods.switchPageCards(addStudentCard, updateStudentInfoCard,
                addStudentToggleBtn, updateStudentToggleBtn);
    }

    @FXML
    public void showUpdateStudentInfoCard() {
        List<GradeCaching> cachedGrades = UtilityMethods.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (student_classSelectionBox.getItems().isEmpty()) {
                student_classSelectionBox.getItems().addAll(cachedGrades);
            }
        }

        student_SelectionBox.getSelectionModel().clearSelection();
        student_SelectionBox.setDisable(true);

        student_classSelectionBox.getSelectionModel().clearSelection();

        UtilityMethods.switchPageCards(updateStudentInfoCard, addStudentCard,
                updateStudentToggleBtn, addStudentToggleBtn);
    }

    @FXML
    public void LoadClassStudent() {
        GradeCaching selectedGrade = student_classSelectionBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            return;
        }

        String levelId = selectedGrade.getLevelId();
        apiUtilityClass.loadClassStudents(levelId, staffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        student_SelectionBox.getItems().addAll(UtilityMethods.studentsHolder);
                        student_SelectionBox.setDisable(false);
                        UtilityMethods.showSuccess(notification, "Students Fetched. You can select student now");
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Failure
                    Platform.runLater(() -> {
                        UtilityMethods.showError(notification, "Network error! Check your connection and try again");
                    });
                });
    }

    @FXML
    public void handleFindStudentData() {
        if (student_SelectionBox.isDisable()) {
            UtilityMethods.showError(notification, "Please select a class to fetch Students");
            return;
        }

        StudentsHolder student = student_SelectionBox.getSelectionModel().getSelectedItem();
        if (student == null) {
            UtilityMethods.showError(notification, "Please select student");
            return;
        }

        String studentId = student.getStudentId();
        UtilityMethods.processingButton(findStudentBtn);

        apiUtilityClass.searchStudentData(studentId, staffId,
                () -> {
                    // On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(findStudentBtn, "FETCH DETAILS");
                        UtilityMethods.showSuccess(notification, "Student Found");
                        studentInfoUpdatePanel(UtilityMethods.updateStudentPersonalData);
                    });
                },
                () -> {
                    //On failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(findStudentBtn, "FETCH DETAILS");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(findStudentBtn, "FETCH DETAILS");
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleUpdateStudentInfo() {
        if (!validateUpdateForm()) return;

        UtilityMethods.updateStudentPersonalData = getUpdatedInfo();
        UtilityMethods.processingButton(updateStudentInfoBtn);

        apiUtilityClass.updateStudentPersonalData(UtilityMethods.updateStudentPersonalData,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(updateStudentInfoBtn, "UPDATE INFO");
                        showUpdateStudentInfoCard();
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(updateStudentInfoBtn, "UPDATE INFO");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                }, () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(updateStudentInfoBtn, "UPDATE INFO");
                        UtilityMethods.showError(notification, "Network Error! Check your internet connection and try again");
                    });
                });
    }

    @FXML
    public void handleCancelInfoUpdate() {
        showUpdateStudentInfoCard();
        UtilityMethods.updateStudentPersonalData = null;
    }

    @FXML
    public void fetchClassSubjects() {
        GradeCaching selectedGrade = score_SelectClassBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            return;
        }

        String levelId = selectedGrade.getLevelId();
        apiUtilityClass.loadClassSubjects(levelId, staffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        if (UtilityMethods.subjectsHolder != null) {
                            score_selectSubjectBox.getItems().addAll(UtilityMethods.subjectsHolder);
                            score_selectSubjectBox.setDisable(false);
                        }
                        UtilityMethods.showSuccess(notification, "Subjects loaded successfully. You can select now");
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On NetworkIssue
                    Platform.runLater(() -> {
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleSaveGradingCriteria() {
        String gradeCriteria = gradeCriteriaField.getText();
        Double lowerRange = Double.parseDouble(lowerRangeField.getText());
        Double upperRange = Double.parseDouble(upperRangeField.getText());
        String remarks = gradeRemarksField.getText();

        if (!validateGradeCriteriaForm(
                gradeCriteria, lowerRange, upperRange, remarks
        )) return;

        UtilityMethods.processingButton(saveGradeBtn);
        GradingCriteria gradingCriteria = new GradingCriteria(
                lowerRange, upperRange, gradeCriteria, remarks
        );

        apiUtilityClass.saveGradingCriteria(gradingCriteria, staffId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(saveGradeBtn, "ADD GRADE CRITERIA");
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                        gradeCriteriaField.clear();
                        upperRangeField.clear();
                        lowerRangeField.clear();
                        gradeRemarksField.clear();
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(saveGradeBtn, "ADD GRADE CRITERIA");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                }, () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(saveGradeBtn, "ADD GRADE CRITERIA");
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleLoadStudentsForScores() {
        if (score_selectSubjectBox.isDisable()) {
            UtilityMethods.showError(notification, "Select class to load subjects");
            return;
        }

        SubjectsHolder selectedSubject = score_selectSubjectBox.getSelectionModel().getSelectedItem();
        if (selectedSubject == null) {
            UtilityMethods.showError(notification, "Select subject to load students");
            return;
        }

        UtilityMethods.processingButton(score_LoadStudentsBtn);
        subjectId = selectedSubject.getSubjectId();

        apiUtilityClass.loadSubjectStudents(subjectId, staffId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(score_LoadStudentsBtn, "LOAD STUDENTS");
                        displaySubjectStudents(UtilityMethods.subjectScores);
                        UtilityMethods.showSuccess(
                                notification, "Successfully load " + UtilityMethods.subjectScores.getScoresTable().size() + " students"
                        );
                        score_selectSubjectBox.setDisable(true);

                        subjectResultSearchBox.setVisible(false);
                        subjectResultSearchBox.setManaged(false);
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(score_LoadStudentsBtn, "LOAD STUDENTS");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                ()-> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(score_LoadStudentsBtn, "LOAD STUDENTS");
                        UtilityMethods.showError(notification, "Network error! Check your network connection and try again!");
                    });
                });
    }

    @FXML
    public void handleClearScoresTable() {
        for (StudentsScoresTable student : studentsScoresTable.getItems()) {
            student.setExercise1Score("0");
            student.setClassTestScore("0");
            student.setExercise2Score("0");
            student.setProjectScore("0");
            student.setClassScore("0");
            student.setExamScore("0");
            student.setCalculatedExamScore("0");
        }
        studentsScoresTable.refresh();
    }

    public void handleCancelScores() {
        resultsTableContainer.setVisible(false);
        resultsTableContainer.setManaged(false);

        subjectResultSearchBox.setVisible(true);
        subjectResultSearchBox.setManaged(true);
    }

    @FXML
    public void handleSaveScores() {
        SemesterCaching selectedSemester = saveScoresSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            UtilityMethods.showError(notification, "Select semester");
            return;
        }

        UtilityMethods.processingButton(saveScoresBtn);
        String scoresInfo =
                subjectId + "_" + selectedSemester.getSemesterId() + "_" + staffId;

        scores_SaveProgressBarVBox.setVisible(true);
        scores_SaveProgressBarVBox.setManaged(true);

        uploadNextScore(scoresInfo);
    }

    @FXML
    public void showAddSubjectCard() {
        List<GradeCaching> cachedGrades = UtilityMethods.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (gradeComboBox.getItems().isEmpty()) {
                gradeComboBox.getItems().addAll(cachedGrades);
            }
        }

        UtilityMethods.switchPageCards(addNewSubjectCard, updateSubjectsCard,
                addSubjectToggleBtn, updateSubjectToggleBtn);
    }

    @FXML
    public void showUpdateSubjectCard() {
        List<GradeCaching> cachedGrades = UtilityMethods.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (subject_gradeSelectionBox.getItems().isEmpty()) {
                subject_gradeSelectionBox.getItems().addAll(cachedGrades);
            }
        }

        subjectSelectionBox.setDisable(true);

        UtilityMethods.switchPageCards(updateSubjectsCard, addNewSubjectCard,
                updateSubjectToggleBtn, addSubjectToggleBtn);
    }

    @FXML
    public void handleAddSubject() {
        String subjectName = subjectNameField.getText();
        if (subjectName.isEmpty()) {
            UtilityMethods.showError(notification, "Please enter subjectName");
            return;
        }

        GradeCaching grade = gradeComboBox.getSelectionModel().getSelectedItem();
        if (grade == null) {
            UtilityMethods.showError(notification, "Please select a grade");
            return;
        }

        String gradeId = grade.getLevelId();

        UtilityMethods.processingButton(saveSubjectBtn);

        AssignSubject assignSubject = new AssignSubject(
                staffId, subjectName, gradeId
        );

        apiUtilityClass.addNewSubject(assignSubject,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(saveSubjectBtn, "Save Subject");
                        UtilityMethods.showSuccess(notification, "Successfully saved subject with ID: " + apiUtilityClass.successMessageContainer);
                        subjectNameField.clear();
                        gradeComboBox.getSelectionModel().clearSelection();
                    });
                }, () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(saveSubjectBtn, "Save Subject");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                }, () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(saveSubjectBtn, "Save Subject");
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleFetchSubjects() {
        GradeCaching selectedGrade = subject_gradeSelectionBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            return;
        }

        UtilityMethods.showError(notification, "Loading class subjects");
         apiUtilityClass.loadClassSubjects(selectedGrade.getLevelId(), staffId,
                 () -> {
             //On Success
                     Platform.runLater(() -> {
                         if (UtilityMethods.subjectsHolder != null && !UtilityMethods.subjectsHolder.isEmpty()) {
                             subjectSelectionBox.getItems().addAll(UtilityMethods.subjectsHolder);
                         }

                         UtilityMethods.showSuccess(notification, "Successfully loaded subjects");
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
                         UtilityMethods.showError(notification, "Network error! Check your network connection and try again!");
                     });
                 });
    }

    @FXML
    public void handleLoadSubjectData() {
        if (subjectSelectionBox.isDisable()) {
            UtilityMethods.showError(notification, "Please select a grade to load subjects");
            return;
        }

        SubjectsHolder selectedSubject = subjectSelectionBox.getSelectionModel().getSelectedItem();
        if (selectedSubject == null) {
            UtilityMethods.showError(notification, "Select a subject");
            return;
        }

        String subjectId = selectedSubject.getSubjectId();
        UtilityMethods.processingButton(fetchSubjectBtn);

        apiUtilityClass.fetchSubjectData(staffId, subjectId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        loadSubjectData(UtilityMethods.subjectData);
                        UtilityMethods.notProcessingButton(fetchSubjectBtn, "FETCH SUBJECT DATA");
                        UtilityMethods.showSuccess(notification, "Successfully fetched subject");

                        subjectSearchBox.setVisible(false);
                        subjectSearchBox.setManaged(false);

                        subjectSearchResponsePanel.setVisible(true);
                        subjectSearchResponsePanel.setManaged(true);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(fetchSubjectBtn, "FETCH SUBJECT DATA");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(fetchSubjectBtn, "FETCH SUBJECT DATA");
                        UtilityMethods.showError(notification, "Network error! Check your network connection and try again!");
                    });
                });
    }

    @FXML
    public void handleUpdateSubjectData() {
        String subjectName = subject_updateNameField.getText().trim();
        if (subjectName.isEmpty()) {
            UtilityMethods.showError(notification, "Please enter a subject name");
            return;
        }

        GradeCaching selectedGrade = subject_updateGradeBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            UtilityMethods.showError(notification, "Select a grade");
            return;
        }

        UtilityMethods.processingButton(updateSubjectBtn);
        String subjectId = subjectIdLabel.getText();

        UtilityMethods.subjectData.setSubjectId(subjectId);
        UtilityMethods.subjectData.setSubjectName(subjectName);
        UtilityMethods.subjectData.setLevelId(
                selectedGrade.getLevelId()
        );

        updateSubjectDetails(UtilityMethods.subjectData);
    }

    @FXML
    public void handleDeleteSubject() {
        String subjectId = subjectIdLabel.getText();

        UtilityMethods.processingButton(deleteSubjectBtn);
        apiUtilityClass.deleteSubject(staffId, subjectId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(deleteSubjectBtn, "DELETE SUBJECT");

                        subjectSearchResponsePanel.setVisible(false);
                        subjectSearchResponsePanel.setManaged(false);

                        subjectSearchBox.setVisible(true);
                        subjectSearchBox.setManaged(true);

                        UtilityMethods.showSuccess(notification, "Successfully deleted subject data");
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(deleteSubjectBtn, "DELETE SUBJECT");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(deleteSubjectBtn, "DELETE SUBJECT");
                        UtilityMethods.showError(notification, "Network error! Check your network connection and try again!");
                    });
                });
    }

    @FXML
    public void handleCancelSubjectUpdate() {
        subjectSearchResponsePanel.setVisible(false);
        subjectSearchResponsePanel.setManaged(false);

        subjectSearchBox.setVisible(true);
        subjectSearchBox.setManaged(true);
    }

    @FXML
    public void handleLoadClassResults() {
        resultGrade = results_GradeSelectionBox.getSelectionModel().getSelectedItem();
        if (resultGrade == null) {
            UtilityMethods.showError(notification, "Please select a grade to load");
            return;
        }

        resultSemester = results_SemesterSelectionBox.getSelectionModel().getSelectedItem();
        if (resultSemester == null) {
            UtilityMethods.showError(notification, "Select Semester to Load Results");
            return;
        }

        String levelId = resultGrade.getLevelId();
        String semesterId = resultSemester.getSemesterId();
        UtilityMethods.processingButton(loadClassResultsBtn);
        loadClassResults(levelId, semesterId);
    }

    @FXML
    public void handleDownloadReports() {
        String levelId = resultGrade.getLevelId();
        String semesterName = resultSemester.getSemesterName();

        String[] semesterNameSplit = semesterName.split("-");

        String promotionGradeId = "no_promotion";
        String passedTotalScore = "";
        if (semesterNameSplit[0].equals("THIRD")) {
            if (!resultsPassMarkBox.isVisible()) {
                resultsPassMarkBox.setVisible(true);
                resultsPassMarkBox.setManaged(true);
                UtilityMethods.showError(notification,
                        "Enter pass score and select grade to contain passed students");
                return;
            }

            if (!resultsPassMarkBox.isVisible()) {
                resultsPassMarkBox.setVisible(true);
                resultsPassMarkBox.setManaged(true);
            }

            GradeCaching selectedPromotionGrade =
                    promotionGradeComboBox.getSelectionModel().getSelectedItem();
            if (selectedPromotionGrade == null) {
                UtilityMethods.showError(notification, "Select grade to contain promoted students");
                return;
            }

            passedTotalScore = passScoreField.getText();
            if (passedTotalScore.equals("0") || passedTotalScore.matches("\\d+")
                    || passedTotalScore.isEmpty()) {
                UtilityMethods.showError(notification, "Enter passed score");
                return;
            }

            promotionGradeId = selectedPromotionGrade.getLevelId();
        }

        String semesterId = resultSemester.getSemesterId();

        UtilityMethods.processingButton(generateReportBtn);
        UtilityMethods.showSuccess(notification, "Work while we process your request");

        downloadSemesterReport(staffId, levelId, semesterId, promotionGradeId, passedTotalScore);
    }

    @FXML
    public void handleClearResults() {
        classResultsTable.getItems().clear();
        subjectBreakdownTable.getItems().clear();

        resultsDisplayPanel.setVisible(false);
        resultsDisplayPanel.setManaged(false);

        results_SemesterSelectionBox.getSelectionModel().clearSelection();
        results_GradeSelectionBox.getSelectionModel().clearSelection();

        viewResultsFilterContainer.setVisible(true);
        viewResultsFilterContainer.setManaged(true);
    }

    @FXML
    public void handleResetPassword() {
        String newPassword = "";
        if (passwordResetBox.isVisible()) {
            newPassword = passwordField.getText().trim();
            passwordResetBox.setVisible(false);
            passwordResetBox.setManaged(false);

            staffDetailsPanel.setDisable(false);
        } else {
            staffDetailsPanel.setDisable(true);

            passwordResetBox.setVisible(true);
            passwordResetBox.setManaged(true);
            return;
        }

        String staffId = staffIdLabel.getText().trim();

        UtilityMethods.processingButton(resetPasswordBtn);

        resetStaffPassword(staffId, staffId, newPassword);
    }

    @FXML
    public void handleShowPassword() {
        if (passwordField.isVisible()) {

            newPasswordField.setText(passwordField.getText());
            newPasswordField.setVisible(true);
            newPasswordField.setManaged(true);

            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {

            passwordField.setText(newPasswordField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);

            newPasswordField.setVisible(false);
            newPasswordField.setManaged(false);
        }
    }

    @FXML
    public void handleCancelReset() {
        passwordResetBox.setVisible(false);
        passwordResetBox.setManaged(false);

        staffDetailsPanel.setDisable(false);
    }

    @FXML
    public void handleCancelUpdate() {
        staffDetailsPanel.setVisible(false);
        staffDetailsPanel.setManaged(false);

        staffListTableContainer.setVisible(true);
        staffListTableContainer.setManaged(true);
    }

    @FXML
    public void handleUpdateStaff() {
        if (!validateStaffUpdateForm()) return;

        UtilityMethods.processingButton(updateStaffInfoBtn);
        FindStaffResponse info = updateStaffDTO();

        apiUtilityClass.updateStaffInfo(info,
                () -> {
                    //On Success
                    Platform.runLater( () -> {
                        UtilityMethods.notProcessingButton(updateStaffInfoBtn, "SAVE CHANGES");
                        staffDetailsPanel.setVisible(false);
                        staffDetailsPanel.setManaged(false);

                        staffListTableContainer.setVisible(true);
                        staffListTableContainer.setManaged(true);

                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater( () -> {
                        UtilityMethods.notProcessingButton(updateStaffInfoBtn, "SAVE CHANGES");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater( () -> {
                        UtilityMethods.notProcessingButton(updateStaffInfoBtn, "SAVE CHANGES");
                        UtilityMethods.showError(notification, "Network Error! Check your internet and try again");
                    });
                });
    }

    @FXML
    public void backToLogsList() {
        viewLogDetailsPanel.setVisible(false);
        viewLogDetailsPanel.setManaged(false);

        logsSearchArea.setVisible(true);
        logsSearchArea.setManaged(true);
    }

    @FXML
    public void handleSaveAndClear() {
        if (!validateForm()) return;

        UtilityMethods.processingButton(saveAndClearBtn);
        saveStudent(() -> {
            // On Success
            Platform.runLater(() -> {
                clearForm();
                UtilityMethods.notProcessingButton(saveAndClearBtn, "ADD STUDENT");
                UtilityMethods.showSuccess(notification, "Student saved successfully! ID: " + apiUtilityClass.successMessageContainer);
            });
        }, () -> {
            // On Failure
            Platform.runLater(() -> {
                UtilityMethods.notProcessingButton(saveAndClearBtn, "ADD STUDENT");
                UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
            });
        }, () -> {
            // On Network Failure
            Platform.runLater(() -> {
                UtilityMethods.notProcessingButton(saveAndClearBtn, "ADD STUDENT");
                UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
            });
        });
    }

    @FXML
    public void handleClearAddStudentForm() {
        clearForm();
    }

    public void handleFetchLogs() {

    }

    @FXML
    public void showAddSemesterCard() {
        UtilityMethods.switchPageCards(addNewSemesterPanel, updateSemesterPanel,
                addSemesterToggleBtn, updateSemesterToggleBtn);
    }

    @FXML
    public void showUpdateSemesterInfoCard() {
        List<SemesterCaching> cachedSemesters = UtilityMethods.semesterCachingManager.getCachedSemesters();
        if (cachedSemesters != null && !cachedSemesters.isEmpty()) {
            if (semester_searchInfoBox.getItems().isEmpty()) {
                semester_searchInfoBox.getItems().addAll(cachedSemesters);
            }
        }

        updateSemesterSearchBox.setVisible(true);
        updateSemesterSearchBox.setManaged(true);

        updateSemesterResponsePanel.setVisible(false);
        updateSemesterResponsePanel.setManaged(false);

        UtilityMethods.switchPageCards(updateSemesterPanel, addNewSemesterPanel,
                updateSemesterToggleBtn, addSemesterToggleBtn);
    }

    @FXML
    public void handleAddSemester() {
        String semesterName = semesterNameField.getText().trim();
        if (!semesterName.contains("_")) {
            UtilityMethods.showError(notification, "Semester Name must contain hyphen eg. 'First_Term'");
            return;
        }

        LocalDate startDate = semesterStartDate.getValue();
        if (startDate == null || startDate.isBefore(LocalDate.now())) {
            UtilityMethods.showError(notification, "Start Date cannot be empty or before current date");
            return;
        }

        LocalDate endDate = semesterEndDate.getValue();
        if (endDate == null || endDate.isBefore(LocalDate.now())) {
            UtilityMethods.showError(notification, "End Date cannot be empty or before current date");
            return;
        }

        String academicYear = semesterAcademicYearField.getText().trim();
        if (academicYear.isEmpty()) {
            UtilityMethods.showError(notification, "Academic Year cannot be empty");
            return;
        }

        UtilityMethods.processingButton(addSemesterBtn);
        apiUtilityClass.addNewSemester(staffId, semesterName, startDate, endDate, academicYear,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(addSemesterBtn, "ADD SEMESTER");
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(addSemesterBtn, "ADD SEMESTER");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(addSemesterBtn, "ADD SEMESTER");
                        UtilityMethods.showError(notification, "Network Error! Check your connection and try again");
                    });
                });
    }

    @FXML
    public void handleFetchSemesterData() {
        SemesterCaching selectedSemester = semester_searchInfoBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            UtilityMethods.showError(notification, "Please select a Semester");
            return;
        }

        UtilityMethods.processingButton(update_searchSemesterBtn);
        apiUtilityClass.searchSemester(selectedSemester.getSemesterId(),
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(update_searchSemesterBtn, "FETCH DATA");
                        fillSemesterDataPanel();

                        UtilityMethods.showSuccess(notification, "Successfully fetched Semester Data");
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(update_searchSemesterBtn, "FETCH DATA");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(update_searchSemesterBtn, "FETCH DATA");
                        UtilityMethods.showError(notification, "Network Error! Check your connection and try again");
                    });
                });
    }

    @FXML
    public void handleCancelSemesterUpdate() {
        showUpdateSemesterInfoCard();
    }

    @FXML
    public void handleUpdateSemester() {
        FindSemester info =  new FindSemester();
        info.setSemesterID(updateSemesterIdLabel.getText());
        info.setSemesterName(updateSemesterNameField.getText().trim());
        info.setSemesterStartDate(updateSemesterStartDate.getValue().toString());
        info.setSemesterEndDate(updateSemesterEndDate.getValue().toString());
        info.setAcademicYear(updateSemesterAcademicYearField.getText().trim());
        apiUtilityClass.updateSemesterInfo(info,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(updateSemesterBtn, "SAVE CHANGES");
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(updateSemesterBtn, "SAVE CHANGES");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(updateSemesterBtn, "SAVE CHANGES");
                        UtilityMethods.showError(notification, "Network Error! Check your connection and try again");
                    });
                });
    }


    /** ---------------UTILITY METHODS FOR THIS CLASS--------------- **/

    public void initializeAfterLogin() {
        apiUtilityClass = new ApiUtilityClass(this);
        setActiveButton(dashboardBtn);
        loadStaffInfo();
        showLoading();
        apiUtilityClass.loadAdminDashboard(staffId, notification,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        loadDashboardData();

                        hideLoading();
                    });
                });
    }

    private void fillSemesterDataPanel() {
        updateSemesterIdLabel.setText(semester.getSemesterID());
        updateSemesterNameField.setText(semester.getSemesterName());
        updateSemesterStartDate.setValue(LocalDate.parse(semester.getSemesterStartDate()));
        updateSemesterEndDate.setValue(LocalDate.parse(semester.getSemesterEndDate()));
        updateSemesterAcademicYearField.setText(semester.getAcademicYear());

        updateSemesterSearchBox.setVisible(false);
        updateSemesterSearchBox.setManaged(false);

        updateSemesterResponsePanel.setVisible(true);
        updateSemesterResponsePanel.setManaged(true);
    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        genderComboBox.getSelectionModel().clearSelection();
        dateOfBirthPicker.setValue(null);
        homeTownField.clear();
        parentNameField.clear();
        parentPhoneField.clear();
        classComboBox.getSelectionModel().clearSelection();
    }

    private void saveStudent(Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        // Get values
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String gender = genderComboBox.getSelectionModel().getSelectedItem().toString();
        LocalDate dob = dateOfBirthPicker.getValue();
        String homeTown = homeTownField.getText().trim();
        String parentName = parentNameField.getText().trim();
        String parentPhone = parentPhoneField.getText().trim();

        GradeCaching selectedClass = classComboBox.getSelectionModel().getSelectedItem();
        String selectedGradeId;
        if (selectedClass == null) {
            UtilityMethods.showError(notification, "No grade selected!");
            return;
        }
        selectedGradeId = selectedClass.getLevelId();

        apiUtilityClass.enrollNewStudent(staffId, firstName, lastName, selectedGradeId, gender,
                dob, homeTown, parentName, parentPhone, onSuccess, onFailure, onNetworkIssue);
    }

    private boolean validateForm() {
        if (firstNameField.getText().trim().isEmpty()) {
            UtilityMethods.showError(notification, "First name is required");
            return false;
        }
        if (lastNameField.getText().trim().isEmpty()) {
            UtilityMethods.showError(notification, "Last name is required");
            return false;
        }
        if (genderComboBox.getValue() == null) {
            UtilityMethods.showError(notification, "Please select a gender");
            return false;
        }
        if (dateOfBirthPicker.getValue() == null) {
            UtilityMethods.showError(notification, "Date of birth is required");
            return false;
        }
        if (parentNameField.getText().trim().isEmpty()) {
            UtilityMethods.showError(notification, "Parent name is required");
            return false;
        }
        if (parentPhoneField.getText().trim().isEmpty()) {
            UtilityMethods.showError(notification, "Parent phone number is required");
            return false;
        }
        if (classComboBox.getValue() == null) {
            UtilityMethods.showError(notification, "Please select a class");
            return false;
        }
        return true;
    }

    private FindStaffResponse updateStaffDTO() {
        FindStaffResponse staff = new FindStaffResponse();

        staff.setStaffId(staffIdLabel.getText());
        staff.setFirstName(editFirstNameField.getText().trim());
        staff.setSurname(editLastNameField.getText().trim());

        staff.setEmail(editEmailField.getText().trim());
        staff.setPhoneNumber(editPhoneField.getText().trim());

        staff.setDateOfBirth(String.valueOf(editDateOfBirthPicker.getValue()));
        staff.setGender(editGenderComboBox.getSelectionModel().getSelectedItem().name());


        staff.setStaffRoles(getStaffRoles());
        staff.setStaffStatus(editStatusComboBox.getSelectionModel().getSelectedItem().toString());

        return staff;
    }

    private List<String> getStaffRoles() {
        List<String> selectedRoles = new ArrayList<>();
        if (role1.isSelected()) {
            selectedRoles.add(StaffRoles.PRINCIPAL.name());
        }

        if (role2.isSelected()) {
            selectedRoles.add(StaffRoles.ACCOUNTANT.name());
        }

        if (role3.isSelected()) {
            selectedRoles.add(StaffRoles.TEACHING_STAFF.name());
        }

        if (role4.isSelected()) {
            selectedRoles.add(StaffRoles.GENERAL_STAFF.name());
        }

        if (role5.isSelected()) {
            selectedRoles.add(StaffRoles.ADMINISTRATOR.name());
        }

        return selectedRoles;
    }

    private void resetStaffPassword(String staffId, String newPasswordStaffId, String newPassword) {
        apiUtilityClass.recoverPassword(staffId, newPasswordStaffId, newPassword,
                () -> {
                    //On Success
                    Platform.runLater( () -> {
                        UtilityMethods.notProcessingButton(resetPasswordBtn, "RESET PASSWORD");
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater( () -> {
                        UtilityMethods.notProcessingButton(resetPasswordBtn, "RESET PASSWORD");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                }, () -> {
                    //On Network Issue
                    Platform.runLater( () -> {
                        UtilityMethods.notProcessingButton(resetPasswordBtn, "Recover password");
                        UtilityMethods.showError(notification, "Network error! Check your internet and try again");
                    });
                });
    }

    private boolean validateStaffUpdateForm() {
        if (editFirstNameField.getText().isEmpty()) {
            UtilityMethods.showError(notification, "Please enter first name");
            return false;
        }

        if (editLastNameField.getText().isEmpty()) {
            UtilityMethods.showError(notification, "Please enter last name");
            return false;
        }

        if (editEmailField.getText().isEmpty() || !editEmailField.getText().endsWith(".com")) {
            UtilityMethods.showError(notification, "Please enter valid email");
            return false;
        }

        if (editPhoneField.getText().isEmpty() || editPhoneField.getText().length() != 10) {
            UtilityMethods.showError(notification, "Please enter valid phone number");
            return false;
        }

        if (editDateOfBirthPicker.getValue() == null) {
            UtilityMethods.showError(notification, "Please select date of birth");
            return false;
        }

        if (editGenderComboBox.getSelectionModel().getSelectedItem() == null) {
            UtilityMethods.showError(notification, "Please select gender");
            return false;
        }

        if (!role1.isSelected()) {
            if (!role2.isSelected()) {
                if (!role3.isSelected()) {
                    if (!role4.isSelected()) {
                        if (!role5.isSelected()) {
                            UtilityMethods.showError(notification, "Please select at least one role");
                            return false;
                        }
                    }
                }
            }
        }

        if (editStatusComboBox.getSelectionModel().getSelectedItem() == null) {
            UtilityMethods.showError(notification, "Please select status");
            return false;
        }

        return true;
    }

    private void downloadSemesterReport(String staffId, String levelId, String semesterId, String promotionGradeId, String passScore) {
        apiUtilityClass.downloadStudentsReport(staffId, levelId, semesterId, promotionGradeId, passScore,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(generateReportBtn, "DOWNLOAD TERM REPORTS");
                        UtilityMethods.showSuccess(notification, "Results download successful.");
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(generateReportBtn, "DOWNLOAD TERM REPORTS");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(generateReportBtn, "DOWNLOAD SEMESTER REPORT");
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    private void loadClassResults(String levelId, String semesterId) {
        apiUtilityClass.loadClassResults(levelId, semesterId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(loadClassResultsBtn, "FETCH RESULTS");
                        populateClassResults(UtilityMethods.classResults);
                        UtilityMethods.showSuccess(notification, "Results successfully loaded");
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(loadClassResultsBtn, "FETCH RESULTS");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(loadClassResultsBtn, "FETCH RESULTS");
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    private void populateClassResults(List<ViewClassSemesterReport> viewClassSemesterReports) {
        gradeNameLabel.setText(viewClassSemesterReports.getFirst().getClassName());
        semesterNameLabel.setText(viewClassSemesterReports.getFirst().getSemesterName());

        classResultsTable.getItems().setAll(viewClassSemesterReports);

        viewResultsFilterContainer.setVisible(false);
        viewResultsFilterContainer.setManaged(false);

        resultsDisplayPanel.setVisible(true);
        resultsDisplayPanel.setManaged(true);
    }

    private void updateSubjectDetails(SubjectDTO subject) {
        apiUtilityClass.updateSubjectDetails(subject,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(updateSubjectBtn, "SAVE CHANGES");

                        subjectSearchResponsePanel.setVisible(false);
                        subjectSearchResponsePanel.setManaged(false);

                        subjectSearchBox.setVisible(true);
                        subjectSearchBox.setManaged(true);

                        UtilityMethods.showSuccess(notification, "Successfully updated subject data");
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(updateSubjectBtn, "SAVE CHANGES");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(updateSubjectBtn, "SAVE CHANGES");
                        UtilityMethods.showError(notification, "Network error! Check your network connection and try again!");
                    });
                });
    }

    private void loadSubjectData(SubjectDTO data) {
        subjectIdLabel.setText(data.getSubjectId());
        subject_updateNameField.setText(data.getSubjectName());

        List<GradeCaching> cachedGrades = UtilityMethods.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (subject_updateGradeBox.getItems().isEmpty()) {
                subject_updateGradeBox.getItems().addAll(cachedGrades);
            }

            for (GradeCaching grade : cachedGrades) {
                if (grade.getLevelId().equals(data.getLevelId())) {
                    subject_updateGradeBox.getSelectionModel().select(grade);
                    break;
                }
            }
        }
    }

    private void uploadNextScore(String scoreInfo) {
        if (
                currentUploadIndex >= UtilityMethods.subjectScores.getScoresTable().size()
        ) {
            UtilityMethods.notProcessingButton(saveScoresBtn, "UPLOAD SCORES");

            score_SaveProgressBar.setProgress(0.0);
            scores_SaveProgressBarVBox.setVisible(false);
            scores_SaveProgressBarVBox.setManaged(false);

            currentUploadIndex = 0;
            UtilityMethods.subjectScores = null;

            resultsTableContainer.setVisible(false);
            resultsTableContainer.setManaged(false);

            subjectResultSearchBox.setVisible(true);
            subjectResultSearchBox.setManaged(true);

            return;
        }

        StudentsScoresTable student =
                UtilityMethods.subjectScores.getScoresTable().get(currentUploadIndex);

        apiUtilityClass.saveStudentScores(
                student, scoreInfo,
                () -> {
                    // On Success
                    Platform.runLater(() -> {
                        currentUploadIndex++;

                        double progress =
                                (double)  currentUploadIndex / UtilityMethods.subjectScores.getScoresTable().size();

                        score_SaveProgressBar.setProgress(progress);

                        uploadNextScore(scoreInfo);
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(saveScoresBtn, "CONTINUE");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(saveScoresBtn, "CONTINUE");
                        UtilityMethods.showError(notification,
                                "Network Error! Fix connection and click Continue.");
                    });
                }
        );
    }

    private void displaySubjectStudents(SubjectScores subjectScores) {
        subjectNameLabel.setText(subjectScores.getSubjectName());
        subjectCodeLabel.setText(subjectScores.getSubjectId());

        List<StudentsScoresTable> scores = subjectScores.getScoresTable();
        if (scores != null && !scores.isEmpty()) {
            studentsScoresTable.getItems().addAll(scores);
        }

        List<SemesterCaching> cachedSemesters = UtilityMethods.semesterCachingManager.getCachedSemesters();
        if (cachedSemesters != null && !cachedSemesters.isEmpty()) {
            if (saveScoresSemesterComboBox.getItems().isEmpty()) {
                saveScoresSemesterComboBox.getItems().addAll(cachedSemesters);
            }
        }

        resultsTableContainer.setVisible(true);
        resultsTableContainer.setManaged(true);
    }

    private boolean validateGradeCriteriaForm(String gradeCriteria, Double lowerRange, Double upperRange, String remarks) {
        if (gradeCriteria.isEmpty()) {
            UtilityMethods.showError(notification, "Grade criteria is required");
            return false;
        }

        if (lowerRange.isNaN() || upperRange.isNaN()) {
            UtilityMethods.showError(notification, "Please enter grade criteria marks");
            return false;
        }

        if (lowerRange > upperRange) {
            UtilityMethods.showError(notification, "Lower range cannot be greater than upper range");
            return false;
        }

        if (remarks.isEmpty()) {
            UtilityMethods.showError(notification, "Remarks is required");
            return false;
        }

        return true;
    }

    private UpdateStudentPersonalData getUpdatedInfo() {
        UpdateStudentPersonalData update = new UpdateStudentPersonalData();
        update.setStudentId(studentIdLabel.getText());
        update.setFirstName(updateFirstNameField.getText());
        update.setLastName(updateLastNameField.getText());
        update.setGender(updateGenderComboBox.getSelectionModel().getSelectedItem().toString());
        update.setDateOfBirth(updateDateOfBirthPicker.getValue().toString());
        update.setHomeTown(updateHomeTownField.getText().trim());
        update.setParentName(updateParentNameField.getText().trim());
        update.setParentPhoneNumber(updateParentPhoneField.getText().trim());
        update.setGradeId(updateClassComboBox.getSelectionModel().getSelectedItem().getLevelId());
        update.setStatus(updateStatusComboBox.getSelectionModel().getSelectedItem().toString());

        return update;
    }

    private boolean validateUpdateForm() {
        if (updateFirstNameField.getText().trim().isEmpty()) {
            UtilityMethods.showError(notification, "First name is required");
            return false;
        }
        if (updateLastNameField.getText().trim().isEmpty()) {
            UtilityMethods.showError(notification, "Last name is required");
            return false;
        }
        if (updateGenderComboBox.getValue() == null) {
            UtilityMethods.showError(notification, "Please select a gender");
            return false;
        }
        if (updateDateOfBirthPicker.getValue() == null) {
            UtilityMethods.showError(notification, "Date of birth is required");
            return false;
        }
        if (updateHomeTownField.getText().trim().isEmpty()) {
            UtilityMethods.showError(notification, "Hometown is required");
            return false;
        }
        if (updateParentNameField.getText().trim().isEmpty()) {
            UtilityMethods.showError(notification, "Parent name is required");
            return false;
        }
        if (updateParentPhoneField.getText().trim().isEmpty()) {
            UtilityMethods.showError(notification, "Parent phone number is required");
            return false;
        }
        if (updateClassComboBox.getValue() == null) {
            UtilityMethods.showError(notification, "Please select a class");
            return false;
        }
        return true;
    }

    private void studentInfoUpdatePanel(UpdateStudentPersonalData data) {

        List<GradeCaching> cachedGrades  = UtilityMethods.gradeCacheManager.getGradesCache();
        GradeCaching updateGrade = null;
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            for (GradeCaching grade : cachedGrades) {
                if (grade.getLevelId().equals(data.getGradeId())) {
                    updateGrade = grade;
                    break;
                }
            }
        }


        //Populate update panel
        studentIdLabel.setText(data.getStudentId());
        studentStatusLabel.setText(data.getStatus());

        if (data.getStatus().equals("ACTIVE")) {
            studentStatusLabel.setTextFill(Color.web("#16A34A")); // green

        } else if (data.getStatus().equals("COMPLETED")) {
            studentStatusLabel.setTextFill(Color.web("#38BDF8")); // light blue

        } else {
            studentStatusLabel.setTextFill(Color.web("#DC2626")); // red
        }

        updateGradeNameLabel.setText(updateGrade.getLevelName() == null ? "" : updateGrade.getLevelName());


        updateFirstNameField.setText(data.getFirstName());
        updateLastNameField.setText(data.getLastName());

        if (updateGenderComboBox.getItems().isEmpty()) {
            updateGenderComboBox.getItems().addAll(Gender.values());
        }
        updateGenderComboBox.getSelectionModel().select(Gender.valueOf(data.getGender()));
        updateDateOfBirthPicker.setValue(LocalDate.parse(data.getDateOfBirth()));

        updateHomeTownField.setText(data.getHomeTown());
        updateParentNameField.setText(data.getParentName());
        updateParentPhoneField.setText(data.getParentPhoneNumber());

        updateClassComboBox.getSelectionModel().select(updateGrade);


        if (updateStatusComboBox.getItems().isEmpty()) {
            updateStatusComboBox.getItems().addAll(StudentStatus.values());
        }
        updateStatusComboBox.getSelectionModel().select(StudentStatus.valueOf(data.getStatus()));

        studentSearchFieldBox.setVisible(false);
        studentSearchFieldBox.setManaged(false);

        studentInfoPanel.setVisible(true);
        studentInfoPanel.setManaged(true);
    }

    private void populateClassInfoArea() {
        List<StaffCaching> cachedStaffs = UtilityMethods.staffCacheManager.getCachedStaffList();
        if (cachedStaffs != null && !cachedStaffs.isEmpty()) {
            if (updateClassInfoStaffComboBox.getItems().isEmpty()) {
                updateClassInfoStaffComboBox.getItems().addAll(cachedStaffs);
            }

            for (StaffCaching staffCaching : cachedStaffs) {
                if (staffCaching.getStaffId().equals(UtilityMethods.findAndUpdateClassInfo.getStaffId())) {
                    updateClassInfoStaffComboBox.getSelectionModel().select(staffCaching);
                    break;
                }
            }
        }

        updateClassInfoNameField.setText(UtilityMethods.findAndUpdateClassInfo.getLevelName());
        updateClassInfoDisplayArea.setVisible(true);
        updateClassInfoDisplayArea.setManaged(true);
    }

    private void loadDashboardData() {
        List<LogsDetails> cachedLogs = UtilityMethods.systemLogsCacheManager.getCachedSystemLogs();
        if (cachedLogs != null) {
            if (recentActivitiesTable.getItems().isEmpty()) {
                recentActivitiesTable.getItems().addAll(cachedLogs);
            }
        }
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

        manageClassView.setVisible(false);
        manageClassView.setManaged(false);

        manageSemesterView.setVisible(false);
        manageSemesterView.setManaged(false);

        gradingCriteriaView.setVisible(false);
        gradingCriteriaView.setManaged(false);

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
        gradeCriteriaNavBtn.setStyle(INACTIVE_BUTTON);
        scoresNavBtn.setStyle(INACTIVE_BUTTON);
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
        editGenderComboBox.setValue(Gender.valueOf(data.getGender().toUpperCase()));

        getStaffRoles(data.getStaffRoles());
        setRoleCheckBoxes();

        editStatusComboBox.getItems().addAll(StaffStatus.values());
        editStatusComboBox.setValue(StaffStatus.valueOf(data.getStaffStatus()));
    }

    private void getStaffRoles(List<String> roles) {
        for (String role : roles) {
            UtilityMethods.staffRoles.add(StaffRoles.valueOf(role));
        }
    }

    private void setRoleCheckBoxes() {
        for (StaffRoles staffRole : UtilityMethods.staffRoles) {
            if (staffRole.equals(StaffRoles.PRINCIPAL)) {
                role1.setSelected(true);
            } else {
                role1.setSelected(false);
            }

            if (staffRole.equals(StaffRoles.ACCOUNTANT)) {
                role2.setSelected(true);
            } else {
                role2.setSelected(false);
            }

            if (staffRole.equals(StaffRoles.TEACHING_STAFF)) {
                role3.setSelected(true);
            } else {
                role3.setSelected(false);
            }

            if (staffRole.equals(StaffRoles.GENERAL_STAFF)) {
                role4.setSelected(true);
            } else {
                role4.setSelected(false);
            }

            if (staffRole.equals(StaffRoles.ADMINISTRATOR)){
                role5.setSelected(true);
            } else {
                role5.setSelected(false);
            }
        }
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
