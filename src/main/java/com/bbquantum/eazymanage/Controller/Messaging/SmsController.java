package com.bbquantum.eazymanage.Controller.Messaging;

import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.DTO.Messaging.BroadcastSmsRequest;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Services.MessagingService;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.List;

public class SmsController {

    @FXML private CheckBox allClassesCheck;
    @FXML private Label classesSelectedLabel;
    @FXML private FlowPane classesRow;
    @FXML private TextArea smsTextField;
    @FXML private Label smsCounterLabel;

    // ---- Footer
    @FXML private Label  statusLabel;
    @FXML private Button clearButton;
    @FXML private Button sendButton;

    private String staffId;
    @SuppressWarnings("unused")
    private String role;

    private final ApiCore apiCore = new ApiCore();
    private final MessagingService messagingService = new MessagingService(apiCore);

    /** Backing checkboxes for classes, paired with their levelId via userData. */
    private final List<CheckBox> classChecks = new ArrayList<>();

    @FXML
    private void initialize() {
        // ---- Build class checkboxes from the cache
        List<GradeCaching> grades = UtilityClass.gradeCacheManager.getGradesCache();
        if (grades != null) {
            for (GradeCaching g : grades) {
                CheckBox cb = new CheckBox(g.getLevelName());
                cb.setUserData(g.getLevelId());
                cb.getStyleClass().add("audience-check");

                cb.selectedProperty().addListener((obs, was, now) -> {
                    updateClassesCounter();
                    if (!now && allClassesCheck.isSelected()) {
                        allClassesCheck.setSelected(false);
                    }
                });
                classChecks.add(cb);
                classesRow.getChildren().add(cb);
            }
        }

        updateClassesCounter();

        // "Select all classes" master toggle
        allClassesCheck.selectedProperty().addListener((obs, was, now) -> {

            if (was != null && was != now) {
                for (CheckBox cb : classChecks) cb.setSelected(now);
            }
        });

        smsTextField.textProperty().addListener((obs, was, now) -> updateSmsCounter());
        updateSmsCounter();
    }

    public void attach(String staffId, String role) {
        this.staffId = staffId;
        this.role = role;
    }

    @FXML
    private void onSend() {
        List<String> selectedClassIds = pickSelectedValues(classChecks);

        if (selectedClassIds.isEmpty()) {
            setStatus("Pick at least one class.");
            return;
        }

        String sms     = trim(smsTextField.getText());
        if (sms.isEmpty()) {
            setStatus("Fill in the SMS message.");
            return;
        }

        BroadcastSmsRequest request = new BroadcastSmsRequest();
        request.setSmsText(sms);
        request.setAudience(selectedClassIds);

        sendButton.setDisable(true);
        clearButton.setDisable(true);
        setStatus("Sending…");

        messagingService.broadcastSms(staffId, request,
                response -> Platform.runLater(() -> {
                    sendButton.setDisable(false);
                    clearButton.setDisable(false);
                    setStatus(response);
                    clearForm();
                }),
                () -> Platform.runLater(() -> {
                    sendButton.setDisable(false);
                    clearButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err == null || err.isBlank() ? "Send failed." : err);
                }),
                () -> Platform.runLater(() -> {
                    sendButton.setDisable(false);
                    clearButton.setDisable(false);
                    setStatus("Network unavailable. Try again when you're back online.");
                }));
    }

    @FXML
    private void onClear() {
        clearForm();
        setStatus("");
    }

    private void updateClassesCounter() {
        long selected = classChecks.stream().filter(CheckBox::isSelected).count();
        if (classChecks.isEmpty()) {
            classesSelectedLabel.setText("");
        } else if (selected == 0) {
            classesSelectedLabel.setText("No classes selected");
        } else if (selected == classChecks.size()) {
            classesSelectedLabel.setText("All " + classChecks.size() + " classes selected");
        } else {
            classesSelectedLabel.setText(selected + " of " + classChecks.size() + " classes selected");
        }
    }

    private void updateSmsCounter() {
        String s = smsTextField.getText() == null ? "" : smsTextField.getText();
        int len = s.length();
        if (len == 0) { smsCounterLabel.setText(""); return; }
        // 160 is the single-segment GSM-7 ceiling. Beyond that the
        // backend will fan out into multiple SMS parts; we don't block
        // it, just warn.
        if (len <= 160) {
            smsCounterLabel.setText(len + " / 160 characters");
        } else {
            int parts = (int) Math.ceil(len / 153.0); // multipart SMS use 153/segment
            smsCounterLabel.setText(len + " characters — will send as " + parts + " SMS parts");
        }
    }

    /** Collect the {@code userData} of every selected checkbox in a row. */
    private static List<String> pickSelectedValues(List<CheckBox> boxes) {
        List<String> out = new ArrayList<>();
        for (CheckBox cb : boxes) {
            if (cb.isSelected() && cb.getUserData() != null) {
                out.add(cb.getUserData().toString());
            }
        }
        return out;
    }

    private void clearForm() {
        for (CheckBox cb : classChecks)     cb.setSelected(false);
        allClassesCheck.setSelected(false);
        smsTextField.clear();
        updateClassesCounter();
        updateSmsCounter();
    }

    private void setStatus(String s) {
        statusLabel.setText(s == null ? "" : s);
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
