package com.bbquantum.eazymanage.Controller.Academics;

import com.bbquantum.eazymanage.Controller.Academics.Classes.ClassesSectionController;
import com.bbquantum.eazymanage.Controller.Academics.Grade.GradingSectionController;
import com.bbquantum.eazymanage.Controller.Academics.Holiday.HolidaysSectionController;
import com.bbquantum.eazymanage.Controller.Academics.Semester.SemestersSectionController;
import com.bbquantum.eazymanage.Controller.Academics.Subject.SubjectsSectionController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

public class AcademicSetupTabController {

    @FXML private TabPane sectionTabs;
    @FXML private Tab classesTab;
    @FXML private Tab semestersTab;
    @FXML private Tab subjectsTab;
    @FXML private Tab gradingTab;
    @FXML private Tab holidaysTab;

    private String role;
    private String staffId;

    @FXML
    private void initialize() {
        sectionTabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == classesTab && classesTab.getContent() == null) loadClasses();

            else if (newTab == semestersTab && semestersTab.getContent() == null) loadSemesters();

            else if (newTab == subjectsTab  && subjectsTab.getContent()  == null) loadSubjects();

            else if (newTab == gradingTab && gradingTab.getContent() == null) loadGradingSetup();

            else if (newTab == holidaysTab  && holidaysTab.getContent()  == null) loadHolidays();
        });
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
        // Eagerly load the first section so the user sees content rather
        // than an empty tab the moment they open Academic Setup. The other
        // two stay lazy.
        if (classesTab.getContent() == null) loadClasses();
    }

    // ====================================================================
    // Lazy loaders
    // ====================================================================

    private void loadClasses() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Academics/Classes/ClassesSection.fxml"));
            Parent content = loader.load();
            ClassesSectionController controller = loader.getController();
            controller.attach(role, staffId);
            classesTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            classesTab.setContent(errorContent("Could not load Classes", ex));
        }
    }

    private void loadSemesters() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Academics/Semester/SemesterSection.fxml"));
            Parent content = loader.load();
            SemestersSectionController controller = loader.getController();
            controller.attach(role, staffId);
            semestersTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            semestersTab.setContent(errorContent("Could not load Semesters", ex));
        }
    }

    private void loadSubjects() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Academics/Subject/SubjectsSection.fxml"));
            Parent content = loader.load();
            SubjectsSectionController controller = loader.getController();
            controller.attach(role, staffId);
            subjectsTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            subjectsTab.setContent(errorContent("Could not load Subjects", ex));
        }
    }

    private void loadGradingSetup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Academics/Grades/GradingSection.fxml"));
            Parent content = loader.load();
            GradingSectionController controller = loader.getController();
            controller.attach(role, staffId);
            gradingTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            gradingTab.setContent(errorContent("Could not load grading setup", ex));
        }
    }

    private void loadHolidays() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Academics/Holiday/HolidaySection.fxml"));
            Parent content = loader.load();
            HolidaysSectionController controller = loader.getController();
            controller.attach(role, staffId);
            holidaysTab.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
            holidaysTab.setContent(errorContent("Could not load Holidays", ex));
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
