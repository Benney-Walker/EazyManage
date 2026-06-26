package com.bbquantum.eazymanage.Controller.Report;

import com.bbquantum.eazymanage.DTO.Academics.Results.SbaRecord;
import com.bbquantum.eazymanage.DTO.Academics.Subject.SubjectsHolder;
import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.DTO.Institution.SemesterCaching;
import com.bbquantum.eazymanage.Reports.ReportViewerController;
import com.bbquantum.eazymanage.Services.AcademicsService;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Services.ReportsService;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.util.List;

public class SbaReportController implements ReportViewerController {

    // ---- header
    @FXML private Button backButton;

    // ---- filters
    @FXML private ChoiceBox<GradeCaching> classChoice;
    @FXML private ChoiceBox<SubjectsHolder>  subjectChoice;
    @FXML private ChoiceBox<SemesterCaching> semesterChoice;
    @FXML private Button loadButton;

    // ---- table
    @FXML
    private TableView<SbaRecord> sbaTable;
    @FXML private TableColumn<SbaRecord, String> studentIdCol;
    @FXML private TableColumn<SbaRecord, String> studentNameCol;
    @FXML private TableColumn<SbaRecord, String> classTest1Col;
    @FXML private TableColumn<SbaRecord, String> groupWorkCol;
    @FXML private TableColumn<SbaRecord, String> classTest2Col;
    @FXML private TableColumn<SbaRecord, String> projectCol;
    @FXML private TableColumn<SbaRecord, String> classScoreCol;
    @FXML private TableColumn<SbaRecord, String> examScoreCol;
    @FXML private TableColumn<SbaRecord, String> calcExamCol;

    // ---- action bar
    @FXML private Label statusLabel;
    @FXML private Button   downloadButton;

    // ---- identity / context
    private String staffId;
    @SuppressWarnings("unused")
    private String role;
    private Runnable onBack;

    // Phase 8d — direct service references.
    private final ApiCore           apiCore          = new ApiCore();
    private final AcademicsService  academicsService = new AcademicsService(apiCore);
    private final ReportsService    reportsService   = new ReportsService(apiCore);

    private final ObservableList<SbaRecord> rows = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Choice converters
        classChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(GradeCaching g) { return g == null ? "" : g.getLevelName(); }

            @Override
            public GradeCaching fromString(String s) { return null; }
        });
        subjectChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(SubjectsHolder s) { return s == null ? "" : s.getSubjectName(); }

            @Override
            public SubjectsHolder fromString(String s) { return null; }
        });
        semesterChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(SemesterCaching s) {
                return s == null ? "" : s.getSemesterName() + " " + s.getAcademicYear();
            }

            @Override
            public SemesterCaching fromString(String s) { return null; }
        });

        // Pre-populate class + semester from caches
        List<GradeCaching> grades = UtilityClass.gradeCacheManager.getGradesCache();
        if (grades != null) classChoice.getItems().setAll(grades);
        List<SemesterCaching> semesters = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (semesters != null) semesterChoice.getItems().setAll(semesters);

        // When class changes, reload its subjects.
        classChoice.valueProperty().addListener((obs, oldVal, newVal) -> {
            subjectChoice.getItems().clear();
            subjectChoice.setDisable(true);
            if (newVal != null) loadSubjectsFor(newVal);
        });

        // Table columns
        studentIdCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStudentId()));
        studentNameCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStudentName()));
        classTest1Col.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getClassTest1Score()));
        groupWorkCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getGroupWorkScore()));
        classTest2Col.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getClassTest2Score()));
        projectCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getProjectScore()));
        classScoreCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getClassScore()));
        examScoreCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getExamScore()));
        calcExamCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCalculatedExamScore()));

        sbaTable.setItems(rows);
    }

    @Override
    public void attach(String staffId, String role, Runnable onBack) {
        this.staffId = staffId;
        this.role    = role;
        this.onBack  = onBack;

        // Auto-pick the first class / semester so we don't sit on three empty dropdowns.
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

    private void loadSubjectsFor(GradeCaching grade) {
        setStatus("Loading subjects…");
        academicsService.loadClassSubjects(grade.getLevelId(), staffId,
                () -> Platform.runLater(() -> {
                    // loadClassSubjects writes to UtilityClass.subjectsHolder; snapshot now.
                    List<SubjectsHolder> subjects = UtilityClass.subjectsHolder;
                    if (subjects != null && !subjects.isEmpty()) {
                        subjectChoice.getItems().setAll(subjects);
                        subjectChoice.setDisable(false);
                        subjectChoice.getSelectionModel().selectFirst();
                        setStatus("");
                    } else {
                        subjectChoice.setDisable(true);
                        setStatus("No subjects on this class.");
                    }
                }),
                () -> Platform.runLater(() -> setStatus("Could not load subjects.")),
                () -> Platform.runLater(() -> setStatus("Network issue — try again.")));
    }

    @FXML
    private void onLoad() {
        GradeCaching grade = classChoice.getValue();
        SubjectsHolder subject = subjectChoice.getValue();
        SemesterCaching semester = semesterChoice.getValue();
        if (grade == null || subject == null || semester == null) {
            setStatus("Pick class, subject, and semester first.");
            return;
        }
        setStatus("Loading SBA…");
        rows.clear();
        downloadButton.setDisable(true);
        loadButton.setDisable(true);

        reportsService.loadSbaRecords(grade.getLevelId(), subject.getSubjectId(), semester.getSemesterId(), staffId,
                list -> Platform.runLater(() -> populate(list)),
                () -> Platform.runLater(() -> {
                    setStatus(rows.isEmpty()
                            ? "No SBA records for that selection."
                            : "Loaded " + rows.size() + " student(s).");
                    downloadButton.setDisable(rows.isEmpty());
                    loadButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setStatus("Could not load SBA.");
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
        SubjectsHolder subject = subjectChoice.getValue();
        SemesterCaching semester = semesterChoice.getValue();
        if (grade == null || subject == null || semester == null) return;

        downloadButton.setDisable(true);
        setStatus("Requesting PDF…");

        String suggestedName = "SBA - " + safe(grade.getLevelName())
                + " - " + safe(subject.getSubjectName())
                + " - " + safe(semester.getSemesterName()) + ".pdf";

        reportsService.downloadSbaReport(
                staffId, grade.getLevelId(), subject.getSubjectId(), semester.getSemesterId(),
                suggestedName,
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

    private void populate(List<SbaRecord> incoming) {
        rows.clear();
        if (incoming != null) rows.addAll(incoming);
    }

    private Window parentWindow() {
        return downloadButton.getScene() == null ? null : downloadButton.getScene().getWindow();
    }

    private void setStatus(String text) {
        statusLabel.setText(text == null ? "" : text);
    }

    private static String safe(String s) { return s == null ? "" : s; }
}
