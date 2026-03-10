package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.Constants.Gender;
import com.bbquantum.eazymanage.Constants.StaffRoles;
import com.bbquantum.eazymanage.Constants.StaffStatus;
import com.bbquantum.eazymanage.DTO.Academics.*;
import com.bbquantum.eazymanage.DTO.Finance.ClassFeesSummary;
import com.bbquantum.eazymanage.DTO.Finance.FeesSummaryTable;
import com.bbquantum.eazymanage.DTO.Finance.StudentFeesPaymentDisplay;
import com.bbquantum.eazymanage.DTO.Finance.PaymentHistory;
import com.bbquantum.eazymanage.DTO.Institution.*;
import com.bbquantum.eazymanage.Utitity.ApiUtilityClass;
import com.bbquantum.eazymanage.Utitity.UtilityMethods;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;

import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PrincipalController implements Initializable {

    private ApiUtilityClass apiUtilityClass;

    //Store staff Data from backend
    String institutionName;
    String principalName;

    //Store logged in staff Id from login
    public String staffId;

    //Get refreshed staff list
    public List<ViewStaffList> refreshedStaffViewList;
    //Store fees filter
    public List<FeesSummaryTable> feesSummaryTable;

    //LEFT SIDEBAR - Navigation side
    @FXML public StackPane notification;
    @FXML public Label versionLabel;
    @FXML private Button mainDashboardBtn;
    @FXML private Button staffDashboardBtn;
    @FXML private Button financeDashboardBtn;
    @FXML private Button studentDashboardBtn;
    @FXML private Button logoutButton;

    //User info
    @FXML private Label principalNameLabel;
    @FXML private Label institutionNameLabel;

    //Content Area
    @FXML private StackPane contentArea;

    //MAIN DASHBOARD VIEW
    @FXML private ScrollPane mainDashboardView;
    @FXML private Label dateTimeLabel;
    @FXML private Button refreshDashboardBtn;
    @FXML public Label totalStudentsLabel;
    @FXML public Label totalStaffLabel;
    @FXML private Label semesterFeesLabel;
    @FXML public Label feesPaidLabel;
    @FXML public ProgressBar feesProgressBar;
    @FXML public TableView<AbsenteesView> absenteesTable;
    @FXML private TableColumn<AbsenteesView, String> studentIdColumn;
    @FXML private TableColumn<AbsenteesView, String> studentNameColumn;
    @FXML private TableColumn<AbsenteesView, String> studentLevelColumn;
    @FXML private TableColumn<AbsenteesView, String> instructorNameColumn;
    @FXML private TableColumn<AbsenteesView, String> instructorIdColumn;

    //STAFF DASHBOARD VIEW
    @FXML private ScrollPane staffDashboardView;
    @FXML private VBox staffManagementContainer;
    @FXML private Label totalStaffCountLabel;
    @FXML private Label teachingStaffCountLabel;
    @FXML public VBox staffDetailsPanel;
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
    @FXML private Button updateStaffBtn;

    @FXML public TableView<ViewStaffList> allStaffTable;
    @FXML private TableColumn<ViewStaffList, String> staffIdColumn;
    @FXML private TableColumn<ViewStaffList, String> staffNameColumn;
    @FXML private TableColumn<ViewStaffList, String> staffRoleColumn;

    //FINANCE DASHBOARD VIEW
    @FXML private ScrollPane financeDashboardView;
    @FXML public Label totalSemesterFeesLabel;
    @FXML private Label amountPaidLabel;
    @FXML private Label collectionPercentageLabel;
    @FXML private ProgressBar collectionProgressBar;
    @FXML private Label outstandingBalanceLabel;
    @FXML private ComboBox<StudentsHolder> studentSelectionBox;
    @FXML private Button searchFeesHistoryBtn;
    @FXML private Button fullyPaidBtn;
    @FXML private Button partiallyPaidBtn;
    @FXML private Button notPaidBtn;
    @FXML public ComboBox<SemesterCaching> semesterComboBox;
    @FXML public ComboBox<GradeCaching> classLevelComboBox;
    @FXML private VBox feeReportPanel;
    @FXML private Label studentNameLabel;
    @FXML private Label studentIdDisplayLabel;
    @FXML private Label studentClassLabel;
    @FXML private Label studentSemesterLabel;
    @FXML private Label studentTotalFeesLabel;
    @FXML private Label studentAmountPaidLabel;
    @FXML private Label studentBalanceLabel;
    @FXML private Label studentPaymentPercentageLabel;
    @FXML private ProgressBar studentPaymentProgressBar;
    @FXML private TableView<PaymentHistory> paymentHistoryTable;
    @FXML private TableColumn<PaymentHistory, String> paymentDateColumn;
    @FXML private TableColumn<PaymentHistory, String> paymentAmountColumn;
    @FXML private TableColumn<PaymentHistory, String> paymentPersonColumn;
    @FXML private TableColumn<PaymentHistory, String> paymentPersonContactColumn;
    @FXML
    public VBox noStudentFoundPanel;
    //Entire class fees summary table
    @FXML
    public VBox filteredStudentsPanel;
    @FXML private Label filterTitleLabel;
    @FXML private Label filteredCountLabel;
    @FXML
    public TableView<ClassFeesSummary> classSummaryTable;
    @FXML private TableColumn<ClassFeesSummary, String> summaryClassColumn;
    @FXML private TableColumn<ClassFeesSummary, String> summaryStudentCountColumn;
    @FXML private TableColumn<ClassFeesSummary, String> summaryExpectedFeesColumn;
    @FXML private TableColumn<ClassFeesSummary, String> summaryAmountPaidColumn;
    @FXML private TableColumn<ClassFeesSummary, String> summaryOutstandingColumn;
    //Fees filtering table
    @FXML private TableView<FeesSummaryTable> filteredStudentsTable;
    @FXML private TableColumn<FeesSummaryTable, String> filteredStudentIdColumn;
    @FXML private TableColumn<FeesSummaryTable, String> filteredStudentNameColumn;
    @FXML private TableColumn<FeesSummaryTable, String> filteredStudentClassColumn;
    @FXML private TableColumn<FeesSummaryTable, String> filteredTotalFeesColumn;
    @FXML private TableColumn<FeesSummaryTable, String> filteredAmountPaidColumn;
    @FXML private TableColumn<FeesSummaryTable, String> filteredBalanceColumn;

    //STUDENT DASHBOARD VIEW
    @FXML private ScrollPane studentDashboardView;
    @FXML private Label totalStudentsCountLabel;
    @FXML private Button getResultsBtn;
    @FXML private ComboBox<StudentsHolder> results_StudentSelectionBox;
    @FXML private ComboBox<GradeCaching> printStudentClassComboBox;
    @FXML
    public Button printStudentDataBtn;
    @FXML public ComboBox<SemesterCaching> resultsSemesterComboBox;
    @FXML public ComboBox<GradeCaching> resultsClassComboBox;
    @FXML private VBox resultsDisplayPanel;
    @FXML private Label resultsStudentNameLabel;
    @FXML private Label resultsStudentIdLabel;
    @FXML private Label resultsClassLabel;
    @FXML private Label resultsSemesterLabel;
    @FXML private Label totalScoreLabel;
    @FXML private Label averageScoreLabel;
    @FXML private Label positionLabel;
    @FXML
    public VBox noResultsFoundPanel;
    @FXML private TableView<StudentSubjectResults> subjectResultsTable;
    @FXML private TableColumn<StudentSubjectResults, String> subjectNameColumn;
    @FXML private TableColumn<StudentSubjectResults, String> ex1Column;
    @FXML private TableColumn<StudentSubjectResults, String> classTestColumn;
    @FXML private TableColumn<StudentSubjectResults, String> ex2Column;
    @FXML private TableColumn<StudentSubjectResults, String> projectScoreColumn;
    @FXML private TableColumn<StudentSubjectResults, String> classScoreColumn;
    @FXML private TableColumn<StudentSubjectResults, String> examScoreColumn;
    @FXML private TableColumn<StudentSubjectResults, String> calExamScoreColumn;
    @FXML private TableColumn<StudentSubjectResults, String> subTotalScoreColumn;
    @FXML private TableColumn<StudentSubjectResults, String> subjectGradeColumn;
    @FXML private TableColumn<StudentSubjectResults, String> subjectRemarkColumn;

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
    @FXML private ComboBox<StudentsHolder> studentSearchComboBox;
    @FXML private ComboBox<GradeCaching> studentSearchClassBox;
    @FXML private Button findStudentBtn;
    @FXML private VBox studentDetailsPanel;
    @FXML private Label studentIdLabel;
    @FXML private Label updateStudentNameLabel;
    @FXML private Label studentStatusLabel;
    @FXML private Label updateGradeNameLabel;
    @FXML private Label updateGenderComboBox;
    @FXML private Label updateDateOfBirthPicker;
    @FXML private Label updateHomeTownField;
    @FXML private Label updateParentNameField;
    @FXML private Label updateParentPhoneField;

    //MANAGE RESULTS PAGE
    @FXML private ScrollPane manageResultsView;
    @FXML private ComboBox<SemesterCaching> classResultsSemesterComboBox;
    @FXML private ComboBox<GradeCaching> classResultsGradeComboBox;
    @FXML private Button loadClassResultsBtn;
    @FXML private HBox resultsInfoBox;
    @FXML private Label gradeNameLabel;
    @FXML private Label semesterNameLabel;
    @FXML private VBox noResultsBox;
    @FXML private VBox classResultsTableContainer;
    @FXML private TableView<ViewClassSemesterReport> classResultsTable;
    @FXML private TableColumn<ViewClassSemesterReport, String> studentResultIdColumn;
    @FXML private TableColumn<ViewClassSemesterReport, String> studentResultNameColumn;
    @FXML private TableColumn<ViewClassSemesterReport, String> classNameColumn;
    @FXML private TableColumn<ViewClassSemesterReport, String> semesterNameColumn;
    @FXML private TableColumn<ViewClassSemesterReport, String> totalScoreColumn;
    @FXML private TableColumn<ViewClassSemesterReport, String> averageScoreColumn;
    @FXML private TableColumn<ViewClassSemesterReport, String> positionColumn;
    @FXML private VBox studentDetailsBox;
    @FXML private TableView<ViewStudentsSubjectsResults> subjectBreakdownTable;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> subjectNameCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> exercise1Col;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> classTestCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> exercise2Col;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> projectScoreCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> classScoreCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> examScoreCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> calculatedExamScoreCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> totalCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> gradeCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> remarkCol;

    //SPINNER
    @FXML private VBox loadingOverlay;
    @FXML private ProgressIndicator loadingSpinner;

    @Override
    public void initialize(URL url,  ResourceBundle resourceBundle) {
        //Absentees Table
        studentIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getStudentId())));

        studentNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getStudentName())));

        studentLevelColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentGrade()));

        instructorNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getInstructorName()));

        instructorIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getInstructorId())));


        //Filtered payment list Table
        filteredStudentIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentId()));

        filteredStudentNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentName()));

        filteredStudentClassColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getGradeName()));

        filteredTotalFeesColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTotalFees()));

        filteredAmountPaidColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAmountPaid()));

        filteredBalanceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOutStandingBalance()));


        //Payment history Table
        paymentDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDateOfPayment()));

        paymentAmountColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAmountPaid()));

        paymentPersonColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPersonWhoPaid()));

        paymentPersonContactColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getContactInfo()));


        //Fees summary by class
        summaryClassColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClassName()));

        summaryStudentCountColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTotalStudents()));

        summaryExpectedFeesColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getExpectedFees()));

        summaryAmountPaidColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAmountPaid()));

        summaryOutstandingColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOutstandingFees()));


        //Individual student results search table
        subjectNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSubjectName()));

        ex1Column.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getExercise1Score()));

        classTestColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClassTestScore()));

        ex2Column.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getExercise2Score()));

        projectScoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProjectScore()));

        classScoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClassScore()));

        examScoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getExamScore()));

        calExamScoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCalculatedExamScore()));

        subTotalScoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTotal()));

        subjectGradeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getGrade()));

        subjectRemarkColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRemark()));


        //Staff List Table
        staffIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStaffId()));

        staffNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStaffName()));

        staffRoleColumn.setCellValueFactory(cellData -> {
            List<String> staffRoles = cellData.getValue().getStaffRoles();

            if (staffRoles == null || staffRoles.isEmpty()) {
                return new SimpleStringProperty("");
            }

            String joinRoles = String.join(", ", staffRoles);
            return new SimpleStringProperty(joinRoles);
        });

        allStaffTable.setRowFactory(tableView -> {
            TableRow<ViewStaffList> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    ViewStaffList staffData = row.getItem();
                    handleSearchStaff(staffData.getStaffId());
                }
            });

            return row;
        });


        //Student results search table
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

    }

    @FXML
    public void showMainDashboard() {
        loadMainDashboardData();

        switchView(mainDashboardView, mainDashboardBtn);
    }

    @FXML
    private void showStaffDashboard() {
        loadStaffDashboardData();

        staffDetailsPanel.setVisible(false);
        staffDetailsPanel.setManaged(false);

        staffManagementContainer.setVisible(true);
        staffManagementContainer.setManaged(true);

        switchView(staffDashboardView, staffDashboardBtn);
    }

    @FXML
    private void showFinanceDashboard() {
        loadFinanceDashboardData();

        studentSelectionBox.setDisable(true);

        loadFinanceDashboard();

        switchView(financeDashboardView, financeDashboardBtn);
    }

    @FXML
    private void showStudentDashboard() {

        loadStudentDashboardData();

        List<GradeCaching> cachedGrades = UtilityMethods.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (resultsClassComboBox.getItems().isEmpty()) {
                resultsClassComboBox.getItems().addAll(cachedGrades);
            }
        }

        List<SemesterCaching> semesterCaching = UtilityMethods.semesterCachingManager.getCachedSemesters();
        if (semesterCaching != null && !semesterCaching.isEmpty()) {
            resultsSemesterComboBox.getItems().setAll(semesterCaching);
        }

        results_StudentSelectionBox.setDisable(true);

        switchView(studentDashboardView, studentDashboardBtn);
    }

    @FXML
    public void handleLogout() throws IOException {
        UtilityMethods.clearSession();
        // Close the login window
        Stage principalDashboard = (Stage) logoutButton.getScene().getWindow();

        // Load the appropriate dashboard
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/Authentication/auth-container.fxml"));
        Scene scene = new Scene(loader.load());

        // Create new stage for dashboard
        Stage loginStage = new Stage();
        loginStage.setTitle("EazyManage - Authentication");
        loginStage.setScene(scene);
        loginStage.sizeToScene();
        loginStage.setResizable(false);

        // Close login and show dashboard
        principalDashboard.close();
        loginStage.show();
    }

    @FXML
    public void refreshDashboard() {
        showLoading();
        apiUtilityClass.loadPrincipalData(staffId, notification,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        loadMainDashboardData();

                        hideLoading();
                    });
                });
    }

    @FXML
    public void handleLoadClassStudents() {
        GradeCaching selectedGrade = classLevelComboBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) return;

        System.out.println("Happened");

        showLoading();
        apiUtilityClass.loadClassStudents(selectedGrade.getLevelId(), staffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        hideLoading();
                        studentSelectionBox.getItems().clear();
                        studentSelectionBox.getItems().addAll(UtilityMethods.studentsHolder);
                        UtilityMethods.showSuccess(notification, "Students loaded");
                        studentSelectionBox.setDisable(false);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        hideLoading();
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        hideLoading();
                        UtilityMethods.showError(notification, "Network error! Check your connection");
                    });
                });
    }

    @FXML
    public void handleSearchStudentFees() {
        //Extract studentId from textField
        StudentsHolder student = studentSelectionBox.getSelectionModel().getSelectedItem();
        if (student == null) {
            UtilityMethods.showError(notification, "Please select a student!");
            return;
        }

        //Get selected class from combo box
        GradeCaching selectedGrade = classLevelComboBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            UtilityMethods.showError(notification, "Please select Grade");
            return;
        }
        String gradeId = selectedGrade.getLevelId();

        //Get selected semester from combo box
        SemesterCaching selectedSemester = semesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            UtilityMethods.showError(notification, "Please select Semester");
            return;
        }
        String semesterId = selectedSemester.getSemesterId();
        UtilityMethods.processingButton(searchFeesHistoryBtn);

        apiUtilityClass.fetchStudentPaymentRecords(student.getStudentId(), semesterId, gradeId,
                () -> {
                    // On success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(searchFeesHistoryBtn, "FETCH FEES RECORDS");
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                        displayFeeReport(UtilityMethods.paymentRecords);
                    });
                },
                () -> {
                    // On failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(searchFeesHistoryBtn, "FETCH FEES RECORDS");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                }, () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(searchFeesHistoryBtn, "FETCH FEES RECORDS");
                        UtilityMethods.showError(notification,"Network error! Check your internet and try again");
                    });
                });
    }

    @FXML
    public void showFullyPaid() {
        String ownUrl = "/fully-paid-students/" + staffId;
        String filter = "FULLY PAID STUDENTS";

        UtilityMethods.processingButton(fullyPaidBtn);
        apiUtilityClass.getFeesFilterData(ownUrl,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(fullyPaidBtn, "FULLY PAID");
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                        String size = String.valueOf(feesSummaryTable.size());
                        displayFilteredFeesTable(feesSummaryTable, filter, size);
                    });
                },
                () -> {
                    //On failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(fullyPaidBtn, "FULLY PAID");
                        filteredStudentsPanel.setVisible(false);
                        filteredStudentsPanel.setManaged(false);
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(fullyPaidBtn, "FULLY PAID");
                        UtilityMethods.showError(notification, "Network Error! Check your internet and try again");
                    });
                });
    }

    @FXML
    public void showPartialPayment() {
        String ownUrl = "/partially-paid-students/" + staffId;
        String filter = "PARTIALLY PAID STUDENTS";

        UtilityMethods.processingButton(partiallyPaidBtn);
        apiUtilityClass.getFeesFilterData(ownUrl,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(partiallyPaidBtn, "PARTIAL PAYMENT");
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                        String size = String.valueOf(feesSummaryTable.size());
                        displayFilteredFeesTable(feesSummaryTable, filter, size);
                    });
                },
                () -> {
                    //On failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(partiallyPaidBtn, "PARTIAL PAYMENT");
                        filteredStudentsPanel.setVisible(false);
                        filteredStudentsPanel.setManaged(false);
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(fullyPaidBtn, "PARTIAL PAYMENT");
                        UtilityMethods.showError(notification, "Network Error! Check your internet and try again");
                    });
                });
    }

    @FXML
    public void showNotPaid() {
        String ownUrl = "/not-paid-students/" + staffId;
        String filter = "NOT PAID STUDENTS";

        UtilityMethods.processingButton(notPaidBtn);
        apiUtilityClass.getFeesFilterData(ownUrl,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(notPaidBtn, "NOT PAID");
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                        String size = String.valueOf(feesSummaryTable.size());
                        displayFilteredFeesTable(feesSummaryTable, filter, size);
                    });
                },
                () -> {
                    //On failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(notPaidBtn, "NOT PAID");
                        filteredStudentsPanel.setVisible(false);
                        filteredStudentsPanel.setManaged(false);
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(notPaidBtn, "NOT PAID");
                        UtilityMethods.showError(notification, "Network Error! Check your internet and try again");
                    });
                });
    }

    @FXML
    public void handleClearFilter() {
        filteredStudentsPanel.setVisible(false);
        filteredStudentsPanel.setManaged(false);
    }

    @FXML
    public void openAddStudentPage() {
        showAddStudentCard();
        switchView(addNewStudentView, studentDashboardBtn);
    }

    @FXML
    public void openClassResultsView() {
        List<GradeCaching> cachedGrades = UtilityMethods.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (classResultsGradeComboBox.getItems().isEmpty()) {
                classResultsGradeComboBox.getItems().addAll(cachedGrades);
            }
        }

        List<SemesterCaching> semesterCaching = UtilityMethods.semesterCachingManager.getCachedSemesters();
        if (semesterCaching != null && !semesterCaching.isEmpty()) {
            if (classResultsSemesterComboBox.getItems().isEmpty()) {
                classResultsSemesterComboBox.getItems().addAll(semesterCaching);
            }
        }

        switchView(manageResultsView, staffDashboardBtn);
    }

    @FXML
    public void handleFetchStudents() {
        GradeCaching selectedGrade = resultsClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) return;

        showLoading();
        apiUtilityClass.loadClassStudents(selectedGrade.getLevelId(), staffId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        hideLoading();
                        results_StudentSelectionBox.getItems().clear();
                        results_StudentSelectionBox.getItems().addAll(UtilityMethods.studentsHolder);
                        results_StudentSelectionBox.setDisable(false);
                        UtilityMethods.showSuccess(notification, "Students loaded");
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        hideLoading();
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        hideLoading();
                        UtilityMethods.showError(notification, "Network error! Check your connection");
                    });
                });
    }

    @FXML
    public void handleSearchResults() {
        StudentsHolder student = results_StudentSelectionBox.getSelectionModel().getSelectedItem();
        if (student == null) {
            UtilityMethods.showError(notification, "Please select a student");
            return;
        }
        //Get selected grade/class
        GradeCaching selectedGrade = resultsClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            UtilityMethods.showError(notification, "No grade selected");
            return;
        }
        String classId = selectedGrade.getLevelId();

        SemesterCaching selectedSemester = resultsSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            UtilityMethods.showError(notification, "No semester selected");
            return;
        }
        String semesterId = selectedSemester.getSemesterId();
        UtilityMethods.processingButton(getResultsBtn);

        apiUtilityClass.SearchStudentResults(student.getStudentId(), semesterId, classId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(getResultsBtn, "FETCH RESULT");
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                        displayStudentResultsPanel(apiUtilityClass.studentResult);
                    });
                },
                () -> {
                    //On failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(getResultsBtn, "FETCH RESULT");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(getResultsBtn, "FETCH RESULT");
                        UtilityMethods.showError(notification, "Network Error! Check your internet and try again");
                    });
                });
    }

    @FXML
    public void backToStaffDashboard() {
        showStaffDashboard();
    }

    @FXML
    public void handleUpdateStaffInfo() {
        if (!validateStaffUpdateForm()) return;

        UtilityMethods.processingButton(updateStaffBtn);
        FindStaffResponse info = updateStaffDTO();

        apiUtilityClass.updateStaffInfo(info,
                () -> {
                    //On Success
                    Platform.runLater( () -> {
                        UtilityMethods.notProcessingButton(updateStaffBtn, "SAVE CHANGES");
                        staffDetailsPanel.setVisible(false);
                        staffDetailsPanel.setManaged(false);

                        staffManagementContainer.setVisible(true);
                        staffManagementContainer.setManaged(true);

                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater( () -> {
                        UtilityMethods.notProcessingButton(updateStaffBtn, "SAVE CHANGES");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater( () -> {
                        UtilityMethods.notProcessingButton(updateStaffBtn, "SAVE CHANGES");
                        UtilityMethods.showError(notification, "Network Error! Check your internet and try again");
                    });
                });
    }

    @FXML
    public void showAddStudentCard() {
        clearForm();

        if (genderComboBox.getItems().isEmpty()) {
            genderComboBox.getItems().addAll("Male", "Female");
        }

        List<GradeCaching> gradeCaching = UtilityMethods.gradeCacheManager.getGradesCache();
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
        List<GradeCaching> cachedGrades = UtilityMethods.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (studentSearchClassBox.getItems().isEmpty()) {
                studentSearchClassBox.getItems().addAll(cachedGrades);
            }
        }

        studentDetailsPanel.setVisible(false);
        studentDetailsPanel.setManaged(false);

        studentSearchFieldBox.setVisible(true);
        studentSearchFieldBox.setManaged(true);

        switchPageCards(updateStudentInfoCard, addStudentCard,
                updateStudentToggleBtn, addStudentToggleBtn);
    }

    @FXML
    public void handleSaveAndClear() {
        if (!validateForm()) return;

        UtilityMethods.processingButton(saveAndClearBtn);
        saveStudent(() -> {
            // On Success
            Platform.runLater(() -> {
                clearForm();
                UtilityMethods.notProcessingButton(saveAndClearBtn, "Save Student");
                UtilityMethods.showSuccess(notification, "Student saved successfully! ID: " + apiUtilityClass.successMessageContainer);
            });
        }, () -> {
            // On Failure
            Platform.runLater(() -> {
                UtilityMethods.notProcessingButton(saveAndClearBtn, "Save Student");
                UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
            });
        }, () -> {
            // On Network Failure
            Platform.runLater(() -> {
                UtilityMethods.notProcessingButton(saveAndClearBtn, "Save and Close");
                UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
            });
        });
    }

    @FXML
    public void fetchGradeStudents() {
        GradeCaching selectedGrade = studentSearchClassBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) return;

        showLoading();
        apiUtilityClass.loadClassStudents(selectedGrade.getLevelId(), staffId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        hideLoading();
                        studentSearchComboBox.getItems().clear();
                        studentSearchComboBox.getItems().addAll(UtilityMethods.studentsHolder);
                        studentSearchComboBox.setDisable(false);
                        UtilityMethods.showSuccess(notification, "Students loaded");
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        hideLoading();
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        hideLoading();
                        UtilityMethods.showError(notification, "Network error! Check your connection");
                    });
                });
    }

    @FXML
    public void handleFindStudent() {
        StudentsHolder student = studentSearchComboBox.getSelectionModel().getSelectedItem();
        if (student == null) {
            UtilityMethods.showError(notification, "Select student");
            return;
        }

        UtilityMethods.processingButton(findStudentBtn);
        apiUtilityClass.searchStudentData(student.getStudentId(), staffId,
                () -> {
                    // On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(findStudentBtn, "Find Student");
                        UtilityMethods.showSuccess(notification, "Student Found");
                        studentInfoUpdatePanel(UtilityMethods.updateStudentPersonalData);
                    });
                },
                () -> {
                    //On failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(findStudentBtn, "Find Student");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(findStudentBtn, "Find Student");
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void backToDashboard() {
        showStaffDashboard();
    }

    @FXML
    public void handleLoadClassResults() {
        SemesterCaching semesterCaching = classResultsSemesterComboBox.getSelectionModel().getSelectedItem();
        if (semesterCaching == null) {
            UtilityMethods.showError(notification, "Select Semester to Load Results");
            return;
        }

        GradeCaching gradeCaching = classResultsGradeComboBox.getSelectionModel().getSelectedItem();
        if (gradeCaching == null) {
            UtilityMethods.showError(notification, "Select Grade to Load Results");
            return;
        }

        String gradeId = gradeCaching.getLevelId();
        String semesterId = semesterCaching.getSemesterId();

        UtilityMethods.processingButton(loadClassResultsBtn);
        apiUtilityClass.loadClassResults(gradeId, semesterId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(loadClassResultsBtn, "FETCH RESULTS");
                        UtilityMethods.showSuccess(notification, "Results successfully loaded");
                        populateClassResults(UtilityMethods.classResults);
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(loadClassResultsBtn, "FETCH RESULTS");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);

                    });
                }, () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(loadClassResultsBtn, "Fetch Results");
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }




    /*=========== Utility methods for the Class ==============*/


    @FXML
    public void initializeAfterLogin() {
        apiUtilityClass = new ApiUtilityClass(this);
        setActiveButton(mainDashboardBtn);
        loadPrincipalInfo();
        showLoading();
        apiUtilityClass.loadPrincipalData(staffId, notification,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        loadMainDashboardData();

                        hideLoading();
                    });
                });
    }

    private void loadFinanceDashboard() {
        List<GradeCaching> cachedGrades = UtilityMethods.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            Platform.runLater(() -> classLevelComboBox.getItems().setAll(cachedGrades));
        }

        List<SemesterCaching> cachedSemesters = UtilityMethods.semesterCachingManager.getCachedSemesters();
        if (cachedSemesters != null && !cachedSemesters.isEmpty()) {
            Platform.runLater(() -> semesterComboBox.getItems().setAll(cachedSemesters));
        }

        List<ClassFeesSummary> cachedClassFees = UtilityMethods.feesCacheManager.getClassFeesSummaryCache();
        if (cachedClassFees != null && !cachedClassFees.isEmpty()) {
            Platform.runLater(() -> classSummaryTable.getItems().setAll(cachedClassFees));
        }
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
            selectedRoles.add(StaffRoles.ADMINISTRATOR.name());
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

    private void handleSearchStaff(String staffId) {
        showLoading();

        apiUtilityClass.searchStaffData(staffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        hideLoading();
                        displayStaffDetails(apiUtilityClass.findStaffResponse);
                    });
                },
                () -> {
            //On failure
                    Platform.runLater(() -> {
                        hideLoading();
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        hideLoading();
                        UtilityMethods.showError(notification, "Network Error! Check your connection and try again");
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

        if (!role1.isSelected() || !role2.isSelected() || !role3.isSelected() ||
                !role4.isSelected() || !role5.isSelected()) {
            UtilityMethods.showError(notification, "Please select at least one role");
            return false;
        }

        if (editGenderComboBox.getSelectionModel().getSelectedItem() == null) {
            UtilityMethods.showError(notification, "Please select gender");
            return false;
        }

        if (editStatusComboBox.getSelectionModel().getSelectedItem() == null) {
            UtilityMethods.showError(notification, "Please select status");
            return false;
        }

        return true;
    }

    private void populateClassResults(List<ViewClassSemesterReport> viewClassSemesterReports) {
        gradeNameLabel.setText(viewClassSemesterReports.getFirst().getClassName());
        semesterNameLabel.setText(viewClassSemesterReports.getFirst().getSemesterName());

        classResultsTable.getItems().setAll(viewClassSemesterReports);

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

    public void displayFilteredFeesTable(List<FeesSummaryTable> feesSummaryTable,
                                         String filterLabel, String studentsCount) {
        filterTitleLabel.setText(filterLabel);
        filteredCountLabel.setText(studentsCount);
        filteredStudentsTable.getItems().setAll(feesSummaryTable);
        filteredStudentsPanel.setVisible(true);
        filteredStudentsPanel.setManaged(true);
    }

    //Set the logged in staff Id called from login
    public void setStaffId(String currentStaffId) {
        this.staffId = currentStaffId;
    }

    //Load staff Dashboard data on open
    private void loadStaffDashboardData() {
        totalStaffCountLabel.setText(
                String.valueOf(UtilityMethods.totalStaffFromBackend)
        );

        teachingStaffCountLabel.setText(
                String.valueOf(UtilityMethods.totalTeachingStaff)
        );

        List<ViewStaffList> staffList = UtilityMethods.staffListCacheManager.getCachedStaffList();
        if (staffList != null && !staffList.isEmpty()) {
            allStaffTable.getItems().setAll(staffList);
        }
    }

    //Load finance Dashboard Data on open
    private void loadFinanceDashboardData() {
        totalSemesterFeesLabel.setText(
                String.valueOf(UtilityMethods.totalSemesterFeesFromBackend)
        );
        amountPaidLabel.setText(
                String.valueOf(UtilityMethods.feesPaidFromBackend)
        );
        collectionProgressBar.setProgress(
                UtilityMethods.feesProgressBarFromBackend
        );
        String collectionPercent = collectionPercentageCalc(
                UtilityMethods.totalSemesterFeesFromBackend, UtilityMethods.feesPaidFromBackend
        ).toString();
        collectionPercentageLabel.setText(
                collectionPercent + "%"
        );
        String outStanding = calOutstandingBalance(
                UtilityMethods.totalSemesterFeesFromBackend, UtilityMethods.feesPaidFromBackend
        ).toString();

        outstandingBalanceLabel.setText(outStanding);
    }

    private Double calOutstandingBalance(Double totalSemesterFeesFromBackend,
                                         Double feesPaidFromBackend) {
        return totalSemesterFeesFromBackend - feesPaidFromBackend;
    }

    //Calculating collection percentage
    private Double collectionPercentageCalc(Double totalSemesterFeesFromBackend,
                                            Double feesPaidFromBackend) {
        return (feesPaidFromBackend / totalSemesterFeesFromBackend) * 100;
    }

    //Load student Dashboard data
    private void loadStudentDashboardData() {
        totalStudentsCountLabel.setText(
                String.valueOf(UtilityMethods.totalStudentsFromBackend)
        );
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    private void loadPrincipalInfo() {
        versionLabel.setText(apiUtilityClass.applicationVersion);
        principalNameLabel.setText(principalName.toUpperCase());
        institutionNameLabel.setText(institutionName.toUpperCase());
    }

    public void loadMainDashboardData() {

        //Load total students
        totalStudentsLabel.setText(String.valueOf(UtilityMethods.totalStudentsFromBackend));

        //Load total Staff
        totalStaffLabel.setText(String.valueOf(UtilityMethods.totalStaffFromBackend));

        //Load semester Fees
        semesterFeesLabel.setText(String.valueOf(UtilityMethods.totalSemesterFeesFromBackend));

        //Load fees Paid
        feesPaidLabel.setText(String.valueOf(UtilityMethods.feesPaidFromBackend));

        //Calculate progress
        try {
            Double totalFeesPaid = UtilityMethods.feesPaidFromBackend;
            if (totalFeesPaid > 0.0) {
                UtilityMethods.feesProgressBarFromBackend = totalFeesPaid / UtilityMethods.totalSemesterFeesFromBackend;
            } else {
                UtilityMethods.feesProgressBarFromBackend = 0.0;
            }
            feesProgressBar.setProgress(UtilityMethods.feesProgressBarFromBackend);
        } catch (Exception e) {
            feesProgressBar.setProgress(0.0);
            UtilityMethods.showError(notification, "Couldn't get fees Progress");
        }

        List<AbsenteesView> cachedStudents = UtilityMethods.cacheManager.getCachedAbsenteeStudents();
        if (cachedStudents != null && !cachedStudents.isEmpty()) {
            absenteesTable.getItems().setAll(cachedStudents);
        }

    }


    private void switchView(ScrollPane viewToShow, Button activeButton) {
        //Hide all views
        mainDashboardView.setVisible(false);
        mainDashboardView.setManaged(false);

        staffDashboardView.setVisible(false);
        staffDashboardView.setManaged(false);

        financeDashboardView.setVisible(false);
        financeDashboardView.setManaged(false);

        studentDashboardView.setVisible(false);
        studentDashboardView.setManaged(false);

        addNewStudentView.setVisible(false);
        addNewStudentView.setManaged(false);

        manageResultsView.setVisible(false);
        manageResultsView.setManaged(false);

        viewToShow.setVisible(true);
        viewToShow.setManaged(true);

        setActiveButton(activeButton);
    }

    private void setActiveButton(Button activeButton) {
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #ecf0f1; " +
                "-fx-font-size: 14px; -fx-padding: 15 20 15 20; -fx-background-radius: 8; " +
                "-fx-cursor: hand; -fx-border-color: transparent;";

        String activeStyle = "-fx-background-color: #667eea; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 15 20 15 20; " +
                "-fx-background-radius: 8; -fx-cursor: hand;";

        //Set all buttons to inactive
        mainDashboardBtn.setStyle(inactiveStyle);
        staffDashboardBtn.setStyle(inactiveStyle);
        financeDashboardBtn.setStyle(inactiveStyle);
        studentDashboardBtn.setStyle(inactiveStyle);

        //Set active button style
        activeButton.setStyle(activeStyle);
    }

    private void displayStaffDetails(FindStaffResponse staff) {
        staffFullNameLabel.setText(staff.getFirstName() + " " + staff.getSurname());
        staffIdLabel.setText(staff.getStaffId());
        editFirstNameField.setText(staff.getFirstName());
        editLastNameField.setText(staff.getSurname());
        editEmailField.setText(staff.getEmail());
        editPhoneField.setText(staff.getPhoneNumber());
        editDateOfBirthPicker.setValue(LocalDate.parse(staff.getDateOfBirth()));

        // Populate and preselect role
        getStaffRoles(staff.getStaffRoles());
        setRoleCheckBoxes();

        editStatusComboBox.getItems().addAll(StaffStatus.values());
        editStatusComboBox.setValue(StaffStatus.valueOf(staff.getStaffStatus()));


        editGenderComboBox.getItems().addAll(Gender.values());
        editGenderComboBox.setValue(Gender.valueOf(staff.getGender().toUpperCase()));

        staffDetailsPanel.setVisible(true);
        staffDetailsPanel.setManaged(true);

        staffManagementContainer.setVisible(false);
        staffManagementContainer.setManaged(false);
    }

    private void setRoleCheckBoxes() {
        for (StaffRoles staffRole : UtilityMethods.staffRoles) {
            if (staffRole.equals(StaffRoles.PRINCIPAL)) {
                role1.setSelected(true);
            } else if (staffRole.equals(StaffRoles.ACCOUNTANT)) {
                role2.setSelected(true);
            } else if (staffRole.equals(StaffRoles.TEACHING_STAFF)) {
                role3.setSelected(true);
            } else if (staffRole.equals(StaffRoles.GENERAL_STAFF)) {
                role4.setSelected(true);
            } else if (staffRole.equals(StaffRoles.ADMINISTRATOR)){
                role5.setSelected(true);
            }
        }
    }

    private void getStaffRoles(List<String> roles) {
        for (String role : roles) {
            UtilityMethods.staffRoles.add(StaffRoles.valueOf(role));
        }
    }

    private void displayFeeReport(StudentFeesPaymentDisplay data) {
        studentIdDisplayLabel.setText(data.getStudentId());
        studentNameLabel.setText(data.getStudentName());
        studentClassLabel.setText(data.getLevelName());
        studentSemesterLabel.setText(data.getSemesterName());

        // Get Fees Values
        Double totalFees = Double.parseDouble(data.getTotalFeesAmount());
        Double totalFeesPaid = Double.parseDouble(data.getTotalAmountPaid());
        double outStandingBalance = totalFees -  totalFeesPaid;

        studentTotalFeesLabel.setText(totalFees.toString());
        studentAmountPaidLabel.setText(totalFeesPaid.toString());
        studentBalanceLabel.setText(Double.toString(outStandingBalance));

        Double percent = collectionPercentageCalc(totalFees, totalFeesPaid);
        studentPaymentPercentageLabel.setText(percent + "%");
        studentPaymentProgressBar.setProgress(percent/100);

        //Populate the payment history table
        paymentHistoryTable.getItems().addAll(data.getPaymentHistory());
        feeReportPanel.setVisible(true);
        feeReportPanel.setManaged(true);
        noStudentFoundPanel.setVisible(false);
        noStudentFoundPanel.setManaged(false);
    }

    //Calculate total fees paid
    private Double calAmountPaid(List<PaymentHistory> paymentHistory) {
        double totalFeesPaid = 0.0;
        for (PaymentHistory history : paymentHistory) {
            double paidFee = Double.parseDouble(history.getAmountPaid());
            totalFeesPaid += paidFee;
        }
        return totalFeesPaid;
    }

    public void displayStudentResultsPanel(StudentResult studentResult) {
        //Setting panel data
        resultsStudentIdLabel.setText(studentResult.getStudentId());
        resultsStudentNameLabel.setText(studentResult.getStudentName());
        resultsClassLabel.setText(studentResult.getGrade());
        resultsSemesterLabel.setText(studentResult.getSemesterName());
        totalScoreLabel.setText(studentResult.getTotalScore().toString());
        averageScoreLabel.setText(studentResult.getAverageScore().toString());
        positionLabel.setText(studentResult.getPosition());
        subjectResultsTable.getItems().addAll(studentResult.getStudentSubjectResults());

        //Display panel
        resultsDisplayPanel.setVisible(true);
        resultsDisplayPanel.setManaged(true);
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
            UtilityMethods.showError(notification, "No grade selected!");
            return;
        }
        selectedGradeId = selectedClass.getLevelId();

        apiUtilityClass.enrollNewStudent(staffId, firstName, lastName, selectedGradeId, gender,
                dob, homeTown, parentName, parentPhone, onSuccess, onFailure, onNetworkIssue);
    }

    private void studentInfoUpdatePanel(UpdateStudentPersonalData data) {
        List<GradeCaching> gradeCachings  = UtilityMethods.gradeCacheManager.getGradesCache();
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
        updateStudentNameLabel.setText(
                data.getFirstName() + " " + data.getLastName()
        );

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

        studentSearchFieldBox.setVisible(false);
        studentSearchFieldBox.setManaged(false);

        studentDetailsPanel.setVisible(true);
        studentDetailsPanel.setManaged(true);
    }

}