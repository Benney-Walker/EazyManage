package com.bbquantum.eazymanage.Shell;

import com.bbquantum.eazymanage.DTO.Academics.StudentConductRecord;
import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.DTO.Institution.SemesterCaching;
import com.bbquantum.eazymanage.Utitity.ApiUtilityClass;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.util.*;
import java.util.stream.Collectors;

public class ConductController {

    // ---- toolbar
    @FXML private ChoiceBox<GradeCaching> classChoice;
    @FXML private ChoiceBox<SemesterCaching> semesterChoice;
    @FXML private Button refreshButton;

    // ---- left: student list
    @FXML private TableView<StudentConductRecord> studentsTable;
    @FXML private TableColumn<StudentConductRecord, String> studentIdCol;
    @FXML
    private TableColumn<StudentConductRecord, String> studentNameCol;

    // ---- right: editor
    @FXML private Label selectedStudentLabel;
    @FXML private CheckBox punctualityCheck;
    @FXML private CheckBox attendanceCheck;
    @FXML private CheckBox neatnessCheck;
    @FXML private CheckBox respectForAuthorityCheck;
    @FXML private CheckBox cooperationWithPeersCheck;
    @FXML private CheckBox classParticipationCheck;
    @FXML private CheckBox completionOfAssignmentsCheck;
    @FXML private CheckBox honestyCheck;
    @FXML private TextArea conductRemarkField;
    @FXML private Label saveStatusLabel;
    @FXML private Button saveButton;
    @FXML private Button resetButton;

    // ---- identity (set by AppShellController after FXML load)
    private String staffId;
    @SuppressWarnings("unused")  // kept for symmetry with other shell controllers; conduct doesn't yet branch on role
    private String role;

    // ---- model state
    private final ApiUtilityClass api = new ApiUtilityClass((com.bbquantum.eazymanage.Controller.StaffController) null);
    private final ObservableList<StudentConductRecord> records = FXCollections.observableArrayList();
    private final Map<String, StudentConductRecord> recordsById = new HashMap<>();
    private StudentConductRecord currentlyEdited;

