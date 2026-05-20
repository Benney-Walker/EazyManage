package com.bbquantum.eazymanage.DTO.Academics;

public class StudentSubjectResults {
    private String subjectName;

    private String classTest1Score;

    private String groupWorkScore;

    private String classTest2Score;

    private String projectScore;

    private String classScore;

    private String examScore;

    private String calculatedExamScore;

    private String total;

    private String grade;

    private String remark;

    /**
     * Free-text comment a teacher attaches to one subject's score row.
     * Distinct from {@link #remark}, which is the auto-derived grade
     * descriptor (e.g. "Excellent") mapped from the grading criteria.
     */
    private String teacherRemark;

    public StudentSubjectResults(String subjectName, String classTest1Score, String groupWorkScore, String classTest2Score,
                                 String projectScore, String classScore, String examScore, String calculatedExamScore, String total, String grade, String remark) {
        this.subjectName = subjectName;
        this.classTest1Score = classTest1Score;
        this.groupWorkScore = groupWorkScore;
        this.classTest2Score = classTest2Score;
        this.projectScore = projectScore;
        this.classScore = classScore;
        this.examScore = examScore;
        this.calculatedExamScore = calculatedExamScore;
        this.total = total;
        this.grade = grade;
        this.remark = remark;
    }

    /** Convenience constructor including the teacher-typed remark. */
    public StudentSubjectResults(String subjectName, String classTest1Score, String groupWorkScore, String classTest2Score,
                                 String projectScore, String classScore, String examScore, String calculatedExamScore,
                                 String total, String grade, String remark, String teacherRemark) {
        this(subjectName, classTest1Score, groupWorkScore, classTest2Score, projectScore,
                classScore, examScore, calculatedExamScore, total, grade, remark);
        this.teacherRemark = teacherRemark;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getClassTest1Score() {
        return classTest1Score;
    }

    public void setClassTest1Score(String classTest1Score) {
        this.classTest1Score = classTest1Score;
    }

    public String getGroupWorkScore() {
        return groupWorkScore;
    }

    public void setGroupWorkScore(String groupWorkScore) {
        this.groupWorkScore = groupWorkScore;
    }

    public String getClassTest2Score() {
        return classTest2Score;
    }

    public void setClassTest2Score(String classTest2Score) {
        this.classTest2Score = classTest2Score;
    }

    public String getProjectScore() {
        return projectScore;
    }

    public void setProjectScore(String projectScore) {
        this.projectScore = projectScore;
    }

    public String getClassScore() {
        return classScore;
    }

    public void setClassScore(String classScore) {
        this.classScore = classScore;
    }

    public String getExamScore() {
        return examScore;
    }

    public void setExamScore(String examScore) {
        this.examScore = examScore;
    }

    public String getCalculatedExamScore() {
        return calculatedExamScore;
    }

    public void setCalculatedExamScore(String calculatedExamScore) {
        this.calculatedExamScore = calculatedExamScore;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTeacherRemark() {
        return teacherRemark;
    }

    public void setTeacherRemark(String teacherRemark) {
        this.teacherRemark = teacherRemark;
    }
}
