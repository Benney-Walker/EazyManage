package com.bbquantum.eazymanage.DTO.Academics.Results;

import java.util.List;

public class MasterScoreSheet {

    private String className;

    private String semesterName;

    private List<String> subjects;

    private List<MasterScoreSheetRow> records;

    public MasterScoreSheet() {}

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public List<MasterScoreSheetRow> getRecords() {
        return records;
    }

    public void setRecords(List<MasterScoreSheetRow> records) {
        this.records = records;
    }
}
