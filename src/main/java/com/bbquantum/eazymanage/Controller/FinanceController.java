package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.DTO.Finance.FetchFeesDetails;
import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.DTO.Institution.SemesterCaching;
import com.bbquantum.eazymanage.DTO.Finance.RecentPaymentTable;
import com.bbquantum.eazymanage.DTO.Finance.StudentFeesPaymentDisplay;
import com.bbquantum.eazymanage.DTO.Finance.StudentPaymentHistory;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
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
import javafx.util.Duration;

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

    //Hold fees Details for update
    public FetchFeesDetails feesDetails;

    //Navigation Side
    @FXML public StackPane notification;
    @FXML public Label versionLabel;
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
    @FXML private Button refreshDashboardBtn;
    @FXML private TableView<RecentPaymentTable> recentPaymentsTable;
    @FXML private TableColumn<RecentPaymentTable, String> paymentDateCol;
    @FXML private TableColumn<RecentPaymentTable, String> studentIdCol;
    @FXML private TableColumn<RecentPaymentTable, String> studentNameCol;
    @FXML private TableColumn<RecentPaymentTable, String> amountCol;
    @FXML private TableColumn<RecentPaymentTable, String> payerCol;
    @FXML private TableColumn<RecentPaymentTable, String> gradeCol;

    //ADD SEMESTER FEES
    @FXML private ScrollPane addSemesterFeesView;

    @FXML private Button addSemesterFeesToggleBtn;
    @FXML private Button updateFeesDetailsToggleBtn;

    @FXML private VBox addSemesterFeesCard;
    @FXML private TextField feesAmountField;
    @FXML private ComboBox<SemesterCaching> feesSemesterComboBox;
    @FXML private ComboBox<GradeCaching> feesClassComboBox;
    @FXML private Button saveFeesBtn;

    @FXML private VBox updateSemesterFeesCard;
    @FXML private VBox feesSearchArea;
    @FXML private ComboBox<SemesterCaching> searchFeesSemesterComboBox;
    @FXML private ComboBox<GradeCaching> searchFeesClassComboBox;
    @FXML private Button fetchFeesDetailsBtn;

    @FXML private VBox feesDetailsArea;
    @FXML private TextField updateFeesAmountField;
    @FXML private ComboBox<SemesterCaching> updateFeesSemesterComboBox;
    @FXML private ComboBox<GradeCaching> updateFeesClassComboBox;
    @FXML private Button updateFeesBtn;

    //RECORD PAYMENT VIEW
    @FXML private ScrollPane recordNewPayment;
    @FXML private TextField studentIdField;
    @FXML private ComboBox<SemesterCaching> recordPaymentSemesterComboBox;
    @FXML private ComboBox<GradeCaching> recordPaymentClassComboBox;
    @FXML private TextField amountPaidField;
    @FXML private TextField payerNameField;
    @FXML private TextField payerPhoneField;
    @FXML private Button saveFeesPaymentBtn;

    //SEARCH STUDENT REPORT VIEW
    @FXML private Button searchFeesReportBtn;
    @FXML private Button printFeesReportBtn;
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

    //SPINNER
    @FXML private VBox loadingOverlay;
    @FXML private ProgressIndicator loadingSpinner;

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
        loadDashboardData();
        updateDateTime();
        switchView(dashboardView, dashboardBtn);
    }

    @FXML
    public void showAddFees() {
        switchView(addSemesterFeesView, addFeesBtn);
        showAddSemesterFeesCard();
    }

    @FXML
    public void showRecordPayment() {
        List<SemesterCaching> semesterCachingList = utilityClass.semesterCachingManager.getCachedSemesters();
        if (semesterCachingList == null || semesterCachingList.isEmpty()) {  // 👈 Correct order
            utilityClass.showError(notification, "Semesters not added to system");
            return;
        }
        recordPaymentSemesterComboBox.getItems().setAll(semesterCachingList);

        List<GradeCaching> gradeCaching = utilityClass.gradeCacheManager.getGradesCache();
        if (gradeCaching == null || gradeCaching.isEmpty()) {  // 👈 Add null check
            utilityClass.showError(notification, "Grades not added to system");
            return;
        }
        recordPaymentClassComboBox.getItems().setAll(gradeCaching);


        switchView(recordNewPayment, recordPaymentBtn);
    }

    @FXML
    public void showSearchReport() {
        List<SemesterCaching> semesterCachingList = utilityClass.semesterCachingManager.getCachedSemesters();
        if (semesterCachingList == null || semesterCachingList.isEmpty()) {  // 👈 Correct order
            utilityClass.showError(notification, "Semesters not added to system");
            return;
        }
        reportSearchSemesterComboBox.getItems().setAll(semesterCachingList);

        List<GradeCaching> gradeCaching = utilityClass.gradeCacheManager.getGradesCache();
        if (gradeCaching == null || gradeCaching.isEmpty()) {  // 👈 Add null check
            utilityClass.showError(notification, "Grades not added to system");
            return;
        }
        reportSearchClassComboBox.getItems().setAll(gradeCaching);

        switchView(searchStudentReportView, searchReportBtn);
    }

    @FXML
    public void handleLogout() throws IOException {
        utilityClass.clearSession();
        // Close the login window
        Stage financeDashboard = (Stage) logoutButton.getScene().getWindow();

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
        financeDashboard.close();
        loginStage.show();
    }

    @FXML
    public void refreshDashboard() {
        showLoading();
        utilityClass.loadFinanceDashboardData(accountantId, notification,
                () -> {
                    //On Success
                    Platform.runLater(() -> {
                        loadDashboardData();

                        updateDateTime();

                        hideLoading();
                    });
                });

    }

    @FXML
    public void backToDashboard() {
        showDashboard();
    }

    @FXML
    public void handleSaveSemesterFees() {
        String feesAmount = feesAmountField.getText();
        if (feesAmount == null || feesAmount.isBlank()) {  // 👈 Also check isBlank()
            utilityClass.showError(notification, "Please enter fees amount");
            return;
        }

        SemesterCaching selectedItem = feesSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            utilityClass.showError(notification, "Please select a semester from the semester list");
            return;
        }
        String semesterId = selectedItem.getSemesterId();

        GradeCaching selectedItemGrade = feesClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedItemGrade == null) {
            utilityClass.showError(notification, "Please select a grade from the grade list");
            return;
        }
        String gradeId = selectedItemGrade.getLevelId();
        utilityClass.processingButton(saveFeesBtn);

        utilityClass.addNewFees(gradeId, semesterId, feesAmount,
                () -> {
            // On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveFeesBtn, "SAVE FEES");
                        utilityClass.showSuccess(notification, utilityClass.successMessageContainer);
                    });
                },
                () -> {
            // On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveFeesBtn, "SAVE FEES");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                }, () -> {
            //On Network Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveFeesBtn, "SAVE FEES");
                        utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    public void handleUpdateSemesterFees() {
        String feesAmount = updateFeesAmountField.getText().trim();
        if (feesAmount.isEmpty()) {
            utilityClass.showError(notification, "Please enter fees amount");
            return;
        }

        SemesterCaching selectedSemester = updateFeesSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            utilityClass.showError(notification, "Please select a semester");
            return;
        }

        GradeCaching selectedGrade = updateFeesClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            utilityClass.showError(notification, "Please select a grade");
            return;
        }

        utilityClass.processingButton(updateFeesBtn);
        feesDetails.setAmount(feesAmount);
        feesDetails.setSemesterId(selectedSemester.getSemesterId());
        feesDetails.setLevelId(selectedGrade.getLevelId());

        utilityClass.updateSemesterFees(feesDetails,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(updateFeesBtn, "SAVE CHANGES");
                        utilityClass.showSuccess(notification, utilityClass.successMessageContainer);
                        feesDetailsArea.setVisible(false);
                        feesDetailsArea.setManaged(false);

                        feesSearchArea.setVisible(true);
                        feesSearchArea.setManaged(true);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(updateFeesBtn, "SAVE CHANGES");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(updateFeesBtn, "SAVE CHANGES");
                        utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleSavePayment() {
        // Validate student ID
        String studentId = studentIdField.getText();
        if (studentId == null || studentId.isBlank()) {
            utilityClass.showError(notification, "Please enter student ID");
            return;
        }

        // Validate semester
        SemesterCaching selectedSemester = recordPaymentSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            utilityClass.showError(notification, "Please select a semester from the semester list");
            return;
        }
        String semesterId = selectedSemester.getSemesterId();

        // Validate grade
        GradeCaching selectedItemGrade = recordPaymentClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedItemGrade == null) {
            utilityClass.showError(notification, "Please select a grade from the grade list");
            return;
        }
        String gradeId = selectedItemGrade.getLevelId();

        // Validate amount paid
        String amountPaidText = amountPaidField.getText();
        if (amountPaidText == null || amountPaidText.isBlank()) {
            utilityClass.showError(notification, "Please enter amount paid");
            return;
        }

        // Convert to Double
        Double amountPaid;
        try {
            amountPaid = Double.parseDouble(amountPaidText);
        } catch (NumberFormatException e) {
            utilityClass.showError(notification, "Please enter a valid amount");
            return;
        }

        // Validate payer name
        String payerName = payerNameField.getText();
        if (payerName == null || payerName.isBlank()) {
            utilityClass.showError(notification, "Please enter payer name");
            return;
        }

        // Validate payer phone
        String payerPhone = payerPhoneField.getText();
        if (payerPhone == null || payerPhone.isBlank()) {
            utilityClass.showError(notification, "Please enter payer phone");
            return;
        }

        utilityClass.addNewFeesPayment(studentId, semesterId, gradeId, String.valueOf(amountPaid), payerName, payerPhone,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveFeesPaymentBtn, "SAVE PAYMENT");
                        clearPaymentForm();
                        utilityClass.showSuccess(notification, utilityClass.successMessageContainer);

                    });
                }, () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveFeesBtn, "SAVE PAYMENT");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                }, () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(saveFeesBtn, "SAVE PAYMENT");
                        utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
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
    public void handleSearchFeesReport() {
        //Get selected semesterId
        SemesterCaching selectedItem = reportSearchSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            utilityClass.showError(notification, "Please select a semester from the semester list");
            return;
        }
        String semesterId = selectedItem.getSemesterId();

        //Get selected gradeId
        GradeCaching selectedItemGrade = reportSearchClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedItemGrade == null) {
            utilityClass.showError(notification, "Please select a grade from the grade list");
            return;
        }
        String gradeId = selectedItemGrade.getLevelId();

        String studentId = reportSearchStudentIdField.getText();
        if (studentId == null) {
            utilityClass.showError(notification, "Please enter a fees balance");
            return;
        }
        utilityClass.processingButton(searchFeesReportBtn);
        utilityClass.searchFeesReport(studentId, semesterId, gradeId,
                () -> {
                    // On success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(searchFeesReportBtn, "Search Report");
                        displayFeesReport(utilityClass.report);
                    });
                },
                () -> {
                    // On failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(searchFeesReportBtn, "Search Report");
                        utilityClass.showError(notification, "Could not fetch data, check student Id and try again");
                    });
                }, () -> {
            //On Network Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(searchFeesReportBtn, "Search Report");
                        utilityClass.showError(notification, "Network Error! Check your network connection and try again");
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
        utilityClass.showError(notification, "Feature not yet implemented");
    }

    @FXML
    public void handleExport() {
        utilityClass.showError(notification, "Feature not yet implemented");
    }

    @FXML
    public void initializeAfterLogin() {
        utilityClass = new UtilityClass(this);
        setActiveButton(dashboardBtn);
        loadAccountantInfo();
        showLoading();
        utilityClass.loadFinanceDashboardData(accountantId, notification,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        loadDashboardData();

                        hideLoading();
                    });
                });
    }

    @FXML
    public void showAddSemesterFeesCard() {

        List<SemesterCaching> cachedSemesters = utilityClass.semesterCachingManager.getCachedSemesters();
        if (cachedSemesters != null && !cachedSemesters.isEmpty()) {
            if (feesSemesterComboBox.getItems().isEmpty()) {
                feesSemesterComboBox.getItems().addAll(cachedSemesters);
            }
        }

        List<GradeCaching> cachedGrades = utilityClass.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (feesClassComboBox.getItems().isEmpty()) {
                feesClassComboBox.getItems().addAll(cachedGrades);
            }
        }

        switchPageCards(addSemesterFeesCard, updateSemesterFeesCard,
                addSemesterFeesToggleBtn, updateFeesDetailsToggleBtn);
    }

    @FXML
    public void showUpdateSemesterFeesCard() {
        List<SemesterCaching> cachedSemesters = utilityClass.semesterCachingManager.getCachedSemesters();
        if (cachedSemesters != null && !cachedSemesters.isEmpty()) {
            if (searchFeesSemesterComboBox.getItems().isEmpty()) {
                searchFeesSemesterComboBox.getItems().addAll(cachedSemesters);
            }
        }

        List<GradeCaching> cachedGrades = utilityClass.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (searchFeesClassComboBox.getItems().isEmpty()) {
                searchFeesClassComboBox.getItems().addAll(cachedGrades);
            }
        }

        feesDetailsArea.setVisible(false);
        feesDetailsArea.setManaged(false);

        feesSearchArea.setVisible(true);
        feesSearchArea.setManaged(true);

        switchPageCards(updateSemesterFeesCard, addSemesterFeesCard,
                updateFeesDetailsToggleBtn, addSemesterFeesToggleBtn);
    }

    @FXML
    public void handleFetchFeesDetails() {
        SemesterCaching selectedSemester = searchFeesSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            utilityClass.showError(notification, "Please select a Term");
            return;
        }

        GradeCaching selectedGrade = searchFeesClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            utilityClass.showError(notification, "Please select a Grade");
            return;
        }

        utilityClass.processingButton(fetchFeesDetailsBtn);
        String semesterId = selectedSemester.getSemesterId();
        String levelId = selectedGrade.getLevelId();
        utilityClass.fetchFeesDetails(semesterId, levelId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(fetchFeesDetailsBtn, "FETCH FEES DETAILS");
                        fillFeesDetailsArea(feesDetails);
                        utilityClass.showSuccess(notification, utilityClass.successMessageContainer);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(fetchFeesDetailsBtn, "FETCH FEES DETAILS");
                        utilityClass.showError(notification, utilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        utilityClass.notProcessingButton(fetchFeesDetailsBtn, "FETCH FEES DETAILS");
                        utilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    private void fillFeesDetailsArea(FetchFeesDetails data) {
        updateFeesAmountField.setText(data.getAmount());

        //Get fee semester
        List<SemesterCaching> cachedSemesters = utilityClass.semesterCachingManager.getCachedSemesters();
        if (cachedSemesters != null && !cachedSemesters.isEmpty()) {
            SemesterCaching feesSemester = null;
            for (SemesterCaching semester : cachedSemesters) {
                if (semester.getSemesterId().equals(data.getSemesterId())) {
                    feesSemester = semester;
                    break;
                }
            }

            if (updateFeesSemesterComboBox.getItems().isEmpty()) {
                updateFeesSemesterComboBox.getItems().addAll(cachedSemesters);
            }
            updateFeesSemesterComboBox.getSelectionModel().select(feesSemester);
        }

        List<GradeCaching> cachedGrades = utilityClass.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            GradeCaching feesGrade = null;
            for (GradeCaching grade : cachedGrades) {
                if (grade.getLevelId().equals(data.getLevelId())) {
                    feesGrade = grade;
                    break;
                }
            }

            if (updateFeesClassComboBox.getItems().isEmpty()) {
                updateFeesClassComboBox.getItems().addAll(cachedGrades);
            }
            updateFeesClassComboBox.getSelectionModel().select(feesGrade);
        }

        feesSearchArea.setVisible(false);
        feesSearchArea.setManaged(false);

        feesDetailsArea.setVisible(true);
        feesDetailsArea.setManaged(true);
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

    private void switchView(ScrollPane scrollPane, Button actionButton) {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        addSemesterFeesView.setVisible(false);
        addSemesterFeesView.setManaged(false);
        recordNewPayment.setVisible(false);
        recordNewPayment.setManaged(false);
        searchStudentReportView.setVisible(false);
        searchStudentReportView.setManaged(false);

        scrollPane.setVisible(true);
        scrollPane.setManaged(true);

        setActiveButton(actionButton);
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

    private void setActiveButton(Button activeButton) {
        String INACTIVE_BUTTON = "-fx-background-color: transparent; -fx-text-fill: #ecf0f1; " +
                "-fx-font-size: 14px; -fx-padding: 15 20 15 20; -fx-background-radius: 8; " +
                "-fx-cursor: hand; -fx-border-color: transparent;";

        String ACTIVE_BUTTON = "-fx-background-color: #667eea; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 15 20 15 20; " +
                "-fx-background-radius: 8; -fx-cursor: hand;";

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
        updateDateTime();

        Double totalFees = utilityClass.totalSemesterFeesFromBackend;
        Double paidFees = utilityClass.feesPaidFromBackend;
        Double outstanding = totalFees - paidFees;

        totalFeesLabel.setText(String.format("%.2f", totalFees));
        amountPaidLabel.setText(String.format("%.2f", paidFees));
        outstandingLabel.setText(String.format("%.2f", outstanding));

        double progress = utilityClass.feesProgressBarFromBackend;
        progressBar.setProgress(progress);

        List<RecentPaymentTable> recentPaymentTransactions = utilityClass.feesCacheManager.getRecentPaymentCache();
        if (recentPaymentTransactions != null) {
            recentPaymentsTable.getItems().setAll(recentPaymentTransactions);
        }
    }

    private void loadAccountantInfo() {
        versionLabel.setText(utilityClass.applicationVersion);
        institutionNameLabel.setText(institutionName);
        accountantNameLabel.setText(accountantName);
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
}
