package com.bbquantum.eazymanage.Controller.Academics;

import com.bbquantum.eazymanage.DTO.Institution.FindAndUpdateClassInfo;
import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.DTO.Institution.StaffCaching;
import com.bbquantum.eazymanage.Services.AcademicsService;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.util.List;

public class FindClassController {

    @FXML private ChoiceBox<GradeCaching> classChoice;
    @FXML private Button loadButton;
    @FXML private Label statusLabel;

    @FXML private ScrollPane formScroll;
    @FXML private Label      headerLabel;
    @FXML private TextField classNameField;
    @FXML private ChoiceBox<StaffCaching> instructorChoice;
    @FXML private Label      saveStatusLabel;
    @FXML private Button     saveButton;

    @SuppressWarnings("unused")
    private String role;
    private String staffId;

    private final ApiCore apiCore = new ApiCore();
    private final AcademicsService academicsService = new AcademicsService(apiCore);

    private FindAndUpdateClassInfo currentClass;

    @FXML
    private void initialize() {
        classChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(GradeCaching g) { return g == null ? "" : g.getLevelName(); }

            @Override
            public GradeCaching fromString(String s) { return null; }
        });

        List<GradeCaching> grades = UtilityClass.gradeCacheManager.getGradesCache();
        if (grades != null)
            classChoice.getItems().setAll(grades);

        classChoice.valueProperty().addListener((obs, oldVal, newVal) ->
                loadButton.setDisable(newVal == null));

        instructorChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(StaffCaching s) {
                if (s == null) return "";
                return safe(s.getStaffName()) + " (" + safe(s.getStaffId()) + ")";
            }
            @Override
            public StaffCaching fromString(String s) { return null; }
        });

        List<StaffCaching> staff = UtilityClass.staffCacheManager.getCachedStaffList();
        if (staff != null)
            instructorChoice.getItems().setAll(staff);
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
    }

    @FXML
    private void onLoad() {
        GradeCaching picked = classChoice.getValue();
        if (picked == null) return;

        loadButton.setDisable(true);
        setStatus("Loading…");
        hideForm();

        academicsService.findClassInfo(picked.getLevelId(), staffId,
                () -> Platform.runLater(() -> {
                    loadButton.setDisable(false);
                    FindAndUpdateClassInfo data = UtilityClass.findAndUpdateClassInfo;
                    if (data == null) {
                        setStatus("Class not found.");
                        return;
                    }
                    populateForm(data);
                    setStatus("");
                }),
                () -> Platform.runLater(() -> {
                    loadButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err == null || err.isBlank() ? "Could not load class info." : err);
                }),
                () -> Platform.runLater(() -> {
                    loadButton.setDisable(false);
                    setStatus("Network unavailable.");
                }));
    }

    private void populateForm(FindAndUpdateClassInfo data) {
        currentClass = data;
        classNameField.setText(safe(data.getLevelName()));
        headerLabel.setText("Editing: " + safe(data.getLevelName()));

        // Best-effort: try to preselect the staff in the dropdown whose
        // id matches data.getStaffId() (which on a fresh load IS the
        // current instructor, before any save).
        if (data.getStaffId() != null) {
            for (StaffCaching s : instructorChoice.getItems()) {
                if (data.getStaffId().equals(s.getStaffId())) {
                    instructorChoice.setValue(s);
                    break;
                }
            }
        }

        formScroll.setVisible(true);
        formScroll.setManaged(true);
    }

    @FXML
    private void onSave() {
        if (currentClass == null) {
            setSaveStatus("Load a class first.");
            return;
        }
        String newName = trim(classNameField.getText());
        if (newName.isEmpty()) {
            setSaveStatus("Class name is required.");
            return;
        }
        if (!newName.contains("_") || newName.contains(" ")) {
            setSaveStatus("Class name must contain '_' and no spaces (e.g. Grade_1).");
            return;
        }
        StaffCaching instructor = instructorChoice.getValue();
        if (instructor == null) {
            setSaveStatus("Pick a class teacher.");
            return;
        }

        // Build the update DTO: preserve levelId, write the new name and
        // the picked instructor's id into the {@code staffId} field.
        FindAndUpdateClassInfo out = new FindAndUpdateClassInfo();
        out.setLevelId(currentClass.getLevelId());
        out.setLevelName(newName);
        out.setStaffId(instructor.getStaffId());

        saveButton.setDisable(true);
        setSaveStatus("Saving…");

        academicsService.updateClassInfo(out, staffId,
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    setSaveStatus("Saved.");
                    currentClass = out;
                }),
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setSaveStatus(err == null || err.isBlank() ? "Save failed." : err);
                }),
                () -> Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    setSaveStatus("Network unavailable.");
                }));
    }

    private void hideForm() {
        formScroll.setVisible(false);
        formScroll.setManaged(false);
        currentClass = null;
    }

    private void setStatus(String s)     { statusLabel.setText(s == null ? "" : s); }
    private void setSaveStatus(String s) { saveStatusLabel.setText(s == null ? "" : s); }
    private static String trim(String s) { return s == null ? "" : s.trim(); }
    private static String safe(String s) { return s == null ? "" : s; }
}
