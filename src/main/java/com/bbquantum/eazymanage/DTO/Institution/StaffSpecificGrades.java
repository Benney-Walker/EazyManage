package com.bbquantum.eazymanage.DTO.Institution;

public class StaffSpecificGrades {
    private String levelId;

    private String levelName;

    public StaffSpecificGrades(String levelId, String levelName) {
        this.levelId = levelId;
        this.levelName = levelName;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }
}
