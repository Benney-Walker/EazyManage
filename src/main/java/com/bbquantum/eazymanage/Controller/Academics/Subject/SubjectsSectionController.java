package com.bbquantum.eazymanage.Controller.Academics.Subject;

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

public class SubjectsSectionController {

    @FXML private StackPane contentSlot;
    @FXML private VBox pickerView;
    @FXML private TabPane innerTabs;
    @FXML private Tab addTab;
    @FXML private Tab       findTab;

    private String role;
    private String staffId;

    @FXML
    private void initialize() {
        innerTabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == addTab  && addTab.getContent()  == null) loadAdd();
            else if (newTab == findTab && findTab.getContent() == null) loadFind();
        });
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
    }

    @FXML private void onPickAdd(MouseEvent e)  { enterWorking(addTab);  }
    @FXML private void onPickFind(MouseEvent e) { enterWorking(findTab); }

    private void enterWorking(Tab initial) {
        pickerView.setVisible(false);
        pickerView.setManaged(false);
        innerTabs.setVisible(true);
        innerTabs.setManaged(true);
        if (initial == addTab  && addTab.getContent()  == null) loadAdd();
        else if (initial == findTab && findTab.getContent() == null) loadFind();
        innerTabs.getSelectionModel().select(initial);
    }

    private void loadAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Academics/Subject/AddSubject.fxml"));
            Parent content = loader.load();
            AddSubjectController controller = loader.getController();
            controller.attach(role, staffId);
            addTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            addTab.setContent(errorContent("Could not load Add Subject", ex));
        }
    }

    private void loadFind() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Academics/Subject/FindSubject.fxml"));
            Parent content = loader.load();
            FindSubjectController controller = loader.getController();
            controller.attach(role, staffId);
            findTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            findTab.setContent(errorContent("Could not load Find Subject", ex));
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
