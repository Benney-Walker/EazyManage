package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.Constants.AttendanceStatus;
import com.bbquantum.eazymanage.DTO.Academics.*;
import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.DTO.Institution.SemesterCaching;
import com.bbquantum.eazymanage.DTO.Institution.StudentAttendance;
import com.bbquantum.eazymanage.DTO.Institution.UpdateStudentPersonalData;
import com.bbquantum.eazymanage.Utitity.RadioStatusCell;
import com.bbquantum.eazymanage.Utitity.StudentAttendanceUI;
import com.bbquantum.eazymanage.Utitity.ApiUtilityClass;
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
import java.util.List;
import java.util.ResourceBundle;

public class StaffController implements Initializable {

    // CONTROLLER OWN VARIABLES
    private ApiUtilityClass apiUtilityClass;
    private String institutionName;
    public String staffId;
    private String staffFullName;

    //Grades information and dashboard rotation
    private PauseTransition resumeTimer =
            new PauseTransition(Duration.seconds(15));

    private List<GradeInformation> gradeInformation;

    private boolean isRotationPaused = false;
    private int currentDisplayIndex;

    //Holds results for printing report cards
    private GradeInformation resultGrade;

    //Semester and Grade for loading and downloading results
    private SemesterCaching resultSemester;

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
    @FXML private Button switchRole;
    @FXML private Label staffNameLabel;
    @FXML private Label institutionalNameLabel;

    // MAIN DASHBOARD VIEW
    @FXML private StackPane contentArea;
    @FXML private ScrollPane dashboardView;
    @FXML private VBox mainDashboard;
    @FXML private Label dateTimeLabel;
    @FXML private Label classNameLabel;
    @FXML private Label totalStudentsLabel;
    @FXML private Button prevClassBtn;
    @FXML private Button nextClassBtn;
    @FXML public TableView<StudentsRoaster> studentsTable;
    @FXML private TableColumn<StudentsRoaster, String> studentIdCol;
    @FXML private TableColumn<StudentsRoaster, String> studentNameCol;
    @FXML private TableColumn<StudentsRoaster, String> genderCol;

    //NEW SUBJECT SCORES
    @FXML private ScrollPane AddNewSubjectScoresView;

    @FXML private VBox score_subjectSelectionBox;
    @FXML private ComboBox<SubjectsHolder> score_subSelectionBox;
    @FXML private ComboBox<GradeCaching> score_classSelectionBox;
    @FXML private Button loadSubjectStudentsBtn;

    @FXML private VBox score_studentListPanel;
    @FXML private Label subjectNameLabel;
    @FXML private Label subjectCodeLabel;
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
    @FXML private Button saveScores;

    @FXML private VBox saveScoresSemesterVBox;
    @FXML private ComboBox<SemesterCaching> saveScoresSemesterComboBox;

    // MANAGE STUDENTS
    @FXML private ScrollPane addNewStudentView;

    @FXML private VBox studentPersonalInfo;
    @FXML private Label studentIdLabel;
    @FXML private Label studentStatusLabel;
    @FXML private Label updateGradeNameLabel;
    @FXML private Label updateFirstNameField;
    @FXML private Label updateLastNameField;
    @FXML private Label updateGenderComboBox;
    @FXML private Label updateDateOfBirthPicker;
    @FXML private Label updateHomeTownField;
    @FXML private Label updateParentNameField;
    @FXML private Label updateParentPhoneField;

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
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> classTest1Col;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> groupWorkCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> classTest2Col;
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

        studentsTable.setOnMousePressed(event -> pauseRotation());
        studentsTable.setOnMouseClicked(event -> pauseRotation());
        studentsTable.setOnScroll(event -> pauseRotation());

