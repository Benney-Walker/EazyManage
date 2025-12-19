package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.DTO.*;
import com.bbquantum.eazymanage.DTO.Academics.GradeCaching;
import com.bbquantum.eazymanage.DTO.Academics.SemesterCaching;
import com.bbquantum.eazymanage.DTO.Academics.StudentResults;
import com.bbquantum.eazymanage.DTO.Academics.StudentResultsSubject;
import com.bbquantum.eazymanage.DTO.Finance.ClassFeesSummary;
import com.bbquantum.eazymanage.DTO.Finance.FeesSummaryTable;
import com.bbquantum.eazymanage.DTO.Finance.StudentFeesPaymentDisplay;
import com.bbquantum.eazymanage.DTO.Finance.StudentPaymentHistory;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.net.http.HttpClient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.ResourceBundle;

public class PrincipalController implements Initializable {

    private UtilityClass utilityClass;

    public final HttpClient httpClient = HttpClient.newHttpClient();
    public final Gson gson = new Gson();

    //Store staff Data from backend
    String institutionName;
    String principalName;

    //Store logged in staff Id from login
    public String currentStaffId;

    //Store studentData for Update
    public UpdateStudentPersonalData updateStudentPersonalData;

    //LEFT SIDEBAR - Navigation side
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

    //STAFF DASHBOARD VIEW
    @FXML private ScrollPane staffDashboardView;
    @FXML private Label totalStaffCountLabel;
    @FXML private TextField searchStaffIdField;
    @FXML
    public VBox staffDetailsPanel;
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
    @FXML
    public VBox noStaffFoundPanel;
    @FXML
    public TableView<RecentStaffView> allStaffTable;
    @FXML private TableColumn<RecentStaffView, String> staffIdColumn;
    @FXML private TableColumn<RecentStaffView, String> staffNameColumn;
    @FXML private TableColumn<RecentStaffView, String> staffRoleColumn;
    @FXML private TableColumn<RecentStaffView, String> staffGradeColumn;
    @FXML private TableColumn<RecentStaffView, String> staffSubjectColumn;

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
    @FXML private VBox studentDetailsPanel;
    @FXML private Label studentFullNameLabel;
    @FXML private Label studentIdLabelDisplay;
    @FXML private Label studentClassLabelDisplay;
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

        //Set current date and Time
        updateDateTime();

