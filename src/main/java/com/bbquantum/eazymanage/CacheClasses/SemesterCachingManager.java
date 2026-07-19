package com.bbquantum.eazymanage.CacheClasses;

import com.bbquantum.eazymanage.DTO.Caching.CachedSemesters;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class SemesterCachingManager {
    private static SemesterCachingManager instance;

    private List<CachedSemesters> cachedSemesters;

    private LocalDateTime lastUpdatedTime;

    private static final long CACHE_TTL_MINUTES = 120;

    private SemesterCachingManager() {}

    public static SemesterCachingManager getInstance() {
        if (instance == null) {
            instance = new SemesterCachingManager();
        }
        return instance;
    }

    public boolean isSemesterCacheValid() {
        if (cachedSemesters == null || lastUpdatedTime == null) {
            return false;
        }

        long minutesElapsed = ChronoUnit.MINUTES.between(lastUpdatedTime, LocalDateTime.now());
        return minutesElapsed < CACHE_TTL_MINUTES;
    }

    public List<CachedSemesters> getCachedSemesters() {
        if (isSemesterCacheValid()) {
            return cachedSemesters;
        }
        return null;
    }

    public void setCachedSemesters(List<CachedSemesters> cachedSemesters) {
        this.cachedSemesters = cachedSemesters;
        this.lastUpdatedTime = LocalDateTime.now();
    }

    public void clearCachedSemesters() {
        this.cachedSemesters = null;
        this.lastUpdatedTime = LocalDateTime.now();
    }
}
