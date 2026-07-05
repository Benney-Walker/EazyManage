package com.bbquantum.eazymanage.CacheClasses;

import com.bbquantum.eazymanage.DTO.Attendance.AbsenteesView;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class RecentStudentCacheManager {
    //Singleton instance
    private static RecentStudentCacheManager instance;

    //Cached data
    private List<AbsenteesView> cachedAbsenteeStudents;

    //Timestamp of the last update
    private LocalDateTime lastUpdatedTime;

    private static final long CACHE_TTL_MINUTES = 60;

    private RecentStudentCacheManager() {}

    public static RecentStudentCacheManager getInstance() {
        if (instance == null) {
            instance = new RecentStudentCacheManager();
        }
        return instance;
    }

    public boolean isCacheValid() {
        if (cachedAbsenteeStudents == null || lastUpdatedTime == null) {
            return false;
        }

        long minutesElapsed = ChronoUnit.MINUTES.between(lastUpdatedTime, LocalDateTime.now());
        return minutesElapsed < CACHE_TTL_MINUTES;
    }

    //Cache List Retrieval
    public List<AbsenteesView> getCachedAbsenteeStudents() {
        if (isCacheValid()) {
            return cachedAbsenteeStudents;
        }

        return null;
    }

    public void setCachedAbsenteeStudents(List<AbsenteesView> cachedAbsenteeStudents) {
        this.cachedAbsenteeStudents = cachedAbsenteeStudents;
        this.lastUpdatedTime = LocalDateTime.now();
    }

    public void clearCachedStudents() {
        this.cachedAbsenteeStudents = null;
        this.lastUpdatedTime = LocalDateTime.now();
    }
}
