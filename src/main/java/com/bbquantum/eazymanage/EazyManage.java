package com.bbquantum.eazymanage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class EazyManage extends Application {
    @Override
    public void start(Stage stage) throws IOException {
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

        // 🔥 THIS IS THE KEY
        stage.sizeToScene();

        // Optional but recommended
        stage.centerOnScreen();
        stage.setResizable(false); // keep auth UI clean

        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}
