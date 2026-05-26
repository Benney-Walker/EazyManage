package com.bbquantum.eazymanage.Controller.Settings;

import com.bbquantum.eazymanage.Services.WebSocketService;
import com.bbquantum.eazymanage.Shell.ShellLauncher;
import com.bbquantum.eazymanage.Utitity.SessionManager;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SettingsController {

    @FXML private ScrollPane rootScroll;
    @FXML private Label      staffNameLabel;
    @FXML private Label      organizationLabel;
    @FXML private Label      staffIdLabel;
    @FXML private Label      currentRoleLabel;
    @FXML private Label      roleHintLabel;
    @FXML private ChoiceBox<String> roleChoice;
    @FXML private Button switchButton;
    @FXML private Label      switchStatusLabel;
    @FXML private Label versionLabel;

    /** Active role at the moment this controller was initialised. Used to
     *  decide whether the "Switch role" button should be enabled. */
    private String currentActiveRole;

    @FXML
    private void initialize() {
        // Wire the choice listener once. Re-enabling/disabling the switch
        // button is purely a function of "is this a different role from
        // what we're currently acting as?"
        roleChoice.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean differs = newVal != null && !newVal.equals(currentActiveRole);
            switchButton.setDisable(!differs);
            switchStatusLabel.setText("");
        });

        versionLabel.setText(UtilityClass.APPLICATION_VERSION);
    }

    /**
     * Inject identity from the shell after FXML load. Same convention as
     * the other shell-native controllers (Conduct, Reports, Attendance).
     */
    public void attach(String role, String staffId, String organizationName, String username) {
        this.currentActiveRole = role;

        staffNameLabel.setText(safe(username));
        organizationLabel.setText(safe(organizationName));
        staffIdLabel.setText(safe(staffId));
        currentRoleLabel.setText("You are currently acting as: " + prettifyRole(role));

        // Roles list from the JWT (comma-separated string stored at login).
        List<String> allRoles = parseRoles(SessionManager.getRole());
        roleChoice.setItems(FXCollections.observableArrayList(
                allRoles.stream().map(SettingsController::prettifyRole).collect(Collectors.toList())));
        // Preselect the currently-active role.
        roleChoice.setValue(prettifyRole(role));

        if (allRoles.size() <= 1) {
            roleChoice.setDisable(true);
            switchButton.setDisable(true);
            roleHintLabel.setText("You have only one role assigned. Contact your administrator if you need additional access.");
        }
    }

    @FXML
    private void onSwitchRole() {
        String chosenPretty = roleChoice.getValue();
        if (chosenPretty == null) return;
        String chosen = denormalisePrettyRole(chosenPretty);

        if (chosen.equals(currentActiveRole)) return;

        switchButton.setDisable(true);
        roleChoice.setDisable(true);
        switchStatusLabel.setText("Switching…");

        // Disconnect realtime BEFORE relaunching — the shell relaunch tears
        // down the current scene, and we don't want an orphaned STOMP
        // session attached to a dead scene. The new shell will reconnect
        // automatically when boot() runs.
        WebSocketService.getInstance().disconnect();

        // Defer the relaunch one FX tick so the status label paints first;
        // ShellLauncher.launch swaps the scene synchronously, which would
        // otherwise leave the user staring at the old screen until the
        // new one is ready.
        Platform.runLater(() -> {
            try {
                Stage stage = (Stage) switchButton.getScene().getWindow();
                ShellLauncher.launch(
                        stage,
                        chosen,
                        SessionManager.getUserId(),
                        SessionManager.getOrganization(),
                        SessionManager.getUserName());
            } catch (Exception e) {
                e.printStackTrace();
                switchButton.setDisable(false);
                roleChoice.setDisable(false);
                switchStatusLabel.setText("Could not switch: " + e.getMessage());
            }
        });
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private static List<String> parseRoles(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /** Map the wire role string to a friendly display label. */
    private static String prettifyRole(String role) {
        if (role == null) return "";
        return switch (role) {
            case "PRINCIPAL"      -> "Principal";
            case "ADMINISTRATOR"  -> "Administrator";
            case "ACCOUNTANT"     -> "Accountant";
            case "TEACHING_STAFF" -> "Teaching Staff";
            default               -> role;
        };
    }

    /** Reverse of {@link #prettifyRole}. Required because the ChoiceBox
     *  is populated with display labels but the persisted active role is
     *  the wire string. */
    private static String denormalisePrettyRole(String pretty) {
        if (pretty == null) return "";
        return switch (pretty) {
            case "Principal"      -> "PRINCIPAL";
            case "Administrator"  -> "ADMINISTRATOR";
            case "Accountant"     -> "ACCOUNTANT";
            case "Teaching Staff" -> "TEACHING_STAFF";
            default               -> pretty;
        };
    }

    private static String safe(String s) { return s == null ? "" : s; }
}
