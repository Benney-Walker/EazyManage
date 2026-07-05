package com.bbquantum.eazymanage.CacheClasses;

import com.bbquantum.eazymanage.DTO.Academics.Holiday.Holiday;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class HolidayCachingManager {

    private static HolidayCachingManager instance;

    private List<Holiday> cachedHolidays;
    private LocalDateTime lastUpdatedTime;

    private static final long CACHE_TTL_MINUTES = 120;

    private HolidayCachingManager() {}

    public static HolidayCachingManager getInstance() {
        if (instance == null) {
            instance = new HolidayCachingManager();
        }
        return instance;
    }

    public boolean isHolidayCacheValid() {
        if (cachedHolidays == null || lastUpdatedTime == null) {
            return false;
        }
        long minutesElapsed = ChronoUnit.MINUTES.between(lastUpdatedTime, LocalDateTime.now());
        return minutesElapsed < CACHE_TTL_MINUTES;
    }

    public List<Holiday> getCachedHolidays() {
        if (isHolidayCacheValid()) {
            return cachedHolidays;
        }
        return null;
    }

    public void setCachedHolidays(List<Holiday> cachedHolidays) {
        this.cachedHolidays = cachedHolidays;
        this.lastUpdatedTime = LocalDateTime.now();
    }

    public void clearCachedHolidays() {
        this.cachedHolidays = null;
        this.lastUpdatedTime = LocalDateTime.now();
    }
}
