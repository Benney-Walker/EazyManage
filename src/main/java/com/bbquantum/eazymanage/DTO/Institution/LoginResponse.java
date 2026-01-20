package com.bbquantum.eazymanage.DTO.Institution;

public class LoginResponse {
    String success;

    String staffId;

    String staffRole;

    String institutionName;

    String staffFullName;

    String gradeId;

    public LoginResponse(String success, String staffId, String staffRole,
                         String institutionName, String staffFullName) {
        this.success = success;
        this.staffId = staffId;
        this.staffRole = staffRole;
        this.institutionName = institutionName;
        this.staffFullName = staffFullName;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStaffRole() {
        return staffRole;
    }

    public void setStaffRole(String staffRole) {
        this.staffRole = staffRole;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getStaffFullName() {
        return staffFullName;
    }

    public void setStaffFullName(String staffFullName) {
        this.staffFullName = staffFullName;
    }

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }
}
