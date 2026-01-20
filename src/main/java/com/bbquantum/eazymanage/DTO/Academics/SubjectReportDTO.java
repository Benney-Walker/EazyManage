package com.bbquantum.eazymanage.DTO.Academics;

public class SubjectReportDTO {
    private String subjectName;

    private String classScore;

    private String examScore;

    private String total;

    private String grade;

    private String description;

    public SubjectReportDTO(String subjectName, String classScore, String examScore, String total,
                            String grade, String description) {
        this.subjectName = subjectName;
        this.classScore = classScore;
        this.examScore = examScore;
        this.total = total;
        this.grade = grade;
        this.description = description;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
