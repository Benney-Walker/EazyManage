package com.bbquantum.eazymanage.DTO.Academics;

public class StudentConductRecord {

    private String studentId;
    private String studentName;
    private String semesterId;

    // Human-readable column headers
    public static final String[] RATING_LABELS = {
            "EXCELLENT", "VERY GOOD", "GOOD", "FAIR", "NOT TO EXPECT."
    };

    // Six conduct categories, each holding a rating enum string (nullable).
    private String regular;
    private String punctual;
    private String physicalAppearance;
    private String social;
    private String emotional;
    private String cognitiveSkills;

    // Free text
    private String conductRemark;

    public StudentConductRecord() {}

    public StudentConductRecord(String studentId, String studentName, String semesterId,
                                String regular, String punctual, String physicalAppearance,
                                String social, String emotional, String cognitiveSkills,
                                String conductRemark) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.semesterId = semesterId;
        this.regular = regular;
        this.punctual = punctual;
        this.physicalAppearance = physicalAppearance;
        this.social = social;
        this.emotional = emotional;
        this.cognitiveSkills = cognitiveSkills;
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

    public String getRegular() {
        return regular;
    }

    public void setRegular(String regular) {
        this.regular = regular;
    }

    public String getPunctual() {
        return punctual;
    }

    public void setPunctual(String punctual) {
        this.punctual = punctual;
    }

    public String getPhysicalAppearance() {
        return physicalAppearance;
    }

    public void setPhysicalAppearance(String physicalAppearance) {
        this.physicalAppearance = physicalAppearance;
    }

    public String getSocial() {
        return social;
    }

    public void setSocial(String social) {
        this.social = social;
    }

    public String getEmotional() {
        return emotional;
    }

    public void setEmotional(String emotional) {
        this.emotional = emotional;
    }

    public String getCognitiveSkills() {
        return cognitiveSkills;
    }

    public void setCognitiveSkills(String cognitiveSkills) {
        this.cognitiveSkills = cognitiveSkills;
    }

    public String getConductRemark() {
        return conductRemark;
    }

    public void setConductRemark(String conductRemark) {
        this.conductRemark = conductRemark;
    }
}
