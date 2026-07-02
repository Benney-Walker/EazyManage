package com.bbquantum.eazymanage.DTO.Messaging;

import java.util.List;

public class BroadcastEmailRequest {

    private String mailSubject;

    private String mailBody;

    private List<String> audience;

    public BroadcastEmailRequest() {}

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getMailBody() {
        return mailBody;
    }

    public void setMailBody(String mailBody) {
        this.mailBody = mailBody;
    }

    public List<String> getAudience() {
        return audience;
    }

    public void setAudience(List<String> audience) {
        this.audience = audience;
    }
}
