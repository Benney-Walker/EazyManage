package com.bbquantum.eazymanage.Controller.Report;

import com.bbquantum.eazymanage.Controller.StaffController;
import com.bbquantum.eazymanage.DTO.Academics.ViewClassSemesterReport;
import com.bbquantum.eazymanage.DTO.Academics.ViewStudentsSubjectsResults;
import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.DTO.Institution.SemesterCaching;
import com.bbquantum.eazymanage.Reports.ReportViewerController;
import com.bbquantum.eazymanage.Utitity.ApiUtilityClass;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClassResultsReportController implements ReportViewerController {

    // ---- header
    @FXML private Button backButton;

    // ---- filters
    @FXML private ChoiceBox<GradeCaching> classChoice;
    @FXML private ChoiceBox<SemesterCaching> semesterChoice;
    @FXML private Button loadButton;

    // ---- master table
    @FXML private TableView<ViewClassSemesterReport> classResultsTable;
    @FXML private TableColumn<ViewClassSemesterReport, String> studentIdCol;
    @FXML private TableColumn<ViewClassSemesterReport, String> studentNameCol;
    @FXML private TableColumn<ViewClassSemesterReport, String> totalCol;
    @FXML private TableColumn<ViewClassSemesterReport, String> averageCol;
    @FXML private TableColumn<ViewClassSemesterReport, String> positionCol;

    // ---- detail (subject breakdown)
    @FXML
    private VBox subjectBreakdownBox;
    @FXML private Label breakdownHeadingLabel;
    @FXML private TableView<ViewStudentsSubjectsResults> subjectBreakdownTable;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> subjectNameCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> classTest1ColView;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> groupWorkColView;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> classTest2ColView;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> projectColView;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> classScoreColView;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> examScoreColView;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> calcExamColView;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> subjectTotalCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> gradeCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> descriptionCol;

    // ---- action bar
    @FXML private Label    promotionLabel;
    @FXML private ChoiceBox<GradeCaching> promotionGradeChoice;
    @FXML private Label    passScoreLabel;
    @FXML private TextField passScoreField;
    @FXML private Label    statusLabel;
    @FXML private Button   downloadButton;

    // ---- identity / context
    private String staffId;
    private String role;
    private Runnable onBack;

    private final ApiUtilityClass api =
            new ApiUtilityClass((StaffController) null);

    // ---- model state
    private final ObservableList<ViewClassSemesterReport> rows = FXCollections.observableArrayList();
    private ViewClassSemesterReport expandedRow;       // currently expanded master row

    @FXML
    private void initialize() {
        // Class / semester choices
        classChoice.setConverter(new StringConverter<>() {
            @Override public String toString(GradeCaching g) { return g == null ? "" : g.getLevelName(); }
            @Override public GradeCaching fromString(String s) { return null; }
        });
        semesterChoice.setConverter(new StringConverter<>() {
            @Override public String toString(SemesterCaching s) { return s == null ? "" : s.getSemesterName(); }
            @Override public SemesterCaching fromString(String s) { return null; }
        });
        promotionGradeChoice.setConverter(new StringConverter<>() {
            @Override public String toString(GradeCaching g) { return g == null ? "" : g.getLevelName(); }
            @Override public GradeCaching fromString(String s) { return null; }
        });

        List<GradeCaching> grades = UtilityClass.gradeCacheManager.getGradesCache();
        if (grades != null) {
            classChoice.getItems().setAll(grades);
            promotionGradeChoice.getItems().setAll(grades);
        }
        List<SemesterCaching> semesters = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (semesters != null) semesterChoice.getItems().setAll(semesters);

        // Master table columns
        studentIdCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStudentId()));
        studentNameCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStudentName()));
        totalCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTotal()));
        averageCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getAverage()));
        positionCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getClassPosition()));

        classResultsTable.setItems(rows);

        // Detail table columns
        subjectNameCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getSubjectName()));
        classTest1ColView.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getClassTest1Score()));
        groupWorkColView.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getGroupWorkScore()));
        classTest2ColView.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getClassTest2Score()));
        projectColView.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getProjectScore()));
        classScoreColView.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getClassScore()));
        examScoreColView.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getExamScore()));
        calcExamColView.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCalculatedExamScore()));
        subjectTotalCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTotal()));
        gradeCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getGrade()));
        descriptionCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDescription()));

        // Click a master row to toggle the detail table.
        classResultsTable.setRowFactory(tv -> {
            TableRow<ViewClassSemesterReport> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (row.isEmpty()) return;
                ViewClassSemesterReport clicked = row.getItem();
                toggleBreakdown(clicked);
            });
            return row;
        });

        downloadButton.setDisable(true);
    }

    @Override
    public void attach(String staffId, String role, Runnable onBack) {
        this.staffId = staffId;
        this.role    = role;
        this.onBack  = onBack;

        // Show promotion controls only to administrators (matching legacy).
        boolean admin = "ADMINISTRATOR".equals(role);
        for (Node n : List.of(promotionLabel, promotionGradeChoice, passScoreLabel, passScoreField)) {
            n.setVisible(admin);
            n.setManaged(admin);
        }

        // Auto-pick first available class / semester so the screen feels populated.
        if (!classChoice.getItems().isEmpty() && classChoice.getValue() == null) {
            classChoice.getSelectionModel().selectFirst();
        }
        if (!semesterChoice.getItems().isEmpty() && semesterChoice.getValue() == null) {
            semesterChoice.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void onBack() {
        if (onBack != null) onBack.run();
    }

    @FXML
    private void onLoad() {
        GradeCaching grade = classChoice.getValue();
        SemesterCaching semester = semesterChoice.getValue();
        if (grade == null || semester == null) {
            setStatus("Pick a class and semester first.");
            return;
        }
        setStatus("Loading…");
        rows.clear();
        collapseBreakdown();
        downloadButton.setDisable(true);
        loadButton.setDisable(true);

        api.loadClassResults(grade.getLevelId(), semester.getSemesterId(),
                () -> Platform.runLater(() -> {
                    populate(UtilityClass.classResults);
                    setStatus(rows.isEmpty()
                            ? "No results found for that class and semester."
                            : "Loaded " + rows.size() + " student(s).");
                    downloadButton.setDisable(rows.isEmpty());
                    loadButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setStatus("Could not load results.");
                    loadButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setStatus("Network issue — try again.");
                    loadButton.setDisable(false);
                }));
    }

    @FXML
    private void onDownload() {
        GradeCaching grade = classChoice.getValue();
        SemesterCaching semester = semesterChoice.getValue();
        if (grade == null || semester == null) return;

        // Promotion fields default to the legacy "no_promotion" sentinel unless
        // an administrator picked a target grade and a numeric pass score.
        String promotionGradeId = "no_promotion";
        String passScore = "0";
        if ("ADMINISTRATOR".equals(role)) {
            if (promotionGradeChoice.getValue() != null) {
                promotionGradeId = promotionGradeChoice.getValue().getLevelId();
            }
            if (passScoreField.getText() != null && !passScoreField.getText().isBlank()) {
                passScore = passScoreField.getText().trim();
            }
        }

        downloadButton.setDisable(true);
        setStatus("Requesting PDF…");

        api.downloadStudentsReport(
                staffId, grade.getLevelId(), semester.getSemesterId(),
                promotionGradeId, passScore,
                this::parentWindow,
                () -> Platform.runLater(() -> {
                    setStatus("Saved.");
                    downloadButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setStatus("Could not generate report.");
                    downloadButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setStatus("Network issue while downloading.");
                    downloadButton.setDisable(false);
                })
        );
    }

    // ---- internals ------------------------------------------------------

    private void populate(List<ViewClassSemesterReport> incoming) {
        rows.clear();
        if (incoming != null) rows.addAll(incoming);
    }

    /** Show / hide the per-subject breakdown for the clicked row. */
    private void toggleBreakdown(ViewClassSemesterReport row) {
        if (Objects.equals(expandedRow, row) && subjectBreakdownBox.isVisible()) {
            collapseBreakdown();
            return;
        }
        expandedRow = row;
        breakdownHeadingLabel.setText("Subject breakdown — " + safe(row.getStudentName())
                + "  ·  " + safe(row.getStudentId()));
        List<ViewStudentsSubjectsResults> subjects = row.getSubjects() == null
                ? new ArrayList<>() : row.getSubjects();
        subjectBreakdownTable.getItems().setAll(subjects);
        subjectBreakdownBox.setVisible(true);
        subjectBreakdownBox.setManaged(true);
    }

    private void collapseBreakdown() {
        expandedRow = null;
        subjectBreakdownBox.setVisible(false);
        subjectBreakdownBox.setManaged(false);
        subjectBreakdownTable.getItems().clear();
    }

    private Window parentWindow() {
        return downloadButton.getScene() == null ? null : downloadButton.getScene().getWindow();
    }

    private void setStatus(String text) {
        statusLabel.setText(text == null ? "" : text);
    }

    private static String safe(String s) { return s == null ? "" : s; }
}
