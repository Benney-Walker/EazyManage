package com.bbquantum.eazymanage.Controller.Report;

import com.bbquantum.eazymanage.DTO.Academics.Results.MasterScoreSheet;
import com.bbquantum.eazymanage.DTO.Academics.Results.MasterScoreSheetRow;
import com.bbquantum.eazymanage.DTO.Caching.GradeCaching;
import com.bbquantum.eazymanage.DTO.Caching.SemesterCaching;
import com.bbquantum.eazymanage.Reports.ReportViewerController;
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

import java.util.*;
import java.util.function.Function;

public class MasterSheetReportController implements ReportViewerController {

    // ---- header
    @FXML private Button backButton;
    @FXML private Label subtitleLabel;

    // ---- filters
    @FXML private ChoiceBox<GradeCaching> classChoice;
    @FXML private ChoiceBox<SemesterCaching> semesterChoice;
    @FXML private Button loadButton;

    // ---- table (columns added at runtime)
    @FXML private TableView<MasterScoreSheetRow> sheetTable;

    // ---- action bar
    @FXML private Label  sheetMetaLabel;
    @FXML private Label  statusLabel;
    @FXML private Button downloadButton;

    // ---- identity / context
    private String staffId;
    @SuppressWarnings("unused")
    private String role;
    private Runnable onBack;

    // Phase 8d — direct service references.
    private final ApiCore apiCore = new ApiCore();
    private final ReportsService reportsService = new ReportsService(apiCore);

    private final ObservableList<MasterScoreSheetRow> rows = FXCollections.observableArrayList();
    private MasterScoreSheet currentSheet;     // last successfully loaded sheet

    @FXML
    private void initialize() {
        classChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(GradeCaching g) { return g == null ? "" : g.getLevelName(); }

            @Override
            public GradeCaching fromString(String s) { return null; }
        });

        semesterChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(SemesterCaching s) {
                return s == null ? "" : s.getSemesterName() + " " + s.getAcademicYear(); }

            @Override
            public SemesterCaching fromString(String s) { return null; }
        });

        List<GradeCaching> grades = UtilityClass.gradeCacheManager.getGradesCache();
        if (grades != null) classChoice.getItems().setAll(grades);
        List<SemesterCaching> semesters = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (semesters != null) semesterChoice.getItems().setAll(semesters);

        sheetTable.setItems(rows);
    }

    @Override
    public void attach(String staffId, String role, Runnable onBack) {
        this.staffId = staffId;
        this.role    = role;
        this.onBack  = onBack;

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
        sheetTable.getColumns().clear();
        downloadButton.setDisable(true);
        loadButton.setDisable(true);
        sheetMetaLabel.setText("");

        reportsService.loadMasterSheet(grade.getLevelId(), semester.getSemesterId(), staffId,
                sheet -> Platform.runLater(() -> applySheet(sheet)),
                () -> Platform.runLater(() -> {
                    setStatus(rows.isEmpty()
                            ? "No data for that selection."
                            : "Loaded " + rows.size() + " student(s).");
                    downloadButton.setDisable(rows.isEmpty());
                    loadButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setStatus("Could not load sheet.");
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

        downloadButton.setDisable(true);
        setStatus("Requesting PDF…");

        String suggestedName = "Master Sheet - " + safe(grade.getLevelName())
                + " - " + safe(semester.getSemesterName()) + ".pdf";

        reportsService.downloadMasterSheet(
                staffId, grade.getLevelId(), semester.getSemesterId(),
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

    /*==================================================
                       HELPERS
     ==================================================*/

    private void applySheet(MasterScoreSheet sheet) {
        currentSheet = sheet;
        if (sheet == null) return;

        sheetTable.getColumns().clear();
        rows.clear();

        List<MasterScoreSheetRow> records = sheet.getRecords();
        List<String> subjects = sheet.getSubjects();
        if (records == null || subjects == null) records = Collections.emptyList();

        Set<String> subjectColumns = new LinkedHashSet<>(subjects);


        // Left fixed columns
        sheetTable.getColumns().add(makeFixedColumn("Student ID", 110,
                MasterScoreSheetRow::getStudentId));
        sheetTable.getColumns().add(makeFixedColumn("Name", 200,
                MasterScoreSheetRow::getStudentName));

        // Dynamic subject columns. Each cell looks up the row's
        // subjectScore map by the column's subject name.
        for (String subjectName : subjectColumns) {
            sheetTable.getColumns().add(makeSubjectColumn(subjectName));
        }

        // Right fixed aggregate columns. Total + Average come from the
        // backend; Position is computed client-side because the backend
        // doesn't ship it.
        sheetTable.getColumns().add(makeFixedColumn("Total", 90,
                r -> formatDouble(r.getStudentTotalScore())));
        sheetTable.getColumns().add(makeFixedColumn("Average", 90,
                r -> formatDouble(r.getStudentAverageScore())));
        sheetTable.getColumns().add(makeFixedColumn("Position", 80,
                r -> formatInteger(r.getPosition())));

        rows.addAll(records);

        // Meta label uses the backend's class/semester names where given,
        // falling back to the picker selection's display labels otherwise.
        String classLabel = sheet.getClassName();
        if (classLabel == null || classLabel.isBlank()) {
            GradeCaching pickedClass = classChoice.getValue();
            classLabel = pickedClass == null ? "" : safe(pickedClass.getLevelName());
        }
        String semesterLabel = sheet.getSemesterName();
        if (semesterLabel == null || semesterLabel.isBlank()) {
            SemesterCaching pickedSemester = semesterChoice.getValue();
            semesterLabel = pickedSemester == null ? "" : safe(pickedSemester.getSemesterName());
        }

        sheetMetaLabel.setText(
                rows.size() + " student(s)  ·  "
                        + subjectColumns.size() + " subject(s)"
                        + (classLabel.isBlank()    ? "" : "  ·  " + classLabel)
                        + (semesterLabel.isBlank() ? "" : "  ·  " + semesterLabel));
    }

    private static TableColumn<MasterScoreSheetRow, String> makeFixedColumn(
            String heading, double width,
            Function<MasterScoreSheetRow, String> getter) {

        TableColumn<MasterScoreSheetRow, String> col = new TableColumn<>(heading);
        col.setPrefWidth(width);
        col.setCellValueFactory(cell -> new SimpleStringProperty(safe(getter.apply(cell.getValue()))));
        return col;
    }

    private static TableColumn<MasterScoreSheetRow, String> makeSubjectColumn(String subjectName) {
        TableColumn<MasterScoreSheetRow, String> col = new TableColumn<>(subjectName);
        col.setPrefWidth(95);
        col.setCellValueFactory(cell -> {
            Map<String, Double> scores = cell.getValue().getSubjectScores();
            Double v = scores == null ? null : scores.get(subjectName);
            return new SimpleStringProperty(formatDouble(v));
        });
        return col;
    }

    private static String formatDouble(Double d) {
        if (d == null) return "";
        if (d == Math.floor(d) && !Double.isInfinite(d)) {
            return Integer.toString(d.intValue());
        }
        // One decimal place is enough precision for scores.
        return String.format("%.1f", d);
    }

    /** A nullable Integer reads better blank than as "null". */
    private static String formatInteger(Integer i) {
        return i == null ? "" : Integer.toString(i);
    }

    private Window parentWindow() {
        return downloadButton.getScene() == null ? null : downloadButton.getScene().getWindow();
    }

    private void setStatus(String text) {
        statusLabel.setText(text == null ? "" : text);
    }

    private static String safe(String s) { return s == null ? "" : s; }
}
