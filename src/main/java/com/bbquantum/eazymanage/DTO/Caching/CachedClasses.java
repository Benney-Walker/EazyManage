package com.bbquantum.eazymanage.DTO.Caching;

public class CachedClasses {
    private String levelId;

    private String levelName;

    public CachedClasses(String levelId, String levelName) {
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
