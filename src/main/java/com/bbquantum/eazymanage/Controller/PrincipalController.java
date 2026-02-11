package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.Constants.StaffRoles;
import com.bbquantum.eazymanage.Constants.StaffStatus;
import com.bbquantum.eazymanage.Constants.StudentStatus;
import com.bbquantum.eazymanage.DTO.Academics.*;
import com.bbquantum.eazymanage.DTO.Finance.ClassFeesSummary;
import com.bbquantum.eazymanage.DTO.Finance.FeesSummaryTable;
import com.bbquantum.eazymanage.DTO.Finance.StudentFeesPaymentDisplay;
import com.bbquantum.eazymanage.DTO.Finance.PaymentHistory;
import com.bbquantum.eazymanage.DTO.Institution.*;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class PrincipalController implements Initializable {

    private UtilityClass utilityClass;

    //Store staff Data from backend
    String institutionName;
    String principalName;

    //Store logged in staff Id from login
    public String currentStaffId;

    //Store staff Data for Update
    public FindStaffResponse findStaffResponse;
    //Get refreshed staff list
    public List<ViewStaffList> refreshedStaffViewList;
    //Store fees filter
    public List<FeesSummaryTable> feesSummaryTable;

    public PasswordRecoveryDTO passwordRecoveryDTO;

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
    @FXML private Label totalStaffCountLabel;
    @FXML private TextField searchStaffIdField;
    @FXML private VBox newPasswordBox;
    @FXML private TextField newPasswordField;
    @FXML public VBox staffDetailsPanel;
    @FXML private Label staffFullNameLabel;
    @FXML private Label staffIdLabel;
    @FXML private TextField editFirstNameField;
    @FXML private TextField editLastNameField;
    @FXML private TextField editEmailField;
    @FXML private TextField editPhoneField;
    @FXML private DatePicker editDateOfBirthPicker;
    @FXML private ComboBox<String> editGenderComboBox;
    @FXML private ComboBox<StaffRoles> editRoleComboBox;
    @FXML private ComboBox<StaffStatus> editStatusComboBox;
    @FXML private Button searchStaffBtn;
    @FXML private Button passwordResetBtn;
    @FXML private Button updateStaffInfoBtn;
    @FXML
    public VBox noStaffFoundPanel;
    @FXML
    public TableView<ViewStaffList> allStaffTable;
    @FXML private TableColumn<ViewStaffList, String> staffIdColumn;
    @FXML private TableColumn<ViewStaffList, String> staffNameColumn;
    @FXML private TableColumn<ViewStaffList, String> staffRoleColumn;
    @FXML private TableColumn<ViewStaffList, String> staffGradeColumn;

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
    @FXML private TableView<StudentSubjectResults> subjectResultsTable;
    @FXML private TableColumn<StudentSubjectResults, String> subjectNameColumn;
    @FXML private TableColumn<StudentSubjectResults, String> subjectScoreColumn;
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

    //ADD SEMESTER PAGE
    @FXML private ScrollPane addSemesterView;

    @FXML private Button addSemesterToggleBtn;
    @FXML private Button updateSemesterToggleBtn;

    @FXML private VBox addSemesterCard;
    @FXML private TextField semesterNameField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField academicYearField;
    @FXML private Button saveSemesterBtn;

    @FXML private VBox updateSemesterInfoCard;
    @FXML private VBox semesterSearchPanel;
    @FXML private ComboBox<SemesterCaching> updateSemesterInfoComboBox;
    @FXML private Button findSemesterBtn;
    @FXML private VBox semesterInfoPanel;
    @FXML private TextField updateSemesterNameField;
    @FXML private DatePicker updateStartDatePicker;
    @FXML private DatePicker updateEndDatePicker;
    @FXML private TextField updateAcademicYearField;
    @FXML private Button updateSemesterBtn;

    //ADD GRADES CRITERIA PAGE
    @FXML private ScrollPane addGradesView;
    @FXML private TextField gradeCriteriaField;
    @FXML private TextField lowerRangeField;
    @FXML private TextField upperRangeField;
    @FXML private TextField gradeRemarksField;
    @FXML private Button saveGradeBtn;

    //ADD CLASS PAGE
    @FXML private ScrollPane addNewClassView;

    @FXML private Button addClassToggleBtn;
    @FXML private Button updateClassInfoToggleBtn;

    @FXML private StackPane classCardContainer;

    @FXML private VBox addClassCard;
    @FXML private TextField addClassNameField;
    @FXML private ComboBox<StaffCaching> addClassStaffComboBox;
    @FXML private Button saveClassBtn;

    @FXML private VBox updateClassInfoCard;
    @FXML private VBox updateClassSearchArea;
    @FXML private TextField updateClassSearchField;
    @FXML private Button searchClassBtn;

    @FXML private VBox updateClassInfoDisplayArea;
    @FXML private TextField updateClassInfoNameField;
    @FXML private ComboBox<StaffCaching> updateClassInfoStaffComboBox;
    @FXML private Button updateClassInfoBtn;

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
    public void refreshDashboard() {
        showLoading();
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
        loadStaffDashboardData();
        switchView(staffDashboardView, staffDashboardBtn);
    }

    @FXML
    private void showFinanceDashboard() {
        loadFinanceDashboardData();

        List<GradeCaching> cachedGrades = utilityClass.gradeCacheManager.getGradesCache();
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

        switchView(financeDashboardView, financeDashboardBtn);
    }

    @FXML
    private void showStudentDashboard() {

        loadStudentDashboardData();

        List<GradeCaching> cachedGrades = utilityClass.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            resultsClassComboBox.getItems().setAll(cachedGrades);
            printStudentClassComboBox.getItems().setAll(cachedGrades);
        }

        List<SemesterCaching> semesterCaching = utilityClass.semesterCachingManager.getCachedSemesters();
        if (semesterCaching != null && !semesterCaching.isEmpty()) {
            resultsSemesterComboBox.getItems().setAll(semesterCaching);
        }

        switchView(studentDashboardView, studentDashboardBtn);
    }

    @FXML
    public void openAddClassPage() {
        showAddClassCard();
        switchView(addNewClassView, staffDashboardBtn);
    }

    public void openClassResultsView() {
        List<GradeCaching> cachedGrades = utilityClass.gradeCacheManager.getGradesCache();
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
            utilityClass.notProcessingButton(passwordResetBtn, "RESET PASSWORD");
            return;
        }
        utilityClass.recoverPassword(staffId, newPassword,
                () -> {
            //On Success
                    Platform.runLater( () -> {
                        utilityClass.notProcessingButton(passwordResetBtn, "RESET PASSWORD");
                        utilityClass.showSuccess(notification, utilityClass.successMessageContainer);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater( () -> {
                        utilityClass.notProcessingButton(passwordResetBtn, "RESET PASSWORD");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
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
        staffDetailsPanel.setVisible(false);
        staffDetailsPanel.setManaged(false);
        searchStaffIdField.clear();
    }

    @FXML
    public void handleUpdateStaff() {
        if (!validateStaffUpdateForm()) {
            return;
        }

        utilityClass.processingButton(updateStaffInfoBtn);
        FindStaffResponse updateInfo = updateStaffDTO();
        utilityClass.updateStaffInfo(updateInfo,
                () -> {
            //On Success
                    Platform.runLater( () -> {
                        utilityClass.notProcessingButton(updateStaffInfoBtn, "SAVE CHANGES");
                        utilityClass.showSuccess(notification, utilityClass.successMessageContainer);
                        staffDetailsPanel.setVisible(false);
                        staffDetailsPanel.setManaged(false);
                        searchStaffIdField.clear();
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater( () -> {
                        utilityClass.notProcessingButton(updateStaffInfoBtn, "SAVE CHANGES");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater( () -> {
                        utilityClass.notProcessingButton(updateStaffInfoBtn, "SAVE CHANGES");
                        utilityClass.showError(notification, "Network Error! Check your internet and try again");
                    });
                });

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
        if (selectedGrade == null) {
            utilityClass.showError(notification, "Please select Grade");
            return;
        }
        String gradeId = selectedGrade.getLevelId();

        //Get selected semester from combo box
        SemesterCaching selectedSemester = semesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            utilityClass.showError(notification, "Please select Semester");
            return;
        }
        String semesterId = selectedSemester.getSemesterId();
        utilityClass.processingButton(searchFeesHistoryBtn);

        utilityClass.fetchStudentPaymentRecords(studentId, semesterId, gradeId,
                () -> {
                    // On success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(searchFeesHistoryBtn, "FETCH FEES RECORDS");
                        utilityClass.showSuccess(notification, utilityClass.successMessageContainer);
                        displayFeeReport(utilityClass.paymentRecords);
                    });
                },
                () -> {
                    // On failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(searchFeesHistoryBtn, "FETCH FEES RECORDS");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                }, () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(searchFeesHistoryBtn, "FETCH FEES RECORDS");
                        utilityClass.showError(notification,"Network error! Check your internet and try again");
                    });
                });
    }

    @FXML
    public void showFullyPaid() {
        String ownUrl = "/fully-paid-students/" + currentStaffId;
        String filter = "FULLY PAID STUDENTS";

        utilityClass.processingButton(fullyPaidBtn);
        utilityClass.getFeesFilterData(ownUrl,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(fullyPaidBtn, "FULLY PAID");
                        utilityClass.showSuccess(notification, utilityClass.successMessageContainer);
                        String size = String.valueOf(feesSummaryTable.size());
                        displayFilteredFeesTable(feesSummaryTable, filter, size);
                    });
                },
                () -> {
            //On failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(fullyPaidBtn, "FULLY PAID");
                        filteredStudentsPanel.setVisible(false);
                        filteredStudentsPanel.setManaged(false);
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(fullyPaidBtn, "FULLY PAID");
                        utilityClass.showError(notification, "Network Error! Check your internet and try again");
                    });
                });
    }

    @FXML
    public void showPartialPayment() {
        String ownUrl = "/partially-paid-students/" + currentStaffId;
        String filter = "PARTIALLY PAID STUDENTS";

        utilityClass.processingButton(partiallyPaidBtn);
        utilityClass.getFeesFilterData(ownUrl,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(partiallyPaidBtn, "PARTIAL PAYMENT");
                        utilityClass.showSuccess(notification, utilityClass.successMessageContainer);
                        String size = String.valueOf(feesSummaryTable.size());
                        displayFilteredFeesTable(feesSummaryTable, filter, size);
                    });
                },
                () -> {
                    //On failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(partiallyPaidBtn, "PARTIAL PAYMENT");
                        filteredStudentsPanel.setVisible(false);
                        filteredStudentsPanel.setManaged(false);
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(fullyPaidBtn, "PARTIAL PAYMENT");
                        utilityClass.showError(notification, "Network Error! Check your internet and try again");
                    });
                });
    }

    @FXML
    public void showNotPaid() {
        String ownUrl = "/not-paid-students/" + currentStaffId;
        String filter = "NOT PAID STUDENTS";

        utilityClass.processingButton(notPaidBtn);
        utilityClass.getFeesFilterData(ownUrl,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(notPaidBtn, "NOT PAID");
                        utilityClass.showSuccess(notification, utilityClass.successMessageContainer);
                        String size = String.valueOf(feesSummaryTable.size());
                        displayFilteredFeesTable(feesSummaryTable, filter, size);
                    });
                },
                () -> {
                    //On failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(notPaidBtn, "NOT PAID");
                        filteredStudentsPanel.setVisible(false);
                        filteredStudentsPanel.setManaged(false);
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(notPaidBtn, "NOT PAID");
                        utilityClass.showError(notification, "Network Error! Check your internet and try again");
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
    public void handleSearchResults() {
        String studentId = resultsSearchIdField.getText().trim();
        if (studentId.isEmpty()) {
            utilityClass.showError(notification, "Please enter student id");
            return;
        }
        //Get selected grade/class
        GradeCaching selectedGrade = resultsClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            utilityClass.showError(notification, "No grade selected");
            return;
        }
        String classId = selectedGrade.getLevelId();

        SemesterCaching selectedSemester = resultsSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            utilityClass.showError(notification, "No semester selected");
            return;
        }
        String semesterId = selectedSemester.getSemesterId();
        utilityClass.processingButton(getResultsBtn);

        utilityClass.SearchStudentResults(studentId, semesterId, classId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(getResultsBtn, "FETCH RESULT");
                        utilityClass.showSuccess(notification, utilityClass.successMessageContainer);
                        displayStudentResultsPanel(utilityClass.studentResult);
                    });
                },
                () -> {
            //On failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(getResultsBtn, "FETCH RESULT");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(getResultsBtn, "FETCH RESULT");
                        utilityClass.showError(notification, "Network Error! Check your internet and try again");
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

        utilityClass.processingButton(searchStaffBtn);
        utilityClass.searchStaffData(staffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(searchStaffBtn, "FIND STAFF");
                        displayStaffDetails(findStaffResponse);
                    });
                },
                () -> {
            //On failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(searchStaffBtn, "FIND STAFF");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(searchStaffBtn, "FIND STAFF");
                        utilityClass.showError(notification, "Network Error! Check your connection and try again");
                    });
                });
    }

    @FXML
    public void initializeAfterLogin() {
        utilityClass = new UtilityClass(this);
        setActiveButton(mainDashboardBtn);
        loadPrincipalInfo();
        showLoading();
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
        showAddSemesterCard();
        switchView(addSemesterView, staffDashboardBtn);
    }

    @FXML
    public void openAddSubjectPage() {
        showAddSubjectCard();
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
                        utilityClass.notProcessingButton(printStudentDataBtn, "PRINT CLASS LIST");
                        utilityClass.showSuccess(notification, utilityClass.successMessageContainer);
                    });
                },
                () -> {
                    // On failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(printStudentDataBtn, "PRINT CLASS LIST");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(printStudentDataBtn, "PRINT CLASS LIST");
                        utilityClass.showError(notification, "Network Error! Check your connection and try again");
                    });
                });
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
                        utilityClass.notProcessingButton(saveGradeBtn, "ADD GRADE CRITERIA");
                        utilityClass.showSuccess(notification, utilityClass.successMessageContainer);
                        gradeCriteriaField.clear();
                        upperRangeField.clear();
                        lowerRangeField.clear();
                        gradeRemarksField.clear();
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveGradeBtn, "ADD GRADE CRITERIA");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                }, () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveGradeBtn, "ADD GRADE CRITERIA");
                        utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });

    }

    @FXML
    public void handleAddClass() {
        String className = addClassNameField.getText().trim();
        if (className.isEmpty()) {
            utilityClass.showError(notification, "Class name is required");
            return;
        }

        if (!className.contains("_") || className.contains(" ")) {
            utilityClass.showError(notification, "Class name must contain '_' eg.Grade_1");
            return;
        }

        StaffCaching staffCaching = addClassStaffComboBox.getSelectionModel().getSelectedItem();
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
                        utilityClass.notProcessingButton(saveClassBtn, "ADD CLASS");
                        utilityClass.showSuccess(notification, utilityClass.successMessageContainer);
                    });
                }, () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveClassBtn, "ADD CLASS");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                }, () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveClassBtn, "ADD CLASS");
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
        utilityClass.loadClassResults(gradeId, semesterId,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(loadClassResultsBtn, "FETCH RESULTS");
                        utilityClass.showSuccess(notification, "Results successfully loaded");
                        populateClassResults(utilityClass.viewClassSemesterReports);
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(loadClassResultsBtn, "FETCH RESULTS");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);

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
    public void showAddClassCard() {
        addClassNameField.clear();
        addClassStaffComboBox.getSelectionModel().clearSelection();

        List<StaffCaching> cachedStaffs = utilityClass.staffCacheManager.getCachedStaffList();
        if (cachedStaffs != null && !cachedStaffs.isEmpty()) {
            if (addClassStaffComboBox.getItems().isEmpty()) {
                addClassStaffComboBox.getItems().addAll(cachedStaffs);
            }
        }

        switchPageCards(addClassCard, updateClassInfoCard, addClassToggleBtn, updateClassInfoToggleBtn);
    }

    @FXML
    public void showUpdateClassInfoCard() {
        updateClassInfoDisplayArea.setVisible(false);
        updateClassInfoDisplayArea.setManaged(false);

        updateClassSearchArea.setVisible(true);
        updateClassSearchArea.setManaged(true);

        switchPageCards(updateClassInfoCard, addClassCard, updateClassInfoToggleBtn, addClassToggleBtn);
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
    public void showAddSemesterCard() {
        switchPageCards(addSemesterCard, updateSemesterInfoCard,
                addSemesterToggleBtn, updateSemesterToggleBtn);
    }

    @FXML
    public void showUpdateSemesterCard() {
        List<SemesterCaching> cachedSemesters = utilityClass.semesterCachingManager.getCachedSemesters();
        if (cachedSemesters != null && !cachedSemesters.isEmpty()) {
            if (updateSemesterInfoComboBox.getItems().isEmpty()) {
                updateSemesterInfoComboBox.getItems().addAll(cachedSemesters);
            }
        }
        semesterInfoPanel.setVisible(false);
        semesterInfoPanel.setManaged(false);

        semesterSearchPanel.setVisible(true);
        semesterSearchPanel.setManaged(true);
        switchPageCards(updateSemesterInfoCard, addSemesterCard,
                updateSemesterToggleBtn, addSemesterToggleBtn);
    }

    public void handleSearchSemesterInfo() {
        SemesterCaching selectedItem = updateSemesterInfoComboBox.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            utilityClass.showError(notification, "Please select a semester from the list");
            return;
        }

        utilityClass.processingButton(findSemesterBtn);
        String semesterId = selectedItem.getSemesterId();
        utilityClass.searchSemester(semesterId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(findSemesterBtn, "FETCH INFO");
                        utilityClass.showSuccess(notification, "Semester Info loaded successfully");
                        populateSemesterInfoPanel(utilityClass.semester);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(findSemesterBtn, "FETCH INFO");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(findSemesterBtn, "FETCH INFO");
                        utilityClass.showError(notification, "Network Error! Check your connection and try again");
                    });
                });
    }

    public void handleUpdateSemesterInfo() {
        String semesterName = updateSemesterNameField.getText().trim();
        LocalDate startDate = updateStartDatePicker.getValue();
        LocalDate endDate = updateEndDatePicker.getValue();
        String academicYear = updateAcademicYearField.getText().trim();

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

        utilityClass.semester.setSemesterName(semesterName);
        utilityClass.semester.setSemesterStartDate(String.valueOf(startDate));
        utilityClass.semester.setSemesterEndDate(String.valueOf(endDate));
        utilityClass.semester.setAcademicYear(academicYear);

        utilityClass.processingButton(updateSemesterBtn);
        utilityClass.updateSemesterInfo(utilityClass.semester,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(updateSemesterBtn, "SAVE CHANGES");
                        utilityClass.showSuccess(notification, utilityClass.successMessageContainer);

                        semesterInfoPanel.setVisible(false);
                        semesterInfoPanel.setManaged(false);

                        updateSemesterInfoComboBox.getSelectionModel().clearSelection();
                        updateSemesterInfoComboBox.setPromptText("Select a Semester");

                        semesterSearchPanel.setVisible(true);
                        semesterSearchPanel.setManaged(true);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(updateSemesterBtn, "SAVE CHANGES");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(updateSemesterBtn, "SAVE CHANGES");
                        utilityClass.showError(notification, "Network Error! Check your connection and try again");
                    });
                });
    }

    private void populateSemesterInfoPanel(FindSemester semester) {
        updateSemesterNameField.setText(semester.getSemesterName());
        updateStartDatePicker.setValue(LocalDate.parse(semester.getSemesterStartDate()));
        updateEndDatePicker.setValue(LocalDate.parse(semester.getSemesterEndDate()));
        updateAcademicYearField.setText(semester.getAcademicYear());

        semesterSearchPanel.setVisible(false);
        semesterSearchPanel.setManaged(false);

        semesterInfoPanel.setVisible(true);
        semesterInfoPanel.setManaged(true);
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
    public void handleFindClass() {
        String searchParameter = updateClassSearchField.getText().trim();
        if (searchParameter.isEmpty()) {
            utilityClass.showError(notification, "Please enter class");
            return;
        }

        if (searchParameter.contains(" ") && !searchParameter.startsWith("LV")) {
            utilityClass.showError(notification, "Class name must contain '_'. eg. Grade_1");
            return;
        }

        if (!searchParameter.contains("_") && !searchParameter.endsWith("\\d+(\\.\\d+)?")) {
            searchParameter = searchParameter + "_";
        }

        utilityClass.processingButton(searchClassBtn);
        utilityClass.findClassInfo(searchParameter, currentStaffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        List<StaffCaching> cachedStaffs = utilityClass.staffCacheManager.getCachedStaffList();
                        if (cachedStaffs != null && !cachedStaffs.isEmpty()) {
                            if (updateClassInfoStaffComboBox.getItems().isEmpty()) {
                                updateClassInfoStaffComboBox.getItems().addAll(cachedStaffs);
                            }

                            for (StaffCaching staffCaching : cachedStaffs) {
                                if (staffCaching.getStaffId().equals(utilityClass.findAndUpdateClassInfo.getStaffId())) {
                                    updateClassInfoStaffComboBox.getSelectionModel().select(staffCaching);
                                    break;
                                }
                            }
                        }

                        updateClassInfoNameField.setText(utilityClass.findAndUpdateClassInfo.getLevelName());
                        updateClassInfoDisplayArea.setVisible(true);
                        updateClassInfoDisplayArea.setManaged(true);

                        updateClassSearchArea.setVisible(false);
                        updateClassSearchArea.setManaged(false);
                        utilityClass.notProcessingButton(searchClassBtn, "FIND CLASS");
                        utilityClass.showSuccess(notification, "Class information found");
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(searchClassBtn, "FIND CLASS");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(searchClassBtn, "FIND CLASS");
                        utilityClass.showError(notification, "Network error! Check your connection and try again");
                    });
                });
    }

    @FXML
    public void handleUpdateClassInfo() {
        String newClassName = updateClassInfoNameField.getText().trim();
        if (newClassName.isEmpty()) {
            utilityClass.showError(notification, "Please enter class name");
            return;
        }

        if (!newClassName.contains("_") || newClassName.contains(" ")) {
            utilityClass.showError(notification, "Class name must contain '_' eg.Grade_1");
            return;
        }

        StaffCaching staffCaching = updateClassInfoStaffComboBox.getSelectionModel().getSelectedItem();
        if (staffCaching == null) {
            utilityClass.showError(notification, "Please select a staff");
            return;
        }

        utilityClass.processingButton(updateClassInfoBtn);
        utilityClass.findAndUpdateClassInfo.setLevelName(newClassName);
        utilityClass.findAndUpdateClassInfo.setStaffId(staffCaching.getStaffId());

        utilityClass.updateClassInfo(utilityClass.findAndUpdateClassInfo,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(updateClassInfoBtn, "SAVE CHANGES");
                        updateClassInfoDisplayArea.setVisible(false);
                        updateClassInfoDisplayArea.setManaged(false);

                        updateClassSearchArea.setVisible(true);
                        updateClassSearchArea.setManaged(true);
                        utilityClass.showSuccess(notification, utilityClass.successMessageContainer);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(updateClassInfoBtn, "SAVE CHANGES");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(updateClassInfoBtn, "SAVE CHANGES");
                        utilityClass.showError(notification, "Network Error! Check your connection and try again");
                    });
                });
    }

    private FindStaffResponse updateStaffDTO() {
        FindStaffResponse staff = new FindStaffResponse();
        staff.setStaffId(staffIdLabel.getText());
        staff.setFirstName(editFirstNameField.getText().trim());
        staff.setSurname(editLastNameField.getText().trim());
        staff.setEmail(editEmailField.getText().trim());
        staff.setPhoneNumber(editPhoneField.getText().trim());
        staff.setDateOfBirth(String.valueOf(editDateOfBirthPicker.getValue()));
        staff.setGender(editGenderComboBox.getSelectionModel().getSelectedItem());
        staff.setStaffRole(editRoleComboBox.getSelectionModel().getSelectedItem().toString());
        staff.setStaffStatus(editStatusComboBox.getSelectionModel().getSelectedItem().toString());

        return staff;
    }

    private boolean validateStaffUpdateForm() {
        if (editFirstNameField.getText().isEmpty()) {
            utilityClass.showError(notification, "Please enter first name");
            return false;
        }

        if (editLastNameField.getText().isEmpty()) {
            utilityClass.showError(notification, "Please enter last name");
            return false;
        }

        if (editEmailField.getText().isEmpty() || !editEmailField.getText().endsWith(".com")) {
            utilityClass.showError(notification, "Please enter valid email");
            return false;
        }

        if (editPhoneField.getText().isEmpty() || editPhoneField.getText().length() != 10) {
            utilityClass.showError(notification, "Please enter valid phone number");
            return false;
        }

        if (editDateOfBirthPicker.getValue() == null) {
            utilityClass.showError(notification, "Please select date of birth");
            return false;
        }

        if (editGenderComboBox.getSelectionModel().getSelectedItem() == null) {
            utilityClass.showError(notification, "Please select gender");
            return false;
        }

        if (editStatusComboBox.getSelectionModel().getSelectedItem() == null) {
            utilityClass.showError(notification, "Please select status");
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
        List<ViewStaffList> staffList = utilityClass.staffListCacheManager.getCachedStaffList();
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
        versionLabel.setText(utilityClass.applicationVersion);
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

        List<AbsenteesView> cachedStudents = utilityClass.cacheManager.getCachedAbsenteeStudents();
        if (cachedStudents != null) {
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
        staffIdLabel.setText(staff.getStaffId());
        editFirstNameField.setText(staff.getFirstName());
        editLastNameField.setText(staff.getSurname());
        editEmailField.setText(staff.getEmail());
        editPhoneField.setText(staff.getPhoneNumber());
        editDateOfBirthPicker.setValue(LocalDate.parse(staff.getDateOfBirth()));

        // Populate and preselect role
        editRoleComboBox.getItems().addAll(StaffRoles.values());
        editRoleComboBox.setValue(StaffRoles.valueOf(staff.getStaffRole()));

        editStatusComboBox.getItems().addAll(StaffStatus.values());
        editStatusComboBox.setValue(StaffStatus.valueOf(staff.getStaffStatus()));


        editGenderComboBox.getItems().addAll("Male", "Female");
        editGenderComboBox.setValue(staff.getGender());

        staffDetailsPanel.setVisible(true);
        staffDetailsPanel.setManaged(true);
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

}