package com.bbquantum.eazymanage.CacheClasses;

import com.bbquantum.eazymanage.DTO.Caching.StaffCaching;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class StaffCacheManager {
    private static StaffCacheManager instance;

    private List<StaffCaching> cachedList;

    private LocalDateTime lastUpdatedTime;

    private static final long CACHE_TTL_MINUTES = 120;

    private StaffCacheManager() {}

    public static StaffCacheManager getInstance() {
        if (instance == null) {
            instance = new StaffCacheManager();
        }
        return instance;
    }

    public boolean isCacheValid() {
        if (cachedList == null || lastUpdatedTime == null) {
            return false;
        }

        long minutesElapsed = ChronoUnit.MINUTES.between(lastUpdatedTime, LocalDateTime.now());
        return minutesElapsed < CACHE_TTL_MINUTES;
    }

    public List<StaffCaching> getCachedStaffList() {
        if (isCacheValid()) {
            return cachedList;
        }
        return null;
    }

    public void setStaffList(List<StaffCaching> staffCachingList) {
        cachedList = staffCachingList;
        this.lastUpdatedTime = LocalDateTime.now();
    }

    public void clearCachedStaffList() {
        this.cachedList = null;
        this.lastUpdatedTime = LocalDateTime.now();
    }

}
