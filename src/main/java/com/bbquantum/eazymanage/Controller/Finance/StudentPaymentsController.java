package com.bbquantum.eazymanage.Controller.Finance;

import com.bbquantum.eazymanage.DTO.Finance.PaymentHistory;
import com.bbquantum.eazymanage.DTO.Finance.StudentFeesPaymentDisplay;
import com.bbquantum.eazymanage.DTO.Caching.CachedClasses;
import com.bbquantum.eazymanage.DTO.Caching.CachedSemesters;
import com.bbquantum.eazymanage.DTO.Academics.Students.StudentsHolder;
import com.bbquantum.eazymanage.Services.AcademicsService;
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
import java.util.function.Consumer;

public class StudentPaymentsController {

    @FXML private ChoiceBox<StudentsHolder> studentChoice;
    @FXML private ChoiceBox<CachedClasses> classChoice;
    @FXML private ChoiceBox<CachedSemesters> semesterChoice;
    @FXML private Button fetchButton;
    @FXML private Button exportButton;
    @FXML private Label statusLabel;

    @FXML private HBox summaryBanner;
    @FXML private Label studentNameLabel;
    @FXML private Label contextLabel;
    @FXML private Label totalLabel;
    @FXML private Label paidLabel;
    @FXML private Label balanceLabel;

    @FXML private TableView<PaymentHistory> historyTable;
    @FXML private TableColumn<PaymentHistory, String> dateCol;
    @FXML private TableColumn<PaymentHistory, String> amountCol;
    @FXML private TableColumn<PaymentHistory, String> balanceCol;
    @FXML private TableColumn<PaymentHistory, String> payerCol;
    @FXML private TableColumn<PaymentHistory, String> contactCol;

    @SuppressWarnings("unused")
    private String role;
    private String staffId;

    private final ApiCore apiCore = new ApiCore();
    private final FinanceService financeService = new FinanceService(apiCore);
    private final AcademicsService academicsService = new AcademicsService(apiCore);

    private final ObservableList<PaymentHistory> rows = FXCollections.observableArrayList();
    private StudentFeesPaymentDisplay currentSnapshot;

