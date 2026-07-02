/*package com.bbquantum.eazymanage.Controller.Messaging;

import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.DTO.Messaging.BroadcastSmsRequest;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Services.MessagingService;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BroadcastsController {

    // ---- Audience
    @FXML private FlowPane staffRolesRow;
    @FXML private FlowPane classesRow;
    @FXML private CheckBox allClassesCheck;
    @FXML private Label classesSelectedLabel;

    // ---- Message
    @FXML private TextField emailSubjectField;
    @FXML private TextArea emailBodyField;
    @FXML private TextArea  smsTextField;
    @FXML private Label     smsCounterLabel;

    // ---- Footer
    @FXML private Label  statusLabel;
    @FXML private Button clearButton;
    @FXML private Button sendButton;

    private String staffId;
    @SuppressWarnings("unused")
    private String role;

    private final ApiCore         apiCore         = new ApiCore();
    private final MessagingService messagingService = new MessagingService(apiCore);

    private static final Map<String, String> STAFF_ROLE_LABELS = new LinkedHashMap<>();
    static {
        STAFF_ROLE_LABELS.put("Principal",       "PRINCIPAL");
        STAFF_ROLE_LABELS.put("Administrator",   "ADMINISTRATOR");
        STAFF_ROLE_LABELS.put("Accountant",      "ACCOUNTANT");
        STAFF_ROLE_LABELS.put("Teaching staff",  "TEACHING_STAFF");
        STAFF_ROLE_LABELS.put("General staff",   "GENERAL_STAFF");
    }


    private final List<CheckBox> staffRoleChecks = new ArrayList<>();

    private final List<CheckBox> classChecks = new ArrayList<>();

    @FXML
    private void initialize() {
        // ---- Build staff-role checkboxes
        for (Map.Entry<String, String> entry : STAFF_ROLE_LABELS.entrySet()) {
            CheckBox cb = new CheckBox(entry.getKey());
            cb.setUserData(entry.getValue());
            cb.getStyleClass().add("audience-check");
            staffRoleChecks.add(cb);
            staffRolesRow.getChildren().add(cb);
        }

        // ---- Build class checkboxes from the cache
        List<GradeCaching> grades = UtilityClass.gradeCacheManager.getGradesCache();
        if (grades != null) {
            for (GradeCaching g : grades) {
                CheckBox cb = new CheckBox(g.getLevelName());
                cb.setUserData(g.getLevelId());
                cb.getStyleClass().add("audience-check");
                // Update the counter whenever a class toggles; also flip
                // the "Select all classes" master back to false if the
                // user manually deselects one (intuitive feedback).
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


    public void setIdentity(String staffId, String role) {
        this.staffId = staffId;
        this.role    = role;
    }

    // ====================================================================
    // Send
    // ====================================================================

    @FXML
    private void onSend() {
        List<String> selectedRoles = pickSelectedValues(staffRoleChecks);
        List<String> selectedClassIds = pickSelectedValues(classChecks);

        if (selectedRoles.isEmpty() && selectedClassIds.isEmpty()) {
            setStatus("Pick at least one staff role or one class.");
            return;
        }

        String subject = trim(emailSubjectField.getText());
        String body    = trim(emailBodyField.getText());
        String sms     = trim(smsTextField.getText());

        if (subject.isEmpty() && body.isEmpty() && sms.isEmpty()) {
            setStatus("Fill in at least the email or the SMS.");
            return;
        }
        // If either email field is filled, both should be — a subject
        // without a body or vice versa would land oddly in inboxes.
        if (!subject.isEmpty() && !body.isEmpty()) {
            setStatus("Email subject and body must both be filled, or both left blank.");
            return;
        }

        Audience audience = new Audience(
                selectedRoles.isEmpty()    ? null : selectedRoles,
                selectedClassIds.isEmpty() ? null : selectedClassIds);

        BroadcastSmsRequest request = new BroadcastSmsRequest(
                staffId,
                audience,
                subject.isEmpty() ? null : subject,
                body.isEmpty()    ? null : body,
                sms.isEmpty()     ? null : sms);

        sendButton.setDisable(true);
        clearButton.setDisable(true);
        setStatus("Sending…");

        messagingService.sendBroadcast(staffId, request,
                response -> Platform.runLater(() -> {
                    sendButton.setDisable(false);
                    clearButton.setDisable(false);
                    int email = response.getEmailRecipients();
                    int sms2  = response.getSmsRecipients();
                    int skip  = response.getSkipped();
                    StringBuilder s = new StringBuilder("Sent — email: ")
                            .append(email).append(", SMS: ").append(sms2);
                    if (skip > 0) s.append(" (").append(skip).append(" skipped: no contact info)");
                    setStatus(s.toString());
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

    // ====================================================================
    // Helpers
    // ====================================================================


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
        for (CheckBox cb : staffRoleChecks) cb.setSelected(false);
        for (CheckBox cb : classChecks)     cb.setSelected(false);
        allClassesCheck.setSelected(false);
        emailSubjectField.clear();
        emailBodyField.clear();
        smsTextField.clear();
        updateClassesCounter();
        updateSmsCounter();
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

    private void setStatus(String s) {
        statusLabel.setText(s == null ? "" : s);
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}*/
