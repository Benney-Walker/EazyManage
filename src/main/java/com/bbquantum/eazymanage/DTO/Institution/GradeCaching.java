package com.bbquantum.eazymanage.DTO.Institution;

public class GradeCaching {
    private String levelId;

    private String levelName;

    public GradeCaching(String levelId, String levelName) {
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

    @Override
    public String toString() {

        return levelName;
    }
}
