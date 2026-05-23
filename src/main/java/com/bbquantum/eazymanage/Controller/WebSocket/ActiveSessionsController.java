package com.bbquantum.eazymanage.Controller.WebSocket;

import com.bbquantum.eazymanage.DTO.WebSocket.ActiveSession;
import com.bbquantum.eazymanage.Services.PresenceTracker;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ActiveSessionsController {

    @FXML private TableView<ActiveSession> sessionsTable;
    @FXML private TableColumn<ActiveSession, String> staffIdCol;
    @FXML private TableColumn<ActiveSession, String> displayNameCol;
    @FXML private TableColumn<ActiveSession, String> roleCol;
    @FXML private TableColumn<ActiveSession, String> connectedAtCol;
    @FXML private TableColumn<ActiveSession, String> lastSeenAtCol;
    @FXML private Label countLabel;

    @FXML
    private void initialize() {
        staffIdCol.setCellValueFactory(c     ->
                new SimpleStringProperty(safe(c.getValue().getStaffId())));

        displayNameCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getDisplayName())));

        roleCol.setCellValueFactory(c        ->
                new SimpleStringProperty(prettyRole(c.getValue().getRole())));

        connectedAtCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getConnectedAt())));

        lastSeenAtCol.setCellValueFactory(c  ->
                new SimpleStringProperty(safe(c.getValue().getLastSeenAt())));

        sessionsTable.setItems(PresenceTracker.getInstance().getSessions());

        // Count label is bound rather than polled; the IntegerProperty
        // updates on every list change.
        countLabel.textProperty().bind(
                Bindings.format("%d online", PresenceTracker.getInstance().countProperty()));
    }

    private static String safe(String s) { return s == null ? "" : s; }

    private static String prettyRole(String role) {
        if (role == null) return "";
        return switch (role) {
            case "PRINCIPAL"      -> "Principal";
            case "ADMINISTRATOR"  -> "Administrator";
            case "ACCOUNTANT"     -> "Accountant";
            case "TEACHING_STAFF" -> "Teaching Staff";
            default               -> role;
        };
    }
}
