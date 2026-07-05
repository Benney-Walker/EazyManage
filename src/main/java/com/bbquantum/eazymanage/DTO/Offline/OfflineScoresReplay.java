package com.bbquantum.eazymanage.DTO.Offline;

import com.bbquantum.eazymanage.DTO.Academics.Results.StudentsScoresTable;

public class OfflineScoresReplay {

    private String subjectId;

    private String semesterId;

    private StudentsScoresTable scores;

    public OfflineScoresReplay(String subjectId, String semesterId, StudentsScoresTable scores, String staffId) {
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

    public StudentsScoresTable getScores() {
        return scores;
    }
}
