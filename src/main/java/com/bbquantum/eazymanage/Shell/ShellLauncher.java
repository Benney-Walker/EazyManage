package com.bbquantum.eazymanage.Shell;

import com.bbquantum.eazymanage.Utitity.SessionManager;
import com.bbquantum.eazymanage.Utitity.ThemeManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class ShellLauncher {

    private ShellLauncher() {}

    public static void launch(Stage stage, String activeRole, String staffId,
                              String organizationName, String username) throws IOException {

        SessionManager.setActiveRole(activeRole);

        Scene previous = stage.getScene();
        double  previousWidth     = previous == null ? 1280 : Math.max(previous.getWidth(),  800);
        double  previousHeight    = previous == null ? 820  : Math.max(previous.getHeight(), 600);
        boolean wasMaximized      = stage.isMaximized();
        if (previous != null) {
            ThemeManager.getInstance().unregister(previous);
        }

        FXMLLoader loader = new FXMLLoader(
                ShellLauncher.class.getResource("/com/bbquantum/eazymanage/Shell/AppShell.fxml"));
        Scene scene = new Scene(loader.load(), previousWidth, previousHeight);
        ThemeManager.getInstance().register(scene);

        AppShellController shell = loader.getController();
        shell.setIdentity(activeRole, staffId, organizationName, username);

        stage.setTitle("EazyManage");
        try {
            stage.getIcons().setAll(new Image(
                    ShellLauncher.class.getResourceAsStream(
                            "/com/bbquantum/eazymanage/Icons/EazyManage.png")));
        } catch (Exception ignored) {
            //Exception ignored
        }
        stage.setScene(scene);

        if (wasMaximized) {
            stage.setMaximized(false);
        }
        stage.setMaximized(true);
        stage.setResizable(false);

        shell.boot();
    }
}
