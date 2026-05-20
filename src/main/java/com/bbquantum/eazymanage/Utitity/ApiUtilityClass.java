package com.bbquantum.eazymanage.Utitity;

import com.bbquantum.eazymanage.Controller.*;
import com.bbquantum.eazymanage.DTO.Academics.*;
import com.bbquantum.eazymanage.DTO.Finance.*;
import com.bbquantum.eazymanage.DTO.Institution.*;
import com.bbquantum.eazymanage.Services.*;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;

public class ApiUtilityClass {

    public final String applicationVersion = "V 2.6.1";

    // Controllers - only one will be set at a time
    private final PrincipalController principalController;
    private final FinanceController financeController;
    private final StaffController staffController;
    private final AdminController adminController;

    //Store staff Data for Update
    public FindStaffResponse findStaffResponse;

    //Counter for loading Dashboards
    private int counter = 0;

    //Auth Token
    private final String token = SessionManager.getToken();

    //Backend response messages
    public String errorMessageContainer = "";
    public String successMessageContainer = "";

    // Store student results
    public StudentResult studentResult;


    private final ApiCore core = new ApiCore();
    private final AuthService authService = new AuthService(core, this);
    private final AttendanceService attendanceService = new AttendanceService(core, this);
    private final StudentService studentService = new StudentService(core, this);
    private final FinanceService financeService = new FinanceService(core, this);
    private final StaffService staffService = new StaffService(core, this);
    private final AcademicsService academicsService   = new AcademicsService(core, this);
    private final DashboardService dashboardService   = new DashboardService(core, this);
    private final ConductService conductService = new ConductService(core, this);

    // PRINCIPAL CONSTRUCTOR
    public ApiUtilityClass(PrincipalController principalController) {
        this.principalController = principalController;
        this.financeController = null;
        this.staffController = null;
        this.adminController = null;
    }

    // FINANCE CONSTRUCTOR
    public ApiUtilityClass(FinanceController financeController) {
        this.financeController = financeController;
        this.principalController = null;
        this.staffController = null;
        this.adminController = null;
    }

    // STAFF CONSTRUCTOR
    public ApiUtilityClass(StaffController staffController) {
        this.staffController = staffController;
        this.principalController = null;
        this.financeController = null;
        this.adminController = null;
    }

    //ADMIN CONTROLLER
    public ApiUtilityClass(AdminController adminController) {
        this.adminController = adminController;
        this.principalController = null;
        this.staffController = null;
        this.financeController = null;
    }



    public void loadPrincipalData(String principalId, StackPane notification, Runnable onSuccess) {
        dashboardService.loadPrincipalData(principalId, notification, onSuccess);
    }

    public void loadFinanceDashboardData(String financeId, StackPane notification, Runnable onSuccess) {
        dashboardService.loadFinanceDashboardData(financeId, notification, onSuccess);
    }

    public void loadStaffDashboardData(String staffId, StackPane notification, Runnable onSuccess) {
        dashboardService.loadStaffDashboardData(staffId, notification, onSuccess);
    }

    public void loadAdminDashboard(String staffId, StackPane notification, Runnable onSuccess) {
        dashboardService.loadAdminDashboard(staffId, notification, onSuccess);
    }

    public void enrollNewStudent(String staffId, String firstName, String lastName, String levelId, String gender,
                                 LocalDate dateOfBirth, String hometown, String parentName, String guardianContact,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        studentService.enrollNewStudent(staffId, firstName, lastName, levelId, gender,
                dateOfBirth, hometown, parentName, guardianContact,
                onSuccess, onFailure, onNetworkIssue);
    }

    public void loadSubjectStudents(String subjectId, String staffId,
                                    Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        academicsService.loadSubjectStudents(subjectId, staffId, onSuccess, onFailure, onNetworkIssue);
    }

    public void saveStudentScores(StudentsScoresTable scores, String subjectId, String semesterId, String staffId,
                                  Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        academicsService.saveStudentScores(scores, subjectId, semesterId, staffId, onSuccess, onFailure, onNetworkIssue);
    }

    public void saveSubjectTeacherRemark(String studentId, String teacherRemark,
                                         String subjectId, String semesterId, String staffId,
                                         Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        academicsService.saveSubjectTeacherRemark(studentId, teacherRemark,
                subjectId, semesterId, staffId, onSuccess, onFailure, onNetworkIssue);
    }

