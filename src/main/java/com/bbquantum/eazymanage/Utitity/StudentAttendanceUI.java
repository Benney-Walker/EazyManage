package com.bbquantum.eazymanage.Utitity;

import com.bbquantum.eazymanage.Constants.AttendanceStatus;
import com.bbquantum.eazymanage.DTO.Institution.StudentAttendance;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StudentAttendanceUI {
    private final StringProperty levelId;

    private final StringProperty studentId;

    private final StringProperty studentName;

    private final ObjectProperty<AttendanceStatus> status;

    public StudentAttendanceUI(StudentAttendance studentAttendance) {
        this.levelId = new SimpleStringProperty(studentAttendance.getLevelId());
        this.studentId = new SimpleStringProperty(studentAttendance.getStudentId());
        this.studentName = new SimpleStringProperty(studentAttendance.getStudentName());
        this.status = new SimpleObjectProperty<>(AttendanceStatus.valueOf(studentAttendance.getStatus()));
    }

    public StudentAttendance toStudentAttendance() {
        return new StudentAttendance(
                levelId.get(),
                studentId.get(),
                studentName.get(),
                status.get().name()
        );
    }

    public StringProperty levelIdProperty() {
        return levelId;
    }

    public StringProperty studentNameProperty() {
        return studentName;
    }

    public String getStudentId() {
        return studentId.get();
    }

    public ObjectProperty<AttendanceStatus> statusProperty() {
        return status;
    }

    public AttendanceStatus getStatus() {
        return status.get();
    }

    public void setStatus(AttendanceStatus status) {
        this.status.set(status);
    }

    public String getStudentName() {
        return studentName.get();
    }
}
