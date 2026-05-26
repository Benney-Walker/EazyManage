package com.bbquantum.eazymanage.Controller.Results;

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

public class ResultsTabController {

    @FXML private Label subtitleLabel;
    @FXML private StackPane contentSlot;

    // ---- State A: picker ----
    @FXML private VBox  pickerView;
    @FXML private HBox pickerRow;
    @FXML private VBox enterCard;
    @FXML private VBox  viewCard;

    // ---- State B: tabs ----
    @FXML private TabPane innerTabs;
    @FXML private Tab enterTab;
    @FXML private Tab     viewTab;

    // ---- identity
    private String role;
    private String staffId;

    // ---- lazy-loaded inner controllers
    private EnterScoresController enterController;
    private ViewResultsController viewController;

    @FXML
    private void initialize() {
        innerTabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == enterTab && enterTab.getContent() == null) {
                loadEnterContent();
            } else if (newTab == viewTab && viewTab.getContent() == null) {
                loadViewContent();
            }
        });
    }

    /**
     * Inject identity. Called by AppShellController right after FXML load.
     * Role filtering is applied here so the picker/tab layout matches the
     * user's permissions before they see anything.
     */
    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
        applyRoleFilter();
    }

    /** Hide entry surfaces the user isn't allowed to use. */
    private void applyRoleFilter() {
        boolean canEnter = canEnterScores(role);
        if (!canEnter) {
            // Hide the Enter Scores card so the picker shows only View.
            enterCard.setVisible(false);
            enterCard.setManaged(false);
            // Drop the Enter Scores tab entirely. Once removed it's not
            // accessible even by URL/keyboard, and the View tab becomes
            // the only selectable one.
            innerTabs.getTabs().remove(enterTab);
        }
    }

    private static boolean canEnterScores(String role) {
        return "TEACHING_STAFF".equals(role) || "ADMINISTRATOR".equals(role);
    }

    // ====================================================================
    // Picker handlers
    // ====================================================================

    @FXML
    private void onPickEnter(MouseEvent e) {
        if (!canEnterScores(role)) return;     // defensive — card should be hidden
        enterWorkingState(enterTab);
    }

    @FXML
    private void onPickView(MouseEvent e) {
        enterWorkingState(viewTab);
    }

    private void enterWorkingState(Tab initialTab) {
        pickerView.setVisible(false);
        pickerView.setManaged(false);

        innerTabs.setVisible(true);
        innerTabs.setManaged(true);

        // For the very first entry, the selection listener won't fire if
        // the initial tab is already the selected one — load it directly.
        if (initialTab == enterTab && enterTab.getContent() == null) {
            loadEnterContent();
        } else if (initialTab == viewTab && viewTab.getContent() == null) {
            loadViewContent();
        }
        innerTabs.getSelectionModel().select(initialTab);
    }

    // ====================================================================
    // Lazy loaders
    // ====================================================================

    private void loadEnterContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Results/EnterScores.fxml"));
            Parent content = loader.load();
            enterController = loader.getController();
            enterController.attach(role, staffId);
            enterTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            enterTab.setContent(errorContent("Could not load Enter Scores", ex));
        }
    }

    private void loadViewContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Results/ViewResults.fxml"));
            Parent content = loader.load();
            viewController = loader.getController();
            viewController.attach(role, staffId);
            viewTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            viewTab.setContent(errorContent("Could not load View Results", ex));
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
