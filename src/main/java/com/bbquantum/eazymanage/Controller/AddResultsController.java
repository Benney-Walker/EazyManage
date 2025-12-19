package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.DTO.Academics.StudentsScoresTable;
import com.bbquantum.eazymanage.DTO.Academics.SubjectScores;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AddResultsController implements Initializable {

    private UtilityClass utilityClass;

    // ADD STUDENT RESULTS
    @FXML private TextField subjectCodeField;
    @FXML private HBox subjectInfoBox;
    @FXML private Label subjectNameLabel;
    @FXML private Label subjectCodeLabel;
    @FXML private VBox noStudentsBox;
    @FXML private Separator separator1;
    @FXML private ScrollPane resultsTableContainer;
    @FXML private Button cancel;
    @FXML private Button saveScores;
    @FXML public TableView<StudentsScoresTable> studentsScoresTable;
    @FXML private TableColumn<StudentsScoresTable, String> studentIdColumn;
    @FXML private TableColumn<StudentsScoresTable, String> studentNameColumn;
    @FXML private TableColumn<StudentsScoresTable, String> classScoreColumn;
    @FXML private TableColumn<StudentsScoresTable, String> examScoreColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set cell value factories
        studentIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentId()));

        studentNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentName()));

        classScoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClassScore()));

        examScoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getExamScore()));

        // Make table editable
        studentsScoresTable.setEditable(true);

        // Make class score column editable
        classScoreColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        classScoreColumn.setOnEditCommit(event -> {
            StudentsScoresTable student = event.getRowValue();
            String newValue = event.getNewValue();
            if (isValidClassScore(newValue)) {
                student.setClassScore(newValue);
            } else {
                utilityClass.showError("Class score must be between 0 and 40");
                studentsScoresTable.refresh();
            }
        });

        // Make exam score column editable
        examScoreColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        examScoreColumn.setOnEditCommit(event -> {
            StudentsScoresTable student = event.getRowValue();
            String newValue = event.getNewValue();
            if (isValidExamScore(newValue)) {
                student.setExamScore(newValue);
            } else {
                utilityClass.showError("Exam score must be between 0 and 60");
                studentsScoresTable.refresh();
            }
        });

        utilityClass = new UtilityClass(this);
    }

    @FXML
    public void handleLoadStudents() {
        String subjectId = subjectCodeField.getText().trim();
        if (subjectId.isEmpty()) {
            utilityClass.showError("Subject code field is empty!");
            return;
        }
        utilityClass.loadSubjectStudents(subjectId);
    }

    public void displaySubjectStudents(SubjectScores subjectScores) {
        subjectNameLabel.setText(subjectScores.getSubjectName());
        subjectCodeLabel.setText(subjectScores.getSubjectId());

        // Use setAll() to replace items, not addAll()
        List<StudentsScoresTable> scores = subjectScores.getScoresTable();
        if (scores != null) {
            studentsScoresTable.getItems().setAll(scores);
        } else {
            studentsScoresTable.getItems().clear();
        }

        subjectInfoBox.setVisible(true);
        subjectInfoBox.setManaged(true);

        noStudentsBox.setVisible(false);
        noStudentsBox.setManaged(false);

        resultsTableContainer.setVisible(true);
        resultsTableContainer.setManaged(true);
    }

    public void noStudentsFound() {
        subjectInfoBox.setVisible(true);
        subjectInfoBox.setManaged(true);

        noStudentsBox.setVisible(true);
        noStudentsBox.setManaged(true);

        resultsTableContainer.setVisible(false);
        resultsTableContainer.setManaged(false);
    }

    @FXML
    public void handleSaveScores() {
        if (studentsScoresTable.getItems().isEmpty()) {
            utilityClass.showError("No students to save scores for!");
            return;
        }

        String subjectId = subjectCodeField.getText().trim();

        List<StudentsScoresTable> scores = studentsScoresTable.getItems();

        // Validate all fields first
        for (StudentsScoresTable student : scores) {
            if (student.getClassScore() == null || student.getClassScore().isBlank()) {
                utilityClass.showError("Class score is empty for " + student.getStudentId());
                return;
            }
            if (student.getExamScore() == null || student.getExamScore().isBlank()) {
                utilityClass.showError("Exam score is empty for " + student.getStudentId());
                return;
            }
        }

        utilityClass.processingButton(saveScores);

        // Track progress
        final int totalStudents = scores.size();
        final int[] savedCount = {0};
        final int[] failedCount = {0};

        for (StudentsScoresTable student : scores) {
            utilityClass.saveStudentScores(
                    student.getStudentId(),
                    subjectId,
                    student.getClassScore(),
                    student.getExamScore(),
                    () -> {
                        // On success
                        savedCount[0]++;
                        checkCompletion(savedCount[0], failedCount[0], totalStudents);
                    },
                    () -> {
                        // On failure
                        failedCount[0]++;
                        checkCompletion(savedCount[0], failedCount[0], totalStudents);
                    }
            );
        }
    }

    private void checkCompletion(int saved, int failed, int total) {
        if (saved + failed == total) {
            Platform.runLater(() -> {
                utilityClass.notProcessingButton(saveScores, "Save All Scores");
                if (failed == 0) {
                    utilityClass.showSuccess("All " + saved + " scores saved successfully!");
                } else {
                    utilityClass.showError(saved + " saved, " + failed + " failed");
                }
            });
        }
    }

    @FXML
    public void handleClearScores() {
        for (StudentsScoresTable student : studentsScoresTable.getItems()) {
            student.setClassScore("");
            student.setExamScore("");
        }
        studentsScoresTable.refresh();
    }

    private boolean isValidClassScore(String value) {
        try {
            int score = Integer.parseInt(value);
            return score >= 0 && score <= 40;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidExamScore(String value) {
        try {
            int score = Integer.parseInt(value);
            return score >= 0 && score <= 60;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}