package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.Constants.Gender;
import com.bbquantum.eazymanage.Constants.StaffRoles;
import com.bbquantum.eazymanage.Constants.StaffStatus;
import com.bbquantum.eazymanage.Constants.StudentStatus;
import com.bbquantum.eazymanage.DTO.Academics.*;
import com.bbquantum.eazymanage.DTO.Institution.*;
import com.bbquantum.eazymanage.Services.*;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
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
import javafx.stage.Window;
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
    //
    // Phase 8d — services referenced directly. ApiContext is the shared
    // error/success store; the ApiUtilityClass facade is no longer
    // constructed by any controller.
    private final ApiCore apiCore          = new ApiCore();
    private final AcademicsService academicsService = new AcademicsService(apiCore);
    private final AuthService authService      = new AuthService(apiCore);
    private final DashboardService dashboardService = new DashboardService(apiCore);
    private final StaffService staffService     = new StaffService(apiCore);
    private final StudentService   studentService   = new StudentService(apiCore);
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
    @FXML private Button switchRole;

    //MAIN CONTENT
    @FXML private StackPane contentArea;

    //PAGE HEADERS
    @FXML private HBox dashboardHeader;
    @FXML private HBox manageClassHeader;
    @FXML private HBox manageStudentHeader;
    @FXML private HBox gradingCriteriaHeader;
    @FXML private HBox subjectScoresHeader;
    @FXML private HBox manageSubjectHeader;
    @FXML private HBox manageReportsHeader;
    @FXML private HBox manageStaffHeader;
    @FXML private HBox systemLogsHeader;
    @FXML private HBox manageSemesterHeader;

    //PAGE HEADER CONTROLLERS — for wiring the refresh button
    @FXML private RefreshController dashboardHeaderController;
    @FXML private RefreshController manageClassHeaderController;
    @FXML private RefreshController manageStudentHeaderController;
    @FXML private RefreshController gradingCriteriaHeaderController;
    @FXML private RefreshController subjectScoresHeaderController;
    @FXML private RefreshController manageSubjectHeaderController;
    @FXML private RefreshController manageReportsHeaderController;
    @FXML private RefreshController manageStaffHeaderController;
    @FXML private RefreshController systemLogsHeaderController;
    @FXML private RefreshController manageSemesterHeaderController;

    //DASHBOARD VIEW
    @FXML private ScrollPane dashboardView;
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
    @FXML private TableColumn<StudentsScoresTable, String> classTest1Column;
    @FXML private TableColumn<StudentsScoresTable, String> groupWorkColumn;
    @FXML private TableColumn<StudentsScoresTable, String> classTest2Column;
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
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> classTest1Col;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> groupWorkCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> classTest2Col;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> projectScoreCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> examScoreCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> calculatedExamScoreCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> totalCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> gradeCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> remarkCol;

    @FXML private VBox resultsPassMarkBox;
    @FXML private TextField passScoreField;
    @FXML private ComboBox<GradeCaching> promotionGradeComboBox;
    @FXML
    public Button generateReportBtn;

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
                new SimpleStringProperty(cellData.getValue().getStatus()));


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
        classTest1Column.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClassTest1Score()));

        groupWorkColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getGroupWorkScore()));

        classTest2Column.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClassTest2Score()));

        projectColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProjectScore()));

        examScoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getExamScore()));

        //Uneditable columns
        studentIdColumn.setEditable(false);
        studentNameColumn.setEditable(false);
        classScoreColumn.setEditable(false);
        calExamScoreColumn.setEditable(false);

        classTest1Column.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        groupWorkColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        classTest2Column.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        projectColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        examScoreColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));

        classTest1Column.setEditable(true);
        groupWorkColumn.setEditable(true);
        classTest2Column.setEditable(true);
        projectColumn.setEditable(true);
        examScoreColumn.setEditable(true);
        studentsScoresTable.setEditable(true);
        studentsScoresTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        classTest1Column.setOnEditCommit(event -> {
            if (!event.getNewValue().matches("\\d+(\\.\\d+)?")) {
                UtilityClass.showError(notification, "Only numbers allowed");
                studentsScoresTable.refresh();
                return;
            }

            if (Double.parseDouble(event.getNewValue()) > 20) {
                UtilityClass.showError(notification, "Class Test 1 shouldn't be more than 20");
                studentsScoresTable.refresh();
                return;
            }

            StudentsScoresTable row = event.getRowValue();
            row.setClassTest1Score(event.getNewValue());
            recalculateClassScore(row);
            studentsScoresTable.refresh();
        });

        groupWorkColumn.setOnEditCommit(event -> {
            if (!event.getNewValue().matches("\\d+(\\.\\d+)?")) {
                UtilityClass.showError(notification, "Only numbers allowed");
                studentsScoresTable.refresh();
                return;
            }

            if (Double.parseDouble(event.getNewValue()) > 30) {
                UtilityClass.showError(notification, "Group Work shouldn't be more than 30");
                studentsScoresTable.refresh();
                return;
            }

            StudentsScoresTable row = event.getRowValue();
            row.setGroupWorkScore(event.getNewValue());
            recalculateClassScore(row);
            studentsScoresTable.refresh();
        });

        classTest2Column.setOnEditCommit(event -> {
            if (!event.getNewValue().matches("\\d+(\\.\\d+)?")) {
                UtilityClass.showError(notification, "Only numbers allowed");
                studentsScoresTable.refresh();
                return;
            }

            if (Double.parseDouble(event.getNewValue()) > 20) {
                UtilityClass.showError(notification, "Class Test 2 shouldn't be more than 20");
                studentsScoresTable.refresh();
                return;
            }

            StudentsScoresTable row = event.getRowValue();
            row.setClassTest2Score(event.getNewValue());
            recalculateClassScore(row);
            studentsScoresTable.refresh();
        });

        projectColumn.setOnEditCommit(event -> {
            if (!event.getNewValue().matches("\\d+(\\.\\d+)?")) {
                UtilityClass.showError(notification, "Only numbers allowed");
                studentsScoresTable.refresh();
                return;
            }

            if (Double.parseDouble(event.getNewValue()) > 30) {
                UtilityClass.showError(notification, "Project score shouldn't be more than 30");
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
                UtilityClass.showError(notification, "Only numbers allowed");
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

        classTest1Col.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClassTest1Score()));

        groupWorkCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getGroupWorkScore()));

        classTest2Col.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClassTest2Score()));

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
                new SimpleStringProperty(cellData.getValue().getStatus()));


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
        setHeaderTitle(manageStudentHeader, "MANAGE STUDENTS");

        switchView(studentNavBtn, manageStudentView);
        showAddStudentCard();
    }

    @FXML
    public void showGradingCriteria() {
        setHeaderTitle(gradingCriteriaHeader, "MANAGE GRADING SYSTEM");

        switchView(gradeCriteriaNavBtn, gradingCriteriaView);
    }

    @FXML
    public void showManageScores() {


        List<GradeCaching> cachedGrades = UtilityClass.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (score_SelectClassBox.getItems().isEmpty()) {
                score_SelectClassBox.getItems().addAll(cachedGrades);
            }
        }
        setHeaderTitle(subjectScoresHeader, "MANAGE SCORES");

        switchView(scoresNavBtn, subjectScoresView);
    }

    @FXML
    public void showManageSemester() {
        setHeaderTitle(manageSemesterHeader, "MANAGE SEMESTERS");
        switchView(dashboardBtn, manageSemesterView);

        showAddSemesterCard();
    }

    public void showManageClasses() {
        setHeaderTitle(manageClassHeader, "MANAGE CLASSES");
        switchView(dashboardBtn, manageClassView);

        showAddClassCard();
    }

    @FXML
    public void showManageSubjects() {
        setHeaderTitle(manageSubjectHeader, "MANAGE SUBJECTS");
        switchView(dashboardBtn, manageSubjectView);

        showAddSubjectCard();
    }

    @FXML
    public void showManageResults() {
        List<GradeCaching> cachedGrades = UtilityClass.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (results_GradeSelectionBox.getItems().isEmpty()) {
                results_GradeSelectionBox.getItems().addAll(cachedGrades);
            }
        }

        List<SemesterCaching> cachedSemesters = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (cachedSemesters != null && !cachedSemesters.isEmpty()) {
            if (results_SemesterSelectionBox.getItems().isEmpty()) {
                results_SemesterSelectionBox.getItems().addAll(cachedSemesters);
            }
        }

        resultsDisplayPanel.setVisible(false);
        resultsDisplayPanel.setManaged(false);

        setHeaderTitle(manageReportsHeader, "MANAGE RESULTS");
        switchView(reportsNavBtn, manageReportsView);
    }

    @FXML
    public void showManageStaffs() {
        staffDetailsPanel.setVisible(false);
        staffDetailsPanel.setManaged(false);

        List<ViewStaffList> cachedStaffList = UtilityClass.staffListCacheManager.getCachedStaffList();
        if (cachedStaffList != null && !cachedStaffList.isEmpty()) {
            if (staffListTable.getItems().isEmpty()) {
                staffListTable.getItems().addAll(cachedStaffList);
            }
        }

        setHeaderTitle(manageStaffHeader, "MANAGE STAFFS");
        switchView(staffNavButton, manageStaffView);
    }

    @FXML
    public void showSystemLogs() {
        viewLogDetailsPanel.setVisible(false);
        viewLogDetailsPanel.setManaged(false);

        activitiesListPanel.setVisible(true);
        activitiesListPanel.setManaged(true);

        List<StaffCaching> cachedStaffList = UtilityClass.staffCacheManager.getCachedStaffList();
        if (cachedStaffList != null && !cachedStaffList.isEmpty()) {
            if (filterStaffBox.getItems().isEmpty()) {
                filterStaffBox.getItems().addAll(cachedStaffList);
            }
        }

        setHeaderTitle(systemLogsHeader, "VIEW SYSTEM LOGS");
        switchView(logsNavBtn, systemLogsView);
    }

    @FXML
    public void handleOpenSwitchRole() throws IOException {

        Stage adminDashboard = (Stage) switchRole.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/SwitchRole/SwitchRole.fxml"));
        Scene scene = new Scene(loader.load());

        // Create new stage for dashboard
        Stage switchRoleStage = new Stage();
        switchRoleStage.setTitle("EazyManage - User Roles");
        switchRoleStage.setScene(scene);
        switchRoleStage.sizeToScene();
        switchRoleStage.setResizable(false);

        adminDashboard.close();
        switchRoleStage.show();
    }

    @FXML
    public void refreshDashboard() {
        showLoading();
        dashboardService.loadAdminDashboard(staffId, notification,
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
        List<StaffCaching> cachedStaffList = UtilityClass.staffCacheManager.getCachedStaffList();
        if (cachedStaffList != null && !cachedStaffList.isEmpty()) {
            if (addClassStaffComboBox.getItems().isEmpty()) {
                addClassStaffComboBox.getItems().addAll(cachedStaffList);
            }
        }

        addClassNameField.clear();
        addClassStaffComboBox.getSelectionModel().clearSelection();

        UtilityClass.switchPageCards(addClassCard, updateClassInfoCard,
                addClassToggleBtn, updateClassInfoToggleBtn);
    }

    @FXML
    public void showUpdateClassInfoCard() {
        List<GradeCaching> cachedGrades = UtilityClass.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (updateClassSearchBox.getItems().isEmpty()) {
                updateClassSearchBox.getItems().addAll(cachedGrades);
            }
        }

        updateClassInfoDisplayArea.setVisible(false);
        updateClassInfoDisplayArea.setManaged(false);

        updateClassSearchArea.setVisible(true);
        updateClassSearchArea.setManaged(true);

        UtilityClass.switchPageCards(updateClassInfoCard, addClassCard,
                updateClassInfoToggleBtn, addClassToggleBtn);
    }

    @FXML
    public void handleAddClass() {
        String className = addClassNameField.getText().trim();
        if (className.isEmpty()) {
            UtilityClass.showError(notification, "Class name is required");
            return;
        }

        if (!className.contains("_") || className.contains(" ")) {
            UtilityClass.showError(notification, "Class name must contain '_' eg.Grade_1");
            return;
        }

        StaffCaching staffCaching = addClassStaffComboBox.getSelectionModel().getSelectedItem();
        if (staffCaching == null) {
            UtilityClass.showError(notification, "Please select a staff cache");
            return;
        }

        String instructorId = staffCaching.getStaffId();
        UtilityClass.processingButton(saveClassBtn);

        academicsService.addNewGrade(staffId, className, instructorId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(saveClassBtn, "ADD CLASS");
                        UtilityClass.showSuccess(notification, ApiContext.getInstance().successMessageContainer);
                    });
                }, () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(saveClassBtn, "ADD CLASS");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                }, () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(saveClassBtn, "ADD CLASS");
                        UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleFindClass() {
        GradeCaching selectedGrade = updateClassSearchBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            UtilityClass.showError(notification, "Please select a grade from the grades list");
            return;
        }

        String levelId = selectedGrade.getLevelId();
        UtilityClass.processingButton(searchClassBtn);

        academicsService.findClassInfo(levelId, staffId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        populateClassInfoArea();

                        updateClassSearchArea.setVisible(false);
                        updateClassSearchArea.setManaged(false);
                        UtilityClass.notProcessingButton(searchClassBtn, "FETCH GRADE INFO");
                        UtilityClass.showSuccess(notification, "Class information found");
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(searchClassBtn, "FETCH GRADE INFO");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(searchClassBtn, "FETCH GRADE INFO");
                        UtilityClass.showError(notification, "Network error! Check your connection and try again");
                    });
                });
    }

    @FXML
    public void handleUpdateClassInfo() {
        String newClassName = updateClassInfoNameField.getText().trim();
        if (newClassName.isEmpty()) {
            UtilityClass.showError(notification, "Please enter class name");
            return;
        }

        if (!newClassName.contains("_") || newClassName.contains(" ")) {
            UtilityClass.showError(notification, "Class name must contain '_' eg.Grade_1");
            return;
        }

        StaffCaching staffCaching = updateClassInfoStaffComboBox.getSelectionModel().getSelectedItem();
        if (staffCaching == null) {
            UtilityClass.showError(notification, "Please select a staff");
            return;
        }

        UtilityClass.processingButton(updateClassInfoBtn);
        UtilityClass.findAndUpdateClassInfo.setLevelName(newClassName);
        UtilityClass.findAndUpdateClassInfo.setStaffId(staffCaching.getStaffId());

        academicsService.updateClassInfo(UtilityClass.findAndUpdateClassInfo, staffId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(updateClassInfoBtn, "SAVE CHANGES");
                        updateClassInfoDisplayArea.setVisible(false);
                        updateClassInfoDisplayArea.setManaged(false);

                        updateClassSearchArea.setVisible(true);
                        updateClassSearchArea.setManaged(true);
                        UtilityClass.showSuccess(notification, ApiContext.getInstance().successMessageContainer);
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(updateClassInfoBtn, "SAVE CHANGES");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(updateClassInfoBtn, "SAVE CHANGES");
                        UtilityClass.showError(notification, "Network Error! Check your connection and try again");
                    });
                });
    }

    @FXML
    public void showAddStudentCard() {
        List<GradeCaching> cachedGrades = UtilityClass.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (classComboBox.getItems().isEmpty()) {
                classComboBox.getItems().addAll(cachedGrades);
            }
        }

        if (genderComboBox.getItems().isEmpty()) {
            genderComboBox.getItems().addAll(Gender.values());
        }

        UtilityClass.switchPageCards(addStudentCard, updateStudentInfoCard,
                addStudentToggleBtn, updateStudentToggleBtn);
    }

    @FXML
    public void showUpdateStudentInfoCard() {
        List<GradeCaching> cachedGrades = UtilityClass.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (student_classSelectionBox.getItems().isEmpty()) {
                student_classSelectionBox.getItems().addAll(cachedGrades);
            }
        }

        student_SelectionBox.getSelectionModel().clearSelection();

        student_classSelectionBox.getSelectionModel().clearSelection();

        studentInfoPanel.setVisible(false);
        studentInfoPanel.setManaged(false);

        studentSearchFieldBox.setVisible(true);
        studentSearchFieldBox.setManaged(true);

        UtilityClass.switchPageCards(updateStudentInfoCard, addStudentCard,
                updateStudentToggleBtn, addStudentToggleBtn);
    }

    @FXML
    public void LoadClassStudent() {
        GradeCaching selectedGrade = student_classSelectionBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            return;
        }

        String levelId = selectedGrade.getLevelId();
        showLoading();
        academicsService.loadClassStudents(levelId, staffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        student_SelectionBox.getItems().clear();
                        student_SelectionBox.getItems().addAll(UtilityClass.studentsHolder);
                        hideLoading();
                        UtilityClass.showSuccess(notification, "Students Fetched. You can select student now");
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        hideLoading();
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
            //On Network Failure
                    Platform.runLater(() -> {
                        hideLoading();
                        UtilityClass.showError(notification, "Network error! Check your connection and try again");
                    });
                });
    }

    @FXML
    public void handleFindStudentData() {
        if (student_SelectionBox.isDisable()) {
            UtilityClass.showError(notification, "Please select a class to fetch Students");
            return;
        }

        StudentsHolder student = student_SelectionBox.getSelectionModel().getSelectedItem();
        if (student == null) {
            UtilityClass.showError(notification, "Please select student");
            return;
        }

        String studentId = student.getStudentId();
        UtilityClass.processingButton(findStudentBtn);

        studentService.searchStudentData(staffId, studentId,
                () -> {
                    // On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(findStudentBtn, "FETCH DETAILS");
                        studentInfoUpdatePanel(UtilityClass.updateStudentPersonalData);
                        UtilityClass.showSuccess(notification, "Student Found");
                    });
                },
                () -> {
                    //On failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(findStudentBtn, "FETCH DETAILS");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(findStudentBtn, "FETCH DETAILS");
                        UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleUpdateStudentInfo() {
        if (!validateUpdateForm()) return;

        UtilityClass.updateStudentPersonalData = getUpdatedInfo();
        UtilityClass.processingButton(updateStudentInfoBtn);

        studentService.updateStudentPersonalData(UtilityClass.updateStudentPersonalData, staffId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(updateStudentInfoBtn, "UPDATE INFO");
                        showUpdateStudentInfoCard();
                        UtilityClass.showSuccess(notification, ApiContext.getInstance().successMessageContainer);
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(updateStudentInfoBtn, "UPDATE INFO");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                }, () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(updateStudentInfoBtn, "UPDATE INFO");
                        UtilityClass.showError(notification, "Network Error! Check your internet connection and try again");
                    });
                });
    }

    @FXML
    public void handleCancelInfoUpdate() {
        showUpdateStudentInfoCard();
        UtilityClass.updateStudentPersonalData = null;
    }

    @FXML
    public void fetchClassSubjects() {
        GradeCaching selectedGrade = score_SelectClassBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            return;
        }

        showLoading();
        String levelId = selectedGrade.getLevelId();
        academicsService.loadClassSubjects(levelId, staffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        score_selectSubjectBox.setDisable(false);
                        if (UtilityClass.subjectsHolder != null) {
                            score_selectSubjectBox.getItems().clear();
                            score_selectSubjectBox.getItems().addAll(UtilityClass.subjectsHolder);
                        }
                        hideLoading();
                        UtilityClass.showSuccess(notification, "Subjects loaded successfully. You can select now");
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        hideLoading();
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
            //On NetworkIssue
                    Platform.runLater(() -> {
                        hideLoading();
                        UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
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

        UtilityClass.processingButton(saveGradeBtn);
        GradingCriteria gradingCriteria = new GradingCriteria(
                lowerRange, upperRange, gradeCriteria, remarks
        );

        academicsService.saveGradingCriteria(gradingCriteria, staffId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(saveGradeBtn, "ADD GRADE CRITERIA");
                        UtilityClass.showSuccess(notification, ApiContext.getInstance().successMessageContainer);
                        gradeCriteriaField.clear();
                        upperRangeField.clear();
                        lowerRangeField.clear();
                        gradeRemarksField.clear();
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(saveGradeBtn, "ADD GRADE CRITERIA");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                }, () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(saveGradeBtn, "ADD GRADE CRITERIA");
                        UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleLoadStudentsForScores() {
        if (score_selectSubjectBox.isDisable()) {
            UtilityClass.showError(notification, "Select class to load subjects");
            return;
        }

        SubjectsHolder selectedSubject = score_selectSubjectBox.getSelectionModel().getSelectedItem();
        if (selectedSubject == null) {
            UtilityClass.showError(notification, "Select subject to load students");
            return;
        }

        UtilityClass.processingButton(score_LoadStudentsBtn);
        subjectId = selectedSubject.getSubjectId();

        academicsService.loadSubjectStudents(subjectId, staffId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(score_LoadStudentsBtn, "LOAD STUDENTS");
                        displaySubjectStudents(UtilityClass.subjectScores);
                        UtilityClass.showSuccess(
                                notification, "Successfully load " + UtilityClass.subjectScores.getScoresTable().size() + " students"
                        );
                        score_selectSubjectBox.setDisable(true);

                        subjectResultSearchBox.setVisible(false);
                        subjectResultSearchBox.setManaged(false);
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(score_LoadStudentsBtn, "LOAD STUDENTS");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                ()-> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(score_LoadStudentsBtn, "LOAD STUDENTS");
                        UtilityClass.showError(notification, "Network error! Check your network connection and try again!");
                    });
                });
    }

    @FXML
    public void handleClearScoresTable() {
        for (StudentsScoresTable student : studentsScoresTable.getItems()) {
            student.setClassTest1Score("0");
            student.setGroupWorkScore("0");
            student.setClassTest2Score("0");
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

        score_selectSubjectBox.setDisable(false);

        subjectResultSearchBox.setVisible(true);
        subjectResultSearchBox.setManaged(true);
    }

    @FXML
    public void handleSaveScores() {
        SemesterCaching selectedSemester = saveScoresSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            UtilityClass.showError(notification, "Select semester");
            return;
        }

        UtilityClass.processingButton(saveScoresBtn);

        scores_SaveProgressBarVBox.setVisible(true);
        scores_SaveProgressBarVBox.setManaged(true);

        uploadNextScore(subjectId, selectedSemester.getSemesterId(), staffId);
    }

    @FXML
    public void showAddSubjectCard() {
        List<GradeCaching> cachedGrades = UtilityClass.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (gradeComboBox.getItems().isEmpty()) {
                gradeComboBox.getItems().addAll(cachedGrades);
            }
        }

        UtilityClass.switchPageCards(addNewSubjectCard, updateSubjectsCard,
                addSubjectToggleBtn, updateSubjectToggleBtn);
    }

    @FXML
    public void showUpdateSubjectCard() {
        List<GradeCaching> cachedGrades = UtilityClass.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (subject_gradeSelectionBox.getItems().isEmpty()) {
                subject_gradeSelectionBox.getItems().addAll(cachedGrades);
            }
        }

        UtilityClass.switchPageCards(updateSubjectsCard, addNewSubjectCard,
                updateSubjectToggleBtn, addSubjectToggleBtn);
    }

    @FXML
    public void handleAddSubject() {
        String subjectName = subjectNameField.getText();
        if (subjectName.isEmpty()) {
            UtilityClass.showError(notification, "Please enter subjectName");
            return;
        }

        GradeCaching grade = gradeComboBox.getSelectionModel().getSelectedItem();
        if (grade == null) {
            UtilityClass.showError(notification, "Please select a grade");
            return;
        }

        String gradeId = grade.getLevelId();

        UtilityClass.processingButton(saveSubjectBtn);

        AssignSubject assignSubject = new AssignSubject(
                staffId, subjectName, gradeId
        );

        academicsService.addNewSubject(assignSubject,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(saveSubjectBtn, "Save Subject");
                        UtilityClass.showSuccess(notification, "Successfully saved subject with ID: " + ApiContext.getInstance().successMessageContainer);
                        subjectNameField.clear();
                        gradeComboBox.getSelectionModel().clearSelection();
                    });
                }, () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(saveSubjectBtn, "Save Subject");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                }, () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(saveSubjectBtn, "Save Subject");
                        UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleFetchSubjects() {
        GradeCaching selectedGrade = subject_gradeSelectionBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            return;
        }
        showLoading();

        academicsService.loadClassSubjects(selectedGrade.getLevelId(), staffId,
                 () -> {
             //On Success
                     Platform.runLater(() -> {
                         hideLoading();
                         if (UtilityClass.subjectsHolder != null && !UtilityClass.subjectsHolder.isEmpty()) {
                             subjectSelectionBox.getItems().clear();
                             subjectSelectionBox.getItems().addAll(UtilityClass.subjectsHolder);
                         }

                         UtilityClass.showSuccess(notification, "Successfully loaded subjects");
                     });
                 },
                 () -> {
             //On Failure
                     Platform.runLater(() -> {
                         hideLoading();
                         UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                     });
                 },
                 () -> {
             //On Network Issue
                     Platform.runLater(() -> {
                         hideLoading();
                         UtilityClass.showError(notification, "Network error! Check your network connection and try again!");
                     });
                 });
    }

    @FXML
    public void handleLoadSubjectData() {
        if (subjectSelectionBox.isDisable()) {
            UtilityClass.showError(notification, "Please select a grade to load subjects");
            return;
        }

        SubjectsHolder selectedSubject = subjectSelectionBox.getSelectionModel().getSelectedItem();
        if (selectedSubject == null) {
            UtilityClass.showError(notification, "Select a subject");
            return;
        }

        String subjectId = selectedSubject.getSubjectId();
        UtilityClass.processingButton(fetchSubjectBtn);

        academicsService.fetchSubjectData(staffId, subjectId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        loadSubjectData(UtilityClass.subjectData);
                        UtilityClass.notProcessingButton(fetchSubjectBtn, "FETCH SUBJECT DATA");
                        UtilityClass.showSuccess(notification, "Successfully fetched subject");

                        subjectSearchBox.setVisible(false);
                        subjectSearchBox.setManaged(false);

                        subjectSearchResponsePanel.setVisible(true);
                        subjectSearchResponsePanel.setManaged(true);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(fetchSubjectBtn, "FETCH SUBJECT DATA");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(fetchSubjectBtn, "FETCH SUBJECT DATA");
                        UtilityClass.showError(notification, "Network error! Check your network connection and try again!");
                    });
                });
    }

    @FXML
    public void handleUpdateSubjectData() {
        String subjectName = subject_updateNameField.getText().trim();
        if (subjectName.isEmpty()) {
            UtilityClass.showError(notification, "Please enter a subject name");
            return;
        }

        GradeCaching selectedGrade = subject_updateGradeBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            UtilityClass.showError(notification, "Select a grade");
            return;
        }

        UtilityClass.processingButton(updateSubjectBtn);
        String subjectId = subjectIdLabel.getText();

        UtilityClass.subjectData.setSubjectId(subjectId);
        UtilityClass.subjectData.setSubjectName(subjectName);
        UtilityClass.subjectData.setLevelId(
                selectedGrade.getLevelId()
        );

        updateSubjectDetails(UtilityClass.subjectData);
    }

    @FXML
    public void handleDeleteSubject() {
        String subjectId = subjectIdLabel.getText();

        UtilityClass.processingButton(deleteSubjectBtn);
        academicsService.deleteSubject(staffId, subjectId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(deleteSubjectBtn, "DELETE SUBJECT");

                        subjectSearchResponsePanel.setVisible(false);
                        subjectSearchResponsePanel.setManaged(false);

                        subjectSearchBox.setVisible(true);
                        subjectSearchBox.setManaged(true);

                        UtilityClass.showSuccess(notification, "Successfully deleted subject data");
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(deleteSubjectBtn, "DELETE SUBJECT");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(deleteSubjectBtn, "DELETE SUBJECT");
                        UtilityClass.showError(notification, "Network error! Check your network connection and try again!");
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
            UtilityClass.showError(notification, "Please select a grade to load");
            return;
        }

        resultSemester = results_SemesterSelectionBox.getSelectionModel().getSelectedItem();
        if (resultSemester == null) {
            UtilityClass.showError(notification, "Select Semester to Load Results");
            return;
        }

        String levelId = resultGrade.getLevelId();
        String semesterId = resultSemester.getSemesterId();
        UtilityClass.processingButton(loadClassResultsBtn);
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
                UtilityClass.showError(notification,
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
                UtilityClass.showError(notification, "Select grade to contain promoted students");
                return;
            }

            passedTotalScore = passScoreField.getText();
            if (passedTotalScore.equals("0") || passedTotalScore.matches("\\d+")
                    || passedTotalScore.isEmpty()) {
                UtilityClass.showError(notification, "Enter passed score");
                return;
            }

            promotionGradeId = selectedPromotionGrade.getLevelId();
        }

        String semesterId = resultSemester.getSemesterId();

        UtilityClass.processingButton(generateReportBtn);
        UtilityClass.showSuccess(notification, "Work while we process your request");

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

        UtilityClass.processingButton(resetPasswordBtn);

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

        UtilityClass.processingButton(updateStaffInfoBtn);
        FindStaffResponse info = updateStaffDTO();

        staffService.updateStaffInfo(info, staffId,
                () -> {
                    //On Success
                    Platform.runLater( () -> {
                        UtilityClass.notProcessingButton(updateStaffInfoBtn, "SAVE CHANGES");
                        staffDetailsPanel.setVisible(false);
                        staffDetailsPanel.setManaged(false);

                        staffListTableContainer.setVisible(true);
                        staffListTableContainer.setManaged(true);

                        UtilityClass.showSuccess(notification, ApiContext.getInstance().successMessageContainer);
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater( () -> {
                        UtilityClass.notProcessingButton(updateStaffInfoBtn, "SAVE CHANGES");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater( () -> {
                        UtilityClass.notProcessingButton(updateStaffInfoBtn, "SAVE CHANGES");
                        UtilityClass.showError(notification, "Network Error! Check your internet and try again");
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
    public void handleSaveStudent() {
        if (!validateForm()) return;

        UtilityClass.processingButton(saveAndClearBtn);
        saveStudent(() -> {
            // On Success
            Platform.runLater(() -> {
                clearForm();
                UtilityClass.notProcessingButton(saveAndClearBtn, "ADD STUDENT");
                UtilityClass.showSuccess(notification, "Student saved successfully! ID: " + ApiContext.getInstance().successMessageContainer);
            });
        }, () -> {
            // On Failure
            Platform.runLater(() -> {
                UtilityClass.notProcessingButton(saveAndClearBtn, "ADD STUDENT");
                UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
            });
        }, () -> {
            // On Network Failure
            Platform.runLater(() -> {
                UtilityClass.notProcessingButton(saveAndClearBtn, "ADD STUDENT");
                UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
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
        UtilityClass.switchPageCards(addNewSemesterPanel, updateSemesterPanel,
                addSemesterToggleBtn, updateSemesterToggleBtn);
    }

    @FXML
    public void showUpdateSemesterInfoCard() {
        List<SemesterCaching> cachedSemesters = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (cachedSemesters != null && !cachedSemesters.isEmpty()) {
            if (semester_searchInfoBox.getItems().isEmpty()) {
                semester_searchInfoBox.getItems().addAll(cachedSemesters);
            }
        }

        updateSemesterSearchBox.setVisible(true);
        updateSemesterSearchBox.setManaged(true);

        updateSemesterResponsePanel.setVisible(false);
        updateSemesterResponsePanel.setManaged(false);

        UtilityClass.switchPageCards(updateSemesterPanel, addNewSemesterPanel,
                updateSemesterToggleBtn, addSemesterToggleBtn);
    }

    @FXML
    public void handleAddSemester() {
        String semesterName = semesterNameField.getText().trim();
        if (!semesterName.contains("_")) {
            UtilityClass.showError(notification, "Semester Name must contain hyphen eg. 'First_Term'");
            return;
        }

        LocalDate startDate = semesterStartDate.getValue();
        if (startDate == null || startDate.isBefore(LocalDate.now())) {
            UtilityClass.showError(notification, "Start Date cannot be empty or before current date");
            return;
        }

        LocalDate endDate = semesterEndDate.getValue();
        if (endDate == null || endDate.isBefore(LocalDate.now())) {
            UtilityClass.showError(notification, "End Date cannot be empty or before current date");
            return;
        }

        String academicYear = semesterAcademicYearField.getText().trim();
        if (academicYear.isEmpty()) {
            UtilityClass.showError(notification, "Academic Year cannot be empty");
            return;
        }

        UtilityClass.processingButton(addSemesterBtn);
        academicsService.addNewSemester(staffId, semesterName, startDate, endDate, academicYear,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(addSemesterBtn, "ADD SEMESTER");
                        UtilityClass.showSuccess(notification, ApiContext.getInstance().successMessageContainer);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(addSemesterBtn, "ADD SEMESTER");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(addSemesterBtn, "ADD SEMESTER");
                        UtilityClass.showError(notification, "Network Error! Check your connection and try again");
                    });
                });
    }

    @FXML
    public void handleFetchSemesterData() {
        SemesterCaching selectedSemester = semester_searchInfoBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            UtilityClass.showError(notification, "Please select a Semester");
            return;
        }

        UtilityClass.processingButton(update_searchSemesterBtn);
        academicsService.searchSemester(selectedSemester.getSemesterId(),
                semesterData -> semester = semesterData,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(update_searchSemesterBtn, "FETCH DATA");
                        fillSemesterDataPanel();

                        UtilityClass.showSuccess(notification, "Successfully fetched Semester Data");
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(update_searchSemesterBtn, "FETCH DATA");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(update_searchSemesterBtn, "FETCH DATA");
                        UtilityClass.showError(notification, "Network Error! Check your connection and try again");
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
        academicsService.updateSemesterInfo(info,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(updateSemesterBtn, "SAVE CHANGES");
                        UtilityClass.showSuccess(notification, ApiContext.getInstance().successMessageContainer);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(updateSemesterBtn, "SAVE CHANGES");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(updateSemesterBtn, "SAVE CHANGES");
                        UtilityClass.showError(notification, "Network Error! Check your connection and try again");
                    });
                });
    }


    /** ---------------UTILITY METHODS FOR THIS CLASS--------------- **/

    public void initializeAfterLogin() {

        // Wire refresh button on all headers
        dashboardHeaderController.setOnRefresh(this::refreshDashboard);
        manageClassHeaderController.setOnRefresh(this::refreshDashboard);
        manageStudentHeaderController.setOnRefresh(this::refreshDashboard);
        gradingCriteriaHeaderController.setOnRefresh(this::refreshDashboard);
        subjectScoresHeaderController.setOnRefresh(this::refreshDashboard);
        manageSubjectHeaderController.setOnRefresh(this::refreshDashboard);
        manageReportsHeaderController.setOnRefresh(this::refreshDashboard);
        manageStaffHeaderController.setOnRefresh(this::refreshDashboard);
        systemLogsHeaderController.setOnRefresh(this::refreshDashboard);
        manageSemesterHeaderController.setOnRefresh(this::refreshDashboard);

        setActiveButton(dashboardBtn);
        loadStaffInfo();
        showLoading();
        dashboardService.loadAdminDashboard(staffId, notification,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        loadDashboardData();

                        hideLoading();
                    });
                });
    }

    private void setHeaderTitle(HBox headerRoot, String title) {
        if (headerRoot == null) return;           // ← crash-proof line 1
        Label titleLabel = (Label) headerRoot.lookup("#pageTitleLabel");
        if (titleLabel != null) {                 // ← crash-proof line 2
            titleLabel.setText(title);
        }
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
            UtilityClass.showError(notification, "No grade selected!");
            return;
        }
        selectedGradeId = selectedClass.getLevelId();

        studentService.enrollNewStudent(staffId, firstName, lastName, selectedGradeId, gender,
                dob, homeTown, parentName, parentPhone, onSuccess, onFailure, onNetworkIssue);
    }

    private boolean validateForm() {
        if (firstNameField.getText().trim().isEmpty()) {
            UtilityClass.showError(notification, "First name is required");
            return false;
        }
        if (lastNameField.getText().trim().isEmpty()) {
            UtilityClass.showError(notification, "Last name is required");
            return false;
        }
        if (genderComboBox.getValue() == null) {
            UtilityClass.showError(notification, "Please select a gender");
            return false;
        }
        if (dateOfBirthPicker.getValue() == null) {
            UtilityClass.showError(notification, "Date of birth is required");
            return false;
        }
        if (parentNameField.getText().trim().isEmpty()) {
            UtilityClass.showError(notification, "Parent name is required");
            return false;
        }
        if (parentPhoneField.getText().trim().length() != 10) {
            UtilityClass.showError(notification, "Invalid parent phone number");
            return false;
        }
        if (classComboBox.getValue() == null) {
            UtilityClass.showError(notification, "Please select a class");
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
        authService.recoverPassword(staffId, newPasswordStaffId, newPassword,
                () -> {
                    //On Success
                    Platform.runLater( () -> {
                        UtilityClass.notProcessingButton(resetPasswordBtn, "RESET PASSWORD");
                        UtilityClass.showSuccess(notification, ApiContext.getInstance().successMessageContainer);
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater( () -> {
                        UtilityClass.notProcessingButton(resetPasswordBtn, "RESET PASSWORD");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                }, () -> {
                    //On Network Issue
                    Platform.runLater( () -> {
                        UtilityClass.notProcessingButton(resetPasswordBtn, "Recover password");
                        UtilityClass.showError(notification, "Network error! Check your internet and try again");
                    });
                });
    }

    private boolean validateStaffUpdateForm() {
        if (editFirstNameField.getText().isEmpty()) {
            UtilityClass.showError(notification, "Please enter first name");
            return false;
        }

        if (editLastNameField.getText().isEmpty()) {
            UtilityClass.showError(notification, "Please enter last name");
            return false;
        }

        if (editEmailField.getText().isEmpty() || !editEmailField.getText().endsWith(".com")) {
            UtilityClass.showError(notification, "Please enter valid email");
            return false;
        }

        if (editPhoneField.getText().isEmpty() || editPhoneField.getText().length() != 10) {
            UtilityClass.showError(notification, "Please enter valid phone number");
            return false;
        }

        if (editDateOfBirthPicker.getValue() == null) {
            UtilityClass.showError(notification, "Please select date of birth");
            return false;
        }

        if (editGenderComboBox.getSelectionModel().getSelectedItem() == null) {
            UtilityClass.showError(notification, "Please select gender");
            return false;
        }

        if (!role1.isSelected()) {
            if (!role2.isSelected()) {
                if (!role3.isSelected()) {
                    if (!role4.isSelected()) {
                        if (!role5.isSelected()) {
                            UtilityClass.showError(notification, "Please select at least one role");
                            return false;
                        }
                    }
                }
            }
        }

        if (editStatusComboBox.getSelectionModel().getSelectedItem() == null) {
            UtilityClass.showError(notification, "Please select status");
            return false;
        }

        return true;
    }

    private void downloadSemesterReport(String staffId, String levelId, String semesterId, String promotionGradeId, String passScore) {
        academicsService.downloadStudentsReport(staffId, levelId, semesterId, promotionGradeId, passScore,
                this::parentWindow,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(generateReportBtn, "DOWNLOAD TERM REPORTS");
                        UtilityClass.showSuccess(notification, "Results download successful.");
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(generateReportBtn, "DOWNLOAD TERM REPORTS");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(generateReportBtn, "DOWNLOAD SEMESTER REPORT");
                        UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    private void loadClassResults(String levelId, String semesterId) {
        academicsService.loadClassResults(levelId, semesterId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(loadClassResultsBtn, "FETCH RESULTS");
                        populateClassResults(UtilityClass.classResults);
                        UtilityClass.showSuccess(notification, "Results successfully loaded");
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(loadClassResultsBtn, "FETCH RESULTS");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(loadClassResultsBtn, "FETCH RESULTS");
                        UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
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
        academicsService.updateSubjectDetails(subject,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(updateSubjectBtn, "SAVE CHANGES");

                        subjectSearchResponsePanel.setVisible(false);
                        subjectSearchResponsePanel.setManaged(false);

                        subjectSearchBox.setVisible(true);
                        subjectSearchBox.setManaged(true);

                        UtilityClass.showSuccess(notification, "Successfully updated subject data");
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(updateSubjectBtn, "SAVE CHANGES");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(updateSubjectBtn, "SAVE CHANGES");
                        UtilityClass.showError(notification, "Network error! Check your network connection and try again!");
                    });
                });
    }

    private void loadSubjectData(SubjectDTO data) {
        subjectIdLabel.setText(data.getSubjectId());
        subject_updateNameField.setText(data.getSubjectName());

        List<GradeCaching> cachedGrades = UtilityClass.gradeCacheManager.getGradesCache();
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

    private void uploadNextScore(String subjectId, String selectedSemester, String staffId) {
        if (
                currentUploadIndex >= UtilityClass.subjectScores.getScoresTable().size()
        ) {
            UtilityClass.notProcessingButton(saveScoresBtn, "UPLOAD SCORES");

            score_SaveProgressBar.setProgress(0.0);
            scores_SaveProgressBarVBox.setVisible(false);
            scores_SaveProgressBarVBox.setManaged(false);

            currentUploadIndex = 0;
            UtilityClass.subjectScores = null;

            resultsTableContainer.setVisible(false);
            resultsTableContainer.setManaged(false);

            score_selectSubjectBox.setDisable(false);

            subjectResultSearchBox.setVisible(true);
            subjectResultSearchBox.setManaged(true);

            return;
        }

        StudentsScoresTable student =
                UtilityClass.subjectScores.getScoresTable().get(currentUploadIndex);

        academicsService.saveStudentScores(
                student, subjectId, selectedSemester, staffId,
                () -> {
                    // On Success
                    Platform.runLater(() -> {
                        currentUploadIndex++;

                        double progress =
                                (double)  currentUploadIndex / UtilityClass.subjectScores.getScoresTable().size();

                        score_SaveProgressBar.setProgress(progress);

                        uploadNextScore(subjectId, selectedSemester, staffId);
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(saveScoresBtn, "CONTINUE");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(saveScoresBtn, "CONTINUE");
                        UtilityClass.showError(notification,
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
            if (!studentsScoresTable.getItems().isEmpty()) {
                studentsScoresTable.getItems().setAll(scores);
            } else {
                studentsScoresTable.getItems().addAll(scores);
            }
        }

        List<SemesterCaching> cachedSemesters = UtilityClass.semesterCachingManager.getCachedSemesters();
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
            UtilityClass.showError(notification, "Grade criteria is required");
            return false;
        }

        if (lowerRange.isNaN() || upperRange.isNaN()) {
            UtilityClass.showError(notification, "Please enter grade criteria marks");
            return false;
        }

        if (lowerRange > upperRange) {
            UtilityClass.showError(notification, "Lower range cannot be greater than upper range");
            return false;
        }

        if (remarks.isEmpty()) {
            UtilityClass.showError(notification, "Remarks is required");
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
            UtilityClass.showError(notification, "First name is required");
            return false;
        }
        if (updateLastNameField.getText().trim().isEmpty()) {
            UtilityClass.showError(notification, "Last name is required");
            return false;
        }
        if (updateGenderComboBox.getValue() == null) {
            UtilityClass.showError(notification, "Please select a gender");
            return false;
        }
        if (updateDateOfBirthPicker.getValue() == null) {
            UtilityClass.showError(notification, "Date of birth is required");
            return false;
        }
        if (updateHomeTownField.getText().trim().isEmpty()) {
            UtilityClass.showError(notification, "Hometown is required");
            return false;
        }
        if (updateParentNameField.getText().trim().isEmpty()) {
            UtilityClass.showError(notification, "Parent name is required");
            return false;
        }
        if (updateParentPhoneField.getText().trim().length() != 10) {
            UtilityClass.showError(notification, "Invalid phone number");
            return false;
        }
        if (updateClassComboBox.getValue() == null) {
            UtilityClass.showError(notification, "Please select a class");
            return false;
        }
        return true;
    }

    private void studentInfoUpdatePanel(UpdateStudentPersonalData data) {

        List<GradeCaching> cachedGrades  = UtilityClass.gradeCacheManager.getGradesCache();
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
        updateGenderComboBox.getSelectionModel().select(Gender.valueOf(data.getGender().toUpperCase()));
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
        List<StaffCaching> cachedStaffs = UtilityClass.staffCacheManager.getCachedStaffList();
        if (cachedStaffs != null && !cachedStaffs.isEmpty()) {
            if (updateClassInfoStaffComboBox.getItems().isEmpty()) {
                updateClassInfoStaffComboBox.getItems().addAll(cachedStaffs);
            }

            for (StaffCaching staffCaching : cachedStaffs) {
                if (staffCaching.getStaffId().equals(UtilityClass.findAndUpdateClassInfo.getStaffId())) {
                    updateClassInfoStaffComboBox.getSelectionModel().select(staffCaching);
                    break;
                }
            }
        }

        updateClassInfoNameField.setText(UtilityClass.findAndUpdateClassInfo.getLevelName());
        updateClassInfoDisplayArea.setVisible(true);
        updateClassInfoDisplayArea.setManaged(true);
    }

    private void loadDashboardData() {

        List<LogsDetails> cachedLogs = UtilityClass.systemLogsCacheManager.getCachedSystemLogs();
        if (cachedLogs != null) {
            if (recentActivitiesTable.getItems().isEmpty()) {
                recentActivitiesTable.getItems().addAll(cachedLogs);
            }
        }

        setHeaderTitle(dashboardHeader, "ADMIN DASHBOARD");
    }

    private void loadStaffInfo() {
        if (institutionName != null) {
            institutionalNameLabel.setText(institutionName);
        }
        if (staffFullName != null) {
            adminNameLabel.setText(staffFullName);
        }

        versionLabel.setText(UtilityClass.APPLICATION_VERSION);
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
        activityStatusField.setText(log.getStatus());
        if (log.getStatus().equals("SUCCESS") ) {
            activityStatusField.setTextFill(Color.web("#16A34A"));
        } else {
            activityStatusField.setTextFill(Color.web("#DC2626"));
        }

        activityDataField.setText(log.getActionData());
    }

    private void getStaffDetails(String searchId) {
        staffService.searchStaffData(searchId, staffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        fillStaffData(ApiContext.getInstance().findStaffResponse);

                        staffListTableContainer.setVisible(false);
                        staffListTableContainer.setManaged(false);

                        staffDetailsPanel.setVisible(true);
                        staffDetailsPanel.setManaged(true);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, "Network Error! Check your connection and try again");
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
        UtilityClass.staffRoles.clear();
        for (String role : roles) {
            UtilityClass.staffRoles.add(StaffRoles.valueOf(role));
        }
    }

    private void setRoleCheckBoxes() {
        // 1. Reset all to false first so we start with a clean slate
        role1.setSelected(false);
        role2.setSelected(false);
        role3.setSelected(false);
        role4.setSelected(false);
        role5.setSelected(false);

        // 2. Loop through the list and only set TRUE when a match is found
        for (StaffRoles staffRole : UtilityClass.staffRoles) {
            if (staffRole.equals(StaffRoles.PRINCIPAL)) {
                role1.setSelected(true);
            }
            if (staffRole.equals(StaffRoles.ACCOUNTANT)) {
                role2.setSelected(true);
            }
            if (staffRole.equals(StaffRoles.TEACHING_STAFF)) {
                role3.setSelected(true);
            }
            if (staffRole.equals(StaffRoles.GENERAL_STAFF)) {
                role4.setSelected(true);
            }
            if (staffRole.equals(StaffRoles.ADMINISTRATOR)){
                role5.setSelected(true);
            }
        }
    }

    private double parseDouble(String input) {
        if (input == null || input.isBlank()) return 0;
        return Double.parseDouble(input);
    }

    private void recalculateClassScore(StudentsScoresTable row) {
        try {
            double ex1 = parseDouble(row.getClassTest1Score());
            double ex2 = parseDouble(row.getClassTest2Score());
            double test = parseDouble(row.getGroupWorkScore());
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

    private Window parentWindow() {
        return generateReportBtn.getScene() == null ? null : generateReportBtn.getScene().getWindow();
    }
}
