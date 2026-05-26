package com.bbquantum.eazymanage.Controller.Staff;

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

public class StaffTabController {

    @FXML private StackPane contentSlot;

    // State A
    @FXML private VBox pickerView;
    @FXML private VBox enrollCard;
    @FXML private VBox findCard;

    // State B
    @FXML private TabPane innerTabs;
    @FXML private Tab     enrollTab;
    @FXML private Tab findTab;

    private String role;
    private String staffId;
    private String organizationId;

    private EnrollStaffShellController enrollController;
    private FindStaffShellController   findController;

    @FXML
    private void initialize() {
        innerTabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == enrollTab && enrollTab.getContent() == null) loadEnrollContent();
            else if (newTab == findTab && findTab.getContent() == null) loadFindContent();
        });
    }

    /**
     * Inject identity. {@code organizationId} is needed by the enrollment
     * service so the backend knows which institution the staff belongs to;
     * the shell sources it from {@code SessionManager.getOrganization()}.
     */
    public void attach(String role, String staffId, String organizationId) {
        this.role           = role;
        this.staffId        = staffId;
        this.organizationId = organizationId;
    }

    @FXML
    private void onPickEnroll(MouseEvent e) { enterWorkingState(enrollTab); }

    @FXML
    private void onPickFind(MouseEvent e)   { enterWorkingState(findTab); }

    private void enterWorkingState(Tab initialTab) {
        pickerView.setVisible(false);
        pickerView.setManaged(false);
        innerTabs.setVisible(true);
        innerTabs.setManaged(true);

        if (initialTab == enrollTab && enrollTab.getContent() == null) loadEnrollContent();
        else if (initialTab == findTab && findTab.getContent() == null) loadFindContent();
        innerTabs.getSelectionModel().select(initialTab);
    }

    // ====================================================================
    // Lazy loaders
    // ====================================================================

    private void loadEnrollContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Staff/EnrollStaff.fxml"));
            Parent content = loader.load();
            enrollController = loader.getController();
            enrollController.attach(role, staffId, organizationId);
            enrollTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            enrollTab.setContent(errorContent("Could not load Enroll Staff", ex));
        }
    }

    private void loadFindContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Staff/FindStaff.fxml"));
            Parent content = loader.load();
            findController = loader.getController();
            findController.attach(role, staffId);
            findTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            findTab.setContent(errorContent("Could not load Find Staff", ex));
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
