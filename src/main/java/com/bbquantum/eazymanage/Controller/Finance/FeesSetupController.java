package com.bbquantum.eazymanage.Controller.Finance;

import com.bbquantum.eazymanage.DTO.Finance.FetchFeesDetails;
import com.bbquantum.eazymanage.DTO.Caching.GradeCaching;
import com.bbquantum.eazymanage.DTO.Caching.SemesterCaching;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Services.FinanceService;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.List;

public class FeesSetupController {

    // ---- Add card
    @FXML private ChoiceBox<GradeCaching> addClassChoice;
    @FXML private ChoiceBox<SemesterCaching> addSemesterChoice;
    @FXML private TextField addAmountField;
    @FXML private Label addStatusLabel;
    @FXML private Button addButton;

    // ---- Update card
    @FXML private ChoiceBox<GradeCaching> updateClassChoice;
    @FXML private ChoiceBox<SemesterCaching> updateSemesterChoice;
    @FXML private Button loadButton;
    @FXML private Separator updateSeparator;
    @FXML private VBox updateBlock;
    @FXML private TextField updateAmountField;
    @FXML private Label updateStatusLabel;
    @FXML private Button updateButton;

    @SuppressWarnings("unused")
    private String role;
    private String staffId;

    private final ApiCore apiCore = new ApiCore();
    private final FinanceService financeService = new FinanceService(apiCore);

    /** Loaded current fees; preserved on save so the levelId / semesterId
     *  travel back to the update endpoint untouched. */
    private FetchFeesDetails currentFees;

    @FXML
    private void initialize() {
        StringConverter<GradeCaching> gradeConv = new StringConverter<>() {
            @Override
            public String toString(GradeCaching g) {
                return g == null ? "" : g.getLevelName();
            }

            @Override
            public GradeCaching fromString(String s) { return null; }
        };

        StringConverter<SemesterCaching> semConv = new StringConverter<>() {
            @Override
            public String toString(SemesterCaching s) {

                return s == null ? "" : s.getSemesterName() + " " + s.getAcademicYear();
            }

            @Override
            public SemesterCaching fromString(String s) { return null; }
        };

        addClassChoice.setConverter(gradeConv);
        updateClassChoice.setConverter(gradeConv);
        addSemesterChoice.setConverter(semConv);
        updateSemesterChoice.setConverter(semConv);

        List<GradeCaching> grades = UtilityClass.gradeCacheManager.getGradesCache();
        List<SemesterCaching> semesters = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (grades != null) {
            addClassChoice.getItems().setAll(grades);
            updateClassChoice.getItems().setAll(grades);
        }
        if (semesters != null) {
            addSemesterChoice.getItems().setAll(semesters);
            updateSemesterChoice.getItems().setAll(semesters);
        }

        // Load button enabled only when both update-side selections are made.
        Runnable refreshLoadEnabled = () -> loadButton.setDisable(
                updateClassChoice.getValue() == null || updateSemesterChoice.getValue() == null);
        updateClassChoice.valueProperty().addListener((obs, o, n) -> refreshLoadEnabled.run());
        updateSemesterChoice.valueProperty().addListener((obs, o, n) -> refreshLoadEnabled.run());
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
    }

    // ====================================================================
    // Add
    // ====================================================================

    @FXML
    private void onAdd() {
        GradeCaching cls    = addClassChoice.getValue();
        SemesterCaching sem = addSemesterChoice.getValue();
        String amount       = trim(addAmountField.getText());

        if (cls == null || sem == null || amount.isEmpty()) {
            setAddStatus("Fill all fields.");
            return;
        }
        if (!isNumeric(amount)) {
            setAddStatus("Amount must be a number.");
            return;
        }

        addButton.setDisable(true);
        setAddStatus("Adding…");

        financeService.addNewFees(staffId, cls.getLevelId(), sem.getSemesterId(), amount,
                () -> Platform.runLater(() -> {
                    addButton.setDisable(false);
                    setAddStatus("Fees added.");
                    addAmountField.clear();
                }),
                () -> Platform.runLater(() -> {
                    addButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setAddStatus(err == null || err.isBlank() ? "Could not add fees." : err);
                }),
                () -> Platform.runLater(() -> {
                    addButton.setDisable(false);
                    setAddStatus("Network unavailable.");
                }));
    }

    // ====================================================================
    // Load + Update
    // ====================================================================

    @FXML
    private void onLoad() {
        GradeCaching cls    = updateClassChoice.getValue();
        SemesterCaching sem = updateSemesterChoice.getValue();
        if (cls == null || sem == null) return;

        loadButton.setDisable(true);
        setUpdateStatus("Loading…");
        hideUpdateBlock();

        financeService.fetchFeesDetails(staffId, sem.getSemesterId(), cls.getLevelId(),
                fees -> Platform.runLater(() -> {
                    loadButton.setDisable(false);
                    if (fees == null) {
                        setUpdateStatus("No fees set for that combination yet — add them via the form above.");
                        return;
                    }
                    currentFees = fees;
                    updateAmountField.setText(safe(fees.getAmount()));
                    showUpdateBlock();
                    setUpdateStatus("");
                }),
                () -> { /* Consumer fired */ },
                () -> Platform.runLater(() -> {
                    loadButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setUpdateStatus(err == null || err.isBlank() ? "Could not load fees." : err);
                }),
                () -> Platform.runLater(() -> {
                    loadButton.setDisable(false);
                    setUpdateStatus("Network unavailable.");
                }));
    }

    @FXML
    private void onUpdate() {
        if (currentFees == null) { setUpdateStatus("Load fees first."); return; }
        String amount = trim(updateAmountField.getText());
        if (amount.isEmpty())  { setUpdateStatus("Amount is required."); return; }
        if (!isNumeric(amount)){ setUpdateStatus("Amount must be a number."); return; }

        // Preserve levelId / semesterId from the loaded record; only the
        // amount actually changes.
        currentFees.setAmount(amount);

        updateButton.setDisable(true);
        setUpdateStatus("Saving…");

        financeService.updateSemesterFees(staffId, currentFees,
                () -> Platform.runLater(() -> {
                    updateButton.setDisable(false);
                    setUpdateStatus("Saved.");
                }),
                () -> Platform.runLater(() -> {
                    updateButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setUpdateStatus(err == null || err.isBlank() ? "Save failed." : err);
                }),
                () -> Platform.runLater(() -> {
                    updateButton.setDisable(false);
                    setUpdateStatus("Network unavailable.");
                }));
    }

    private void showUpdateBlock() {
        updateSeparator.setVisible(true);
        updateSeparator.setManaged(true);
        updateBlock.setVisible(true);
        updateBlock.setManaged(true);
    }
    private void hideUpdateBlock() {
        updateSeparator.setVisible(false);
        updateSeparator.setManaged(false);
        updateBlock.setVisible(false);
        updateBlock.setManaged(false);
        currentFees = null;
    }

    private void setAddStatus(String s)    { addStatusLabel.setText(s == null ? "" : s); }
    private void setUpdateStatus(String s) { updateStatusLabel.setText(s == null ? "" : s); }
    private static String trim(String s)   { return s == null ? "" : s.trim(); }
    private static String safe(String s)   { return s == null ? "" : s; }
    private static boolean isNumeric(String s) {
        try { Double.parseDouble(s); return true; } catch (NumberFormatException e) { return false; }
    }
}
