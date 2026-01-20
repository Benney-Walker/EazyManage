package com.bbquantum.eazymanage.CacheClasses;

import com.bbquantum.eazymanage.DTO.Institution.RecentStudentView;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class RecentStudentCacheManager {
    //Singleton instance
    private static RecentStudentCacheManager instance;

    //Cached data
    private List<RecentStudentView> cachedStudents;

    //Timestamp of the last update
    private LocalDateTime lastUpdatedTime;

    private static final long CACHE_TTL_MINUTES = 5;

    private RecentStudentCacheManager() {}

    public static RecentStudentCacheManager getInstance() {
        if (instance == null) {
            instance = new RecentStudentCacheManager();
        }
        return instance;
    }

    public boolean isCacheValid() {
        if (cachedStudents == null || lastUpdatedTime == null) {
            return false;
        }

        long minutesElapsed = ChronoUnit.MINUTES.between(lastUpdatedTime, LocalDateTime.now());
        return minutesElapsed < CACHE_TTL_MINUTES;
    }

    //Cache List Retrieval
    public List<RecentStudentView> getCachedStudents() {
        if (isCacheValid()) {
            return cachedStudents;
        }

        return null;
    }

    public void setCachedStudents(List<RecentStudentView> cachedStudents) {
        this.cachedStudents = cachedStudents;
        this.lastUpdatedTime = LocalDateTime.now();
    }

    public void clearCachedStudents() {
        this.cachedStudents = null;
        this.lastUpdatedTime = LocalDateTime.now();
    }
}
