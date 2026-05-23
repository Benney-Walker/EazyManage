package com.bbquantum.eazymanage.DTO.WebSocket;

public class BroadcastRequest {

    private String title;
    private String body;
    /** "INFO" / "WARNING" / "URGENT" */
    private String priority;
    /** Comma-separated roles, or null for everyone. */
    private String audience;

    public BroadcastRequest() {}

    public BroadcastRequest(String title, String body, String priority, String audience) {
        this.title = title;
        this.body = body;
        this.priority = priority;
        this.audience = audience;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }
}
