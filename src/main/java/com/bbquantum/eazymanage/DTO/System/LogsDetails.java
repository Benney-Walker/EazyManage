package com.bbquantum.eazymanage.DTO.System;

public class LogsDetails {

    private String id;

    private String date;

    private String time;

    private String type;

    private String message;

    private String createdBy;

    private String status;

    public LogsDetails(String id, String date, String time, String type, String message, String createdBy, String status) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.type = type;
        this.message = message;
        this.createdBy = createdBy;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
