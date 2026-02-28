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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class PrincipalController implements Initializable {

    private ApiUtilityClass apiUtilityClass;

    //Store staff Data from backend
    String institutionName;
    String principalName;

    //Store logged in staff Id from login
    public String currentStaffId;

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

    @FXML public TableView<ViewStaffList> allStaffTable;
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
        apiUtilityClass.loadPrincipalData(currentStaffId, notification,
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
    private void showStaffDashboard() {
        loadStaffDashboardData();
        switchView(staffDashboardView, staffDashboardBtn);
    }

    @FXML
    private void showFinanceDashboard() {
        loadFinanceDashboardData();

        List<GradeCaching> cachedGrades = apiUtilityClass.gradeCacheManager.getGradesCache();
        if (cachedGrades != null) {
            Platform.runLater(() -> classLevelComboBox.getItems().setAll(cachedGrades));
        }

        List<SemesterCaching> semesterCaching = apiUtilityClass.semesterCachingManager.getCachedSemesters();
        if (semesterCaching != null) {
            Platform.runLater(() -> semesterComboBox.getItems().setAll(semesterCaching));
        }

        List<ClassFeesSummary> cachedClassfees = apiUtilityClass.feesCacheManager.getClassFeesSummaryCache();
        if (cachedClassfees != null) {
            Platform.runLater(() -> classSummaryTable.getItems().setAll(cachedClassfees));
        }

        switchView(financeDashboardView, financeDashboardBtn);
    }

    @FXML
    private void showStudentDashboard() {

        loadStudentDashboardData();

        List<GradeCaching> cachedGrades = apiUtilityClass.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            resultsClassComboBox.getItems().setAll(cachedGrades);
            printStudentClassComboBox.getItems().setAll(cachedGrades);
        }

        List<SemesterCaching> semesterCaching = apiUtilityClass.semesterCachingManager.getCachedSemesters();
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
        List<GradeCaching> cachedGrades = apiUtilityClass.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (classResultsGradeComboBox.getItems().isEmpty()) {
                classResultsGradeComboBox.getItems().addAll(cachedGrades);
            }
        }

        List<SemesterCaching> semesterCaching = apiUtilityClass.semesterCachingManager.getCachedSemesters();
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
            UtilityMethods.showError(notification, "Please enter a staff id!");
            return;
        }

        if (!newPasswordBox.isVisible()) {
            newPasswordBox.setVisible(true);
            newPasswordBox.setManaged(true);
        }

        String newPassword = newPasswordField.getText();
        if (newPassword.isEmpty()) {
            UtilityMethods.showError(notification, "Please enter a new password!");
            return;
        }

        UtilityMethods.processingButton(passwordResetBtn);

        // Confirm action
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Reset Password");
        confirmAlert.setHeaderText("Reset password for Staff ID: " + staffId);
        confirmAlert.setContentText("New Password: " + newPassword);

        if (confirmAlert.showAndWait().get() != ButtonType.OK) {
            UtilityMethods.notProcessingButton(passwordResetBtn, "RESET PASSWORD");
            return;
        }
        apiUtilityClass.recoverPassword(staffId, newPassword,
                () -> {
            //On Success
                    Platform.runLater( () -> {
                        UtilityMethods.notProcessingButton(passwordResetBtn, "RESET PASSWORD");
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater( () -> {
                        UtilityMethods.notProcessingButton(passwordResetBtn, "RESET PASSWORD");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                }, () -> {
            //On Network Issue
                    Platform.runLater( () -> {
                        UtilityMethods.notProcessingButton(passwordResetBtn, "Recover password");
                        UtilityMethods.showError(notification, "Network error! Check your internet and try again");
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

        UtilityMethods.processingButton(updateStaffInfoBtn);
        FindStaffResponse updateInfo = updateStaffDTO();
        apiUtilityClass.updateStaffInfo(updateInfo,
                () -> {
            //On Success
                    Platform.runLater( () -> {
                        UtilityMethods.notProcessingButton(updateStaffInfoBtn, "SAVE CHANGES");
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                        staffDetailsPanel.setVisible(false);
                        staffDetailsPanel.setManaged(false);
                        searchStaffIdField.clear();
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
    public void handleSearchStudentFees() {
        //Extract studentId from textField
        String studentId = studentIdSearchField.getText().trim();
        if (studentId.isEmpty()) {
            UtilityMethods.showError(notification, "Please enter a student id!");
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

        apiUtilityClass.fetchStudentPaymentRecords(studentId, semesterId, gradeId,
                () -> {
                    // On success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(searchFeesHistoryBtn, "FETCH FEES RECORDS");
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                        displayFeeReport(apiUtilityClass.paymentRecords);
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
        String ownUrl = "/fully-paid-students/" + currentStaffId;
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
        String ownUrl = "/partially-paid-students/" + currentStaffId;
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
        String ownUrl = "/not-paid-students/" + currentStaffId;
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
    public void handleSearchResults() {
        String studentId = resultsSearchIdField.getText().trim();
        if (studentId.isEmpty()) {
            UtilityMethods.showError(notification, "Please enter student id");
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

        apiUtilityClass.SearchStudentResults(studentId, semesterId, classId,
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
    public void handleSearchStaff() {
        String staffId = searchStaffIdField.getText().trim();

        if (staffId.isEmpty()) {
            UtilityMethods.showError(notification, "Enter Staff ID");
            return;
        }

        UtilityMethods.processingButton(searchStaffBtn);
        apiUtilityClass.searchStaffData(staffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(searchStaffBtn, "FIND STAFF");
                        displayStaffDetails(apiUtilityClass.findStaffResponse);
                    });
                },
                () -> {
            //On failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(searchStaffBtn, "FIND STAFF");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(searchStaffBtn, "FIND STAFF");
                        UtilityMethods.showError(notification, "Network Error! Check your connection and try again");
                    });
                });
    }

    @FXML
    public void initializeAfterLogin() {
        apiUtilityClass = new ApiUtilityClass(this);
        setActiveButton(mainDashboardBtn);
        loadPrincipalInfo();
        showLoading();
        apiUtilityClass.loadPrincipalData(currentStaffId, notification,
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
            UtilityMethods.showError(notification, "Please select a class first");
            return;
        }

        UtilityMethods.processingButton(printStudentDataBtn);
        apiUtilityClass.fetchAndSaveStudentList(selectedGrade.getLevelId(), selectedGrade.getLevelName(),
                () -> {
                    // On success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(printStudentDataBtn, "PRINT CLASS LIST");
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                    });
                },
                () -> {
                    // On failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(printStudentDataBtn, "PRINT CLASS LIST");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(printStudentDataBtn, "PRINT CLASS LIST");
                        UtilityMethods.showError(notification, "Network Error! Check your connection and try again");
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
            UtilityMethods.showError(notification, "Semester name is required");
            return;
        }

        if (!semesterName.contains("_") || semesterName.contains(" ")
                || semesterName.contains("-")) {
            UtilityMethods.showError(notification, "Semester name must contain '_' eg. First_Term");
            return;
        }

        if (startDate == null || endDate == null) {
            UtilityMethods.showError(notification, "Start and end dates are required");
            return;
        }
        if (endDate.isBefore(startDate)) {
            UtilityMethods.showError(notification, "End date cannot be before start date");
            return;
        }
        if (academicYear.isEmpty()) {
            UtilityMethods.showError(notification, "Academic year is required");
            return;
        }

        UtilityMethods.processingButton(saveSemesterBtn);
        apiUtilityClass.addNewSemester(semesterName, startDate, endDate, academicYear, currentStaffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(saveSemesterBtn, "Save Semester");
                        clearSemesterForm();
                        UtilityMethods.showSuccess(notification, "Successfully saved semester");
                    });
                }, () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(saveSemesterBtn, "Save Semester");
                        UtilityMethods.showError(notification, "Could not save semester! Try again");
                    });
                }, () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(saveSemesterBtn, "Save Semester");
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
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
            UtilityMethods.showError(notification, "Grade criteria is required");
            return;
        }

        if (lowerRange.isNaN() || upperRange.isNaN()) {
            UtilityMethods.showError(notification, "Please enter grade criteria marks");
            return;
        }

        if (lowerRange > upperRange) {
            UtilityMethods.showError(notification, "Lower range cannot be greater than upper range");
            return;
        }

        if (remarks.isEmpty()) {
            UtilityMethods.showError(notification, "Remarks is required");
            return;
        }

        UtilityMethods.processingButton(saveGradeBtn);
        GradingCriteria gradingCriteria = new GradingCriteria(
                lowerRange, upperRange, gradeCriteria, remarks
        );

        apiUtilityClass.saveGradingCriteria(gradingCriteria, currentStaffId,
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
                        populateClassResults(apiUtilityClass.viewClassSemesterReports);
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

    @FXML
    public void showAddStudentCard() {
        clearForm();

        if (genderComboBox.getItems().isEmpty()) {
            genderComboBox.getItems().addAll("Male", "Female");
        }

        List<GradeCaching> gradeCaching = apiUtilityClass.gradeCacheManager.getGradesCache();
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

        List<StaffCaching> cachedStaffs = apiUtilityClass.staffCacheManager.getCachedStaffList();
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
        List<SemesterCaching> cachedSemesters = apiUtilityClass.semesterCachingManager.getCachedSemesters();
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
            UtilityMethods.showError(notification, "Please select a semester from the list");
            return;
        }

        UtilityMethods.processingButton(findSemesterBtn);
        String semesterId = selectedItem.getSemesterId();
        apiUtilityClass.searchSemester(semesterId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(findSemesterBtn, "FETCH INFO");
                        UtilityMethods.showSuccess(notification, "Semester Info loaded successfully");
                        populateSemesterInfoPanel(apiUtilityClass.semester);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(findSemesterBtn, "FETCH INFO");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(findSemesterBtn, "FETCH INFO");
                        UtilityMethods.showError(notification, "Network Error! Check your connection and try again");
                    });
                });
    }

    public void handleUpdateSemesterInfo() {
        String semesterName = updateSemesterNameField.getText().trim();
        LocalDate startDate = updateStartDatePicker.getValue();
        LocalDate endDate = updateEndDatePicker.getValue();
        String academicYear = updateAcademicYearField.getText().trim();

        if (semesterName.isEmpty()) {
            UtilityMethods.showError(notification, "Semester name is required");
            return;
        }

        if (!semesterName.contains("_") || semesterName.contains(" ")
                || semesterName.contains("-")) {
            UtilityMethods.showError(notification, "Semester name must contain '_' eg. First_Term");
            return;
        }

        if (startDate == null || endDate == null) {
            UtilityMethods.showError(notification, "Start and end dates are required");
            return;
        }
        if (endDate.isBefore(startDate)) {
            UtilityMethods.showError(notification, "End date cannot be before start date");
            return;
        }
        if (academicYear.isEmpty()) {
            UtilityMethods.showError(notification, "Academic year is required");
            return;
        }

        apiUtilityClass.semester.setSemesterName(semesterName);
        apiUtilityClass.semester.setSemesterStartDate(String.valueOf(startDate));
        apiUtilityClass.semester.setSemesterEndDate(String.valueOf(endDate));
        apiUtilityClass.semester.setAcademicYear(academicYear);

        UtilityMethods.processingButton(updateSemesterBtn);
        apiUtilityClass.updateSemesterInfo(apiUtilityClass.semester,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(updateSemesterBtn, "SAVE CHANGES");
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);

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
    public void handleFindStudent() {
        String searchId = studentSearchField.getText();
        if (searchId.isEmpty()) {
            UtilityMethods.showError(notification, "Enter search parameter");
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
                UtilityMethods.showError(notification, "Enter student full name or enter student id");
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

        UtilityMethods.processingButton(findStudentBtn);
        apiUtilityClass.searchStudentData(searchParameter1, searchParameter2, searchParameter3,
                () -> {
                    // On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(findStudentBtn, "Find Student");
                        UtilityMethods.showSuccess(notification, "Student Found");
                        studentInfoUpdatePanel(apiUtilityClass.updateStudentPersonalData);
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

        UtilityMethods.processingButton(updateStudentInfoBtn);
        apiUtilityClass.updateStudentPersonalData(studentPersonalData,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(updateStudentInfoBtn, "UPDATE INFO");
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                        showUpdateInfoCard();
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
        showUpdateInfoCard();
    }

    @FXML
    public void showAddSubjectCard() {
        subjectNameField.clear();
        gradeComboBox.getSelectionModel().clearSelection();

        List<GradeCaching> gradeCaching  = apiUtilityClass.gradeCacheManager.getGradesCache();
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

        List<GradeCaching> gradeCaching  = apiUtilityClass.gradeCacheManager.getGradesCache();
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
                subjectName, gradeId
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
    public void handlePrintSubjectData() {
        GradeCaching gradeCaching = printClassSubjectsComboBox.getSelectionModel().getSelectedItem();
        if (gradeCaching == null) {
            UtilityMethods.showError(notification, "Select Grade");
            return;
        }

        UtilityMethods.processingButton(printSubjectDataBtn);
        String levelId = gradeCaching.getLevelId();
        String levelName = gradeCaching.getLevelName();

        apiUtilityClass.printSubjectList(levelId, levelName,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(printSubjectDataBtn, "Print Subject List");
                        UtilityMethods.showSuccess(notification, "Subject List successfully downloaded");
                    });
                },
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(printSubjectDataBtn, "Print Subject List");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
                    //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(printSubjectDataBtn, "Print Subject List");
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleFindClass() {
        String searchParameter = updateClassSearchField.getText().trim();
        if (searchParameter.isEmpty()) {
            UtilityMethods.showError(notification, "Please enter class");
            return;
        }

        if (searchParameter.contains(" ") && !searchParameter.startsWith("LV")) {
            UtilityMethods.showError(notification, "Class name must contain '_'. eg. Grade_1");
            return;
        }

        if (!searchParameter.contains("_") && !searchParameter.endsWith("\\d+(\\.\\d+)?")) {
            searchParameter = searchParameter + "_";
        }

        UtilityMethods.processingButton(searchClassBtn);
        apiUtilityClass.findClassInfo(searchParameter, currentStaffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        List<StaffCaching> cachedStaffs = apiUtilityClass.staffCacheManager.getCachedStaffList();
                        if (cachedStaffs != null && !cachedStaffs.isEmpty()) {
                            if (updateClassInfoStaffComboBox.getItems().isEmpty()) {
                                updateClassInfoStaffComboBox.getItems().addAll(cachedStaffs);
                            }

                            for (StaffCaching staffCaching : cachedStaffs) {
                                if (staffCaching.getStaffId().equals(apiUtilityClass.findAndUpdateClassInfo.getStaffId())) {
                                    updateClassInfoStaffComboBox.getSelectionModel().select(staffCaching);
                                    break;
                                }
                            }
                        }

                        updateClassInfoNameField.setText(apiUtilityClass.findAndUpdateClassInfo.getLevelName());
                        updateClassInfoDisplayArea.setVisible(true);
                        updateClassInfoDisplayArea.setManaged(true);

                        updateClassSearchArea.setVisible(false);
                        updateClassSearchArea.setManaged(false);
                        UtilityMethods.notProcessingButton(searchClassBtn, "FIND CLASS");
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
        apiUtilityClass.findAndUpdateClassInfo.setLevelName(newClassName);
        apiUtilityClass.findAndUpdateClassInfo.setStaffId(staffCaching.getStaffId());

        apiUtilityClass.updateClassInfo(apiUtilityClass.findAndUpdateClassInfo,
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
        totalStaffCountLabel.setText(String.valueOf(apiUtilityClass.totalStaffFromBackend));
        List<ViewStaffList> staffList = apiUtilityClass.staffListCacheManager.getCachedStaffList();
        allStaffTable.getItems().setAll(staffList);
    }

    //Load finance Dashboard Data on open
    private void loadFinanceDashboardData() {
        totalSemesterFeesLabel.setText(String.valueOf(apiUtilityClass.totalSemesterFeesFromBackend));
        amountPaidLabel.setText(String.valueOf(apiUtilityClass.feesPaidFromBackend));
        collectionProgressBar.setProgress(apiUtilityClass.feesProgressBarFromBackend);
        String collectionPercent = collectionPercentageCalc(apiUtilityClass.totalSemesterFeesFromBackend, apiUtilityClass.feesPaidFromBackend).toString();
        collectionPercentageLabel.setText(collectionPercent + "%");
        String outStanding = calOutstandingBalance(apiUtilityClass.totalSemesterFeesFromBackend, apiUtilityClass.feesPaidFromBackend).toString();
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
        totalStudentsCountLabel.setText(String.valueOf(apiUtilityClass.totalStudentsFromBackend));
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

        updateDateTime();

        //Load total students
        totalStudentsLabel.setText(String.valueOf(apiUtilityClass.totalStudentsFromBackend));

        //Load total Staff
        totalStaffLabel.setText(String.valueOf(apiUtilityClass.totalStaffFromBackend));

        //Load semester Fees
        semesterFeesLabel.setText(String.valueOf(apiUtilityClass.totalSemesterFeesFromBackend));

        //Load fees Paid
        feesPaidLabel.setText(String.valueOf(apiUtilityClass.feesPaidFromBackend));

        //Calculate progress
        try {
            Double totalFeesPaid = apiUtilityClass.feesPaidFromBackend;
            if (totalFeesPaid > 0.0) {
                apiUtilityClass.feesProgressBarFromBackend = totalFeesPaid / apiUtilityClass.totalSemesterFeesFromBackend;
            } else {
                apiUtilityClass.feesProgressBarFromBackend = 0.0;
            }
            feesProgressBar.setProgress(apiUtilityClass.feesProgressBarFromBackend);
        } catch (Exception e) {
            feesProgressBar.setProgress(0.0);
            UtilityMethods.showError(notification, "Couldn't get fees Progress");
        }

        List<AbsenteesView> cachedStudents = apiUtilityClass.cacheManager.getCachedAbsenteeStudents();
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

        apiUtilityClass.enrollNewStudent(firstName, lastName, selectedGradeId, gender,
                dob, homeTown, parentName, parentPhone, currentStaffId, onSuccess, onFailure, onNetworkIssue);
    }

    private void studentInfoUpdatePanel(UpdateStudentPersonalData data) {
        List<GradeCaching> gradeCachings  = apiUtilityClass.gradeCacheManager.getGradesCache();
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