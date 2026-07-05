package com.bbquantum.eazymanage.DTO.Offline;

public class OfflinePaymentReplay {

    private String studentId;

    private String levelId;

    private String semesterId;

    private String amountPaid;

    private String payerName;

    private String payerPhone;

    public OfflinePaymentReplay(String studentId, String levelId, String semesterId, String amountPaid, String payerName, String payerPhone, String staffId) {
        this.studentId = studentId;
        this.levelId = levelId;
        this.semesterId = semesterId;
        this.amountPaid = amountPaid;
        this.payerName = payerName;
        this.payerPhone = payerPhone;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getLevelId() {
        return levelId;
    }

    public String getSemesterId() {
        return semesterId;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public String getPayerName() {
        return payerName;
    }

    public String getPayerPhone() {
        return payerPhone;
    }
}
