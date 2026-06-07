package com.bbquantum.eazymanage.Controller.Finance;

import com.bbquantum.eazymanage.DTO.Finance.StudentPaymentRecords;
import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.DTO.Institution.SemesterCaching;
import com.bbquantum.eazymanage.DTO.Institution.StudentsHolder;
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
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Optional;

public class RecordPaymentsController {

    // ---- New payment
    @FXML private ChoiceBox<GradeCaching>    newClassChoice;
    @FXML private ChoiceBox<StudentsHolder>  newStudentChoice;
    @FXML private ChoiceBox<SemesterCaching> semesterChoice;
    @FXML private TextField amountField;
    @FXML private TextField payerNameField;
    @FXML private TextField payerPhoneField;
    @FXML private Label recordStatusLabel;
    @FXML private Button resetButton;
    @FXML private Button recordButton;

    // ---- Load + amend
    @FXML private ChoiceBox<GradeCaching>    loadClassChoice;
    @FXML private ChoiceBox<StudentsHolder>  loadStudentChoice;
    @FXML private ChoiceBox<SemesterCaching> loadSemesterChoice;
    @FXML private Label loadStatusLabel;
    @FXML private Button loadButton;

    @FXML private TableView<StudentPaymentRecords> paymentsTable;
    @FXML private TableColumn<StudentPaymentRecords, String> dateCol;
    @FXML private TableColumn<StudentPaymentRecords, String> amountCol;
    @FXML private TableColumn<StudentPaymentRecords, String> payerNameCol;
    @FXML private TableColumn<StudentPaymentRecords, String> payerPhoneCol;

    @FXML private VBox editBlock;
    @FXML private Label editHeaderLabel;
    @FXML private TextField editAmountField;
    @FXML private TextField editPayerNameField;
    @FXML private TextField editPayerPhoneField;
    @FXML private Label editStatusLabel;
    @FXML private Button deleteButton;
    @FXML private Button saveButton;

    @SuppressWarnings("unused")
    private String role;
    private String staffId;

    private final ApiCore apiCore = new ApiCore();
    private final FinanceService financeService = new FinanceService(apiCore);
    private final AcademicsService academicsService = new AcademicsService(apiCore);

    private final ObservableList<StudentPaymentRecords> rows = FXCollections.observableArrayList();
    private StudentPaymentRecords currentPayment;

