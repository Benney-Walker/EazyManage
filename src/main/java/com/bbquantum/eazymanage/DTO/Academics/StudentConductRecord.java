package com.bbquantum.eazymanage.DTO.Academics;

public class StudentConductRecord {

    private String studentId;
    private String studentName;
    private String semesterId;

    // the form stays consistent across screens.
    private Boolean punctuality;
    private Boolean attendance;
    private Boolean neatness;
    private Boolean respectForAuthority;
    private Boolean cooperationWithPeers;
    private Boolean classParticipation;
    private Boolean completionOfAssignments;
    private Boolean honesty;

    //Free text
    private String conductRemark;

    public StudentConductRecord() {}

    public StudentConductRecord(String studentId, String studentName, String semesterId,
                                Boolean punctuality, Boolean attendance, Boolean neatness,
                                Boolean respectForAuthority, Boolean cooperationWithPeers,
                                Boolean classParticipation, Boolean completionOfAssignments,
                                Boolean honesty, String conductRemark) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.semesterId = semesterId;
        this.punctuality = punctuality;
        this.attendance = attendance;
        this.neatness = neatness;
        this.respectForAuthority = respectForAuthority;
        this.cooperationWithPeers = cooperationWithPeers;
        this.classParticipation = classParticipation;
        this.completionOfAssignments = completionOfAssignments;
        this.honesty = honesty;
        this.conductRemark = conductRemark;
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

    public String getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(String semesterId) {
        this.semesterId = semesterId;
    }

    public Boolean getPunctuality() {
        return punctuality;
    }

    public void setPunctuality(Boolean punctuality) {
        this.punctuality = punctuality;
    }

    public Boolean getAttendance() {
        return attendance;
    }

    public void setAttendance(Boolean attendance) {
        this.attendance = attendance;
    }

    public Boolean getNeatness() {
        return neatness;
    }

    public void setNeatness(Boolean neatness) {
        this.neatness = neatness;
    }

    public Boolean getRespectForAuthority() {
        return respectForAuthority;
    }

    public void setRespectForAuthority(Boolean respectForAuthority) {
        this.respectForAuthority = respectForAuthority;
    }

    public Boolean getCooperationWithPeers() {
        return cooperationWithPeers;
    }

    public void setCooperationWithPeers(Boolean cooperationWithPeers) {
        this.cooperationWithPeers = cooperationWithPeers;
    }

    public Boolean getClassParticipation() {
        return classParticipation;
    }

    public void setClassParticipation(Boolean classParticipation) {
        this.classParticipation = classParticipation;
    }

    public Boolean getCompletionOfAssignments() {
        return completionOfAssignments;
    }

    public void setCompletionOfAssignments(Boolean completionOfAssignments) {
        this.completionOfAssignments = completionOfAssignments;
    }

    public Boolean getHonesty() {
        return honesty;
    }

    public void setHonesty(Boolean honesty) {
        this.honesty = honesty;
    }

    public String getConductRemark() {
        return conductRemark;
    }

    public void setConductRemark(String conductRemark) {
        this.conductRemark = conductRemark;
    }
}
