package com.bbquantum.eazymanage.DTO.Finance;

public class ClassFeesSummary {
    private String className;

    private String totalStudents;

    private String expectedFees;

    private String amountPaid;

    private String outstandingFees;

    public ClassFeesSummary(String className, String totalStudents, String expectedFees, String amountPaid, String outstandingFees) {
        this.className = className;
        this.totalStudents = totalStudents;
        this.expectedFees = expectedFees;
        this.amountPaid = amountPaid;
        this.outstandingFees = outstandingFees;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(String totalStudents) {
        this.totalStudents = totalStudents;
    }

    public String getExpectedFees() {
        return expectedFees;
    }

    public void setExpectedFees(String expectedFees) {
        this.expectedFees = expectedFees;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getOutstandingFees() {
        return outstandingFees;
    }

    public void setOutstandingFees(String outstandingFees) {
        this.outstandingFees = outstandingFees;
    }
}
