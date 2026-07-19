package com.bbquantum.eazymanage.DTO.Offline;

import com.bbquantum.eazymanage.DTO.Attendance.StudentAttendance;

import java.util.List;

public class AttendancePayload {

    public String levelId;

    public String dateMarked;

    public List<StudentAttendance> studentAttendance;

    public AttendancePayload(String levelId, String dateMarked, List<StudentAttendance> studentAttendance) {
        this.levelId = levelId;
        this.dateMarked = dateMarked;
        this.studentAttendance = studentAttendance;
    }
}
