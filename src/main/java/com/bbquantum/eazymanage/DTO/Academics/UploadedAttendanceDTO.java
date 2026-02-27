package com.bbquantum.eazymanage.DTO.Academics;

public class UploadedAttendanceDTO {
    private String studentId;

    public UploadedAttendanceDTO(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }
}
