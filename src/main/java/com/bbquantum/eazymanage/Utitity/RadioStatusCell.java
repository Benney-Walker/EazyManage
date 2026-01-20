package com.bbquantum.eazymanage.Utitity;

import com.bbquantum.eazymanage.Constants.AttendanceStatus;
import javafx.geometry.Pos;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;

public class RadioStatusCell extends TableCell<StudentAttendanceUI, AttendanceStatus> {
    private final RadioButton radioButton = new RadioButton();

    private final AttendanceStatus targetStatus;

    public RadioStatusCell(AttendanceStatus targetStatus) {
        this.targetStatus = targetStatus;
        setAlignment(Pos.CENTER);

        radioButton.setOnAction(event -> {
            if (getTableRow() != null && getTableRow().getItem() != null) {
                StudentAttendanceUI studentAttendanceUI = getTableRow().getItem();

                studentAttendanceUI.setStatus(targetStatus);
            }
        });
    }

    @Override
    public void updateItem(AttendanceStatus attendanceStatus, boolean empty) {
        super.updateItem(attendanceStatus, empty);

        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
            setGraphic(null);
        } else {
            StudentAttendanceUI studentAttendanceUI = getTableRow().getItem();

            radioButton.setSelected(studentAttendanceUI.getStatus() == targetStatus);

            setGraphic(radioButton);
        }
    }
}
