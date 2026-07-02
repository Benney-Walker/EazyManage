package com.bbquantum.eazymanage.Controller.Messaging;

import com.bbquantum.eazymanage.DTO.Messaging.BroadcastEmailRequest;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Services.MessagingService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EmailController {

    @FXML private FlowPane staffRolesRow;
    @FXML private TextField emailSubjectField;
    @FXML private TextArea emailBodyField;

    // ---- Footer
    @FXML private Label statusLabel;
    @FXML private Button clearButton;
    @FXML private Button sendButton;

    private String staffId;
    @SuppressWarnings("unused")
    private String role;

    private final ApiCore apiCore         = new ApiCore();
    private final MessagingService messagingService = new MessagingService(apiCore);

    private static final Map<String, String> STAFF_ROLE_LABELS = new LinkedHashMap<>();
    static {
        STAFF_ROLE_LABELS.put("Principal",       "PRINCIPAL");
        STAFF_ROLE_LABELS.put("Administrator",   "ADMINISTRATOR");
        STAFF_ROLE_LABELS.put("Accountant",      "ACCOUNTANT");
        STAFF_ROLE_LABELS.put("Teaching staff",  "TEACHING_STAFF");
        STAFF_ROLE_LABELS.put("General staff",   "GENERAL_STAFF");
    }

    /** Backing checkboxes for staff roles, in display order. */
    private final List<CheckBox> staffRoleChecks = new ArrayList<>();

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
    }

    public void attach(String staffId, String role) {
        this.staffId = staffId;
        this.role = role;
    }

    @FXML
    private void onSend() {
        List<String> selectedRoles = pickSelectedValues(staffRoleChecks);

        if (selectedRoles.isEmpty()) {
            setStatus("Pick at least one staff role.");
            return;
        }

        String subject = trim(emailSubjectField.getText());
        String body    = trim(emailBodyField.getText());

        if (subject.isEmpty() || body.isEmpty()) {
            setStatus("All fields must be filled.");
            return;
        }

        BroadcastEmailRequest request = new BroadcastEmailRequest();
        request.setMailSubject(subject);
        request.setMailBody(body);
        request.setAudience(selectedRoles);

        sendButton.setDisable(true);
        clearButton.setDisable(true);
        setStatus("Sending…");

        messagingService.broadcastEmail(staffId, request,
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
        for (CheckBox cb : staffRoleChecks) cb.setSelected(false);

        emailSubjectField.clear();
        emailBodyField.clear();
    }

    private void setStatus(String s) {
        statusLabel.setText(s == null ? "" : s);
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
