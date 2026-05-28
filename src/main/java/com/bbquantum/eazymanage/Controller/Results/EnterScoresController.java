package com.bbquantum.eazymanage.Controller.Results;

import com.bbquantum.eazymanage.DTO.Academics.StudentsScoresTable;
import com.bbquantum.eazymanage.DTO.Academics.SubjectsHolder;
import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.DTO.Institution.SemesterCaching;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

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
    @FXML private TableColumn<StudentsScoresTable, String> classScoreCol;
    @FXML private TableColumn<StudentsScoresTable, String> calExamScoreCol;

    // ---- action bar
    @FXML private VBox progressBox;
    @FXML private Label       progressLabel;
    @FXML private ProgressBar progressBar;
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
    private int currentUploadIndex = 0;
    private GradeCaching    currentClass;
    private SubjectsHolder  currentSubject;
    private SemesterCaching currentSemester;

    private List<TableColumn<StudentsScoresTable, String>> editableColumns;

    @FXML
    private void initialize() {
        classChoice.setConverter(new StringConverter<>() {
            @Override public String toString(GradeCaching g) { return g == null ? "" : g.getLevelName(); }
            @Override public GradeCaching fromString(String s) { return null; }
        });
        subjectChoice.setConverter(new StringConverter<>() {
            @Override public String toString(SubjectsHolder s) { return s == null ? "" : s.getSubjectName(); }
            @Override public SubjectsHolder fromString(String s) { return null; }
        });
        semesterChoice.setConverter(new StringConverter<>() {
            @Override public String toString(SemesterCaching s) { return s == null ? "" : s.getSemesterName(); }
            @Override public SemesterCaching fromString(String s) { return null; }
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

        configureEditableColumn(ct1Col,        StudentsScoresTable::getClassTest1Score,    StudentsScoresTable::setClassTest1Score, true);
        configureEditableColumn(groupWorkCol,  StudentsScoresTable::getGroupWorkScore,     StudentsScoresTable::setGroupWorkScore, true);
        configureEditableColumn(ct2Col,        StudentsScoresTable::getClassTest2Score,    StudentsScoresTable::setClassTest2Score, true);
        configureEditableColumn(projectCol,    StudentsScoresTable::getProjectScore,       StudentsScoresTable::setProjectScore, true);
        configureEditableColumn(examCol,       StudentsScoresTable::getExamScore,          StudentsScoresTable::setExamScore, true);

        editableColumns = List.of(ct1Col, ct2Col, groupWorkCol, projectCol, examCol);

        // calculated, not editable.
        configureReadOnlyColumn(classScoreCol, StudentsScoresTable::getClassScore);
        configureReadOnlyColumn(calExamScoreCol,   StudentsScoresTable::getCalculatedExamScore);

        rows.addListener((ListChangeListener<StudentsScoresTable>) c ->
                saveButton.setDisable(rows.isEmpty()));
    }

    /** Wire a single column for editable string display. */
    private void configureEditableColumn(
            TableColumn<StudentsScoresTable, String> col,
            Function<StudentsScoresTable, String> getter,
            BiConsumer<StudentsScoresTable, String> setter,
            boolean recalcOnCommit) {

        col.setCellValueFactory(c ->
                new SimpleStringProperty(safeStatic(getter.apply(c.getValue()))));

        col.setCellFactory(c -> new ScoreCell(editableColumns));

        col.setEditable(true);
        col.setOnEditCommit(e -> {
            setter.accept(e.getRowValue(), e.getNewValue());
            if (recalcOnCommit) {
                recalculate(e.getRowValue());
                scoresTable.refresh();
            }
        });
    }

    private void configureReadOnlyColumn(
            TableColumn<StudentsScoresTable, String> col,
            Function<StudentsScoresTable, String> getter) {

        col.setCellValueFactory(c ->
                new SimpleStringProperty(safeStatic(getter.apply(c.getValue()))));
        col.setEditable(false);
        // Visually distinguish the computed cells from editable inputs.
        col.setCellFactory(c -> {
            TableCell<StudentsScoresTable, String> cell = new TableCell<>() {
                @Override protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };
            cell.getStyleClass().add("computed-cell");
            return cell;
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
        GradeCaching grade       = classChoice.getValue();
        SubjectsHolder subject   = subjectChoice.getValue();
        SemesterCaching semester = semesterChoice.getValue();
        if (grade == null || subject == null || semester == null) return;

        currentClass    = grade;
        currentSubject  = subject;
        currentSemester = semester;

        setStatus("Loading students…");
        fetchButton.setDisable(true);

        academicsService.loadSubjectStudents(subject.getSubjectId(), staffId,
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
        currentUploadIndex = 0;
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

        // Reset upload index when starting fresh (not a "Continue" retry).
        if (currentUploadIndex >= rows.size()) currentUploadIndex = 0;

        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        fetchButton.setDisable(true);
        classChoice.setDisable(true);
        subjectChoice.setDisable(true);
        semesterChoice.setDisable(true);

        progressBox.setVisible(true);
        progressBox.setManaged(true);
        updateProgress();
        uploadNext();
    }

    private void uploadNext() {
        if (currentUploadIndex >= rows.size()) {
            onSaveComplete();
            return;
        }
        StudentsScoresTable record = rows.get(currentUploadIndex);

        academicsService.saveStudentScores(
                record, currentSubject.getSubjectId(), currentSemester.getSemesterId(), staffId,
                () -> Platform.runLater(() -> {
                    currentUploadIndex++;
                    updateProgress();
                    uploadNext();
                }),
                () -> Platform.runLater(() -> {
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus("Save paused at row " + (currentUploadIndex + 1) + ". "
                            + (err == null ? "" : err)
                            + " Click 'Continue' to retry.");
                    saveButton.setText("Continue");
                    saveButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setStatus("Save paused — network unavailable at row "
                            + (currentUploadIndex + 1) + ". Fix connection and click 'Continue'.");
                    saveButton.setText("Continue");
                    saveButton.setDisable(false);
                }));
    }

    private void updateProgress() {
        if (rows.isEmpty()) { progressBar.setProgress(0); return; }
        double frac = (double) currentUploadIndex / rows.size();
        progressBar.setProgress(frac);
        progressLabel.setText("Saved " + currentUploadIndex + " of " + rows.size());
    }

    private void onSaveComplete() {
        progressBar.setProgress(1);
        progressLabel.setText("All saved.");
        setStatus("Scores saved.");
        currentUploadIndex = 0;
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
        rows.clear();
        currentUploadIndex = 0;
        classChoice.getSelectionModel().clearSelection();
        subjectChoice.getSelectionModel().clearSelection();
        semesterChoice.getSelectionModel().clearSelection();
        subjectChoice.setDisable(true);
        currentClass = null;
        currentSubject = null;
        currentSemester = null;
        classInfoBanner.setVisible(false);
        classInfoBanner.setManaged(false);
        progressBox.setVisible(false);
        progressBox.setManaged(false);
        saveButton.setText("Save scores");
        saveButton.setDisable(true);
        fetchButton.setDisable(true);
        setStatus("");
    }

    // ====================================================================
    // Helpers
    // ====================================================================

    private void setStatus(String s) { statusLabel.setText(s == null ? "" : s); }
    private String  safe(String s)        { return s == null ? "" : s; }
    private static String safeStatic(String s) { return s == null ? "" : s; }
}
