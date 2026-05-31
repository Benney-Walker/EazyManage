package com.bbquantum.eazymanage.DTO.Academics;

public class AssignSubject {

    private String subjectName;

    private String gradeId;

    public AssignSubject() {}

    public AssignSubject(String subjectName, String gradeId) {
        this.subjectName = subjectName;
        this.gradeId = gradeId;
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