    @FXML
    private void initialize() {
        // Converters
        StringConverter<SemesterCaching> semConv = new StringConverter<>() {
            @Override
            public String toString(SemesterCaching s) {
                return s == null ? "" : s.getSemesterName();
            }

            @Override
            public SemesterCaching fromString(String s) { return null; }
        };

        StringConverter<GradeCaching> gradeConv = new StringConverter<>() {
            @Override
            public String toString(GradeCaching g) {
                return g == null ? "" : g.getLevelName();
            }

            @Override
            public GradeCaching fromString(String s) { return null; }
        };

        StringConverter<StudentsHolder> studentConv = new StringConverter<>() {
            @Override
            public String toString(StudentsHolder s) {
                if (s == null) return "";
                return safe(s.getStudentName()) + " (" + safe(s.getStudentId()) + ")";
            }
            @Override
            public StudentsHolder fromString(String s) { return null; }
        };

        semesterChoice.setConverter(semConv);
        loadSemesterChoice.setConverter(semConv);
        newClassChoice.setConverter(gradeConv);
        loadClassChoice.setConverter(gradeConv);
        newStudentChoice.setConverter(studentConv);
        loadStudentChoice.setConverter(studentConv);

        List<SemesterCaching> sems = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (sems != null) {
            semesterChoice.getItems().setAll(sems);
            loadSemesterChoice.getItems().setAll(sems);
        }

        List<GradeCaching> grades = UtilityClass.gradeCacheManager.getGradesCache();
        if (grades != null) {
            newClassChoice.getItems().setAll(grades);
            loadClassChoice.getItems().setAll(grades);
        }

        newStudentChoice.setDisable(true);
        loadStudentChoice.setDisable(true);

        newClassChoice.valueProperty().addListener((obs, oldV, newV) ->
                cascadeLoadStudents(newV, newStudentChoice, this::setRecordStatus));
        loadClassChoice.valueProperty().addListener((obs, oldV, newV) ->
                cascadeLoadStudents(newV, loadStudentChoice, this::setLoadStatus));

        // Table
        paymentsTable.setItems(rows);
        dateCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getDateOfPayment())));

        amountCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getAmount())));

        payerNameCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getPayerName())));

        payerPhoneCol.setCellValueFactory(c ->
                new SimpleStringProperty(safe(c.getValue().getPayerContact())));

        paymentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) populateEdit(newV);
        });
    }

    private void cascadeLoadStudents(GradeCaching cls,
                                     ChoiceBox<StudentsHolder> target,
                                     java.util.function.Consumer<String> statusSink) {
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

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
    }

    // ====================================================================
    // New payment
    // ====================================================================

    @FXML
    private void onRecord() {
        StudentsHolder student = newStudentChoice.getValue();
        GradeCaching selectedClass = newClassChoice.getValue();
        SemesterCaching sem = semesterChoice.getValue();
        String amount    = trim(amountField.getText());
        String payerName = trim(payerNameField.getText());
        String payerPhone = trim(payerPhoneField.getText());

        if (selectedClass == null) {
            setRecordStatus("Pick a class first.");
            return;
        }
        if (student == null) {
            setRecordStatus("Pick a student.");
            return;
        }
        if (sem == null || amount.isEmpty() || payerName.isEmpty() || payerPhone.isEmpty()) {
            setRecordStatus("Fill all fields.");
            return;
        }
        if (!isNumeric(amount)) {
            setRecordStatus("Amount must be a number.");
            return;
        }

        recordButton.setDisable(true);
        resetButton.setDisable(true);
        setRecordStatus("Recording…");

        financeService.addNewFeesPayment(
                staffId,
                student.getStudentId(),
                selectedClass.getLevelId(),
                sem.getSemesterId(),
                amount, payerName, payerPhone,
                () -> Platform.runLater(() -> {
                    recordButton.setDisable(false);
                    resetButton.setDisable(false);
                    setRecordStatus("Payment recorded.");
                    clearNewPaymentForm();
                }),
                () -> Platform.runLater(() -> {
                    recordButton.setDisable(false);
                    resetButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setRecordStatus(err == null || err.isBlank() ? "Could not record payment." : err);
                }),
                () -> Platform.runLater(() -> {
                    recordButton.setDisable(false);
                    resetButton.setDisable(false);
                    setRecordStatus("Network unavailable.");
                }));
    }

    @FXML
    private void onReset() {
        clearNewPaymentForm();
        setRecordStatus("");
    }

    private void clearNewPaymentForm() {
        newClassChoice.getSelectionModel().clearSelection();
        newStudentChoice.getItems().clear();
        newStudentChoice.setDisable(true);
        semesterChoice.getSelectionModel().clearSelection();
        amountField.clear();
        payerNameField.clear();
        payerPhoneField.clear();
    }

    // ====================================================================
    // Load existing payments
    // ====================================================================

    @FXML
    private void onLoad() {
        GradeCaching cls       = loadClassChoice.getValue();
        StudentsHolder student = loadStudentChoice.getValue();
        SemesterCaching sem    = loadSemesterChoice.getValue();

        if (cls == null)     { setLoadStatus("Pick a class first."); return; }
        if (student == null) { setLoadStatus("Pick a student."); return; }
        if (sem == null)     { setLoadStatus("Pick a semester."); return; }

        loadButton.setDisable(true);
        setLoadStatus("Loading…");
        rows.clear();
        hideEditBlock();

        financeService.fetchPaymentRecord(staffId, student.getStudentId(), cls.getLevelId(), sem.getSemesterId(),
                list -> Platform.runLater(() -> {
                    loadButton.setDisable(false);
                    if (list == null || list.isEmpty()) {
                        setLoadStatus("No payments found for that student in this class+semester.");
                        return;
                    }
                    rows.setAll(list);
                    setLoadStatus(rows.size() + " payment" + (rows.size() == 1 ? "" : "s") + " loaded.");
                }),
                () -> { /* Consumer fired */ },
                () -> Platform.runLater(() -> {
                    loadButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setLoadStatus(err == null || err.isBlank() ? "Could not load payments." : err);
                }),
                () -> Platform.runLater(() -> {
                    loadButton.setDisable(false);
                    setLoadStatus("Network unavailable.");
                }));
    }

    // ====================================================================
    // Amend
    // ====================================================================

    private void populateEdit(StudentPaymentRecords row) {
        currentPayment = row;
        editAmountField.setText(safe(row.getAmount()));
        editPayerNameField.setText(safe(row.getPayerName()));
        editPayerPhoneField.setText(safe(row.getPayerContact()));
        editHeaderLabel.setText("Edit payment from " + safe(row.getDateOfPayment()));
        editBlock.setVisible(true);
        editBlock.setManaged(true);
    }

    @FXML
    private void onSave() {
        if (currentPayment == null) { setEditStatus("Click a row to edit it."); return; }
        String amount = trim(editAmountField.getText());
        String name   = trim(editPayerNameField.getText());
        String phone  = trim(editPayerPhoneField.getText());

        if (amount.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            setEditStatus("All edit fields are required.");
            return;
        }
        if (!isNumeric(amount)) { setEditStatus("Amount must be a number."); return; }

        // Build an update DTO preserving paymentId / studentId / etc.
        StudentPaymentRecords out = new StudentPaymentRecords();
        out.setPaymentId(currentPayment.getPaymentId());
        out.setDateOfPayment(currentPayment.getDateOfPayment());
        out.setStudentId(currentPayment.getStudentId());
        out.setStudentName(currentPayment.getStudentName());
        out.setSemesterId(currentPayment.getSemesterId());
        out.setAmount(amount);
        out.setPayerName(name);
        out.setPayerContact(phone);

        saveButton.setDisable(true);
        deleteButton.setDisable(true);
        setEditStatus("Saving…");

        financeService.updatePaymentDetails(staffId, out,
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    deleteButton.setDisable(false);
                    setEditStatus("Saved.");
                    // Update the row in the table so the user sees the new values.
                    int idx = rows.indexOf(currentPayment);
                    if (idx >= 0) rows.set(idx, out);
                    currentPayment = out;
                }),
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    deleteButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setEditStatus(err == null || err.isBlank() ? "Save failed." : err);
                }),
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    deleteButton.setDisable(false);
                    setEditStatus("Network unavailable.");
                }));
    }

    // ====================================================================
    // Delete
    // ====================================================================

    @FXML
    private void onDelete() {
        if (currentPayment == null) { setEditStatus("Click a row first."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete payment of GHS " + safe(currentPayment.getAmount())
                        + " from " + safe(currentPayment.getDateOfPayment()) + "?\n"
                        + "This cannot be undone.",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);
        confirm.setTitle("Confirm deletion");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        StudentPaymentRecords toDelete = currentPayment;
        saveButton.setDisable(true);
        deleteButton.setDisable(true);
        setEditStatus("Deleting…");

        financeService.deletePaymentRecord(staffId, toDelete.getPaymentId(),
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    deleteButton.setDisable(false);
                    setEditStatus("Deleted.");
                    rows.remove(toDelete);
                    hideEditBlock();
                }),
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    deleteButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setEditStatus(err == null || err.isBlank() ? "Delete failed." : err);
                }),
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    deleteButton.setDisable(false);
                    setEditStatus("Network unavailable.");
                }));
    }

    private void hideEditBlock() {
        editBlock.setVisible(false);
        editBlock.setManaged(false);
        currentPayment = null;
    }

    private void setRecordStatus(String s) { recordStatusLabel.setText(s == null ? "" : s); }
    private void setLoadStatus(String s)   { loadStatusLabel.setText(s == null ? "" : s); }
    private void setEditStatus(String s)   { editStatusLabel.setText(s == null ? "" : s); }
    private static String trim(String s)   { return s == null ? "" : s.trim(); }
    private static String safe(String s)   { return s == null ? "" : s; }
    private static boolean isNumeric(String s) {
        try { Double.parseDouble(s); return true; } catch (NumberFormatException e) { return false; }
    }
}