    @FXML
    private void initialize() {
        // ---- choice rendering
        classChoice.setConverter(new StringConverter<>() {
            @Override public String toString(GradeCaching g) { return g == null ? "" : g.getLevelName(); }
            @Override public GradeCaching fromString(String s) { return null; }
        });
        semesterChoice.setConverter(new StringConverter<>() {
            @Override public String toString(SemesterCaching s) { return s == null ? "" : s.getSemesterName(); }
            @Override public SemesterCaching fromString(String s) { return null; }
        });

        // Populate from caches; if empty, the user has not yet loaded their
        // dashboard. Leave the choice boxes empty in that case rather than
        // crashing — the dashboard auto-loads on app start anyway.
        List<GradeCaching> grades = UtilityClass.gradeCacheManager.getGradesCache();
        if (grades != null) classChoice.getItems().setAll(grades);
        List<SemesterCaching> semesters = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (semesters != null) semesterChoice.getItems().setAll(semesters);

        // ---- student table columns
        studentIdCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getStudentId()));
        studentNameCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getStudentName()));
        studentsTable.setItems(records);
        studentsTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> bindEditorTo(newSel));

        // Save/reset start disabled until a student is selected.
        saveButton.setDisable(true);
        resetButton.setDisable(true);
    }

    /** Called by the shell after FXML load. Triggers initial data load if both selectors have a default. */
    public void setIdentity(String staffId, String role) {
        this.staffId = staffId;
        this.role    = role;

        // Auto-pick the first class and semester if available so the screen
        // feels populated on entry. The user can change them at any time.
        if (!classChoice.getItems().isEmpty() && classChoice.getValue() == null) {
            classChoice.getSelectionModel().selectFirst();
        }
        if (!semesterChoice.getItems().isEmpty() && semesterChoice.getValue() == null) {
            semesterChoice.getSelectionModel().selectFirst();
        }
        if (classChoice.getValue() != null && semesterChoice.getValue() != null) {
            reload();
        }
    }

    @FXML
    private void onReload() { reload(); }

    private void reload() {
        GradeCaching grade = classChoice.getValue();
        SemesterCaching semester = semesterChoice.getValue();
        if (grade == null || semester == null || staffId == null) return;

        setStatus("Loading…");
        api.loadClassConduct(grade.getLevelId(), semester.getSemesterId(), staffId,
                list -> Platform.runLater(() -> populateRecords(list)),
                () -> Platform.runLater(() -> setStatus("Loaded " + records.size() + " student(s)")),
                () -> Platform.runLater(() -> setStatus("Could not load records")),
                () -> Platform.runLater(() -> setStatus("Network issue — try again")));
    }

    private void populateRecords(List<StudentConductRecord> incoming) {
        records.clear();
        recordsById.clear();
        if (incoming == null) return;
        for (StudentConductRecord r : incoming) {
            // Stamp the semesterId locally so saves go to the right place even
            // if the backend didn't echo it back.
            if (r.getSemesterId() == null && semesterChoice.getValue() != null) {
                r.setSemesterId(semesterChoice.getValue().getSemesterId());
            }
            records.add(r);
            recordsById.put(r.getStudentId(), r);
        }
        if (!records.isEmpty()) {
            studentsTable.getSelectionModel().selectFirst();
        } else {
            bindEditorTo(null);
        }
    }

    /** Pull the selected student's record into the editor controls. */
    private void bindEditorTo(StudentConductRecord r) {
        currentlyEdited = r;
        if (r == null) {
            selectedStudentLabel.setText("Select a student from the list to begin");
            setIndeterminate(punctualityCheck, attendanceCheck, neatnessCheck, respectForAuthorityCheck,
                    cooperationWithPeersCheck, classParticipationCheck, completionOfAssignmentsCheck, honestyCheck);
            conductRemarkField.clear();
            saveButton.setDisable(true);
            resetButton.setDisable(true);
            return;
        }
        selectedStudentLabel.setText(safe(r.getStudentName()) + "  ·  " + safe(r.getStudentId()));
        applyBool(punctualityCheck,             r.getPunctuality());
        applyBool(attendanceCheck,              r.getAttendance());
        applyBool(neatnessCheck,                r.getNeatness());
        applyBool(respectForAuthorityCheck,     r.getRespectForAuthority());
        applyBool(cooperationWithPeersCheck,    r.getCooperationWithPeers());
        applyBool(classParticipationCheck,      r.getClassParticipation());
        applyBool(completionOfAssignmentsCheck, r.getCompletionOfAssignments());
        applyBool(honestyCheck,                 r.getHonesty());
        conductRemarkField.setText(r.getConductRemark() == null ? "" : r.getConductRemark());
        saveButton.setDisable(false);
        resetButton.setDisable(false);
        setStatus("");
    }

    /** Push current editor state into the bound record and persist to backend. */
    @FXML
    private void onSave() {
        if (currentlyEdited == null) return;
        currentlyEdited.setPunctuality            (booleanOf(punctualityCheck));
        currentlyEdited.setAttendance             (booleanOf(attendanceCheck));
        currentlyEdited.setNeatness               (booleanOf(neatnessCheck));
        currentlyEdited.setRespectForAuthority    (booleanOf(respectForAuthorityCheck));
        currentlyEdited.setCooperationWithPeers   (booleanOf(cooperationWithPeersCheck));
        currentlyEdited.setClassParticipation     (booleanOf(classParticipationCheck));
        currentlyEdited.setCompletionOfAssignments(booleanOf(completionOfAssignmentsCheck));
        currentlyEdited.setHonesty                (booleanOf(honestyCheck));
        currentlyEdited.setConductRemark          (conductRemarkField.getText());

        saveButton.setDisable(true);
        setStatus("Saving…");

        // Snapshot the id so callbacks aren't fooled by a row-switch mid-save.
        String savingId = currentlyEdited.getStudentId();

        api.saveStudentConduct(currentlyEdited, staffId,
                () -> Platform.runLater(() -> {
                    setStatus("Saved at " + nowText());
                    saveButton.setDisable(false);
                    // Refresh the row in the table so any future re-bind sees latest values
                    refreshRow(savingId);
                }),
                () -> Platform.runLater(() -> {
                    setStatus("Save failed — check details");
                    saveButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setStatus("Network issue while saving");
                    saveButton.setDisable(false);
                }));
    }

    /** Reload the editor from the model (discards unsaved edits for this row). */
    @FXML
    private void onReset() {
        bindEditorTo(currentlyEdited);
    }

    // ---- helpers --------------------------------------------------------

    private static void applyBool(CheckBox box, Boolean v) {
        if (v == null) {
            box.setIndeterminate(true);
            box.setSelected(false);
        } else {
            box.setIndeterminate(false);
            box.setSelected(v);
        }
    }

    /** Convert a tri-state CheckBox into a nullable Boolean. */
    private static Boolean booleanOf(CheckBox box) {
        if (box.isIndeterminate()) return null;
        return box.isSelected();
    }

    private static void setIndeterminate(CheckBox... boxes) {
        for (CheckBox b : boxes) {
            b.setIndeterminate(true);
            b.setSelected(false);
        }
    }

    private void setStatus(String text) {
        saveStatusLabel.setText(text == null ? "" : text);
    }

    private void refreshRow(String studentId) {
        // Find the row in the observable list and replace it with itself, which
        // makes the TableView reapply cellValueFactories without a full reload.
        for (int i = 0; i < records.size(); i++) {
            if (Objects.equals(records.get(i).getStudentId(), studentId)) {
                StudentConductRecord same = records.get(i);
                records.set(i, same);
                break;
            }
        }
    }

    private static String safe(String s) { return s == null ? "" : s; }

    private static String nowText() {
        return java.time.LocalTime.now().withNano(0).toString();
    }

    // Constructor-call helper retained for parity with code-review style.
    @SuppressWarnings("unused")
    private static List<String> studentIdsOf(List<StudentConductRecord> rs) {
        return rs == null ? new ArrayList<>() : rs.stream().map(StudentConductRecord::getStudentId).collect(Collectors.toList());
    }
}
