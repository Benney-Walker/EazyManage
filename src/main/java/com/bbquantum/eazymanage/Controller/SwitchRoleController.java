package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.Utitity.SessionManager;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class SwitchRoleController {
    //Controller own variables
    private String username;
    private String roles;
    private String staffId;
    private String organizationName;
    private String selectedRole;

    //Role Switching dialog box
    @FXML private StackPane notification;
    @FXML private VBox switchRolePanel;
    @FXML private Label staffName;
    @FXML private Button rolePrincipalBtn;
    @FXML private Button roleAdminBtn;
    @FXML private Button roleStaffBtn;
    @FXML private Button roleFinanceBtn;
    @FXML private Button roleSwitchBtn;
    @FXML private Button logoutButton;

    @FXML private Label errorMessageLabel;

    public void initialize() {
        if (SessionManager.isTokenExpired()) {
            disAblePage();
        }

        rolePrincipalBtn.setDisable(true);
        roleAdminBtn.setDisable(true);
        roleStaffBtn.setDisable(true);
        roleFinanceBtn.setDisable(true);

        getStaffCredentials();

        rolePrincipalBtn.setOnAction(event -> {
            selectedRole = rolePrincipalBtn.getText();
            setActiveRoleBtn(rolePrincipalBtn);
        });

        roleAdminBtn.setOnAction(event -> {
            selectedRole = roleAdminBtn.getText();
            setActiveRoleBtn(roleAdminBtn);
        });

        roleStaffBtn.setOnAction(event -> {
            selectedRole = roleStaffBtn.getText();
            setActiveRoleBtn(roleStaffBtn);
        });

        roleFinanceBtn.setOnAction(event -> {
            selectedRole = roleFinanceBtn.getText();
            setActiveRoleBtn(roleFinanceBtn);
        });
    }

    @FXML
    public void handleSwitchRoles() throws IOException {
        String fxmlFile = "";
        int width = 1200;
        int height = 800;

        switch (selectedRole) {
            case "PRINCIPAL":
                fxmlFile = "/com/bbquantum/eazymanage/Principal/principal-main-dashboard.fxml";
                break;

            case "ACCOUNTANT":
                fxmlFile = "/com/bbquantum/eazymanage/Accountant/accountant-dashboard.fxml";
                break;

            case "TEACHING_STAFF":
                fxmlFile = "/com/bbquantum/eazymanage/Staff/staff-dashboard.fxml";
                break;

            case "ADMINISTRATOR":
                fxmlFile = "/com/bbquantum/eazymanage/Admin/Admin-dashboard.fxml";
                break;

        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Scene scene = new Scene(loader.load(), width, height);

        UtilityClass.findController(selectedRole, staffId, organizationName, username, loader);

        Stage stage = (Stage) roleSwitchBtn.getScene().getWindow();

        Stage newStage = new Stage();
        newStage.setTitle("Eazy Manage");
        newStage.getIcons().add(
                new Image(getClass().getResourceAsStream(
                        "/com/bbquantum/eazymanage/Assets/EazyManage.png"
                ))
        );
        newStage.setScene(scene);
        newStage.setMaximized(true);

        stage.close();
        newStage.show();
    }

    @FXML
    public void handleLogout() throws IOException {
        UtilityClass.clearSession();

        Stage switchRolePage = (Stage) logoutButton.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/Authentication/auth-container.fxml"));
        Scene scene = new Scene(loader.load());

        // Create new stage for dashboard
        Stage loginStage = new Stage();
        loginStage.setTitle("EazyManage - Authentication");
        loginStage.setScene(scene);
        loginStage.sizeToScene();
        loginStage.setResizable(false);

        switchRolePage.close();
        loginStage.show();
    }

    /*----------------- UTILITY METHODS FOR CONTROLLER -------------*/

    private void getStaffCredentials() {

        username = SessionManager.getUserName();
        roles = SessionManager.getRole();
        staffId = SessionManager.getUserId();
        organizationName = SessionManager.getOrganization();

        String[] roleArray = roles.split(",");
        displayPageInfo(username, List.of(roleArray));
    }

    private void displayPageInfo(String userName, List<String> roles) {
        staffName.setText(userName);

        if (roles.contains("PRINCIPAL")) {
            rolePrincipalBtn.setDisable(false);
        }

        if (roles.contains("ACCOUNTANT")) {
            roleFinanceBtn.setDisable(false);
        }

        if (roles.contains("ADMINISTRATOR")) {
            roleAdminBtn.setDisable(false);
        }

        if (roles.contains("TEACHING_STAFF")) {
            roleStaffBtn.setDisable(false);
        }
    }

    private void disAblePage() {
        rolePrincipalBtn.setDisable(true);
        roleAdminBtn.setDisable(true);
        roleStaffBtn.setDisable(true);
        roleFinanceBtn.setDisable(true);

        roleSwitchBtn.setDisable(true);

        showError("Session Expired. Click the sign out button to open login page");
    }

    private void logOut(Button button) throws IOException {
        UtilityClass.clearSession();

        Stage  stage = (Stage) button.getScene().getWindow();

        // Load the appropriate dashboard
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/Authentication/auth-container.fxml"));
        Scene scene = new Scene(loader.load());

        // Create new stage for dashboard
        Stage loginStage = new Stage();
        loginStage.setTitle("EazyManage - Authentication");
        loginStage.setScene(scene);
        loginStage.sizeToScene();
        loginStage.setResizable(false);

        // Close login and show dashboard
        stage.close();
        loginStage.show();
    }

    private void setActiveRoleBtn(Button activeRoleBtn) {
        String ACTIVEBTN = "-fx-background-color: #4f46e5; -fx-text-fill: white;";
        String INACTIVEBTN = "-fx-background-color: transparent; -fx-text-fill: #334155;";

        rolePrincipalBtn.setStyle(INACTIVEBTN);
        roleAdminBtn.setStyle(INACTIVEBTN);
        roleStaffBtn.setStyle(INACTIVEBTN);
        roleFinanceBtn.setStyle(INACTIVEBTN);

        activeRoleBtn.setStyle(ACTIVEBTN);
    }

    private void showError(String message) {
        errorMessageLabel.setText(message);

        errorMessageLabel.setVisible(true);
        errorMessageLabel.setManaged(true);

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished( event -> {
            errorMessageLabel.setVisible(false);
            errorMessageLabel.setManaged(false);
        });

        delay.play();
    }
}
