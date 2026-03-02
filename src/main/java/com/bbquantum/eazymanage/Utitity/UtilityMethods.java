package com.bbquantum.eazymanage.Utitity;

import com.bbquantum.eazymanage.CacheClasses.*;
import com.bbquantum.eazymanage.DTO.Academics.PrintLevelSubjects;
import com.bbquantum.eazymanage.DTO.Academics.StudentListPrint;
import com.bbquantum.eazymanage.DTO.Academics.SubjectScores;
import com.bbquantum.eazymanage.DTO.Academics.SubjectsHolder;
import com.bbquantum.eazymanage.DTO.Finance.GradeFeesReport;
import com.bbquantum.eazymanage.DTO.Finance.PaymentHistory;
import com.bbquantum.eazymanage.DTO.Institution.FindAndUpdateClassInfo;
import com.bbquantum.eazymanage.DTO.Institution.StudentsHolder;
import com.bbquantum.eazymanage.DTO.Institution.UpdateStudentPersonalData;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

public class UtilityMethods {

    // Cache Managers
    public static final RecentStudentCacheManager cacheManager = RecentStudentCacheManager.getInstance();
    public static final StaffCacheManager staffCacheManager = StaffCacheManager.getInstance();
    public static final GradeCacheManager gradeCacheManager = GradeCacheManager.getInstance();
    public static final FeesCacheManager feesCacheManager = FeesCacheManager.getInstance();
    public static final SemesterCachingManager semesterCachingManager = SemesterCachingManager.getInstance();
    public static final StaffListCacheManager staffListCacheManager = StaffListCacheManager.getInstance();
    public static final GradeInfoCacheManager gradeInfoCacheManager = GradeInfoCacheManager.getInstance();
    public static final SystemLogsCacheManager systemLogsCacheManager = SystemLogsCacheManager.getInstance();

    // System own variables
    public static Long totalStudentsFromBackend = 0L;
    public static Long totalStaffFromBackend = 0L;
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


    public static void clearSession() {

        SessionManager.clearSession();

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
        ApiUtilityClass.paymentRecords = null;
    }

    public static void showError(StackPane notification, String message) {
        Platform.runLater(() -> {
            NotificationUtil.showError(notification, message);
        });
    }

    public static void showSuccess(StackPane notification, String message) {
        Platform.runLater(() -> {
            NotificationUtil.showSuccess(notification, message);
        });
    }

    public static void processingButton(Button button) {
        button.setText("Processing...");
        button.setDisable(true);
    }

    public static void notProcessingButton(Button button, String buttonText) {
        button.setText(buttonText);
        button.setDisable(false);
    }

    public static void switchPageCards(VBox cardToShow, VBox cardToHide,
                                 Button actionButton, Button inactiveButton) {
        cardToHide.setVisible(false);
        cardToHide.setManaged(false);

        cardToShow.setOpacity(0);
        cardToShow.setTranslateY(20);
        cardToShow.setVisible(true);
        cardToShow.setManaged(true);

        FadeTransition fade = new FadeTransition(Duration.millis(180), cardToShow);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(180), cardToShow);
        slide.setFromY(20);
        slide.setToY(0);

        new ParallelTransition(fade, slide).play();

