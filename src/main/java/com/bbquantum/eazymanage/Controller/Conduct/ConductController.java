package com.bbquantum.eazymanage.Controller.Conduct;

import com.bbquantum.eazymanage.Constants.ConductRatings;
import com.bbquantum.eazymanage.DTO.Academics.Conduct.StudentConductRecord;
import com.bbquantum.eazymanage.DTO.Caching.CachedClasses;
import com.bbquantum.eazymanage.DTO.Caching.CachedSemesters;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Services.ConductService;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.*;

public class ConductController {

    // ---- toolbar
    @FXML private ChoiceBox<CachedClasses> classChoice;
    @FXML private ChoiceBox<CachedSemesters> semesterChoice;
    @FXML private Button refreshButton;

    // ---- left: student list
    @FXML private TableView<StudentConductRecord> studentsTable;
    @FXML private TableColumn<StudentConductRecord, String> studentIdCol;
    @FXML private TableColumn<StudentConductRecord, String> studentNameCol;

    // ---- right: editor
    @FXML private Label selectedStudentLabel;
    @FXML private VBox rubricContainer;
    @FXML private TextArea conductRemarkField;
    @FXML private TextArea peculiarIssueField;
    @FXML private Label saveStatusLabel;
    @FXML private Button saveButton;
    @FXML private Button resetButton;

    // ---- identity (set by AppShellController after FXML load)
    private String staffId;
    @SuppressWarnings("unused")
    private String role;

    // ---- model state
    private final ApiCore apiCore = new ApiCore();
    private final ConductService conductService = new ConductService(apiCore);
    private final ObservableList<StudentConductRecord> records = FXCollections.observableArrayList();
    private final Map<String, StudentConductRecord> recordsById = new HashMap<>();
    private StudentConductRecord currentlyEdited;

    private final List<Category> categories = List.of(
            new Category("REGULAR", StudentConductRecord::getRegular, StudentConductRecord::setRegular),
            new Category("PUNCTUAL", StudentConductRecord::getPunctual, StudentConductRecord::setPunctual),
            new Category("PHYSICAL APPEARANCE", StudentConductRecord::getPhysicalAppearance, StudentConductRecord::setPhysicalAppearance),
            new Category("SOCIAL", StudentConductRecord::getSocial, StudentConductRecord::setSocial),
            new Category("EMOTIONAL",  StudentConductRecord::getEmotional, StudentConductRecord::setEmotional),
            new Category("COGNITIVE SKILLS", StudentConductRecord::getCognitiveSkills, StudentConductRecord::setCognitiveSkills)
    );

    /** The toggle cell for [category][rating]. Built once in initialize(). */
    private ToggleButton[][] cells;
    /** One toggle group per category row so only one rating can be active. */
    private ToggleGroup[] rowGroups;

