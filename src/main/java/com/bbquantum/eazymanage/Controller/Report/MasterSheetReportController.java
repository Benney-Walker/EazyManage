package com.bbquantum.eazymanage.Controller.Report;

import com.bbquantum.eazymanage.DTO.Academics.Results.MasterScoreRow;
import com.bbquantum.eazymanage.DTO.Academics.Results.MasterScoreSheet;
import com.bbquantum.eazymanage.DTO.Academics.Results.MasterSheetSubjectColumn;
import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.DTO.Institution.SemesterCaching;
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

import java.util.List;
import java.util.Map;

public class MasterSheetReportController implements ReportViewerController {

    // ---- header
    @FXML private Button backButton;
    @FXML private Label subtitleLabel;

    // ---- filters
    @FXML private ChoiceBox<GradeCaching> classChoice;
    @FXML private ChoiceBox<SemesterCaching> semesterChoice;
    @FXML private Button loadButton;

    // ---- table (columns added at runtime)
    @FXML private TableView<MasterScoreRow> sheetTable;

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
    private final ApiCore        apiCore        = new ApiCore();
    private final ReportsService reportsService = new ReportsService(apiCore);

    private final ObservableList<MasterScoreRow> rows = FXCollections.observableArrayList();
    private MasterScoreSheet currentSheet;     // last successfully loaded sheet

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

    private void applySheet(MasterScoreSheet sheet) {
        currentSheet = sheet;
        if (sheet == null) return;

        sheetTable.getColumns().clear();
        rows.clear();

        // Left fixed columns
        sheetTable.getColumns().add(makeFixedColumn("Student ID", 110,
                MasterScoreRow::getStudentId));
        sheetTable.getColumns().add(makeFixedColumn("Name", 200,
                MasterScoreRow::getStudentName));

        // Dynamic subject columns
        List<MasterSheetSubjectColumn> subjectColumns = sheet.getSubjectColumns();
        if (subjectColumns != null) {
            for (MasterSheetSubjectColumn col : subjectColumns) {
                sheetTable.getColumns().add(makeSubjectColumn(col));
            }
        }

        // Right fixed aggregate columns
        sheetTable.getColumns().add(makeFixedColumn("Total", 80,
                MasterScoreRow::getTotal));
        sheetTable.getColumns().add(makeFixedColumn("Average", 80,
                MasterScoreRow::getAverage));
        sheetTable.getColumns().add(makeFixedColumn("Position", 80,
                MasterScoreRow::getPosition));

        if (sheet.getRows() != null) rows.addAll(sheet.getRows());

        int subjectCount = subjectColumns == null ? 0 : subjectColumns.size();
        sheetMetaLabel.setText(rows.size() + " student(s)  ·  " + subjectCount + " subject(s)");
    }

    /**
     * Build a column whose value is read straight off the {@code MasterScoreRow}
     * via a getter reference.
     */
    private static TableColumn<MasterScoreRow, String> makeFixedColumn(
            String heading, double width,
            java.util.function.Function<MasterScoreRow, String> getter) {

        TableColumn<MasterScoreRow, String> col = new TableColumn<>(heading);
        col.setPrefWidth(width);
        col.setCellValueFactory(cell -> new SimpleStringProperty(safe(getter.apply(cell.getValue()))));
        return col;
    }

    private static TableColumn<MasterScoreRow, String> makeSubjectColumn(
            MasterSheetSubjectColumn def) {

        String heading = def.getShortName() != null && !def.getShortName().isBlank()
                ? def.getShortName()
                : def.getSubjectName();
        final String subjectId = def.getSubjectId();

        TableColumn<MasterScoreRow, String> col = new TableColumn<>(heading);
        col.setPrefWidth(85);
        col.setCellValueFactory(cell -> {
            Map<String, String> scores = cell.getValue().getSubjectScores();
            String v = scores == null ? "" : scores.get(subjectId);
            return new SimpleStringProperty(safe(v));
        });
        return col;
    }

    // ---- helpers --------------------------------------------------------

    private Window parentWindow() {
        return downloadButton.getScene() == null ? null : downloadButton.getScene().getWindow();
    }

    private void setStatus(String text) {
        statusLabel.setText(text == null ? "" : text);
    }

    private static String safe(String s) { return s == null ? "" : s; }
}