        setActiveToggle(actionButton, inactiveButton);
    }

    public static void setActiveToggle(Button active, Button inactive) {
        active.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: white;");
        inactive.setStyle("-fx-background-color: transparent; -fx-text-fill: #334155;");
    }

    public static void saveSubjectListToFile(List<PrintLevelSubjects> subjects, File file, String levelName) throws Exception {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Header
            writer.println("========================================");
            writer.println("         SUBJECT LIST - " + levelName);
            writer.println("========================================");
            writer.println();
            writer.println(String.format("%-5s | %-20s | %-30s", "No.", "Student ID", "Full Name"));
            writer.println("---------------------------------------------------");

            // Data rows
            int count = 1;
            for (PrintLevelSubjects subject : subjects) {
                writer.println(String.format("%-5d | %-20s | %-30s",
                        count++,
                        subject.getSubjectId(),
                        subject.getSubjectName()));
            }

            writer.println();
            writer.println("---------------------------------------------------");
            writer.println("Total Subjects: " + subjects.size());
            writer.println("Generated on: " + LocalDate.now());
        }
    }

    public static void saveStudentPayRecordsListToFile(List<PaymentHistory> records,
                                                File file,
                                                String levelName,
                                                String semesterName,
                                                String studentName,
                                                String studentId) throws Exception {

        try (PrintWriter writer = new PrintWriter(
                new BufferedWriter(new FileWriter(file, false)))) {

            // Header
            writer.println("===============================================================");
            writer.println("                    STUDENT PAYMENT RECORDS                    ");
            writer.println("===============================================================");
            writer.println("STUDENT ID : " + studentId);
            writer.println("STUDENT NAME : " + studentName);
            writer.println("GRADE : " + levelName);
            writer.println("SEMESTER : " + semesterName);
            writer.println("---------------------------------------------------------------");

            // Column titles
            writer.println(String.format(
                    "%-4s | %-12s | %-10s | %-10s | %-20s | %-15s",
                    "No",
                    "Date",
                    "Amount",
                    "Balance",
                    "Payer",
                    "Contact"
            ));

            writer.println("---------------------------------------------------------------");

            // Data rows
            int count = 1;
            for (PaymentHistory record : records) {
                writer.println(String.format(
                        "%-4d | %-12s | %-10s | %-10s | %-20s | %-15s",
                        count++,
                        record.getDateOfPayment(),
                        record.getAmountPaid(),
                        record.getFeesBalance(),
                        record.getPersonWhoPaid(),
                        record.getContactInfo()
                ));
            }

            writer.println("---------------------------------------------------------------");
            writer.println("Total Transactions: " + records.size());
            writer.println("Generated on: " + LocalDate.now());
        }
    }

    public static void saveGradePayRecordsListToFile(List<GradeFeesReport> records,
                                              File file,
                                              String levelName,
                                              String semesterName) throws Exception {

        try (PrintWriter writer = new PrintWriter(
                new BufferedWriter(new FileWriter(file, false)))) {

            // Header
            writer.println("===============================================================");
            writer.println("                          PAYMENT RECORDS                      ");
            writer.println("===============================================================");
            writer.println("GRADE : " + levelName);
            writer.println("SEMESTER : " + semesterName);
            writer.println("---------------------------------------------------------------");

            // Column titles
            writer.println(String.format(
                    "%-4s | %-12s | %-15s | %-10s | %-20s | %-15s",
                    "No",
                    "Student Id",
                    "Student Name",
                    "Fees Amount",
                    "Amount Paid",
                    "Outstanding"
            ));

            writer.println("---------------------------------------------------------------");

            // Data rows
            int count = 1;
            for (GradeFeesReport record : records) {
                writer.println(String.format(
                        "%-4d | %-12s | %-15s | %-10s | %-20s | %-15s",
                        count++,
                        record.getStudentId(),
                        record.getStudentName(),
                        record.getTotalAmountToPay(),
                        record.getAmountPayed(),
                        record.getOutstanding()

                ));
            }

            writer.println("---------------------------------------------------------------");
            writer.println("Total Students: " + records.size());
            writer.println("Generated on: " + LocalDate.now());
        }
    }


    public static void saveStudentListToFile(List<StudentListPrint> students, File file, String levelName) throws Exception {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Header
            writer.println("========================================");
            writer.println("         STUDENT LIST - " + levelName);
            writer.println("========================================");
            writer.println();
            writer.println(String.format("%-5s | %-20s | %-30s",
                    "No.",
                    "Student ID",
                    "Full Name"));
            writer.println("---------------------------------------------------");

            // Data rows
            int count = 1;
            for (StudentListPrint student : students) {
                writer.println(String.format("%-5d | %-20s | %-30s",
                        count++,
                        student.getStudentId(),
                        student.getFullName()));
            }

            writer.println();
            writer.println("---------------------------------------------------");
            writer.println("Total Students: " + students.size());
            writer.println("Generated on: " + LocalDate.now());
        }
    }
}
