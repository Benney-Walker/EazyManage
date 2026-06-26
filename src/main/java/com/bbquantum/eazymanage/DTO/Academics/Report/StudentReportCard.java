package com.bbquantum.eazymanage.DTO.Academics.Report;

import com.bbquantum.eazymanage.DTO.Academics.Conduct.ConductReport;
import com.bbquantum.eazymanage.DTO.Academics.Conduct.StudentConductRecord;
import com.bbquantum.eazymanage.DTO.Academics.Results.StudentResult;

import java.util.List;

public class StudentReportCard {

    private String schoolName;

    private String studentId;

    private String studentName;

    private String className;

    private String semesterName;

    private String totalScore;

    private String averageScore;

    private String classPosition;

    private String academicYear;

    private String totalStudents;

    private String vacationDate;

    private String resumptionDate;

    private String attendancePresent;

    private String totalAttendance;

    private String instructorName;

    private String currentDate;

    private String promotedTo;

    private List<SubjectScoresReport> subjectScores;

    private ConductReport studentConductReport;

    public StudentReportCard() {}

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

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

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public String getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(String averageScore) {
        this.averageScore = averageScore;
    }

    public String getClassPosition() {
        return classPosition;
    }

    public void setClassPosition(String classPosition) {
        this.classPosition = classPosition;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(String totalStudents) {
        this.totalStudents = totalStudents;
    }

    public String getVacationDate() {
        return vacationDate;
    }

    public void setVacationDate(String vacationDate) {
        this.vacationDate = vacationDate;
    }

    public String getResumptionDate() {
        return resumptionDate;
    }

    public void setResumptionDate(String resumptionDate) {
        this.resumptionDate = resumptionDate;
    }

    public String getAttendancePresent() {
        return attendancePresent;
    }

    public void setAttendancePresent(String attendancePresent) {
        this.attendancePresent = attendancePresent;
    }

    public String getTotalAttendance() {
        return totalAttendance;
    }

    public void setTotalAttendance(String totalAttendance) {
        this.totalAttendance = totalAttendance;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getPromotedTo() {
        return promotedTo;
    }

    public void setPromotedTo(String promotedTo) {
        this.promotedTo = promotedTo;
    }

    public List<SubjectScoresReport> getSubjectScores() {
        return subjectScores;
    }

    public void setSubjectScores(List<SubjectScoresReport> subjectScores) {
        this.subjectScores = subjectScores;
    }

    public ConductReport getStudentConductReport() {
        return studentConductReport;
    }

    public void setStudentConductReport(ConductReport studentConductReport) {
        this.studentConductReport = studentConductReport;
    }
}
