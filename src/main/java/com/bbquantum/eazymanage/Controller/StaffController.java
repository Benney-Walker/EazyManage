package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.Constants.AttendanceStatus;
import com.bbquantum.eazymanage.DTO.Academics.*;
import com.bbquantum.eazymanage.DTO.Institution.StudentAttendance;
import com.bbquantum.eazymanage.Utitity.RadioStatusCell;
import com.bbquantum.eazymanage.Utitity.StudentAttendanceUI;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
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
import javafx.stage.Stage;
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
    private UtilityClass utilityClass;
    public SubjectScores subjectScores;
    private String institutionName;
    public String staffId;
    private String staffFullName;
    private String gradeNumber;
    private String gradeName;
    private String gradeSize;

    //Semester Id for loading and downloading results
    private SemesterCaching resultSemester;

    //Passed students response
    public String passedResponse;

    public List<GenerateStudentReport> generateStudentReports;
    public List<StudentAttendance> studentAttendances;
    private final ObservableList<StudentAttendanceUI> attendanceData = FXCollections.observableArrayList();
    private LocalDate selectedAttendanceDate;

    // NAVIGATION
    @FXML public StackPane notification;
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
    @FXML public TableView<GradeStudentsDisplayDTO> studentsTable;
    @FXML private TableColumn<GradeStudentsDisplayDTO, String> studentIdCol;
    @FXML private TableColumn<GradeStudentsDisplayDTO, String> studentNameCol;
    @FXML private TableColumn<GradeStudentsDisplayDTO, String> genderCol;
    @FXML private TableColumn<GradeStudentsDisplayDTO, String> homeTownCol;
    @FXML private TableColumn<GradeStudentsDisplayDTO, String> parentNameCol;
    @FXML private TableColumn<GradeStudentsDisplayDTO, String> parentPhoneCol;

    //NEW SUBJECT SCORES
    @FXML private ScrollPane newSubjectResultView;
    @FXML private TextField subjectCodeField;
    @FXML private HBox subjectInfoBox;
    @FXML private Label subjectNameLabel;
    @FXML private Label subjectCodeLabel;
    @FXML private VBox noStudentsBox;
    @FXML private VBox resultsTableContainer;
    @FXML private Button loadSubjectStudentsBtn;
    @FXML private Button saveScores;
    @FXML private VBox saveScoresSemesterComboBox;
    @FXML public TableView<StudentsScoresTable> studentsScoresTable;
    @FXML private TableColumn<StudentsScoresTable, String> studentIdColumn;
    @FXML private TableColumn<StudentsScoresTable, String> studentNameColumn;
    @FXML private TableColumn<StudentsScoresTable, String> classScoreColumn;
    @FXML private TableColumn<StudentsScoresTable, String> examScoreColumn;

    // ADD STUDENT VIEW
    @FXML private ScrollPane addNewStudentView;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private TextField homeTownField;
    @FXML private TextField parentNameField;
    @FXML private TextField parentPhoneField;
    @FXML private ComboBox<GradeCaching> classComboBox;
    @FXML private Button saveAndClearBtn;

    //ADD SUBJECT VIEW
    @FXML private ScrollPane addNewSubjectView;
    @FXML private TextField subjectNameField;
    @FXML private ComboBox<GradeCaching> gradeComboBox;
    @FXML private ComboBox<SemesterCaching> semesterComboBox;
    @FXML private Button saveSubjectBtn;
    @FXML private ComboBox<GradeCaching> printClassSubjectsComboBox;
    @FXML public Button printSubjectDataBtn;

    //MANAGE RESULTS PAGE
    @FXML private ScrollPane manageResultsView;
    @FXML private ComboBox<SemesterCaching> resultsSemesterComboBox;
    @FXML private Button loadClassResultsBtn;
    @FXML private HBox resultsInfoBox;
    @FXML private Label gradeNameLabel;
    @FXML private Label semesterNameLabel;
    @FXML private VBox noResultsBox;
    @FXML private VBox classResultsTableContainer;
    @FXML private TableView<GenerateStudentReport> classResultsTable;
    @FXML private TableColumn<GenerateStudentReport, String> studentResultIdColumn;
    @FXML private TableColumn<GenerateStudentReport, String> studentResultNameColumn;
    @FXML private TableColumn<GenerateStudentReport, String> classNameColumn;
    @FXML private TableColumn<GenerateStudentReport, String> semesterNameColumn;
    @FXML private TableColumn<GenerateStudentReport, String> totalScoreColumn;
    @FXML private TableColumn<GenerateStudentReport, String> averageScoreColumn;
    @FXML private TableColumn<GenerateStudentReport, String> positionColumn;
    @FXML private VBox studentDetailsBox;
    @FXML private TableView<SubjectReportDTO> subjectBreakdownTable;
    @FXML private TableColumn<SubjectReportDTO, String> subjectNameCol;
    @FXML private TableColumn<SubjectReportDTO, String> classScoreCol;
    @FXML private TableColumn<SubjectReportDTO, String> examScoreCol;
    @FXML private TableColumn<SubjectReportDTO, String> totalCol;
    @FXML private TableColumn<SubjectReportDTO, String> gradeCol;
    @FXML private TableColumn<SubjectReportDTO, String> remarkCol;
    @FXML private Button generateReportBtn;
    @FXML private TextField passScoreField;
    @FXML private Button movePassedStudentsBtn;
    @FXML private ComboBox<GradeCaching> promotionGradeComboBox;

    //ATTENDANCE PAGE
    @FXML private ScrollPane attendanceView;
    @FXML private ComboBox<GradeCaching> attendanceClassSelection;
    @FXML private Button markAttendanceBtn;
    @FXML private Button loadAndUpdateAttendanceBtn;
    @FXML private DatePicker attendanceDatePicker;
    @FXML private HBox attendanceInfoBox;
    @FXML private Label attendanceClassNameLabel;
    @FXML private Label attendanceClassCodeLabel;
    @FXML private VBox noAttendanceStudentsBox;
    @FXML private VBox attendanceTableContainer;
    @FXML private TableView<StudentAttendanceUI> attendanceTable;
    @FXML private TableColumn<StudentAttendanceUI, String> attendanceLevelIdColumn;
    @FXML private TableColumn<StudentAttendanceUI, String> attendanceStudentNameColumn;
    @FXML private TableColumn<StudentAttendanceUI, AttendanceStatus> absentColumn;
    @FXML private TableColumn<StudentAttendanceUI, AttendanceStatus> presentColumn;
    @FXML private Button saveAttendanceBtn;
    @FXML private Button updateAttendanceBtn;

    //SPINNER
    @FXML private VBox loadingOverlay;
    @FXML private ProgressIndicator loadingSpinner;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        studentIdCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentId()));

        studentNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFullName()));

        genderCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getGender()));

        homeTownCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getHomeTown()));

        parentNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getParentName()));

        parentPhoneCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getParentPhone()));


        studentIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentId()));

        studentNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentName()));

        //Editable columns
        classScoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClassScore()));

        examScoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getExamScore()));


        classScoreColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        examScoreColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));

        classScoreColumn.setEditable(true);
        examScoreColumn.setEditable(true);
        studentsScoresTable.setEditable(true);
        studentsScoresTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        classScoreColumn.setOnEditCommit(event -> {
            StudentsScoresTable row = event.getRowValue();
            row.setClassScore(event.getNewValue());
        });

        examScoreColumn.setOnEditCommit(event -> {
            StudentsScoresTable row = event.getRowValue();
            row.setExamScore(event.getNewValue());
        });

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

        classScoreCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClassScore()));

        examScoreCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getExamScore()));

        totalCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTotal()));

        gradeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getGrade()));

        remarkCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription()));


        classResultsTable.setRowFactory(tableView -> {
            TableRow<GenerateStudentReport> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    GenerateStudentReport report = row.getItem();
                    List<SubjectReportDTO> clicked = report.getSubjects();

                    if (studentDetailsBox.isVisible() && subjectBreakdownTable.getUserData() == clicked) {
                        studentDetailsBox.setVisible(false);
                        studentDetailsBox.setManaged(false);

                        subjectBreakdownTable.getItems().clear();
                        subjectBreakdownTable.setUserData(null);
                    } else {
                        subjectBreakdownTable.setItems(
                                FXCollections.observableArrayList(clicked)
                        );
                        subjectBreakdownTable.setUserData(clicked);

                        studentDetailsBox.setVisible(true);
                        studentDetailsBox.setManaged(true);
                    }
                }
            });
            return row;
        });

        attendanceTable.setItems(attendanceData);

        attendanceLevelIdColumn.setCellValueFactory(
                cellData -> cellData.getValue().levelIdProperty());

        attendanceStudentNameColumn.setCellValueFactory(
                cellData -> cellData.getValue().studentNameProperty());

        absentColumn.setCellValueFactory(
                cellData -> cellData.getValue().statusProperty());

        presentColumn.setCellValueFactory(
                cellData -> cellData.getValue().statusProperty()
        );

        absentColumn.setCellFactory(column ->
                new RadioStatusCell(AttendanceStatus.ABSENT));

        presentColumn.setCellFactory(column ->
                new RadioStatusCell(AttendanceStatus.PRESENT));
    }

    @FXML
    public void showDashboard() {
        showLoading();
        utilityClass.loadStaffDashboardData(staffId, notification,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        System.out.println("showDashboard");
                        loadDashBoardData();

                        hideLoading();
                    });
                });
        SwitchView(dashboardBtn, dashboardView);
    }

    @FXML
    public void showAddStudent() {
        SwitchView(addStudentBtn, addNewStudentView);

        if (genderComboBox.getItems().isEmpty()) {
            genderComboBox.getItems().addAll("Male", "Female");
        }

        // Populate class ComboBox from cache
        List<GradeCaching> grades = utilityClass.gradeCacheManager.getGradeCache();
        if (grades != null && !grades.isEmpty()) {
            if (classComboBox.getItems().isEmpty()) {
                classComboBox.getItems().addAll(grades);
            }
        }
    }

    @FXML
    public void showAddResults() {
        List<SemesterCaching> semesterCaching = utilityClass.semesterCachingManager.getCachedSemesters();
        if (semesterCaching != null && !semesterCaching.isEmpty()) {
            if (semesterComboBox.getItems().isEmpty()) {
                semesterComboBox.getItems().addAll(semesterCaching);
            }
        }
        SwitchView(addResultsBtn, newSubjectResultView);
    }

    @FXML
    public void openResultsPage() {
        List<SemesterCaching> semesterCaching = utilityClass.semesterCachingManager.getCachedSemesters();
        if (semesterCaching != null && !semesterCaching.isEmpty()) {
            if (resultsSemesterComboBox.getItems().isEmpty()) {
                resultsSemesterComboBox.getItems().addAll(semesterCaching);
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
        List<GradeCaching> grades = utilityClass.gradeCacheManager.getGradeCache();
        if (grades != null && !grades.isEmpty()) {
            if (gradeComboBox.getItems().isEmpty()) {
                gradeComboBox.getItems().addAll(grades);
            }
        }

        List<GradeCaching> gradeCaching = utilityClass.gradeCacheManager.getGradeCache();
        if (gradeCaching != null && !gradeCaching.isEmpty()) {
            if (printClassSubjectsComboBox.getItems().isEmpty()) {
                printClassSubjectsComboBox.getItems().addAll(gradeCaching);
            }
        }
        SwitchView(addSubjectBtn, addNewSubjectView);
    }

    @FXML
    public void openAttendancePage() {
        List<GradeCaching> grades = utilityClass.gradeCacheManager.getGradeCache();
        if (grades != null && !grades.isEmpty()) {
            if (attendanceClassSelection.getItems().isEmpty()) {
                attendanceClassSelection.getItems().addAll(grades);
            }
        }

        SwitchView(attendanceBtn, attendanceView);
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
                        utilityClass.showSuccess(notification, "Successfully saved subject");
                    });
                }, () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveSubjectBtn, "Save Subject");
                        utilityClass.showError(notification, "Could not save subject! Try again");
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
    public void handleLoadStudents() {
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
                        utilityClass.notProcessingButton(loadSubjectStudentsBtn, "Load Students");
                        displaySubjectStudents(subjectScores);
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(loadSubjectStudentsBtn, "Load Students");
                        noStudentsFound();
                        utilityClass.showError(notification, "No students found! Check and verify subject Code");
                    });
                },
                ()-> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(loadSubjectStudentsBtn, "Load Students");
                        utilityClass.showError(notification, "Network error! Check your network connection and try again!");
                    });
                });
    }

    @FXML
    public void handleSaveScores() {
        if (studentsScoresTable.getItems().isEmpty()) {
            utilityClass.showError(notification, "No students to save scores for!");
            return;
        }

        String subjectId = subjectCodeField.getText().trim();
        if (subjectId.isEmpty()) {
            utilityClass.showError(notification, "Subject code field is empty!");
            return;
        }

        SemesterCaching semesterCaching = semesterComboBox.getSelectionModel().getSelectedItem();
        if (semesterCaching == null) {
            utilityClass.showError(notification, "Please select a semester");
            saveScoresSemesterComboBox.setVisible(true);
            saveScoresSemesterComboBox.setManaged(true);
            return;
        }
        String semesterId = semesterCaching.getSemesterId();

        List<StudentsScoresTable> scores = studentsScoresTable.getItems();

        // Validate all fields first
        for (StudentsScoresTable student : scores) {
            if (student.getClassScore() == null || student.getClassScore().isBlank()) {
                utilityClass.showError(notification, "Class score is empty for " + student.getStudentId());
                return;
            }
            if (student.getExamScore() == null || student.getExamScore().isBlank()) {
                utilityClass.showError(notification, "Exam score is empty for " + student.getStudentId());
                return;
            }
        }

        utilityClass.processingButton(saveScores);

        // Track progress
        final int totalStudents = scores.size();
        final int[] savedCount = {0};
        final int[] failedCount = {0};

        for (StudentsScoresTable student : scores) {
            if (Double.parseDouble(student.getClassScore()) > 50) {
                utilityClass.showError(notification, "Class Score must be less than or equal to 50");
                return;
            }
            utilityClass.saveStudentScores(
                    staffId,
                    student.getStudentId(),
                    subjectId,
                    student.getClassScore(),
                    student.getExamScore(),
                    semesterId,
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
                        utilityClass.notProcessingButton(saveScores, "Save All Academic Results");
                        utilityClass.showError(notification, "Network error! Check your network connection and try again!");
                    });
        }
    }

    @FXML
    public void handleClearScores() {
        for (StudentsScoresTable student : studentsScoresTable.getItems()) {
            student.setClassScore("");
            student.setExamScore("");
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
                utilityClass.notProcessingButton(saveAndClearBtn, "Save and Close");
                utilityClass.showSuccess(notification, "Student saved successfully! ID: " + utilityClass.studentId);
                clearForm();
            });
        }, () -> {
            // On Failure
            Platform.runLater(() -> {
                utilityClass.notProcessingButton(saveAndClearBtn, "Save and Close");
                utilityClass.showError(notification, "Could not save student");
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
        resultSemester = resultsSemesterComboBox.getSelectionModel().getSelectedItem();
        if (resultSemester == null) {
            utilityClass.showError(notification, "Select Semester to Load Results");
            return;
        }
        String semesterId = resultSemester.getSemesterId();
        utilityClass.processingButton(loadClassResultsBtn);
        utilityClass.loadClassResults(staffId, semesterId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(loadClassResultsBtn, "Fetch Results");
                        utilityClass.showSuccess(notification, "Results successfully loaded");
                        populateClassResults(generateStudentReports);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(loadClassResultsBtn, "Fetch Results");
                        utilityClass.showError(notification, "Could not fetch results. " +
                                "Staff have no class assigned");

                        resultsInfoBox.setVisible(false);
                        resultsInfoBox.setManaged(false);
                        classResultsTableContainer.setVisible(false);
                        classResultsTableContainer.setManaged(false);

                        noResultsBox.setVisible(true);
                        noResultsBox.setManaged(true);
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
    public void handleGenerateReport() {
        GradeCaching gradeCaching = promotionGradeComboBox.getSelectionModel().getSelectedItem();

        String semesterName = resultSemester.getSemesterName();
        String[] semesterNameSplit = semesterName.split("-");

        String promotionGradeId = "";
        if (semesterNameSplit[0].equals("THIRD")) {
            if (gradeCaching == null) {
                utilityClass.showError(notification, "Select grade to contain promoted students");
                return;
            }
            promotionGradeId = gradeCaching.getLevelId();
        }

        if (!semesterNameSplit[0].equals("THIRD")) {
            promotionGradeId = null;
        }

        String semesterId = resultSemester.getSemesterId();

        utilityClass.processingButton(generateReportBtn);
        utilityClass.showSuccess(notification, "Work while we process your request");
        utilityClass.downloadStudentReport(staffId, semesterId, promotionGradeId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(generateReportBtn, "Generate Academic Reports");
                        utilityClass.showSuccess(notification, "Results downloaded successfully");
                    });
                }, () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(generateReportBtn, "Generate Results");
                        utilityClass.showError(notification, "Could not download results");
                    });
                }, () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(generateReportBtn, "Generate Results");
                        utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void movePassedStudents() {
        String passTotalScore = passScoreField.getText();
        if (passTotalScore.equals(" ") || passTotalScore.isEmpty()) {
            utilityClass.showError(notification, "Please enter a total pass score");
            return;
        }

        GradeCaching gradeCaching = promotionGradeComboBox.getSelectionModel().getSelectedItem();
        if (gradeCaching == null) {
            utilityClass.showError(notification, "Select Grade");
            return;
        }

        String nextGradeId = gradeCaching.getLevelId();
        String semesterId = resultSemester.getSemesterId();

        utilityClass.processingButton(movePassedStudentsBtn);
        utilityClass.movePassedStudents(staffId, semesterId, passTotalScore, nextGradeId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(movePassedStudentsBtn, "Move Passed Students");
                        utilityClass.showSuccess(notification, passedResponse);
                    });
                }, () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(movePassedStudentsBtn, "Move Passed Students");
                        utilityClass.showError(notification, "Could not move passed students");
                    });
                }, () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(movePassedStudentsBtn, "Move Passed Students");
                        utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void loadAttendanceStudents() {
        GradeCaching gradeCaching = attendanceClassSelection.getSelectionModel().getSelectedItem();
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
                        attendanceClassNameLabel.setText(gradeCaching.getLevelName());
                        attendanceClassCodeLabel.setText(gradeCaching.getLevelId());
                        updateAttendanceBtn.setDisable(true);

                        attendanceInfoBox.setVisible(true);
                        attendanceInfoBox.setManaged(true);
                        attendanceTableContainer.setVisible(true);
                        attendanceTableContainer.setManaged(true);
                        utilityClass.notProcessingButton(markAttendanceBtn, "Mark Attendance");
                        utilityClass.showSuccess(notification, "Students Loaded Successfully");
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(markAttendanceBtn, "Mark Attendance");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(markAttendanceBtn, "Mark Attendance");
                        utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void loadAttendanceForUpdate() {
        attendanceDatePicker.setVisible(true);
        attendanceDatePicker.setManaged(true);
        GradeCaching gradeCaching = attendanceClassSelection.getSelectionModel().getSelectedItem();
        if (gradeCaching == null) {
            utilityClass.showError(notification, "Select Grade");
            return;
        }
        String levelId = gradeCaching.getLevelId();

        selectedAttendanceDate = attendanceDatePicker.getValue();
        if (selectedAttendanceDate == null) {
            utilityClass.showError(notification, "Select Date to update");
            return;
        }
        String date = selectedAttendanceDate.toString();

        utilityClass.processingButton(loadAndUpdateAttendanceBtn);

        utilityClass.loadStudentsForAttendance(levelId, date,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        attendanceData.clear();
                        for(StudentAttendance studentAttendance: studentAttendances) {
                            attendanceData.add(new StudentAttendanceUI(studentAttendance));
                        }
                        attendanceClassNameLabel.setText(gradeCaching.getLevelName());
                        attendanceClassCodeLabel.setText(gradeCaching.getLevelId());
                        saveAttendanceBtn.setDisable(true);

                        attendanceInfoBox.setVisible(true);
                        attendanceInfoBox.setManaged(true);
                        attendanceTableContainer.setVisible(true);
                        attendanceTableContainer.setManaged(true);
                        utilityClass.notProcessingButton(loadAndUpdateAttendanceBtn, "Update Attendance");
                        utilityClass.showSuccess(notification, "Students Loaded Successfully");
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(loadAndUpdateAttendanceBtn, "Mark Attendance");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(loadAndUpdateAttendanceBtn, "Mark Attendance");
                        utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleSaveAttendance() {
        utilityClass.processingButton(saveAttendanceBtn);
        ObservableList<StudentAttendanceUI> attendanceList = attendanceTable.getItems();

        List<StudentAttendance> studentAttendance = new ArrayList<>();
        for (StudentAttendanceUI studentAttendanceUI : attendanceList) {
            studentAttendance.add(studentAttendanceUI.toStudentAttendance());
        }

        final int totalStudents = studentAttendance.size();
        final int[] savedCount = {0};
        final int[] failedCount = {0};

        for (StudentAttendance markAttendance: studentAttendance) {
            utilityClass.markStudentAttendance(markAttendance.getStudentId(), staffId,
                    markAttendance.getLevelId(), markAttendance.getStatus(),
                    () -> {
                //On Success
                        Platform.runLater(() -> {
                            savedCount[0]++;
                            completionCheck(savedCount[0], failedCount[0], totalStudents, saveAttendanceBtn, "Save Attendance");
                        });
                    },
                    () -> {
                //On Failure
                        Platform.runLater(() -> {
                            failedCount[0]++;
                            completionCheck(savedCount[0], failedCount[0], totalStudents, saveAttendanceBtn, "Save Attendance");
                        });
                    },
                    () -> {
                //On Network Issue
                        Platform.runLater(() -> {
                            utilityClass.notProcessingButton(saveAttendanceBtn, "Save Attendance");
                            utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                        });
                    });
        }
    }

    @FXML
    public void handleUpdateAttendance() {
        utilityClass.processingButton(updateAttendanceBtn);
        ObservableList<StudentAttendanceUI> attendanceList = attendanceTable.getItems();

        List<StudentAttendance> studentAttendance = new ArrayList<>();
        for (StudentAttendanceUI studentAttendanceUI : attendanceList) {
            studentAttendance.add(studentAttendanceUI.toStudentAttendance());
        }

        final int totalStudents = studentAttendance.size();
        final int[] savedCount = {0};
        final int[] failedCount = {0};

        for (StudentAttendance markAttendance: studentAttendance) {
            utilityClass.updateAttendance(markAttendance.getStudentId(),
                    staffId, markAttendance.getLevelId(),
                    markAttendance.getStatus(), selectedAttendanceDate.toString(),
                    () -> {
                        //On Success
                        Platform.runLater(() -> {
                            savedCount[0]++;
                            completionCheck(savedCount[0], failedCount[0], totalStudents, updateAttendanceBtn, "Update Attendance");
                        });
                    },
                    () -> {
                        //On Failure
                        Platform.runLater(() -> {
                            failedCount[0]++;
                            completionCheck(savedCount[0], failedCount[0], totalStudents, updateAttendanceBtn, "Update Attendance");
                        });
                    },
                    () -> {
                        //On Network Issue
                        Platform.runLater(() -> {
                            utilityClass.notProcessingButton(saveAttendanceBtn, "Update Attendance");
                            utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                        });
                    });
        }
    }

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
                        utilityClass.showError(notification, "Could not download Subject List");
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

    private void populateClassResults(List<GenerateStudentReport> generateStudentReports) {
        gradeNameLabel.setText(generateStudentReports.getFirst().getClassName());
        semesterNameLabel.setText(generateStudentReports.getFirst().getSemesterName());

        classResultsTable.getItems().setAll(generateStudentReports);

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
        if (classComboBox.getValue() == null) {
            utilityClass.showError(notification, "Please select a class");
            return false;
        }
        return true;
    }

    private void checkCompletion(int saved, int failed, int total, Button button) {
        if (saved + failed == total) {
            Platform.runLater(() -> {
                utilityClass.notProcessingButton(button, "Save All Scores");
                if (failed == 0) {
                    utilityClass.showSuccess(notification, "All " + saved + " scores saved successfully!");
                } else {
                    utilityClass.showError(notification, saved + " saved, " + failed + " failed");
                }
            });
        }
    }

    private void completionCheck(int saved, int failed, int total, Button button, String text) {
        if (saved + failed == total) {
            Platform.runLater(() -> {
                utilityClass.notProcessingButton(button, text);
                if (failed == 0) {
                    utilityClass.showSuccess(notification, "All " + saved + " attendance saved successfully!");
                } else {
                    utilityClass.showError(notification, saved + " saved, " + failed + " failed");
                }
            });
        }
    }

    public void displaySubjectStudents(SubjectScores subjectScores) {
        subjectNameLabel.setText(subjectScores.getSubjectName());
        subjectCodeLabel.setText(subjectScores.getSubjectId());

        // Use setAll() to replace items, not addAll()
        List<StudentsScoresTable> scores = subjectScores.getScoresTable();
        if (scores != null) {
            studentsScoresTable.getItems().setAll(scores);
        } else {
            System.out.println("No students found!");
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


    @FXML
    public void initializeAfterLogin() {
        setActiveButton(dashboardBtn);
        loadStaffInfo();
        showLoading();
        utilityClass = new UtilityClass(this);
        utilityClass.loadStaffDashboardData(staffId, notification,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        loadDashBoardData();

                        hideLoading();
                    });
                });
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

    //SET GRADE DETAILS
    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public void setGradeSize(String gradeSize) {
        this.gradeSize = gradeSize;
    }

    public void setGradeNumber(String gradeNumber) {
        this.gradeNumber = gradeNumber;
    }

    //GET GRADE DETAILS
    public String getGradeNumber() {
        return gradeNumber;
    }

    public String getGradeName() {
        return gradeName;
    }
    public String getGradeSize() {
        return gradeSize;
    }

    private void loadDashBoardData() {
        updateDateTime();

        if (gradeName != null && gradeSize != null) {
            classNameLabel.setText(gradeName + "_" + gradeNumber);
            totalStudentsLabel.setText(gradeSize);
        }

        List<GradeStudentsDisplayDTO> gradeStudents = utilityClass.gradeStudentsCacheManager.getCachedGradeStudents();
        if (gradeStudents != null) {
            studentsTable.getItems().setAll(gradeStudents);
        }
    }

    private void loadStaffInfo() {
        if (institutionName != null) {
            institutionalNameLabel.setText(institutionName);
        }
        if (staffFullName != null) {
            staffNameLabel.setText(staffFullName);
        }
    }

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy | HH:mm:ss");
        dateTimeLabel.setText(now.format(formatter));
    }

    private void SwitchView(Button activeButton, ScrollPane viewToShow) {

        dashboardView.setVisible(false);
        dashboardView.setManaged(false);

        newSubjectResultView.setVisible(false);
        newSubjectResultView.setManaged(false);

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
        String ACTIVE_BUTTON = "-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-padding: 15 20 15 20; -fx-background-radius: 8; -fx-cursor: hand;";
        String INACTIVE_BUTTON = "-fx-background-color: transparent; -fx-text-fill: #ecf0f1; -fx-font-size: 14px; -fx-padding: 15 20 15 20;" +
                " -fx-background-radius: 8; -fx-cursor: hand; -fx-border-color: transparent;";


        dashboardBtn.setStyle(INACTIVE_BUTTON);
        addStudentBtn.setStyle(INACTIVE_BUTTON);
        addResultsBtn.setStyle(INACTIVE_BUTTON);
        addSubjectBtn.setStyle(INACTIVE_BUTTON);
        addResultsBtn.setStyle(INACTIVE_BUTTON);
        resultsBtn.setStyle(INACTIVE_BUTTON);
        attendanceBtn.setStyle(INACTIVE_BUTTON);

        activeButton.setStyle(ACTIVE_BUTTON);
    }
}