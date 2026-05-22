package com.bbquantum.eazymanage.DTO.Academics;

public class StudentReportCard {

    private StudentResult academics;

    private StudentConductRecord conduct;

    private String institutionName;

    private String institutionAddress;

    private String headmasterName;

    private String issueDate;

    public StudentReportCard() {}

    public StudentResult getAcademics() {
        return academics;
    }

    public void setAcademics(StudentResult academics) {
        this.academics = academics;
    }

    public StudentConductRecord getConduct() {
        return conduct;
    }

    public void setConduct(StudentConductRecord conduct) {
        this.conduct = conduct;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getInstitutionAddress() {
        return institutionAddress;
    }

    public void setInstitutionAddress(String institutionAddress) {
        this.institutionAddress = institutionAddress;
    }

    public String getHeadmasterName() {
        return headmasterName;
    }

    public void setHeadmasterName(String headmasterName) {
        this.headmasterName = headmasterName;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }
}
