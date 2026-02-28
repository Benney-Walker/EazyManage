package com.bbquantum.eazymanage.CacheClasses;

import com.bbquantum.eazymanage.DTO.Institution.LogsDetails;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class SystemLogsCacheManager {
    private static SystemLogsCacheManager instance;

    private List<LogsDetails> cachedSystemLogs;

    private LocalDateTime lastUpdatedTime;

    private static final long CACHE_TTL_MINUTES = 5;

    public SystemLogsCacheManager() {}

    public static SystemLogsCacheManager getInstance() {
        if (instance == null) {
            instance = new SystemLogsCacheManager();
        }

        return instance;
    }

    public boolean isCachedLogsValid() {
        if (cachedSystemLogs == null || lastUpdatedTime == null) {
            return false;
        }

        long minutesElasped = ChronoUnit.MINUTES.between(lastUpdatedTime, LocalDateTime.now());
        return minutesElasped < CACHE_TTL_MINUTES;
    }

    public List<LogsDetails> getCachedSystemLogs() {
        if (isCachedLogsValid()) {
            return cachedSystemLogs;
        }

        return null;
    }

    public void setCachedSystemLogs(List<LogsDetails> cachedSystemLogs) {
        this.cachedSystemLogs = cachedSystemLogs;
        this.lastUpdatedTime = LocalDateTime.now();
    }

    public void clearCachedSystemLogs() {
        this.cachedSystemLogs = null;
        this.lastUpdatedTime = null;
    }
}
