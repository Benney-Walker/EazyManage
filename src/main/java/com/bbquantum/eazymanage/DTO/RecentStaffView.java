package com.bbquantum.eazymanage.DTO;

public class RecentStaffView {
    private String staffId;

    private String staffName;

    private String assignedLevel;

    public RecentStaffView(String staffId, String staffName, String assignedLevel) {
        this.staffId = staffId;
        this.staffName = staffName;
        this.assignedLevel = assignedLevel;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getAssignedLevel() {
        return assignedLevel;
    }

    public void setAssignedLevel(String assignedLevel) {
        this.assignedLevel = assignedLevel;
    }
}
