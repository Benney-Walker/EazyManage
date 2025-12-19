package com.bbquantum.eazymanage.CacheClasses;

import com.bbquantum.eazymanage.DTO.RecentStaffView;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class StaffListCacheManager {
    private static StaffListCacheManager instance;

    private List<RecentStaffView> cachedStaffList;

    private LocalDateTime lastUpdatedTime;

    private static final long CACHE_TTL_MINUTES = 60;

    private StaffListCacheManager() {}

    public static StaffListCacheManager getInstance() {
        if (instance == null) {
            instance = new StaffListCacheManager();
        }
        return instance;
    }

    public boolean isCacheValid() {
        if (cachedStaffList == null || lastUpdatedTime == null) {
            return false;
        }

        long minutesElapsed = ChronoUnit.MINUTES.between(lastUpdatedTime, LocalDateTime.now());
        return minutesElapsed < CACHE_TTL_MINUTES;
    }

    public List<RecentStaffView> getCachedList() {
        if (isCacheValid()) {
            return cachedStaffList;
        }
        return null;
    }

    public void setStaffList(List<RecentStaffView> staffCachingList) {
        cachedStaffList = staffCachingList;
        this.lastUpdatedTime = LocalDateTime.now();
    }
}
