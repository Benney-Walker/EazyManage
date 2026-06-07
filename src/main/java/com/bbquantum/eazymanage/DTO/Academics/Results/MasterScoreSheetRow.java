package com.bbquantum.eazymanage.DTO.Academics.Results;

import java.util.List;
import java.util.Map;

public class MasterScoreSheetRow {

    private String studentId;

    private String studentName;

    private Map<String, Double> subjectScores;

    private Double studentTotalScore;

    private Double studentAverageScore;

    private Integer position;

    public MasterScoreSheetRow() {}

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

    public Map<String, Double> getSubjectScores() {
        return subjectScores;
    }

    public void setSubjectScores(Map<String, Double> subjectScores) {
        this.subjectScores = subjectScores;
    }

    public Double getStudentTotalScore() {
        return studentTotalScore;
    }

    public void setStudentTotalScore(Double studentTotalScore) {
        this.studentTotalScore = studentTotalScore;
    }

    public Double getStudentAverageScore() {
        return studentAverageScore;
    }

    public void setStudentAverageScore(Double studentAverageScore) {
        this.studentAverageScore = studentAverageScore;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
