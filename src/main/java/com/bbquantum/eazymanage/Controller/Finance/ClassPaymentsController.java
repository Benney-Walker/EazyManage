package com.bbquantum.eazymanage.Controller.Finance;

import com.bbquantum.eazymanage.DTO.Finance.GradeFeesReport;
import com.bbquantum.eazymanage.DTO.Caching.GradeCaching;
import com.bbquantum.eazymanage.DTO.Caching.SemesterCaching;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Services.FinanceService;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class ClassPaymentsController {

    @FXML private ChoiceBox<GradeCaching> classChoice;
    @FXML private ChoiceBox<SemesterCaching> semesterChoice;
    @FXML private Button fetchButton;
    @FXML private Button                     exportButton;
    @FXML private Label statusLabel;

    @FXML private HBox infoBanner;
    @FXML private Label contextTitleLabel;
    @FXML private Label contextSubtitleLabel;
    @FXML private Label rowCountLabel;

    @FXML private TableView<GradeFeesReport> reportTable;
    @FXML private TableColumn<GradeFeesReport, String> studentIdCol;
    @FXML private TableColumn<GradeFeesReport, String> studentNameCol;
    @FXML private TableColumn<GradeFeesReport, String> totalCol;
    @FXML private TableColumn<GradeFeesReport, String> paidCol;
    @FXML private TableColumn<GradeFeesReport, String> outstandingCol;

    @SuppressWarnings("unused")
    private String role;
    private String staffId;

    private final ApiCore apiCore = new ApiCore();
    private final FinanceService financeService = new FinanceService(apiCore);

    private final ObservableList<GradeFeesReport> rows = FXCollections.observableArrayList();
    private GradeCaching    currentClass;
    private SemesterCaching currentSemester;

    @FXML
    private void initialize() {
        classChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(GradeCaching g) {
                return g == null ? "" : g.getLevelName();
            }

            @Override
            public GradeCaching fromString(String s) {
                return null;
            }
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
        List<SemesterCaching> sems = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (grades != null) classChoice.getItems().setAll(grades);
        if (sems != null) semesterChoice.getItems().setAll(sems);

        Runnable refreshFetchEnabled = () -> fetchButton.setDisable(
                classChoice.getValue() == null || semesterChoice.getValue() == null);
        classChoice.valueProperty().addListener((obs, o, n) -> refreshFetchEnabled.run());
        semesterChoice.valueProperty().addListener((obs, o, n) -> refreshFetchEnabled.run());

        // Table wiring
        reportTable.setItems(rows);
        studentIdCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentId())));

        studentNameCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getStudentName())));

        totalCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getTotalAmountToPay())));

        paidCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getAmountPayed())));

        outstandingCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getOutstanding())));
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
    }

    @FXML
    private void onFetch() {
        GradeCaching cls    = classChoice.getValue();
        SemesterCaching sem = semesterChoice.getValue();
        if (cls == null || sem == null) return;

        currentClass    = cls;
        currentSemester = sem;
        fetchButton.setDisable(true);
        exportButton.setDisable(true);
        setStatus("Loading…");
        rows.clear();
        hideBanner();

        financeService.fetchGradeFeesRecords(staffId, cls.getLevelId(), sem.getSemesterId(),
                list -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    if (list == null || list.isEmpty()) {
                        setStatus("No records found.");
                        return;
                    }
                    rows.setAll(list);
                    showBanner();
                    exportButton.setDisable(false);
                    setStatus("");
                }),
                () -> { /* Consumer fired */ },
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err == null || err.isBlank() ? "Could not load report." : err);
                }),
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    setStatus("Network unavailable.");
                }));
    }

    @FXML
    private void onExportCsv() {
        if (rows.isEmpty() || currentClass == null || currentSemester == null) return;

        FileChooser fc = new FileChooser();
        fc.setTitle("Save CSV");
        fc.setInitialFileName(currentClass.getLevelName() + "_"
                + currentSemester.getSemesterName().replace(' ', '_') + "_payments.csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        Stage stage = (Stage) exportButton.getScene().getWindow();
        File file = fc.showSaveDialog(stage);
        if (file == null) return;

        try (FileWriter w = new FileWriter(file)) {
            w.write("Student ID,Student Name,Total Due,Paid,Outstanding\n");
            for (GradeFeesReport r : rows) {
                w.write(csv(r.getStudentId())     + ","
                        + csv(r.getStudentName())   + ","
                        + csv(r.getTotalAmountToPay()) + ","
                        + csv(r.getAmountPayed())   + ","
                        + csv(r.getOutstanding())   + "\n");
            }
            setStatus("Exported to " + file.getName());
        } catch (Exception ex) {
            ex.printStackTrace();
            setStatus("Could not write CSV: " + ex.getMessage());
        }
    }

    private void showBanner() {
        infoBanner.setVisible(true);
        infoBanner.setManaged(true);
        contextTitleLabel.setText(safe(currentClass.getLevelName()));
        contextSubtitleLabel.setText(safe(currentSemester.getSemesterName()));
        rowCountLabel.setText(rows.size() + " student" + (rows.size() == 1 ? "" : "s"));
    }
    private void hideBanner() {
        infoBanner.setVisible(false);
        infoBanner.setManaged(false);
    }

    private void setStatus(String s) { statusLabel.setText(s == null ? "" : s); }
    private static String safe(String s) { return s == null ? "" : s; }
    /** Minimal CSV escaping: quote any value containing comma, quote, or newline. */
    private static String csv(String s) {
        if (s == null) return "";
        if (s.indexOf(',') >= 0 || s.indexOf('"') >= 0 || s.indexOf('\n') >= 0) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
