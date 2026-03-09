package com.bbquantum.eazymanage.DTO.Academics;

public class AssignSubject {
    private String staffId;

    private String subjectName;

    private String gradeId;

    public AssignSubject(String staffId, String subjectName, String gradeId) {
        this.staffId = staffId;
        this.subjectName = subjectName;
        this.gradeId = gradeId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }
}
