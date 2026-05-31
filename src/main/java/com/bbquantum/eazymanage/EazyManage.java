package com.bbquantum.eazymanage;

import com.bbquantum.eazymanage.Shell.AppShellController;
import com.bbquantum.eazymanage.Shell.ShellLauncher;
import com.bbquantum.eazymanage.Utitity.SessionManager;
import com.bbquantum.eazymanage.Utitity.ThemeManager;
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
                    new FXMLLoader(EazyManage.class.getResource("Shell/Auth/auth-container.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            ThemeManager.getInstance().register(scene);

            stage.setTitle("EAZY MANAGE - Authentication");
            stage.getIcons().add(
                    new Image(getClass().getResourceAsStream(
                            "Icons/EazyManage.png"
                    ))
            );
            stage.setScene(scene);

            stage.sizeToScene();

            stage.centerOnScreen();
            stage.setResizable(false); // keep auth UI clean

        }

        stage.show();
    }

    private static boolean contains(String[] arr, String value) {
        for (String s : arr) if (s.equals(value)) return true;
        return false;
    }

    public static void main(String[] args) {
        launch();
    }
}
