package com.bbquantum.eazymanage.DTO.Caching;

import java.util.List;

public class ViewStaffList {
    private String staffId;

    private String staffName;

    private List<String> staffRoles;

    public ViewStaffList(String staffId, String staffName,
                         List<String> staffRoles) {
        this.staffId = staffId;
        this.staffName = staffName;
        this.staffRoles = staffRoles;
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

    public List<String> getStaffRoles() {
        return staffRoles;
    }

    public void setStaffRoles(List<String> staffRoles) {
        this.staffRoles = staffRoles;
    }
}
