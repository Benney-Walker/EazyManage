package com.bbquantum.eazymanage.DTO;

public class SubscriptionResponse {
    boolean success;

    String institutionId;

    String message;

    public  SubscriptionResponse(boolean success,String institutionId,String message) {
        this.success=success;
        this.institutionId=institutionId;
        this.message=message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
