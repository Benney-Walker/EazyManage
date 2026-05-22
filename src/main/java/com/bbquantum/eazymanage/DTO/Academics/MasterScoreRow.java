package com.bbquantum.eazymanage.DTO.Academics;

import java.util.Map;

public class MasterScoreRow {

    private String studentId;
    private String studentName;

    /** Per-subject totals, keyed by subjectId. */
    private Map<String, String> subjectScores;

    /** Aggregate row totals. */
    private String total;
    private String average;
    private String position;

    public MasterScoreRow() {}

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public Map<String, String> getSubjectScores() { return subjectScores; }
    public void setSubjectScores(Map<String, String> subjectScores) {
        this.subjectScores = subjectScores;
    }

    public String getTotal() { return total; }
    public void setTotal(String total) { this.total = total; }

    public String getAverage() { return average; }
    public void setAverage(String average) { this.average = average; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
}
