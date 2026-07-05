package com.bbquantum.eazymanage.CacheClasses;

import com.bbquantum.eazymanage.DTO.Finance.ClassFeesSummary;
import com.bbquantum.eazymanage.DTO.Finance.RecentPaymentTable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class FeesCacheManager {
    private static FeesCacheManager instance;

    private List<ClassFeesSummary> cachedClassFeesSummaryList;

    private List<RecentPaymentTable> cachedRecentPaymentList;

    private LocalDateTime lastUpdateTime;

    private static final long CACHE_TTL_MINUTES = 60;

    private FeesCacheManager() {}

    public static FeesCacheManager getInstance() {
        if (instance == null) {
            instance = new FeesCacheManager();
        }
        return instance;
    }

    //Check if the cache is stale
    public boolean isCachedClassFeesValid() {
        if (cachedClassFeesSummaryList == null || lastUpdateTime == null) {
            return false;
        }

        long minutesElapsed = ChronoUnit.MINUTES.between(lastUpdateTime, LocalDateTime.now());
        return minutesElapsed < CACHE_TTL_MINUTES;
    }

    public boolean isCachedRecentPaymentValid() {
        if (cachedRecentPaymentList == null || lastUpdateTime == null) {
            return false;
        }

        long minutesElapsed = ChronoUnit.MINUTES.between(lastUpdateTime, LocalDateTime.now());
        return minutesElapsed < CACHE_TTL_MINUTES;
    }

    //Retrieves the cached list
    public List<ClassFeesSummary> getClassFeesSummaryCache() {
        if (isCachedClassFeesValid()) {
            return cachedClassFeesSummaryList;
        }
        return null;
    }

    public List<RecentPaymentTable> getRecentPaymentCache() {
        if (isCachedRecentPaymentValid()) {
            return cachedRecentPaymentList;
        }
        return null;
    }

    public void setClassFeesSummaryCache(List<ClassFeesSummary> cachedClassFeesSummaryList) {
        this.cachedClassFeesSummaryList = cachedClassFeesSummaryList;
        this.lastUpdateTime = LocalDateTime.now();
    }

    public void setRecentPaymentCache(List<RecentPaymentTable> cachedRecentPaymentList) {
        this.cachedRecentPaymentList = cachedRecentPaymentList;
        this.lastUpdateTime = LocalDateTime.now();
    }

    public void clearRecentPaymentCache() {
        this.cachedRecentPaymentList = null;
        this.lastUpdateTime = null;
    }

    public void clearClassFeesSummaryCache() {
        this.cachedClassFeesSummaryList = null;
        this.lastUpdateTime = null;
    }

}
