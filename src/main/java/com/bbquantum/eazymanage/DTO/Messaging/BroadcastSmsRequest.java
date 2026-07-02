package com.bbquantum.eazymanage.DTO.Messaging;

import java.util.List;

public class BroadcastSmsRequest {

    private String smsText;

    private List<String> audience;

    public BroadcastSmsRequest() {}

    public String getSmsText() {
        return smsText;
    }

    public void setSmsText(String smsText) {
        this.smsText = smsText;
    }

    public List<String> getAudience() {
        return audience;
    }

    public void setAudience(List<String> audience) {
        this.audience = audience;
    }
}
