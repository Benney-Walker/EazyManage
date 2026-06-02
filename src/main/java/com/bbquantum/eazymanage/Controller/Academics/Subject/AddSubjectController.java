package com.bbquantum.eazymanage.Controller.Academics.Subject;

import com.bbquantum.eazymanage.DTO.Academics.Subject.AddNewSubject;
import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.Services.AcademicsService;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Services.ApiCore;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.util.List;

public class AddSubjectController {

    @FXML private TextField nameField;
    @FXML private ChoiceBox<GradeCaching> classChoice;
    @FXML private Label statusLabel;
    @FXML private Button resetButton;
    @FXML private Button saveButton;

    @SuppressWarnings("unused")
    private String role;
    private String staffId;

    private final ApiCore apiCore = new ApiCore();
    private final AcademicsService academicsService = new AcademicsService(apiCore);

    @FXML
    private void initialize() {
        classChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(GradeCaching g) { return g == null ? "" : g.getLevelName(); }

            @Override
            public GradeCaching fromString(String s) { return null; }
        });

        List<GradeCaching> grades = UtilityClass.gradeCacheManager.getGradesCache();
        if (grades != null) classChoice.getItems().setAll(grades);
    }

    public void attach(String role, String staffId) {
        this.role    = role;
        this.staffId = staffId;
    }

    @FXML
    private void onSave() {
        String name = trim(nameField.getText());
        GradeCaching cls = classChoice.getValue();
        if (name.isEmpty()) { setStatus("Subject name is required."); return; }
        if (cls == null)    { setStatus("Pick a class."); return; }

        AddNewSubject newSubject = new AddNewSubject();
        newSubject.setSubjectName(name);
        newSubject.setGradeId(cls.getLevelId());

        saveButton.setDisable(true);
        resetButton.setDisable(true);
        setStatus("Adding…");

        academicsService.addNewSubject(newSubject, staffId,
                () -> Platform.runLater(() -> {
                    setStatus("Subject added.");
                    clearForm();
                    saveButton.setDisable(false);
                    resetButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    String err = ApiContext.getInstance().errorMessageContainer;
                    setStatus(err == null || err.isBlank() ? "Could not add subject." : err);
                    saveButton.setDisable(false);
                    resetButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> {
                    setStatus("Network unavailable.");
                    saveButton.setDisable(false);
                    resetButton.setDisable(false);
                }));
    }

    @FXML
    private void onReset() {
        clearForm();
        setStatus("");
    }

    private void clearForm() {
        nameField.clear();
        classChoice.getSelectionModel().clearSelection();
    }

    private void setStatus(String s) { statusLabel.setText(s == null ? "" : s); }
    private static String trim(String s) { return s == null ? "" : s.trim(); }
}
