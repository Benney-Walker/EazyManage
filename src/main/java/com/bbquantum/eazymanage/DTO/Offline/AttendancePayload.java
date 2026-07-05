package com.bbquantum.eazymanage.DTO.Offline;

public class AttendancePayload {

    public String studentId;

    public String staffId;

    public String levelId;

    public String status;

    public String dateMarked;

    public String operation;

    public AttendancePayload() {}

    public AttendancePayload(String staffId, String studentId, String levelId,
                             String status, String dateMarked, String operation) {
        this.studentId = studentId;
        this.staffId = staffId;
        this.levelId = levelId;
        this.status = status;
        this.dateMarked = dateMarked;
        this.operation = operation;
    }
}
