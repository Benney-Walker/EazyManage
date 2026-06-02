package com.bbquantum.eazymanage.Controller.Academics.Subject;

import com.bbquantum.eazymanage.DTO.Academics.Subject.SubjectDTO;
import com.bbquantum.eazymanage.DTO.Academics.Subject.SubjectsHolder;
import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.Services.AcademicsService;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Optional;

public class FindSubjectController {

    // ---- left side
    @FXML private ChoiceBox<GradeCaching> classChoice;
    @FXML private Button fetchButton;
    @FXML private ListView<SubjectsHolder> subjectList;

    // ---- right side
    @FXML private ScrollPane formScroll;
    @FXML private Label headerLabel;
    @FXML private TextField                   nameField;
    @FXML private ChoiceBox<GradeCaching>     formClassChoice;
    @FXML private Label                       actionStatusLabel;
    @FXML private Button                      deleteButton;
    @FXML private Button                      saveButton;

    @SuppressWarnings("unused")
    private String role;
    private String staffId;

    private final ApiCore apiCore = new ApiCore();
    private final AcademicsService academicsService = new AcademicsService(apiCore);

    private final ObservableList<SubjectsHolder> rows = FXCollections.observableArrayList();

    /** Currently-loaded subject. Used to preserve subjectId on save/delete. */
    private SubjectDTO currentSubject;

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

        classChoice.setConverter(gradeConv);
        formClassChoice.setConverter(gradeConv);

        List<GradeCaching> grades = UtilityClass.gradeCacheManager.getGradesCache();
        if (grades != null) {
            classChoice.getItems().setAll(grades);
            formClassChoice.getItems().setAll(grades);
        }
        classChoice.valueProperty().addListener((obs, oldVal, newVal) ->
                fetchButton.setDisable(newVal == null));

        // List rendering — show subject names.
        subjectList.setItems(rows);
        subjectList.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(SubjectsHolder item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getSubjectName());
            }
        });

        // Click a row → fetch full data for that subject.
        subjectList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) loadSubjectData(newVal.getSubjectId());
        });
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
    }

    // ====================================================================
    // Fetch list
    // ====================================================================

    @FXML
    private void onFetch() {
        GradeCaching cls = classChoice.getValue();
        if (cls == null) return;

        fetchButton.setDisable(true);
        setActionStatus("Loading subjects…");
        hideForm();
        rows.clear();

        academicsService.loadClassSubjects(cls.getLevelId(), staffId,
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    List<SubjectsHolder> list = UtilityClass.subjectsHolder;
                    if (list == null || list.isEmpty()) {
                        setActionStatus("No subjects in this class.");
                        return;
                    }
                    rows.setAll(list);
                    setActionStatus("");
                }),
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setActionStatus(err == null || err.isBlank() ? "Could not load subjects." : err);
                }),
                () -> Platform.runLater(() -> {
                    fetchButton.setDisable(false);
                    setActionStatus("Network unavailable.");
                }));
    }

    // ====================================================================
    // Load full data for one subject
    // ====================================================================

    private void loadSubjectData(String subjectId) {
        if (subjectId == null) return;
        setActionStatus("Loading subject…");

        academicsService.fetchSubjectData(staffId, subjectId,
                () -> Platform.runLater(() -> {
                    SubjectDTO data = UtilityClass.subjectData;
                    if (data == null) {
                        setActionStatus("Could not load subject details.");
                        return;
                    }
                    populateForm(data);
                    setActionStatus("");
                }),
                () -> Platform.runLater(() -> {
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setActionStatus(err == null || err.isBlank() ? "Could not load subject." : err);
                }),
                () -> Platform.runLater(() -> setActionStatus("Network unavailable.")));
    }

    private void populateForm(SubjectDTO data) {
        currentSubject = data;
        nameField.setText(safe(data.getSubjectName()));
        headerLabel.setText("Editing: " + safe(data.getSubjectName()));

        if (data.getLevelId() != null) {
            for (GradeCaching g : formClassChoice.getItems()) {
                if (data.getLevelId().equals(g.getLevelId())) {
                    formClassChoice.setValue(g);
                    break;
                }
            }
        }

        formScroll.setVisible(true);
        formScroll.setManaged(true);
    }

    // ====================================================================
    // Save
    // ====================================================================

    @FXML
    private void onSave() {
        if (currentSubject == null) {
            setActionStatus("Pick a subject from the list first.");
            return;
        }
        String name = trim(nameField.getText());
        GradeCaching cls = formClassChoice.getValue();
        if (name.isEmpty()) { setActionStatus("Subject name is required."); return; }
        if (cls == null)    { setActionStatus("Pick a class."); return; }

        SubjectDTO subjectData = new SubjectDTO();
        subjectData.setSubjectId(currentSubject.getSubjectId());
        subjectData.setSubjectName(name);
        subjectData.setLevelId(cls.getLevelId());

        saveButton.setDisable(true);
        deleteButton.setDisable(true);
        setActionStatus("Saving…");

        academicsService.updateSubjectDetails(subjectData, staffId,
                () -> Platform.runLater(() -> {
                    setActionStatus("Saved.");
                    currentSubject = subjectData;
                    saveButton.setDisable(false);
                    deleteButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setActionStatus(err == null || err.isBlank() ? "Save failed." : err);
                    saveButton.setDisable(false);
                    deleteButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setActionStatus("Network unavailable.");
                    saveButton.setDisable(false);
                    deleteButton.setDisable(false);
                }));
    }

    // ====================================================================
    // Delete (with confirmation)
    // ====================================================================

    @FXML
    private void onDelete() {
        if (currentSubject == null) {
            setActionStatus("Pick a subject from the list first.");
            return;
        }


        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete subject \"" + safe(currentSubject.getSubjectName()) +
                        "\"? This cannot be undone.",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);
        confirm.setTitle("Confirm deletion");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        saveButton.setDisable(true);
        deleteButton.setDisable(true);
        setActionStatus("Deleting…");

        String deletedId   = currentSubject.getSubjectId();
        String deletedName = safe(currentSubject.getSubjectName());

        academicsService.deleteSubject(staffId, deletedId,
                () -> Platform.runLater(() -> {
                    setActionStatus("Deleted \"" + deletedName + "\".");
                    // Remove from the visible list and clear the form.
                    rows.removeIf(s -> deletedId.equals(s.getSubjectId()));
                    hideForm();
                    saveButton.setDisable(false);
                    deleteButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setActionStatus(err == null || err.isBlank() ? "Delete failed." : err);
                    saveButton.setDisable(false);
                    deleteButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setActionStatus("Network unavailable.");
                    saveButton.setDisable(false);
                    deleteButton.setDisable(false);
                }));
    }

    private void hideForm() {
        formScroll.setVisible(false);
        formScroll.setManaged(false);
        currentSubject = null;
    }

    private void setActionStatus(String s) { actionStatusLabel.setText(s == null ? "" : s); }
    private static String trim(String s)   { return s == null ? "" : s.trim(); }
    private static String safe(String s)   { return s == null ? "" : s; }
}
