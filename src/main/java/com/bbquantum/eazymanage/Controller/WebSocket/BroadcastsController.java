package com.bbquantum.eazymanage.Controller.WebSocket;

import com.bbquantum.eazymanage.DTO.WebSocket.BroadcastRequest;
import com.bbquantum.eazymanage.Services.WebSocketService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class BroadcastsController {

    @FXML private ChoiceBox<String> priorityChoice;
    @FXML private HBox audienceRow;
    @FXML private TextField titleField;
    @FXML private TextArea bodyField;
    @FXML private Label connectionStatusLabel;
    @FXML private Label statusLabel;
    @FXML private Button clearButton;
    @FXML private Button sendButton;

    private CheckBox principalCheck;
    private CheckBox adminCheck;
    private CheckBox teacherCheck;
    private CheckBox accountantCheck;

    @SuppressWarnings("unused")
    private String role;

    @FXML
    private void initialize() {
        priorityChoice.setItems(FXCollections.observableArrayList("INFO", "WARNING", "URGENT"));
        priorityChoice.getSelectionModel().select("INFO");

        // Audience checkboxes — built imperatively so we can reuse the
        // same row layout if the role list changes (e.g. Phase 8).
        principalCheck   = new CheckBox("Principal");
        adminCheck       = new CheckBox("Administrators");
        teacherCheck     = new CheckBox("Teachers");
        accountantCheck  = new CheckBox("Accountants");
        audienceRow.getChildren().addAll(principalCheck, adminCheck, teacherCheck, accountantCheck);

        refreshConnectionStatus();
    }

    /** Called by AppShellController after FXML load. */
    public void setIdentity(String role) {
        this.role = role;
        boolean canSend = "PRINCIPAL".equals(role) || "ADMINISTRATOR".equals(role);
        sendButton.setDisable(!canSend);
        priorityChoice.setDisable(!canSend);
        titleField.setDisable(!canSend);
        bodyField.setDisable(!canSend);
        principalCheck.setDisable(!canSend);
        adminCheck.setDisable(!canSend);
        teacherCheck.setDisable(!canSend);
        accountantCheck.setDisable(!canSend);

        if (!canSend) {
            statusLabel.setText("View-only role.");
        }
    }

    @FXML
    private void onSend() {
        String title = trim(titleField.getText());
        String body  = trim(bodyField.getText());
        if (title.isEmpty() || body.isEmpty()) {
            statusLabel.setText("Title and message are required.");
            return;
        }

        String audience = collectAudience();
        String priority = priorityChoice.getValue();
        if (priority == null) priority = "INFO";

        BroadcastRequest request = new BroadcastRequest(title, body, priority, audience);

        if (!WebSocketService.getInstance().isConnected()) {
            statusLabel.setText("Not connected — broadcast not sent.");
            refreshConnectionStatus();
            return;
        }

        sendButton.setDisable(true);
        statusLabel.setText("Sending…");

        // The send returns immediately; the inbound echo via the user queue
        // is what confirms delivery. Re-enable the button after a brief
        // delay so the UI doesn't feel stuck on slow links.
        try {
            WebSocketService.getInstance().sendBroadcast(request);
            statusLabel.setText("Sent.");
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Send failed.");
        }

        // Restore the form for the next message.
        Platform.runLater(() -> {
            titleField.clear();
            bodyField.clear();
            sendButton.setDisable(false);
        });
    }

    @FXML
    private void onClear() {
        titleField.clear();
        bodyField.clear();
        priorityChoice.getSelectionModel().select("INFO");
        principalCheck.setSelected(false);
        adminCheck.setSelected(false);
        teacherCheck.setSelected(false);
        accountantCheck.setSelected(false);
        statusLabel.setText("");
    }

    // ---- helpers --------------------------------------------------------

    /** Returns null for "all roles" so the server doesn't need to special-case "". */
    private String collectAudience() {
        List<String> roles = new ArrayList<>();
        if (principalCheck.isSelected())   roles.add("PRINCIPAL");
        if (adminCheck.isSelected())       roles.add("ADMINISTRATOR");
        if (teacherCheck.isSelected())     roles.add("TEACHING_STAFF");
        if (accountantCheck.isSelected())  roles.add("ACCOUNTANT");
        if (roles.isEmpty() || roles.size() == 4) return null;
        return String.join(",", roles);
    }

    private void refreshConnectionStatus() {
        boolean connected = WebSocketService.getInstance().isConnected();
        connectionStatusLabel.setText(connected ? "Connected" : "Disconnected");
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }
}
