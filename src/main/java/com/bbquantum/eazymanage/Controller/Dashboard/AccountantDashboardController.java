package com.bbquantum.eazymanage.Controller.Dashboard;

import com.bbquantum.eazymanage.DTO.Finance.RecentPaymentTable;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Services.DashboardService;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AccountantDashboardController {

    @FXML private Label greetingLabel;
    @FXML private Label       subtitleLabel;
    @FXML private Label       statusLabel;
    @FXML private Button refreshButton;

    @FXML private Label       totalFeesLabel;
    @FXML private Label       amountPaidLabel;
    @FXML private Label       amountPaidHintLabel;
    @FXML private Label       outstandingLabel;
    @FXML private Label       percentageLabel;
    @FXML private ProgressBar progressBar;

    @FXML private TableView<RecentPaymentTable> recentPaymentsTable;
    @FXML private TableColumn<RecentPaymentTable, String> paymentDateCol;
    @FXML private TableColumn<RecentPaymentTable, String> studentIdCol;
    @FXML private TableColumn<RecentPaymentTable, String> studentNameCol;
    @FXML private TableColumn<RecentPaymentTable, String> amountCol;
    @FXML private TableColumn<RecentPaymentTable, String> payerCol;
    @FXML private TableColumn<RecentPaymentTable, String> gradeCol;

    private String staffId;
    private String fullName;
    @SuppressWarnings("unused")
    private String organizationName;

    private final ApiCore apiCore          = new ApiCore();
    private final DashboardService dashboardService = new DashboardService(apiCore);
    private final StackPane toastSink = new StackPane();

    private final ObservableList<RecentPaymentTable> rows = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        recentPaymentsTable.setItems(rows);
        paymentDateCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getPaymentDate())));

        studentIdCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentId())));

        studentNameCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentNameCol())));

        amountCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getAmountCol())));

        payerCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getPayerCol())));

        gradeCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getLevelCol())));
    }

    public void attach(String staffId, String fullName, String organizationName) {
        this.staffId          = staffId;
        this.fullName         = fullName;
        this.organizationName = organizationName;

        greetingLabel.setText(timeOfDayGreeting()
                + (fullName == null ? "" : ", " + fullName.split(" ")[0]));
        subtitleLabel.setText("Collections overview — "
                + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")));

        renderFromGlobals();
        onRefresh();
    }

    @FXML
    private void onRefresh() {
        refreshButton.setDisable(true);
        setStatus("Loading…");

        UtilityClass.clearSessionForRefresh();
        dashboardService.loadFinanceDashboardData(staffId, toastSink,
                () -> Platform.runLater(() -> {
                    refreshButton.setDisable(false);
                    renderFromGlobals();
                    setStatus("Updated.");
                }));
    }

    private void renderFromGlobals() {
        double totalFees = UtilityClass.totalSemesterFeesFromBackend == null ? 0d : UtilityClass.totalSemesterFeesFromBackend;
        double feesPaid  = UtilityClass.feesPaidFromBackend          == null ? 0d : UtilityClass.feesPaidFromBackend;
        double outstanding = Math.max(0d, totalFees - feesPaid);

        totalFeesLabel.setText("GH₵ " + String.format("%.2f", totalFees));
        amountPaidLabel.setText("GH₵ " + String.format("%.2f", feesPaid));
        outstandingLabel.setText("GH₵ " + String.format("%.2f", outstanding));

        double progress = totalFees > 0 ? Math.min(1.0, feesPaid / totalFees) : 0.0;
        progressBar.setProgress(progress);
        percentageLabel.setText(String.format("%.1f", progress * 100) + "%");
        amountPaidHintLabel.setText(String.format("%.1f", progress * 100) + "% of total collected");

        List<RecentPaymentTable> recent = UtilityClass.feesCacheManager.getRecentPaymentCache();
        if (recent != null) rows.setAll(recent);
        else                rows.clear();
    }

    private void setStatus(String s) { statusLabel.setText(s == null ? "" : s); }

    private static String timeOfDayGreeting() {
        int hour = java.time.LocalTime.now().getHour();
        if (hour < 12) return "Good morning";
        if (hour < 17) return "Good afternoon";
        return "Good evening";
    }
    private static String safe(String s) { return s == null ? "" : s; }
}
