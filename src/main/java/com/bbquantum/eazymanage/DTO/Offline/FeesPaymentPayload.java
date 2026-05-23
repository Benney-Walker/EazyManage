package com.bbquantum.eazymanage.DTO.Offline;

public class FeesPaymentPayload {

    public String staffId;

    public String studentId;

    public String semesterId;

    public String amountPaid;

    public String payerName;

    public String payerPhone;

    public FeesPaymentPayload() {}

    public FeesPaymentPayload(String staffId, String studentId, String semesterId, String amountPaid, String payerName, String payerPhone) {
        this.staffId = staffId;
        this.studentId = studentId;
        this.semesterId = semesterId;
        this.amountPaid = amountPaid;
        this.payerName = payerName;
        this.payerPhone = payerPhone;
    }
}
