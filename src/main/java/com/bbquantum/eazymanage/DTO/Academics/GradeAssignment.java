package com.bbquantum.eazymanage.DTO.Academics;

public class GradeAssignment {
    String gradeName;

    String staffId;

    public GradeAssignment(String gradeName, String staffId) {
        this.gradeName = gradeName;
        this.staffId = staffId;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
}
