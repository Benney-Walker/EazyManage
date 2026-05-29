package com.bbquantum.eazymanage.DTO.Messaging;

public class BroadcastSendRequest {

    private String senderId;

    private Audience audience;

    private String emailSubject;

    private String emailBody;

    private String smsText;

    public BroadcastSendRequest() {}

    public BroadcastSendRequest(String senderId,Audience audience, String emailSubject, String emailBody, String smsText) {
        this.senderId = senderId;
        this.audience = audience;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;
        this.smsText = smsText;

    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Audience getAudience() {
        return audience;
    }

    public void setAudience(Audience audience) {
        this.audience = audience;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public String getSmsText() {
        return smsText;
    }

    public void setSmsText(String smsText) {
        this.smsText = smsText;
    }
}
