package com.bbquantum.eazymanage.DTO.Auth;

public class SubscriptionResponse {

    private boolean isSuccess;

    private String message;

    public SubscriptionResponse(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getMessage() {
        return message;
    }
}