    @FXML
    private void initialize() {
        classChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(CachedClasses g) {
                return g == null ? "" : g.getLevelName();
            }

            @Override
            public CachedClasses fromString(String s) {
                return null;
            }
        });

        semesterChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(CachedSemesters s) {
                return s == null ? "" : s.getSemesterName() + " " + s.getAcademicYear();
            }

            @Override
            public CachedSemesters fromString(String s) { return null; }
        });

        studentChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(StudentsHolder s) {
                return s == null ? "" : s.getStudentName();
            }

            @Override
            public StudentsHolder fromString(String s) { return null; }
        });

        List<CachedClasses> grades = UtilityClass.CACHED_CLASS_MANAGER.getGradesCache();
        List<CachedSemesters> sems = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (grades != null) classChoice.getItems().setAll(grades);
        if (sems != null) semesterChoice.getItems().setAll(sems);

        studentChoice.setDisable(true);

        classChoice.valueProperty().addListener((obs,  oldVal, newVal) ->
                cascadeLoadStudents(newVal, studentChoice, this::setStatus));

        historyTable.setItems(rows);
        dateCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getDateOfPayment())));

        amountCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getAmountPaid())));

        balanceCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getFeesBalance())));
        payerCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getPersonWhoPaid())));

        contactCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getContactInfo())));
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
    }

    @FXML
    private void onFetch() {
        StudentsHolder student = studentChoice.getValue();
        CachedClasses cls    = classChoice.getValue();
        CachedSemesters sem = semesterChoice.getValue();

        if (student == null || cls == null || sem == null) {
            setStatus("Fill all fields");
            return;
        }

        fetchButton.setDisable(true);
        exportButton.setDisable(true);
        setStatus("Loading…");
        rows.clear();
        hideBanner();

        financeService.fetchStudentPaymentRecords(staffId, student.getStudentId(), sem.getSemesterId(), cls.getLevelId(),
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    StudentFeesPaymentDisplay snapshot = UtilityClass.paymentRecords;
                    if (snapshot == null) {
                        setStatus("No payment records found.");
                        return;
                    }
                    currentSnapshot = snapshot;
                    List<PaymentHistory> history = snapshot.getPaymentHistory();
                    if (history != null) rows.setAll(history);
                    showBanner();
                    exportButton.setDisable(rows.isEmpty());
                    setStatus("");
                }),
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err == null || err.isBlank() ? "Could not load history." : err);
                }),
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    setStatus("Network unavailable.");
                }));
    }

    @FXML
    private void onExportCsv() {
        if (rows.isEmpty() || currentSnapshot == null) return;

        FileChooser fc = new FileChooser();
        fc.setTitle("Save CSV");
        fc.setInitialFileName(
                safe(currentSnapshot.getStudentId()) + "_"
                        + safe(currentSnapshot.getSemesterName()).replace(' ', '_') + "_payments.csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        Stage stage = (Stage) exportButton.getScene().getWindow();
        File file = fc.showSaveDialog(stage);
        if (file == null) return;

        try (FileWriter w = new FileWriter(file)) {
            // Summary header rows above the payment list, so the file
            // stands alone without needing the UI to interpret it.
            w.write("Student," + csv(currentSnapshot.getStudentName()) + "\n");
            w.write("Student ID," + csv(currentSnapshot.getStudentId()) + "\n");
            w.write("Class," + csv(currentSnapshot.getLevelName()) + "\n");
            w.write("Semester," + csv(currentSnapshot.getSemesterName()) + "\n");
            w.write("Total fees," + csv(currentSnapshot.getTotalFeesAmount()) + "\n");
            w.write("Total paid," + csv(currentSnapshot.getTotalAmountPaid()) + "\n");
            w.write("Balance," + csv(currentSnapshot.getFeesBalance()) + "\n");
            w.write("\n");
            w.write("Date,Amount,Balance After,Payer,Contact\n");
            for (PaymentHistory h : rows) {
                w.write(csv(h.getDateOfPayment()) + ","
                        + csv(h.getAmountPaid())    + ","
                        + csv(h.getFeesBalance())   + ","
                        + csv(h.getPersonWhoPaid()) + ","
                        + csv(h.getContactInfo())   + "\n");
            }
            setStatus("Exported to " + file.getName());
        } catch (Exception ex) {
            ex.printStackTrace();
            setStatus("Could not write CSV: " + ex.getMessage());
        }
    }

    private void cascadeLoadStudents(CachedClasses cls,
                                     ChoiceBox<StudentsHolder> target,
                                     Consumer<String> statusSink) {
        target.getItems().clear();
        target.getSelectionModel().clearSelection();
        if (cls == null) {
            target.setDisable(true);
            return;
        }
        target.setDisable(true);
        statusSink.accept("Loading students…");

        academicsService.loadClassStudents(cls.getLevelId(), staffId,
                () -> Platform.runLater(() -> {
                    List<StudentsHolder> list = UtilityClass.studentsHolder;
                    if (list == null || list.isEmpty()) {
                        statusSink.accept("No students in that class.");
                        return;
                    }
                    target.getItems().setAll(list);
                    target.setDisable(false);
                    statusSink.accept("");
                }),
                () -> Platform.runLater(() -> {
                    String err = ApiContext.getInstance().errorMessageContainer;
                    statusSink.accept(err == null || err.isBlank() ? "Could not load students." : err);
                }),
                () -> Platform.runLater(() -> statusSink.accept("Network unavailable.")));
    }

    private void showBanner() {
        summaryBanner.setVisible(true);
        summaryBanner.setManaged(true);
        studentNameLabel.setText(safe(currentSnapshot.getStudentName())
                + " — " + safe(currentSnapshot.getStudentId()));
        contextLabel.setText(safe(currentSnapshot.getLevelName())
                + " · " + safe(currentSnapshot.getSemesterName()));
        totalLabel.setText("Total: " + safe(currentSnapshot.getTotalFeesAmount()));
        paidLabel.setText("Paid: " + safe(currentSnapshot.getTotalAmountPaid()));
        balanceLabel.setText("Balance: " + safe(currentSnapshot.getFeesBalance()));
    }
    private void hideBanner() {
        summaryBanner.setVisible(false);
        summaryBanner.setManaged(false);
        currentSnapshot = null;
    }

    private void setStatus(String s) { statusLabel.setText(s == null ? "" : s); }
    private static String trim(String s) { return s == null ? "" : s.trim(); }
    private static String safe(String s) { return s == null ? "" : s; }
    private static String csv(String s) {
        if (s == null) return "";
        if (s.indexOf(',') >= 0 || s.indexOf('"') >= 0 || s.indexOf('\n') >= 0) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
