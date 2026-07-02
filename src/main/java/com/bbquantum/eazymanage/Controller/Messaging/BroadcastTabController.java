package com.bbquantum.eazymanage.Controller.Messaging;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class BroadcastTabController {

    @FXML private Label subtitleLabel;
    @FXML private StackPane contentSlot;

    // State A: picker
    @FXML private VBox pickerView;
    @FXML private HBox pickerRow;
    @FXML private VBox  staffCard;
    @FXML private VBox  guardianCard;
    @FXML private Label findCardTitle;
    @FXML private Label findCardSubtitle;

    // State B: tabs
    @FXML private TabPane innerTabs;
    @FXML private Tab     staffTab;
    @FXML private Tab     guardianTab;

    // identity
    private String role;
    private String staffId;

    private SmsController smsController;
    private EmailController emailController;

    @FXML
    private void initialize() {
        innerTabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == staffTab    && staffTab.getContent()    == null) loadStaffContent();
            else if (newTab == guardianTab   && guardianTab.getContent()   == null) loadGuardianContent();
        });
    }

    public void attach(String staffId, String role) {
        this.role    = role;
        this.staffId = staffId;
    }

    private static boolean canBroadcastInfo(String role) {
        return "PRINCIPAL".equals(role) || "ADMINISTRATOR".equals(role);
    }

    @FXML
    private void onPickStaff(MouseEvent e) {
        if (!canBroadcastInfo(role)) return;     // defensive — card should be hidden
        enterWorkingState(staffTab);
    }

    @FXML
    private void onPickGuardian(MouseEvent e) {
        if (!canBroadcastInfo(role)) return;     // defensive — card should be hidden
        enterWorkingState(guardianTab);
    }

    private void enterWorkingState(Tab initialTab) {
        pickerView.setVisible(false);
        pickerView.setManaged(false);

        innerTabs.setVisible(true);
        innerTabs.setManaged(true);

        if (initialTab == staffTab && staffTab.getContent() == null) loadStaffContent();
        else if (initialTab == guardianTab   && guardianTab.getContent()   == null) loadGuardianContent();

        innerTabs.getSelectionModel().select(initialTab);
    }

    private void loadStaffContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Messaging/emails.fxml"));
            Parent content = loader.load();
            emailController = loader.getController();
            emailController.attach(staffId, role);
            staffTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            staffTab.setContent(errorContent("Could not load Add Student", ex));
        }
    }

    private void loadGuardianContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Messaging/sms.fxml"));
            Parent content = loader.load();
            smsController = loader.getController();
            smsController.attach(staffId, role);
            guardianTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            guardianTab.setContent(errorContent("Could not load Add Student", ex));
        }
    }

    private Node errorContent(String title, Throwable ex) {
        VBox box = new VBox(8);
        box.setStyle("-fx-padding: 24;");
        Label t = new Label(title);
        t.getStyleClass().add("page-title");
        Label d = new Label(ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage());
        d.getStyleClass().add("page-subtitle");
        box.getChildren().addAll(t, d);
        return box;
    }
}