    @FXML
    private void initialize() {
        // ---- choice rendering
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
            public CachedSemesters fromString(String s) { return null; }
        });

        // Populate from caches; if empty, the user has not yet loaded their
        // dashboard. Leave the choice boxes empty in that case rather than
        // crashing — the dashboard auto-loads on app start anyway.
        List<CachedClasses> grades = UtilityClass.CACHED_CLASS_MANAGER.getGradesCache();
        if (grades != null) classChoice.getItems().setAll(grades);
        List<CachedSemesters> semesters = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (semesters != null) semesterChoice.getItems().setAll(semesters);

        // ---- student table columns
        studentIdCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getStudentId()));
        studentNameCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getStudentName()));
        studentsTable.setItems(records);
        studentsTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> bindEditorTo(newSel));

        buildRubricGrid();
        // Save/reset start disabled until a student is selected.
        saveButton.setDisable(true);
        resetButton.setDisable(true);
    }

    private void buildRubricGrid() {
        String[] ratingLabels = StudentConductRecord.RATING_LABELS;

        cells = new ToggleButton[categories.size()][ConductRatings.values().length];
        rowGroups = new ToggleGroup[categories.size()];

        // Header row
        HBox header = new HBox();
        header.getStyleClass().add("rubric-header-row");
        Label corner = new Label("CONDUCT");
        corner.getStyleClass().add("rubric-corner");
        corner.setMinWidth(170);
        corner.setMaxWidth(170);
        header.getChildren().add(corner);
        for (String rl : ratingLabels) {
            Label h = new Label(rl);
            h.getStyleClass().add("rubric-col-header");
            h.setAlignment(Pos.CENTER);
            HBox.setHgrow(h, Priority.ALWAYS);
            h.setMaxWidth(Double.MAX_VALUE);
            header.getChildren().add(h);
        }
        rubricContainer.getChildren().add(header);

        // Category rows
        for (int r = 0; r < categories.size(); r++) {
            Category cat = categories.get(r);
            HBox row = new HBox();
            row.getStyleClass().add("rubric-row");

            Label rowLabel = new Label(cat.getLabel());
            rowLabel.getStyleClass().add("rubric-row-label");
            rowLabel.setMinWidth(170);
            rowLabel.setMaxWidth(170);
            rowLabel.setWrapText(true);
            row.getChildren().add(rowLabel);

            ToggleGroup group = new ToggleGroup();
            rowGroups[r] = group;

            for (int c = 0; c < ConductRatings.values().length; c++) {
                ToggleButton cell = new ToggleButton();
                cell.getStyleClass().add("rubric-cell");
                cell.setToggleGroup(group);
                cell.setMaxWidth(Double.MAX_VALUE);
                cell.setMaxHeight(Double.MAX_VALUE);
                HBox.setHgrow(cell, Priority.ALWAYS);
                cell.setUserData(ConductRatings.values()[c]);

                // Click an already-selected cell to clear it. Without this,
                // a ToggleGroup never lets you get back to "none selected".
                final ToggleButton thisCell = cell;
                cell.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, ev -> {
                    if (thisCell.isSelected()) {
                        group.selectToggle(null);
                        ev.consume();
                    }
                });

                cells[r][c] = cell;
                row.getChildren().add(cell);


                cell.selectedProperty().addListener((obs, was, now) ->
                        cell.setText(now ? "\u2713" : ""));
            }
            rubricContainer.getChildren().add(row);
        }
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
        CachedClasses grade = classChoice.getValue();
        CachedSemesters semester = semesterChoice.getValue();
        if (grade == null || semester == null || staffId == null) return;

        setStatus("Loading…");
        conductService.loadClassConduct(grade.getLevelId(), semester.getSemesterId(), staffId,
                list -> Platform.runLater(() -> populateRecords(list)),
                () -> Platform.runLater(() -> setStatus("Loaded " + records.size() + " student(s)")),
                () -> Platform.runLater(() -> setStatus(ApiContext.getInstance().errorMessageContainer)),
                () -> Platform.runLater(() -> setStatus("Network issue — try again")));
    }

    private void populateRecords(List<StudentConductRecord> incoming) {
        records.clear();
        recordsById.clear();
        if (incoming == null) return;
        for (StudentConductRecord r : incoming) {
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

    /** Pull the selected student's record into the rubric grid. */
    private void bindEditorTo(StudentConductRecord r) {
        currentlyEdited = r;
        if (r == null) {
            selectedStudentLabel.setText("Select a student from the list to begin");
            clearGrid();
            conductRemarkField.clear();
            peculiarIssueField.clear();
            saveButton.setDisable(true);
            resetButton.setDisable(true);
            return;
        }
        selectedStudentLabel.setText(safe(r.getStudentName()) + "  ·  " + safe(r.getStudentId()));
        for (int row = 0; row < categories.size(); row++) {
            applyRating(row, categories.get(row).getGetter().apply(r));
        }
        conductRemarkField.setText(r.getConductRemark() == null ? "" : r.getConductRemark());
        peculiarIssueField.setText(r.getPeculiarIssue() == null ? "" : r.getPeculiarIssue());
        saveButton.setDisable(false);
        resetButton.setDisable(false);
        setStatus("");
    }

    /** Select the cell in row {@code row} matching {@code rating}, or clear
     *  the row if the rating is null / unrecognised. */
    private void applyRating(int row, String rating) {
        rowGroups[row].selectToggle(null);
        if (rating == null) return;

        int c = ConductRatings.valueOf(rating).ordinal();
        cells[row][c].setSelected(true);
    }

    private void clearGrid() {
        if (rowGroups == null) return;
        for (ToggleGroup g : rowGroups) g.selectToggle(null);
    }

    /** Read the selected rating for a category row, or null if none picked. */
    private String ratingOf(int row) {

        Toggle sel = rowGroups[row].getSelectedToggle();

        if (sel == null) {
            return null;
        }

        ConductRatings rating =
                (ConductRatings) sel.getUserData();

        return rating.name();
    }

    /** Push grid state into the bound record and persist. */
    @FXML
    private void onSave() {
        if (currentlyEdited == null) return;
        for (int row = 0; row < categories.size(); row++) {
            categories.get(row).getSetter().accept(currentlyEdited, ratingOf(row));
        }
        currentlyEdited.setConductRemark(conductRemarkField.getText());
        currentlyEdited.setPeculiarIssue(peculiarIssueField.getText());

        saveButton.setDisable(true);
        setStatus("Saving…");

        String savingId = currentlyEdited.getStudentId();

        conductService.saveStudentConduct(currentlyEdited, staffId,
                () -> Platform.runLater(() -> {
                    setStatus("Saved at " + nowText());
                    saveButton.setDisable(false);
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
}