        studentsTable.setRowFactory(tableView -> {
            TableRow<StudentsRoaster> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    StudentsRoaster studentsRoaster = row.getItem();

                    handleFindStudent(studentsRoaster.getStudentId());
                }
            });
            return row;
        });



        //SCORES TABLE
        studentIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentId()));

        studentNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentName()));

        classScoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClassScore()));

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

        calExamScoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCalculatedExamScore()));


        classTest1Column.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        groupWorkColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        classTest2Column.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        projectColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        examScoreColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));

        //Class score set to uneditable
        classScoreColumn.setEditable(false);
        calExamScoreColumn.setEditable(false);

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
        gradeInformation = UtilityClass.gradeInfoCacheManager.getGradesInformation();
        if (gradeInformation != null && !gradeInformation.isEmpty()) {
            loadDashBoardData(gradeInformation.getFirst());
            startDashboardChanges();
        }

        studentPersonalInfo.setVisible(false);
        studentPersonalInfo.setManaged(false);

        mainDashboard.setVisible(true);
        mainDashboard.setManaged(true);

        SwitchView(dashboardBtn, dashboardView);
    }

    @FXML
    public void showAddResults() {
        studentsScoresTable.getItems().clear();
        score_subSelectionBox.getItems().clear();

        score_studentListPanel.setVisible(false);
        score_studentListPanel.setManaged(false);

        score_subjectSelectionBox.setVisible(true);
        score_subjectSelectionBox.setManaged(true);

        List<GradeCaching> cachedGrades = UtilityClass.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (score_classSelectionBox.getItems().isEmpty()) {
                score_classSelectionBox.getItems().addAll(cachedGrades);
            }
        }

        SwitchView(addResultsBtn, AddNewSubjectScoresView);
    }

    @FXML
    public void showResultsPage() {
        loadResultsPage();

        SwitchView(resultsBtn, manageResultsView);
    }

    @FXML
    public void showAttendancePage() {
        SwitchView(attendanceBtn, attendanceView);
        displayMarkAttendanceCard();
    }

    @FXML
    public void handleOpenSwitchRole() throws IOException {

        Stage staffDashboard = (Stage) switchRole.getScene().getWindow();

        // Load the appropriate dashboard
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/SwitchRole/SwitchRole.fxml"));
        Scene scene = new Scene(loader.load());

        // Create new stage for dashboard
        Stage switchRoleStage = new Stage();
        switchRoleStage.setTitle("EazyManage - User Roles");
        switchRoleStage.setScene(scene);
        switchRoleStage.sizeToScene();
        switchRoleStage.setResizable(false);

        staffDashboard.close();
        switchRoleStage.show();
    }

    @FXML
    public void refreshDashboard() {
        showLoading();
        apiUtilityClass.loadStaffDashboardData(staffId, notification,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        gradeInformation = UtilityClass.gradeInfoCacheManager.getGradesInformation();
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
            UtilityClass.showError(notification, "Sorry, you've been assigned to only one class");
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
            UtilityClass.showError(notification, "Sorry, you've been assigned to only one class");
            return;
        }

        pauseRotation();

        currentDisplayIndex++;
        if (currentDisplayIndex >= gradeInformation.size()) {
            currentDisplayIndex = 0;
        }

        loadDashBoardData(gradeInformation.get(currentDisplayIndex));
    }

    @FXML
    public void fetchGradeSubjects() {
        GradeCaching selectedGrade = score_classSelectionBox.getSelectionModel().getSelectedItem();
        if ( selectedGrade == null) return;

        showLoading();

        apiUtilityClass.loadClassSubjects(selectedGrade.getLevelId(), staffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        score_subSelectionBox.getItems().clear();
                        score_subSelectionBox.getItems().addAll(UtilityClass.subjectsHolder);
                        hideLoading();
                        UtilityClass.showSuccess(notification, "Subjects loaded");
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        hideLoading();
                        UtilityClass.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        hideLoading();
                        UtilityClass.showError(notification, "Network error! Check your internet connection");
                    });
                });
    }

    @FXML
    public void handleLoadStudentsForScores() {
        SubjectsHolder selectedSubject = score_subSelectionBox.getSelectionModel().getSelectedItem();
        if (selectedSubject == null) {
            UtilityClass.showError(notification, "Subject not selected");
            return;
        }
        UtilityClass.processingButton(loadSubjectStudentsBtn);
        apiUtilityClass.loadSubjectStudents(selectedSubject.getSubjectId(), staffId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(loadSubjectStudentsBtn, "LOAD STUDENTS");
                        displaySubjectStudents(UtilityClass.subjectScores);
                        UtilityClass.showSuccess(notification, "Successfully loaded students");

                        score_subjectSelectionBox.setVisible(false);
                        score_subjectSelectionBox.setManaged(false);
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(loadSubjectStudentsBtn, "LOAD STUDENTS");
                        UtilityClass.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                ()-> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(loadSubjectStudentsBtn, "LOAD STUDENTS");
                        UtilityClass.showError(notification, "Network error! Check your network connection and try again!");
                    });
                });
    }

    @FXML
    public void handleClearScores() {
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

    @FXML
    public void handleCancelScoreUpload() {
        score_studentListPanel.setVisible(false);
        score_studentListPanel.setManaged(false);

        score_subjectSelectionBox.setVisible(true);
        score_subjectSelectionBox.setManaged(true);
    }

    @FXML
    public void handleSaveScores() {
        String subjectId = subjectCodeLabel.getText();
        System.out.println("SUBJECT ID: " + subjectId);
        SemesterCaching selectedSemester = saveScoresSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            UtilityClass.showError(notification, "Semester not selected");
            return;
        }

        UtilityClass.processingButton(saveScores);
        String semesterId = selectedSemester.getSemesterId();

        scores_SaveProgressBarVBox.setVisible(true);
        scores_SaveProgressBarVBox.setManaged(true);

        uploadNextScore(subjectId, semesterId, staffId);
    }

    @FXML
    public void handleLoadClassResults() {
        resultGrade = resultsGradeComboBox.getSelectionModel().getSelectedItem();
        if (resultGrade == null) {
            UtilityClass.showError(notification, "Please select a grade to load");
            return;
        }

        resultSemester = resultsSemesterComboBox.getSelectionModel().getSelectedItem();
        if (resultSemester == null) {
            UtilityClass.showError(notification, "Select Semester to Load Results");
            return;
        }

        String gradeId = resultGrade.getGradeId();
        String semesterId = resultSemester.getSemesterId();
        UtilityClass.processingButton(loadClassResultsBtn);

        loadClassResults(gradeId, semesterId);
    }

    @FXML
    public void handleMovePassedStudents() {

        String currentGradeId = resultGrade.getGradeId();

        UtilityClass.processingButton(movePassedStudentsBtn);
        apiUtilityClass.movePassedStudents(currentGradeId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(movePassedStudentsBtn, "TRANSFER STUDENTS");
                        UtilityClass.showSuccess(notification, apiUtilityClass.successMessageContainer);
                        resultsPassmarkHBox.setVisible(false);
                        resultsPassmarkHBox.setManaged(false);
                        movePassedStudentsBtn.setDisable(true);
                    });
                }, () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(movePassedStudentsBtn, "TRANSFER STUDENTS");
                        UtilityClass.showError(notification, apiUtilityClass.errorMessageContainer);
                        movePassedStudentsBtn.setDisable(true);
                    });
                }, () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(movePassedStudentsBtn, "TRANSFER STUDENTS");
                        UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
                        movePassedStudentsBtn.setDisable(true);
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
    public void backToDashboard() {
        showDashboard();
    }

    @FXML
    public void loadAttendanceStudents() {
        GradeCaching gradeCaching = markAttendanceGradeComboBox.getSelectionModel().getSelectedItem();
        if (gradeCaching == null) {
            UtilityClass.showError(notification, "Select Grade");
            return;
        }

        UtilityClass.processingButton(markAttendanceBtn);
        String levelId = gradeCaching.getLevelId();

        apiUtilityClass.loadStudentsForAttendance(levelId, "no_date",
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        attendanceData.clear();
                        for(StudentAttendance studentAttendance: studentAttendances) {
                            attendanceData.add(new StudentAttendanceUI(studentAttendance));
                        }

                        displayAttendanceList(gradeCaching);

                        UtilityClass.notProcessingButton(markAttendanceBtn, "FETCH STUDENTS");
                        UtilityClass.showSuccess(notification, "Students Loaded Successfully");
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(markAttendanceBtn, "FETCH STUDENTS");
                        UtilityClass.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(markAttendanceBtn, "FETCH STUDENTS");
                        UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void loadAttendanceForUpdate() {
        GradeCaching gradeCaching = updateAttendanceGradeComboBox.getSelectionModel().getSelectedItem();
        if (gradeCaching == null) {
            UtilityClass.showError(notification, "Select Grade");
            return;
        }
        String levelId = gradeCaching.getLevelId();

        if (!updateAttendanceDatePicker.isVisible()) {
            updateAttendanceDatePicker.setVisible(true);
            updateAttendanceDatePicker.setManaged(true);
        }

        selectedAttendanceDate = updateAttendanceDatePicker.getValue();
        if (selectedAttendanceDate == null) {
            UtilityClass.showError(notification, "Select Date to update");
            return;
        }

        if (selectedAttendanceDate.isBefore(LocalDate.now())) {
            UtilityClass.showError(notification, "You can't update previous attendance records");
            return;
        }

        String date = selectedAttendanceDate.toString();

        UtilityClass.processingButton(updateAttendanceBtn);

        apiUtilityClass.loadStudentsForAttendance(levelId, date,
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

                        UtilityClass.notProcessingButton(updateAttendanceBtn, "FETCH STUDENTS");
                        UtilityClass.showSuccess(notification, "Students Loaded Successfully");
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(updateAttendanceBtn, "FETCH STUDENTS");
                        UtilityClass.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(updateAttendanceBtn, "FETCH STUDENTS");
                        UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
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
        UtilityClass.processingButton(saveAttendanceBtn);
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



    @FXML
    public void handleUpdateAttendance() {
        UtilityClass.processingButton(updateAndSaveAttendanceBtn);
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

    @FXML
    public void displayMarkAttendanceCard() {
        markAttendanceGradeComboBox.getSelectionModel().clearSelection();

        List<GradeCaching> gradeCaching = UtilityClass.gradeCacheManager.getGradesCache();
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

        UtilityClass.switchPageCards(markAttendancePanel, updateAttendancePanel, markAttendanceCard, updateAttendanceCard);
    }

    @FXML
    public void displayUpdateAttendanceCard() {
        updateAttendanceGradeComboBox.getSelectionModel().clearSelection();

        List<GradeCaching> gradeCaching = UtilityClass.gradeCacheManager.getGradesCache();
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

        UtilityClass.switchPageCards(updateAttendancePanel, markAttendancePanel, updateAttendanceCard, markAttendanceCard);
    }



    /** ---------------UTILITY METHODS FOR THIS CLASS--------------- **/

    public void initializeAfterLogin() {
        apiUtilityClass = new ApiUtilityClass(this);
        setActiveButton(dashboardBtn);
        loadStaffInfo();
        showLoading();
        apiUtilityClass.loadStaffDashboardData(staffId, notification,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        gradeInformation = UtilityClass.gradeInfoCacheManager.getGradesInformation();
                        if (gradeInformation != null && !gradeInformation.isEmpty()) {
                            loadDashBoardData(gradeInformation.getFirst());
                            startDashboardChanges();
                        }

                        hideLoading();
                    });
                });
    }

    private void loadClassResults(String gradeId, String semesterId) {
        apiUtilityClass.loadClassResults(gradeId, semesterId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(loadClassResultsBtn, "Fetch Results");
                        UtilityClass.showSuccess(notification, "Results successfully loaded");
                        populateClassResults(UtilityClass.classResults);
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(loadClassResultsBtn, "Fetch Results");
                        UtilityClass.showError(notification, apiUtilityClass.errorMessageContainer);

                        resultsInfoBox.setVisible(false);
                        resultsInfoBox.setManaged(false);
                        classResultsTableContainer.setVisible(false);
                        classResultsTableContainer.setManaged(false);
                    });
                }, () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(loadClassResultsBtn, "Fetch Results");
                        UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    private void loadResultsPage() {
        viewResultsHBox.setVisible(true);
        viewResultsHBox.setManaged(true);

        resultsInfoBox.setVisible(false);
        resultsInfoBox.setManaged(false);

        classResultsTableContainer.setVisible(false);
        classResultsTableContainer.setManaged(false);

        List<GradeInformation> gradesInformation = UtilityClass.gradeInfoCacheManager.getGradesInformation();
        if (gradesInformation != null && !gradesInformation.isEmpty()) {
            if (resultsGradeComboBox.getItems().isEmpty()) {
                resultsGradeComboBox.getItems().addAll(gradesInformation);
            }
        }

        List<SemesterCaching> semesterCaching = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (semesterCaching != null && !semesterCaching.isEmpty()) {
            if (resultsSemesterComboBox.getItems().isEmpty()) {
                resultsSemesterComboBox.getItems().addAll(semesterCaching);
            }
        }
    }

    private void uploadNextScore(String subjectId, String semesterId, String staffId) {
        if (
                currentUploadIndex >= UtilityClass.subjectScores.getScoresTable().size()
        ) {
            UtilityClass.notProcessingButton(saveScores, "UPLOAD SCORES");

            score_SaveProgressBar.setProgress(0.0);
            scores_SaveProgressBarVBox.setVisible(false);
            scores_SaveProgressBarVBox.setManaged(false);

            currentUploadIndex = 0;
            UtilityClass.subjectScores = null;

            score_studentListPanel.setVisible(false);
            score_studentListPanel.setManaged(false);

            score_subjectSelectionBox.setVisible(true);
            score_subjectSelectionBox.setManaged(true);

            return;
        }

        StudentsScoresTable studentScores =
                UtilityClass.subjectScores.getScoresTable().get(currentUploadIndex);

        apiUtilityClass.saveStudentScores(
                studentScores, subjectId, semesterId, staffId,
                () -> {
                    // On Success
                    Platform.runLater(() -> {
                        currentUploadIndex++;

                        double progress =
                                (double)  currentUploadIndex / UtilityClass.subjectScores.getScoresTable().size();

                        score_SaveProgressBar.setProgress(progress);

                        uploadNextScore(subjectId, semesterId, staffId);
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(saveScores, "CONTINUE");
                        UtilityClass.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(saveScores, "CONTINUE");
                        UtilityClass.showError(notification,
                                "Network Error! Fix connection and click Continue.");
                    });
                }
        );
    }

    private void displayAttendanceList(GradeCaching gradeCaching) {
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
    }

    private void uploadNextAttendance() {

        if (checkUploadAttendanceIndex()) return;

        StudentAttendance record = studentAttendanceList.get(currentUploadIndex);

        apiUtilityClass.markStudentAttendance(
                record.getStudentId(),
                staffId,
                record.getLevelId(),
                record.getStatus(),

                () -> {
                    Platform.runLater(() -> {
                        currentUploadIndex++;

                        double progress = (double) currentUploadIndex / studentAttendanceList.size();
                        attendanceSaveProgressBar.setProgress(progress);

                        uploadNextAttendance();
                    });
                },

                () -> {
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(saveAttendanceBtn, "CONTINUE");
                        UtilityClass.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },

                () -> {
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(saveAttendanceBtn, "CONTINUE");
                        UtilityClass.showError(notification,
                                "Network Error! Fix connection and click Continue.");
                    });
                }
        );
    }

    private boolean checkUploadAttendanceIndex() {
        if (currentUploadIndex >= studentAttendanceList.size()) {
            // Finished
            UtilityClass.notProcessingButton(saveAttendanceBtn, "SAVE ATTENDANCE");
            UtilityClass.showSuccess(notification, "Attendance Saved Successfully");

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
            return true;
        }
        return false;
    }

    private void uploadUpdateAttendance() {
        if (checkUploadIndex()) return;

        StudentAttendance record = studentAttendanceList.get(currentUploadIndex);

        apiUtilityClass.updateAttendance(
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
                        UtilityClass.notProcessingButton(updateAndSaveAttendanceBtn, "CONTINUE");
                        UtilityClass.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(updateAndSaveAttendanceBtn, "CONTINUE");
                        UtilityClass.showError(notification, "Network Error! Check your network connection and Click on Continue");
                    });
                });
    }

    private boolean checkUploadIndex() {
        if (currentUploadIndex >= studentAttendanceList.size()) {
            UtilityClass.notProcessingButton(updateAndSaveAttendanceBtn, "SAVE ATTENDANCE");

            UtilityClass.showSuccess(notification, "Attendance Saved Successfully");

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
            return true;
        }
        return false;
    }

    private void handleFindStudent(String studentId) {
        showLoading();
        apiUtilityClass.searchStudentData(staffId, studentId,
                () -> {
                    // On Success
                    Platform.runLater(() -> {
                        hideLoading();
                        UtilityClass.showSuccess(notification, "Student Found");
                        studentInfoDisplayPanel(UtilityClass.updateStudentPersonalData);
                        studentPersonalInfo.setVisible(true);
                        studentPersonalInfo.setManaged(true);

                        mainDashboard.setVisible(false);
                        mainDashboard.setManaged(false);
                    });
                },
                () -> {
                    //On failure
                    Platform.runLater(() -> {
                        hideLoading();
                        UtilityClass.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        hideLoading();
                        UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    private void studentInfoDisplayPanel(UpdateStudentPersonalData data) {
        List<GradeCaching> gradeCachings  = UtilityClass.gradeCacheManager.getGradesCache();
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
        updateGenderComboBox.setText(data.getGender());
        updateDateOfBirthPicker.setText(data.getDateOfBirth());
        updateHomeTownField.setText(data.getHomeTown());
        updateParentNameField.setText(data.getParentName());
        updateParentPhoneField.setText(data.getParentPhoneNumber());
        studentStatusLabel.setText(data.getStatus());
        if (data.getStatus().equals("ACTIVE")) {
            studentStatusLabel.setTextFill(Color.web("#16A34A")); // green

        } else if (data.getStatus().equals("COMPLETED")) {
            studentStatusLabel.setTextFill(Color.web("#38BDF8")); // light blue

        } else {
            studentStatusLabel.setTextFill(Color.web("#DC2626")); // red
        }
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

    private void displaySubjectStudents(SubjectScores subjectScores) {
        subjectNameLabel.setText(subjectScores.getSubjectName());

        System.out.println("SUBJECT ID: " + subjectScores.getSubjectId());
        subjectCodeLabel.setText(subjectScores.getSubjectId());

        List<StudentsScoresTable> scores = subjectScores.getScoresTable();
        if (scores != null) {
            if (studentsScoresTable.getItems().isEmpty()) {
                studentsScoresTable.getItems().addAll(scores);
            } else {
                studentsScoresTable.getItems().setAll(scores);
            }
        }

        List<SemesterCaching> cachedSemesters = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (cachedSemesters != null && !cachedSemesters.isEmpty()) {
            if (saveScoresSemesterComboBox.getItems().isEmpty()) {
                saveScoresSemesterComboBox.getItems().addAll(cachedSemesters);
            }
        }

        score_studentListPanel.setVisible(true);
        score_studentListPanel.setManaged(true);
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

    private void loadDashBoardData(GradeInformation gradeInformation) {
        updateDateTime();
        classNameLabel.setText(gradeInformation.getGradeName());
        totalStudentsLabel.setText(gradeInformation.getGradeSize());

        studentsTable.getItems().setAll(gradeInformation.getStudentRoasters());
    }

    private void startDashboardChanges() {
        Timeline gradeRotationTimeline = new Timeline(
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

    private void reCalculateExamScore(StudentsScoresTable row) {
        try {
            double examScore = parseDouble(row.getExamScore());

            double calculatedScore = (examScore/100) * 50;

            row.setCalculatedExamScore(String.format("%.1f", calculatedScore));
        } catch (Exception e) {
            row.setCalculatedExamScore("0");
        }
    }

    private void loadStaffInfo() {
        if (institutionName != null) {
            institutionalNameLabel.setText(institutionName);
        }
        if (staffFullName != null) {
            staffNameLabel.setText(staffFullName);
        }

        versionLabel.setText(apiUtilityClass.applicationVersion);
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
        addResultsBtn.setStyle(INACTIVE_BUTTON);
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
}