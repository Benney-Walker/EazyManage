package com.bbquantum.eazymanage.Controller.Finance;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class FinanceTabController {

    @FXML private StackPane contentSlot;
    @FXML private VBox pickerView;

    @FXML private TabPane innerTabs;
    @FXML private Tab feesTab;
    @FXML private Tab paymentsTab;
    @FXML private Tab classReportTab;
    @FXML private Tab studentReportTab;

    private String role;
    private String staffId;

    @FXML
    private void initialize() {
        innerTabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if      (newTab == feesTab && feesTab.getContent() == null)
                loadFees();
            else if (newTab == paymentsTab && paymentsTab.getContent() == null)
                loadPayments();
            else if (newTab == classReportTab && classReportTab.getContent() == null)
                loadClassReport();
            else if (newTab == studentReportTab  && studentReportTab.getContent() == null)
                loadStudentReport();
        });
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
    }

    @FXML private void onPickFees(MouseEvent e) {
        enterWorking(feesTab);
    }

    @FXML private void onPickPayments(MouseEvent e) {
        enterWorking(paymentsTab);
    }

    @FXML private void onPickClassReport(MouseEvent e) {
        enterWorking(classReportTab);
    }

    @FXML private void onPickStudentReport(MouseEvent e) {
        enterWorking(studentReportTab);
    }

    private void enterWorking(Tab initial) {
        pickerView.setVisible(false);
        pickerView.setManaged(false);
        innerTabs.setVisible(true);
        innerTabs.setManaged(true);

        if      (initial == feesTab          && feesTab.getContent()          == null) loadFees();
        else if (initial == paymentsTab      && paymentsTab.getContent()      == null) loadPayments();
        else if (initial == classReportTab   && classReportTab.getContent()   == null) loadClassReport();
        else if (initial == studentReportTab && studentReportTab.getContent() == null) loadStudentReport();
        innerTabs.getSelectionModel().select(initial);
    }

    // ====================================================================
    // Lazy loaders
    // ====================================================================

    private void loadFees() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Finance/FeesSetup.fxml"));
            Parent content = loader.load();
            FeesSetupController controller = loader.getController();
            controller.attach(role, staffId);
            feesTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            feesTab.setContent(errorContent("Could not load Fees Setup", ex));
        }
    }

    private void loadPayments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Finance/RecordPayments.fxml"));
            Parent content = loader.load();
            RecordPaymentsController controller = loader.getController();
            controller.attach(role, staffId);
            paymentsTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            paymentsTab.setContent(errorContent("Could not load Record Payments", ex));
        }
    }

    private void loadClassReport() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Finance/ClassPayments.fxml"));
            Parent content = loader.load();
            ClassPaymentsController controller = loader.getController();
            controller.attach(role, staffId);
            classReportTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            classReportTab.setContent(errorContent("Could not load Class Payments", ex));
        }
    }

    private void loadStudentReport() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Finance/StudentPayments.fxml"));
            Parent content = loader.load();
            StudentPaymentsController controller = loader.getController();
            controller.attach(role, staffId);
            studentReportTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            studentReportTab.setContent(errorContent("Could not load Student Payments", ex));
        }
    }

    private Node errorContent(String title, Throwable ex) {
        VBox box = new VBox(8);
        box.setStyle("-fx-padding: 24;");
        Label t = new Label(title); t.getStyleClass().add("page-title");
        Label d = new Label(ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage());
        d.getStyleClass().add("page-subtitle");
        box.getChildren().addAll(t, d);
        return box;
    }
}
