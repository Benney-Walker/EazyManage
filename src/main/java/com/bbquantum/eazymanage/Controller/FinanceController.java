package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.DTO.Finance.*;
import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.DTO.Institution.SemesterCaching;
import com.bbquantum.eazymanage.DTO.Institution.StudentsHolder;
import com.bbquantum.eazymanage.Utitity.ApiUtilityClass;
import com.bbquantum.eazymanage.Utitity.UtilityMethods;
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
    private ApiUtilityClass apiUtilityClass;
    public String staffId;
    public String institutionName;
    public String accountantName;

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
    @FXML private ScrollPane manageFeesPaymentPayment;

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
        switchView(manageFeesPaymentPayment, recordPaymentBtn);
        showAddNewPaymentCard();
    }

    @FXML
    public void showSearchReport() {
        switchView(searchStudentReportView, searchReportBtn);
        showFetchStudentPayReportCard();
    }

    @FXML
    public void handleLogout() throws IOException {
        UtilityMethods.clearSession();
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
        apiUtilityClass.loadFinanceDashboardData(staffId, notification,
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
            UtilityMethods.showError(notification, "Please enter fees amount");
            return;
        }

        SemesterCaching selectedItem = feesSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            UtilityMethods.showError(notification, "Please select a semester from the semester list");
            return;
        }
        String semesterId = selectedItem.getSemesterId();

        GradeCaching selectedItemGrade = feesClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedItemGrade == null) {
            UtilityMethods.showError(notification, "Please select a grade from the grade list");
            return;
        }
        String gradeId = selectedItemGrade.getLevelId();
        UtilityMethods.processingButton(saveFeesBtn);

        apiUtilityClass.addNewFees(gradeId, semesterId, feesAmount,
                () -> {
            // On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(saveFeesBtn, "SAVE FEES");
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                    });
                },
                () -> {
            // On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(saveFeesBtn, "SAVE FEES");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                }, () -> {
            //On Network Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(saveFeesBtn, "SAVE FEES");
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    public void handleUpdateSemesterFees() {
        String feesAmount = updateFeesAmountField.getText().trim();
        if (feesAmount.isEmpty()) {
            UtilityMethods.showError(notification, "Please enter fees amount");
            return;
        }

        SemesterCaching selectedSemester = updateFeesSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            UtilityMethods.showError(notification, "Please select a semester");
            return;
        }

        GradeCaching selectedGrade = updateFeesClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            UtilityMethods.showError(notification, "Please select a grade");
            return;
        }

        UtilityMethods.processingButton(updateFeesBtn);
        feesDetails.setAmount(feesAmount);
        feesDetails.setSemesterId(selectedSemester.getSemesterId());
        feesDetails.setLevelId(selectedGrade.getLevelId());

        apiUtilityClass.updateSemesterFees(feesDetails,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(updateFeesBtn, "SAVE CHANGES");
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                        feesDetailsArea.setVisible(false);
                        feesDetailsArea.setManaged(false);

                        feesSearchArea.setVisible(true);
                        feesSearchArea.setManaged(true);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(updateFeesBtn, "SAVE CHANGES");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(updateFeesBtn, "SAVE CHANGES");
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    public  void handleFetchStudents() {
        GradeCaching selectedGrade = newPaymentClassSelection.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) return;

        String gradeId = selectedGrade.getLevelId();
        apiUtilityClass.loadClassStudents(gradeId, staffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        newPaymentStudentSelection.getItems().clear();
                        newPaymentStudentSelection.getItems().addAll(UtilityMethods.studentsHolder);
                        newPaymentStudentSelection.setDisable(false);
                        UtilityMethods.showSuccess(notification, "Successfully loaded students");
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleSavePayment() {
        if (newPaymentStudentSelection.isDisable()) {
            UtilityMethods.showError(notification, "Please select grade to load students");
            return;
        }

        StudentsHolder student = newPaymentStudentSelection.getSelectionModel().getSelectedItem();
        if (student == null) {
            UtilityMethods.showError(notification, "Please select a student");
            return;
        }

        // Validate semester
        SemesterCaching selectedSemester = newPaymentSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            UtilityMethods.showError(notification, "Please select a semester from the semester list");
            return;
        }
        String semesterId = selectedSemester.getSemesterId();

        // Validate amount paid
        String amountPaidText = newPaymentAmountField.getText();
        if (amountPaidText == null || amountPaidText.isBlank()) {
            UtilityMethods.showError(notification, "Please enter amount paid");
            return;
        }

        // Convert to Double
        Double amountPaid;
        try {
            amountPaid = Double.parseDouble(amountPaidText);
        } catch (NumberFormatException e) {
            UtilityMethods.showError(notification, "Please enter a valid amount");
            return;
        }

        // Validate payer name
        String payerName = newPaymentPayerNameField.getText();
        if (payerName == null || payerName.isBlank()) {
            UtilityMethods.showError(notification, "Please enter payer name");
            return;
        }

        // Validate payer phone
        String payerPhone = newPaymentPayerPhoneField.getText();
        if (payerPhone == null || payerPhone.isBlank()) {
            UtilityMethods.showError(notification, "Please enter payer phone");
            return;
        }

        apiUtilityClass.addNewFeesPayment(student.getStudentId(), semesterId, String.valueOf(amountPaid), payerName, payerPhone,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(saveFeesPaymentBtn, "SAVE PAYMENT");
                        clearPaymentForm();
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);

                    });
                }, () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(saveFeesBtn, "SAVE PAYMENT");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                }, () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(saveFeesBtn, "SAVE PAYMENT");
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    private void clearPaymentForm() {
        newPaymentStudentSelection.getSelectionModel().clearSelection();
        newPaymentStudentSelection.setDisable(true);
        newPaymentAmountField.clear();
        newPaymentPayerNameField.clear();
        newPaymentPayerPhoneField.clear();
        newPaymentSemesterComboBox.getSelectionModel().clearSelection();
    }

    public void handleFetchGradeStudents() {
        GradeCaching selectedGrade = reportSearchClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) return;

        UtilityMethods.showSuccess(notification, "Fetching grade students");
        String gradeId = selectedGrade.getLevelId();
        apiUtilityClass.loadClassStudents(gradeId, staffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        reportStudentSelectionBox.getItems().clear();
                        reportStudentSelectionBox.getItems().addAll(UtilityMethods.studentsHolder);
                        reportStudentSelectionBox.setDisable(false);

                        UtilityMethods.showSuccess(notification, "Successfully loaded students");
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleSearchFeesReport() {
        if (reportStudentSelectionBox.isDisable()) {
            UtilityMethods.showError(notification, "Please select a class to fetch students");
            return;
        }

        StudentsHolder student = reportStudentSelectionBox.getSelectionModel().getSelectedItem();
        if (student == null) {
            UtilityMethods.showError(notification, "Please select a student");
            return;
        }

        SemesterCaching selectedSemester = reportSearchSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            UtilityMethods.showError(notification, "Please select a semester");
            return;
        }

        GradeCaching selectedGrade = reportSearchClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            UtilityMethods.showError(notification, "Please select a grade");
            return;
        }

        UtilityMethods.processingButton(searchFeesReportBtn);
        apiUtilityClass.fetchStudentPaymentRecords(student.getStudentId(), selectedSemester.getSemesterId(), selectedGrade.getLevelId(),
                () -> {
                    // On success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(searchFeesReportBtn, "FETCH RECORDS");
                        displayFeesReport(UtilityMethods.paymentRecords);
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                    });
                },
                () -> {
                    // On failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(searchFeesReportBtn, "FETCH RECORDS");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                }, () -> {
            //On Network Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(searchFeesReportBtn, "FETCH RECORDS");
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
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

        UtilityMethods.processingButton(printStudentPayReportBtn);
        UtilityMethods.showSuccess(notification, "Keep working while we prepare your report");
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
                    UtilityMethods.saveStudentPayRecordsListToFile(
                            UtilityMethods.paymentRecords.getPaymentHistory(),
                            file, gradeName, semesterName, studentName, studentId
                    );
                    UtilityMethods.showSuccess(notification, "Successfully saved payment records to file");
                    UtilityMethods.notProcessingButton(printStudentPayReportBtn, "EXPORT TO EXCEL");
                } catch (Exception e) {
                    e.printStackTrace();
                    UtilityMethods.showError(notification, "Could not export file");
                }
            } else {
                UtilityMethods.showError(notification, "Could not export file");
            }
        });
    }

    @FXML
    public void handlePrintGradeFeesReport() {
        String levelName = gradePaymentsReportNameLabel.getText();
        String semesterName = gradePaymentsReportSemesterLabel.getText();

        UtilityMethods.processingButton(gradePaymentsExportReportBtn);
        UtilityMethods.showSuccess(notification, "Keep working while we prepare your report");
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
                    UtilityMethods.saveGradePayRecordsListToFile(gradeFeesReport,
                            file, levelName, semesterName
                    );
                    UtilityMethods.showSuccess(notification, "Successfully saved payment records to file");
                    UtilityMethods.notProcessingButton(gradePaymentsExportReportBtn, "EXPORT TO EXCEL");
                } catch (Exception e) {
                    e.printStackTrace();
                    UtilityMethods.showError(notification, "Could not export file");
                }
            } else {
                UtilityMethods.showError(notification, "Could not export file");
            }
        });
    }

    @FXML
    public void initializeAfterLogin() {
        apiUtilityClass = new ApiUtilityClass(this);
        setActiveButton(dashboardBtn);
        loadAccountantInfo();
        showLoading();
        apiUtilityClass.loadFinanceDashboardData(staffId, notification,
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

        List<SemesterCaching> cachedSemesters = UtilityMethods.semesterCachingManager.getCachedSemesters();
        if (cachedSemesters != null && !cachedSemesters.isEmpty()) {
            if (feesSemesterComboBox.getItems().isEmpty()) {
                feesSemesterComboBox.getItems().addAll(cachedSemesters);
            }
        }

        List<GradeCaching> cachedGrades = UtilityMethods.gradeCacheManager.getGradesCache();
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

        List<SemesterCaching> semesters = UtilityMethods.semesterCachingManager.getCachedSemesters();
        if (semesters != null && !semesters.isEmpty()) {
            if (newPaymentSemesterComboBox.getItems().isEmpty()) {
                Platform.runLater(() -> {
                    newPaymentSemesterComboBox.getItems().addAll(semesters);
                });
            }
        }

        List<GradeCaching> cachedGrades = UtilityMethods.gradeCacheManager.getGradesCache();
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

        List<SemesterCaching> semesters = UtilityMethods.semesterCachingManager.getCachedSemesters();
        if (semesters != null && !semesters.isEmpty()) {
            if (searchPaymentSemesterComboBox.getItems().isEmpty()) {
                searchPaymentSemesterComboBox.getItems().addAll(semesters);
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
        List<SemesterCaching> cachedSemesters = UtilityMethods.semesterCachingManager.getCachedSemesters();
        if (cachedSemesters != null && !cachedSemesters.isEmpty()) {
            if (reportSearchSemesterComboBox.getItems().isEmpty()) {
                reportSearchSemesterComboBox.getItems().addAll(cachedSemesters);
            }
        }

         List<GradeCaching> cachedGrades = UtilityMethods.gradeCacheManager.getGradesCache();
         if (cachedGrades != null && !cachedGrades.isEmpty()) {
             if (reportSearchClassComboBox.getItems().isEmpty()) {
                 reportSearchClassComboBox.getItems().addAll(cachedGrades);
             }
         }

        switchPageCards(studentPaymentRecordsCard, gradePaymentRecordsCard,
                studentPayReportToggleBtn, gradePayReportToggleBtn);
     }

     public void showFetchGradePayReportCard() {
         List<SemesterCaching> cachedSemesters = UtilityMethods.semesterCachingManager.getCachedSemesters();
         if (cachedSemesters != null && !cachedSemesters.isEmpty()) {
             if (gradePaymentsReportSemesterComboBox.getItems().isEmpty()) {
                 gradePaymentsReportSemesterComboBox.getItems().addAll(cachedSemesters);
             }
         }

         List<GradeCaching> cachedGrades = UtilityMethods.gradeCacheManager.getGradesCache();
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
        List<SemesterCaching> cachedSemesters = UtilityMethods.semesterCachingManager.getCachedSemesters();
        if (cachedSemesters != null && !cachedSemesters.isEmpty()) {
            if (searchFeesSemesterComboBox.getItems().isEmpty()) {
                searchFeesSemesterComboBox.getItems().addAll(cachedSemesters);
            }
        }

        List<GradeCaching> cachedGrades = UtilityMethods.gradeCacheManager.getGradesCache();
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
            UtilityMethods.showError(notification, "Please select a Term");
            return;
        }

        GradeCaching selectedGrade = searchFeesClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            UtilityMethods.showError(notification, "Please select a Grade");
            return;
        }

        UtilityMethods.processingButton(fetchFeesDetailsBtn);
        String semesterId = selectedSemester.getSemesterId();
        String levelId = selectedGrade.getLevelId();
        apiUtilityClass.fetchFeesDetails(semesterId, levelId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(fetchFeesDetailsBtn, "FETCH FEES DETAILS");
                        fillFeesDetailsArea(feesDetails);
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(fetchFeesDetailsBtn, "FETCH FEES DETAILS");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(fetchFeesDetailsBtn, "FETCH FEES DETAILS");
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleGetStudents() {
        GradeCaching selectedGrade = searchPaymentClassSelectionBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) return;

        UtilityMethods.showSuccess(notification, "Fetching students");
        String gradeId = selectedGrade.getLevelId();

        apiUtilityClass.loadClassStudents(gradeId, staffId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        searchPaymentStudentSelectionBox.getItems().clear();
                        searchPaymentStudentSelectionBox.getItems().addAll(UtilityMethods.studentsHolder);
                        searchPaymentStudentSelectionBox.setDisable(false);

                        UtilityMethods.showSuccess(notification, "Students fetched successfully");
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleFetchPayment() {
        if (searchPaymentStudentSelectionBox.isDisable()) {
            UtilityMethods.showError(notification, "Please select grade to load students");
            return;
        }

        StudentsHolder selectedStudent = searchPaymentStudentSelectionBox.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            UtilityMethods.showError(notification, "Please select a student");
            return;
        }

        SemesterCaching selectedSemester = searchPaymentSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            UtilityMethods.showError(notification, "Please select a Term");
            return;
        }

        UtilityMethods.processingButton(fetchPaymentDetailsBtn);
        apiUtilityClass.fetchPaymentRecord(selectedStudent.getStudentId(), selectedSemester.getSemesterId(),
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(fetchPaymentDetailsBtn, "FETCH PAYMENT RECORDS");
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);

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
                        UtilityMethods.notProcessingButton(fetchPaymentDetailsBtn, "FETCH PAYMENT RECORDS");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(fetchPaymentDetailsBtn, "FETCH PAYMENT RECORDS");
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleUpdatePayment() {
        String studentId = updatePaymentStudentIdField.getText().trim();
        String transactionId = transactionIdLabel.getText().trim();

        SemesterCaching selectedSemester = updatePaymentSemesterComboBox.getSelectionModel().getSelectedItem();
        if (selectedSemester == null) {
            UtilityMethods.showError(notification, "Please select a Term");
            return;
        }

        String amount = updatePaymentAmountField.getText().trim();
        if (amount.isEmpty()) {
            UtilityMethods.showError(notification, "Please enter amount");
            return;
        }

        String paidBy = updatePaymentPayerNameField.getText().trim();
        if (paidBy.isEmpty()) {
            UtilityMethods.showError(notification, "Please enter payee name");
            return;
        }

        String contact = updatePaymentPayerPhoneField.getText().trim();
        if (contact.length() < 10) {
            UtilityMethods.showError(notification, "Please enter valid contact number");
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

        UtilityMethods.processingButton(updateFeesPaymentBtn);
        apiUtilityClass.updatePaymentDetails(update,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(updateFeesPaymentBtn, "SAVE CHANGES");
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                        paymentDetailsArea.setVisible(false);
                        paymentDetailsArea.setManaged(false);

                        paymentRecordsTableView.refresh();
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(updateFeesPaymentBtn, "SAVE CHANGES");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(updateFeesPaymentBtn, "SAVE CHANGES");
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
                    });
                });
    }

    @FXML
    public void handleDeletePayment() {
        String transactionId = transactionIdLabel.getText().trim();
        UtilityMethods.processingButton(deleteFeesPaymentBtn);

        apiUtilityClass.deletePaymentRecord(transactionId,
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(deleteFeesPaymentBtn, "DELETE RECORD");
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                        paymentDetailsArea.setVisible(false);
                        paymentDetailsArea.setManaged(false);

                        updatePaymentsTableAfterDelete(studentPaymentRecords, transactionId);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(deleteFeesPaymentBtn, "DELETE RECORD");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(deleteFeesPaymentBtn, "DELETE RECORD");
                        UtilityMethods.showError(notification, "Network Error! Check your network connection and try again");
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
            UtilityMethods.showError(notification, "Please select a Semester");
            return;
        }

        GradeCaching selectedGrade = gradePaymentsReportClassComboBox.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            UtilityMethods.showError(notification, "Please select a Grade");
            return;
        }

        UtilityMethods.processingButton(gradePaymentsReportBtn);
        apiUtilityClass.fetchGradeFeesRecords(selectedGrade.getLevelId(), selectedSemester.getSemesterId(),
                () -> {
            //On Success
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(gradePaymentsReportBtn, "FETCH PAYMENTS REPORT");
                        populateGradePaymentsReportResponsePanel(gradeFeesReport,
                                selectedGrade, selectedSemester.getSemesterName());
                        UtilityMethods.showSuccess(notification, apiUtilityClass.successMessageContainer);
                    });
                },
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(gradePaymentsReportBtn, "FETCH PAYMENTS REPORT");
                        UtilityMethods.showError(notification, apiUtilityClass.errorMessageContainer);
                    });
                },
                () -> {
            //On Network Issue
                    Platform.runLater(() -> {
                        UtilityMethods.notProcessingButton(gradePaymentsReportBtn, "FETCH PAYMENTS REPORT");
                        UtilityMethods.showError(notification, "Network Error! Please check your network connection");
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

        List<SemesterCaching> cached = UtilityMethods.semesterCachingManager.getCachedSemesters();
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
        List<SemesterCaching> cachedSemesters = UtilityMethods.semesterCachingManager.getCachedSemesters();
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

        List<GradeCaching> cachedGrades = UtilityMethods.gradeCacheManager.getGradesCache();
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
        manageFeesPaymentPayment.setVisible(false);
        manageFeesPaymentPayment.setManaged(false);
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

        Double totalFees = UtilityMethods.totalSemesterFeesFromBackend;
        Double paidFees = UtilityMethods.feesPaidFromBackend;
        Double outstanding = totalFees - paidFees;

        totalFeesLabel.setText(String.format("%.2f", totalFees));
        amountPaidLabel.setText(String.format("%.2f", paidFees));
        outstandingLabel.setText(String.format("%.2f", outstanding));

        double progress = paidFees/totalFees;
        percentageLabel.setText(String.format("%.2f", progress * 100) + "%");
        progressBar.setProgress(progress);

        List<RecentPaymentTable> recentPaymentTransactions = UtilityMethods.feesCacheManager.getRecentPaymentCache();
        if (recentPaymentTransactions != null) {
            recentPaymentsTable.getItems().setAll(recentPaymentTransactions);
        }
    }

    private void loadAccountantInfo() {
        versionLabel.setText(apiUtilityClass.applicationVersion);
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
