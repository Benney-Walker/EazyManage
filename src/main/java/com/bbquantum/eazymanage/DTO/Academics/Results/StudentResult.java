package com.bbquantum.eazymanage.DTO.Academics.Results;

import java.util.List;

public class StudentResult {
    private String studentId;

    private String studentName;

    private String className;

    private String semesterName;

    private Double totalScore;

    private Double averageScore;

    private String position;

    private String semesterRemark;

    private List<StudentSubjectResults> studentSubjectResults;

    public StudentResult(String studentId, String studentName, String grade, String semesterName, Double totalScore,
                         Double averageScore, String position, List<StudentSubjectResults> studentSubjectResults) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.className = grade;
        this.semesterName = semesterName;
        this.totalScore = totalScore;
        this.averageScore = averageScore;
        this.position = position;
        this.studentSubjectResults = studentSubjectResults;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getGrade() {
        return className;
    }

    public void setGrade(String grade) {
        this.className = grade;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getSemesterRemark() {
        return semesterRemark;
    }

    public void setSemesterRemark(String semesterRemark) {
        this.semesterRemark = semesterRemark;
    }

    public List<StudentSubjectResults> getStudentSubjectResults() {
        return studentSubjectResults;
    }

    public void setStudentSubjectResults(List<StudentSubjectResults> studentResultSubjects) {
        this.studentSubjectResults = studentResultSubjects;
    }
}
