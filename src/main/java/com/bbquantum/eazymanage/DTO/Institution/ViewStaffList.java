package com.bbquantum.eazymanage.DTO.Institution;

import java.util.List;

public class ViewStaffList {
    private String staffId;

    private String staffName;

    private String staffRole;

    private List<String> assignedLevel;

    public ViewStaffList(String staffId, String staffName,
                         String staffRole, List<String> assignedLevel) {
        this.staffId = staffId;
        this.staffName = staffName;
        this.staffRole = staffRole;
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

    public String getStaffRole() {
        return staffRole;
    }

    public void setStaffRole(String staffRole) {
        this.staffRole = staffRole;
    }

    public List<String> getAssignedLevel() {
        return assignedLevel;
    }

    public void setAssignedLevel(List<String> assignedLevel) {
        this.assignedLevel = assignedLevel;
    }
}
