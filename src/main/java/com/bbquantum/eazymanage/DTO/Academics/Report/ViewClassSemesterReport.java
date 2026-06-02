package com.bbquantum.eazymanage.DTO.Academics.Report;

import com.bbquantum.eazymanage.DTO.Academics.Results.ViewStudentsSubjectsResults;

import java.util.List;

public class ViewClassSemesterReport {
    private String studentId;

    private String studentName;

    private String className;

    private String semesterName;

    private String totalScore;

    private String averageScore;

    private String classPosition;

    private String totalStudents;

    /** Free-text overall-semester teacher remark for this student. */
    private String semesterRemark;

    private List<ViewStudentsSubjectsResults> scoresList;

    public ViewClassSemesterReport(String studentId, String studentName, String className, String semesterName,
                                   String classPosition, String totalScore, String averageScore, List<ViewStudentsSubjectsResults> scoresList,
                                   String totalStudents) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.className = className;
        this.semesterName = semesterName;
        this.scoresList = scoresList;
        this.classPosition = classPosition;
        this.totalScore = totalScore;
        this.averageScore = averageScore;
        this.classPosition = classPosition;
        this.totalStudents = totalStudents;
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

    public String getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(String totalStudents) {
        this.totalStudents = totalStudents;
    }

    public String getSemesterRemark() {
        return semesterRemark;
    }

    public void setSemesterRemark(String semesterRemark) {
        this.semesterRemark = semesterRemark;
    }

    public List<ViewStudentsSubjectsResults> getScoresList() {
        return scoresList;
    }

    public void setScoresList(List<ViewStudentsSubjectsResults> scoresList) {
        this.scoresList = scoresList;
    }
}
