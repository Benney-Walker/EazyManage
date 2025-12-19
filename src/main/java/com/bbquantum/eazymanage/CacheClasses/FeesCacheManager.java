package com.bbquantum.eazymanage.CacheClasses;

import com.bbquantum.eazymanage.DTO.Finance.ClassFeesSummary;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class FeesCacheManager {
    private static FeesCacheManager instance;

    private List<ClassFeesSummary> cachedClassFeesSummaryList;

    private LocalDateTime lastUpdateTime;

    private static final long CACHE_TTL_MINUTES = 5;

    private FeesCacheManager() {}

    public static FeesCacheManager getInstance() {
        if (instance == null) {
            instance = new FeesCacheManager();
        }
        return instance;
    }

    //Check if the cache is stale
    public boolean isCacheValid() {
        if (cachedClassFeesSummaryList == null || lastUpdateTime == null) {
            return false;
        }

        long minutesElapsed = ChronoUnit.MINUTES.between(lastUpdateTime, LocalDateTime.now());
        return minutesElapsed < CACHE_TTL_MINUTES;
    }

    //Retrieves the cached list
    public List<ClassFeesSummary> getClassFeesSummaryCache() {
        if (isCacheValid()) {
            return cachedClassFeesSummaryList;
        }
        return null;
    }

    public void setGradeCache(List<ClassFeesSummary> cachedClassFeesSummaryList) {
        this.cachedClassFeesSummaryList = cachedClassFeesSummaryList;
        this.lastUpdateTime = LocalDateTime.now();
    }

}
