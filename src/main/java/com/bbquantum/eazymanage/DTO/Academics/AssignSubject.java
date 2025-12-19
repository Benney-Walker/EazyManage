package com.bbquantum.eazymanage.DTO.Academics;

public class AssignSubject {
    private String subjectName;

    private String gradeId;

    private String semesterId;

    public AssignSubject(String subjectName, String gradeId, String semesterId) {
        this.subjectName = subjectName;
        this.gradeId = gradeId;
        this.semesterId = semesterId;
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

    public String getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(String semesterId) {
        this.semesterId = semesterId;
    }
}
