package com.bbquantum.eazymanage.Controller.Report;

import com.bbquantum.eazymanage.DTO.Academics.Conduct.ConductReport;
import com.bbquantum.eazymanage.DTO.Academics.Report.StudentReportCard;
import com.bbquantum.eazymanage.DTO.Academics.Report.SubjectScoresReport;
import com.bbquantum.eazymanage.DTO.Caching.CachedClasses;
import com.bbquantum.eazymanage.DTO.Caching.CachedSemesters;
import com.bbquantum.eazymanage.DTO.Academics.Students.StudentsHolder;
import com.bbquantum.eazymanage.Reports.ReportViewerController;
import com.bbquantum.eazymanage.Services.AcademicsService;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Services.ReportsService;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportCardReportController implements ReportViewerController {

    // ---- header
    @FXML private Button backButton;

    // ---- filters
    @FXML private ChoiceBox<CachedClasses> classChoice;
    @FXML private ChoiceBox<CachedSemesters> semesterChoice;
    @FXML private Button refreshStudentsBtn;

    // ---- left: student picker
    @FXML private TableView<StudentsHolder> studentsTable;
    @FXML private TableColumn<StudentsHolder, String> studentIdCol;
    @FXML private TableColumn<StudentsHolder, String> studentNameCol;

    // ---- right pane: paper
    @FXML private Label schoolNameLabel;

    @FXML private Label nameValue;
    @FXML private Label positionValue;
    @FXML private Label studentIdValue;
    @FXML private Label gradeValue;
    @FXML private Label classSizeValue;
    @FXML private Label termValue;
    @FXML private Label yearValue;
    @FXML private Label vacationValue;
    @FXML private Label resumptionValue;

    @FXML private TableView<SubjectScoresReport>          subjectsTable;
    @FXML private TableColumn<SubjectScoresReport, String> subjectCol;
    @FXML private TableColumn<SubjectScoresReport, String> classScoreCol;
    @FXML private TableColumn<SubjectScoresReport, String> examScoreCol;
    @FXML private TableColumn<SubjectScoresReport, String> totalScoreCol;
    @FXML private TableColumn<SubjectScoresReport, String> gradeCol;
    @FXML private TableColumn<SubjectScoresReport, String> remarksCol;

    @FXML private GridPane conductGrid;

    @FXML private Label attendancePresentValue;
    @FXML private Label totalAttendanceValue;
    @FXML private Label promotedToValue;

    @FXML private Label peculiarIssueLabel;
    @FXML private Label facilitatorRemarkLabel;

    @FXML private Label instructorNameValue;
    @FXML private Label currentDateValue;

    // ---- action bar
    @FXML private Label    promotionLabel;
    @FXML private ChoiceBox<CachedClasses> promotionClassChoice;
    @FXML private Label  statusLabel;
    @FXML private Button promoteButton;
    @FXML private Button repeatButton;
    @FXML private Button downloadButton;

    private String staffId;

    @SuppressWarnings("unused")
    private String role;
    private Runnable onBack;

    // Phase 8d — direct service references.
    private final ApiCore           apiCore           = new ApiCore();
    private final AcademicsService  academicsService  = new AcademicsService(apiCore);
    private final ReportsService    reportsService    = new ReportsService(apiCore);

    // ---- model
    private final ObservableList<StudentsHolder> students   = FXCollections.observableArrayList();
    private final ObservableList<SubjectScoresReport>  subjectScoresRows = FXCollections.observableArrayList();

    private StudentReportCard currentCard;

    // Friendly labels for the six conduct categories, in printed order.
    private static final Map<String, String> CONDUCT_LABELS = new LinkedHashMap<>();

    // Rating enum string -> human label for display in the preview.
    private static final Map<String, String> RATING_HEADERS = new LinkedHashMap<>();

    static {
        CONDUCT_LABELS.put("regular",            "Regular");
        CONDUCT_LABELS.put("punctual",           "Punctual");
        CONDUCT_LABELS.put("physicalAppearance", "Physical Appearance");
        CONDUCT_LABELS.put("social",             "Social");
        CONDUCT_LABELS.put("emotional",          "Emotional");
        CONDUCT_LABELS.put("cognitiveSkills",    "Cognitive Skills");

        RATING_HEADERS.put("EXCELLENT",          "EXCELLENT");
        RATING_HEADERS.put("VERY_GOOD",          "VERY GOOD");
        RATING_HEADERS.put("GOOD",               "GOOD");
        RATING_HEADERS.put("FAIR",               "FAIR");
        RATING_HEADERS.put("NOT_TO_EXPECTATION", "NOT EXPECT.");
    }

    @FXML
    private void initialize() {
        // Choice converters
        classChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(CachedClasses g) {
                return g == null ? "" : g.getLevelName();
            }

            @Override
            public CachedClasses fromString(String s) { return null; }
        });

        semesterChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(CachedSemesters s) {

                return s == null ? "" : s.getSemesterName() + " " + s.getAcademicYear();
            }

            @Override
            public CachedSemesters fromString(String s) {
                return null;
            }
        });

        promotionClassChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(CachedClasses g) {
                return g == null ? "" : g.getLevelName();
            }

            @Override
            public CachedClasses fromString(String s) { return null; }
        });

        // Populate filters from caches
        List<CachedClasses> grades = UtilityClass.CACHED_CLASS_MANAGER.getGradesCache();
        if (grades != null) {
            classChoice.getItems().setAll(grades);
            promotionClassChoice.getItems().setAll(grades);
        }

        List<CachedSemesters> semesters = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (semesters != null)
            semesterChoice.getItems().setAll(semesters);

        // Students table
        studentIdCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentId())));
        studentNameCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentName())));
        studentsTable.setItems(students);

        // Loading a student fires when selection changes
        studentsTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> loadCardFor(newSel));

        // Subject table
        subjectsTable.setItems(subjectScoresRows);
        subjectCol.setCellValueFactory(    c -> new SimpleStringProperty(safe(c.getValue().getSubjectName())));
        classScoreCol.setCellValueFactory( c -> new SimpleStringProperty(safe(c.getValue().getClassScore())));
        examScoreCol.setCellValueFactory(  c -> new SimpleStringProperty(safe(c.getValue().getExamScore())));
        totalScoreCol.setCellValueFactory( c -> new SimpleStringProperty(safe(c.getValue().getTotal())));
        gradeCol.setCellValueFactory(      c -> new SimpleStringProperty(safe(c.getValue().getGrade())));
        // Remarks column displays gradeDescriptor (e.g. "EXCELLENT", "VERY GOOD").
        remarksCol.setCellValueFactory(    c -> new SimpleStringProperty(safe(c.getValue().getGradeDescriptor())));

        // When the class changes, refresh the student list.
        classChoice.valueProperty().addListener((obs, oldVal, newVal) -> {
            students.clear();
            clearCardPreview();
            if (newVal != null) loadStudentsFor(newVal);
        });

        clearCardPreview();
    }

    @Override
    public void attach(String staffId, String role, Runnable onBack) {
        this.staffId = staffId;
        this.role    = role;
        this.onBack  = onBack;

        // Show promotion controls only to teaching staff (matching legacy).
        boolean teacher = "TEACHING_STAFF".equals(role);
        for (Node n : List.of(promotionLabel, promotionClassChoice)) {
            n.setVisible(teacher);
            n.setManaged(teacher);
        }

        promotionClassChoice.setDisable(true);

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
        CachedClasses grade = classChoice.getValue();
        if (grade != null) loadStudentsFor(grade);
    }

    // ---- student list loading ------------------------------------------

    private void loadStudentsFor(CachedClasses grade) {
        refreshStudentsBtn.setDisable(true);

        setStatus("Loading students…");
        academicsService.loadClassStudents(grade.getLevelId(), staffId,
                () -> Platform.runLater(() -> {
                    List<StudentsHolder> list = UtilityClass.studentsHolder;
                    students.setAll(list == null ? List.of() : list);
                    setStatus(students.isEmpty()
                            ? "No students on this class."
                            : students.size() + " student(s).");
                }),
                () -> Platform.runLater(() -> setStatus("Could not load students.")),
                () -> Platform.runLater(() -> setStatus("Network issue while loading students.")));

        refreshStudentsBtn.setDisable(false);
    }

    // ---- card loading + rendering --------------------------------------
    private void loadCardFor(StudentsHolder student) {
        if (student == null) {
            clearCardPreview();
            return;
        }
        CachedSemesters semester = semesterChoice.getValue();
        if (semester == null) {
            setStatus("Pick a semester first.");
            return;
        }

        setStatus("Loading report card…");
        downloadButton.setDisable(true);
        promoteButton.setDisable(true);
        repeatButton.setDisable(true);
        currentCard = null;

        reportsService.loadReportCard(student.getStudentId(), semester.getSemesterId(), staffId,
                card -> Platform.runLater(() -> applyCard(card)),
                () -> Platform.runLater(() -> {
                    if (currentCard != null) {
                        if (semester.getSemesterName().equals("THIRD_TERM")) {
                            promotionClassChoice.setDisable(false);
                            promoteButton.setDisable(false);
                            repeatButton.setDisable(false);
                        }

                        downloadButton.setDisable(false);
                        setStatus("");
                    } else {
                        setStatus("No data for this student.");
                    }
                }),
                () -> Platform.runLater(() -> setStatus(ApiContext.getInstance().errorMessageContainer)),
                () -> Platform.runLater(() -> setStatus("Network issue while loading card.")));
    }

    private void applyCard(StudentReportCard card) {


        currentCard = card;
        if (card == null) {
            clearCardPreview();
            return;
        }

        schoolNameLabel.setText(safe(card.getSchoolName(), "School Name"));

        // Meta block
        nameValue.setText(           safe(card.getStudentName(),       "—"));
        positionValue.setText(       safe(card.getClassPosition(),     "—"));
        studentIdValue.setText(      safe(card.getStudentId(),         "—"));
        gradeValue.setText(          safe(card.getClassName(),         "—"));
        classSizeValue.setText(      safe(card.getTotalStudents(),     "—"));
        termValue.setText(           safe(card.getSemesterName(),      "—"));
        yearValue.setText(           safe(card.getAcademicYear(),      "—"));
        vacationValue.setText(       safe(card.getVacationDate(),      "—"));
        resumptionValue.setText(     safe(card.getResumptionDate(),    "—"));

        // Subject table — populated from subjectScores
        List<SubjectScoresReport> rows = card.getSubjectScores();
        subjectScoresRows.setAll(rows == null ? List.of() : rows);

        // Conduct grid — built at runtime
        rebuildConductGrid(card.getStudentConductReport());

        // Bottom rows
        attendancePresentValue.setText(safe(card.getAttendancePresent(), "—"));
        totalAttendanceValue.setText(  safe(card.getTotalAttendance(),   "—"));
        promotedToValue.setText(       safe(card.getPromotedTo(),        "—"));

        ConductReport conduct = card.getStudentConductReport();
        String remark = conduct == null ? "" : safe(conduct.getFacilitatorRemark());
        facilitatorRemarkLabel.setText(remark.isBlank()
                ? "(No facilitator's remark recorded)"
                : remark);
        String issue = conduct == null ? "" : safe(conduct.getPeculiarIssue());
        peculiarIssueLabel.setText(issue.isBlank()
                ? "(No Peculiar Issue recorded)"
                : issue);
        instructorNameValue.setText(safe(card.getInstructorName(), "—"));
        currentDateValue.setText(   safe(card.getCurrentDate(),    "—"));
    }

    private void clearCardPreview() {
        currentCard = null;
        downloadButton.setDisable(true);
        subjectScoresRows.clear();
        conductGrid.getChildren().clear();
        conductGrid.getColumnConstraints().clear();

        schoolNameLabel.setText("School Name");

        for (Label l : new Label[]{
                nameValue, positionValue, studentIdValue, gradeValue,
                classSizeValue, termValue, yearValue, vacationValue, resumptionValue,
                attendancePresentValue, totalAttendanceValue, promotedToValue,
                instructorNameValue, currentDateValue}) {
            if (l != null) l.setText("—");
        }
        facilitatorRemarkLabel.setText("");
    }

    private void rebuildConductGrid(ConductReport conduct) {
        conductGrid.getChildren().clear();
        conductGrid.getColumnConstraints().clear();

        // Column constraints: label column wider, rating columns equal.
        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setHgrow(Priority.SOMETIMES);
        labelCol.setMinWidth(160);
        labelCol.setPercentWidth(28);
        conductGrid.getColumnConstraints().add(labelCol);
        for (int i = 0; i < RATING_HEADERS.size(); i++) {
            ColumnConstraints c = new ColumnConstraints();
            c.setHgrow(Priority.SOMETIMES);
            c.setPercentWidth(14.4);  // 5 * 14.4 = 72%, balanced with the 28% label col
            c.setHalignment(HPos.CENTER);
            conductGrid.getColumnConstraints().add(c);
        }

        // Header row (row 0): blank label cell + 5 rating headers
        Label cornerHeader = new Label("CONDUCT");
        cornerHeader.getStyleClass().add("conduct-grid-header");
        conductGrid.add(cornerHeader, 0, 0);

        int colIdx = 1;
        for (String ratingHeader : RATING_HEADERS.values()) {
            Label h = new Label(ratingHeader);
            h.getStyleClass().add("conduct-grid-header");
            h.setMaxWidth(Double.MAX_VALUE);
            h.setAlignment(Pos.CENTER);
            conductGrid.add(h, colIdx++, 0);
        }

        // Six category rows
        int rowIdx = 1;
        for (Map.Entry<String, String> entry : CONDUCT_LABELS.entrySet()) {
            String categoryKey   = entry.getKey();
            String categoryLabel = entry.getValue();

            Label catLabel = new Label(categoryLabel);
            catLabel.getStyleClass().add("conduct-grid-row-label");
            conductGrid.add(catLabel, 0, rowIdx);

            String rating = rawRatingFor(conduct, categoryKey);

            // Add one cell per rating column, ticking the one that matches.
            int rc = 1;
            for (String ratingKey : RATING_HEADERS.keySet()) {
                Label cell = new Label(ratingKey.equals(rating) ? "\u2713" : "");
                cell.getStyleClass().add("conduct-grid-cell");
                cell.setMaxWidth(Double.MAX_VALUE);
                cell.setAlignment(Pos.CENTER);
                conductGrid.add(cell, rc++, rowIdx);
            }
            rowIdx++;
        }
    }

    /** Read one of the six rating strings by key, or null if unset. */
    private static String rawRatingFor(ConductReport c, String key) {
        if (c == null) return null;
        return switch (key) {
            case "regular"            -> c.getRegular();
            case "punctual"           -> c.getPunctual();
            case "physicalAppearance" -> c.getPhysicalAppearance();
            case "social"             -> c.getSocial();
            case "emotional"          -> c.getEmotional();
            case "cognitiveSkills"    -> c.getCognitiveSkills();
            default -> null;
        };
    }

    // ---- download -------------------------------------------------------

    @FXML
    private void onDownload() {
        if (currentCard == null) return;
        CachedSemesters semester = semesterChoice.getValue();
        if (semester == null) return;

        String studentId = currentCard.getStudentId();
        String studentName = currentCard.getStudentName();

        downloadButton.setDisable(true);
        setStatus("Requesting PDF…");

        String suggestedName = "Report Card - " + safe(studentName, studentId)
                + " - " + safe(semester.getSemesterName(), "Semester") + ".pdf";

        reportsService.downloadReportCard(
                staffId, studentId, semester.getSemesterId(), suggestedName,
                this::parentWindow,
                () -> Platform.runLater(() -> {
                    setStatus("Saved.");
                    downloadButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setStatus(ApiContext.getInstance().errorMessageContainer);
                    downloadButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setStatus("Network issue while downloading.");
                    downloadButton.setDisable(false);
                })
        );
    }

    @FXML
    private void onPromote() {
        if (currentCard == null) return;
        CachedSemesters semester = semesterChoice.getValue();
        if (semester == null) return;

        CachedClasses promotionClass = promotionClassChoice.getValue();
        if (promotionClass == null) {
            setStatus("Promotion class not selected.");
            return;
        }

        if (promotionClass == classChoice.getValue()) {
            setStatus("Student cannot be promoted to the same class");
            return;
        }

        String studentId = currentCard.getStudentId();

        promoteButton.setDisable(true);
        repeatButton.setDisable(true);
        downloadButton.setDisable(true);
        setStatus("Processing");

        reportsService.promoteStudent(staffId, studentId, semester.getSemesterId(),
                promotionClass.getLevelId(),
                () -> Platform.runLater(() -> {
                    setStatus(ApiContext.getInstance().successMessageContainer);
                    downloadButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setStatus(ApiContext.getInstance().errorMessageContainer);
                    promoteButton.setDisable(false);
                    repeatButton.setDisable(false);
                    downloadButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setStatus("Network Unavailable");
                    promoteButton.setDisable(false);
                    repeatButton.setDisable(false);
                    downloadButton.setDisable(false);
                })
        );
    }

    @FXML
    private void onRepeat() {
        if (currentCard == null) return;
        CachedSemesters semester = semesterChoice.getValue();
        if (semester == null) return;

        String studentId = currentCard.getStudentId();

        promoteButton.setDisable(true);
        repeatButton.setDisable(true);
        downloadButton.setDisable(true);
        setStatus("Processing");

        reportsService.repeatStudent(staffId, studentId, semester.getSemesterId(),
                () -> Platform.runLater(() -> {
                    setStatus(ApiContext.getInstance().successMessageContainer);
                    downloadButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setStatus(ApiContext.getInstance().errorMessageContainer);
                    promoteButton.setDisable(false);
                    repeatButton.setDisable(false);
                    downloadButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setStatus("Network Unavailable");
                    promoteButton.setDisable(false);
                    repeatButton.setDisable(false);
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
