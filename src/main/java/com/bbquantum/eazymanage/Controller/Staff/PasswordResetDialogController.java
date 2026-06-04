package com.bbquantum.eazymanage.Controller.Staff;

import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class PasswordResetDialogController {

    @FXML private Label subtitleLabel;
    @FXML private PasswordField newPasswordField;
    @FXML private TextField newPasswordPlainField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField confirmPasswordPlainField;
    @FXML private CheckBox showPasswordsCheck;
    @FXML private Label statusLabel;
    @FXML private Button cancelButton;
    @FXML private Button resetButton;

    /** Logged-in admin (actor for audit). */
    private String actorStaffId;
    /** Staff being reset (target). */
    private String targetStaffId;

    private AuthService authService;

    /** Owner stage so we can close ourselves cleanly. */
    private Stage dialogStage;

    private Consumer<String> onSuccess;

    @FXML
    private void initialize() {
        // Keep both password representations in sync so toggling visibility
        // never loses what the admin typed.
        newPasswordField.textProperty().addListener((obs, oldV, newV) -> {
            if (!newPasswordPlainField.getText().equals(newV)) {
                newPasswordPlainField.setText(newV);
            }
        });
        newPasswordPlainField.textProperty().addListener((obs, oldV, newV) -> {
            if (!newPasswordField.getText().equals(newV)) {
                newPasswordField.setText(newV);
            }
        });
        confirmPasswordField.textProperty().addListener((obs, oldV, newV) -> {
            if (!confirmPasswordPlainField.getText().equals(newV)) {
                confirmPasswordPlainField.setText(newV);
            }
        });
        confirmPasswordPlainField.textProperty().addListener((obs, oldV, newV) -> {
            if (!confirmPasswordField.getText().equals(newV)) {
                confirmPasswordField.setText(newV);
            }
        });

        // Escape cancels, Enter from anywhere triggers reset (defaultButton
        // handles Enter when something inside the form has focus already,
        // but adding the explicit filter covers the rare empty-focus case).
        Platform.runLater(() -> {
            if (newPasswordField.getScene() == null) return;
            newPasswordField.getScene().addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, ev -> {
                if (ev.getCode() == KeyCode.ESCAPE) {
                    onCancel();
                    ev.consume();
                }
            });
            newPasswordField.requestFocus();
        });
    }

    /** Called by the launcher right after FXML load. */
    public void attach(String actorStaffId, String targetStaffId, String targetDisplayName,
                       AuthService authService, Consumer<String> onSuccess) {
        this.actorStaffId  = actorStaffId;
        this.targetStaffId = targetStaffId;
        this.authService   = authService;
        this.onSuccess     = onSuccess;
        subtitleLabel.setText("Set a new password for " + targetDisplayName +
                " (" + targetStaffId + "). They'll use it on their next login.");
    }

    /** Setter so the launcher can give us a handle for self-close. */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    @FXML
    private void onToggleVisibility() {
        boolean showPlain = showPasswordsCheck.isSelected();
        // New password pair
        newPasswordField.setVisible(!showPlain);
        newPasswordField.setManaged(!showPlain);
        newPasswordPlainField.setVisible(showPlain);
        newPasswordPlainField.setManaged(showPlain);
        // Confirm password pair
        confirmPasswordField.setVisible(!showPlain);
        confirmPasswordField.setManaged(!showPlain);
        confirmPasswordPlainField.setVisible(showPlain);
        confirmPasswordPlainField.setManaged(showPlain);
    }

    @FXML
    private void onCancel() {
        if (dialogStage != null) dialogStage.close();
    }

    @FXML
    private void onReset() {
        String newPwd     = currentNewPassword();
        String confirmPwd = currentConfirmPassword();

        if (newPwd.isEmpty()) {
            setStatus("Enter a new password.");
            return;
        }
        if (newPwd.length() < 6) {
            setStatus("Password must be at least 6 characters.");
            return;
        }
        if (!newPwd.equals(confirmPwd)) {
            setStatus("Passwords do not match.");
            return;
        }

        setBusy(true);
        setStatus("Resetting…");

        authService.resetPassword(actorStaffId, targetStaffId, newPwd,
                () -> Platform.runLater(() -> {
                    String msg = ApiContext.getInstance().successMessageContainer;
                    if (onSuccess != null) onSuccess.accept(msg);
                    if (dialogStage != null) dialogStage.close();
                }),
                () -> Platform.runLater(() -> {
                    setBusy(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err);
                }),
                () -> Platform.runLater(() -> {
                    setBusy(false);
                    setStatus("Network unavailable. Try again when you're back online.");
                }));
    }

    private String currentNewPassword() {
        return (newPasswordPlainField.isVisible()
                ? newPasswordPlainField.getText()
                : newPasswordField.getText());
    }

    private String currentConfirmPassword() {
        return (confirmPasswordPlainField.isVisible()
                ? confirmPasswordPlainField.getText()
                : confirmPasswordField.getText());
    }

    private void setBusy(boolean busy) {
        resetButton.setDisable(busy);
        cancelButton.setDisable(busy);
        newPasswordField.setDisable(busy);
        newPasswordPlainField.setDisable(busy);
        confirmPasswordField.setDisable(busy);
        confirmPasswordPlainField.setDisable(busy);
        showPasswordsCheck.setDisable(busy);
    }

    private void setStatus(String s) {
        statusLabel.setText(s == null ? "" : s);
    }
}
