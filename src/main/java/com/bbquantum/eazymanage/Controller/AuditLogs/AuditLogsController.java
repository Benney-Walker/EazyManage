package com.bbquantum.eazymanage.Controller.AuditLogs;

import com.bbquantum.eazymanage.DTO.Institution.LogsDTO;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Services.LoggingService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.function.Predicate;

public class AuditLogsController {

    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private TextField actorFilterField;
    @FXML private Button clearFiltersButton;
    @FXML private Button refreshButton;
    @FXML private Label statusLabel;

    @FXML private HBox infoBanner;
    @FXML private Label visibleCountLabel;
    @FXML private Label totalCountLabel;

    @FXML private TableView<LogsDTO> logsTable;
    @FXML private TableColumn<LogsDTO, String> dateCol;
    @FXML private TableColumn<LogsDTO, String> timeCol;
    @FXML private TableColumn<LogsDTO, String> actorCol;
    @FXML private TableColumn<LogsDTO, String> typeCol;
    @FXML private TableColumn<LogsDTO, String> statusCol;
    @FXML private TableColumn<LogsDTO, String> messageCol;

    @SuppressWarnings("unused")
    private String role;
    private String staffId;

    private final ApiCore apiCore = new ApiCore();
    private final LoggingService loggingService = new LoggingService(apiCore);

    /** All log entries returned by the latest fetch. */
    private final ObservableList<LogsDTO> allLogs = FXCollections.observableArrayList();
    /** View over {@link #allLogs} after applying filters. */
    private FilteredList<LogsDTO> filtered;

    @FXML
    private void initialize() {
        // Wire columns to DTO fields.
        dateCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getDate())));

        timeCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getTime())));

        actorCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getCreatedBy())));

        typeCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getType())));

        statusCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStatus())));

        messageCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getMessage())));

        // Wrap the backing list in a FilteredList. Updating the predicate
        // re-evaluates the view without copying anything.
        filtered = new FilteredList<>(allLogs, x -> true);
        logsTable.setItems(filtered);

        // Recompute filter predicate whenever any input changes.
        fromDatePicker.valueProperty().addListener((obs, oldV, newV) -> applyFilter());
        toDatePicker.valueProperty().addListener((obs, oldV, newV) -> applyFilter());
        actorFilterField.textProperty().addListener((obs, oldV, newV) -> applyFilter());
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
    }

    // ====================================================================
    // Refresh
    // ====================================================================

    @FXML
    private void onRefresh() {
        refreshButton.setDisable(true);
        setStatus("Loading…");

        loggingService.fetchAllLogs(staffId,
                logs -> Platform.runLater(() -> {
                    refreshButton.setDisable(false);
                    if (logs == null || logs.isEmpty()) {
                        allLogs.clear();
                        infoBanner.setVisible(false);
                        infoBanner.setManaged(false);
                        setStatus("No log entries found.");
                        return;
                    }
                    allLogs.setAll(logs);
                    showBanner();
                    setStatus("");
                }),
                () -> Platform.runLater(() -> {
                    refreshButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err == null || err.isBlank()
                            ? "Could not load logs."
                            : err);
                }),
                () -> Platform.runLater(() -> {
                    refreshButton.setDisable(false);
                    setStatus("Network unavailable.");
                }));
    }

    // ====================================================================
    // Filtering
    // ====================================================================

    private void applyFilter() {
        LocalDate from = fromDatePicker.getValue();
        LocalDate to   = toDatePicker.getValue();
        String actor   = actorFilterField.getText() == null
                ? "" : actorFilterField.getText().trim().toLowerCase();

        Predicate<LogsDTO> p = row -> {

            if (from != null || to != null) {
                LocalDate rowDate;
                try {
                    if (row.getDate() == null || row.getDate().isBlank()) return false;
                    rowDate = LocalDate.parse(row.getDate());
                } catch (DateTimeParseException ex) {
                    return false;
                }
                if (from != null && rowDate.isBefore(from)) return false;
                if (to   != null && rowDate.isAfter(to))    return false;
            }

            // Actor filter: substring match on createdBy.
            if (!actor.isEmpty()) {
                String cb = row.getCreatedBy() == null ? "" : row.getCreatedBy().toLowerCase();
                if (!cb.contains(actor)) return false;
            }
            return true;
        };
        filtered.setPredicate(p);
        showBanner();
    }

    @FXML
    private void onClearFilters() {
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        actorFilterField.clear();
        // Listeners fire on each change above, but set the predicate once
        // more explicitly so the visible count refreshes too.
        applyFilter();
    }

    private void showBanner() {
        if (allLogs.isEmpty()) {
            infoBanner.setVisible(false);
            infoBanner.setManaged(false);
            return;
        }
        infoBanner.setVisible(true);
        infoBanner.setManaged(true);
        visibleCountLabel.setText("Showing " + filtered.size()
                + " of " + allLogs.size() + " entr"
                + (allLogs.size() == 1 ? "y" : "ies"));
        totalCountLabel.setText("Total: " + allLogs.size());
    }

    private void setStatus(String s)     { statusLabel.setText(s == null ? "" : s); }
    private static String safe(String s) { return s == null ? "" : s; }
}
