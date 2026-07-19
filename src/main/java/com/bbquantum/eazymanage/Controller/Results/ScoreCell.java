package com.bbquantum.eazymanage.Controller.Results;

import com.bbquantum.eazymanage.DTO.Academics.Results.StudentsScoresTable;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.converter.DefaultStringConverter;

import java.util.List;

public class ScoreCell extends TextFieldTableCell<StudentsScoresTable, String> {

    private final List<TableColumn<StudentsScoresTable, String>> editableColumns;

    private final double maxScore;

    public ScoreCell(double maxScore, List<TableColumn<StudentsScoresTable, String>> editableColumns) {
        super(new DefaultStringConverter());
        this.editableColumns = editableColumns;
        this.maxScore = maxScore;
    }

    @Override
    public void startEdit() {
        super.startEdit();

        if (getGraphic() instanceof TextField tf) {
            tf.requestFocus();
            tf.selectAll();
            // Filter (not handler) so we run before TextField's defaults.
            tf.addEventFilter(KeyEvent.KEY_PRESSED, this::onEditorKey);
        }
    }

    private void onEditorKey(KeyEvent ev) {
        KeyCode code = ev.getCode();
        // Direction: +1 step forward, -1 step backward; column-only vs row-only.
        boolean handled = false;
        int colStep = 0, rowStep = 0;

        if (code == KeyCode.ENTER || (code == KeyCode.TAB && !ev.isShiftDown())) {
            colStep = +1;
            handled = true;
        } else if (code == KeyCode.TAB) {
            colStep = -1;
            handled = true;
        } else if (code == KeyCode.DOWN) {
            rowStep = +1;
            handled = true;
        } else if (code == KeyCode.UP) {
            rowStep = -1;
            handled = true;
        }

        if (!handled) return;
        ev.consume();

        String typed = ((TextField) getGraphic()).getText();

        typed = typed == null ? "" : typed.trim();

        // Empty
        if (typed.isEmpty()) {
            showAlert("Score can't be empty");
            ((TextField) getGraphic()).requestFocus();
            return;
        }

        // Reject anything except digits with one optional decimal point
        if (!typed.matches("\\d+(\\.\\d+)?")) {
            showAlert("Invalid value");
            ((TextField) getGraphic()).requestFocus();
            return;
        }

        double value;

        try {
            value = Double.parseDouble(typed);
        } catch (NumberFormatException ex) {
            ((TextField) getGraphic()).requestFocus();
            return;
        }

        // Reject negatives
        if (value < 0) {
            showAlert("Score cannot be negative");
            ((TextField) getGraphic()).requestFocus();
            return;
        }

        if (value > maxScore) {
            showAlert("Score should not be more than " + maxScore);
            ((TextField) getGraphic()).requestFocus();
            return;
        }

        commitEdit(typed);

        final int curRow = getIndex();
        final TableColumn<StudentsScoresTable, String> curCol = getTableColumn();
        final int cStep = colStep;
        final int rStep = rowStep;
        Platform.runLater(() -> moveTo(curRow, curCol, cStep, rStep));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void moveTo(int curRow,
                        TableColumn<StudentsScoresTable, String> curCol,
                        int colStep, int rowStep) {
        TableView<StudentsScoresTable> table = getTableView();
        if (table == null || editableColumns.isEmpty()) return;
        int rowCount = table.getItems().size();
        if (rowCount == 0) return;

        int targetRow = curRow;
        TableColumn<StudentsScoresTable, String> targetCol = curCol;

        if (rowStep != 0) {
            targetRow = curRow + rowStep;
            // Down past the last row stays on the last row; up past the
            // first stays on the first. Spreadsheet feel.
            if (targetRow < 0) targetRow = 0;
            if (targetRow >= rowCount) targetRow = rowCount - 1;
        } else if (colStep != 0) {
            int idx = editableColumns.indexOf(curCol);
            if (idx < 0) idx = 0;
            int next = idx + colStep;
            if (next >= editableColumns.size()) {
                // End of row -> wrap to first editable col of next row.
                targetRow = curRow + 1;
                next = 0;
                if (targetRow >= rowCount) {
                    // Last cell of last row: leave editor closed, focus
                    // stays on the last cell so the user sees they are done.
                    table.getSelectionModel().clearAndSelect(curRow, curCol);
                    table.requestFocus();
                    return;
                }
            } else if (next < 0) {
                targetRow = curRow - 1;
                next = editableColumns.size() - 1;
                if (targetRow < 0) {
                    table.getSelectionModel().clearAndSelect(curRow, curCol);
                    table.requestFocus();
                    return;
                }
            }
            targetCol = editableColumns.get(next);
        }

        // Select + scroll + open the editor on the target cell.
        final int finalRow = targetRow;
        final TableColumn<StudentsScoresTable, String> finalCol = targetCol;
        TablePosition<StudentsScoresTable, ?> pos = new TablePosition<>(table, finalRow, finalCol);
        table.getSelectionModel().clearSelection();
        table.getFocusModel().focus(pos);
        table.getSelectionModel().select(finalRow, finalCol);
        table.scrollTo(finalRow);
        table.scrollToColumn(finalCol);
        // Open the editor on the next frame -- some JavaFX versions ignore
        // edit() if it lands inside the same pulse as the previous commit.
        Platform.runLater(() -> table.edit(finalRow, finalCol));
    }
}
