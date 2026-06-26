package com.bbquantum.eazymanage.DTO.Academics.Report;

public class SubjectScoresReport {

    private String subjectName;

    private String classScore;

    private String examScore;

    private String total;

    private String grade;

    private String gradeDescriptor;

    public SubjectScoresReport() {}

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getClassScore() {
        return classScore;
    }

    public void setClassScore(String classScore) {
        this.classScore = classScore;
    }

    public String getExamScore() {
        return examScore;
    }

    public void setExamScore(String examScore) {
        this.examScore = examScore;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getGradeDescriptor() {
        return gradeDescriptor;
    }

    public void setGradeDescriptor(String gradeDescriptor) {
        this.gradeDescriptor = gradeDescriptor;
    }
}
