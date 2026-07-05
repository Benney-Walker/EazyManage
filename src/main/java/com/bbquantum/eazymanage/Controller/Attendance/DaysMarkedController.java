package com.bbquantum.eazymanage.Controller.Attendance;

import com.bbquantum.eazymanage.DTO.Attendance.DatesMarked;
import com.bbquantum.eazymanage.DTO.Caching.GradeCaching;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Services.AttendanceService;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

public class DaysMarkedController {

    @FXML private ChoiceBox<GradeCaching> classChoice;
    @FXML private Button fetchButton;

    @FXML private HBox recordInfoBanner;
    @FXML private Label recordTitleLabel;
    @FXML private Label daysMarkedCount;

    @FXML private TableView<DatesMarked> markedDatesTable;
    @FXML private TableColumn<DatesMarked, String> dateCol;
    @FXML private TableColumn<DatesMarked, String> dayCol;
    @FXML private TableColumn<DatesMarked, String> staffCol;

    @FXML private Label statusLabel;
    @FXML private Button cancelButton;

    // ---- identity
    @SuppressWarnings("unused")
    private String role;
    private String staffId;

    // ---- services
    private final ApiCore apiCore = new ApiCore();
    private final AttendanceService attendanceService = new AttendanceService(apiCore);

    private final ObservableList<DatesMarked> rows = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        classChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(GradeCaching g) { return g == null ? "" : g.getLevelName(); }

            @Override
            public GradeCaching fromString(String s) { return null; }
        });

        List<GradeCaching> cached = UtilityClass.gradeCacheManager.getGradesCache();
        if (cached != null) classChoice.getItems().setAll(cached);

        dateCol.setCellValueFactory(cell ->
                new SimpleStringProperty(safe(cell.getValue().getDateMarked())));

        dayCol.setCellValueFactory(cell ->
                new SimpleStringProperty(safe(cell.getValue().getDayMarked())));

        staffCol.setCellValueFactory(cell ->
                new SimpleStringProperty(safe(cell.getValue().getStaffName())));

        markedDatesTable.setItems(rows);
    }

    public void attach(String staffId, String role) {
        this.role    = role;
        this.staffId = staffId;
    }

    @FXML
    private void onFetch() {
        GradeCaching grade = classChoice.getValue();
        if (grade == null) {
            setStatus("Pick a class first.");
            return;
        }

        setStatus("Loading records…");
        fetchButton.setDisable(true);

        List<DatesMarked> holder = new ArrayList<>();

        attendanceService.viewDatesMarked(staffId, grade.getLevelId(),
                holder::addAll,
                () -> Platform.runLater(() -> {
                    if (holder.isEmpty()) {
                        setStatus("No records found.");
                        return;
                    }

                    populateTable(holder, grade.getLevelName());
                    setStatus("");
                    fetchButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err == null || err.isBlank()
                            ? "Could not load records."
                            : err);
                }),
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    setStatus("Network unavailable.");
                }));
    }


    private void populateTable(List<DatesMarked> list, String className) {

        if (list == null || list.isEmpty()) {
            recordInfoBanner.setVisible(false);
            recordInfoBanner.setManaged(false);
            setStatus("No records found for that class.");
            return;
        }

        rows.setAll(list);

        recordInfoBanner.setVisible(true);
        recordInfoBanner.setManaged(true);
        recordTitleLabel.setText(className);
        daysMarkedCount.setText(String.valueOf(list.size()));

        setStatus("");
    }

    @FXML
    private void onReset() {

        classChoice.getSelectionModel().clearSelection();

        rows.clear();
        recordInfoBanner.setVisible(false);
        recordInfoBanner.setManaged(false);

        setStatus("");
    }

    // ====================================================================
    // Helpers
    // ====================================================================

    private void setStatus(String s) { statusLabel.setText(s == null ? "" : s); }
    private static String safe(String s) { return s == null ? "" : s; }
}
