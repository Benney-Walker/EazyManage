package com.bbquantum.eazymanage.DTO.Institution;

public class EnrollmentResponse {
    private boolean success;

    private String staffId;

    private String message;

    private String password;

    public EnrollmentResponse(boolean success, String staffId, String message, String password) {
        this.success = success;
        this.staffId = staffId;
        this.message = message;
        this.password = password;
    }

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
