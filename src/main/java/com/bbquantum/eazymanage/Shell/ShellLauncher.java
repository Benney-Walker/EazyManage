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
        if (previous != null) {
            ThemeManager.getInstance().unregister(previous);
        }

        FXMLLoader loader = new FXMLLoader(
                ShellLauncher.class.getResource("/com/bbquantum/eazymanage/Shell/AppShell.fxml"));
        Scene scene = new Scene(loader.load(), 1280, 820);
        ThemeManager.getInstance().register(scene);

        AppShellController shell = loader.getController();
        shell.setIdentity(activeRole, staffId, organizationName, username);

        stage.setTitle("EazyManage");
        try {
            stage.getIcons().setAll(new Image(
                    ShellLauncher.class.getResourceAsStream(
                            "/com/bbquantum/eazymanage/Assets/EazyManage.png")));
        } catch (Exception ignored) {
            //Exception ignored
        }
        stage.setScene(scene);
        stage.setMaximized(true);

        shell.boot();
    }
}
