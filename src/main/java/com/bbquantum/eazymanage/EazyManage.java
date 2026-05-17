package com.bbquantum.eazymanage;

import com.bbquantum.eazymanage.Controller.AuthController;
import com.bbquantum.eazymanage.Utitity.SessionManager;
import com.bbquantum.eazymanage.Utitity.ThemeManager;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class EazyManage extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        String token = SessionManager.getToken();

        if (!token.isEmpty() && !SessionManager.isTokenExpired()) {
            String username = SessionManager.getUserName();
            String roles = SessionManager.getRole();
            String staffId = SessionManager.getUserId();
            String organizationName = SessionManager.getOrganization();

            String[] roleArray = roles.split(",");

            if (roleArray.length > 1) {
                openRoleSelectionBox(stage);
            } else {
                String role = roleArray[0];
                openRolePage(stage, role, staffId, organizationName, username);
            }

        } else {

            FXMLLoader fxmlLoader =
                    new FXMLLoader(EazyManage.class.getResource("Authentication/auth-container.fxml"));

            Scene scene = new Scene(fxmlLoader.load());

            stage.setTitle("EAZY MANAGE - Authentication");
            stage.getIcons().add(
                    new Image(getClass().getResourceAsStream(
                            "Assets/EazyManage.png"
                    ))
            );
            stage.setScene(scene);
            ThemeManager.getInstance().register(scene);

            stage.sizeToScene();

            stage.centerOnScreen();
            stage.setResizable(false); // keep auth UI clean

        }

        stage.show();
    }

    private void openRolePage(Stage stage, String role, String staffId, String organizationName, String username) throws IOException {
        String fxmlFile = "";
        int width = 1200;
        int height = 800;

        switch (role) {
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

        UtilityClass.findController(role, staffId, organizationName, username, loader);
        stage.setTitle("Eazy Manage");

        stage.getIcons().add(
                new Image(getClass().getResourceAsStream(
                        "Assets/EazyManage.png"
                ))
        );

        stage.setScene(scene);
        ThemeManager.getInstance().register(scene);
        stage.setMaximized(true);
    }

    private void openRoleSelectionBox(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/SwitchRole/SwitchRole.fxml"));
        Scene scene = new Scene(loader.load());

        stage.setTitle("EazyManage - User Roles");
        stage.setScene(scene);
        ThemeManager.getInstance().register(scene);
        stage.sizeToScene();
        stage.setResizable(false);
        stage.centerOnScreen();
    }


    public static void main(String[] args) {
        launch();
    }
}
