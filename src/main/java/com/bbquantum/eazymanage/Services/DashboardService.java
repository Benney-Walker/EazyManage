package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.DTO.Academics.GradeInformation;
import com.bbquantum.eazymanage.DTO.Finance.ClassFeesSummary;
import com.bbquantum.eazymanage.DTO.Finance.RecentPaymentTable;
import com.bbquantum.eazymanage.DTO.Institution.*;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.layout.StackPane;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class DashboardService {

    // Expected fan-out totals per dashboard. Match the legacy `checkCounter`
    // initialCounter constants exactly.
    private static final int PRINCIPAL_TOTAL  = 11;

    private static final int FINANCE_TOTAL    = 5;

    private static final int STAFF_TOTAL      = 3;

    private static final int ADMIN_TOTAL      = 5;

    private final ApiCore core;

    public DashboardService(ApiCore core) {
        this.core   = core;
    }

    // ====================================================================
    // ORCHESTRATORS — one per role
    // ====================================================================

    /** Fan out all 11 Principal dashboard loads in parallel. */
    public void loadPrincipalData(String principalId, StackPane notification, Runnable onSuccess) {
        LoadCounter counter = new LoadCounter(PRINCIPAL_TOTAL);
        Runnable showErrorToast = () -> Platform.runLater(
                () -> UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer));

        loadTotalFeesPaid       (counter, principalId, notification, onSuccess, showErrorToast);
        loadTotalSemesterFees   (counter, principalId, notification, onSuccess, showErrorToast);
        loadTotalStaff          (counter, principalId, notification, onSuccess, showErrorToast);
        loadTotalTeachingStaff  (counter, principalId, notification, onSuccess, showErrorToast);
        loadTotalStudents       (counter, principalId, notification, onSuccess, showErrorToast);
        loadAbsentStudents      (counter, principalId, notification, onSuccess, showErrorToast);
        loadCachedStaffList     (counter, principalId, notification, onSuccess, showErrorToast);
        loadGradesFromBackend   (counter, principalId, notification, onSuccess, showErrorToast);
        loadSemestersFromBackend(counter, principalId, notification, onSuccess, showErrorToast);
        loadClassSummaryFees    (counter, principalId, notification, onSuccess, showErrorToast);
        loadAllStaffList        (counter, principalId, notification, onSuccess, showErrorToast);
    }

    /** Fan out all 5 Finance dashboard loads in parallel. */
    public void loadFinanceDashboardData(String financeId, StackPane notification, Runnable onSuccess) {
        LoadCounter counter = new LoadCounter(FINANCE_TOTAL);
        Runnable showErrorToast = () -> Platform.runLater(
                () -> UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer));

        loadTotalFeesPaid           (counter, financeId, notification, onSuccess, showErrorToast);
        loadTotalSemesterFees       (counter, financeId, notification, onSuccess, showErrorToast);
        loadGradesFromBackend       (counter, financeId, notification, onSuccess, showErrorToast);
        loadSemestersFromBackend    (counter, financeId, notification, onSuccess, showErrorToast);
        fetchRecentFeesTransactions (counter, financeId, notification, onSuccess, showErrorToast);
    }

    /** Fan out all 3 Staff dashboard loads in parallel. */
    public void loadStaffDashboardData(String staffId, StackPane notification, Runnable onSuccess) {
        LoadCounter counter = new LoadCounter(STAFF_TOTAL);
        Runnable showErrorToast = () -> Platform.runLater(
                () -> UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer));

        loadGradesFromBackend    (counter, staffId, notification, onSuccess, showErrorToast);
        loadSemestersFromBackend (counter, staffId, notification, onSuccess, showErrorToast);
        loadStaffGrades          (counter, staffId, notification, onSuccess, showErrorToast);
    }

    /** Fan out all 5 Admin dashboard loads in parallel. */
    public void loadAdminDashboard(String staffId, StackPane notification, Runnable onSuccess) {
        LoadCounter counter = new LoadCounter(ADMIN_TOTAL);
        Runnable showErrorToast = () -> Platform.runLater(
                () -> UtilityClass.showError(notification, ApiContext.getInstance().errorMessageContainer));

        fetchRecentSystemLogs    (counter, staffId, notification, onSuccess, showErrorToast);
        loadCachedStaffList      (counter, staffId, notification, onSuccess, showErrorToast);
        loadGradesFromBackend    (counter, staffId, notification, onSuccess, showErrorToast);
        loadSemestersFromBackend (counter, staffId, notification, onSuccess, showErrorToast);
        loadAllStaffList         (counter, staffId, notification, onSuccess, showErrorToast);
    }

    // ====================================================================
    // LEAF LOADERS — totals
    // ====================================================================

    public void loadTotalStudents(LoadCounter counter, String staffId, StackPane notification,
                                  Runnable onSuccess, Runnable onFailure) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.STUDENT_URL + core.API_VERSION_1 + "/total-students/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + core.token)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }
                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response != null) {
                UtilityClass.totalStudentsFromBackend = Long.parseLong(response);
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            String msg = ex.getMessage();
            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onFailure, onFailure);  // legacy: failure branch funnels onFailure to onComplete
        });

        new Thread(task).start();
    }

    public void loadTotalStaff(LoadCounter counter, String staffId, StackPane notification,
                               Runnable onSuccess, Runnable onFailure) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.STAFF_URL + core.API_VERSION_1 + "/total-staffs/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + core.token)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Message: " + response.body());
                if (response.statusCode() == 200) {
                    return response.body();
                }
                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response != null) {
                UtilityClass.totalStaffFromBackend = Long.parseLong(response);
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            String msg = ex.getMessage();
            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onFailure, onFailure);
        });

        new Thread(task).start();
    }

    public void loadTotalTeachingStaff(LoadCounter counter, String staffId, StackPane notification,
                                       Runnable onSuccess, Runnable onFailure) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.STAFF_URL + core.API_VERSION_1 + "/total-teaching-staffs/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + core.token)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }
                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response != null) {
                UtilityClass.totalTeachingStaff = Long.parseLong(response);
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            String msg = ex.getMessage();
            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onFailure, onFailure);
        });

        new Thread(task).start();
    }

    public void loadTotalSemesterFees(LoadCounter counter, String staffId, StackPane notification,
                                      Runnable onSuccess, Runnable onFailure) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.FINANCE_URL + core.API_VERSION_1 + "/total-fees";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }
                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response != null) {
                UtilityClass.totalSemesterFeesFromBackend = Double.parseDouble(response);
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            String msg = ex.getMessage();
            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        new Thread(task).start();
    }

    public void loadTotalFeesPaid(LoadCounter counter, String staffId, StackPane notification,
                                  Runnable onSuccess, Runnable onFailure) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.FINANCE_URL + core.API_VERSION_1 + "/fees-paid";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }
                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response != null) {
                UtilityClass.feesPaidFromBackend = Double.parseDouble(response);
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            String msg = ex.getMessage();
            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet connection.");
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        new Thread(task).start();
    }

    // ====================================================================
    // LEAF LOADERS — lists, cached
    // ====================================================================

    public void loadAbsentStudents(LoadCounter counter, String staffId, StackPane notification,
                                   Runnable onSuccess, Runnable onFailure) {

        // Fast-path: serve from cache if still valid; still ticks once for fan-in.
        List<AbsenteesView> cachedStudents = UtilityClass.cacheManager.getCachedAbsenteeStudents();
        if (cachedStudents != null && !cachedStudents.isEmpty()) {
            if (UtilityClass.cacheManager.isCacheValid()) {
                counter.tick(onSuccess, null);
                return;
            }
        }

        Type studentListType = new TypeToken<List<AbsenteesView>>() {}.getType();

        Task<List<AbsenteesView>> task = new Task<>() {
            @Override
            protected List<AbsenteesView> call() throws Exception {
                String url = core.STUDENT_URL + core.API_VERSION_1 + "/absent-students/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + core.token)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), studentListType);
                }
                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<AbsenteesView> students = task.getValue();
            if (students == null) {
                counter.tick(onSuccess, onFailure);
                return;
            }
            if (students.isEmpty()) {
                ApiContext.getInstance().successMessageContainer = "Today's attendance not recorded";
                counter.tick(onSuccess, null);
                return;
            }
            UtilityClass.cacheManager.setCachedAbsenteeStudents(students);
            counter.tick(onSuccess, onFailure);     // legacy branch
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            String msg = ex.getMessage();
            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        new Thread(task).start();
    }

    public void loadCachedStaffList(LoadCounter counter, String staffId, StackPane notification,
                                    Runnable onSuccess, Runnable onFailure) {
        Type staffListType = new TypeToken<List<StaffCaching>>() {}.getType();

        Task<List<StaffCaching>> task = new Task<>() {
            @Override
            protected List<StaffCaching> call() throws Exception {
                String url = core.STAFF_URL + core.API_VERSION_1 + "/load-staffs-info";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.body());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), staffListType);
                }
                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<StaffCaching> staffs = task.getValue();
            if (staffs != null) {
                UtilityClass.staffCacheManager.setStaffList(staffs);
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            String msg = ex.getMessage();
            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        new Thread(task).start();
    }

    public void loadGradesFromBackend(LoadCounter counter, String staffId, StackPane notification,
                                      Runnable onSuccess, Runnable onFailure) {

        List<GradeCaching> cachedGrades = UtilityClass.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty()
                && UtilityClass.gradeCacheManager.isCachedGradesValid()) {
            counter.tick(onSuccess, null);
            return;
        }

        Type classListType = new TypeToken<List<GradeCaching>>() {}.getType();

        Task<List<GradeCaching>> task = new Task<>() {
            @Override
            protected List<GradeCaching> call() throws Exception {
                String url = core.STAFF_URL + core.API_VERSION_1 + "/load-levels";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), classListType);
                }
                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<GradeCaching> gradesList = task.getValue();
            System.out.println("success");
            if (gradesList != null && !gradesList.isEmpty()) {
                UtilityClass.gradeCacheManager.setCachedGrades(gradesList);
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            String msg = ex.getMessage();
            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        new Thread(task).start();
    }

    public void loadSemestersFromBackend(LoadCounter counter, String staffId, StackPane notification,
                                         Runnable onSuccess, Runnable onFailure) {

        List<SemesterCaching> semesterCaching = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (semesterCaching != null && !semesterCaching.isEmpty()) {
            if (UtilityClass.semesterCachingManager.isSemesterCacheValid()) {
                counter.tick(onSuccess, null);
                return;
            }
        }

        Type semesterListType = new TypeToken<ArrayList<SemesterCaching>>() {}.getType();

        Task<List<SemesterCaching>> task = new Task<>() {
            @Override
            protected List<SemesterCaching> call() throws Exception {
                String url = core.STAFF_URL + core.API_VERSION_1 + "/load-semesters";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), semesterListType);
                }
                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<SemesterCaching> semesterCachingList = task.getValue();
            if (semesterCachingList != null && !semesterCachingList.isEmpty()) {
                UtilityClass.semesterCachingManager.setCachedSemesters(semesterCachingList);
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            String msg = ex.getMessage();
            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        new Thread(task).start();
    }

    public void loadClassSummaryFees(LoadCounter counter, String staffId, StackPane notification,
                                     Runnable onSuccess, Runnable onFailure) {
        Type classFeesList = new TypeToken<ArrayList<ClassFeesSummary>>() {}.getType();

        Task<List<ClassFeesSummary>> task = new Task<>() {
            @Override
            protected List<ClassFeesSummary> call() throws Exception {
                String url = core.FINANCE_URL + core.API_VERSION_1 + "/class-summary-fees/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + core.token)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), classFeesList);
                }
                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<ClassFeesSummary> classFeesSummaryList = task.getValue();
            if (classFeesSummaryList != null && !classFeesSummaryList.isEmpty()) {
                UtilityClass.feesCacheManager.setClassFeesSummaryCache(classFeesSummaryList);
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            String msg = ex.getMessage();
            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        new Thread(task).start();
    }

    public void loadStaffGrades(LoadCounter counter, String staffId, StackPane notification,
                                Runnable onSuccess, Runnable onFailure) {
        Type studentList = new TypeToken<ArrayList<GradeInformation>>() {}.getType();

        Task<List<GradeInformation>> task = new Task<>() {
            @Override
            protected List<GradeInformation> call() throws Exception {
                String url = core.STAFF_URL + core.API_VERSION_1 + "/load-staff-grades/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + core.token)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), studentList);
                }
                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<GradeInformation> gradesInformation = task.getValue();
            if (gradesInformation != null && !gradesInformation.isEmpty()) {
                UtilityClass.gradeInfoCacheManager.setGradesInformation(gradesInformation);
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            String msg = ex.getMessage();
            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        new Thread(task).start();
    }

    public void loadAllStaffList(LoadCounter counter, String staffId, StackPane notification,
                                 Runnable onSuccess, Runnable onFailure) {
        Type staffListListType = new TypeToken<ArrayList<ViewStaffList>>() {}.getType();

        Task<List<ViewStaffList>> task = new Task<>() {
            @Override
            protected List<ViewStaffList> call() throws Exception {
                String url = core.STAFF_URL + core.API_VERSION_1 + "/staff-list";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), staffListListType);
                }
                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<ViewStaffList> staffViewList = task.getValue();
            if (staffViewList != null && !staffViewList.isEmpty()) {
                UtilityClass.staffListCacheManager.setStaffList(staffViewList);
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            String msg = ex.getMessage();
            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter.tick(onFailure, null);    // legacy: net-error path passed onFailure as completion
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        new Thread(task).start();
    }

    public void fetchRecentFeesTransactions(LoadCounter counter, String staffId, StackPane notification,
                                            Runnable onSuccess, Runnable onFailure) {
        Type listType = new TypeToken<List<RecentPaymentTable>>() {}.getType();

        Task<List<RecentPaymentTable>> task = new Task<>() {
            @Override
            protected List<RecentPaymentTable> call() throws Exception {
                String url = core.FINANCE_URL + core.API_VERSION_1 + "/recent-fees-transactions";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), listType);
                }
                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<RecentPaymentTable> recentPayments = task.getValue();
            if (recentPayments != null && !recentPayments.isEmpty()) {
                UtilityClass.feesCacheManager.setRecentPaymentCache(recentPayments);
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            String msg = ex.getMessage();
            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                counter.tick(onFailure, null);    // legacy: no toast here, just fail
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        new Thread(task).start();
    }

    public void fetchRecentSystemLogs(LoadCounter counter, String staffId, StackPane notification,
                                      Runnable onSuccess, Runnable onFailure) {
        Type listType = new TypeToken<List<LogsDetails>>() {}.getType();

        Task<List<LogsDetails>> task = new Task<>() {
            @Override
            protected List<LogsDetails> call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_1 + "/recentLogs";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), listType);
                }
                ApiContext.getInstance().errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<LogsDetails> response = task.getValue();
            if (response != null) {
                UtilityClass.systemLogsCacheManager.setCachedSystemLogs(response);
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            String msg = ex.getMessage();
            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter.tick(onSuccess, null);
                return;
            }
            counter.tick(onSuccess, onFailure);
        });

        new Thread(task).start();
    }
}
