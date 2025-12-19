package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.DTO.Academics.GradeStudentsDisplayDTO;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class StaffController implements Initializable {

    // CONTROLLER OWN VARIABLES
    private UtilityClass utilityClass;
    private String institutionName;
    public String staffId;
    private String staffFullName;
    private String gradeId;
    private String gradeName;
    private String gradeSize;
    private String studentId;

    // NAVIGATION
    @FXML private Button dashboardBtn;
    @FXML private Button addStudentBtn;
    @FXML private Button addResultsBtn;
    @FXML private Button logoutButton;
    @FXML private Label staffNameLabel;
    @FXML private Label institutionalNameLabel;

    // MAIN DASHBOARD VIEW
    @FXML private StackPane contentArea;
    @FXML private ScrollPane dashboardView;
    @FXML private Label dateTimeLabel;
    @FXML private Label classNameLabel;
    @FXML private Label totalStudentsLabel;
    @FXML private Label subjectsLabel;
    @FXML private TableView<GradeStudentsDisplayDTO> studentsTable;
    @FXML private TableColumn<GradeStudentsDisplayDTO, String> studentIdCol;
    @FXML private TableColumn<GradeStudentsDisplayDTO, String> studentNameCol;
    @FXML private TableColumn<GradeStudentsDisplayDTO, String> genderCol;
    @FXML private TableColumn<GradeStudentsDisplayDTO, String> homeTownCol;
    @FXML private TableColumn<GradeStudentsDisplayDTO, String> parentNameCol;
    @FXML private TableColumn<GradeStudentsDisplayDTO, String> parentPhoneCol;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        studentIdCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentId()));

        studentNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFullName()));

        genderCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getGender()));

        homeTownCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getHomeTown()));

        parentNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getParentName()));

        parentPhoneCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getParentPhone()));

    }

    @FXML
    public void showDashboard() {
        setActiveButton(dashboardBtn);
        loadDashBoardData();
        contentArea.getChildren().clear();
        contentArea.getChildren().add(dashboardView);
        dashboardView.setVisible(true);
        dashboardView.setManaged(true);
    }

    @FXML
    public void showAddStudent(ActionEvent event) {
        setActiveButton(addStudentBtn);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/Staff/add-student.fxml"));
            Parent root = loader.load();

            // Pass staffId to AddStudentController
            AddStudentController controller = loader.getController();
            controller.setStaffId(staffId);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Student");

            // Make it modal
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(((Node) event.getSource()).getScene().getWindow());

            // Set a FIXED SIZE - this enables scrolling!
            Scene scene = new Scene(root, 700, 600);
            dialogStage.setScene(scene);

            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            // Refresh students table after adding
            utilityClass.loadGradeStudents(staffId);

        } catch (Exception e) {
            e.printStackTrace();
            utilityClass.showError("Could not open add student page. Try again");
        }
    }

    @FXML
    public void showAddResults() {
        setActiveButton(addResultsBtn);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/Staff/add-new-subject-results.fxml"));
            Node addResultContent = fxmlLoader.load();

            // Clear StackPane and add content
            contentArea.getChildren().clear();
            contentArea.getChildren().add(addResultContent);
        } catch (Exception e) {
            e.printStackTrace();
            utilityClass.showError("Could not load add result page");
        }
    }

    @FXML
    public void handleLogout() throws IOException {
        Stage staffDashboard = (Stage) logoutButton.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/Authentication/login-view.fxml"));
        Scene scene = new Scene(loader.load(), 400, 680);

        Stage loginStage = new Stage();
        loginStage.setTitle("EazyManage - Login");
        loginStage.setScene(scene);
        loginStage.setResizable(false);

        staffDashboard.close();
        loginStage.show();
    }

    @FXML
    public void refreshDashboard() {
        loadDashBoardData();
        utilityClass.loadGradeStudents(staffId);
    }

    @FXML
    public void openAddSubjectPage() {
        utilityClass.showAddSubjectDialog();
    }

    public void initializeAfterLogin() {
        utilityClass = new UtilityClass(this);
        showDashboard();
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public void setStaffFullName(String staffFullName) {
        this.staffFullName = staffFullName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
        if (classNameLabel != null) {
            classNameLabel.setText(gradeName);
        }
    }

    public void setGradeSize(String gradeSize) {
        this.gradeSize = gradeSize;
        if (totalStudentsLabel != null) {
            totalStudentsLabel.setText(gradeSize);
        }
    }

    // FIXED: Actually populate the table
    public void populateStudentsTable(List<GradeStudentsDisplayDTO> students) {
        if (studentsTable != null && students != null) {
            studentsTable.getItems().setAll(students);
        }
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    private void loadDashBoardData() {
        updateDateTime();

        if (staffId != null) {
            utilityClass.loadGradeNameAndSize(staffId);
            utilityClass.loadGradeStudents(staffId);
        }

        if (institutionName != null) {
            institutionalNameLabel.setText(institutionName);
        }
        if (staffFullName != null) {
            staffNameLabel.setText(staffFullName);
        }
        if (gradeName != null) {
            classNameLabel.setText(gradeName);
        }
        if (gradeSize != null) {
            totalStudentsLabel.setText(gradeSize);
        }
    }

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy | HH:mm:ss");
        dateTimeLabel.setText(now.format(formatter));
    }

    private void setActiveButton(Button activeButton) {
        String ACTIVE_BUTTON = "-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-padding: 15 20 15 20; -fx-background-radius: 8; -fx-cursor: hand;";
        String INACTIVE_BUTTON = "-fx-background-color: transparent; -fx-text-fill: #ecf0f1; -fx-font-size: 14px; -fx-padding: 15 20 15 20;" +
                " -fx-background-radius: 8; -fx-cursor: hand; -fx-border-color: transparent;";

        dashboardBtn.setStyle(INACTIVE_BUTTON);
        addStudentBtn.setStyle(INACTIVE_BUTTON);
        addResultsBtn.setStyle(INACTIVE_BUTTON);

        activeButton.setStyle(ACTIVE_BUTTON);
    }
}