package com.bbquantum.eazymanage.Controller.Results;

import com.bbquantum.eazymanage.DTO.Academics.Report.ViewClassSemesterReport;
import com.bbquantum.eazymanage.DTO.Academics.Results.ViewStudentsSubjectsResults;
import com.bbquantum.eazymanage.DTO.Caching.GradeCaching;
import com.bbquantum.eazymanage.DTO.Caching.SemesterCaching;
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

public class ViewResultsController {

    // ---- toolbar
    @FXML private ChoiceBox<GradeCaching> classChoice;
    @FXML private ChoiceBox<SemesterCaching> semesterChoice;
    @FXML private Button fetchButton;
    @FXML private Label statusLabel;

    // ---- master banner
    @FXML private HBox classInfoBanner;
    @FXML private Label contextTitleLabel;
    @FXML private Label contextSubtitleLabel;
    @FXML private Label rowCountLabel;

    // ---- master table
    @FXML private TableView<ViewClassSemesterReport> classResultsTable;
    @FXML private TableColumn<ViewClassSemesterReport, String> studentIdCol;
    @FXML private TableColumn<ViewClassSemesterReport, String> studentNameCol;
    @FXML private TableColumn<ViewClassSemesterReport, String> totalCol;
    @FXML private TableColumn<ViewClassSemesterReport, String> averageCol;
    @FXML private TableColumn<ViewClassSemesterReport, String> positionCol;
    @FXML private TableColumn<ViewClassSemesterReport, String> remarkCol;

    // ---- detail banner
    @FXML private HBox  detailBanner;
    @FXML private Label detailNameLabel;
    @FXML private Label detailMetaLabel;
    @FXML private Label subjectCountLabel;

    // ---- detail table
    @FXML private TableView<ViewStudentsSubjectsResults> subjectsTable;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> subjectNameCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> ct1Col;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> groupWorkCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> ct2Col;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> projectCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> classScoreCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> examCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> calcExamCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> subjectTotalCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> gradeCol;
    @FXML private TableColumn<ViewStudentsSubjectsResults, String> gradeDescriptor;

    // ---- identity (read for future audit/headers; not actively used here)
    @SuppressWarnings("unused")
    private String role;
    @SuppressWarnings("unused")
    private String staffId;

    // ---- services
    private final ApiCore apiCore = new ApiCore();
    private final AcademicsService academicsService = new AcademicsService(apiCore);

    // ---- model
    private final ObservableList<ViewClassSemesterReport> classRows   = FXCollections.observableArrayList();
    private final ObservableList<ViewStudentsSubjectsResults>  subjectRows = FXCollections.observableArrayList();
    private GradeCaching    currentClass;
    private SemesterCaching currentSemester;

    @FXML
    private void initialize() {
        classChoice.setConverter(new StringConverter<>() {
            @Override public String toString(GradeCaching g) { return g == null ? "" : g.getLevelName(); }
            @Override public GradeCaching fromString(String s) { return null; }
        });
        semesterChoice.setConverter(new StringConverter<>() {
            @Override public String toString(SemesterCaching s) { return s == null ? "" : s.getSemesterName(); }
            @Override public SemesterCaching fromString(String s) { return null; }
        });

        List<GradeCaching> grades = UtilityClass.gradeCacheManager.getGradesCache();
        if (grades != null) classChoice.getItems().setAll(grades);

        List<SemesterCaching> semesters = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (semesters != null) semesterChoice.getItems().setAll(semesters);

        classChoice.valueProperty().addListener((obs, oldVal, newVal) -> refreshFetchButtonState());
        semesterChoice.valueProperty().addListener((obs, oldVal, newVal) -> refreshFetchButtonState());

        // ---- Master table wiring
        classResultsTable.setItems(classRows);
        studentIdCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentId())));

        studentNameCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentName())));

        totalCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getTotalScore())));

        averageCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getAverageScore())));

        positionCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getClassPosition())));

        remarkCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getSemesterRemark())));

        // ---- Row click → populate detail
        classResultsTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) ->
                        applyDetail(newSel));

        // ---- Detail table wiring
        subjectsTable.setItems(subjectRows);

        subjectNameCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getSubjectName())));

        ct1Col.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getClassTest1Score())));

        groupWorkCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getGroupWorkScore())));

        ct2Col.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getClassTest2Score())));

        projectCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getProjectScore())));

        classScoreCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getClassScore())));

        examCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getExamScore())));

        calcExamCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getCalculatedExamScore())));

        subjectTotalCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getTotal())));

        gradeCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getGrade())));

        gradeDescriptor.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getDescription())));
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
    }

    private void refreshFetchButtonState() {
        fetchButton.setDisable(classChoice.getValue() == null || semesterChoice.getValue() == null);
    }

    // ====================================================================
    // Fetch class results
    // ====================================================================

    @FXML
    private void onFetch() {
        GradeCaching grade       = classChoice.getValue();
        SemesterCaching semester = semesterChoice.getValue();
        if (grade == null || semester == null) return;

        currentClass    = grade;
        currentSemester = semester;
        setStatus("Loading results…");
        fetchButton.setDisable(true);
        classRows.clear();
        subjectRows.clear();
        detailBanner.setVisible(false);
        detailBanner.setManaged(false);

        academicsService.loadClassResults(grade.getLevelId(), semester.getSemesterId(), staffId,
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    List<ViewClassSemesterReport> reports = UtilityClass.classResults;
                    populateMaster(reports);
                }),
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err == null || err.isBlank()
                            ? "Could not load results."
                            : err);
                }),
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    setStatus("Network unavailable.");
                }));
    }

    private void populateMaster(List<ViewClassSemesterReport> reports) {
        if (reports == null || reports.isEmpty()) {
            classInfoBanner.setVisible(false);
            classInfoBanner.setManaged(false);
            setStatus("No results for this class and semester.");
            return;
        }
        classRows.setAll(reports);
        classInfoBanner.setVisible(true);
        classInfoBanner.setManaged(true);
        contextTitleLabel.setText(safe(currentClass.getLevelName()));
        contextSubtitleLabel.setText(safe(currentSemester.getSemesterName()));
        rowCountLabel.setText(classRows.size() + " student" + (classRows.size() == 1 ? "" : "s"));
        setStatus("");
    }

    // ====================================================================
    // Detail population
    // ====================================================================

    private void applyDetail(ViewClassSemesterReport selected) {
        if (selected == null) {
            subjectRows.clear();
            detailBanner.setVisible(false);
            detailBanner.setManaged(false);
            return;
        }
        List<ViewStudentsSubjectsResults> subjects = selected.getScoresList();
        if (subjects == null) {
            subjectRows.clear();
        } else {
            subjectRows.setAll(subjects);
        }
        detailBanner.setVisible(true);
        detailBanner.setManaged(true);
        detailNameLabel.setText(safe(selected.getStudentName()) + " — " + safe(selected.getStudentId()));
        detailMetaLabel.setText("Total " + safe(selected.getTotalScore())
                + "   ·   Average " + safe(selected.getAverageScore())
                + "   ·   Position " + safe(selected.getClassPosition()));
        subjectCountLabel.setText(subjectRows.size() + " subject" + (subjectRows.size() == 1 ? "" : "s"));
    }

    // ====================================================================
    // Helpers
    // ====================================================================

    private void setStatus(String s) { statusLabel.setText(s == null ? "" : s); }
    private static String safe(String s) { return s == null ? "" : s; }
}
