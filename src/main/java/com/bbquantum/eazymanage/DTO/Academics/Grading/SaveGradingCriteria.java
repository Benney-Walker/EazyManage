package com.bbquantum.eazymanage.DTO.Academics.Grading;

public class SaveGradingCriteria {
    private int id;

    private Double lowerRange;

    private Double upperRange;

    private String grade;

    private String gradeDescription;

    public SaveGradingCriteria(Double lowerRange, Double upperRange, String grade, String gradeDescription) {
        this.lowerRange = lowerRange;
        this.upperRange = upperRange;
        this.grade = grade;
        this.gradeDescription = gradeDescription;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getLowerRange() {
        return lowerRange;
    }

    public void setLowerRange(Double lowerRange) {
        this.lowerRange = lowerRange;
    }

    public Double getUpperRange() {
        return upperRange;
    }

    public void setUpperRange(Double upperRange) {
        this.upperRange = upperRange;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getGradeDescription() {
        return gradeDescription;
    }

    public void setGradeDescription(String gradeDescription) {
        this.gradeDescription = gradeDescription;
    }
}
