package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.DTO.Academics.GradeCaching;
import com.bbquantum.eazymanage.DTO.Academics.SemesterCaching;
import com.bbquantum.eazymanage.DTO.Finance.RecentPaymentTable;
import com.bbquantum.eazymanage.DTO.Finance.StudentFeesPaymentDisplay;
import com.bbquantum.eazymanage.DTO.Finance.StudentPaymentHistory;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class FinanceController implements Initializable {
    //CONTROLLER OWN VARIABLES
    private UtilityClass utilityClass;
    public String accountantId;
    public String institutionName;
    public String accountantName;

    //Navigation Side
    @FXML private Button dashboardBtn;
    @FXML private Button addFeesBtn;
    @FXML private Button recordPaymentBtn;
    @FXML private Button searchReportBtn;
    @FXML private Label accountantNameLabel;
    @FXML private Label institutionNameLabel;
    @FXML private Button logoutButton;

    //MAIN DASHBOARD VIEW
    @FXML private StackPane contentArea;
    @FXML private ScrollPane dashboardView;
    @FXML private Label dateTimeLabel;
    @FXML private Label totalFeesLabel;
    @FXML private Label amountPaidLabel;
    @FXML private Label percentageLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Label outstandingLabel;
    @FXML private TableView<RecentPaymentTable> recentPaymentsTable;
    @FXML private TableColumn<RecentPaymentTable, String> paymentDateCol;
    @FXML private TableColumn<RecentPaymentTable, String> studentIdCol;
    @FXML private TableColumn<RecentPaymentTable, String> studentNameCol;
    @FXML private TableColumn<RecentPaymentTable, String> amountCol;
    @FXML private TableColumn<RecentPaymentTable, String> payerCol;
    @FXML private TableColumn<RecentPaymentTable, String> gradeCol;

    //ADD SEMESTER FEES
    @FXML private ScrollPane addSemesterFees;
    @FXML private TextField feesAmountField;
    @FXML private ComboBox<SemesterCaching> feesAddSemesterComboBox;
    @FXML private ComboBox<GradeCaching> feesAddClassComboBox;

    //RECORD PAYMENT VIEW
    @FXML private ScrollPane recordNewPayment;
    @FXML private TextField studentIdField;
    @FXML private ComboBox<SemesterCaching> recordPaymentSemesterComboBox;
    @FXML private ComboBox<GradeCaching> recordPaymentClassComboBox;
    @FXML private TextField amountPaidField;
    @FXML private TextField payerNameField;
    @FXML private TextField payerPhoneField;
    @FXML private DatePicker paymentDatePicker;

    //SEARCH STUDENT REPORT VIEW
    @FXML private Button searchFeesReportBtn;
    @FXML private ScrollPane searchStudentReportView;
    @FXML private TextField reportSearchStudentIdField;
    @FXML private ComboBox<SemesterCaching> reportSearchSemesterComboBox;
    @FXML private ComboBox<GradeCaching> reportSearchClassComboBox;

    //REPORT SEARCH PANEL
    @FXML private VBox reportResponsePanel;
    @FXML private Label reportResponseStudentNameLabel;
    @FXML private Label reportResponseStudentIdLabel;
    @FXML private Label reportResponseClassLabel;
    @FXML private Label reportResponseSemesterLabel;
    @FXML private Label reportResponseTotalFeeLabel;
    @FXML private Label reportResponseAmountPaidLabel;
    @FXML private Label reportResponseBalanceLabel;
    @FXML private Label reportResponsePercentageLabel;
    @FXML private ProgressBar reportResponseProgressBar;
    @FXML private TableView<StudentPaymentHistory> paymentHistoryTable;
    @FXML private TableColumn<StudentPaymentHistory, String> dateColumn;
    @FXML private TableColumn<StudentPaymentHistory, String> amountColumn;
    @FXML private TableColumn<StudentPaymentHistory, String> feesBalanceColumn;
    @FXML private TableColumn<StudentPaymentHistory, String> payerNameColumn;
    @FXML private TableColumn<StudentPaymentHistory, String> payerPhoneColumn;
    @FXML private VBox noResponsePanel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //INITIALIZE RECENT PAYMENT TABLE
        paymentDateCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPaymentDate()));

        studentIdCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentId()));

        studentNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentNameCol()));

        amountCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAmountCol()));

        payerCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPayerCol()));

        gradeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getLevelCol()));

        //INITIALIZE PAYMENT HISTORY TABLE
        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDateOfPayment()));

        amountColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAmountPaid()));

        feesBalanceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFeesBalance()));

        payerNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPersonWhoPaid()));

        payerPhoneColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getContactInfo()));

    }

    @FXML
    public void showDashboard() {
        updateDateTime();
        loadDashboardData();
        setActiveButton(dashboardBtn);
        viewToShow(dashboardView);
    }

    @FXML
    public void showAddFees() {
        setActiveButton(addFeesBtn);
        viewToShow(addSemesterFees);

        List<SemesterCaching> semesterCachingList = utilityClass.semesterCachingManager.getCachedSemesters();
        if (semesterCachingList != null) {
            feesAddSemesterComboBox.getItems().setAll(semesterCachingList);
        }

        List<GradeCaching> gradeCaching = utilityClass.gradeCacheManager.getGradeCache();
        if (gradeCaching != null) {
            feesAddClassComboBox.getItems().setAll(gradeCaching);
        }
    }

    @FXML
    public void showRecordPayment() {
        List<SemesterCaching> semesterCachingList = utilityClass.semesterCachingManager.getCachedSemesters();
        if (semesterCachingList == null || semesterCachingList.isEmpty()) {  // 👈 Correct order
            utilityClass.showError("Semesters not added to system");
            return;
        }
        recordPaymentSemesterComboBox.getItems().setAll(semesterCachingList);

        List<GradeCaching> gradeCaching = utilityClass.gradeCacheManager.getGradeCache();
        if (gradeCaching == null || gradeCaching.isEmpty()) {  // 👈 Add null check
            utilityClass.showError("Grades not added to system");
            return;
        }
        recordPaymentClassComboBox.getItems().setAll(gradeCaching);

        setActiveButton(recordPaymentBtn);
        viewToShow(recordNewPayment);
    }

    @FXML
    public void showSearchReport() {
        List<SemesterCaching> semesterCachingList = utilityClass.semesterCachingManager.getCachedSemesters();
        if (semesterCachingList == null || semesterCachingList.isEmpty()) {  // 👈 Correct order
            utilityClass.showError("Semesters not added to system");
            return;
        }
        reportSearchSemesterComboBox.getItems().setAll(semesterCachingList);

        List<GradeCaching> gradeCaching = utilityClass.gradeCacheManager.getGradeCache();
        if (gradeCaching == null || gradeCaching.isEmpty()) {  // 👈 Add null check
            utilityClass.showError("Grades not added to system");
            return;
        }
        reportSearchClassComboBox.getItems().setAll(gradeCaching);

        setActiveButton(searchReportBtn);
        viewToShow(searchStudentReportView);
    }

    @FXML
    public void handleLogout() throws IOException {
        // Close the login window
        Stage financeDashboard = (Stage) logoutButton.getScene().getWindow();

        // Load the appropriate dashboard
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/Authentication/login-view.fxml"));
        Scene scene = new Scene(loader.load(), 400, 680);

        // Create new stage for dashboard
        Stage loginStage = new Stage();
        loginStage.setTitle("EazyManage - Login");
        loginStage.setScene(scene);
        loginStage.setResizable(false);

        // Close login and show dashboard
        financeDashboard.close();
        loginStage.show();
    }

    @FXML
    public void refreshDashboard() {
        utilityClass.showError("Feature not yet implemented");
    }

    @FXML
    public void handleSaveAndClear() {
        SemesterCaching selectedItem = feesAddSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            utilityClass.showError("Please select a semester from the semester list");
            return;
        }
        String semesterId = selectedItem.getSemesterId();

        GradeCaching selectedItemGrade = feesAddClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedItemGrade == null) {
            utilityClass.showError("Please select a grade from the grade list");
            return;
        }
        String gradeId = selectedItemGrade.getLevelId();

        String feesAmount = feesAmountField.getText();
        if (feesAmount == null || feesAmount.isBlank()) {  // 👈 Also check isBlank()
            utilityClass.showError("Please enter a fees amount");
            return;
        }

        utilityClass.addNewFees(gradeId, semesterId, feesAmount);
        feesAmountField.clear();
    }

    @FXML
    public void handleSaveAndClose() {
        utilityClass.showError("Feature not yet implemented");
    }

    @FXML
    public void handleSaveAndClearPayment() {
        // Validate student ID
        String studentId = studentIdField.getText();
        if (studentId == null || studentId.isBlank()) {
            utilityClass.showError("Please enter student ID");
            return;  // 👈 Stop here!
        }

        // Validate semester
        SemesterCaching selectedSemester = recordPaymentSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            utilityClass.showError("Please select a semester from the semester list");
            return;
        }
        String semesterId = selectedSemester.getSemesterId();

        // Validate grade
        GradeCaching selectedItemGrade = recordPaymentClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedItemGrade == null) {
            utilityClass.showError("Please select a grade from the grade list");
            return;
        }
        String gradeId = selectedItemGrade.getLevelId();

        // Validate amount paid
        String amountPaidText = amountPaidField.getText();
        if (amountPaidText == null || amountPaidText.isBlank()) {
            utilityClass.showError("Please enter amount paid");
            return;
        }

        // Convert to Double
        Double amountPaid;
        try {
            amountPaid = Double.parseDouble(amountPaidText);
        } catch (NumberFormatException e) {
            utilityClass.showError("Please enter a valid amount");
            return;
        }

        // Validate payer name
        String payerName = payerNameField.getText();
        if (payerName == null || payerName.isBlank()) {
            utilityClass.showError("Please enter payer name");
            return;
        }

        // Validate payer phone
        String payerPhone = payerPhoneField.getText();
        if (payerPhone == null || payerPhone.isBlank()) {
            utilityClass.showError("Please enter payer phone");
            return;
        }

        System.out.println("STudent: " + studentId);
        System.out.println("gradeId: " + gradeId);
        System.out.println("semesterId: " + semesterId);
        System.out.println("amountPaid: " + amountPaid);
        System.out.println("payerName: " + payerName);
        System.out.println("payerPhone: " + payerPhone);

        // Call API
        utilityClass.addNewFeesPayment(studentId, semesterId, gradeId, String.valueOf(amountPaid), payerName, payerPhone);

        // Clear form after successful save
        clearPaymentForm();
    }

    private void clearPaymentForm() {
        studentIdField.clear();
        amountPaidField.clear();
        payerNameField.clear();
        payerPhoneField.clear();
        recordPaymentSemesterComboBox.getSelectionModel().clearSelection();
        recordPaymentClassComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleSaveAndClosePayment() {
        utilityClass.showError("Feature not yet implemented");
    }

    @FXML
    public void handleSearch() {
        //Get selected semesterId
        SemesterCaching selectedItem = reportSearchSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            utilityClass.showError("Please select a semester from the semester list");
            return;
        }
        String semesterId = selectedItem.getSemesterId();

        //Get selected gradeId
        GradeCaching selectedItemGrade = reportSearchClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedItemGrade == null) {
            utilityClass.showError("Please select a grade from the grade list");
            return;
        }
        String gradeId = selectedItemGrade.getLevelId();

        String studentId = reportSearchStudentIdField.getText();
        if (studentId == null) {
            utilityClass.showError("Please enter a fees balance");
            return;
        }
        utilityClass.processingButton(searchFeesReportBtn);
        utilityClass.searchFeesReport(studentId, semesterId, gradeId,
                () -> {
                    // On success
                    Platform.runLater(() -> {
                        displayFeesReport(utilityClass.report);
                        utilityClass.notProcessingButton(searchFeesReportBtn, "🔍 Search Report");
                    });
                },
                () -> {
                    // On failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(searchFeesReportBtn, "🔍 Search Report");
                        utilityClass.showError("Could not fetch data, check student Id and try again");
                    });
                });
    }

    @FXML
    public void handleClear() {
        reportResponsePanel.setVisible(false);
        reportResponsePanel.setManaged(false);
    }

    @FXML
    public void handlePrint() {
        utilityClass.showError("Feature not yet implemented");
    }

    @FXML
    public void handleEmail() {
        utilityClass.showError("Feature not yet implemented");
    }

    @FXML
    public void handleExport() {
        utilityClass.showError("Feature not yet implemented");
    }

    @FXML
    public void initializeAfterLogin() {
        utilityClass = new UtilityClass(this);
    }

    public void setStaffId(String accountantId) {
        this.accountantId = accountantId;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public void setStaffName(String accountantName) {
        this.accountantName = accountantName;
    }

    private void displayFeesReport(StudentFeesPaymentDisplay studentFeesPaymentDisplay) {
        reportResponseStudentIdLabel.setText(studentFeesPaymentDisplay.getStudentId());
        reportResponseStudentNameLabel.setText(studentFeesPaymentDisplay.getStudentName());
        reportResponseClassLabel.setText(studentFeesPaymentDisplay.getLevelName());
        reportResponseSemesterLabel.setText(studentFeesPaymentDisplay.getSemesterName());

        // Get Fees Values
        Double totalFees = Double.parseDouble(studentFeesPaymentDisplay.getTotalFeesAmount());
        List<StudentPaymentHistory> studentPaymentHistory = studentFeesPaymentDisplay.getStudentPaymentHistory();
        Double totalFeesPaid = calAmountPaid(studentPaymentHistory);
        double outStandingBalance = totalFees -  totalFeesPaid;

        reportResponseTotalFeeLabel.setText(totalFees.toString());
        reportResponseAmountPaidLabel.setText(totalFeesPaid.toString());
        reportResponseBalanceLabel.setText(Double.toString(outStandingBalance));

        Double percent = collectionPercentageCalc(totalFees, totalFeesPaid);
        reportResponsePercentageLabel.setText(percent.toString() + "%");
        reportResponseProgressBar.setProgress(percent/100);

        //Populate the payment history table
        paymentHistoryTable.getItems().setAll(studentPaymentHistory);
        reportResponsePanel.setVisible(true);
        reportResponsePanel.setManaged(true);
        noResponsePanel.setVisible(false);
        noResponsePanel.setManaged(false);
    }

    private Double collectionPercentageCalc(Double totalSemesterFeesFromBackend,
                                            Double feesPaidFromBackend) {
        return (feesPaidFromBackend / totalSemesterFeesFromBackend) * 100;
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

    private void viewToShow(ScrollPane scrollPane) {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        addSemesterFees.setVisible(false);
        addSemesterFees.setManaged(false);
        recordNewPayment.setVisible(false);
        recordNewPayment.setManaged(false);
        searchStudentReportView.setVisible(false);
        searchStudentReportView.setManaged(false);

        scrollPane.setVisible(true);
        scrollPane.setManaged(true);
    }

    private void setActiveButton(Button activeButton) {
        String ACTIVE_BUTTON = "-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-padding: 15 20 15 20; -fx-background-radius: 8; -fx-cursor: hand;";
        String INACTIVE_BUTTON = "-fx-background-color: transparent; -fx-text-fill: #ecf0f1; -fx-font-size: 14px; -fx-padding: 15 20 15 20;" +
                " -fx-background-radius: 8; -fx-cursor: hand; -fx-border-color: transparent;";
        //Reset all Buttons to inactive style
        dashboardBtn.setStyle(INACTIVE_BUTTON);
        addFeesBtn.setStyle(INACTIVE_BUTTON);
        recordPaymentBtn.setStyle(INACTIVE_BUTTON);
        searchReportBtn.setStyle(INACTIVE_BUTTON);

        activeButton.setStyle(ACTIVE_BUTTON);
    }


    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy | HH:mm:ss");
        dateTimeLabel.setText(now.format(formatter));
    }

    private void loadDashboardData() {
        institutionNameLabel.setText(institutionName != null ? institutionName : "");
        accountantNameLabel.setText(accountantName != null ? accountantName : "");

        Double totalFees = utilityClass.totalSemesterFeesFromBackend != null
                ? utilityClass.totalSemesterFeesFromBackend : 0.0;
        Double paidFees = utilityClass.feesPaidFromBackend != null
                ? utilityClass.feesPaidFromBackend : 0.0;
        Double outstanding = totalFees - paidFees;

        totalFeesLabel.setText(String.format("%.2f", totalFees));
        amountPaidLabel.setText(String.format("%.2f", paidFees));
        outstandingLabel.setText(String.format("%.2f", outstanding));

        double progress = utilityClass.feesProgressBarFromBackend != null
                ? utilityClass.feesProgressBarFromBackend : 0.0;
        progressBar.setProgress(progress);
    }
}
