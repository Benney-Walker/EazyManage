package com.bbquantum.eazymanage.DTO.Academics;

public class SbaRecord {

    private String studentId;
    private String studentName;
    private String subjectId;
    private String subjectName;
    private String semesterId;

    private String classTest1Score;
    private String groupWorkScore;
    private String classTest2Score;
    private String projectScore;
    private String classScore;
    private String examScore;
    private String calculatedExamScore;

    public SbaRecord() {}

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

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(String semesterId) {
        this.semesterId = semesterId;
    }

    public String getClassTest1Score() {
        return classTest1Score;
    }

    public void setClassTest1Score(String classTest1Score) {
        this.classTest1Score = classTest1Score;
    }

    public String getGroupWorkScore() {
        return groupWorkScore;
    }

    public void setGroupWorkScore(String groupWorkScore) {
        this.groupWorkScore = groupWorkScore;
    }

    public String getClassTest2Score() {
        return classTest2Score;
    }

    public void setClassTest2Score(String classTest2Score) {
        this.classTest2Score = classTest2Score;
    }

    public String getProjectScore() {
        return projectScore;
    }

    public void setProjectScore(String projectScore) {
        this.projectScore = projectScore;
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

    public String getCalculatedExamScore() {
        return calculatedExamScore;
    }

    public void setCalculatedExamScore(String calculatedExamScore) {
        this.calculatedExamScore = calculatedExamScore;
    }
}
