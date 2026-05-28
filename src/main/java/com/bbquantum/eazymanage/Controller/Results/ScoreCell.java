package com.bbquantum.eazymanage.Controller.Results;

import com.bbquantum.eazymanage.DTO.Academics.StudentsScoresTable;
import javafx.application.Platform;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.converter.DefaultStringConverter;

import java.util.List;

public class ScoreCell extends TextFieldTableCell<StudentsScoresTable, String> {

    private final List<TableColumn<StudentsScoresTable, String>> editableColumns;

    public ScoreCell(List<TableColumn<StudentsScoresTable, String>> editableColumns) {
        super(new DefaultStringConverter());
        this.editableColumns = editableColumns;
    }

    @Override
    public void startEdit() {
        super.startEdit();
        // After startEdit JavaFX swaps in the editor (a TextField). Attach
        // a key filter so we can intercept Enter/Tab/arrows BEFORE the
        // editor's default handler swallows them.
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
        } else if (code == KeyCode.TAB && ev.isShiftDown()) {
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

        // Commit the in-progress edit so onEditCommit fires (which the
        // controller hooks for recalc). commitEdit needs the editor's
        // current text; the underlying TextField provides it.
        String typed = ((javafx.scene.control.TextField) getGraphic()).getText();
        commitEdit(typed);

        // After commit, schedule the focus jump. runLater lets the table
        // settle (cancel the current edit, refresh derived columns) before
        // we open the next editor.
        final int curRow = getIndex();
        final TableColumn<StudentsScoresTable, String> curCol = getTableColumn();
        final int cStep = colStep;
        final int rStep = rowStep;
        Platform.runLater(() -> moveTo(curRow, curCol, cStep, rStep));
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
