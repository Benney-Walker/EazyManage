package com.bbquantum.eazymanage.Utitity;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class NotificationUtil {

    private static final int DISPLAY_TIME = 2000;

    public static void showSuccess(StackPane root, String message) {
        show(root, message, "#10B981"); // mint / success
    }

    public static void showError(StackPane root, String message) {
        show(root, message, "#EF4444"); // red / error
    }

    public static void showWarning(StackPane root, String message) {
        show(root, message, "#F59E0B"); // yellow / warning
    }

    private static void show(StackPane root, String message, String color) {
        Platform.runLater(() -> {
            Label toast = new Label(message);
            toast.setStyle("""
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-padding: 12 20;
                -fx-background-radius: 12;
                -fx-font-size: 13px;
                -fx-font-weight: bold;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 4);
            """.formatted(color));

            StackPane.setAlignment(toast, Pos.TOP_RIGHT);
            toast.setTranslateY(-20);
            toast.setOpacity(0);

            root.getChildren().add(toast);

            // Fade + slide in
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toast);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), toast);
            slideIn.setFromY(-20);
            slideIn.setToY(20);

            ParallelTransition showAnim = new ParallelTransition(fadeIn, slideIn);

            // Auto hide
            PauseTransition stay = new PauseTransition(Duration.millis(DISPLAY_TIME));

            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), toast);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            fadeOut.setOnFinished(e -> root.getChildren().remove(toast));

            new SequentialTransition(showAnim, stay, fadeOut).play();
        });
    }
}
