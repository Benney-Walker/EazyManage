package com.bbquantum.eazymanage.DTO.Offline;

import com.bbquantum.eazymanage.DTO.Academics.Results.StudentsScoresTable;

import java.util.List;

public class ScoresPayload {

    public String subjectId;

    public String semesterId;

    public String staffId;

    public List<StudentsScoresTable> scores;

    public ScoresPayload() {}

    public ScoresPayload(String subjectId, String semesterId, String staffId, List<StudentsScoresTable> scores) {
        this.subjectId = subjectId;
        this.semesterId = semesterId;
        this.staffId = staffId;
        this.scores = scores;
    }
}
