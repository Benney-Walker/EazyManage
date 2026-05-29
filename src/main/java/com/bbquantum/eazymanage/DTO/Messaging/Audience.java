package com.bbquantum.eazymanage.DTO.Messaging;

import java.util.List;

public class Audience {

    private List<String> staffRoles;

    private List<String> classIds;

    public Audience() {}

    public Audience(List<String> staffRoles, List<String> classIds) {
        this.staffRoles = staffRoles;
        this.classIds = classIds;
    }

    public List<String> getStaffRoles() {
        return staffRoles;
    }

    public void setStaffRoles(List<String> staffRoles) {
        this.staffRoles = staffRoles;
    }

    public List<String> getClassIds() {
        return classIds;
    }

    public void setClassIds(List<String> classIds) {
        this.classIds = classIds;
    }
}
