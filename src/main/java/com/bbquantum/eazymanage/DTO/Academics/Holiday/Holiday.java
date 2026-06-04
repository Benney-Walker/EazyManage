package com.bbquantum.eazymanage.DTO.Academics.Holiday;

public class Holiday {

    /** Server-issued primary key. Null on POST; populated on GET responses. */
    private int holidayId;

    /** One of the four HolidayType enum strings — see class javadoc. */
    private String holidayName;

    /** Inclusive first day of the holiday window. ISO yyyy-MM-dd on the wire. */
    private String startDate;

    /** Inclusive last day. May equal startDate for single-day holidays. */
    private String endDate;

    /** The semester this holiday falls in. Sent as id on the wire. */
    private String semesterId;

    /** Display label populated on GET responses; ignored on POST/PUT. */
    private String semesterName;

    private String academicYear;

    public Holiday() {}

    public Holiday(String holidayName, String startDate, String endDate,
                   String semesterId) {
        this.holidayName = holidayName;
        this.startDate   = startDate;
        this.endDate     = endDate;
        this.semesterId  = semesterId;
    }

    public int getHolidayId() {
        return holidayId;
    }

    public void setHolidayId(int holidayId) {
        this.holidayId = holidayId;
    }

    public String getHolidayName() {
        return holidayName;
    }

    public void setHolidayName(String holidayName) {
        this.holidayName = holidayName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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
}
