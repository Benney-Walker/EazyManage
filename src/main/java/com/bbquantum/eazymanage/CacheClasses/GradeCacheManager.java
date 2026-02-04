package com.bbquantum.eazymanage.CacheClasses;

import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.DTO.Institution.StaffSpecificGrades;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class GradeCacheManager {
    //Singleton instance
    private static GradeCacheManager instance;

    //Cached grade data
    private List<GradeCaching> cachedGrades;

    //Timestamp of the last update
    private LocalDateTime cachedGradesDuration;

    //Time-to-Live in minutes
    private static final long GRADES_CACHE_TTL_MINUTES = 5;

    private GradeCacheManager() {}

    public static GradeCacheManager getInstance() {
        if (instance == null) {
            instance = new GradeCacheManager();
        }
        return instance;
    }

    //Check if the cached grades has expired
    public boolean isCachedGradesValid() {
        if (cachedGrades == null || cachedGradesDuration == null) {
            return false;
        }

        long minutesElapsed = ChronoUnit.MINUTES.between(cachedGradesDuration, LocalDateTime.now());
        return minutesElapsed < GRADES_CACHE_TTL_MINUTES;
    }

    //Retrieves the cached grades list
    public List<GradeCaching> getGradesCache() {
        if (isCachedGradesValid()) {
            return cachedGrades;
        }
        return null;
    }

    public void setCachedGrades(List<GradeCaching> grades) {
        this.cachedGrades = grades;
        this.cachedGradesDuration = LocalDateTime.now();
    }

    public void clearCache() {
        this.cachedGrades = null;
        this.cachedGradesDuration = null;
    }
}
