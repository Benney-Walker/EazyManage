package com.bbquantum.eazymanage.DTO.Institution;

public class SemesterCaching {
    private String semesterId;

    private String semesterName;

    private String academicYear;

    public  SemesterCaching(String semesterId, String semesterName, String academicYear) {
        this.semesterId = semesterId;
        this.semesterName = semesterName;
        this.academicYear = academicYear;
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

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    @Override
    public String toString() {
        return semesterName + " (" + academicYear + ")";
    }
}