    public void saveSemesterTeacherRemark(String studentId, String semesterRemark,
                                          String semesterId, String staffId,
                                          Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        academicsService.saveSemesterTeacherRemark(studentId, semesterRemark,
                semesterId, staffId, onSuccess, onFailure, onNetworkIssue);
    }

    public void loadClassConduct(String levelId, String semesterId, String staffId,
                                 java.util.function.Consumer<java.util.List<com.bbquantum.eazymanage.DTO.Academics.StudentConductRecord>> onResult,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        conductService.loadClassConduct(levelId, semesterId, staffId, onResult, onSuccess, onFailure, onNetworkIssue);
    }

    public void saveStudentConduct(com.bbquantum.eazymanage.DTO.Academics.StudentConductRecord record, String staffId,
                                   Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        conductService.saveStudentConduct(record, staffId, onSuccess, onFailure, onNetworkIssue);
    }

    public void addNewGrade(String staffId, String gradeName, String instructorId,
                             Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        academicsService.addNewGrade(staffId, gradeName, instructorId, onSuccess, onFailure, onNetworkIssue);
    }



    public void recoverPassword(String staffId, String newPasswordStaffId, String newPassword,
                                Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        authService.recoverPassword(staffId, newPasswordStaffId, newPassword,
                onSuccess, onFailure, onNetworkIssue);
    }

    public void fetchStudentPaymentRecords(String staffId, String studentId, String semesterId, String gradeId,
                                           Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        financeService.fetchStudentPaymentRecords(staffId, studentId, semesterId, gradeId,
                onSuccess, onFailure, onNetworkIssue);
    }

    public void getFeesFilterData(String ownUrl,
                                  Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        financeService.getFeesFilterData(ownUrl,
                result -> principalController.feesSummaryTable = result,
                onSuccess, onFailure, onNetworkIssue);
    }

    public void searchStaffData(String searchId, String staffId,
                                Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        staffService.searchStaffData(searchId, staffId, onSuccess, onFailure, onNetworkIssue);
    }

    public void searchStudentData(String staffId, String studentId,
                                  Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        studentService.searchStudentData(staffId, studentId, onSuccess, onFailure, onNetworkIssue);
    }

    public void updateStudentPersonalData(UpdateStudentPersonalData data, String staffId,
                                           Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        studentService.updateStudentPersonalData(data, staffId, onSuccess, onFailure, onNetworkIssue);
    }

    public void SearchStudentResults(String studentId, String semesterId, String classId,
                                     Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        academicsService.SearchStudentResults(studentId, semesterId, classId, onSuccess, onFailure, onNetworkIssue);
    }

    public void addNewFees(String staffId, String gradeId, String semesterId, String feesAmount,
                           Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        financeService.addNewFees(staffId, gradeId, semesterId, feesAmount,
                onSuccess, onFailure, onNetworkIssue);
    }

    public void addNewSemester(String staffId, String semesterName, LocalDate startDate,
                                LocalDate endDate, String academicYear,
                                Runnable onSuccess,  Runnable onFailure, Runnable onNetworkIssue) {

        academicsService.addNewSemester(staffId, semesterName, startDate, endDate, academicYear, onSuccess, onFailure, onNetworkIssue);
    }

    public void addNewSubject(AssignSubject assignment,
                               Runnable onSuccess,  Runnable onFailure, Runnable onNetworkIssue) {

        academicsService.addNewSubject(assignment, onSuccess, onFailure, onNetworkIssue);
    }

    public void addNewFeesPayment(String staffId, String studentId, String semesterId,
                                  String amountPaid, String payerName, String payerPhone,
                                  Runnable onSuccess,  Runnable onFailure, Runnable onNetworkIssue) {
        financeService.addNewFeesPayment(staffId, studentId, semesterId,
                amountPaid, payerName, payerPhone,
                onSuccess, onFailure, onNetworkIssue);
    }

    public void saveGradingCriteria(GradingCriteria gradingCriteria, String staffId,
                                    Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        academicsService.saveGradingCriteria(gradingCriteria, staffId, onSuccess, onFailure, onNetworkIssue);
    }

    public void downloadStudentsReport(String staffId, String levelId, String semesterId, String promotionGradeId, String passScore,
                                       Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        academicsService.downloadStudentsReport(staffId, levelId, semesterId, promotionGradeId, passScore, () -> adminController.generateReportBtn.getScene().getWindow(), onSuccess, onFailure, onNetworkIssue);
    }

    public void loadClassResults(String levelId, String semesterId,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        academicsService.loadClassResults(levelId, semesterId, onSuccess, onFailure, onNetworkIssue);
    }

