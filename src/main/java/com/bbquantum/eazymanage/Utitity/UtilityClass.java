package com.bbquantum.eazymanage.Utitity;

import com.bbquantum.eazymanage.CacheClasses.*;
import com.bbquantum.eazymanage.Constants.StaffRoles;
import com.bbquantum.eazymanage.DTO.Academics.Report.ViewClassSemesterReport;
import com.bbquantum.eazymanage.DTO.Academics.Subject.SubjectDTO;
import com.bbquantum.eazymanage.DTO.Academics.Subject.SubjectScores;
import com.bbquantum.eazymanage.DTO.Academics.Subject.SubjectsHolder;
import com.bbquantum.eazymanage.DTO.Finance.StudentFeesPaymentDisplay;
import com.bbquantum.eazymanage.DTO.System.FindAndUpdateClassInfo;
import com.bbquantum.eazymanage.DTO.Academics.Students.StudentsHolder;
import com.bbquantum.eazymanage.DTO.Academics.Students.UpdateStudentPersonalData;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

public class UtilityClass {

    private static final String LOCAL_URL = "http://localhost:8080";

    private static final String PRODUCTION_URL = "https://schoolmanagementsystem-eazymanage.up.railway.app";

    public static final String BASE_URL =  LOCAL_URL + "/api";

    /** Application version banner. Displayed on the dashboard footer. */
    public static final String APPLICATION_VERSION = "V 3.2.0.0";

    // Cache Managers
    public static final RecentStudentCacheManager cacheManager = RecentStudentCacheManager.getInstance();
    public static final StaffCacheManager staffCacheManager = StaffCacheManager.getInstance();
    public static final GradeCacheManager gradeCacheManager = GradeCacheManager.getInstance();
    public static final FeesCacheManager feesCacheManager = FeesCacheManager.getInstance();
    public static final SemesterCachingManager semesterCachingManager = SemesterCachingManager.getInstance();
    public static final StaffListCacheManager staffListCacheManager = StaffListCacheManager.getInstance();
    public static final GradeInfoCacheManager gradeInfoCacheManager = GradeInfoCacheManager.getInstance();
    public static final SystemLogsCacheManager systemLogsCacheManager = SystemLogsCacheManager.getInstance();
    public static final HolidayCachingManager holidayCachingManager = HolidayCachingManager.getInstance();

    // System own variables
    public static Long totalStudentsFromBackend = 0L;
    public static Long totalStaffFromBackend = 0L;
    public static Long totalTeachingStaff = 0L;
    public static Double totalSemesterFeesFromBackend = 0D;
    public static Double feesPaidFromBackend = 0D;
    public static Double feesProgressBarFromBackend = 0D;

    //Holds class information for update
    public static FindAndUpdateClassInfo findAndUpdateClassInfo;

    //Holds Students
    public static List<StudentsHolder> studentsHolder;

    //Holds Subjects
    public static List<SubjectsHolder> subjectsHolder;

    // Stores student information for update
    public static UpdateStudentPersonalData updateStudentPersonalData;

    //Stores students for scores input
    public static SubjectScores subjectScores;

    //Holds subject data for upload and retrieval
    public static SubjectDTO subjectData;

    //Contains students results
    public static List<ViewClassSemesterReport> classResults;

    // Stores retrieved fees report from backend
    public static StudentFeesPaymentDisplay paymentRecords;

    //Holds staff roles for update
    public static List<StaffRoles> staffRoles = new ArrayList<>();

    // Clears session Data
    public static void clearSession() {

        SessionManager.clearSession();

        clearSessionForRefresh();
    }

    public static void clearSessionForRefresh() {
        // Clear cached backend data
        feesCacheManager.clearClassFeesSummaryCache();
        feesCacheManager.clearRecentPaymentCache();

        gradeCacheManager.clearCache();
        gradeInfoCacheManager.clearCachedGradeStudents();
        cacheManager.clearCachedStudents();
        semesterCachingManager.clearCachedSemesters();
        staffCacheManager.clearCachedStaffList();
        staffListCacheManager.clearCachedStaffList();

        // Clear dashboard numbers
        totalStudentsFromBackend = 0L;
        totalStaffFromBackend = 0L;
        totalSemesterFeesFromBackend = 0.0;
        feesPaidFromBackend = 0.0;
        feesProgressBarFromBackend = 0.0;

        // Clear reports / temp objects
        paymentRecords = null;
    }

    public static void showError(StackPane notification, String message) {
        Platform.runLater(() -> {
            NotificationUtil.showError(notification, message);
        });
    }
}
