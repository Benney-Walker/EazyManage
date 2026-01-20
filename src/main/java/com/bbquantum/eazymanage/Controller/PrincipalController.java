package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.DTO.Academics.*;
import com.bbquantum.eazymanage.DTO.Finance.ClassFeesSummary;
import com.bbquantum.eazymanage.DTO.Finance.FeesSummaryTable;
import com.bbquantum.eazymanage.DTO.Finance.StudentFeesPaymentDisplay;
import com.bbquantum.eazymanage.DTO.Finance.StudentPaymentHistory;
import com.bbquantum.eazymanage.DTO.Institution.*;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.stage.Stage;

import java.util.List;
import java.util.ResourceBundle;

public class PrincipalController implements Initializable {

    private UtilityClass utilityClass;

    //Store staff Data from backend
    String institutionName;
    String principalName;

    //Store logged in staff Id from login
    public String currentStaffId;

    //Store student Data for Update
    public UpdateStudentPersonalData updateStudentPersonalData;
    //Store staff Data for Update
    public FindStaffResponse findStaffResponse;
    //Get refreshed staff list
    public List<RecentStaffView> refreshedStaffViewList;
    //Store fees filter
    public List<FeesSummaryTable> feesSummaryTable;

    public PasswordRecoveryDTO passwordRecoveryDTO;

    public List<GenerateStudentReport> generateStudentReport;

    //LEFT SIDEBAR - Navigation side
    @FXML public StackPane notification;
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
    @FXML public Label totalStudentsLabel;
    @FXML public Label totalStaffLabel;
    @FXML private Label semesterFeesLabel;
    @FXML public Label feesPaidLabel;
    @FXML public ProgressBar feesProgressBar;
    @FXML public TableView<RecentStudentView> recentStudentsTable;
    @FXML private TableColumn<RecentStudentView, String> studentIdColumn;
    @FXML private TableColumn<RecentStudentView, String> studentNameColumn;
    @FXML private TableColumn<RecentStudentView, String> studentLevelColumn;
    @FXML private TableColumn<RecentStudentView, String> dateAddedColumn;
    @FXML private TableColumn<RecentStudentView, String> statusColumn;

    //STAFF DASHBOARD VIEW
    @FXML private ScrollPane staffDashboardView;
    @FXML private Label totalStaffCountLabel;
    @FXML private TextField searchStaffIdField;
    @FXML private VBox newPasswordBox;
    @FXML private TextField newPasswordField;
    @FXML public VBox staffDetailsPanel;
    @FXML private Label staffFullNameLabel;
    @FXML private Label staffRoleLabel;
    @FXML private TextField editFirstNameField;
    @FXML private TextField editLastNameField;
    @FXML private TextField editEmailField;
    @FXML private TextField editPhoneField;
    @FXML private DatePicker editDateOfBirthPicker;
    @FXML private RadioButton editMaleRadio;
    @FXML private RadioButton editFemaleRadio;
    @FXML private ToggleGroup editGenderGroup;
    @FXML private ComboBox<String> editRoleComboBox;
    @FXML private Label staffUpdateMessageLabel;
    @FXML private Button searchStaffBtn;
    @FXML private Button passwordResetBtn;
    @FXML private Button refreshStaffListBtn;
    @FXML
    public VBox noStaffFoundPanel;
    @FXML
    public TableView<RecentStaffView> allStaffTable;
    @FXML private TableColumn<RecentStaffView, String> staffIdColumn;
    @FXML private TableColumn<RecentStaffView, String> staffNameColumn;
    @FXML private TableColumn<RecentStaffView, String> staffGradeColumn;

    //FINANCE DASHBOARD VIEW
    @FXML private ScrollPane financeDashboardView;
    @FXML
    public Label totalSemesterFeesLabel;
    @FXML private Label amountPaidLabel;
    @FXML private Label collectionPercentageLabel;
    @FXML private ProgressBar collectionProgressBar;
    @FXML private Label outstandingBalanceLabel;
    @FXML private TextField studentIdSearchField;
    @FXML private Button searchFeesHistoryBtn;
    @FXML private Button fullyPaidBtn;
    @FXML private Button partiallyPaidBtn;
    @FXML private Button notPaidBtn;
    @FXML
    public ComboBox<SemesterCaching> semesterComboBox;
    @FXML
    public ComboBox<GradeCaching> classLevelComboBox;
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
    @FXML private TableView<StudentPaymentHistory> paymentHistoryTable;
    @FXML private TableColumn<StudentPaymentHistory, String> paymentDateColumn;
    @FXML private TableColumn<StudentPaymentHistory, String> paymentAmountColumn;
    @FXML private TableColumn<StudentPaymentHistory, String> paymentPersonColumn;
    @FXML private TableColumn<StudentPaymentHistory, String> paymentPersonContactColumn;
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
    @FXML private TextField searchStudentIdField;
    @FXML private Button findStudent;
    @FXML private Button getResultsBtn;
    @FXML private VBox studentDetailsPanel;
    @FXML private Label studentFullNameLabel;
    @FXML private Label studentIdLabelDisplay;
    @FXML private Label studentClassLabelDisplay;
    @FXML private Label statusField;
    @FXML private Label studentGenderLabel;
    @FXML private Label studentDOBLabel;
    @FXML private Label guardianPhoneLabel;
    @FXML private Label studentGuardianNameLabel;
    @FXML private VBox noStudentFoundPanelStudent;
    @FXML private TextField resultsSearchIdField;
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
    @FXML private TableView<StudentResultsSubject> subjectResultsTable;
    @FXML private TableColumn<StudentResultsSubject, String> subjectNameColumn;
    @FXML private TableColumn<StudentResultsSubject, String> subjectScoreColumn;
    @FXML private TableColumn<StudentResultsSubject, String> subjectGradeColumn;
    @FXML private TableColumn<StudentResultsSubject, String> subjectRemarkColumn;

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
    @FXML private Button saveAndCloseBtn;
    @FXML private Button saveAndClearBtn;

