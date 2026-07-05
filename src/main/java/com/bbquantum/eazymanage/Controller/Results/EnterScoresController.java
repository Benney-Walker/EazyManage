package com.bbquantum.eazymanage.Controller.Results;

import com.bbquantum.eazymanage.DTO.Academics.Results.StudentsScoresTable;
import com.bbquantum.eazymanage.DTO.Academics.Subject.SubjectsHolder;
import com.bbquantum.eazymanage.DTO.Caching.GradeCaching;
import com.bbquantum.eazymanage.DTO.Caching.SemesterCaching;
import com.bbquantum.eazymanage.Services.AcademicsService;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class EnterScoresController {

    // ---- toolbar
    @FXML private ChoiceBox<GradeCaching> classChoice;
    @FXML private ChoiceBox<SubjectsHolder>  subjectChoice;
    @FXML private ChoiceBox<SemesterCaching> semesterChoice;
    @FXML private Button fetchButton;

    // ---- info banner
    @FXML private HBox classInfoBanner;
    @FXML private Label contextTitleLabel;
    @FXML private Label contextSubtitleLabel;
    @FXML private Label rowCountLabel;

    // ---- table
    @FXML private TableView<StudentsScoresTable> scoresTable;
    @FXML private TableColumn<StudentsScoresTable, String> studentIdCol;
    @FXML private TableColumn<StudentsScoresTable, String> nameCol;
    @FXML private TableColumn<StudentsScoresTable, String> ct1Col;
    @FXML private TableColumn<StudentsScoresTable, String> groupWorkCol;
    @FXML private TableColumn<StudentsScoresTable, String> ct2Col;
    @FXML private TableColumn<StudentsScoresTable, String> projectCol;
    @FXML private TableColumn<StudentsScoresTable, String> examCol;

    // ---- action bar
    @FXML private Label       statusLabel;
    @FXML private Button      cancelButton;
    @FXML private Button      saveButton;

    // ---- identity
    @SuppressWarnings("unused")
    private String role;
    private String staffId;

    // ---- services
    private final ApiCore apiCore = new ApiCore();
    private final AcademicsService academicsService = new AcademicsService(apiCore);

    // ---- model
    private final ObservableList<StudentsScoresTable> rows = FXCollections.observableArrayList();
    private GradeCaching    currentClass;
    private SubjectsHolder  currentSubject;
    private SemesterCaching currentSemester;

    private List<TableColumn<StudentsScoresTable, String>> editableColumns;

    @FXML
    private void initialize() {
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

        List<GradeCaching> grades = UtilityClass.gradeCacheManager.getGradesCache();
        if (grades != null) classChoice.getItems().setAll(grades);
        List<SemesterCaching> semesters = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (semesters != null) semesterChoice.getItems().setAll(semesters);

        // Cascade: when a class is picked, load that class's subjects.
        classChoice.valueProperty().addListener((obs, oldVal, newVal) -> {
            subjectChoice.getItems().clear();
            subjectChoice.setValue(null);
            subjectChoice.setDisable(true);
            refreshFetchButtonState();
            if (newVal != null) loadSubjectsFor(newVal);
        });
        subjectChoice.valueProperty().addListener((obs, oldVal, newVal) -> refreshFetchButtonState());
        semesterChoice.valueProperty().addListener((obs, oldVal, newVal) -> refreshFetchButtonState());

        scoresTable.setItems(rows);

        studentIdCol.setCellValueFactory(c -> new SimpleStringProperty(safe(c.getValue().getStudentId())));
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(safe(c.getValue().getStudentName())));

        configureEditableColumn(ct1Col,        StudentsScoresTable::getClassTest1Score,    StudentsScoresTable::setClassTest1Score);
        configureEditableColumn(groupWorkCol,  StudentsScoresTable::getGroupWorkScore,     StudentsScoresTable::setGroupWorkScore);
        configureEditableColumn(ct2Col,        StudentsScoresTable::getClassTest2Score,    StudentsScoresTable::setClassTest2Score);
        configureEditableColumn(projectCol,    StudentsScoresTable::getProjectScore,       StudentsScoresTable::setProjectScore);
        configureEditableColumn(examCol,       StudentsScoresTable::getExamScore,          StudentsScoresTable::setExamScore);

        editableColumns = List.of(ct1Col, ct2Col, groupWorkCol, projectCol, examCol);

        rows.addListener((ListChangeListener<StudentsScoresTable>) c ->
                saveButton.setDisable(rows.isEmpty()));
    }

    /** Wire a single column for editable string display. */
    private void configureEditableColumn(
            TableColumn<StudentsScoresTable, String> col,
            Function<StudentsScoresTable, String> getter,
            BiConsumer<StudentsScoresTable, String> setter) {

        col.setCellValueFactory(c ->
                new SimpleStringProperty(safeStatic(getter.apply(c.getValue()))));

        col.setCellFactory(c -> new ScoreCell(editableColumns));

        col.setEditable(true);
        col.setOnEditCommit(e -> {
            setter.accept(e.getRowValue(), e.getNewValue());
            recalculate(e.getRowValue());
            scoresTable.refresh();
        });
    }

    private void recalculate(StudentsScoresTable row) {
        if (row == null) return;
        double ct1     = parseScore(row.getClassTest1Score());
        double gw      = parseScore(row.getGroupWorkScore());
        double ct2     = parseScore(row.getClassTest2Score());
        double project = parseScore(row.getProjectScore());
        double classScore = ((ct1 + gw + ct2 + project) / 100.0) * 50.0;
        row.setClassScore(String.format("%.1f", classScore));

        double exam = parseScore(row.getExamScore());
        double calcExam = (exam / 100.0) * 50.0;
        row.setCalculatedExamScore(String.format("%.1f", calcExam));
    }

    private static double parseScore(String s) {
        if (s == null || s.isBlank()) return 0;
        try { return Double.parseDouble(s.trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
    }

    private void refreshFetchButtonState() {
        boolean ok = classChoice.getValue() != null
                && subjectChoice.getValue() != null
                && semesterChoice.getValue() != null;
        fetchButton.setDisable(!ok);
    }

    // ====================================================================
    // Cascade: load subjects for picked class
    // ====================================================================

    private void loadSubjectsFor(GradeCaching grade) {
        setStatus("Loading subjects…");
        academicsService.loadClassSubjects(grade.getLevelId(), staffId,
                () -> Platform.runLater(() -> {
                    List<SubjectsHolder> list = UtilityClass.subjectsHolder;
                    if (list == null || list.isEmpty()) {
                        setStatus("No subjects for this class.");
                        subjectChoice.setDisable(true);
                        return;
                    }
                    subjectChoice.getItems().setAll(list);
                    subjectChoice.setDisable(false);
                    setStatus("");
                }),
                () -> Platform.runLater(() -> {
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err == null || err.isBlank()
                            ? "Could not load subjects."
                            : err);
                }),
                () -> Platform.runLater(() -> setStatus("Network unavailable.")));
    }

    // ====================================================================
    // Fetch students for the picked class/subject/semester
    // ====================================================================

    @FXML
    private void onFetch() {
        GradeCaching grade = classChoice.getValue();
        SubjectsHolder subject = subjectChoice.getValue();
        SemesterCaching semester = semesterChoice.getValue();
        if (grade == null || subject == null || semester == null) return;

        currentClass = grade;
        currentSubject = subject;
        currentSemester = semester;

        setStatus("Loading students…");
        fetchButton.setDisable(true);

        academicsService.loadSubjectStudents(staffId, subject.getSubjectId(), semester.getSemesterId(),
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    if (UtilityClass.subjectScores == null) {
                        setStatus("No students for this subject.");
                        rows.clear();
                        classInfoBanner.setVisible(false);
                        classInfoBanner.setManaged(false);
                        return;
                    }
                    List<StudentsScoresTable> table = UtilityClass.subjectScores.getScoresTable();
                    populateTable(table);
                }),
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err == null || err.isBlank()
                            ? "Could not load students."
                            : err);
                }),
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    setStatus("Network unavailable.");
                }));
    }

    private void populateTable(List<StudentsScoresTable> table) {
        rows.clear();
        if (table == null || table.isEmpty()) {
            classInfoBanner.setVisible(false);
            classInfoBanner.setManaged(false);
            setStatus("No students.");
            return;
        }

        rows.setAll(table);
        classInfoBanner.setVisible(true);
        classInfoBanner.setManaged(true);
        contextTitleLabel.setText(safe(currentSubject.getSubjectName()) + " — " + safe(currentClass.getLevelName()));
        contextSubtitleLabel.setText(safe(currentSemester.getSemesterName()));
        rowCountLabel.setText(rows.size() + " student" + (rows.size() == 1 ? "" : "s"));
        setStatus("");
    }

    // ====================================================================
    // Save loop
    // ====================================================================

    @FXML
    private void onSave() {
        if (rows.isEmpty() || currentSubject == null || currentSemester == null) return;

        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        fetchButton.setDisable(true);
        classChoice.setDisable(true);
        subjectChoice.setDisable(true);
        semesterChoice.setDisable(true);
        setStatus("Saving scores");

        academicsService.saveStudentScores(staffId, currentSubject.getSubjectId(), currentSemester.getSemesterId(),
                rows,
                () -> Platform.runLater(() -> {
                    onSaveComplete();
                    rows.clear();
                    setStatus("");
                }),
                () -> Platform.runLater(() -> {
                    setStatus(ApiContext.getInstance().errorMessageContainer);
                    saveButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setStatus("Network unavailable. Turn on offline mode to save offline");
                    saveButton.setDisable(false);
                }));
    }

    private void onSaveComplete() {
        setStatus("Scores saved.");
        saveButton.setText("Save scores");
        saveButton.setDisable(false);
        cancelButton.setDisable(false);
        fetchButton.setDisable(false);
        classChoice.setDisable(false);
        subjectChoice.setDisable(false);
        semesterChoice.setDisable(false);
    }

    // ====================================================================
    // Reset
    // ====================================================================

    @FXML
    private void onReset() {

        for (StudentsScoresTable record : UtilityClass.subjectScores.getScoresTable()) {
            record.setClassTest1Score("0");
            record.setClassTest2Score("0");
            record.setGroupWorkScore("0");
            record.setProjectScore("0");
            record.setExamScore("0");
            record.setClassScore("0");
            record.setCalculatedExamScore("0");
        }

        scoresTable.refresh();
    }

    // ====================================================================
    // Helpers
    // ====================================================================

    private void setStatus(String s) { statusLabel.setText(s == null ? "" : s); }
    private String  safe(String s)        { return s == null ? "" : s; }
    private static String safeStatic(String s) { return s == null ? "" : s; }
}
