package com.bbquantum.eazymanage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class EazyManage extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EazyManage.class.getResource("Authentication/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 680);
        stage.setTitle("EazyManage - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
