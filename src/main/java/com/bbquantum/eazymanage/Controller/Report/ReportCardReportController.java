package com.bbquantum.eazymanage.Controller.Report;

import com.bbquantum.eazymanage.Controller.StaffController;
import com.bbquantum.eazymanage.DTO.Academics.StudentConductRecord;
import com.bbquantum.eazymanage.DTO.Academics.StudentReportCard;
import com.bbquantum.eazymanage.DTO.Academics.StudentSubjectResults;
import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.DTO.Institution.SemesterCaching;
import com.bbquantum.eazymanage.DTO.Institution.StudentsHolder;
import com.bbquantum.eazymanage.Reports.ReportViewerController;
import com.bbquantum.eazymanage.Utitity.ApiUtilityClass;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportCardReportController implements ReportViewerController {

    // ---- header
    @FXML private Button backButton;

    // ---- filters
    @FXML private ChoiceBox<GradeCaching> classChoice;
    @FXML private ChoiceBox<SemesterCaching> semesterChoice;
    @FXML private Button refreshStudentsBtn;

    // ---- left: student picker
    @FXML private TableView<StudentsHolder> studentsTable;
    @FXML private TableColumn<StudentsHolder, String> studentIdCol;
    @FXML private TableColumn<StudentsHolder, String> studentNameCol;

    // ---- right: card preview
    @FXML private Label institutionNameLabel;
    @FXML private Label institutionAddressLabel;
    @FXML private Label studentNameValue;
    @FXML private Label studentIdValue;
    @FXML private Label classValue;
    @FXML private Label semesterValue;
    @FXML private Label positionValue;
    @FXML private Label issueDateValue;
    @FXML private Label totalValue;
    @FXML private Label averageValue;
    @FXML private Label semesterRemarkLabel;
    @FXML private Label headmasterNameLabel;
    @FXML private Label conductRemarkLabel;

    @FXML private TableView<StudentSubjectResults> resultsTable;
    @FXML private TableColumn<StudentSubjectResults, String> subjectCol;
    @FXML private TableColumn<StudentSubjectResults, String> ct1Col;
    @FXML private TableColumn<StudentSubjectResults, String> groupWorkCol;
    @FXML private TableColumn<StudentSubjectResults, String> ct2Col;
    @FXML private TableColumn<StudentSubjectResults, String> projectCol;
    @FXML private TableColumn<StudentSubjectResults, String> classScoreCol;
    @FXML private TableColumn<StudentSubjectResults, String> examScoreCol;
    @FXML private TableColumn<StudentSubjectResults, String> calcExamCol;
    @FXML private TableColumn<StudentSubjectResults, String> subjectTotalCol;
    @FXML private TableColumn<StudentSubjectResults, String> gradeCol;
    @FXML private TableColumn<StudentSubjectResults, String> teacherRemarkCol;

    @FXML private GridPane conductGrid;

    // ---- action bar
    @FXML private Label  statusLabel;
    @FXML private Button downloadButton;

    private String staffId;

    @SuppressWarnings("unused")
    private String role;
    private Runnable onBack;

    private final ApiUtilityClass api = new ApiUtilityClass((StaffController) null);

    // ---- model
    private final ObservableList<StudentsHolder> students = FXCollections.observableArrayList();
    private final ObservableList<StudentSubjectResults> resultRows = FXCollections.observableArrayList();
    private StudentReportCard currentCard;

    // Friendly labels for the conduct booleans, in printed order. Backed by
    // a LinkedHashMap so iteration order is stable across builds.
    private static final Map<String, String> CONDUCT_LABELS = new LinkedHashMap<>();

    static {
        CONDUCT_LABELS.put("punctuality",             "Punctuality");
        CONDUCT_LABELS.put("attendance",              "Attendance");
        CONDUCT_LABELS.put("neatness",                "Neatness");
        CONDUCT_LABELS.put("respectForAuthority",     "Respect for authority");
        CONDUCT_LABELS.put("cooperationWithPeers",    "Cooperation with peers");
        CONDUCT_LABELS.put("classParticipation",      "Class participation");
        CONDUCT_LABELS.put("completionOfAssignments", "Completion of assignments");
        CONDUCT_LABELS.put("honesty",                 "Honesty");
    }

    @FXML
    private void initialize() {
        // Choice converters
        classChoice.setConverter(new StringConverter<>() {
            @Override public String toString(GradeCaching g) { return g == null ? "" : g.getLevelName(); }
            @Override public GradeCaching fromString(String s) { return null; }
        });
        semesterChoice.setConverter(new StringConverter<>() {
            @Override public String toString(SemesterCaching s) { return s == null ? "" : s.getSemesterName(); }
            @Override public SemesterCaching fromString(String s) { return null; }
        });

        // Populate filters from caches
        List<GradeCaching> grades = UtilityClass.gradeCacheManager.getGradesCache();
        if (grades != null) classChoice.getItems().setAll(grades);
        List<SemesterCaching> semesters = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (semesters != null) semesterChoice.getItems().setAll(semesters);

        // Students table
        studentIdCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentId())));
        studentNameCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentName())));
        studentsTable.setItems(students);

        // Loading a student fires when selection changes
        studentsTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> loadCardFor(newSel));

        // Results table
        resultsTable.setItems(resultRows);
        subjectCol.setCellValueFactory(c -> new SimpleStringProperty(safe(c.getValue().getSubjectName())));
        ct1Col.setCellValueFactory(c -> new SimpleStringProperty(safe(c.getValue().getClassTest1Score())));
        groupWorkCol.setCellValueFactory(c -> new SimpleStringProperty(safe(c.getValue().getGroupWorkScore())));
        ct2Col.setCellValueFactory(c -> new SimpleStringProperty(safe(c.getValue().getClassTest2Score())));
        projectCol.setCellValueFactory(c -> new SimpleStringProperty(safe(c.getValue().getProjectScore())));
        classScoreCol.setCellValueFactory(c -> new SimpleStringProperty(safe(c.getValue().getClassScore())));
        examScoreCol.setCellValueFactory(c -> new SimpleStringProperty(safe(c.getValue().getExamScore())));
        calcExamCol.setCellValueFactory(c -> new SimpleStringProperty(safe(c.getValue().getCalculatedExamScore())));
        subjectTotalCol.setCellValueFactory(c -> new SimpleStringProperty(safe(c.getValue().getTotal())));
        gradeCol.setCellValueFactory(c -> new SimpleStringProperty(safe(c.getValue().getGrade())));
        teacherRemarkCol.setCellValueFactory(c -> new SimpleStringProperty(safe(c.getValue().getTeacherRemark())));

        // When the class changes, refresh the student list.
        classChoice.valueProperty().addListener((obs, oldVal, newVal) -> {
            students.clear();
            clearCardPreview();
            if (newVal != null) loadStudentsFor(newVal);
        });
    }

    @Override
    public void attach(String staffId, String role, Runnable onBack) {
        this.staffId = staffId;
        this.role    = role;
        this.onBack  = onBack;

        // Auto-pick first class / semester so something is on screen.
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
    private void onReloadStudents() {
        GradeCaching grade = classChoice.getValue();
        if (grade != null) loadStudentsFor(grade);
    }

    // ---- student list loading ------------------------------------------

    private void loadStudentsFor(GradeCaching grade) {
        setStatus("Loading students…");
        api.loadClassStudents(grade.getLevelId(), staffId,
                () -> Platform.runLater(() -> {
                    List<StudentsHolder> list = UtilityClass.studentsHolder;
                    students.setAll(list == null ? List.of() : list);
                    setStatus(students.isEmpty()
                            ? "No students on this class."
                            : students.size() + " student(s).");
                }),
                () -> Platform.runLater(() -> setStatus("Could not load students.")),
                () -> Platform.runLater(() -> setStatus("Network issue while loading students.")));
    }

    // ---- card loading + rendering --------------------------------------

    private void loadCardFor(StudentsHolder student) {
        if (student == null) {
            clearCardPreview();
            return;
        }
        SemesterCaching semester = semesterChoice.getValue();
        if (semester == null) {
            setStatus("Pick a semester first.");
            return;
        }

        setStatus("Loading report card…");
        downloadButton.setDisable(true);
        currentCard = null;

        api.loadReportCard(student.getStudentId(), semester.getSemesterId(), staffId,
                card -> Platform.runLater(() -> applyCard(card)),
                () -> Platform.runLater(() -> {
                    if (currentCard != null) {
                        downloadButton.setDisable(false);
                        setStatus("");
                    } else {
                        setStatus("No data for this student.");
                    }
                }),
                () -> Platform.runLater(() -> setStatus("Could not load report card.")),
                () -> Platform.runLater(() -> setStatus("Network issue while loading card.")));
    }

    private void applyCard(StudentReportCard card) {
        currentCard = card;
        if (card == null) {
            clearCardPreview();
            return;
        }

        // School header
        institutionNameLabel.setText(safe(card.getInstitutionName(), "School Name"));
        institutionAddressLabel.setText(safe(card.getInstitutionAddress(), " "));
        headmasterNameLabel.setText(card.getHeadmasterName() != null && !card.getHeadmasterName().isBlank()
                ? card.getHeadmasterName() + "  (Headmaster)"
                : "Headmaster");
        issueDateValue.setText(safe(card.getIssueDate(), "—"));

        // Academics block
        var academics = card.getAcademics();
        if (academics != null) {
            studentNameValue.setText(safe(academics.getStudentName(), "—"));
            studentIdValue.setText(safe(academics.getStudentId(), "—"));
            classValue.setText(safe(academics.getGrade(), "—"));
            semesterValue.setText(safe(academics.getSemesterName(), "—"));
            positionValue.setText(safe(academics.getPosition(), "—"));
            totalValue.setText(academics.getTotalScore() == null ? "—" : String.valueOf(academics.getTotalScore()));
            averageValue.setText(academics.getAverageScore() == null ? "—" : String.valueOf(academics.getAverageScore()));

            List<StudentSubjectResults> rows = academics.getStudentSubjectResults();
            resultRows.setAll(rows == null ? List.of() : rows);

            semesterRemarkLabel.setText(safe(academics.getSemesterRemark(),
                    "(No class teacher remark yet)"));
        } else {
            resultRows.clear();
            studentNameValue.setText("—");
            studentIdValue.setText("—");
            classValue.setText("—");
            semesterValue.setText("—");
            positionValue.setText("—");
            totalValue.setText("—");
            averageValue.setText("—");
            semesterRemarkLabel.setText("(No academic results recorded)");
        }

        // Conduct grid
        rebuildConductGrid(card.getConduct());
    }

    private void clearCardPreview() {
        currentCard = null;
        downloadButton.setDisable(true);
        resultRows.clear();
        conductGrid.getChildren().clear();
        for (Label l : new Label[]{
                studentNameValue, studentIdValue, classValue, semesterValue,
                positionValue, issueDateValue, totalValue, averageValue}) {
            l.setText("—");
        }
        semesterRemarkLabel.setText("—");
        conductRemarkLabel.setText("");
        institutionNameLabel.setText("School Name");
        institutionAddressLabel.setText(" ");
        headmasterNameLabel.setText("Headmaster");
    }

    private void rebuildConductGrid(StudentConductRecord conduct) {
        conductGrid.getChildren().clear();
        // Layout: 4 columns × 2 rows of category labels (8 categories).
        int i = 0;
        for (Map.Entry<String, String> entry : CONDUCT_LABELS.entrySet()) {
            int row = i / 4;
            int col = i % 4;

            Label item = new Label(entry.getValue() + ":  " + markFor(conduct, entry.getKey()));
            item.getStyleClass().add("report-card-conduct-item");
            String mark = rawMarkFor(conduct, entry.getKey());
            item.getStyleClass().add(switch (mark) {
                case "yes"   -> "report-card-conduct-mark-yes";
                case "no"    -> "report-card-conduct-mark-no";
                default      -> "report-card-conduct-mark-unset";
            });
            conductGrid.add(item, col, row);
            i++;
        }

        // Conduct remark
        if (conduct != null && conduct.getConductRemark() != null && !conduct.getConductRemark().isBlank()) {
            conductRemarkLabel.setText("Observation: " + conduct.getConductRemark());
            conductRemarkLabel.setVisible(true);
            conductRemarkLabel.setManaged(true);
        } else {
            conductRemarkLabel.setText("");
            conductRemarkLabel.setVisible(false);
            conductRemarkLabel.setManaged(false);
        }
    }

    /** Resolve a category key to "✓" / "✗" / "—" for display. */
    private static String markFor(StudentConductRecord c, String key) {
        Boolean v = lookup(c, key);
        if (v == null) return "—";
        return v ? "✓" : "✗";
    }

    /** Resolve a category key to a CSS-friendly tag for styling. */
    private static String rawMarkFor(StudentConductRecord c, String key) {
        Boolean v = lookup(c, key);
        if (v == null) return "unset";
        return v ? "yes" : "no";
    }

    /** Read one of the eight booleans by key. */
    private static Boolean lookup(StudentConductRecord c, String key) {
        if (c == null) return null;
        return switch (key) {
            case "punctuality"             -> c.getPunctuality();
            case "attendance"              -> c.getAttendance();
            case "neatness"                -> c.getNeatness();
            case "respectForAuthority"     -> c.getRespectForAuthority();
            case "cooperationWithPeers"    -> c.getCooperationWithPeers();
            case "classParticipation"      -> c.getClassParticipation();
            case "completionOfAssignments" -> c.getCompletionOfAssignments();
            case "honesty"                 -> c.getHonesty();
            default -> null;
        };
    }

    // ---- download -------------------------------------------------------

    @FXML
    private void onDownload() {
        if (currentCard == null || currentCard.getAcademics() == null) return;
        SemesterCaching semester = semesterChoice.getValue();
        if (semester == null) return;

        String studentId   = currentCard.getAcademics().getStudentId();
        String studentName = currentCard.getAcademics().getStudentName();

        downloadButton.setDisable(true);
        setStatus("Requesting PDF…");

        String suggestedName = "Report Card - " + safe(studentName, studentId)
                + " - " + safe(semester.getSemesterName(), "Semester") + ".pdf";

        api.downloadReportCard(
                staffId, studentId, semester.getSemesterId(),
                suggestedName,
                this::parentWindow,
                () -> Platform.runLater(() -> {
                    setStatus("Saved.");
                    downloadButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setStatus("Could not generate report card.");
                    downloadButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setStatus("Network issue while downloading.");
                    downloadButton.setDisable(false);
                })
        );
    }

    // ---- helpers --------------------------------------------------------

    private Window parentWindow() {
        return downloadButton.getScene() == null ? null : downloadButton.getScene().getWindow();
    }

    private void setStatus(String text) {
        statusLabel.setText(text == null ? "" : text);
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String safe(String s, String fallback) {
        return (s == null || s.isBlank()) ? fallback : s;
    }
}
