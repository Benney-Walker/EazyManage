package com.bbquantum.eazymanage.DTO.Messaging;

public class BroadcastSendResponse {

    private String broadcastId;

    private int emailRecipients;

    private int smsRecipients;

    private int skipped;

    public BroadcastSendResponse() {}

    public BroadcastSendResponse(String broadcastId, int emailRecipients, int smsRecipients, int skipped) {
        this.broadcastId = broadcastId;
        this.emailRecipients = emailRecipients;
        this.smsRecipients = smsRecipients;
        this.skipped = skipped;
    }

    public String getBroadcastId() {
        return broadcastId;
    }

    public void setBroadcastId(String broadcastId) {
        this.broadcastId = broadcastId;
    }

    public int getEmailRecipients() {
        return emailRecipients;
    }

    public void setEmailRecipients(int emailRecipients) {
        this.emailRecipients = emailRecipients;
    }

    public int getSmsRecipients() {
        return smsRecipients;
    }

    public void setSmsRecipients(int smsRecipients) {
        this.smsRecipients = smsRecipients;
    }

    public int getSkipped() {
        return skipped;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }
}
