package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.Constants.AttendanceStatus;
import com.bbquantum.eazymanage.Constants.StudentStatus;
import com.bbquantum.eazymanage.DTO.Academics.*;
import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.DTO.Institution.SemesterCaching;
import com.bbquantum.eazymanage.DTO.Institution.StudentAttendance;
import com.bbquantum.eazymanage.DTO.Institution.UpdateStudentPersonalData;
import com.bbquantum.eazymanage.Utitity.RadioStatusCell;
import com.bbquantum.eazymanage.Utitity.StudentAttendanceUI;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.util.Duration;
import javafx.util.converter.DefaultStringConverter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class StaffController implements Initializable {

    // CONTROLLER OWN VARIABLES
    private UtilityClass utilityClass;
    public SubjectScores subjectScores;
    private String institutionName;
    public String staffId;
    private String staffFullName;

    //Grades information and dashboard rotation
    private PauseTransition resumeTimer =
            new PauseTransition(Duration.seconds(15));

    private List<GradeInformation> gradeInformation;
    private Timeline gradeRotationTimeline;
    private boolean isRotationPaused = false;
    private int currentDisplayIndex;

    //Holds results for printing report cards
    GradeInformation resultGrade;

    //Semester and Grade for loading and downloading results
    private SemesterCaching resultSemester;

    //Promotion Grades of passed students
    private GradeCaching selectedPromotionGrade;

    //Contains pass score for moving passed students
    private String passedTotalScore;

    public List<StudentAttendance> studentAttendances;
    private final ObservableList<StudentAttendanceUI> attendanceData = FXCollections.observableArrayList();
    private LocalDate selectedAttendanceDate;

    //Stores a list of the attendances for uploaded
    private List<StudentAttendance> studentAttendanceList = new ArrayList<>();

    private int currentUploadIndex;

    // NAVIGATION
    @FXML public StackPane notification;
    @FXML public Label versionLabel;
    @FXML private Button dashboardBtn;
    @FXML private Button addStudentBtn;
    @FXML private Button addResultsBtn;
    @FXML private Button addSubjectBtn;
    @FXML private Button resultsBtn;
    @FXML private Button attendanceBtn;
    @FXML private Button logoutButton;
    @FXML private Label staffNameLabel;
    @FXML private Label institutionalNameLabel;

    // MAIN DASHBOARD VIEW
    @FXML private StackPane contentArea;
    @FXML private ScrollPane dashboardView;
    @FXML private Label dateTimeLabel;
    @FXML private Label classNameLabel;
    @FXML private Label totalStudentsLabel;
    @FXML private Button prevClassBtn;
    @FXML private Button nextClassBtn;
    @FXML public TableView<StudentsRoaster> studentsTable;
    @FXML private TableColumn<StudentsRoaster, String> studentIdCol;
    @FXML private TableColumn<StudentsRoaster, String> studentNameCol;
    @FXML private TableColumn<StudentsRoaster, String> genderCol;
    @FXML private TableColumn<StudentsRoaster, String> homeTownCol;
    @FXML private TableColumn<StudentsRoaster, String> parentNameCol;
    @FXML private TableColumn<StudentsRoaster, String> parentPhoneCol;

    //NEW SUBJECT SCORES
    @FXML private ScrollPane AddNewSubjectScoresView;

    @FXML private HBox subjectResultSearchBox;
    @FXML private TextField subjectCodeField;
    @FXML private Button loadSubjectStudentsBtn;

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

    // ADD STUDENT VIEW
    @FXML private ScrollPane addNewStudentView;

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
    @FXML private TextField studentSearchField;
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
    @FXML private VBox updateButtonVBox;
    @FXML private Button updateStudentInfoBtn;

    //ADD SUBJECT VIEW
    @FXML private ScrollPane addNewSubjectView;

    @FXML private Button addSubjectToggleBtn;
    @FXML private Button printSubjectsToggleBtn;

    @FXML private StackPane subjectCardContainer;

    @FXML private VBox addNewSubjectCard;
    @FXML private TextField subjectNameField;
    @FXML private ComboBox<GradeCaching> gradeComboBox;
    @FXML private Button saveSubjectBtn;

    @FXML private VBox printClassSubjectsCard;
    @FXML private ComboBox<GradeCaching> printClassSubjectsComboBox;
    @FXML public Button printSubjectDataBtn;

    //MANAGE RESULTS PAGE
    @FXML private ScrollPane manageResultsView;

    @FXML private VBox viewResultsHBox;
    @FXML private ComboBox<GradeInformation> resultsGradeComboBox;
    @FXML private ComboBox<SemesterCaching> resultsSemesterComboBox;
    @FXML private Button loadClassResultsBtn;

    @FXML private HBox resultsInfoBox;
    @FXML private Label gradeNameLabel;
    @FXML private Label semesterNameLabel;

    @FXML private VBox classResultsTableContainer;
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

    @FXML public Button generateReportBtn;

    @FXML private HBox resultsPassmarkHBox;
    @FXML private TextField passScoreField;
    @FXML private ComboBox<GradeCaching> promotionGradeComboBox;

    @FXML private Button movePassedStudentsBtn;

    //ATTENDANCE PAGE
    @FXML private ScrollPane attendanceView;

    @FXML private Button markAttendanceCard;
    @FXML private Button updateAttendanceCard;

    @FXML private StackPane attendanceCardContainer;

    @FXML private VBox markAttendancePanel;
    @FXML private VBox markAttendanceSelection;
    @FXML private ComboBox<GradeCaching> markAttendanceGradeComboBox;
    @FXML private Button markAttendanceBtn;
    @FXML private HBox markAttendanceInfoBox;
    @FXML private Label markAttendanceClassNameLabel;
    @FXML private Label markAttendanceClassCodeLabel;
    @FXML private VBox markAttendanceTableContainer;
    @FXML private TableView<StudentAttendanceUI> markAttendanceTable;
    @FXML private TableColumn<StudentAttendanceUI, String> markAttendanceLevelIdCol;
    @FXML private TableColumn<StudentAttendanceUI, String> markAttendanceStudentNameCol;
    @FXML private TableColumn<StudentAttendanceUI, AttendanceStatus> markAbsentCol;
    @FXML private TableColumn<StudentAttendanceUI, AttendanceStatus> markPresentCol;
    @FXML private Button saveAttendanceBtn;
    @FXML private VBox attendanceSaveProgressBarVBox;
    @FXML private ProgressBar attendanceSaveProgressBar;

    @FXML private VBox updateAttendancePanel;
    @FXML private VBox updateAttendanceSelection;
    @FXML private ComboBox<GradeCaching> updateAttendanceGradeComboBox;
    @FXML private DatePicker updateAttendanceDatePicker;
    @FXML private Button updateAttendanceBtn;
    @FXML private HBox updateAttendanceInfoBox;
    @FXML private Label updateAttendanceClassNameLabel;
    @FXML private Label updateAttendanceClassCodeLabel;
    @FXML private VBox updateAttendanceTableContainer;
    @FXML private TableView<StudentAttendanceUI> updateAttendanceTable;
    @FXML private TableColumn<StudentAttendanceUI, String> updateAttendanceLevelIdCol;
    @FXML private TableColumn<StudentAttendanceUI, String> updateAttendanceStudentNameCol;
    @FXML private TableColumn<StudentAttendanceUI, AttendanceStatus> updateAbsentCol;
    @FXML private TableColumn<StudentAttendanceUI, AttendanceStatus> updatePresentCol;
    @FXML private Button updateAndSaveAttendanceBtn;
    @FXML private VBox attendanceUpdateProgressBarVBox;
    @FXML private ProgressBar attendanceUpdateProgressBar;

    //SPINNER
    @FXML private VBox loadingOverlay;
    @FXML private ProgressIndicator loadingSpinner;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //STUDENT ROASTER
        studentIdCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentId()));

        studentNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentName()));

        genderCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentGender()));

        homeTownCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getHomeTown()));

        parentNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getGuardianName()));

        parentPhoneCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getGuardianContact()));

        studentsTable.setOnMousePressed(event -> pauseRotation());
        studentsTable.setOnMouseClicked(event -> pauseRotation());
        studentsTable.setOnScroll(event -> pauseRotation());



        //SCORES TABLE
        studentIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentId()));

        studentNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentName()));

        classScoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClassScore()));

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

        calExamScoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCalculatedExamScore()));


        exercise1Column.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        classTestColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        exercise2Column.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        projectColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        examScoreColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));

        //Class score set to uneditable
        classScoreColumn.setEditable(false);
        calExamScoreColumn.setEditable(false);

        exercise1Column.setEditable(true);
        classTestColumn.setEditable(true);
        exercise2Column.setEditable(true);
        projectColumn.setEditable(true);
        examScoreColumn.setEditable(true);
        studentsScoresTable.setEditable(true);
        studentsScoresTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        exercise1Column.setOnEditCommit(event -> {
            if (!event.getNewValue().matches("\\d+(\\.\\d+)?")) {
                utilityClass.showError(notification, "Only numbers allowed");
                studentsScoresTable.refresh();
                return;
            }

            if (Double.parseDouble(event.getNewValue()) > 20) {
                utilityClass.showError(notification, "Exercise 1 shouldn't be more than 20");
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
                utilityClass.showError(notification, "Only numbers allowed");
                studentsScoresTable.refresh();
                return;
            }

            if (Double.parseDouble(event.getNewValue()) > 30) {
                utilityClass.showError(notification, "Class Test shouldn't be more than 30");
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
                utilityClass.showError(notification, "Only numbers allowed");
                studentsScoresTable.refresh();
                return;
            }

            if (Double.parseDouble(event.getNewValue()) > 20) {
                utilityClass.showError(notification, "Exercise 2 shouldn't be more than 20");
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
                utilityClass.showError(notification, "Only numbers allowed");
                studentsScoresTable.refresh();
                return;
            }

            if (Double.parseDouble(event.getNewValue()) > 30) {
                utilityClass.showError(notification, "Project score shouldn't be more than 30");
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
                utilityClass.showError(notification, "Only numbers allowed");
                studentsScoresTable.refresh();
                return;
            }

            StudentsScoresTable row = event.getRowValue();
            row.setExamScore(event.getNewValue());
            reCalculateExamScore(row);
            studentsScoresTable.refresh();
        });



        //RESULTS TABLE
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


        //Results subject score table
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

        //MARK ATTENDANCE TABLE INITIALIZATION
        markAttendanceTable.setItems(attendanceData);

        markAttendanceLevelIdCol.setCellValueFactory(
                cellData -> cellData.getValue().levelIdProperty());

        markAttendanceStudentNameCol.setCellValueFactory(
                cellData -> cellData.getValue().studentNameProperty());

        markAbsentCol.setCellValueFactory(
                cellData -> cellData.getValue().statusProperty());

        markPresentCol.setCellValueFactory(
                cellData -> cellData.getValue().statusProperty()
        );

        markAbsentCol.setCellFactory(column ->
                new RadioStatusCell(AttendanceStatus.ABSENT));

        markPresentCol.setCellFactory(column ->
                new RadioStatusCell(AttendanceStatus.PRESENT));


        //UPDATE ATTENDANCE TABLE INITIALIZATION
        updateAttendanceTable.setItems(attendanceData);

        updateAttendanceLevelIdCol.setCellValueFactory(
                cellData -> cellData.getValue().levelIdProperty());

        updateAttendanceStudentNameCol.setCellValueFactory(
                cellData -> cellData.getValue().studentNameProperty());

        updateAbsentCol.setCellValueFactory(
                cellData -> cellData.getValue().statusProperty());

        updatePresentCol.setCellValueFactory(
                cellData -> cellData.getValue().statusProperty()
        );

        updateAbsentCol.setCellFactory(column ->
                new RadioStatusCell(AttendanceStatus.ABSENT));

        updatePresentCol.setCellFactory(column ->
                new RadioStatusCell(AttendanceStatus.PRESENT));
    }

    @FXML
    public void showDashboard() {
        gradeInformation = utilityClass.gradeInfoCacheManager.getGradesInformation();
        if (gradeInformation != null && !gradeInformation.isEmpty()) {
            loadDashBoardData(gradeInformation.getFirst());
            startDashboardChanges();
        }
        SwitchView(dashboardBtn, dashboardView);
    }

    @FXML
    public void refreshDashboard() {
        showLoading();
        utilityClass.loadStaffDashboardData(staffId, notification,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        gradeInformation = utilityClass.gradeInfoCacheManager.getGradesInformation();
                        if (gradeInformation != null && !gradeInformation.isEmpty()) {
                            loadDashBoardData(gradeInformation.getFirst());
                            startDashboardChanges();
                        }

                        hideLoading();
                    });
                });
    }

    @FXML
    public void showAddStudent() {
        SwitchView(addStudentBtn, addNewStudentView);

        showAddStudentCard();
    }

    @FXML
    public void showAddResults() {
        subjectResultSearchBox.setVisible(true);
        subjectResultSearchBox.setManaged(true);

        subjectInfoBox.setVisible(false);
        subjectInfoBox.setManaged(false);

        noStudentsBox.setVisible(false);
        noStudentsBox.setManaged(false);

        studentsScoresTable.getItems().clear();

        resultsTableContainer.setVisible(false);
        resultsTableContainer.setManaged(false);

        saveScoresSemesterVBox.setVisible(false);
        saveScoresSemesterVBox.setManaged(false);

        List<SemesterCaching> semesterCaching = utilityClass.semesterCachingManager.getCachedSemesters();
        if (semesterCaching != null && !semesterCaching.isEmpty()) {
            if (saveScoresSemesterComboBox.getItems().isEmpty()) {
                saveScoresSemesterComboBox.getItems().addAll(semesterCaching);
            }
        }

        SwitchView(addResultsBtn, AddNewSubjectScoresView);
    }

    @FXML
    public void openResultsPage() {
        viewResultsHBox.setVisible(true);
        viewResultsHBox.setManaged(true);

        resultsInfoBox.setVisible(false);
        resultsInfoBox.setManaged(false);

        classResultsTableContainer.setVisible(false);
        classResultsTableContainer.setManaged(false);

        List<GradeInformation> gradesInformation = utilityClass.gradeInfoCacheManager.getGradesInformation();
        if (gradesInformation != null && !gradesInformation.isEmpty()) {
            if (resultsGradeComboBox.getItems().isEmpty()) {
                resultsGradeComboBox.getItems().addAll(gradesInformation);
            }
        }

        List<SemesterCaching> semesterCaching = utilityClass.semesterCachingManager.getCachedSemesters();
        if (semesterCaching != null && !semesterCaching.isEmpty()) {
            if (resultsSemesterComboBox.getItems().isEmpty()) {
                resultsSemesterComboBox.getItems().addAll(semesterCaching);
            }
        }

        List<GradeCaching> gradeCaching = utilityClass.gradeCacheManager.getGradesCache();
        if (gradeCaching != null && !gradeCaching.isEmpty()) {
            if (promotionGradeComboBox.getItems().isEmpty()) {
                promotionGradeComboBox.getItems().addAll(gradeCaching);
            }
        }

        SwitchView(resultsBtn, manageResultsView);
    }

    @FXML
    public void handleLogout() throws IOException {
        utilityClass.clearSession();
        Stage staffDashboard = (Stage) logoutButton.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/Authentication/auth-container.fxml"));
        Scene scene = new Scene(loader.load());

        // Create new stage for dashboard
        Stage loginStage = new Stage();
        loginStage.setTitle("EazyManage - Authentication");
        loginStage.setScene(scene);
        loginStage.sizeToScene();
        loginStage.setResizable(false);

        staffDashboard.close();
        loginStage.show();
    }

    @FXML
    public void openAddSubjectPage() {
        SwitchView(addSubjectBtn, addNewSubjectView);
        showAddSubjectCard();
    }

    @FXML
    public void openAttendancePage() {
        SwitchView(attendanceBtn, attendanceView);
        displayMarkAttendanceCard();
    }

    @FXML
    public void handleAddSubject() {
        String subjectName = subjectNameField.getText();
        if (subjectName.isEmpty()) {
            utilityClass.showError(notification, "Please enter subjectName");
            return;
        }

        GradeCaching grade = gradeComboBox.getSelectionModel().getSelectedItem();
        if (grade == null) {
            utilityClass.showError(notification, "Please select a grade");
            return;
        }
        String gradeId = grade.getLevelId();

        utilityClass.processingButton(saveSubjectBtn);

        AssignSubject assignSubject = new AssignSubject(
                subjectName, gradeId
        );

        utilityClass.addNewSubject(assignSubject,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveSubjectBtn, "Save Subject");
                        utilityClass.showSuccess(notification, "Successfully saved subject with ID: " + utilityClass.successMessageContainer);
                        subjectNameField.clear();
                        gradeComboBox.getSelectionModel().clearSelection();
                    });
                }, () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveSubjectBtn, "Save Subject");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                }, () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveSubjectBtn, "Save Subject");
                        utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleLoadStudentsForScores() {
        String subjectId = subjectCodeField.getText().trim();
        if (subjectId.isEmpty()) {
            utilityClass.showError(notification, "Subject code field is empty!");
            return;
        }
        utilityClass.processingButton(loadSubjectStudentsBtn);
        utilityClass.loadSubjectStudents(subjectId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(loadSubjectStudentsBtn, "LOAD STUDENTS");
                        displaySubjectStudents(subjectScores);
                        utilityClass.showSuccess(notification, "Successfully load " + subjectScores.getScoresTable().size() + " students");

                        subjectResultSearchBox.setVisible(false);
                        subjectResultSearchBox.setManaged(false);
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(loadSubjectStudentsBtn, "LOAD STUDENTS");
                        noStudentsFound();
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },
                ()-> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(loadSubjectStudentsBtn, "LOAD STUDENTS");
                        utilityClass.showError(notification, "Network error! Check your network connection and try again!");
                    });
                });
    }

    @FXML
    public void handleSaveScores() {
        String subjectId = subjectCodeField.getText().trim();
        if (subjectId.isEmpty()) {
            utilityClass.showError(notification, "Subject code field is empty!");
            return;
        }

        if (!saveScoresSemesterVBox.isVisible()) {
            saveScoresSemesterVBox.setVisible(true);
            saveScoresSemesterVBox.setManaged(true);
            utilityClass.showError(notification, "Scroll down to select term for results");
            return;
        }

        SemesterCaching semesterCaching = saveScoresSemesterComboBox.getSelectionModel().getSelectedItem();
        if (semesterCaching == null) {
            utilityClass.showError(notification, "Please select a semester");
            return;
        }
        utilityClass.processingButton(saveScores);
        String semesterId = semesterCaching.getSemesterId();

        String scoresInfo = subjectId + "_" + semesterId + "_" + staffId; //It combines subject Id and semesterId for saving

        List<StudentsScoresTable> scores = studentsScoresTable.getItems();
        // Track progress
        final int totalStudents = scores.size();
        final int[] savedCount = {0};
        final int[] failedCount = {0};

        for (StudentsScoresTable student : scores) {
            utilityClass.saveStudentScores(
                    student, scoresInfo,
                    () -> {
                        // On success
                        savedCount[0]++;
                        checkCompletion(savedCount[0], failedCount[0], totalStudents, saveScores);
                    },
                    () -> {
                        // On failure
                        failedCount[0]++;
                        checkCompletion(savedCount[0], failedCount[0], totalStudents, saveScores);
                    },
                    () -> {
                        utilityClass.notProcessingButton(saveScores, "SAVE SCORES");
                        utilityClass.showError(notification, "Network error! Check your network connection and try again!");
                    });
        }
    }

    @FXML
    public void handleClearScores() {
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

    @FXML
    public void handleSaveAndClear() {
        if (!validateForm()) return;

        utilityClass.processingButton(saveAndClearBtn);
        saveStudent(() -> {
            // On Success
            Platform.runLater(() -> {
                clearForm();
                utilityClass.notProcessingButton(saveAndClearBtn, "Save Student");
                utilityClass.showSuccess(notification, "Student saved successfully! ID: " + utilityClass.successMessageContainer);
            });
        }, () -> {
            // On Failure
            Platform.runLater(() -> {
                utilityClass.notProcessingButton(saveAndClearBtn, "Save Student");
                utilityClass.showError(notification, utilityClass.errorMessageContainer);
            });
        }, () -> {
            // On Network Failure
            Platform.runLater(() -> {
                utilityClass.notProcessingButton(saveAndClearBtn, "Save and Close");
                utilityClass.showError(notification, "Network Error! Check your network connection and try again");
            });
        });
    }

    @FXML
    public void handleLoadClassResults() {
        resultGrade = resultsGradeComboBox.getSelectionModel().getSelectedItem();
        if (resultGrade == null) {
            utilityClass.showError(notification, "Please select a grade to load");
            return;
        }

        resultSemester = resultsSemesterComboBox.getSelectionModel().getSelectedItem();
        if (resultSemester == null) {
            utilityClass.showError(notification, "Select Semester to Load Results");
            return;
        }

        String gradeId = resultGrade.getGradeId();
        String semesterId = resultSemester.getSemesterId();
        utilityClass.processingButton(loadClassResultsBtn);

        utilityClass.loadClassResults(gradeId, semesterId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(loadClassResultsBtn, "Fetch Results");
                        utilityClass.showSuccess(notification, "Results successfully loaded");
                        populateClassResults(utilityClass.viewClassSemesterReports);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(loadClassResultsBtn, "Fetch Results");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);

                        resultsInfoBox.setVisible(false);
                        resultsInfoBox.setManaged(false);
                        classResultsTableContainer.setVisible(false);
                        classResultsTableContainer.setManaged(false);
                    });
                }, () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(loadClassResultsBtn, "Fetch Results");
                        utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleClearResults() {
        classResultsTable.getItems().clear();
        subjectBreakdownTable.getItems().clear();

        classResultsTableContainer.setVisible(false);
        classResultsTableContainer.setManaged(false);

        resultsInfoBox.setVisible(false);
        resultsInfoBox.setManaged(false);

        resultsGradeComboBox.getSelectionModel().clearSelection();
        resultsSemesterComboBox.getSelectionModel().clearSelection();
        viewResultsHBox.setVisible(true);
        viewResultsHBox.setManaged(true);
    }

    @FXML
    public void handleDownloadReports() {
        String levelId = resultGrade.getGradeId();

        String semesterName = resultSemester.getSemesterName();
        String[] semesterNameSplit = semesterName.split("-");

        String promotionGradeId = "";
        if (semesterNameSplit[0].equals("THIRD")) {
            if (!resultsPassmarkHBox.isVisible()) {
                resultsPassmarkHBox.setVisible(true);
                resultsPassmarkHBox.setManaged(true);
                utilityClass.showError(notification,
                        "Enter pass score and select grade to contain passed students");
                return;
            }

            selectedPromotionGrade = promotionGradeComboBox.getSelectionModel().getSelectedItem();
            if (selectedPromotionGrade == null) {
                utilityClass.showError(notification, "Select grade to contain promoted students");
                return;
            }

            passedTotalScore = passScoreField.getText();
            if (passedTotalScore.equals("0") || passedTotalScore.matches("\\d+")
            || passedTotalScore.isEmpty()) {
                utilityClass.showError(notification, "Enter passed score score");
                return;
            }

            promotionGradeId = selectedPromotionGrade.getLevelId();
        }

        if (!semesterNameSplit[0].equals("THIRD")) {
            promotionGradeId = "no_promotion";
        }

        String semesterId = resultSemester.getSemesterId();

        utilityClass.processingButton(generateReportBtn);
        utilityClass.showSuccess(notification, "Work while we process your request");

        utilityClass.downloadStudentsReport(levelId, semesterId, promotionGradeId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(generateReportBtn, "DOWNLOAD SEMESTER REPORT");
                        if (semesterNameSplit[0].equals("THIRD")) {
                            resultsPassmarkHBox.setVisible(true);
                            resultsPassmarkHBox.setManaged(true);
                            movePassedStudentsBtn.setDisable(false);
                            utilityClass.showSuccess(notification, "Results downloaded successfully. Enter passed total score and promotion grade for promotion");
                            return;
                        }
                        utilityClass.showSuccess(notification, "Results downloaded successfully.");
                    });
                }, () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(generateReportBtn, "DOWNLOAD SEMESTER REPORT");
                        if (utilityClass.errorMessageContainer != null && !utilityClass.errorMessageContainer.isEmpty()) {
                            utilityClass.showError(notification, utilityClass.errorMessageContainer);
                            return;
                        }
                        utilityClass.showError(notification, "Could not download report");
                    });
                }, () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(generateReportBtn, "DOWNLOAD SEMESTER REPORT");
                        utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleMovePassedStudents() {

        String currentGradeId = resultGrade.getGradeId();
        String nextGradeId = selectedPromotionGrade.getLevelId();
        String semesterId = resultSemester.getSemesterId();

        utilityClass.processingButton(movePassedStudentsBtn);
        utilityClass.movePassedStudents(currentGradeId, semesterId, passedTotalScore, nextGradeId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(movePassedStudentsBtn, "Transfer Students");
                        utilityClass.showSuccess(notification, utilityClass.successMessageContainer);
                        resultsPassmarkHBox.setVisible(false);
                        resultsPassmarkHBox.setManaged(false);
                        movePassedStudentsBtn.setDisable(true);
                    });
                }, () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(movePassedStudentsBtn, "Transfer Students");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                        movePassedStudentsBtn.setDisable(true);
                    });
                }, () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(movePassedStudentsBtn, "Transfer Students");
                        utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                        movePassedStudentsBtn.setDisable(true);
                    });
                });
    }

    @FXML
    public void loadAttendanceStudents() {
        GradeCaching gradeCaching = markAttendanceGradeComboBox.getSelectionModel().getSelectedItem();
        if (gradeCaching == null) {
            utilityClass.showError(notification, "Select Grade");
            return;
        }

        utilityClass.processingButton(markAttendanceBtn);
        String levelId = gradeCaching.getLevelId();

        utilityClass.loadStudentsForAttendance(levelId, "no_date",
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        attendanceData.clear();
                        for(StudentAttendance studentAttendance: studentAttendances) {
                            attendanceData.add(new StudentAttendanceUI(studentAttendance));
                        }

                        markAttendanceSelection.setVisible(false);
                        markAttendanceSelection.setManaged(false);

                        //Displays class information
                        markAttendanceClassNameLabel.setText(gradeCaching.getLevelName());
                        markAttendanceClassCodeLabel.setText(gradeCaching.getLevelId());
                        markAttendanceInfoBox.setVisible(true);
                        markAttendanceInfoBox.setManaged(true);

                        //Displays attendance list
                        markAttendanceTableContainer.setVisible(true);
                        markAttendanceTableContainer.setManaged(true);

                        utilityClass.notProcessingButton(markAttendanceBtn, "FETCH STUDENTS");
                        utilityClass.showSuccess(notification, "Students Loaded Successfully");
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(markAttendanceBtn, "FETCH STUDENTS");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(markAttendanceBtn, "FETCH STUDENTS");
                        utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void loadAttendanceForUpdate() {
        GradeCaching gradeCaching = updateAttendanceGradeComboBox.getSelectionModel().getSelectedItem();
        if (gradeCaching == null) {
            utilityClass.showError(notification, "Select Grade");
            return;
        }
        String levelId = gradeCaching.getLevelId();

        if (!updateAttendanceDatePicker.isVisible()) {
            updateAttendanceDatePicker.setVisible(true);
            updateAttendanceDatePicker.setManaged(true);
        }

        selectedAttendanceDate = updateAttendanceDatePicker.getValue();
        if (selectedAttendanceDate == null) {
            utilityClass.showError(notification, "Select Date to update");
            return;
        }

        if (selectedAttendanceDate.isBefore(LocalDate.now())) {
            utilityClass.showError(notification, "You can't update previous attendance records");
            return;
        }

        String date = selectedAttendanceDate.toString();

        utilityClass.processingButton(updateAttendanceBtn);

        utilityClass.loadStudentsForAttendance(levelId, date,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        attendanceData.clear();
                        //Populates table with list
                        for(StudentAttendance studentAttendance: studentAttendances) {
                            attendanceData.add(new StudentAttendanceUI(studentAttendance));
                        }

                        updateAttendanceSelection.setVisible(false);
                        updateAttendanceSelection.setManaged(false);

                        //Displays class information
                        updateAttendanceClassNameLabel.setText(gradeCaching.getLevelName());
                        updateAttendanceClassCodeLabel.setText(gradeCaching.getLevelId());
                        updateAttendanceInfoBox.setVisible(true);
                        updateAttendanceInfoBox.setManaged(true);

                        //Display attendance list
                        updateAttendanceTableContainer.setVisible(true);
                        updateAttendanceTableContainer.setManaged(true);

                        utilityClass.notProcessingButton(updateAttendanceBtn, "FETCH STUDENTS");
                        utilityClass.showSuccess(notification, "Students Loaded Successfully");
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(updateAttendanceBtn, "FETCH STUDENTS");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(updateAttendanceBtn, "FETCH STUDENTS");
                        utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleCancelAttendance() {
        if (markAttendanceTableContainer.isVisible()) {
            markAttendanceInfoBox.setVisible(false);
            markAttendanceInfoBox.setManaged(false);

            markAttendanceTableContainer.setVisible(false);
            markAttendanceTableContainer.setManaged(false);

            markAttendanceSelection.setVisible(true);
            markAttendanceSelection.setManaged(true);
        } else {
            updateAttendanceInfoBox.setVisible(false);
            updateAttendanceInfoBox.setManaged(false);

            updateAttendanceTableContainer.setVisible(false);
            updateAttendanceTableContainer.setManaged(false);

            updateAttendanceSelection.setVisible(true);
            updateAttendanceSelection.setManaged(true);
        }
    }

    @FXML
    public void handleSaveAttendance() {
        utilityClass.processingButton(saveAttendanceBtn);
        ObservableList<StudentAttendanceUI> attendanceList = markAttendanceTable.getItems();

        if (studentAttendanceList.isEmpty()) {
            for (StudentAttendanceUI studentAttendanceUI : attendanceList) {
                studentAttendanceList.add(studentAttendanceUI.toStudentAttendance());
            }
        }

        attendanceSaveProgressBarVBox.setVisible(true);
        attendanceSaveProgressBarVBox.setManaged(true);

        uploadNextAttendance();
    }

    private void uploadNextAttendance() {

        if (currentUploadIndex >= studentAttendanceList.size()) {
            // Finished
            utilityClass.notProcessingButton(saveAttendanceBtn, "SAVE ATTENDANCE");
            utilityClass.showSuccess(notification, "Attendance Saved Successfully");

            attendanceSaveProgressBar.setProgress(0.0);
            attendanceSaveProgressBarVBox.setVisible(false);
            attendanceSaveProgressBarVBox.setManaged(false);

            currentUploadIndex = 0;
            studentAttendanceList.clear();

            markAttendanceInfoBox.setVisible(false);
            markAttendanceInfoBox.setManaged(false);

            markAttendanceTableContainer.setVisible(false);
            markAttendanceTableContainer.setManaged(false);

            markAttendanceSelection.setVisible(true);
            markAttendanceSelection.setManaged(true);
            return;
        }

        StudentAttendance record = studentAttendanceList.get(currentUploadIndex);

        utilityClass.markStudentAttendance(
                record.getStudentId(),
                staffId,
                record.getLevelId(),
                record.getStatus(),

                () -> {
                    Platform.runLater(() -> {
                        currentUploadIndex++;

                        double progress = (double) currentUploadIndex / studentAttendanceList.size();
                        attendanceSaveProgressBar.setProgress(progress);

                        uploadNextAttendance(); // 🔥 recursive controlled chain
                    });
                },

                () -> {
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveAttendanceBtn, "CONTINUE");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },

                () -> {
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveAttendanceBtn, "CONTINUE");
                        utilityClass.showError(notification,
                                "Network Error! Fix connection and click Continue.");
                    });
                }
        );
    }



    @FXML
    public void handleUpdateAttendance() {
        utilityClass.processingButton(updateAndSaveAttendanceBtn);
        ObservableList<StudentAttendanceUI> attendanceList = markAttendanceTable.getItems();

        if (studentAttendanceList.isEmpty()) {
            for (StudentAttendanceUI studentAttendanceUI : attendanceList) {
                studentAttendanceList.add(studentAttendanceUI.toStudentAttendance());
            }
        }

        attendanceUpdateProgressBarVBox.setVisible(true);
        attendanceUpdateProgressBarVBox.setManaged(true);

        uploadUpdateAttendance();
    }

    private void uploadUpdateAttendance() {
        if (currentUploadIndex >= studentAttendanceList.size()) {
            utilityClass.notProcessingButton(updateAndSaveAttendanceBtn, "SAVE ATTENDANCE");

            utilityClass.showSuccess(notification, "Attendance Saved Successfully");

            attendanceSaveProgressBar.setProgress(0.0);
            attendanceSaveProgressBarVBox.setVisible(false);
            attendanceSaveProgressBarVBox.setManaged(false);

            currentUploadIndex = 0;
            studentAttendanceList.clear();

            updateAttendanceInfoBox.setVisible(false);
            updateAttendanceInfoBox.setManaged(false);

            updateAttendanceTableContainer.setVisible(false);
            updateAttendanceTableContainer.setManaged(false);

            updateAttendanceSelection.setVisible(true);
            updateAttendanceSelection.setManaged(true);
            return;
        }

        StudentAttendance record = studentAttendanceList.get(currentUploadIndex);

        utilityClass.updateAttendance(
                record.getStudentId(),
                staffId,
                record.getLevelId(),
                record.getStatus(),
                selectedAttendanceDate.toString(),
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        currentUploadIndex++;

                        double progress = ((double) currentUploadIndex /studentAttendanceList.size());

                        attendanceUpdateProgressBar.setProgress(progress);

                        uploadUpdateAttendance();
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(updateAndSaveAttendanceBtn, "CONTINUE");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(updateAndSaveAttendanceBtn, "CONTINUE");
                        utilityClass.showError(notification, "Network Error! Check your network connection and Click on Continue");
                    });
                });
    }

    @FXML
    public void handlePrintSubjectData() {
        GradeCaching gradeCaching = printClassSubjectsComboBox.getSelectionModel().getSelectedItem();
        if (gradeCaching == null) {
            utilityClass.showError(notification, "Select Grade");
            return;
        }

        utilityClass.processingButton(printSubjectDataBtn);
        String levelId = gradeCaching.getLevelId();
        String levelName = gradeCaching.getLevelName();

        utilityClass.printSubjectList(levelId, levelName,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(printSubjectDataBtn, "Print Subject List");
                        utilityClass.showSuccess(notification, "Subject List successfully downloaded");
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(printSubjectDataBtn, "Print Subject List");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(printSubjectDataBtn, "Print Subject List");
                        utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleUpdateStudentInfo() {
        if (!validateUpdateForm()) return;
        UpdateStudentPersonalData studentPersonalData = new UpdateStudentPersonalData();
        studentPersonalData.setStudentId(studentIdLabel.getText());
        studentPersonalData.setFirstName(updateFirstNameField.getText());
        studentPersonalData.setLastName(updateLastNameField.getText());
        studentPersonalData.setGender(updateGenderComboBox.getSelectionModel().getSelectedItem());
        studentPersonalData.setDateOfBirth(updateDateOfBirthPicker.getValue().toString());
        studentPersonalData.setParentName(updateParentNameField.getText());
        studentPersonalData.setParentPhoneNumber(updateParentPhoneField.getText());
        studentPersonalData.setGradeId(updateClassComboBox.getSelectionModel().getSelectedItem().getLevelId());
        studentPersonalData.setHomeTown(updateHomeTownField.getText());
        studentPersonalData.setStatus(updateStatusComboBox.getSelectionModel().getSelectedItem().toString());

        utilityClass.processingButton(updateStudentInfoBtn);
        utilityClass.updateStudentPersonalData(studentPersonalData,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(updateStudentInfoBtn, "UPDATE INFO");
                        utilityClass.showSuccess(notification, utilityClass.successMessageContainer);
                        showUpdateInfoCard();
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(updateStudentInfoBtn, "UPDATE INFO");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                }, () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(updateStudentInfoBtn, "UPDATE INFO");
                        utilityClass.showError(notification, "Network Error! Check your internet connection and try again");
                    });
                });
    }

    @FXML
    public void handleCancelInfoUpdate() {
        showUpdateInfoCard();
    }

    @FXML
    public void showAddSubjectCard() {
        subjectNameField.clear();
        gradeComboBox.getSelectionModel().clearSelection();

        List<GradeCaching> gradeCaching  = utilityClass.gradeCacheManager.getGradesCache();
        if (gradeCaching != null && !gradeCaching.isEmpty()) {
            if (gradeComboBox.getItems().isEmpty()) {
                gradeComboBox.getItems().addAll(gradeCaching);
            }
        }

        switchPageCards(addNewSubjectCard, printClassSubjectsCard, addSubjectToggleBtn, printSubjectsToggleBtn);
    }

    @FXML
    public void handleFindStudent() {
        String searchId = studentSearchField.getText();
        if (searchId.isEmpty()) {
            utilityClass.showError(notification, "Enter search parameter");
            return;
        }

        String searchParameter1 = "empty";
        String searchParameter2 = "empty";
        String searchParameter3 = "empty";
        if (searchId.startsWith("STD")) {
            searchParameter1 = searchId;
        } else {
            String[] searchParameters = searchId.split("\\s+");
            if (searchParameters.length < 2) {
                utilityClass.showError(notification, "Enter student full name or enter student id");
                return;
            } else if (searchParameters.length == 3) {
                searchParameter1 = searchParameters[0];
                searchParameter2 = searchParameters[1];
                searchParameter3 = searchParameters[2];
            }
            System.out.println(Arrays.toString(searchParameters));
            searchParameter1 = searchParameters[0];
            searchParameter2 = searchParameters[1];
            searchParameter3 = "empty";
        }

        utilityClass.processingButton(findStudentBtn);
        utilityClass.searchStudentData(searchParameter1, searchParameter2, searchParameter3,
                () -> {
            // On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(findStudentBtn, "Find Student");
                        utilityClass.showSuccess(notification, "Student Found");
                        studentInfoUpdatePanel(utilityClass.updateStudentPersonalData);
                    });
                },
                () -> {
            //On failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(findStudentBtn, "Find Student");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(findStudentBtn, "Find Student");
                        utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    private void studentInfoUpdatePanel(UpdateStudentPersonalData data) {
        List<GradeCaching> gradeCachings  = utilityClass.gradeCacheManager.getGradesCache();
        GradeCaching updateGrade = null;
        if (gradeCachings != null && !gradeCachings.isEmpty()) {
            for (GradeCaching gradeCaching1 : gradeCachings) {
                if (gradeCaching1.getLevelId().equals(data.getGradeId())) {
                    updateGrade = gradeCaching1;
                    break;
                }
            }
        }

        //Populate update panel
        studentIdLabel.setText(data.getStudentId());
        updateGradeNameLabel.setText(updateGrade.getLevelName() == null ? "" : updateGrade.getLevelName());
        updateFirstNameField.setText(data.getFirstName());
        updateLastNameField.setText(data.getLastName());

        if (updateGenderComboBox.getItems().isEmpty()) {
            updateGenderComboBox.getItems().addAll("Male", "Female");
        }

        updateGenderComboBox.getSelectionModel().select(data.getGender());
        updateDateOfBirthPicker.setValue(LocalDate.parse(data.getDateOfBirth()));
        updateHomeTownField.setText(data.getHomeTown());
        updateParentNameField.setText(data.getParentName());
        updateParentPhoneField.setText(data.getParentPhoneNumber());
        updateClassComboBox.getSelectionModel().select(updateGrade);
        studentStatusLabel.setText(data.getStatus());
        if (data.getStatus().equals("ACTIVE")) {
            studentStatusLabel.setTextFill(Color.web("#16A34A")); // green

        } else if (data.getStatus().equals("COMPLETED")) {
            studentStatusLabel.setTextFill(Color.web("#38BDF8")); // light blue

        } else {
            studentStatusLabel.setTextFill(Color.web("#DC2626")); // red
        }

        if (updateStatusComboBox.getItems().isEmpty()) {
            updateStatusComboBox.getItems().addAll(StudentStatus.values());
        }
        updateStatusComboBox.getSelectionModel().select(StudentStatus.valueOf(data.getStatus()));

        studentSearchFieldBox.setVisible(false);
        studentSearchFieldBox.setManaged(false);

        personalInfo.setVisible(true);
        personalInfo.setManaged(true);

        guardianInfo.setVisible(true);
        guardianInfo.setManaged(true);

        gradeInfo.setVisible(true);
        gradeInfo.setManaged(true);

        statusInfo.setVisible(true);
        statusInfo.setManaged(true);

        updateButtonVBox.setVisible(true);
        updateButtonVBox.setManaged(true);
    }

    @FXML
    public void showPrintSubjectCard() {
        printClassSubjectsComboBox.getSelectionModel().clearSelection();

        List<GradeCaching> gradeCaching  = utilityClass.gradeCacheManager.getGradesCache();
        if (gradeCaching != null && !gradeCaching.isEmpty()) {
            if (printClassSubjectsComboBox.getItems().isEmpty()) {
                printClassSubjectsComboBox.getItems().addAll(gradeCaching);
            }
        }

        switchPageCards(printClassSubjectsCard, addNewSubjectCard, printSubjectsToggleBtn, addSubjectToggleBtn);
    }

    @FXML
    public void displayMarkAttendanceCard() {
        markAttendanceGradeComboBox.getSelectionModel().clearSelection();

        List<GradeCaching> gradeCaching = utilityClass.gradeCacheManager.getGradesCache();
        if (gradeCaching != null && !gradeCaching.isEmpty()) {
            if (markAttendanceGradeComboBox.getItems().isEmpty()) {
                markAttendanceGradeComboBox.getItems().addAll(gradeCaching);
            }
        }
        markAttendanceTableContainer.setVisible(false);
        markAttendanceTableContainer.setManaged(false);

        markAttendanceInfoBox.setVisible(false);
        markAttendanceInfoBox.setManaged(false);

        markAttendanceSelection.setVisible(true);
        markAttendanceSelection.setManaged(true);

        switchPageCards(markAttendancePanel, updateAttendancePanel, markAttendanceCard, updateAttendanceCard);
    }

    @FXML
    public void displayUpdateAttendanceCard() {
        updateAttendanceGradeComboBox.getSelectionModel().clearSelection();

        List<GradeCaching> gradeCaching = utilityClass.gradeCacheManager.getGradesCache();
        if (gradeCaching != null && !gradeCaching.isEmpty()) {
            if (updateAttendanceGradeComboBox.getItems().isEmpty()) {
                updateAttendanceGradeComboBox.getItems().addAll(gradeCaching);
            }
        }

        updateAttendanceTableContainer.setVisible(false);
        updateAttendanceTableContainer.setManaged(false);

        updateAttendanceInfoBox.setVisible(false);
        updateAttendanceInfoBox.setManaged(false);

        updateAttendanceSelection.setVisible(true);
        updateAttendanceSelection.setManaged(true);

        switchPageCards(updateAttendancePanel, markAttendancePanel, updateAttendanceCard, markAttendanceCard);
    }

    @FXML
    public void showAddStudentCard() {
        clearForm();

        if (genderComboBox.getItems().isEmpty()) {
            genderComboBox.getItems().addAll("Male", "Female");
        }

        List<GradeCaching> gradeCaching = utilityClass.gradeCacheManager.getGradesCache();
        if (gradeCaching != null && !gradeCaching.isEmpty()) {
            if (classComboBox.getItems().isEmpty()) {
                classComboBox.getItems().addAll(gradeCaching);
            }
        }

        switchPageCards(addStudentCard, updateStudentInfoCard,
                addStudentToggleBtn, updateStudentToggleBtn);
    }

    @FXML
    public void showUpdateInfoCard() {
        personalInfo.setVisible(false);
        personalInfo.setManaged(false);

        guardianInfo.setVisible(false);
        guardianInfo.setManaged(false);

        gradeInfo.setVisible(false);
        gradeInfo.setManaged(false);

        statusInfo.setVisible(false);
        statusInfo.setManaged(false);

        updateButtonVBox.setVisible(false);
        updateButtonVBox.setManaged(false);

        studentSearchFieldBox.setVisible(true);
        studentSearchFieldBox.setManaged(true);

        studentSearchField.clear();

        switchPageCards(updateStudentInfoCard, addStudentCard,
                updateStudentToggleBtn, addStudentToggleBtn);
    }

    @FXML
    public void initializeAfterLogin() {
        utilityClass = new UtilityClass(this);
        setActiveButton(dashboardBtn);
        loadStaffInfo();
        showLoading();
        utilityClass.loadStaffDashboardData(staffId, notification,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        gradeInformation = utilityClass.gradeInfoCacheManager.getGradesInformation();
                        if (gradeInformation != null && !gradeInformation.isEmpty()) {
                            loadDashBoardData(gradeInformation.getFirst());
                            startDashboardChanges();
                        }

                        hideLoading();
                    });
                });
    }

    @FXML
    public void showPreviousClass() {
        if (gradeInformation.size() <= 1) {
            utilityClass.showError(notification, "Sorry, you've been assigned to only one class");
            return;
        }

        pauseRotation();

        currentDisplayIndex--;
        if (currentDisplayIndex < 0) {
            currentDisplayIndex = gradeInformation.size() - 1;
        }

        loadDashBoardData(gradeInformation.get(currentDisplayIndex));
    }

    @FXML
    public void showNextClass() {
        if (gradeInformation.size() <= 1) {
            utilityClass.showError(notification, "Sorry, you've been assigned to only one class");
            return;
        }

        pauseRotation();

        currentDisplayIndex++;
        if (currentDisplayIndex >= gradeInformation.size()) {
            currentDisplayIndex = 0;
        }

        loadDashBoardData(gradeInformation.get(currentDisplayIndex));
    }

    private void switchPageCards(VBox cardToShow, VBox cardToHide,
                                 Button actionButton, Button inactiveButton) {
        cardToHide.setVisible(false);
        cardToHide.setManaged(false);

        cardToShow.setOpacity(0);
        cardToShow.setTranslateY(20);
        cardToShow.setVisible(true);
        cardToShow.setManaged(true);

        FadeTransition fade = new FadeTransition(Duration.millis(180), cardToShow);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(180), cardToShow);
        slide.setFromY(20);
        slide.setToY(0);

        new ParallelTransition(fade, slide).play();

        setActiveToggle(actionButton, inactiveButton);
    }

    private void setActiveToggle(Button active, Button inactive) {
        active.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: white;");
        inactive.setStyle("-fx-background-color: transparent; -fx-text-fill: #334155;");
    }


    private void populateClassResults(List<ViewClassSemesterReport> viewClassSemesterReports) {
        gradeNameLabel.setText(viewClassSemesterReports.getFirst().getClassName());
        semesterNameLabel.setText(viewClassSemesterReports.getFirst().getSemesterName());

        classResultsTable.getItems().setAll(viewClassSemesterReports);

        viewResultsHBox.setVisible(false);
        viewResultsHBox.setManaged(false);

        resultsInfoBox.setVisible(true);
        resultsInfoBox.setManaged(true);

        classResultsTableContainer.setVisible(true);
        classResultsTableContainer.setManaged(true);
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

    private void saveStudent(Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        // Get values
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String gender = genderComboBox.getValue();
        LocalDate dob = dateOfBirthPicker.getValue();
        String homeTown = homeTownField.getText().trim();
        String parentName = parentNameField.getText().trim();
        String parentPhone = parentPhoneField.getText().trim();

        GradeCaching selectedClass = classComboBox.getSelectionModel().getSelectedItem();
        String selectedGradeId;
        if (selectedClass == null) {
            utilityClass.showError(notification, "No grade selected!");
            return;
        }
        selectedGradeId = selectedClass.getLevelId();

        utilityClass.enrollNewStudent(firstName, lastName, selectedGradeId, gender,
                dob, homeTown, parentName, parentPhone, staffId, onSuccess, onFailure, onNetworkIssue);
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

    private boolean validateForm() {
        if (firstNameField.getText().trim().isEmpty()) {
            utilityClass.showError(notification, "First name is required");
            return false;
        }
        if (lastNameField.getText().trim().isEmpty()) {
            utilityClass.showError(notification, "Last name is required");
            return false;
        }
        if (genderComboBox.getValue() == null) {
            utilityClass.showError(notification, "Please select a gender");
            return false;
        }
        if (dateOfBirthPicker.getValue() == null) {
            utilityClass.showError(notification, "Date of birth is required");
            return false;
        }
        if (parentNameField.getText().trim().isEmpty()) {
            utilityClass.showError(notification, "Parent name is required");
            return false;
        }
        if (parentPhoneField.getText().trim().isEmpty()) {
            utilityClass.showError(notification, "Parent phone number is required");
            return false;
        }
        if (classComboBox.getValue() == null) {
            utilityClass.showError(notification, "Please select a class");
            return false;
        }
        return true;
    }

    private boolean validateUpdateForm() {
        if (updateFirstNameField.getText().trim().isEmpty()) {
            utilityClass.showError(notification, "First name is required");
            return false;
        }
        if (updateLastNameField.getText().trim().isEmpty()) {
            utilityClass.showError(notification, "Last name is required");
            return false;
        }
        if (updateGenderComboBox.getValue() == null) {
            utilityClass.showError(notification, "Please select a gender");
            return false;
        }
        if (updateDateOfBirthPicker.getValue() == null) {
            utilityClass.showError(notification, "Date of birth is required");
            return false;
        }
        if (updateParentNameField.getText().trim().isEmpty()) {
            utilityClass.showError(notification, "Parent name is required");
            return false;
        }
        if (updateParentPhoneField.getText().trim().isEmpty()) {
            utilityClass.showError(notification, "Parent phone number is required");
            return false;
        }
        if (updateClassComboBox.getValue() == null) {
            utilityClass.showError(notification, "Please select a class");
            return false;
        }
        return true;
    }

    private void checkCompletion(int saved, int failed, int total, Button button) {
        if (saved + failed == total) {
            Platform.runLater(() -> {
                utilityClass.notProcessingButton(button, "SAVE SCORES");
                if (failed == 0) {
                    utilityClass.showSuccess(notification, "All " + saved + " scores saved successfully!");
                    subjectInfoBox.setVisible(false);
                    subjectInfoBox.setManaged(false);

                    noStudentsBox.setVisible(false);
                    noStudentsBox.setManaged(false);

                    resultsTableContainer.setVisible(false);
                    resultsTableContainer.setManaged(false);
                    studentsScoresTable.getItems().clear();

                    subjectResultSearchBox.setVisible(true);
                    subjectResultSearchBox.setManaged(true);
                } else {
                    utilityClass.showError(notification, utilityClass.errorMessageContainer);
                }

            });
        }
    }

    public void displaySubjectStudents(SubjectScores subjectScores) {
        subjectNameLabel.setText(subjectScores.getSubjectName());
        subjectCodeLabel.setText(subjectScores.getSubjectId());

        List<StudentsScoresTable> scores = subjectScores.getScoresTable();
        if (scores != null) {
            studentsScoresTable.getItems().setAll(scores);
        } else {
            studentsScoresTable.getItems().clear();
        }

        subjectInfoBox.setVisible(true);
        subjectInfoBox.setManaged(true);

        noStudentsBox.setVisible(false);
        noStudentsBox.setManaged(false);

        resultsTableContainer.setVisible(true);
        resultsTableContainer.setManaged(true);
    }

    public void noStudentsFound() {
        noStudentsBox.setVisible(true);
        noStudentsBox.setManaged(true);

        subjectInfoBox.setVisible(false);
        noStudentsBox.setManaged(false);

        resultsTableContainer.setVisible(false);
        resultsTableContainer.setManaged(false);
    }

    //SET STAFF DETAILS
    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public void setStaffFullName(String staffFullName) {
        this.staffFullName = staffFullName;
    }

    private void loadDashBoardData(GradeInformation gradeInformation) {
        updateDateTime();
        classNameLabel.setText(gradeInformation.getGradeName());
        totalStudentsLabel.setText(gradeInformation.getGradeSize());

        studentsTable.getItems().setAll(gradeInformation.getStudentRoasters());
    }

    private void startDashboardChanges() {
        gradeRotationTimeline = new Timeline(
                new KeyFrame(Duration.seconds(30), event -> {
                    if (!isRotationPaused) {
                        rotateDashBoard();
                    }
                })
        );

        gradeRotationTimeline.setCycleCount(Timeline.INDEFINITE);
        gradeRotationTimeline.play();
    }

    private void rotateDashBoard() {
        if (gradeInformation.size() <= 1) return;

        currentDisplayIndex = (currentDisplayIndex + 1) % gradeInformation.size();

        animateDashboardChange(() -> {
            loadDashBoardData(gradeInformation.get(currentDisplayIndex));
        });
    }

    private void animateDashboardChange(Runnable onMidway) {
        FadeTransition fadeOut = new FadeTransition(
                Duration.millis(250), dashboardView
        );

        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(event -> {
            onMidway.run();

            FadeTransition fadeIn = new FadeTransition(
                    Duration.millis(250), dashboardView
            );
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });

        fadeOut.play();
    }

    private void pauseRotation() {
        isRotationPaused = true;

        resumeTimer.stop();
        resumeTimer.setOnFinished(event -> isRotationPaused = false);
        resumeTimer.playFromStart();
    }

    private void loadStaffInfo() {
        if (institutionName != null) {
            institutionalNameLabel.setText(institutionName);
        }
        if (staffFullName != null) {
            staffNameLabel.setText(staffFullName);
        }

        versionLabel.setText(utilityClass.applicationVersion);
    }

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy | HH:mm:ss");
        dateTimeLabel.setText(now.format(formatter));
    }

    private void SwitchView(Button activeButton, ScrollPane viewToShow) {

        dashboardView.setVisible(false);
        dashboardView.setManaged(false);

        AddNewSubjectScoresView.setVisible(false);
        AddNewSubjectScoresView.setManaged(false);

        addNewStudentView.setVisible(false);
        addNewStudentView.setManaged(false);

        addNewSubjectView.setVisible(false);
        addNewSubjectView.setManaged(false);

        manageResultsView.setVisible(false);
        manageResultsView.setManaged(false);

        attendanceView.setVisible(false);
        attendanceView.setManaged(false);

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
        addStudentBtn.setStyle(INACTIVE_BUTTON);
        addResultsBtn.setStyle(INACTIVE_BUTTON);
        addSubjectBtn.setStyle(INACTIVE_BUTTON);
        addResultsBtn.setStyle(INACTIVE_BUTTON);
        resultsBtn.setStyle(INACTIVE_BUTTON);
        attendanceBtn.setStyle(INACTIVE_BUTTON);

        activeButton.setStyle(ACTIVE_BUTTON);
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