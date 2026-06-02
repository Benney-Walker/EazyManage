package com.bbquantum.eazymanage.Controller.Attendance;

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

public class AttendanceTabController {

    @FXML private Label subtitleLabel;
    @FXML private StackPane contentSlot;

    // State A
    @FXML private VBox pickerView;
    @FXML private VBox markCard;
    @FXML private VBox viewCard;

    // State B
    @FXML private TabPane innerTabs;
    @FXML private Tab     markTab;
    @FXML private Tab viewTab;

    // ---- identity injected by the shell ----
    private String role;
    private String staffId;

    // ---- lazy-loaded inner controllers ----
    private MarkAttendanceController markController;
    private ViewAttendanceController viewController;

    @FXML
    private void initialize() {
        // Inner-tab selection lazily materialises content the first time
        // the user picks that tab. This keeps cold-start of the host
        // cheap when the user only wants one of the two flows.
        innerTabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == markTab && markTab.getContent() == null) {
                loadMarkContent();
            } else if (newTab == viewTab && viewTab.getContent() == null) {
                loadViewContent();
            }
        });
    }

    /**
     * Inject identity. Must be called by {@code AppShellController.loadAttendanceScreen}
     * before the user can interact with the picker.
     */
    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
        applyRoleFilter();
    }

    /** Hide entry surfaces the user isn't allowed to use. */
    private void applyRoleFilter() {
        boolean canEnter = canMarkAttendance(role);
        boolean canView = canViewAttendance(role);
        if (!canEnter) {
            // Hide the Enter Scores card so the picker shows only View.
            markCard.setVisible(false);
            markCard.setManaged(false);
            // Drop the Enter Scores tab entirely. Once removed it's not
            // accessible even by URL/keyboard, and the View tab becomes
            // the only selectable one.
            innerTabs.getTabs().remove(markTab);
        }

        if (!canView) {
            // Hide the Enter Scores card so the picker shows only View.
            viewCard.setVisible(false);
            viewCard.setManaged(false);
            // Drop the Enter Scores tab entirely. Once removed it's not
            // accessible even by URL/keyboard, and the View tab becomes
            // the only selectable one.
            innerTabs.getTabs().remove(viewTab);
        }
    }

    private static boolean canMarkAttendance(String role) {
        return "TEACHING_STAFF".equals(role) || "ADMINISTRATOR".equals(role);
    }

    private static boolean canViewAttendance(String role) {
        return "PRINCIPAL".equals(role);
    }

    // ====================================================================
    // Picker handlers
    // ====================================================================

    @FXML
    private void onPickMark(MouseEvent e) {
        enterWorkingState(markTab);
    }

    @FXML
    private void onPickView(MouseEvent e) {
        enterWorkingState(viewTab);
    }

    /**
     * One-way transition from picker → working state. We hide the picker
     * VBox entirely (visible=false, managed=false) so layout collapses it,
     * and reveal the TabPane. Selecting {@code initialTab} triggers the
     * lazy-load path in {@link #initialize()}.
     */
    private void enterWorkingState(Tab initialTab) {
        pickerView.setVisible(false);
        pickerView.setManaged(false);

        innerTabs.setVisible(true);
        innerTabs.setManaged(true);

        // Selecting the same tab that's already selected won't fire the
        // listener, so load it explicitly here for the very first entry.
        if (initialTab == markTab && markTab.getContent() == null) {
            loadMarkContent();
        } else if (initialTab == viewTab && viewTab.getContent() == null) {
            loadViewContent();
        }
        innerTabs.getSelectionModel().select(initialTab);
    }

    // ====================================================================
    // Inner-tab lazy loaders
    // ====================================================================

    private void loadMarkContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Attendance/MarkAttendance.fxml"));
            Parent content = loader.load();
            markController = loader.getController();
            markController.attach(role, staffId);
            markTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            markTab.setContent(errorContent("Could not load Mark Attendance", ex));
        }
    }

    private void loadViewContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Attendance/ViewAttendance.fxml"));
            Parent content = loader.load();
            viewController = loader.getController();
            viewController.attach(role, staffId);
            viewTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            viewTab.setContent(errorContent("Could not load View Attendance", ex));
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