    //ADD SUBJECT VIEW
    @FXML private ScrollPane addNewSubjectView;
    @FXML private TextField subjectNameField;
    @FXML private ComboBox<GradeCaching> gradeComboBox;
    @FXML private ComboBox<SemesterCaching> subjectSemesterComboBox;
    @FXML private Button saveSubjectBtn;

    //ADD SEMESTER PAGE
    @FXML private ScrollPane addSemesterView;
    @FXML private TextField semesterNameField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField academicYearField;
    @FXML private Button saveSemesterBtn;

    //ADD GRADES CRITERIA PAGE
    @FXML private ScrollPane addGradesView;
    @FXML private TextField gradeCriteriaField;
    @FXML private TextField lowerRangeField;
    @FXML private TextField upperRangeField;
    @FXML private TextField gradeRemarksField;
    @FXML private Button saveGradeBtn;

    //ADD CLASS PAGE
    @FXML private ScrollPane addNewClassView;
    @FXML private TextField classNameField;
    @FXML private ComboBox<StaffCaching> staffComboBox;
    @FXML private Button saveClassBtn;

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

    //SPINNER
    @FXML private VBox loadingOverlay;
    @FXML private ProgressIndicator loadingSpinner;

    @Override
    public void initialize(URL url,  ResourceBundle resourceBundle) {
        studentIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getStudentId())));

        studentNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getStudentName())));

        studentLevelColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentGrade()));

        dateAddedColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDateOfRegistration()));

        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getStatus())));




        paymentDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDateOfPayment()));

        paymentAmountColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAmountPaid()));

        paymentPersonColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPersonWhoPaid()));

        paymentPersonContactColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getContactInfo()));

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

        subjectNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSubjectName()));

        subjectScoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getScore()));

        subjectGradeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getGrade()));

        subjectRemarkColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRemark()));

        staffIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStaffId()));

        staffNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStaffName()));

        staffGradeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAssignedLevel()));


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

    }

    @FXML
    private void showMainDashboard() {
        utilityClass.loadPrincipalData(currentStaffId, notification,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        loadMainDashboardData();

                        hideLoading();
                    });
                });

        switchView(mainDashboardView, mainDashboardBtn);
    }

    @FXML
    public void handleLogout() throws IOException {
        utilityClass.clearSession();
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
    private void showStaffDashboard() {
        switchView(staffDashboardView, staffDashboardBtn);

        loadStaffDashboardData();
    }

    @FXML
    private void showFinanceDashboard() {
        loadFinanceDashboardData();

        switchView(financeDashboardView, financeDashboardBtn);

        List<GradeCaching> cachedGrades = utilityClass.gradeCacheManager.getGradeCache();
        if (cachedGrades != null) {
            Platform.runLater(() -> classLevelComboBox.getItems().setAll(cachedGrades));
        }

        List<SemesterCaching> semesterCaching = utilityClass.semesterCachingManager.getCachedSemesters();
        if (semesterCaching != null) {
            Platform.runLater(() -> semesterComboBox.getItems().setAll(semesterCaching));
        }

        List<ClassFeesSummary> cachedClassfees = utilityClass.feesCacheManager.getClassFeesSummaryCache();
        if (cachedClassfees != null) {
            Platform.runLater(() -> classSummaryTable.getItems().setAll(cachedClassfees));
        }
    }

    @FXML
    private void showStudentDashboard() {
        switchView(studentDashboardView, studentDashboardBtn);

        loadStudentDashboardData();

        List<GradeCaching> cachedGrades = utilityClass.gradeCacheManager.getGradeCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            resultsClassComboBox.getItems().setAll(cachedGrades);
            printStudentClassComboBox.getItems().setAll(cachedGrades);
        }

        List<SemesterCaching> semesterCaching = utilityClass.semesterCachingManager.getCachedSemesters();
        if (semesterCaching != null && !semesterCaching.isEmpty()) {
            resultsSemesterComboBox.getItems().setAll(semesterCaching);
        }
    }

    @FXML
    public void openAddClassPage() {
        List<StaffCaching> staffCaching = utilityClass.staffCacheManager.getCachedStaffList();
        if (staffCaching != null && !staffCaching.isEmpty()) {
            if (staffComboBox.getItems().isEmpty()) {
                staffComboBox.getItems().addAll(staffCaching);
            }
        }

        switchView(addNewClassView, staffDashboardBtn);
    }

    public void openClassResultsView() {
        List<GradeCaching> cachedGrades = utilityClass.gradeCacheManager.getGradeCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (classResultsGradeComboBox.getItems().isEmpty()) {
                classResultsGradeComboBox.getItems().addAll(cachedGrades);
            }
        }

        List<SemesterCaching> semesterCaching = utilityClass.semesterCachingManager.getCachedSemesters();
        if (semesterCaching != null && !semesterCaching.isEmpty()) {
            if (classResultsSemesterComboBox.getItems().isEmpty()) {
                classResultsSemesterComboBox.getItems().addAll(semesterCaching);
            }
        }

        switchView(manageResultsView, staffDashboardBtn);
    }

    @FXML
    public void handleResetPassword() {
        String staffId = searchStaffIdField.getText().trim();
        if (staffId.isEmpty()) {
            utilityClass.showError(notification, "Please enter a staff id!");
            return;
        }

        if (!newPasswordBox.isVisible()) {
            newPasswordBox.setVisible(true);
            newPasswordBox.setManaged(true);
        }

        String newPassword = newPasswordField.getText();
        if (newPassword.isEmpty()) {
            utilityClass.showError(notification, "Please enter a new password!");
            return;
        }

        utilityClass.processingButton(passwordResetBtn);

        // Confirm action
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Reset Password");
        confirmAlert.setHeaderText("Reset password for Staff ID: " + staffId);
        confirmAlert.setContentText("New Password: " + newPassword);

        if (confirmAlert.showAndWait().get() != ButtonType.OK) {
            utilityClass.notProcessingButton(passwordResetBtn, "Recover Password");
            return;
        }
        utilityClass.recoverPassword(staffId, newPassword,
                () -> {
            //On Success
                    Platform.runLater( () -> {
                        utilityClass.notProcessingButton(passwordResetBtn, "Recover password");
                        utilityClass.showSuccess(notification, passwordRecoveryDTO.getMessage());
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater( () -> {
                        utilityClass.notProcessingButton(passwordResetBtn, "Recover Password");
                        utilityClass.showError(notification, passwordRecoveryDTO.getMessage());
                    });
                }, () -> {
            //On Network Issue
                    Platform.runLater( () -> {
                        utilityClass.notProcessingButton(passwordResetBtn, "Recover password");
                        utilityClass.showError(notification, "Network error! Check your internet and try again");
                    });
                });
    }

    @FXML
    public void handleCancelUpdate() {
        cancelUpdate();
    }

    @FXML
    public void handleUpdateStaff() {
        staffDetailsPanel.setVisible(false);
        staffDetailsPanel.setManaged(false);

        utilityClass.showError(notification, "Feature not yet implemented");
    }

    @FXML
    public void handleExportToExcel() {

        utilityClass.showError(notification, "Feature not yet implemented");
    }

    @FXML
    public void handleSearchStudentFees() {
        //Extract studentId from textField
        String studentId = studentIdSearchField.getText().trim();
        if (studentId.isEmpty()) {
            utilityClass.showError(notification, "Please enter a student id!");
            return;
        }

        //Get selected class from combo box
        GradeCaching selectedGrade = classLevelComboBox.getSelectionModel().getSelectedItem();
        String gradeId = null;
        if (selectedGrade == null) {
            utilityClass.showError(notification, "Please select Grade");
            return;
        }
        gradeId = selectedGrade.getLevelId();

        //Get selected semester from combo box
        SemesterCaching selectedSemester = semesterComboBox.getSelectionModel().getSelectedItem();
        String semesterId = null;
        if (selectedSemester == null) {
            utilityClass.showError(notification, "Please select Semester");
            return;
        }
        semesterId = selectedSemester.getSemesterId();
        utilityClass.processingButton(searchFeesHistoryBtn);

        utilityClass.searchFeesReport(studentId, semesterId, gradeId,
                () -> {
                    // On success
                    Platform.runLater(() -> {
                        displayFeeReport(utilityClass.report);
                        utilityClass.notProcessingButton(searchFeesHistoryBtn, "Search Report");
                    });
                },
                () -> {
                    // On failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(searchFeesHistoryBtn, "Search Report");
                        utilityClass.showError(notification,"Could not fetch data, check student Id and try again");
                    });
                }, () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(searchFeesHistoryBtn, "Search Report");
                        utilityClass.showError(notification,"Network error! Check your internet and try again");
                    });
                });
    }

    @FXML
    public void showFullyPaid() {
        String ownUrl = "/fully-paid-students/" + currentStaffId;
        String filter = "FULLY PAID STUDENTS";

        utilityClass.getFeesFilterData(ownUrl,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(fullyPaidBtn, "Fully Paid");
                        String size = String.valueOf(feesSummaryTable.size());
                        displayFilteredFeesTable(feesSummaryTable, filter, size);
                    });
                },
                () -> {
            //On failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(fullyPaidBtn, "Fully Paid");
                        filteredStudentsPanel.setVisible(false);
                        filteredStudentsPanel.setManaged(false);
                        utilityClass.showError(notification, "Failed to load payment filter. Try again!");
                    });
                });
    }

    @FXML
    public void showPartialPayment() {
        String ownUrl = "/partially-paid-students/" + currentStaffId;
        String filter = "PARTIALLY PAID STUDENTS";

        utilityClass.getFeesFilterData(ownUrl,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(partiallyPaidBtn, "Partial Payment");
                        String size = String.valueOf(feesSummaryTable.size());
                        displayFilteredFeesTable(feesSummaryTable, filter, size);
                    });
                },
                () -> {
                    //On failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(notPaidBtn, "Not Paid");
                        filteredStudentsPanel.setVisible(false);
                        filteredStudentsPanel.setManaged(false);
                        utilityClass.showError(notification, "Failed to load payment filter. Try again!");
                    });
                });
    }

    @FXML
    public void showNotPaid() {
        String ownUrl = "/not-paid-students/" + currentStaffId;
        String filter = "NOT PAID STUDENTS";

        utilityClass.getFeesFilterData(ownUrl,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(fullyPaidBtn, "Fully Paid");
                        String size = String.valueOf(feesSummaryTable.size());
                        displayFilteredFeesTable(feesSummaryTable, filter, size);
                    });
                },
                () -> {
                    //On failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(fullyPaidBtn, "Fully Paid");
                        filteredStudentsPanel.setVisible(false);
                        filteredStudentsPanel.setManaged(false);
                        utilityClass.showError(notification, "Failed to load payment filter. Try again!");
                    });
                });
    }

    @FXML
    public void handleClearFilter() {
        filteredStudentsPanel.setVisible(false);
        filteredStudentsPanel.setManaged(false);
    }

    @FXML
    public void handleExportFilteredList() {

        utilityClass.showError(notification, "Feature not yet implemented");
    }

    @FXML
    public void handlePrintReport() {

        utilityClass.showError(notification, "Feature not yet implemented");
    }

    @FXML
    public void refreshStaffList() {
        utilityClass.processingButton(refreshStaffListBtn);
        utilityClass.refreshAllStaffList(
                () -> {
                    //on Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(refreshStaffListBtn, "Refresh List");
                        allStaffTable.getItems().setAll(refreshedStaffViewList);
                    });
                },
                () -> {
                    //On failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(refreshStaffListBtn, "Refresh List");
                        utilityClass.showError(notification, "Could not refresh staff list");
                    });
                });
    }

    @FXML
    public void openAddStudentPage() {
        // Populate gender ComboBox
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
        switchView(addNewStudentView, studentDashboardBtn);
    }

    @FXML
    public void backToStudentPage() {
        switchView(studentDashboardView, studentDashboardBtn);
    }

    @FXML
    public void handleSearchStudent() {
        String studentId = searchStudentIdField.getText().trim();
        if (studentId.isEmpty()) {
            utilityClass.showError(notification, "Please enter student id");
            return;
        }
        utilityClass.processingButton(findStudent);
        utilityClass.searchStudentData(studentId,
                () -> {
            //On Success
            Platform.runLater(() -> {
                utilityClass.notProcessingButton(findStudent, "Find Student");
                displayStudentPersonalInfo(updateStudentPersonalData);
            });
                },
                () -> {
            //On failure
            Platform.runLater(() -> {
                utilityClass.notProcessingButton(findStudent, "Find Student");
                utilityClass.showError(notification, "Could not find student with id " + studentId);
            });
                });
    }

    @FXML
    public void handleEditStudent() {
        if (updateStudentPersonalData == null) {
            new Alert(Alert.AlertType.ERROR, "No student selected for editing").showAndWait();
            return;
        }

        utilityClass.showUpdateStudentDataDialog(notification);
    }

    @FXML
    public void handleSearchResults() {
        String studentId = resultsSearchIdField.getText().trim();
        if (studentId.isEmpty()) {
            utilityClass.showError(notification, "Please enter student id");
            return;
        }
        //Get selected grade/class
        GradeCaching selectedGrade = resultsClassComboBox.getSelectionModel().getSelectedItem();
        String classId = null;
        if (selectedGrade == null) {
            utilityClass.showError(notification, "No grade selected");
            return;
        }
        classId = selectedGrade.getLevelId();

        SemesterCaching selectedSemester = resultsSemesterComboBox.getSelectionModel().getSelectedItem();
        String semesterId = null;
        if (selectedSemester == null) {
            utilityClass.showError(notification, "No semester selected");
            return;
        }
        semesterId = selectedSemester.getSemesterId();
        utilityClass.processingButton(getResultsBtn);

        utilityClass.SearchStudentResults(studentId, classId, semesterId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(getResultsBtn, "Search Results");
                        displayStudentResultsPanel(utilityClass.studentResults);
                    });
                },
                () -> {
            //On failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(getResultsBtn, "Search Results");
                        noResultsFoundPanel.setVisible(true);
                        noResultsFoundPanel.setManaged(true);
                    });
                });
    }

    @FXML
    public void handleSearchStaff() {
        String staffId = searchStaffIdField.getText().trim();

        if (staffId.isEmpty()) {
            utilityClass.showError(notification, "Enter Staff ID");
            return;
        }

        utilityClass.searchStaffData(staffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(searchStaffBtn, "Find Staff");
                        displayStaffDetails(findStaffResponse);
                    });
                },
                () -> {
            //On failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(searchStaffBtn, "Find Staff");
                        staffDetailsPanel.setVisible(false);
                        staffDetailsPanel.setManaged(false);
                        noStaffFoundPanel.setVisible(true);
                        noStaffFoundPanel.setManaged(true);
                    });
                });
    }

    @FXML
    public void handlePrintReportCard() {

        utilityClass.showError(notification, "Feature not yet implemented");
    }

    @FXML
    public void initializeAfterLogin() {
        setActiveButton(mainDashboardBtn);
        loadPrincipalInfo();
        showLoading();

        utilityClass = new UtilityClass(this);
        utilityClass.loadPrincipalData(currentStaffId, notification,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        loadMainDashboardData();

                        hideLoading();
                    });
                });
    }

    @FXML
    public void openAddSemesterPage() {
        switchView(addSemesterView, staffDashboardBtn);
    }

    @FXML
    public void openAddSubjectPage() {

        List<GradeCaching> gradeCachings = utilityClass.gradeCacheManager.getGradeCache();
        if (gradeCachings != null && !gradeCachings.isEmpty()) {
            if (gradeComboBox.getItems().isEmpty()) {
                gradeComboBox.getItems().addAll(gradeCachings);
            }
        }
        // Show dialog to add new subject
        switchView(addNewSubjectView, staffDashboardBtn);
    }

    @FXML
    public void handlePrintStudentData() {
        GradeCaching selectedGrade = printStudentClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            utilityClass.showError(notification, "Please select a class first");
            return;
        }

        utilityClass.processingButton(printStudentDataBtn);
        utilityClass.fetchAndSaveStudentList(selectedGrade.getLevelId(), selectedGrade.getLevelName(),
                () -> {
                    // On success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(printStudentDataBtn, "🖨️ Print Student Data");
                    });
                },
                () -> {
                    // On failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(printStudentDataBtn, "🖨️ Print Student Data");
                    });
                }
        );
    }

    @FXML
    public void handleSaveAndClose() {
        if (!validateForm()) return;

        utilityClass.processingButton(saveAndCloseBtn);
        saveStudent(() -> {
            // On Success
            Platform.runLater(() -> {
                utilityClass.notProcessingButton(saveAndCloseBtn, "Save and Close");
                utilityClass.showSuccess(notification, "Student saved successfully! ID: " + utilityClass.studentId);
                Stage stage = (Stage) firstNameField.getScene().getWindow();
                stage.close();
            });
        }, () -> {
            // On Failure
            Platform.runLater(() -> {
                utilityClass.notProcessingButton(saveAndCloseBtn, "Save and Close");
                utilityClass.showError(notification, "Could not save student");
            });
        }, () -> {
            // On Network Failure
            Platform.runLater(() -> {
                utilityClass.notProcessingButton(saveAndCloseBtn, "Save and Close");
                utilityClass.showError(notification, "Network Error! Check your network connection and try again");
            });
        });
    }

    @FXML
    public void handleSaveAndClear() {
        if (!validateForm()) return;

        utilityClass.processingButton(saveAndCloseBtn);
        saveStudent(() -> {
            // On Success
            Platform.runLater(() -> {
                clearForm();
                utilityClass.notProcessingButton(saveAndClearBtn, "Save and Close");
                utilityClass.showSuccess(notification, "Student saved successfully! ID: " + utilityClass.studentId);
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
                utilityClass.notProcessingButton(saveAndCloseBtn, "Save and Close");
                utilityClass.showError(notification, "Network Error! Check your network connection and try again");
            });
        });
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
    public void handleBackToStaffDashboard() {
        switchView(staffDashboardView, staffDashboardBtn);
    }

    @FXML
    public void handleSaveSemester() {
        String semesterName = semesterNameField.getText().trim();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String academicYear = academicYearField.getText().trim();

        if (semesterName.isEmpty()) {
            utilityClass.showError(notification, "Semester name is required");
            return;
        }

        if (!semesterName.contains("_") || semesterName.contains(" ")
                || semesterName.contains("-")) {
            utilityClass.showError(notification, "Semester name must contain '_' eg. First_Term");
            return;
        }

        if (startDate == null || endDate == null) {
            utilityClass.showError(notification, "Start and end dates are required");
            return;
        }
        if (endDate.isBefore(startDate)) {
            utilityClass.showError(notification, "End date cannot be before start date");
            return;
        }
        if (academicYear.isEmpty()) {
            utilityClass.showError(notification, "Academic year is required");
            return;
        }

        utilityClass.processingButton(saveSemesterBtn);
        utilityClass.addNewSemester(semesterName, startDate, endDate, academicYear, currentStaffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveSemesterBtn, "Save Semester");
                        clearSemesterForm();
                        utilityClass.showSuccess(notification, "Successfully saved semester");
                    });
                }, () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveSemesterBtn, "Save Semester");
                        utilityClass.showError(notification, "Could not save semester! Try again");
                    });
                }, () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveSemesterBtn, "Save Semester");
                        utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });

    }

    @FXML
    public void backToDashboard() {
        showStaffDashboard();
    }

    @FXML
    public void openAddGrades() {
        switchView(addGradesView, staffDashboardBtn);
    }

    @FXML void handleSaveGrade() {
        String gradeCriteria = gradeCriteriaField.getText();
        Double lowerRange = Double.parseDouble(lowerRangeField.getText());
        Double upperRange = Double.parseDouble(upperRangeField.getText());
        String remarks = gradeRemarksField.getText();

        if (gradeCriteria.isEmpty()) {
            utilityClass.showError(notification, "Grade criteria is required");
            return;
        }

        if (lowerRange.isNaN() || upperRange.isNaN()) {
            utilityClass.showError(notification, "Please enter grade criteria marks");
            return;
        }

        if (lowerRange > upperRange) {
            utilityClass.showError(notification, "Lower range cannot be greater than upper range");
            return;
        }

        if (remarks.isEmpty()) {
            utilityClass.showError(notification, "Remarks is required");
            return;
        }

        utilityClass.processingButton(saveGradeBtn);
        GradingCriteria gradingCriteria = new GradingCriteria(
                lowerRange, upperRange, gradeCriteria, remarks
        );

        utilityClass.saveGradingCriteria(gradingCriteria, currentStaffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveGradeBtn, "Save Grade");
                        utilityClass.showSuccess(notification, "Successfully saved grading criteria");
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveGradeBtn, "Save Grade");
                        utilityClass.showError(notification, "Could not save grading criteria! Try again");
                    });
                }, () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveGradeBtn, "Save Grade");
                        utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });

    }

    @FXML
    public void handleAddClass() {
        String className = classNameField.getText();
        if (className.isEmpty()) {
            utilityClass.showError(notification, "Class name is required");
            return;
        }

        if (!className.contains("_") || className.contains(" ")) {
            utilityClass.showError(notification, "Class name must contain '_' eg.Grade_1");
            return;
        }

        StaffCaching staffCaching = staffComboBox.getSelectionModel().getSelectedItem();
        if (staffCaching == null) {
            utilityClass.showError(notification, "Please select a staff cache");
            return;
        }

        String staffId = staffCaching.getStaffId();

        utilityClass.processingButton(saveClassBtn);
        utilityClass.addNewGrade(className, staffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveClassBtn, "Add Class");
                        utilityClass.showSuccess(notification, "Successfully added class");
                    });
                }, () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveClassBtn, "Add Class");
                        utilityClass.showError(notification, "Could not add class! Try again");
                    });
                }, () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveClassBtn, "Add Class");
                        utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleLoadClassResults() {
        SemesterCaching semesterCaching = classResultsSemesterComboBox.getSelectionModel().getSelectedItem();
        if (semesterCaching == null) {
            utilityClass.showError(notification, "Select Semester to Load Results");
            return;
        }

        GradeCaching gradeCaching = classResultsGradeComboBox.getSelectionModel().getSelectedItem();
        if (gradeCaching == null) {
            utilityClass.showError(notification, "Select Grade to Load Results");
            return;
        }

        String gradeId = gradeCaching.getLevelId();
        String semesterId = semesterCaching.getSemesterId();

        utilityClass.processingButton(loadClassResultsBtn);
        utilityClass.loadClassResultsPrincipal(gradeId, semesterId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(loadClassResultsBtn, "Fetch Results");
                        utilityClass.showSuccess(notification, "Results successfully loaded");
                        populateClassResults(generateStudentReport);
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(loadClassResultsBtn, "Fetch Results");
                        utilityClass.showError(notification, "Could not fetch results");

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

    private void populateClassResults(List<GenerateStudentReport> generateStudentReports) {
        gradeNameLabel.setText(generateStudentReports.getFirst().getClassName());
        semesterNameLabel.setText(generateStudentReports.getFirst().getSemesterName());

        classResultsTable.getItems().setAll(generateStudentReports);

        resultsInfoBox.setVisible(true);
        resultsInfoBox.setManaged(true);

        classResultsTableContainer.setVisible(true);
        classResultsTableContainer.setManaged(true);
    }

    private void clearSemesterForm() {
        semesterNameField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        academicYearField.clear();
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
                dob, homeTown, parentName, parentPhone, currentStaffId, onSuccess, onFailure, onNetworkIssue);
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
        this.currentStaffId = currentStaffId;
    }

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy | HH:mm:ss");
        dateTimeLabel.setText(now.format(formatter));
    }

    //Load staff Dashboard data on open
    private void loadStaffDashboardData() {
        totalStaffCountLabel.setText(String.valueOf(utilityClass.totalStaffFromBackend));
        List<RecentStaffView> staffList = utilityClass.staffListCacheManager.getCachedStaffList();
        allStaffTable.getItems().setAll(staffList);
    }

    //Load finance Dashboard Data on open
    private void loadFinanceDashboardData() {
        totalSemesterFeesLabel.setText(String.valueOf(utilityClass.totalSemesterFeesFromBackend));
        amountPaidLabel.setText(String.valueOf(utilityClass.feesPaidFromBackend));
        collectionProgressBar.setProgress(utilityClass.feesProgressBarFromBackend);
        String collectionPercent = collectionPercentageCalc(utilityClass.totalSemesterFeesFromBackend, utilityClass.feesPaidFromBackend).toString();
        collectionPercentageLabel.setText(collectionPercent + "%");
        String outStanding = calOutstandingBalance(utilityClass.totalSemesterFeesFromBackend, utilityClass.feesPaidFromBackend).toString();
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
        totalStudentsCountLabel.setText(String.valueOf(utilityClass.totalStudentsFromBackend));
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    private void loadPrincipalInfo() {
        principalNameLabel.setText(principalName.toUpperCase());
        institutionNameLabel.setText(institutionName.toUpperCase());
    }

    public void loadMainDashboardData() {

        updateDateTime();

        //Load total students
        totalStudentsLabel.setText(String.valueOf(utilityClass.totalStudentsFromBackend));

        //Load total Staff
        totalStaffLabel.setText(String.valueOf(utilityClass.totalStaffFromBackend));

        //Load semester Fees
        semesterFeesLabel.setText(String.valueOf(utilityClass.totalSemesterFeesFromBackend));

        //Load fees Paid
        feesPaidLabel.setText(String.valueOf(utilityClass.feesPaidFromBackend));

        //Calculate progress
        try {
            Double totalFeesPaid = utilityClass.feesPaidFromBackend;
            if (totalFeesPaid > 0.0) {
                utilityClass.feesProgressBarFromBackend = totalFeesPaid / utilityClass.totalSemesterFeesFromBackend;
            } else {
                utilityClass.feesProgressBarFromBackend = 0.0;
            }
            feesProgressBar.setProgress(utilityClass.feesProgressBarFromBackend);
        } catch (Exception e) {
            feesProgressBar.setProgress(0.0);
            utilityClass.showError(notification, "Couldn't get fees Progress");
        }

        List<RecentStudentView> cachedStudents = utilityClass.cacheManager.getCachedStudents();
        if (cachedStudents != null) {
            recentStudentsTable.getItems().setAll(cachedStudents);
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

        addNewSubjectView.setVisible(false);
        addNewSubjectView.setManaged(false);

        addSemesterView.setVisible(false);
        addSemesterView.setManaged(false);

        addGradesView.setVisible(false);
        addGradesView.setManaged(false);

        addNewClassView.setVisible(false);
        addNewClassView.setManaged(false);

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

    public void displayStaffDetails(FindStaffResponse staff) {
        staffFullNameLabel.setText(staff.getFirstName() + " " + staff.getSurname());
        staffRoleLabel.setText(staff.getStatus());
        editFirstNameField.setText(staff.getFirstName());
        editLastNameField.setText(staff.getSurname());
        editEmailField.setText(staff.getEmail());
        editPhoneField.setText(staff.getPhoneNumber());
        editDateOfBirthPicker.setValue(staff.getDateOfBirth());

        // Populate and preselect role
        editRoleComboBox.getItems().setAll("Principal", "Teaching Staff", "Accountant", "General Staff");  // Add your roles
        editRoleComboBox.setValue(staff.getStatus());  // or staff.getStatus() if that's the role field

        // Preselect gender
        if ("Male".equalsIgnoreCase(staff.getGender())) {
            editMaleRadio.setSelected(true);
        } else if ("Female".equalsIgnoreCase(staff.getGender())) {
            editFemaleRadio.setSelected(true);
        }

        staffDetailsPanel.setVisible(true);
        staffDetailsPanel.setManaged(true);

        noStaffFoundPanel.setVisible(false);
        noStaffFoundPanel.setManaged(false);
    }

    private void cancelUpdate() {
        staffDetailsPanel.setVisible(false);
        staffDetailsPanel.setManaged(false);
        noStaffFoundPanel.setVisible(false);
        noStaffFoundPanel.setManaged(false);
    }

    private void displayFeeReport(StudentFeesPaymentDisplay studentFeesPaymentDisplay) {
        studentIdDisplayLabel.setText(studentFeesPaymentDisplay.getStudentId());
        studentNameLabel.setText(studentFeesPaymentDisplay.getStudentName());
        studentClassLabel.setText(studentFeesPaymentDisplay.getLevelName());
        studentSemesterLabel.setText(studentFeesPaymentDisplay.getSemesterName());

        // Get Fees Values
        Double totalFees = Double.parseDouble(studentFeesPaymentDisplay.getTotalFeesAmount());
        List<StudentPaymentHistory> studentPaymentHistory = studentFeesPaymentDisplay.getStudentPaymentHistory();
        Double totalFeesPaid = calAmountPaid(studentPaymentHistory);
        double outStandingBalance = totalFees -  totalFeesPaid;

        studentTotalFeesLabel.setText(totalFees.toString());
        studentAmountPaidLabel.setText(totalFeesPaid.toString());
        studentBalanceLabel.setText(Double.toString(outStandingBalance));

        Double percent = collectionPercentageCalc(totalFees, totalFeesPaid);
        studentPaymentPercentageLabel.setText(percent.toString() + "%");
        studentPaymentProgressBar.setProgress(percent/100);

        //Populate the payment history table
        paymentHistoryTable.getItems().setAll(studentPaymentHistory);
        feeReportPanel.setVisible(true);
        feeReportPanel.setManaged(true);
        noStudentFoundPanel.setVisible(false);
        noStudentFoundPanel.setManaged(false);
    }

    //Calculate total fees paid
    private Double calAmountPaid(List<StudentPaymentHistory> studentPaymentHistory) {
        double totalFeesPaid = 0.0;
        for (StudentPaymentHistory history : studentPaymentHistory) {
            double paidFee = Double.parseDouble(history.getAmountPaid());
            totalFeesPaid += paidFee;
        }
        return totalFeesPaid;
    }

    //Displays student's personal info
    public void displayStudentPersonalInfo(UpdateStudentPersonalData updateStudentPersonalData) {
        studentFullNameLabel.setText(updateStudentPersonalData.getFirstName());
        studentIdLabelDisplay.setText(updateStudentPersonalData.getStudentId());
        studentClassLabelDisplay.setText(updateStudentPersonalData.getGradeName());
        studentGenderLabel.setText(updateStudentPersonalData.getGender());
        studentDOBLabel.setText(updateStudentPersonalData.getDateOfBirth());
        studentGuardianNameLabel.setText(updateStudentPersonalData.getParentName());
        guardianPhoneLabel.setText(updateStudentPersonalData.getParentPhoneNumber());
        statusField.setText(updateStudentPersonalData.getStatus());

        //Display info Panel
        studentDetailsPanel.setVisible(true);
        studentDetailsPanel.setManaged(true);
        noStudentFoundPanelStudent.setVisible(false);
        noStudentFoundPanelStudent.setManaged(false);
    }

    public void displayStudentResultsPanel(StudentResults studentResults) {
        //Setting panel data
        resultsStudentIdLabel.setText(studentResults.getStudentId());
        resultsStudentNameLabel.setText(studentResults.getStudentName());
        resultsClassLabel.setText(studentResults.getGrade());
        resultsSemesterLabel.setText(studentResults.getSemesterName());
        totalScoreLabel.setText(studentResults.getTotalScore().toString());
        averageScoreLabel.setText(studentResults.getAverageScore().toString());
        positionLabel.setText(studentResults.getPosition());
        subjectResultsTable.getItems().setAll(studentResults.getStudentSubjectResults());

        //Display panel
        resultsDisplayPanel.setVisible(true);
        resultsDisplayPanel.setManaged(true);
        noResultsFoundPanel.setVisible(false);
        noResultsFoundPanel.setManaged(false);

    }

}