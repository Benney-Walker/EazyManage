package com.bbquantum.eazymanage.Controller.Students;

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

public class StudentsTabController {

    @FXML private Label subtitleLabel;
    @FXML private StackPane contentSlot;

    // State A: picker
    @FXML private VBox pickerView;
    @FXML private HBox pickerRow;
    @FXML private VBox  addCard;
    @FXML private VBox  findCard;
    @FXML private Label findCardTitle;
    @FXML private Label findCardSubtitle;
    @FXML private VBox  browseCard;

    // State B: tabs
    @FXML private TabPane innerTabs;
    @FXML private Tab addTab;
    @FXML private Tab     findTab;
    @FXML private Tab     browseTab;

    // identity
    private String role;
    private String staffId;

    // lazy-loaded inner controllers
    private AddStudentController     addController;
    private FindStudentController    findController;
    private BrowseStudentsController browseController;

    @FXML
    private void initialize() {
        innerTabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == addTab    && addTab.getContent()    == null) loadAddContent();
            else if (newTab == findTab   && findTab.getContent()   == null) loadFindContent();
            else if (newTab == browseTab && browseTab.getContent() == null) loadBrowseContent();
        });
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
        applyRoleFilter();
    }

    private void applyRoleFilter() {
        boolean canEdit = canEditStudents(role);
        if (!canEdit) {
            // Hide the Add card; remove the Add tab so the user can never
            // reach it via keyboard / focus traversal.
            addCard.setVisible(false);
            addCard.setManaged(false);
            innerTabs.getTabs().remove(addTab);

            // Soften the Find card copy: teaching staff can search but not
            // save edits, so calling it "Find & Update" overpromises.
            findCardTitle.setText("Find Student");
            findCardSubtitle.setText("Search a student by ID to view their personal details.");
            findTab.setText("Find Student");
        }
    }

    private static boolean canEditStudents(String role) {
        return "PRINCIPAL".equals(role) || "ADMINISTRATOR".equals(role);
    }

    // ====================================================================
    // Picker handlers
    // ====================================================================

    @FXML
    private void onPickAdd(MouseEvent e) {
        if (!canEditStudents(role)) return;     // defensive — card should be hidden
        enterWorkingState(addTab);
    }

    @FXML
    private void onPickFind(MouseEvent e) {
        enterWorkingState(findTab);
    }

    @FXML
    private void onPickBrowse(MouseEvent e) {
        enterWorkingState(browseTab);
    }

    private void enterWorkingState(Tab initialTab) {
        pickerView.setVisible(false);
        pickerView.setManaged(false);

        innerTabs.setVisible(true);
        innerTabs.setManaged(true);

        if (initialTab == addTab    && addTab.getContent()    == null) loadAddContent();
        else if (initialTab == findTab   && findTab.getContent()   == null) loadFindContent();
        else if (initialTab == browseTab && browseTab.getContent() == null) loadBrowseContent();
        innerTabs.getSelectionModel().select(initialTab);
    }

    // ====================================================================
    // Lazy loaders
    // ====================================================================

    private void loadAddContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Students/AddStudent.fxml"));
            Parent content = loader.load();
            addController = loader.getController();
            addController.attach(role, staffId);
            addTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            addTab.setContent(errorContent("Could not load Add Student", ex));
        }
    }

    private void loadFindContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Students/FindStudent.fxml"));
            Parent content = loader.load();
            findController = loader.getController();
            findController.attach(role, staffId);
            findTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            findTab.setContent(errorContent("Could not load Find Student", ex));
        }
    }

    private void loadBrowseContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Students/BrowseStudents.fxml"));
            Parent content = loader.load();
            browseController = loader.getController();
            browseController.attach(role, staffId);
            browseTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            browseTab.setContent(errorContent("Could not load Browse Students", ex));
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
