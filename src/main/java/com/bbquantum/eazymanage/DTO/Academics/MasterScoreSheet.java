package com.bbquantum.eazymanage.DTO.Academics;

import java.util.List;

public class MasterScoreSheet {

    private String levelId;
    private String levelName;
    private String semesterId;
    private String semesterName;

    /** Column definitions, in render order. */
    private List<MasterSheetSubjectColumn> subjectColumns;

    /** One row per student in the class. */
    private List<MasterScoreRow> rows;

    public MasterScoreSheet() {}

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(String semesterId) {
        this.semesterId = semesterId;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public List<MasterSheetSubjectColumn> getSubjectColumns() {
        return subjectColumns;
    }

    public void setSubjectColumns(List<MasterSheetSubjectColumn> subjectColumns) {
        this.subjectColumns = subjectColumns;
    }

    public List<MasterScoreRow> getRows() {
        return rows;
    }

    public void setRows(List<MasterScoreRow> rows) {
        this.rows = rows;
    }
}
