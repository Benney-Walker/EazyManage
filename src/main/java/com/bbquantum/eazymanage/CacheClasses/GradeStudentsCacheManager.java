package com.bbquantum.eazymanage.CacheClasses;

import com.bbquantum.eazymanage.DTO.Academics.GradeStudentsDisplayDTO;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class GradeStudentsCacheManager {
    private static GradeStudentsCacheManager instance;

    private List<GradeStudentsDisplayDTO> cachedGradeStudents;

    private LocalDateTime lastUpdatedTime;

    private static final long CACHE_TTL_MINUTES = 60;

    private GradeStudentsCacheManager() {}

    public static GradeStudentsCacheManager getInstance() {
        if (instance == null)
            instance = new GradeStudentsCacheManager();
        return instance;
    }

    public boolean isCacheValid() {
        if (cachedGradeStudents == null || lastUpdatedTime == null)
            return false;

        long minutes = ChronoUnit.MINUTES.between(lastUpdatedTime, LocalDateTime.now());
        return minutes < CACHE_TTL_MINUTES;
    }

    public List<GradeStudentsDisplayDTO> getCachedGradeStudents() {
        if (isCacheValid()) {
            return cachedGradeStudents;
        }
        return null;
    }

    public void setCachedGradeStudents(List<GradeStudentsDisplayDTO> cachedGradeStudents) {
        this.cachedGradeStudents = cachedGradeStudents;
        this.lastUpdatedTime = LocalDateTime.now();
    }

    public void clearCachedGradeStudents() {
        this.cachedGradeStudents = null;
        this.lastUpdatedTime = LocalDateTime.now();
    }
}
