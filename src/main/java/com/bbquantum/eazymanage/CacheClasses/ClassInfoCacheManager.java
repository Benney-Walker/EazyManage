package com.bbquantum.eazymanage.CacheClasses;

import com.bbquantum.eazymanage.DTO.Academics.Classes.ClassInformation;

import java.util.List;

public class ClassInfoCacheManager {
    private static ClassInfoCacheManager instance;

    private List<ClassInformation> gradesInformation;

    private ClassInfoCacheManager() {}

    public static ClassInfoCacheManager getInstance() {
        if (instance == null)
            instance = new ClassInfoCacheManager();
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
