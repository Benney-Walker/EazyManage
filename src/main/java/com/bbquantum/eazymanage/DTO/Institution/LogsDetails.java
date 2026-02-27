package com.bbquantum.eazymanage.DTO.Institution;

public class LogsDetails {

    private String actionId;

    private String actionDate;

    private String actionTime;

    private String actionType;

    private String actionData;

    private String createdBy;

    private String status;

    public LogsDetails(String actionId, String actionDate, String actionTime, String actionType, String actionData, String createdBy, String status) {
        this.actionId = actionId;
        this.actionDate = actionDate;
        this.actionTime = actionTime;
        this.actionType = actionType;
        this.actionData = actionData;
        this.createdBy = createdBy;
        this.status = status;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getActionDate() {
        return actionDate;
    }

    public void setActionDate(String actionDate) {
        this.actionDate = actionDate;
    }

    public String getActionTime() {
        return actionTime;
    }

    public void setActionTime(String actionTime) {
        this.actionTime = actionTime;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionData() {
        return actionData;
    }

    public void setActionData(String actionData) {
        this.actionData = actionData;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
