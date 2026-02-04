package com.bbquantum.eazymanage.DTO.Academics;

public class StudentsScoresTable {
    private String studentId;

    private String studentName;

    private String exercise1Score;

    private String classTestScore;

    private String exercise2Score;

    private String projectScore;

    private String classScore;

    private String examScore;

    private String calculatedExamScore;

    public StudentsScoresTable(String studentId, String studentName, String exercise1Score, String classTestScore,
                               String exercise2Score, String projectScore, String classScore, String examScore, String calculatedExamScore) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.exercise1Score = exercise1Score;
        this.classTestScore = classTestScore;
        this.exercise2Score = exercise2Score;
        this.projectScore = projectScore;
        this.classScore = classScore;
        this.examScore = examScore;
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

    public String getExercise1Score() {
        return exercise1Score;
    }

    public void setExercise1Score(String exercise1Score) {
        this.exercise1Score = exercise1Score;
    }

    public String getClassTestScore() {
        return classTestScore;
    }

    public void setClassTestScore(String classTestScore) {
        this.classTestScore = classTestScore;
    }

    public String getExercise2Score() {
        return exercise2Score;
    }

    public void setExercise2Score(String exercise2Score) {
        this.exercise2Score = exercise2Score;
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
