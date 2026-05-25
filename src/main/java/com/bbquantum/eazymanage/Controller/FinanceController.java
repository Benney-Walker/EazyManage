package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.DTO.Finance.*;
import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.DTO.Institution.SemesterCaching;
import com.bbquantum.eazymanage.DTO.Institution.StudentsHolder;
import com.bbquantum.eazymanage.Services.*;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FinanceController implements Initializable {
    //CONTROLLER OWN VARIABLES
    //
    // Phase 8b — services referenced directly. ApiContext is the shared
    // error/success store; the ApiUtilityClass facade is no longer
    // constructed by this controller.
    private final ApiCore apiCore          = new ApiCore();
    private final FinanceService financeService   = new FinanceService(apiCore);
    private final AcademicsService  academicsService = new AcademicsService(apiCore);
    private final DashboardService  dashboardService = new DashboardService(apiCore);
    public String staffId;
    private String institutionName;
    private String accountantName;
    private static String staffRole;


    //Hold fees Details for update
    public FetchFeesDetails feesDetails;

    //Hold Payment records
    public List<StudentPaymentRecords> studentPaymentRecords;

    //Holds grade payment Records
    public List<GradeFeesReport> gradeFeesReport;

    //Navigation Side
    @FXML public StackPane notification;
    @FXML public Label versionLabel;
    @FXML private Button dashboardBtn;
    @FXML private Button addFeesBtn;
    @FXML private Button recordPaymentBtn;
    @FXML private Button searchReportBtn;
    @FXML private Label accountantNameLabel;
    @FXML private Label institutionNameLabel;
    @FXML private Button switchRole;

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
    @FXML private ScrollPane manageFeesPaymentRecords;

    @FXML private Button addNewPaymentToggleBtn;
    @FXML private Button updatePaymentDetailsToggleBtn;

    @FXML private VBox newFeesPaymentCard;
    @FXML private ComboBox<StudentsHolder> newPaymentStudentSelection;
    @FXML private ComboBox<GradeCaching> newPaymentClassSelection;
    @FXML private ComboBox<SemesterCaching> newPaymentSemesterComboBox;
    @FXML private TextField newPaymentAmountField;
    @FXML private TextField newPaymentPayerNameField;
    @FXML private TextField newPaymentPayerPhoneField;
    @FXML private Button saveFeesPaymentBtn;

    @FXML private VBox updateFeesPaymentCard;
    @FXML private VBox paymentSearchArea;
    @FXML private ComboBox<StudentsHolder> searchPaymentStudentSelectionBox;
    @FXML private ComboBox<GradeCaching> searchPaymentClassSelectionBox;
    @FXML private ComboBox<SemesterCaching> searchPaymentSemesterComboBox;
    @FXML private Button fetchPaymentDetailsBtn;

    @FXML private VBox paymentRecordsForStudent;
    @FXML private TableView<StudentPaymentRecords> paymentRecordsTableView;
    @FXML private TableColumn<StudentPaymentRecords, String> paymentRecordDateCol;
    @FXML private TableColumn<StudentPaymentRecords, String> recordStudentIdCol;
    @FXML private TableColumn<StudentPaymentRecords, String> recordStudentNameCol;
    @FXML private TableColumn<StudentPaymentRecords, String> recordAmountCol;
    @FXML private TableColumn<StudentPaymentRecords, String> recordPayerCol;
    @FXML private TableColumn<StudentPaymentRecords, String> recordPayerContactCol;

    @FXML private VBox paymentDetailsArea;
    @FXML private Label transactionIdLabel;
    @FXML private TextField updatePaymentStudentIdField;
    @FXML private ComboBox<SemesterCaching> updatePaymentSemesterComboBox;
    @FXML private TextField updatePaymentAmountField;
    @FXML private TextField updatePaymentPayerNameField;
    @FXML private TextField updatePaymentPayerPhoneField;
    @FXML private Button updateFeesPaymentBtn;
    @FXML private Button deleteFeesPaymentBtn;

    //REPORT VIEW
    @FXML private ScrollPane searchStudentReportView;

    @FXML private Button studentPayReportToggleBtn;
    @FXML private Button gradePayReportToggleBtn;

    @FXML private VBox studentPaymentRecordsCard;
    @FXML private VBox studentPaymentRecordsArea;
    @FXML private ComboBox<StudentsHolder> reportStudentSelectionBox;
    @FXML private ComboBox<SemesterCaching> reportSearchSemesterComboBox;
    @FXML private ComboBox<GradeCaching> reportSearchClassComboBox;
    @FXML private Button searchFeesReportBtn;

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
    @FXML private TableView<PaymentHistory> paymentHistoryTable;
    @FXML private TableColumn<PaymentHistory, String> dateColumn;
    @FXML private TableColumn<PaymentHistory, String> amountColumn;
    @FXML private TableColumn<PaymentHistory, String> feesBalanceColumn;
    @FXML private TableColumn<PaymentHistory, String> payerNameColumn;
    @FXML private TableColumn<PaymentHistory, String> payerPhoneColumn;
    @FXML private Button printStudentPayReportBtn;

    @FXML private VBox gradePaymentRecordsCard;
    @FXML private VBox gradePaymentRecordsSearchArea;
    @FXML private ComboBox<SemesterCaching> gradePaymentsReportSemesterComboBox;
    @FXML private ComboBox<GradeCaching> gradePaymentsReportClassComboBox;
    @FXML private Button gradePaymentsReportBtn;

    @FXML private VBox gradePaymentsReportResponsePanel;
    @FXML private Label gradePaymentsReportNameLabel;
    @FXML private Label gradePaymentsReportIdLabel;
    @FXML private Label gradePaymentsReportSemesterLabel;
    @FXML private Label gradePaymentsReportTotalFeeLabel;
    @FXML private Label gradePaymentsReportAmountPaidLabel;
    @FXML private Label gradePaymentsReportBalanceLabel;
    @FXML private Label gradePaymentsReportPercentageLabel;
    @FXML private ProgressBar gradePaymentsReportProgressBar;
    @FXML private TableView<GradeFeesReport> gradePaymentsReportHistoryTable;
    @FXML private TableColumn<GradeFeesReport, String> studentIdColumn;
    @FXML private TableColumn<GradeFeesReport, String> studentNameColumn;
    @FXML private TableColumn<GradeFeesReport, String> amountToPayColumn;
    @FXML private TableColumn<GradeFeesReport, String> amountPaidColumn;
    @FXML private TableColumn<GradeFeesReport, String> outstandingAmountColumn;
    @FXML private Button gradePaymentsExportReportBtn;


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


        //STUDENT SPECIFIC PAYMENT RECORDS
        paymentRecordDateCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDateOfPayment()));

        recordStudentIdCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentId()));

        recordStudentNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentName()));

        recordAmountCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAmount()));

        recordPayerCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPayerName()));

        recordPayerContactCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPayerContact()));

        paymentRecordsTableView.setRowFactory(tableView -> {
            TableRow<StudentPaymentRecords> row = new TableRow<>();

            row.setOnMouseClicked((event) -> {
                if (!row.isEmpty()) {
                    StudentPaymentRecords paymentRecord = row.getItem();

                    if (paymentDetailsArea.isVisible() && updatePaymentStudentIdField.getText().trim().equals(paymentRecord.getStudentId())) {
                        paymentDetailsArea.setVisible(false);
                        paymentDetailsArea.setManaged(false);

                        updatePaymentStudentIdField.clear();
                        updatePaymentSemesterComboBox.getSelectionModel().clearSelection();
                        updatePaymentAmountField.clear();
                        updatePaymentPayerNameField.clear();
                        updatePaymentPayerPhoneField.clear();
                    } else {
                        populatePaymentForm(paymentRecord);

                        paymentDetailsArea.setVisible(true);
                        paymentDetailsArea.setManaged(true);
                    }
                }
            });
            return row;
        });


        //Grade Payment Records Table
        studentIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentId()));

        studentNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentName()));

        amountToPayColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTotalAmountToPay()));

        amountPaidColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAmountPayed()));

        outstandingAmountColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOutstanding()));
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
        switchView(manageFeesPaymentRecords, recordPaymentBtn);
        showAddNewPaymentCard();
    }

    @FXML
    public void showSearchReport() {
        switchView(searchStudentReportView, searchReportBtn);
        showFetchStudentPayReportCard();
    }

    @FXML
    public void handleOpenSwitchRole() throws IOException {
        // Close the login window
        Stage financeDashboard = (Stage) switchRole.getScene().getWindow();

        // Load the appropriate dashboard
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/SwitchRole/SwitchRole.fxml"));
        Scene scene = new Scene(loader.load());

        // Create new stage for dashboard
        Stage switchRoleStage = new Stage();
        switchRoleStage.setTitle("EazyManage - Authentication");
        switchRoleStage.setScene(scene);
        switchRoleStage.sizeToScene();
        switchRoleStage.setResizable(false);

        // Close login and show dashboard
        financeDashboard.close();
        switchRoleStage.show();
    }

    @FXML
    public void refreshDashboard() {
        showLoading();
        dashboardService.loadFinanceDashboardData(staffId, notification,
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
        if (Integer.parseInt(feesAmount) < 0 || feesAmount.isBlank()) {
            UtilityClass.showError(notification, "Fees amount invalid");
            return;
        }

        SemesterCaching selectedItem = feesSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            UtilityClass.showError(notification, "Please select a semester from the semester list");
            return;
        }
        String semesterId = selectedItem.getSemesterId();

        GradeCaching selectedItemGrade = feesClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedItemGrade == null) {
            UtilityClass.showError(notification, "Please select a grade from the grade list");
            return;
        }
        String gradeId = selectedItemGrade.getLevelId();
        UtilityClass.processingButton(saveFeesBtn);

        financeService.addNewFees(staffId, gradeId, semesterId, feesAmount,
                () -> {
            // On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(saveFeesBtn, "SAVE FEES");
                        UtilityClass.showSuccess(notification, ApiContext.getInstance().successMessageContainer);
                    });
                },
                () -> {
            // On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(saveFeesBtn, "SAVE FEES");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                }, () -> {
            //On Network Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(saveFeesBtn, "SAVE FEES");
                        UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    public void handleUpdateSemesterFees() {
        String feesAmount = updateFeesAmountField.getText().trim();
        if (Integer.parseInt(feesAmount) < 0 || feesAmount.isBlank()) {
            UtilityClass.showError(notification, "Invalid fees amount");
            return;
        }

        SemesterCaching selectedSemester = updateFeesSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            UtilityClass.showError(notification, "Please select a semester");
            return;
        }

        GradeCaching selectedGrade = updateFeesClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            UtilityClass.showError(notification, "Please select a grade");
            return;
        }

        UtilityClass.processingButton(updateFeesBtn);
        feesDetails.setAmount(feesAmount);
        feesDetails.setSemesterId(selectedSemester.getSemesterId());
        feesDetails.setLevelId(selectedGrade.getLevelId());

        financeService.updateSemesterFees(staffId, feesDetails,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(updateFeesBtn, "SAVE CHANGES");
                        UtilityClass.showSuccess(notification, ApiContext.getInstance().successMessageContainer);
                        feesDetailsArea.setVisible(false);
                        feesDetailsArea.setManaged(false);

                        feesSearchArea.setVisible(true);
                        feesSearchArea.setManaged(true);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(updateFeesBtn, "SAVE CHANGES");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(updateFeesBtn, "SAVE CHANGES");
                        UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    public  void handleFetchStudents() {
        GradeCaching selectedGrade = newPaymentClassSelection.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) return;

        showLoading();
        String gradeId = selectedGrade.getLevelId();
        academicsService.loadClassStudents(gradeId, staffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        hideLoading();
                        newPaymentStudentSelection.getItems().clear();
                        newPaymentStudentSelection.getItems().addAll(UtilityClass.studentsHolder);
                        newPaymentStudentSelection.setDisable(false);
                        UtilityClass.showSuccess(notification, "Successfully loaded students");
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        hideLoading();
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
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

    @FXML
    public void handleSavePayment() {
        if (newPaymentStudentSelection.isDisable()) {
            UtilityClass.showError(notification, "Please select grade to load students");
            return;
        }

        StudentsHolder student = newPaymentStudentSelection.getSelectionModel().getSelectedItem();
        if (student == null) {
            UtilityClass.showError(notification, "Please select a student");
            return;
        }

        // Validate semester
        SemesterCaching selectedSemester = newPaymentSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            UtilityClass.showError(notification, "Please select a semester from the semester list");
            return;
        }
        String semesterId = selectedSemester.getSemesterId();

        // Validate amount paid
        String amountPaidText = newPaymentAmountField.getText();
        if (Integer.parseInt(amountPaidText) < 0 || amountPaidText.isBlank()) {
            UtilityClass.showError(notification, "Amount can't be negative");
            return;
        }

        // Convert to Double
        Double amountPaid;
        try {
            amountPaid = Double.parseDouble(amountPaidText);
        } catch (NumberFormatException e) {
            UtilityClass.showError(notification, "Please enter a valid amount");
            return;
        }

        // Validate payer name
        String payerName = newPaymentPayerNameField.getText();
        if (payerName == null || payerName.isBlank()) {
            UtilityClass.showError(notification, "Please enter payer name");
            return;
        }

        // Validate payer phone
        String payerPhone = newPaymentPayerPhoneField.getText();
        if (payerPhone.length() != 10) {
            UtilityClass.showError(notification, "Please enter valid phone number");
            return;
        }

        financeService.addNewFeesPayment(staffId, student.getStudentId(), semesterId, String.valueOf(amountPaid), payerName, payerPhone,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(saveFeesPaymentBtn, "SAVE PAYMENT");
                        clearPaymentForm();
                        UtilityClass.showSuccess(notification, ApiContext.getInstance().successMessageContainer);

                    });
                }, () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(saveFeesBtn, "SAVE PAYMENT");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                }, () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(saveFeesBtn, "SAVE PAYMENT");
                        UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    private void clearPaymentForm() {
        newPaymentAmountField.clear();
        newPaymentPayerNameField.clear();
        newPaymentPayerPhoneField.clear();
    }

    public void handleFetchGradeStudents() {
        GradeCaching selectedGrade = reportSearchClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) return;

        showLoading();
        String gradeId = selectedGrade.getLevelId();
        academicsService.loadClassStudents(gradeId, staffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        hideLoading();
                        reportStudentSelectionBox.getItems().clear();
                        reportStudentSelectionBox.getItems().addAll(UtilityClass.studentsHolder);
                        reportStudentSelectionBox.setDisable(false);

                        UtilityClass.showSuccess(notification, "Successfully loaded students");
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        hideLoading();
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
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

    @FXML
    public void handleSearchFeesReport() {
        if (reportStudentSelectionBox.isDisable()) {
            UtilityClass.showError(notification, "Please select a class to fetch students");
            return;
        }

        StudentsHolder student = reportStudentSelectionBox.getSelectionModel().getSelectedItem();
        if (student == null) {
            UtilityClass.showError(notification, "Please select a student");
            return;
        }

        SemesterCaching selectedSemester = reportSearchSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            UtilityClass.showError(notification, "Please select a semester");
            return;
        }

        GradeCaching selectedGrade = reportSearchClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            UtilityClass.showError(notification, "Please select a grade");
            return;
        }

        UtilityClass.processingButton(searchFeesReportBtn);
        financeService.fetchStudentPaymentRecords(staffId, student.getStudentId(), selectedSemester.getSemesterId(), selectedGrade.getLevelId(),
                () -> {
                    // On success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(searchFeesReportBtn, "FETCH RECORDS");
                        displayFeesReport(UtilityClass.paymentRecords);
                        UtilityClass.showSuccess(notification, ApiContext.getInstance().successMessageContainer);
                    });
                },
                () -> {
                    // On failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(searchFeesReportBtn, "FETCH RECORDS");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                }, () -> {
            //On Network Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(searchFeesReportBtn, "FETCH RECORDS");
                        UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleClearPayRecordsFilters() {
        if (gradePaymentsReportResponsePanel.isVisible()) {
            gradePaymentsReportHistoryTable.getItems().clear();
            gradePaymentsReportResponsePanel.setVisible(false);
            gradePaymentsReportResponsePanel.setManaged(false);
        }

        gradePaymentsReportSemesterComboBox.getSelectionModel().clearSelection();
        gradePaymentsReportSemesterComboBox.getSelectionModel().clearSelection();

        gradePaymentRecordsSearchArea.setVisible(true);
        gradePaymentRecordsSearchArea.setManaged(true);
    }

    @FXML
    public void handleClearStudentPayRecordsFilters() {
        if (reportResponsePanel.isVisible()) {
            reportResponsePanel.setVisible(false);
            reportResponsePanel.setManaged(false);
        }

        reportSearchSemesterComboBox.getSelectionModel().clearSelection();
        reportSearchClassComboBox.getSelectionModel().clearSelection();

        reportStudentSelectionBox.getSelectionModel().clearSelection();

        studentPaymentRecordsArea.setVisible(true);
        studentPaymentRecordsArea.setManaged(true);
    }

    @FXML
    public void handlePrintStudentPayRecords() {
        String studentName = reportResponseStudentNameLabel.getText();
        String studentId = reportResponseStudentIdLabel.getText();
        String gradeName = reportResponseClassLabel.getText();
        String semesterName = reportResponseSemesterLabel.getText();

        UtilityClass.processingButton(printStudentPayReportBtn);
        UtilityClass.showSuccess(notification, "Keep working while we prepare your report");
        Platform.runLater(() -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Subject List");
            fileChooser.setInitialFileName(studentName + "_Payment_Records.csv");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                    new FileChooser.ExtensionFilter("Text Files", "*.txt")
            );

            Stage stage = (Stage) printStudentPayReportBtn.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try {
                    UtilityClass.saveStudentPayRecordsListToFile(
                            UtilityClass.paymentRecords.getPaymentHistory(),
                            file, gradeName, semesterName, studentName, studentId
                    );
                    UtilityClass.showSuccess(notification, "Successfully saved payment records to file");
                    UtilityClass.notProcessingButton(printStudentPayReportBtn, "EXPORT TO EXCEL");
                } catch (Exception e) {
                    e.printStackTrace();
                    UtilityClass.showError(notification, "Could not export file");
                }
            } else {
                UtilityClass.showError(notification, "Could not export file");
            }
        });
    }

    @FXML
    public void handlePrintGradeFeesReport() {
        String levelName = gradePaymentsReportNameLabel.getText();
        String semesterName = gradePaymentsReportSemesterLabel.getText();

        UtilityClass.processingButton(gradePaymentsExportReportBtn);
        UtilityClass.showSuccess(notification, "Keep working while we prepare your report");
        Platform.runLater(() -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Subject List");
            fileChooser.setInitialFileName(levelName + "_Payment_Records.csv");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                    new FileChooser.ExtensionFilter("Text Files", "*.txt")
            );

            Stage stage = (Stage) gradePaymentsExportReportBtn.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try {
                    UtilityClass.saveGradePayRecordsListToFile(gradeFeesReport,
                            file, levelName, semesterName
                    );
                    UtilityClass.showSuccess(notification, "Successfully saved payment records to file");
                    UtilityClass.notProcessingButton(gradePaymentsExportReportBtn, "EXPORT TO EXCEL");
                } catch (Exception e) {
                    e.printStackTrace();
                    UtilityClass.showError(notification, "Could not export file");
                }
            } else {
                UtilityClass.showError(notification, "Could not export file");
            }
        });
    }

    @FXML
    public void initializeAfterLogin() {
        setActiveButton(dashboardBtn);
        loadAccountantInfo();
        showLoading();
        dashboardService.loadFinanceDashboardData(staffId, notification,
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

        List<SemesterCaching> cachedSemesters = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (cachedSemesters != null && !cachedSemesters.isEmpty()) {
            if (feesSemesterComboBox.getItems().isEmpty()) {
                feesSemesterComboBox.getItems().addAll(cachedSemesters);
            }
        }

        List<GradeCaching> cachedGrades = UtilityClass.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (feesClassComboBox.getItems().isEmpty()) {
                feesClassComboBox.getItems().addAll(cachedGrades);
            }
        }

        switchPageCards(addSemesterFeesCard, updateSemesterFeesCard,
                addSemesterFeesToggleBtn, updateFeesDetailsToggleBtn);
    }

    @FXML
    public void showAddNewPaymentCard() {
        newPaymentStudentSelection.setDisable(true);

        List<SemesterCaching> semesters = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (semesters != null && !semesters.isEmpty()) {
            if (newPaymentSemesterComboBox.getItems().isEmpty()) {
                Platform.runLater(() -> {
                    newPaymentSemesterComboBox.getItems().addAll(semesters);
                });
            }
        }

        List<GradeCaching> cachedGrades = UtilityClass.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (newPaymentClassSelection.getItems().isEmpty()) {
                Platform.runLater(() -> {
                    newPaymentClassSelection.getItems().addAll(cachedGrades);
                });
            }
        }

        switchPageCards(newFeesPaymentCard, updateFeesPaymentCard,
                addNewPaymentToggleBtn, updatePaymentDetailsToggleBtn);
    }

    @FXML
    public void showPaymentDetailsCard() {
        searchPaymentStudentSelectionBox.setDisable(true);

        List<SemesterCaching> semesters = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (semesters != null && !semesters.isEmpty()) {
            if (searchPaymentSemesterComboBox.getItems().isEmpty()) {
                searchPaymentSemesterComboBox.getItems().addAll(semesters);
            }
        }

        List<GradeCaching> cachedGrades = UtilityClass.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()) {
            if (searchPaymentClassSelectionBox.getItems().isEmpty()) {
                searchPaymentClassSelectionBox.getItems().addAll(cachedGrades);
            }
        }

        paymentDetailsArea.setVisible(false);
        paymentDetailsArea.setManaged(false);

        paymentRecordsForStudent.setVisible(false);
        paymentRecordsForStudent.setManaged(false);

        paymentSearchArea.setVisible(true);
        paymentSearchArea.setManaged(true);

        switchPageCards(updateFeesPaymentCard, newFeesPaymentCard,
                updatePaymentDetailsToggleBtn, addNewPaymentToggleBtn);
    }

     public void showFetchStudentPayReportCard() {
        List<SemesterCaching> cachedSemesters = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (cachedSemesters != null && !cachedSemesters.isEmpty()) {
            if (reportSearchSemesterComboBox.getItems().isEmpty()) {
                reportSearchSemesterComboBox.getItems().addAll(cachedSemesters);
            }
        }

         List<GradeCaching> cachedGrades = UtilityClass.gradeCacheManager.getGradesCache();
         if (cachedGrades != null && !cachedGrades.isEmpty()) {
             if (reportSearchClassComboBox.getItems().isEmpty()) {
                 reportSearchClassComboBox.getItems().addAll(cachedGrades);
             }
         }

        switchPageCards(studentPaymentRecordsCard, gradePaymentRecordsCard,
                studentPayReportToggleBtn, gradePayReportToggleBtn);
     }

     public void showFetchGradePayReportCard() {
         List<SemesterCaching> cachedSemesters = UtilityClass.semesterCachingManager.getCachedSemesters();
         if (cachedSemesters != null && !cachedSemesters.isEmpty()) {
             if (gradePaymentsReportSemesterComboBox.getItems().isEmpty()) {
                 gradePaymentsReportSemesterComboBox.getItems().addAll(cachedSemesters);
             }
         }

         List<GradeCaching> cachedGrades = UtilityClass.gradeCacheManager.getGradesCache();
         if (cachedGrades != null && !cachedGrades.isEmpty()) {
             if (gradePaymentsReportClassComboBox.getItems().isEmpty()) {
                 gradePaymentsReportClassComboBox.getItems().addAll(cachedGrades);
             }
         }

         gradePaymentsReportResponsePanel.setVisible(false);
         gradePaymentsReportResponsePanel.setManaged(false);

         gradePaymentRecordsSearchArea.setVisible(true);
         gradePaymentRecordsSearchArea.setManaged(true);

         switchPageCards(gradePaymentRecordsCard, studentPaymentRecordsCard,
                 gradePayReportToggleBtn, studentPayReportToggleBtn);
     }

    @FXML
    public void showUpdateSemesterFeesCard() {
        List<SemesterCaching> cachedSemesters = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (cachedSemesters != null && !cachedSemesters.isEmpty()) {
            if (searchFeesSemesterComboBox.getItems().isEmpty()) {
                searchFeesSemesterComboBox.getItems().addAll(cachedSemesters);
            }
        }

        List<GradeCaching> cachedGrades = UtilityClass.gradeCacheManager.getGradesCache();
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
            UtilityClass.showError(notification, "Please select a Term");
            return;
        }

        GradeCaching selectedGrade = searchFeesClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            UtilityClass.showError(notification, "Please select a Grade");
            return;
        }

        UtilityClass.processingButton(fetchFeesDetailsBtn);
        String semesterId = selectedSemester.getSemesterId();
        String levelId = selectedGrade.getLevelId();
        financeService.fetchFeesDetails(staffId, semesterId, levelId,
                semesterFees -> feesDetails = semesterFees,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(fetchFeesDetailsBtn, "FETCH FEES DETAILS");
                        fillFeesDetailsArea(feesDetails);
                        UtilityClass.showSuccess(notification, ApiContext.getInstance().successMessageContainer);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(fetchFeesDetailsBtn, "FETCH FEES DETAILS");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(fetchFeesDetailsBtn, "FETCH FEES DETAILS");
                        UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleGetStudents() {
        GradeCaching selectedGrade = searchPaymentClassSelectionBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) return;

        showLoading();
        String gradeId = selectedGrade.getLevelId();

        academicsService.loadClassStudents(gradeId, staffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        hideLoading();
                        searchPaymentStudentSelectionBox.getItems().clear();
                        searchPaymentStudentSelectionBox.getItems().addAll(UtilityClass.studentsHolder);
                        searchPaymentStudentSelectionBox.setDisable(false);

                        UtilityClass.showSuccess(notification, "Students fetched successfully");
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        hideLoading();
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
            //On network Issue
                    Platform.runLater(() -> {
                        hideLoading();
                        UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleFetchPayment() {
        if (searchPaymentStudentSelectionBox.isDisable()) {
            UtilityClass.showError(notification, "Please select grade to load students");
            return;
        }

        GradeCaching selectedGrade = searchPaymentClassSelectionBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            UtilityClass.showError(notification, "Select grade");
            return;
        }

        StudentsHolder selectedStudent = searchPaymentStudentSelectionBox.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            UtilityClass.showError(notification, "Please select a student");
            return;
        }

        SemesterCaching selectedSemester = searchPaymentSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            UtilityClass.showError(notification, "Please select a Term");
            return;
        }

        UtilityClass.processingButton(fetchPaymentDetailsBtn);
        financeService.fetchPaymentRecord(staffId, selectedStudent.getStudentId(), selectedGrade.getLevelId(), selectedSemester.getSemesterId(),
                records -> studentPaymentRecords = records,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(fetchPaymentDetailsBtn, "FETCH PAYMENT RECORDS");
                        UtilityClass.showSuccess(notification, ApiContext.getInstance().successMessageContainer);

                        paymentRecordsTableView.getItems().clear();
                        paymentRecordsTableView.getItems().addAll(studentPaymentRecords);

                        paymentSearchArea.setVisible(false);
                        paymentSearchArea.setManaged(false);

                        paymentDetailsArea.setVisible(false);
                        paymentDetailsArea.setManaged(false);

                        paymentRecordsForStudent.setVisible(true);
                        paymentRecordsForStudent.setManaged(true);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(fetchPaymentDetailsBtn, "FETCH PAYMENT RECORDS");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(fetchPaymentDetailsBtn, "FETCH PAYMENT RECORDS");
                        UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleUpdatePayment() {
        String studentId = updatePaymentStudentIdField.getText().trim();
        String transactionId = transactionIdLabel.getText().trim();

        SemesterCaching selectedSemester = updatePaymentSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            UtilityClass.showError(notification, "Please select a Term");
            return;
        }

        String amount = updatePaymentAmountField.getText().trim();
        if (Integer.parseInt(amount) < 0 || amount.isBlank()) {
            UtilityClass.showError(notification, "Please enter amount");
            return;
        }

        String paidBy = updatePaymentPayerNameField.getText().trim();
        if (paidBy.isEmpty()) {
            UtilityClass.showError(notification, "Please enter payee name");
            return;
        }

        String contact = updatePaymentPayerPhoneField.getText().trim();
        if (contact.length() < 10) {
            UtilityClass.showError(notification, "Please enter valid contact number");
            return;
        }

        StudentPaymentRecords update = new StudentPaymentRecords();

        for (StudentPaymentRecords records : studentPaymentRecords) {
            if (records.getPaymentId().equals(transactionId)) {
                records.setAmount(amount);
                records.setPayerContact(contact);
                records.setPayerName(paidBy);
                update = records;
                break;
            }
        }

        UtilityClass.processingButton(updateFeesPaymentBtn);
        financeService.updatePaymentDetails(staffId, update,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(updateFeesPaymentBtn, "SAVE CHANGES");
                        UtilityClass.showSuccess(notification, ApiContext.getInstance().successMessageContainer);
                        paymentDetailsArea.setVisible(false);
                        paymentDetailsArea.setManaged(false);

                        paymentRecordsTableView.refresh();
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(updateFeesPaymentBtn, "SAVE CHANGES");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(updateFeesPaymentBtn, "SAVE CHANGES");
                        UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleDeletePayment() {
        String transactionId = transactionIdLabel.getText().trim();
        UtilityClass.processingButton(deleteFeesPaymentBtn);

        financeService.deletePaymentRecord(staffId, transactionId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(deleteFeesPaymentBtn, "DELETE RECORD");
                        UtilityClass.showSuccess(notification, ApiContext.getInstance().successMessageContainer);
                        paymentDetailsArea.setVisible(false);
                        paymentDetailsArea.setManaged(false);

                        updatePaymentsTableAfterDelete(studentPaymentRecords, transactionId);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(deleteFeesPaymentBtn, "DELETE RECORD");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(deleteFeesPaymentBtn, "DELETE RECORD");
                        UtilityClass.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleCloseRecordsTable() {
        paymentRecordsForStudent.setVisible(false);
        paymentRecordsForStudent.setManaged(false);

        paymentRecordsTableView.getItems().clear();

        searchPaymentStudentSelectionBox.getSelectionModel().clearSelection();
        searchPaymentStudentSelectionBox.setDisable(true);
        searchPaymentSemesterComboBox.getSelectionModel().clearSelection();

        paymentSearchArea.setVisible(true);
        paymentSearchArea.setManaged(true);
    }

    @FXML
    public void handleFetchGradeFeesReport() {
        SemesterCaching selectedSemester = gradePaymentsReportSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            UtilityClass.showError(notification, "Please select a Semester");
            return;
        }

        GradeCaching selectedGrade = gradePaymentsReportClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            UtilityClass.showError(notification, "Please select a Grade");
            return;
        }

        UtilityClass.processingButton(gradePaymentsReportBtn);
        financeService.fetchGradeFeesRecords(staffId, selectedGrade.getLevelId(), selectedSemester.getSemesterId(),
                records -> gradeFeesReport = records,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(gradePaymentsReportBtn, "FETCH PAYMENTS REPORT");
                        populateGradePaymentsReportResponsePanel(gradeFeesReport,
                                selectedGrade, selectedSemester.getSemesterName());
                        UtilityClass.showSuccess(notification, ApiContext.getInstance().successMessageContainer);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(gradePaymentsReportBtn, "FETCH PAYMENTS REPORT");
                        UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityClass.notProcessingButton(gradePaymentsReportBtn, "FETCH PAYMENTS REPORT");
                        UtilityClass.showError(notification, "Network Error! Please check your network connection");
                    });
                });
    }

    private void populateGradePaymentsReportResponsePanel(List<GradeFeesReport> reports,
                                                          GradeCaching grade,
                                                          String semesterName) {
        gradePaymentsReportIdLabel.setText(grade.getLevelId());
        gradePaymentsReportNameLabel.setText(grade.getLevelName());
        gradePaymentsReportSemesterLabel.setText(semesterName);

        double totalFees = 0;
        double totalPayments = 0;
        double outStandingBalance = 0;
        for (GradeFeesReport reportItem : reports) {
            totalFees += Double.parseDouble(reportItem.getTotalAmountToPay());
            totalPayments += Double.parseDouble(reportItem.getAmountPayed());
            outStandingBalance += Double.parseDouble(reportItem.getOutstanding());
        }

        gradePaymentsReportTotalFeeLabel.setText(String.valueOf(totalFees));
        gradePaymentsReportAmountPaidLabel.setText(String.valueOf(totalPayments));
        gradePaymentsReportBalanceLabel.setText(String.valueOf(outStandingBalance));

        double percentagePaid = (totalPayments / totalFees) * 100;
        double progressBar = percentagePaid / 100;

        gradePaymentsReportPercentageLabel.setText(percentagePaid + "%");
        gradePaymentsReportProgressBar.setProgress(progressBar);

        gradePaymentsReportHistoryTable.getItems().addAll(reports);

        gradePaymentRecordsSearchArea.setVisible(false);
        gradePaymentRecordsSearchArea.setManaged(false);

        gradePaymentsReportResponsePanel.setVisible(true);
        gradePaymentsReportResponsePanel.setManaged(true);
    }

    private void updatePaymentsTableAfterDelete(List<StudentPaymentRecords> previousList, String TXId) {
        List<StudentPaymentRecords> updatedList = new ArrayList<>();

        for (StudentPaymentRecords records : previousList) {
            if (!records.getPaymentId().equals(TXId)) {
                updatedList.add(records);
            }
        }

        studentPaymentRecords = updatedList;
        paymentRecordsTableView.getItems().clear();
        paymentRecordsTableView.getItems().addAll(studentPaymentRecords);
        paymentRecordsTableView.refresh();
    }

    private void populatePaymentForm(StudentPaymentRecords paymentRecord) {
        transactionIdLabel.setText(paymentRecord.getPaymentId());
        updatePaymentStudentIdField.setText(paymentRecord.getStudentId());
        updatePaymentStudentIdField.setDisable(true);

        List<SemesterCaching> cached = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (cached != null && !cached.isEmpty()) {
            if (updatePaymentSemesterComboBox.getItems().isEmpty()) {
                updatePaymentSemesterComboBox.getItems().addAll(cached);
            }

            for (SemesterCaching semester : cached) {
                if (semester.getSemesterId().equals(paymentRecord.getSemesterId())) {
                    updatePaymentSemesterComboBox.getSelectionModel().select(semester);
                    updatePaymentSemesterComboBox.setDisable(true);
                }
            }
        }

        updatePaymentAmountField.setText(paymentRecord.getAmount());
        updatePaymentPayerNameField.setText(paymentRecord.getPayerName());
        updatePaymentPayerPhoneField.setText(paymentRecord.getPayerContact());
    }

    private void fillFeesDetailsArea(FetchFeesDetails data) {
        updateFeesAmountField.setText(data.getAmount());

        //Get fee semester
        List<SemesterCaching> cachedSemesters = UtilityClass.semesterCachingManager.getCachedSemesters();
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

        List<GradeCaching> cachedGrades = UtilityClass.gradeCacheManager.getGradesCache();
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
        this.staffId = accountantId;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public void setStaffName(String accountantName) {
        this.accountantName = accountantName;
    }

    public void setStaffRole(String staffRole) {
        this.staffRole = staffRole;
    }

    public static String getStaffRole() {
        return staffRole;
    }

    private void displayFeesReport(StudentFeesPaymentDisplay data) {
        reportResponseStudentIdLabel.setText(data.getStudentId());
        reportResponseStudentNameLabel.setText(data.getStudentName());
        reportResponseClassLabel.setText(data.getLevelName());
        reportResponseSemesterLabel.setText(data.getSemesterName());

        // Get Fees Values
        double totalFees = Double.parseDouble(data.getTotalFeesAmount());
        double totalFeesPaid = Double.parseDouble(data.getTotalAmountPaid());
        double outStandingBalance = totalFees -  totalFeesPaid;

        reportResponseTotalFeeLabel.setText(String.valueOf(totalFees));
        reportResponseAmountPaidLabel.setText(String.valueOf(totalFeesPaid));
        reportResponseBalanceLabel.setText(String.valueOf(outStandingBalance));

        double percent = collectionPercentageCalc(totalFees, totalFeesPaid);
        reportResponsePercentageLabel.setText(percent + "%");
        reportResponseProgressBar.setProgress(Double.parseDouble(String.format("%.2f", percent/100)));

        //Populate the payment history table
        paymentHistoryTable.getItems().addAll(data.getPaymentHistory());
        reportResponsePanel.setVisible(true);
        reportResponsePanel.setManaged(true);

        studentPaymentRecordsArea.setVisible(false);
        studentPaymentRecordsArea.setManaged(false);
    }

    private Double collectionPercentageCalc(Double totalSemesterFeesFromBackend,
                                            Double feesPaidFromBackend) {
        return (feesPaidFromBackend / totalSemesterFeesFromBackend) * 100;
    }

    private void switchView(ScrollPane scrollPane, Button actionButton) {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        addSemesterFeesView.setVisible(false);
        addSemesterFeesView.setManaged(false);
        manageFeesPaymentRecords.setVisible(false);
        manageFeesPaymentRecords.setManaged(false);
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

        Double totalFees = UtilityClass.totalSemesterFeesFromBackend;
        Double paidFees = UtilityClass.feesPaidFromBackend;
        Double outstanding = totalFees - paidFees;

        totalFeesLabel.setText(String.format("%.2f", totalFees));
        amountPaidLabel.setText(String.format("%.2f", paidFees));
        outstandingLabel.setText(String.format("%.2f", outstanding));

        double progress = paidFees/totalFees;
        percentageLabel.setText(String.format("%.2f", progress * 100) + "%");
        progressBar.setProgress(progress);

        List<RecentPaymentTable> recentPaymentTransactions = UtilityClass.feesCacheManager.getRecentPaymentCache();
        if (recentPaymentTransactions != null) {
            recentPaymentsTable.getItems().setAll(recentPaymentTransactions);
        }
    }

    private void loadAccountantInfo() {
        versionLabel.setText(UtilityClass.APPLICATION_VERSION);
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