    public void movePassedStudents(String levelId,
                                   Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        academicsService.movePassedStudents(levelId, onSuccess, onFailure, onNetworkIssue);
    }

    public void loadStudentsForAttendance(String levelId, String date,
                                          Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        // The Consumer here is what used to be the direct controller assignment
        // inside the task's setOnSucceeded. Keeping it on this side preserves the
        // service's freedom from controller types.
        attendanceService.loadStudentsForAttendance(levelId, date,
                result -> staffController.studentAttendances = result,
                onSuccess, onFailure, onNetworkIssue);
    }

    public void markStudentAttendance(String studentId, String staffId, String levelId, String status,
                                      Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        attendanceService.markStudentAttendance(studentId, staffId, levelId, status,
                onSuccess, onFailure, onNetworkIssue);
    }

    public void updateAttendance(String studentId, String staffId, String levelId, String status, String dateMarked,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        attendanceService.updateAttendance(studentId, staffId, levelId, status, dateMarked,
                onSuccess, onFailure, onNetworkIssue);
    }

    public void findClassInfo(String levelId, String staffId,
                              Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        academicsService.findClassInfo(levelId, staffId, onSuccess, onFailure, onNetworkIssue);
    }

    public void updateClassInfo(FindAndUpdateClassInfo updateInfo, String staffId,
                                Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        academicsService.updateClassInfo(updateInfo, staffId, onSuccess, onFailure, onNetworkIssue);
    }

    public void updateStaffInfo(FindStaffResponse info, String staffId,
                                Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        staffService.updateStaffInfo(info, staffId, onSuccess, onFailure, onNetworkIssue);
    }

    public void searchSemester(String semesterId,
                               Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        academicsService.searchSemester(semesterId, result -> adminController.semester = result, onSuccess, onFailure, onNetworkIssue);
    }

    public void updateSemesterInfo(FindSemester info,
                                   Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        academicsService.updateSemesterInfo(info, onSuccess, onFailure, onNetworkIssue);
    }

    public void fetchFeesDetails(String staffId, String semesterId, String levelId,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        financeService.fetchFeesDetails(staffId, semesterId, levelId,
                result -> financeController.feesDetails = result,
                onSuccess, onFailure, onNetworkIssue);
    }

    public void updateSemesterFees(String staffId, FetchFeesDetails update,
                                   Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        financeService.updateSemesterFees(staffId, update, onSuccess, onFailure, onNetworkIssue);
    }

    public void fetchPaymentRecord(String staffId, String studentId, String levelId, String semesterId,
                                   Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        financeService.fetchPaymentRecord(staffId, studentId, levelId, semesterId,
                result -> financeController.studentPaymentRecords = result,
                onSuccess, onFailure, onNetworkIssue);
    }

    public void deletePaymentRecord(String staffId, String transactionId,
                                    Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        financeService.deletePaymentRecord(staffId, transactionId, onSuccess, onFailure, onNetworkIssue);
    }

    public void updatePaymentDetails(String staffId, StudentPaymentRecords update,
                                     Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        financeService.updatePaymentDetails(staffId, update, onSuccess, onFailure, onNetworkIssue);
    }

    public void getStudentName(String studentId,
                               Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        studentService.getStudentName(studentId, onSuccess, onFailure, onNetworkIssue);
    }

    public void fetchGradeFeesRecords(String staffId, String levelId, String semesterId,
                                      Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        financeService.fetchGradeFeesRecords(staffId, levelId, semesterId,
                result -> financeController.gradeFeesReport = result,
                onSuccess, onFailure, onNetworkIssue);
    }

    public void loadClassStudents(String levelId, String staffId,
                                  Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        academicsService.loadClassStudents(levelId, staffId, onSuccess, onFailure, onNetworkIssue);
    }

    public void loadClassSubjects(String levelId, String staffId,
                                  Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        academicsService.loadClassSubjects(levelId, staffId, onSuccess, onFailure, onNetworkIssue);
    }

    public void fetchSubjectData(String staffId, String subjectId,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        academicsService.fetchSubjectData(staffId, subjectId, onSuccess, onFailure, onNetworkIssue);
    }

    public void updateSubjectDetails(SubjectDTO data,
                                     Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        academicsService.updateSubjectDetails(data, onSuccess, onFailure, onNetworkIssue);
    }

    public void deleteSubject(String staffId, String subjectId,
                              Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        academicsService.deleteSubject(staffId, subjectId, onSuccess, onFailure, onNetworkIssue);
    }

}