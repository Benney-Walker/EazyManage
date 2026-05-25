package com.bbquantum.eazymanage;

import com.bbquantum.eazymanage.Controller.AuthController;
import com.bbquantum.eazymanage.Shell.AppShellController;
import com.bbquantum.eazymanage.Shell.ShellLauncher;
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
            String stored = SessionManager.getActiveRole();
            String active = (stored != null && !stored.isBlank() && contains(roleArray, stored))
                    ? stored
                    : roleArray[0];

            ShellLauncher.launch(stage, active, staffId, organizationName, username);

        } else {

            FXMLLoader fxmlLoader =
                    new FXMLLoader(EazyManage.class.getResource("Authentication/auth-container.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            ThemeManager.getInstance().register(scene);

            stage.setTitle("EAZY MANAGE - Authentication");
            stage.getIcons().add(
                    new Image(getClass().getResourceAsStream(
                            "Assets/EazyManage.png"
                    ))
            );
            stage.setScene(scene);

            stage.sizeToScene();

            stage.centerOnScreen();
            stage.setResizable(false); // keep auth UI clean

        }

        stage.show();
    }

    private void openRolePage(Stage stage, String role, String staffId,
                              String organizationName, String username) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/bbquantum/eazymanage/Shell/AppShell.fxml"));
        Scene scene = new Scene(loader.load(), 1280, 820);

        // Apply the active theme to the shell scene.
        ThemeManager.getInstance().register(scene);

        AppShellController shell = loader.getController();
        shell.setIdentity(role, staffId, organizationName, username);

        stage.setTitle("EazyManage");
        stage.getIcons().add(
                new Image(getClass().getResourceAsStream("Assets/EazyManage.png")));
        stage.setScene(scene);
        stage.setMaximized(true);

        // Boot the shell AFTER the scene is attached to the stage so the
        // notification popup can resolve its owner window correctly.
        shell.boot();
    }

    private void openRoleSelectionBox(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/bbquantum/eazymanage/SwitchRole/SwitchRole.fxml"));
        Scene scene = new Scene(loader.load());

        // Phase 1: register the role-selection scene with the theme manager.
        ThemeManager.getInstance().register(scene);

        stage.setTitle("EazyManage - User Roles");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setResizable(false);
        stage.centerOnScreen();
    }

    private static boolean contains(String[] arr, String value) {
        for (String s : arr) if (s.equals(value)) return true;
        return false;
    }

    public static void main(String[] args) {
        launch();
    }
}
