package com.bbquantum.eazymanage.DTO.Offline;

public class OfflineAttendanceReplay {

    private String studentId;

    private String levelId;

    private String status;

    private String dateMarked;

    public OfflineAttendanceReplay(String staffId, String studentId, String levelId, String status, String dateMarked) {
        this.studentId = studentId;
        this.levelId = levelId;
        this.status = status;
        this.dateMarked = dateMarked;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getLevelId() {
        return levelId;
    }

    public String getStatus() {
        return status;
    }

    public String getDateMarked() {
        return dateMarked;
    }
}
