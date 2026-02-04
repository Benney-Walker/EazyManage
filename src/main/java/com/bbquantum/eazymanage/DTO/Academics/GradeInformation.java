package com.bbquantum.eazymanage.DTO.Academics;

import java.util.List;

public class GradeInformation {
    private String gradeId;

    private String gradeName;

    private String gradeSize;

    private List<StudentsRoaster> studentRoasters;

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getGradeSize() {
        return gradeSize;
    }

    public void setGradeSize(String gradeSize) {
        this.gradeSize = gradeSize;
    }

    public List<StudentsRoaster> getStudentRoasters() {
        return studentRoasters;
    }

    public void setStudentRoasters(List<StudentsRoaster> studentRoasters) {
        this.studentRoasters = studentRoasters;
    }

    @Override
    public String toString() {

        return gradeName;
    }
}
