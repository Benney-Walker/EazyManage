package com.bbquantum.eazymanage.DTO.Offline;

import com.bbquantum.eazymanage.DTO.Academics.Results.StudentsScoresTable;

import java.util.List;

public class OfflineScoresReplay {

    private String subjectId;

    private String semesterId;

    private List<StudentsScoresTable> scores;

    public OfflineScoresReplay(String subjectId, String semesterId, List<StudentsScoresTable> scores) {
        this.subjectId = subjectId;
        this.semesterId = semesterId;
        this.scores = scores;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public String getSemesterId() {
        return semesterId;
    }

    public List<StudentsScoresTable> getScores() {
        return scores;
    }
}
