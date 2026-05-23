package com.bbquantum.eazymanage.DTO.WebSocket;

public class Broadcast {

    /** Server-assigned identifier; opaque to the client. */
    private String id;

    /** Headline shown in the notification toast/bell list. */
    private String title;

    /** Longer message body shown in the popup. */
    private String body;

    /** ISO 8601 timestamp string — set by the server when the message
     *  was published. */
    private String timestamp;

    /** One of "INFO", "WARNING", "URGENT" — uppercase to match
     *  {@code Notification.Priority} enum names. */
    private String priority;

    /** Staff id of the sender (Principal or Administrator). */
    private String senderId;

    /** Display name of the sender — backend resolves this so the client
     *  doesn't need to look it up. */
    private String senderName;

    /** Optional comma-separated role filter applied by the server before
     *  delivery; useful for the UI to show "to: Teachers" labels. May be
     *  null when the broadcast went to everyone. */
    private String audience;

    public Broadcast() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }
}
