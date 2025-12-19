package com.bbquantum.eazymanage.DTO.Academics;

import java.util.List;

public class SubjectScores {
    private String subjectName;

    private String subjectId;

    private List<StudentsScoresTable> scoresTable;

    public SubjectScores(String subjectName, String subjectId, List<StudentsScoresTable> scoresTable) {
        this.subjectName = subjectName;
        this.subjectId = subjectId;
        this.scoresTable = scoresTable;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public List<StudentsScoresTable> getScoresTable() {
        return scoresTable;
    }

    public void setScoresTable(List<StudentsScoresTable> scoresTable) {
        this.scoresTable = scoresTable;
    }
}
