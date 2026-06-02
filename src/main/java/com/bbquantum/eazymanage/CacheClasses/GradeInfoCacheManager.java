package com.bbquantum.eazymanage.CacheClasses;

import com.bbquantum.eazymanage.DTO.Academics.Classes.ClassInformation;

import java.util.List;

public class GradeInfoCacheManager {
    private static GradeInfoCacheManager instance;

    private List<ClassInformation> gradesInformation;

    private GradeInfoCacheManager() {}

    public static GradeInfoCacheManager getInstance() {
        if (instance == null)
            instance = new GradeInfoCacheManager();
        return instance;
    }

    public List<ClassInformation> getGradesInformation() {

        return gradesInformation;
    }

    public void setGradesInformation(List<ClassInformation> gradesInformation) {
        this.gradesInformation = gradesInformation;
    }

    public void clearCachedGradeStudents() {
        this.gradesInformation = null;
    }
}
