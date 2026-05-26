package com.bbquantum.eazymanage.Controller.Students;

import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.DTO.Institution.StudentsHolder;
import com.bbquantum.eazymanage.Services.AcademicsService;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.util.List;

public class BrowseStudentsController {

    @FXML private ChoiceBox<GradeCaching> classChoice;
    @FXML private Button fetchButton;
    @FXML private Label statusLabel;

    @FXML private HBox classInfoBanner;
    @FXML private Label classNameLabel;
    @FXML private Label rowCountLabel;

    @FXML private TableView<StudentsHolder> studentsTable;
    @FXML private TableColumn<StudentsHolder, String> studentIdCol;
    @FXML private TableColumn<StudentsHolder, String> studentNameCol;

    @SuppressWarnings("unused")
    private String role;
    private String staffId;

    private final ApiCore apiCore = new ApiCore();
    private final AcademicsService academicsService = new AcademicsService(apiCore);

    private final ObservableList<StudentsHolder> rows = FXCollections.observableArrayList();
    private GradeCaching currentClass;

    @FXML
    private void initialize() {
        classChoice.setConverter(new StringConverter<>() {
            @Override public String toString(GradeCaching g) { return g == null ? "" : g.getLevelName(); }
            @Override public GradeCaching fromString(String s) { return null; }
        });
        List<GradeCaching> grades = UtilityClass.gradeCacheManager.getGradesCache();
        if (grades != null) classChoice.getItems().setAll(grades);

        classChoice.valueProperty().addListener((obs, oldVal, newVal) ->
                fetchButton.setDisable(newVal == null));

        studentIdCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentId())));

        studentNameCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentName())));

        studentsTable.setItems(rows);
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
    }

    @FXML
    private void onFetch() {
        GradeCaching grade = classChoice.getValue();
        if (grade == null) return;

        currentClass = grade;
        setStatus("Loading students…");
        fetchButton.setDisable(true);

        academicsService.loadClassStudents(grade.getLevelId(), staffId,
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    List<StudentsHolder> list = UtilityClass.studentsHolder;
                    populateTable(list);
                }),
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err == null || err.isBlank()
                            ? "No students for this class."
                            : err);
                    rows.clear();
                    classInfoBanner.setVisible(false);
                    classInfoBanner.setManaged(false);
                }),
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    setStatus("Network unavailable.");
                }));
    }

    private void populateTable(List<StudentsHolder> list) {
        if (list == null || list.isEmpty()) {
            rows.clear();
            classInfoBanner.setVisible(false);
            classInfoBanner.setManaged(false);
            setStatus("No students on this class.");
            return;
        }
        rows.setAll(list);
        classInfoBanner.setVisible(true);
        classInfoBanner.setManaged(true);
        classNameLabel.setText(safe(currentClass.getLevelName()));
        rowCountLabel.setText(rows.size() + " student" + (rows.size() == 1 ? "" : "s"));
        setStatus("");
    }

    private void setStatus(String s) { statusLabel.setText(s == null ? "" : s); }
    private static String safe(String s) { return s == null ? "" : s; }
}
