package com.bbquantum.eazymanage.CacheClasses;

import com.bbquantum.eazymanage.DTO.Academics.GradeInformation;

import java.time.LocalDateTime;
import java.util.List;

public class GradeInfoCacheManager {
    private static GradeInfoCacheManager instance;

    private List<GradeInformation> gradesInformation;

    private GradeInfoCacheManager() {}

    public static GradeInfoCacheManager getInstance() {
        if (instance == null)
            instance = new GradeInfoCacheManager();
        return instance;
    }

    public List<GradeInformation> getGradesInformation() {

        return gradesInformation;
    }

    public void setGradesInformation(List<GradeInformation> gradesInformation) {
        this.gradesInformation = gradesInformation;
    }

    public void clearCachedGradeStudents() {
        this.gradesInformation = null;
    }
}
