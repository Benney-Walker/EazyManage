package com.bbquantum.eazymanage.DTO.Offline;

import com.bbquantum.eazymanage.DTO.Attendance.StudentAttendance;

import java.util.List;

public class OfflineAttendanceReplay {

    private String levelId;

    private String dateMarked;

    private List<StudentAttendance> studentAttendance;

    public OfflineAttendanceReplay(String levelId, String dateMarked, List<StudentAttendance> studentAttendance) {
        this.levelId = levelId;
        this.dateMarked = dateMarked;
        this.studentAttendance = studentAttendance;
    }

    public String getLevelId() {
        return levelId;
    }

    public String getDateMarked() {
        return dateMarked;
    }

    public List<StudentAttendance> getStudentAttendance() {
        return studentAttendance;
    }
}
