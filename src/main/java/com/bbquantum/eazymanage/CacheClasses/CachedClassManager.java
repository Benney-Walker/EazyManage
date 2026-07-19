package com.bbquantum.eazymanage.CacheClasses;

import com.bbquantum.eazymanage.DTO.Caching.CachedClasses;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CachedClassManager {
    //Singleton instance
    private static CachedClassManager instance;

    //Cached grade data
    private List<CachedClasses> cachedGrades;

    //Timestamp of the last update
    private LocalDateTime cachedGradesDuration;

    //Time-to-Live in minutes
    private static final long GRADES_CACHE_TTL_MINUTES = 120;

    private CachedClassManager() {}

    public static CachedClassManager getInstance() {
        if (instance == null) {
            instance = new CachedClassManager();
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
    public List<CachedClasses> getGradesCache() {
        if (isCachedGradesValid()) {
            return cachedGrades;
        }
        return null;
    }

    public void setCachedGrades(List<CachedClasses> grades) {
        this.cachedGrades = grades;
        this.cachedGradesDuration = LocalDateTime.now();
    }

    public void clearCache() {
        this.cachedGrades = null;
        this.cachedGradesDuration = null;
    }
}
