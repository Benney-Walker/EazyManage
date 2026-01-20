package com.bbquantum.eazymanage.CacheClasses;

import com.bbquantum.eazymanage.DTO.Academics.GradeCaching;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class GradeCacheManager {
    //Singleton instance
    private static GradeCacheManager instance;

    //Cached grade data
    private List<GradeCaching> cachedGrade;

    //Timestamp of the last update
    private LocalDateTime lastUpdateTime;

    //Time-to-Live in minutes
    private static final long CACHE_TTL_MINUTES = 5;

    private GradeCacheManager() {}

    public static GradeCacheManager getInstance() {
        if (instance == null) {
            instance = new GradeCacheManager();
        }
        return instance;
    }

    //Check if the cache is stale
    public boolean isCacheValid() {
        if (cachedGrade == null || lastUpdateTime == null) {
            return false;
        }

        long minutesElapsed = ChronoUnit.MINUTES.between(lastUpdateTime, LocalDateTime.now());
        return minutesElapsed < CACHE_TTL_MINUTES;
    }

    //Retrieves the cached list
    public List<GradeCaching> getGradeCache() {
        if (isCacheValid()) {
            return cachedGrade;
        }
        return null;
    }

    public void setGradeCache(List<GradeCaching> grades) {
        this.cachedGrade = grades;
        this.lastUpdateTime = LocalDateTime.now();
    }

    public void clearCache() {
        this.cachedGrade = null;
        this.lastUpdateTime = LocalDateTime.now();
    }
}