        setActiveButton(mainDashboardBtn);


    }

    @FXML
    private void showMainDashboard() {
        loadMainDashboardData();
        switchView(mainDashboardView, mainDashboardBtn);
    }

    @FXML
    public void viewAllStudents() {

        utilityClass.showError("Feature not yet implemented");
    }

    @FXML
    public void handleLogout() throws IOException {
        // Close the login window
        Stage principalDashboard = (Stage) logoutButton.getScene().getWindow();

        // Load the appropriate dashboard
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/Authentication/login-view.fxml"));
        Scene scene = new Scene(loader.load(), 400, 680);

        // Create new stage for dashboard
        Stage loginStage = new Stage();
        loginStage.setTitle("EazyManage - Login");
        loginStage.setScene(scene);
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
        utilityClass.showAddClassDialog();
    }

    @FXML
    public void handleRecoverPassword() {
        String staffId = searchStaffIdField.getText().trim();

        if (staffId.isEmpty()) {
            utilityClass.showError("Please enter a staff id!");
            return;
        }

        // Confirm action
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Recover Password");
        confirmAlert.setHeaderText("Recover password for Staff ID: " + staffId);
        confirmAlert.setContentText("This will retrieve the staff member's password. Continue?");

        if (confirmAlert.showAndWait().get() != ButtonType.OK) {
            return;
        }
        utilityClass.recoverPassword(staffId);
    }

    @FXML
    public void handleCancelUpdate() {
        cancelUpdate();
    }

    @FXML
    public void handleUpdateStaff() {
        staffDetailsPanel.setVisible(false);
        staffDetailsPanel.setManaged(false);

        utilityClass.showError("Feature not yet implemented");
    }

    @FXML
    public void handleExportToExcel() {

        utilityClass.showError("Feature not yet implemented");
    }

    @FXML
    public void handleSearchStudentFees() {
        //Extract studentId from textField
        String studentId = studentIdSearchField.getText().trim();

        //Get selected class from combo box
        GradeCaching selectedGrade = classLevelComboBox.getSelectionModel().getSelectedItem();
        String gradeId = null;
        if (selectedGrade != null) {
            gradeId = selectedGrade.getLevelId();
        }

        //Get selected semester from combo box
        SemesterCaching selectedSemester = semesterComboBox.getSelectionModel().getSelectedItem();
        String semesterId = null;
        if (selectedSemester != null) {
            semesterId = selectedSemester.getSemesterId();
        }
        utilityClass.processingButton(searchFeesHistoryBtn);

        utilityClass.searchFeesReport(studentId, semesterId, gradeId,
                () -> {
                    // On success
                    Platform.runLater(() -> {
                        displayFeeReport(utilityClass.report);
                        utilityClass.notProcessingButton(searchFeesHistoryBtn, "🔍 Search");
                    });
                },
                () -> {
                    // On failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(searchFeesHistoryBtn, "🔍 Search");
                        utilityClass.showError("Could not fetch data, check student Id and try again");
                    });
                });
    }

    @FXML
    public void showFullyPaid() {
        String ownUrl = "/fully-paid-students/" + currentStaffId;
        String filter = "FULLY PAID STUDENTS";

        utilityClass.getFeesFilterData(ownUrl, filter);
    }

    @FXML
    public void showPartialPayment() {
        String ownUrl = "/partially-paid-students/" + currentStaffId;
        String filter = "PARTIALLY PAID STUDENTS";

        utilityClass.getFeesFilterData(ownUrl, filter);
    }

    @FXML
    public void showNotPaid() {
        String ownUrl = "/not-paid-students/" + currentStaffId;
        String filter = "NOT PAID STUDENTS";

        utilityClass.getFeesFilterData(ownUrl, filter);
    }

    @FXML
    public void handleClearFilter() {
        filteredStudentsPanel.setVisible(false);
        filteredStudentsPanel.setManaged(false);
    }

    @FXML
    public void handleExportFilteredList() {

        utilityClass.showError("Feature not yet implemented");
    }

    @FXML
    public void handlePrintReport() {

        utilityClass.showError("Feature not yet implemented");
    }

    @FXML
    public void refreshStaffList() {
        utilityClass.loadAllStaffList();
    }

    @FXML
    public void openAddStudentPage(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/Staff/add-student.fxml"));
            Parent root = loader.load();

            AddStudentController controller = loader.getController();
            controller.setStaffId(currentStaffId);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Student");

            // Make it modal
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(((Node) event.getSource()).getScene().getWindow());

            // Set a FIXED SIZE - this enables scrolling!
            Scene scene = new Scene(root, 700, 600);  // width: 700, height: 600
            dialogStage.setScene(scene);

            dialogStage.setResizable(false);
            dialogStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            utilityClass.showError("Could not open add student page. Try again");
        }
    }

    @FXML
    public void handleSearchStudent() {
        String studentId = searchStudentIdField.getText().trim();
        utilityClass.searchStudentData(studentId);
    }

    @FXML
    public void handleEditStudent() {
        if (updateStudentPersonalData == null) {
            new Alert(Alert.AlertType.ERROR, "No student selected for editing").showAndWait();
            return;
        }

        utilityClass.showUpdateStudentDataDialog();
    }

    @FXML
    public void handleSearchResults() {
        String studentId = resultsSearchIdField.getText().trim();
        //Get selected grade/class
        GradeCaching selectedGrade = resultsClassComboBox.getSelectionModel().getSelectedItem();
        String classId = null;
        if (selectedGrade != null) {
            classId = selectedGrade.getLevelId();
        }

        SemesterCaching selectedSemester = resultsSemesterComboBox.getSelectionModel().getSelectedItem();
        String semesterId = null;
        if (selectedSemester != null) {
            semesterId = selectedSemester.getSemesterId();
        }

        utilityClass.SearchStudentResults(studentId, classId, semesterId);
    }

    @FXML
    public void handleSearchStaff() {
        String staffId = searchStaffIdField.getText().trim();

        if (staffId.isEmpty()) {
            utilityClass.showError("Enter Staff ID");
            return;
        }

        utilityClass.searchStaffData(staffId);
    }

    @FXML
    public void handlePrintReportCard() {

        utilityClass.showError("Feature not yet implemented");
    }

    @FXML
    public void initializeAfterLogin() {
        utilityClass = new UtilityClass(this);
        // Load dashboard data
        loadMainDashboardData();

        loadPrincipalInfo();
    }

    @FXML
    public void openAddSemesterPage() {
        // Show dialog to add new semester
        utilityClass.showAddSemesterDialog(currentStaffId);
    }

    @FXML
    public void openAddSubjectPage() {
        // Show dialog to add new subject
        utilityClass.showAddSubjectDialog();
    }

    @FXML
    public void handlePrintStudentData() {
        GradeCaching selectedGrade = printStudentClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            utilityClass.showError("Please select a class first");
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
            utilityClass.showError("Couldn't get fees Progress");
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