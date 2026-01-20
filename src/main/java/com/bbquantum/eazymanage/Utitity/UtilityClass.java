package com.bbquantum.eazymanage.Utitity;

import com.bbquantum.eazymanage.CacheClasses.*;
import com.bbquantum.eazymanage.Controller.*;
import com.bbquantum.eazymanage.DTO.Academics.*;
import com.bbquantum.eazymanage.DTO.Finance.ClassFeesSummary;
import com.bbquantum.eazymanage.DTO.Finance.FeesSummaryTable;
import com.bbquantum.eazymanage.DTO.Finance.RecentPaymentTable;
import com.bbquantum.eazymanage.DTO.Finance.StudentFeesPaymentDisplay;
import com.bbquantum.eazymanage.DTO.Institution.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UtilityClass {

    // Controllers - only one will be set at a time
    private final PrincipalController principalController;
    private final FinanceController financeController;
    private final StaffController staffController;

    //Counter for loading Dashboards
    int counter = 0;

    public String errorMessageContainer;

    // OWN HttpClient and Gson - don't rely on controllers
    private final HttpClient httpClient = SSLHelper.createTrustAllHttpClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    // Backend Configuration
    public final String BASE_URL = "https://schoolmanagementsystem-production-afae.up.railway.app/";
    public final String API_BASE_URL = "api/v1";

    // Stores retrieved fees report from backend
    public StudentFeesPaymentDisplay report;
    // Store student results
    public StudentResults studentResults;
    // Store Student Id
    public String studentId;

    // Cache Managers
    public final RecentStudentCacheManager cacheManager = RecentStudentCacheManager.getInstance();
    public final StaffCacheManager staffCacheManager = StaffCacheManager.getInstance();
    public final GradeCacheManager gradeCacheManager = GradeCacheManager.getInstance();
    public final FeesCacheManager feesCacheManager = FeesCacheManager.getInstance();
    public final SemesterCachingManager semesterCachingManager = SemesterCachingManager.getInstance();
    public final StaffListCacheManager staffListCacheManager = StaffListCacheManager.getInstance();
    public final GradeStudentsCacheManager gradeStudentsCacheManager = GradeStudentsCacheManager.getInstance();

    // Controller own variables
    public Long totalStudentsFromBackend = 0L;
    public Long totalStaffFromBackend = 0L;
    public Double totalSemesterFeesFromBackend = 0D;
    public Double feesPaidFromBackend = 0D;
    public Double feesProgressBarFromBackend = 0D;

    // PRINCIPAL CONSTRUCTOR
    public UtilityClass(PrincipalController principalController) {
        this.principalController = principalController;
        this.financeController = null;
        this.staffController = null;
    }

    // FINANCE CONSTRUCTOR
    public UtilityClass(FinanceController financeController) {
        this.financeController = financeController;
        this.principalController = null;
        this.staffController = null;
    }

    // STAFF CONSTRUCTOR
    public UtilityClass(StaffController staffController) {
        this.staffController = staffController;
        this.principalController = null;
        this.financeController = null;
    }

    public void loadPrincipalData(String principalId, StackPane notification, Runnable onSuccess) {
        loadTotalFeesPaid(principalId, notification, onSuccess);

        loadTotalSemesterFees(principalId, notification, onSuccess);

        loadTotalStaff(principalId, notification, onSuccess);

        loadTotalStudents(principalId, notification, onSuccess);

        loadRecentlyAddedStudents(principalId, notification, onSuccess);

        loadCachedStaffList(principalId, notification, onSuccess);

        loadGradesFromBackend(principalId, notification, onSuccess);

        loadSemestersFromBackend(principalId, notification, onSuccess);

        loadClassSummaryFees(principalId, notification, onSuccess);

        loadAllStaffList(principalId, notification, onSuccess);
    }

    private void checkCounter(int counter, Runnable onSuccess) {
        if (principalController != null && financeController == null && staffController == null) {
            int initialCounter = 10;
            if (counter == initialCounter) {
                onSuccess.run();
                this.counter = 0;
            }
        } else if (principalController == null && financeController != null && staffController == null) {
            int initialCounter = 5;
            if (counter == initialCounter) {
                onSuccess.run();
                this.counter = 0;
            }
        } else if (principalController == null && financeController == null && staffController != null) {
            int initialCounter = 4;
            if (counter == initialCounter) {
                onSuccess.run();
                this.counter = 0;
            }
        }
    }

    public void loadFinanceDashboardData(String financeId, StackPane notification, Runnable onSuccess) {
        loadTotalFeesPaid(financeId, notification, onSuccess);

        loadTotalSemesterFees(financeId, notification, onSuccess);

        loadGradesFromBackend(financeId, notification, onSuccess);

        loadSemestersFromBackend(financeId, notification, onSuccess);

        fetchRecentFeesTransactions(financeId, notification, onSuccess);
    }

    public void loadStaffDashboardData(String staffId, StackPane notification, Runnable onSuccess) {
        loadGradesFromBackend(staffId, notification, onSuccess);

        loadSemestersFromBackend(staffId, notification, onSuccess);

        loadGradeStudents(staffId, notification, onSuccess);

        loadGradeNameAndSize(staffId, notification, onSuccess);
    }

    // ==================== PRINCIPAL METHODS ====================

    public void loadTotalStudents(String staffId, StackPane notification, Runnable onSuccess) {
        if (totalStudentsFromBackend != 0) {
            counter++;
            checkCounter(counter, onSuccess);
            return;
        }

        Task<Long> task = new Task<>() {
            @Override
            protected Long call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/total-students/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url)).GET().build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                return Long.parseLong(response.body());
            }
        };

        task.setOnSucceeded(event -> {
            totalStudentsFromBackend = task.getValue();
            counter ++;
            checkCounter(counter, onSuccess);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                showError(notification, "Cannot connect to server. Check your internet.");
            } else {
                showError(notification, "Unexpected error occurred.");
            }
        });

        new Thread(task).start();
    }

    public void loadTotalStaff(String staffId,  StackPane notification, Runnable onSuccess) {
        if (totalStaffFromBackend != 0) {
            counter++;
            checkCounter(counter, onSuccess);
            return;
        }

        Task<Long> task = new Task<>() {
            @Override
            protected Long call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/total-staffs/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() ==  200) {
                    return Long.parseLong(response.body());
                }

                System.out.println(response.body());
                return 0L;
            }
        };

        task.setOnSucceeded(event -> {
            totalStaffFromBackend = task.getValue();
            counter ++;
            checkCounter(counter, onSuccess);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                showError(notification, "Cannot connect to server. Check your internet.");
            } else {
                showError(notification, "Unexpected error occurred. Please try again later.");
            }
            principalController.totalStaffLabel.setText("0");
        });

        new Thread(task).start();
    }

    public void loadTotalSemesterFees(String staffId, StackPane notification, Runnable onSuccess) {
        if (totalSemesterFeesFromBackend != 0) {
            counter ++;
            checkCounter(counter, onSuccess);
            return;
        }

        Task<Double> task = new Task<>() {
            @Override
            protected Double call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/total-fees/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                return Double.parseDouble(response.body());
            }
        };

        task.setOnSucceeded(event -> {
            totalSemesterFeesFromBackend = task.getValue();
            counter ++;
            checkCounter(counter, onSuccess);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                showError(notification, "Cannot connect to server. Check your internet.");
            } else {
                showError(notification, "Unexpected error occurred.");
            }
        });

        new Thread(task).start();
    }

    public void loadTotalFeesPaid(String staffId, StackPane notification, Runnable onSuccess) {
        if (feesPaidFromBackend != 0) {
            counter ++;
            checkCounter(counter, onSuccess);
            return;
        }

        Task<Double> task = new Task<>() {
            @Override
            protected Double call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/fees-paid/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                return Double.parseDouble(response.body());
            }
        };

        task.setOnSucceeded(event -> {
            feesPaidFromBackend = task.getValue();
            counter ++;
            checkCounter(counter, onSuccess);
        });

        task.setOnFailed(event -> {
            principalController.feesPaidLabel.setText("0");
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                showError(notification, "Cannot connect to server. Check your internet connection.");
            } else {
                showError(notification, "Unexpected error occurred. Please try again later.");
            }
        });

        new Thread(task).start();
    }

    public void loadRecentlyAddedStudents(String staffId, StackPane notification, Runnable onSuccess) {

        List<RecentStudentView> cachedStudents = cacheManager.getCachedStudents();
        if (cachedStudents != null && !cachedStudents.isEmpty()) {
            if (cacheManager.isCacheValid()) {
                principalController.recentStudentsTable.getItems().setAll(cachedStudents);
                counter ++;
                checkCounter(counter, onSuccess);
                return;
            }
        }

        Type studentListType = new TypeToken<List<RecentStudentView>>() {}.getType();

        Task<List<RecentStudentView>> task = new Task<>() {
            @Override
            protected List<RecentStudentView> call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/recently-added-students/" + staffId;
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .header("Accept", "application/json")
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), studentListType);
                }

                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<RecentStudentView> students = task.getValue();

            if (students != null && !students.isEmpty()) {
                cacheManager.setCachedStudents(students);
            }
            counter ++;
            checkCounter(counter, onSuccess);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                showError(notification, "Cannot connect to server. Check your internet.");
            } else {
                showError(notification, "Unexpected error occurred.");
            }
        });

        new Thread(task).start();
    }

    public void loadCachedStaffList(String staffId, StackPane notification, Runnable onSuccess) {
        List<StaffCaching> staffCachings = staffCacheManager.getCachedStaffList();
        if (staffCachings != null && !staffCachings.isEmpty()) {
            if (staffCacheManager.isCacheValid()) {
                counter ++;
                checkCounter(counter, onSuccess);
                return;
            }
        }

        Type staffListType = new TypeToken<List<StaffCaching>>() {}.getType();

        Task<List<StaffCaching>> task = new Task<>() {
            @Override
            protected List<StaffCaching> call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/load-staffs-info/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .header("Accept", "application/json")
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), staffListType);
                }

                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<StaffCaching> staffs = task.getValue();
            if (staffs != null) {
                staffCacheManager.setStaffList(staffs);
            }
            counter ++;
            checkCounter(counter, onSuccess);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                showError(notification, "Cannot connect to server. Check your internet.");
            } else {
                showError(notification, "Unexpected error occurred.");
            }
        });

        new Thread(task).start();
    }

    public void loadGradesFromBackend(String staffId, StackPane notification, Runnable onSuccess) {

        List<GradeCaching> cachedGrades = gradeCacheManager.getGradeCache();
        if (cachedGrades != null && !cachedGrades.isEmpty() && gradeCacheManager.isCacheValid()) {
            counter++;
            System.out.println("1 " + counter);
            checkCounter(counter, onSuccess);
            return;
        }


        Type classListType = new TypeToken<List<GradeCaching>>() {}.getType();

        Task<List<GradeCaching>> task = new Task<>() {
            @Override
            protected List<GradeCaching> call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/load-levels/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    System.out.println(response.body());
                    return gson.fromJson(response.body(), classListType);
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<GradeCaching> gradesList = task.getValue();

            if (gradesList != null && !gradesList.isEmpty()) {
                gradeCacheManager.setGradeCache(gradesList);
            }
            counter ++;
            checkCounter(counter, onSuccess);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                showError(notification, "Cannot connect to server. Check your internet.");
            } else {
                showError(notification, "Unexpected error occurred.");
            }
        });

        new Thread(task).start();
    }

    public void loadSemestersFromBackend(String staffId, StackPane notification, Runnable onSuccess) {

        List<SemesterCaching> semesterCaching = semesterCachingManager.getCachedSemesters();
        if (semesterCaching != null && !semesterCaching.isEmpty()) {
            if (semesterCachingManager.isSemesterCacheValid()) {
                counter++;
                System.out.println("2 " + counter);
                checkCounter(counter, onSuccess);
                return;
            }
        }

        Type semesterListType = new TypeToken<ArrayList<SemesterCaching>>() {}.getType();

        Task<List<SemesterCaching>> task = new Task<>() {
            @Override
            protected List<SemesterCaching> call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/load-semesters/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), semesterListType);
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<SemesterCaching> semesterCachingList = task.getValue();
            if (semesterCachingList != null && !semesterCachingList.isEmpty()) {
                semesterCachingManager.setCachedSemesters(semesterCachingList);
            }
            counter ++;
            checkCounter(counter, onSuccess);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                showError(notification, "Cannot connect to server. Check your internet.");
            } else {
                showError(notification, "Unexpected error occurred.");
            }
        });

        new Thread(task).start();
    }

    public void loadClassSummaryFees(String staffId, StackPane notification, Runnable onSuccess) {

        List<ClassFeesSummary> cachedClassFees = feesCacheManager.getClassFeesSummaryCache();
        if (cachedClassFees != null && !cachedClassFees.isEmpty()) {
            if (feesCacheManager.isCachedClassFeesValid()) {
                counter++;
                checkCounter(counter, onSuccess);
                return;
            }
        }

        Type classFeesList = new TypeToken<ArrayList<ClassFeesSummary>>() {}.getType();

        Task<List<ClassFeesSummary>> task = new Task<>() {
            @Override
            protected List<ClassFeesSummary> call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/class-summary-fees/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), classFeesList);
                } else {
                    throw new Exception("Failed to fetch class-summary-fees data.");
                }
            }
        };

        task.setOnSucceeded(event -> {
            List<ClassFeesSummary> classFeesSummaryList = task.getValue();
            if (classFeesSummaryList != null && !classFeesSummaryList.isEmpty()) {
                feesCacheManager.setClassFeesSummaryCache(classFeesSummaryList);
            }
            counter ++;
            checkCounter(counter, onSuccess);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                showError(notification, "Cannot connect to server. Check your internet.");
            } else {
                showError(notification, "Unexpected error occurred.");
            }
        });

        new Thread(task).start();
    }

    // ==================== STAFF CONTROLLER METHODS ====================

    public void loadGradeNameAndSize(String staffId, StackPane notification, Runnable onSuccess) {
        if (staffController.getGradeName() != null && staffController.getGradeNumber() != null
                && staffController.getGradeSize() != null) {
            counter++;
            System.out.println("4 " + counter);
            checkCounter(counter, onSuccess);
            return;
        }

        Task<GradeInfoResponse> task = new Task<>() {
            @Override
            protected GradeInfoResponse call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/load-grade-and-size/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .header("Accept", "application/json")
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), GradeInfoResponse.class);
                }

                return null;
            }
        };

        task.setOnSucceeded(event -> {
            GradeInfoResponse gradeInfoResponse = task.getValue();
            if (gradeInfoResponse != null) {
                staffController.setGradeNumber(gradeInfoResponse.getGradeNumber());
                staffController.setGradeName(gradeInfoResponse.getGradeName());
                staffController.setGradeSize(gradeInfoResponse.getGradeSize());
            }

            counter ++;
            checkCounter(counter, onSuccess);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                showError(notification, "Cannot connect to server. Check your internet.");
            } else {
                showError(notification, "Unexpected error occurred.");
            }
        });

        new Thread(task).start();
    }

    public void loadGradeStudents(String staffId, StackPane notification, Runnable onSuccess) {

        List<GradeStudentsDisplayDTO> gradeStudentsLists = gradeStudentsCacheManager.getCachedGradeStudents();
        if (gradeStudentsLists != null && !gradeStudentsLists.isEmpty() && gradeStudentsCacheManager.isCacheValid()) {

            Platform.runLater(() -> staffController.studentsTable.getItems().setAll(gradeStudentsLists));

            counter++;
            System.out.println("3 " + counter);
            checkCounter(counter, onSuccess);
            return;
        }

        Type studentList = new TypeToken<ArrayList<GradeStudentsDisplayDTO>>() {}.getType();

        Task<List<GradeStudentsDisplayDTO>> task = new Task<>() {
            @Override
            protected List<GradeStudentsDisplayDTO> call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/load-grade-students/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    System.out.println(response.body());
                    return gson.fromJson(response.body(), studentList);
                }

                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<GradeStudentsDisplayDTO> gradeStudentsDisplayDTOS = task.getValue();
            if (gradeStudentsDisplayDTOS != null) {
                gradeStudentsCacheManager.setCachedGradeStudents(gradeStudentsDisplayDTOS);
            }
            counter ++;
            checkCounter(counter, onSuccess);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                showError(notification, "Cannot connect to server. Check your internet.");
            } else {
                showError(notification, "Unexpected error occurred.");
            }
        });

        new Thread(task).start();
    }

    public void enrollNewStudent(String firstName, String lastName, String levelId, String gender,
                                 LocalDate dateOfBirth, String hometown, String parentName, String guardianContact,
                                 String staffId, Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/add-new-student";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("firstName", firstName);
                requestBody.addProperty("lastName", lastName);
                requestBody.addProperty("levelId", levelId);
                requestBody.addProperty("gender", gender);
                requestBody.addProperty("dateOfBirth", dateOfBirth.toString());
                requestBody.addProperty("hometown", hometown);
                requestBody.addProperty("parentName", parentName);
                requestBody.addProperty("guardianContact", guardianContact);
                requestBody.addProperty("staffId", staffId);
                System.out.println(gson.toJson(requestBody));

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                JsonObject responseBody = gson.fromJson(response.body(), JsonObject.class);

                if (response.statusCode() == 200) {
                    if (responseBody.get("status").getAsString().equals("success")) {
                        String studentId = responseBody.get("studentId").getAsString();
                        System.out.println(studentId);
                        return studentId;
                    } else {
                        return responseBody.get("message").getAsString();
                    }
                } else {
                    return response.body();
                }
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response != null) {
                studentId = response;
                onSuccess.run();
                return;
            }
            onFailure.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
            } else {
                onNetworkIssue.run();
            }
        });

        new Thread(task).start();
    }

    public void loadSubjectStudents(String subjectId,
                                    Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Task<SubjectScores> task = new Task<>() {
            @Override
            protected SubjectScores call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/load-subject-students/" + subjectId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Received: " + response.body());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), SubjectScores.class);
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            staffController.subjectScores = task.getValue();
            if (staffController.subjectScores == null) {
                onFailure.run();
                return;
            }
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
            } else {
                onNetworkIssue.run();
            }
        });

        new Thread(task).start();
    }

    public void saveStudentScores(String staffId, String studentId, String subjectId, String classScore, String examScore,
                                  String semesterId,
                                  Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/save-student-scores" +
                        "?staffId=" + staffId +
                        "&studentId=" + studentId +
                        "&subjectId=" + subjectId +
                        "&semesterId=" + semesterId +
                        "&classScore=" + classScore +
                        "&examScore=" + examScore;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return "success";
                }
                return response.body();
            }
        };

        task.setOnSucceeded(event -> {
            String status = task.getValue();
            if ("success".equals(status)) {  // Safer null check
                onSuccess.run(); // Run on JavaFX thread
                return;
            }

            onFailure.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
            } else {
                onNetworkIssue.run();
            }
        });

        new Thread(task).start();
    }

    public void addNewGrade(String gradeName, String instructorId,
                             Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/add-new-grade" +
                        "?gradeName=" + gradeName +
                        "&instructorId=" + instructorId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
                return jsonResponse.get("status").getAsString();
            }
        };

        task.setOnSucceeded(event -> {
            String status = task.getValue();
            if ("success".equals(status)) {
                onSuccess.run();
                gradeCacheManager.clearCache();
            } else {
                onFailure.run();
            }
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
            } else {
                onNetworkIssue.run();
            }
        });

        new Thread(task).start();
    }

    public void recoverPassword(String staffId, String newPassword,
                                Runnable onSuccess,
                                Runnable onFailure, Runnable onNetworkIssue) {
        Task<PasswordRecoveryDTO> task = new Task<>() {
            @Override
            protected PasswordRecoveryDTO call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/reset-staff-password/" +
                        staffId + "/" + newPassword;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), PasswordRecoveryDTO.class);
                }

                return gson.fromJson(response.body(), PasswordRecoveryDTO.class);
            }
        };

        task.setOnSucceeded(event -> {
            principalController.passwordRecoveryDTO = task.getValue();
            if (principalController.passwordRecoveryDTO.getStatus().equals("failed")
                    || principalController.passwordRecoveryDTO == null ) {
                onFailure.run();
                return;
            }
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
            } else {
                onNetworkIssue.run();
            }
        });

        new Thread(task).start();
    }

    public void searchFeesReport(String studentId, String semesterId, String gradeId,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<StudentFeesPaymentDisplay> task = new Task<>() {
            @Override
            protected StudentFeesPaymentDisplay call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/search-fees-report" +
                        "?studentId=" + studentId +
                        "&semesterId=" + semesterId +
                        "&gradeId=" + gradeId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), StudentFeesPaymentDisplay.class);
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            report = task.getValue();
            if (report != null) {
                onSuccess.run();
            }
            onFailure.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
            } else {
                onNetworkIssue.run();
            }
        });

        new Thread(task).start();
    }

    public void getFeesFilterData(String ownUrl,
                                  Runnable onSuccess, Runnable onFailure) {
        if (principalController == null) return;

        Type fullYPaidList = new TypeToken<List<FeesSummaryTable>>() {}.getType();

        Task<List<FeesSummaryTable>> task = new Task<>() {
            @Override
            protected List<FeesSummaryTable> call() throws Exception {
                String url = BASE_URL + API_BASE_URL + ownUrl;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), fullYPaidList);
                } else {
                    throw new Exception("Failed to fetch not-paid student!");
                }
            }
        };

        task.setOnSucceeded(event -> {
            principalController.feesSummaryTable = task.getValue();
            if (principalController.feesSummaryTable != null) {
                onSuccess.run();
            } else {
                onFailure.run();
            }
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onFailure.run();
            } else {
                onFailure.run();
            }
        });

        new Thread(task).start();
    }

    public void loadAllStaffList(String staffId, StackPane notification, Runnable onSuccess) {

        List<RecentStaffView> staffList = staffListCacheManager.getCachedStaffList();
        if (staffList != null && !staffList.isEmpty()) {
            if (staffListCacheManager.isCacheValid()) {
                counter ++;
                checkCounter(counter, onSuccess);
                return;
            }
        }

        Type staffListList = new TypeToken<ArrayList<RecentStaffView>>() {}.getType();

        Task<List<RecentStaffView>> task = new Task<>() {
            @Override
            protected List<RecentStaffView> call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/staff-list/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    System.out.println(response.body());
                    return gson.fromJson(response.body(), staffListList);
                } else {
                    throw new Exception("Failed to fetch staff-list data.");
                }
            }
        };

        task.setOnSucceeded(event -> {
            List<RecentStaffView> staffViewList = task.getValue();
            if (staffViewList != null && !staffViewList.isEmpty()) {
                staffListCacheManager.setStaffList(staffViewList);
            }
            counter ++;
            checkCounter(counter, onSuccess);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                showError(notification, "Cannot connect to server. Check your internet.");
            } else {
                showError(notification, "Unexpected error occurred.");
            }
        });

        new Thread(task).start();
    }

    public void refreshAllStaffList(Runnable onSuccess, Runnable onFailure) {

        Type staffListList = new TypeToken<ArrayList<RecentStaffView>>() {}.getType();

        Task<List<RecentStaffView>> task = new Task<>() {
            @Override
            protected List<RecentStaffView> call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/staff-list/" + principalController.currentStaffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), staffListList);
                } else {
                    throw new Exception("Failed to fetch staff-list data.");
                }
            }
        };

        task.setOnSucceeded(event -> {
            principalController.refreshedStaffViewList = task.getValue();
            if (principalController.refreshedStaffViewList != null) {
                onSuccess.run();
            } else  {
                onFailure.run();
            }
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onFailure.run();
            } else {
                onFailure.run();
            }
        });

        new Thread(task).start();
    }

    public void searchStaffData(String staffId,
                                Runnable onSuccess, Runnable onFailure) {
        if (principalController == null) return;

        Task<FindStaffResponse> task = new Task<>() {
            @Override
            protected FindStaffResponse call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/find-staff-by-id/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                return gson.fromJson(response.body(), FindStaffResponse.class);
            }
        };

        task.setOnSucceeded(event -> {
            principalController.findStaffResponse = task.getValue();
            if (principalController.findStaffResponse != null) {
                onSuccess.run();
            } else {
                onFailure.run();
            }
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onFailure.run();
            } else {
                onFailure.run();
            }
        });

        new Thread(task).start();
    }

    public void searchStudentData(String studentId, Runnable onSuccess, Runnable onFailure) {

        Task<UpdateStudentPersonalData> task = new Task<>() {
            @Override
            protected UpdateStudentPersonalData call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/find-student-by-id/" + studentId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    System.out.println(response.body());
                    return gson.fromJson(response.body(), UpdateStudentPersonalData.class);
                } else {
                    throw new Exception("Failed to fetch student personal data.");
                }
            }
        };

        task.setOnSucceeded(event -> {
            principalController.updateStudentPersonalData = task.getValue();
            if(principalController.updateStudentPersonalData != null) {
                onSuccess.run();
            } else {
                onFailure.run();
            }
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onFailure.run();
            } else {
                onFailure.run();
            }
        });

        new Thread(task).start();
    }

    public void showUpdateStudentDataDialog(StackPane notification) {
        if (principalController == null || principalController.updateStudentPersonalData == null) return;

        Dialog<UpdateStudentPersonalData> dialog = new Dialog<>();
        dialog.setTitle("Edit Personal Data");
        dialog.setHeaderText("Update personal and guardian contact details for " +
                principalController.updateStudentPersonalData.getFirstName());

        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(25, 25, 25, 25));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(new Label(principalController.updateStudentPersonalData.getFirstName()), 1, 0);

        grid.add(new Label("Student ID:"), 0, 1);
        grid.add(new Label(principalController.updateStudentPersonalData.getStudentId()), 1, 1);

        grid.add(new Label("Class/Grade:"), 0, 2);
        grid.add(new Label(principalController.updateStudentPersonalData.getGradeName()), 1, 2);

        grid.add(new Separator(), 0, 3, 2, 1);

        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female");
        genderComboBox.setValue(principalController.updateStudentPersonalData.getGender());
        grid.add(new Label("Gender (Editable):"), 0, 4);
        grid.add(genderComboBox, 1, 4);

        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("ACTIVE", "INACTIVE", "COMPLETED");
        statusComboBox.setValue(principalController.updateStudentPersonalData.getStatus());
        grid.add(new Label("Status (Editable):"), 0, 5);
        grid.add(genderComboBox, 1, 5);

        DatePicker dobPicker = new DatePicker(LocalDate.parse(principalController.updateStudentPersonalData.getDateOfBirth()));
        grid.add(new Label("Date of Birth (Editable):"), 0, 6);
        grid.add(dobPicker, 1, 6);

        TextField guardianNameField = new TextField(principalController.updateStudentPersonalData.getParentName());
        grid.add(new Label("Guardian Name (Editable):"), 0, 7);
        grid.add(guardianNameField, 1, 7);

        TextField guardianPhoneField = new TextField(principalController.updateStudentPersonalData.getParentPhoneNumber());
        grid.add(new Label("Contact (Editable):"), 0, 8);
        grid.add(guardianPhoneField, 1, 8);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(genderComboBox::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new UpdateStudentPersonalData(
                        principalController.updateStudentPersonalData.getStudentId(),
                        principalController.updateStudentPersonalData.getFirstName(),
                        principalController.updateStudentPersonalData.getLastName(),
                        genderComboBox.getValue(),
                        dobPicker.getValue().toString(),
                        guardianNameField.getText(),
                        guardianPhoneField.getText(),
                        principalController.updateStudentPersonalData.getGradeName(),
                        principalController.updateStudentPersonalData.getStatus()
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(update -> {
            updateStudentPersonalData(update, notification);
        });
    }

    private void updateStudentPersonalData(UpdateStudentPersonalData updateStudentPersonalData,
                                           StackPane notification) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/update-student-data";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("studentId", updateStudentPersonalData.getStudentId());
                requestBody.addProperty("gender", updateStudentPersonalData.getGender());
                requestBody.addProperty("dateOfBirth", updateStudentPersonalData.getDateOfBirth().toString());
                requestBody.addProperty("guardianName", updateStudentPersonalData.getParentName());
                requestBody.addProperty("guardianContact", updateStudentPersonalData.getParentPhoneNumber());
                requestBody.addProperty("status", updateStudentPersonalData.getStatus());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    JsonObject responseBody = gson.fromJson(response.body(), JsonObject.class);
                    return responseBody.get("status").getAsString();
                } else {
                    return "error";
                }
            }
        };

        task.setOnSucceeded(event -> {
            String status = task.getValue();
            if ("success".equals(status)) {
                showSuccess(notification, "Student data updated successfully.");
            } else {
                showError(notification, "Error: " + status);
            }
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                showError(notification, "Cannot connect to server. Check your internet.");
            } else {
                showError(notification, "Unexpected error occurred.");
            }
        });

        new Thread(task).start();
    }

    public void SearchStudentResults(String studentId, String semesterId, String classId,
                                     Runnable onSuccess, Runnable onFailure) {
        if (principalController == null) return;

        Task<StudentResults> task = new Task<>() {
            @Override
            protected StudentResults call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/search-student-results" +
                        "?studentId=" + studentId +
                        "&semesterId=" + semesterId +
                        "&classId=" + classId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.body() == null || "null".equals(response.body())) {
                    return null;
                }

                return gson.fromJson(response.body(), StudentResults.class);
            }
        };

        task.setOnSucceeded(event -> {
            studentResults = task.getValue();
            if (studentResults != null) {
                onSuccess.run();
            } else {
                onFailure.run();
            }
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onFailure.run();
            } else {
                onFailure.run();
            }
        });

        new Thread(task).start();
    }

    public void addNewFees(String gradeId, String semesterId, String feesAmount,
                           Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/add-new-fees" +
                        "?gradeId=" + gradeId +
                        "&semesterId=" + semesterId +
                        "&feesAmount=" + feesAmount;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }
                return response.body();
            }
        };

        task.setOnSucceeded(event -> {
            String result = task.getValue();
            if (result.equals("success")) {
                onSuccess.run();
                return;
            }
            onFailure.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
            } else {
                onNetworkIssue.run();
            }
        });

        new Thread(task).start();
    }

    public void addNewSemester(String semesterName, LocalDate startDate,
                                LocalDate endDate, String academicYear, String currentStaffId,
                                Runnable onSuccess,  Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/add-semester";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("semesterName", semesterName);
                requestBody.addProperty("startDate", startDate.toString());
                requestBody.addProperty("endDate", endDate.toString());
                requestBody.addProperty("academicYear", academicYear);
                requestBody.addProperty("staffId", currentStaffId);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
                return jsonResponse.get("status").getAsString();
            }
        };

        task.setOnSucceeded(event -> {
            String status = task.getValue();
            if ("success".equals(status)) {
                onSuccess.run();
                semesterCachingManager.clearCachedSemesters();
            } else {
                onFailure.run();
            }
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
            } else {
                onNetworkIssue.run();
            }
        });

        new Thread(task).start();
    }

    public void addNewSubject(AssignSubject assignment,
                               Runnable onSuccess,  Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/add-subject";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("subjectName", assignment.getSubjectName());
                requestBody.addProperty("gradeId", assignment.getGradeId());
                System.out.println(assignment.getGradeId());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
                return jsonResponse.get("status").getAsString();
            }
        };

        task.setOnSucceeded(event -> {
            String status = task.getValue();
            if ("success".equals(status)) {
                onSuccess.run();
            } else {
                onFailure.run();
            }
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
            } else {
                onNetworkIssue.run();
            }
        });

        new Thread(task).start();
    }

    public void addNewFeesPayment(String studentId, String semesterId, String gradeId,
                                  String amountPaid, String payerName, String payerPhone,
                                  Runnable onSuccess,  Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/add-fees-payment";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("studentId", studentId);
                requestBody.addProperty("semesterId", semesterId);
                requestBody.addProperty("gradeId", gradeId);
                requestBody.addProperty("amountPaid", amountPaid);
                requestBody.addProperty("payerName", payerName);
                requestBody.addProperty("payerPhone", payerPhone);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
                if (response.statusCode() == 200) {
                    jsonResponse.get("status").getAsString();
                    if (jsonResponse.get("status").getAsString().equals("success")) {
                        return jsonResponse.get("message").getAsString();
                    } else {
                        return jsonResponse.get("status").getAsString();
                    }
                } else {
                    return jsonResponse.get("message").getAsString();
                }
            }
        };

        task.setOnSucceeded(event -> {
            String status = task.getValue();
            if (status.equals("failed")) {
                onFailure.run();
                return;
            }

            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
            } else {
                onNetworkIssue.run();
            }
        });

        new Thread(task).start();
    }

    public void fetchAndSaveStudentList(String levelId, String levelName, Runnable onSuccess, Runnable onFailure) {
        Type listType = new TypeToken<List<StudentListPrint>>() {}.getType();

        Task<List<StudentListPrint>> task = new Task<>() {
            @Override
            protected List<StudentListPrint> call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/students-by-level/" + levelId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .header("Accept", "application/json")
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), listType);
                } else {
                    throw new Exception("Failed to fetch students");
                }
            }
        };

        task.setOnSucceeded(event -> {
            List<StudentListPrint> students = task.getValue();
            if (students == null || students.isEmpty()) {
                onFailure.run();
                return;
            }

            // Show save dialog
            Platform.runLater(() -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Student List");
                fileChooser.setInitialFileName(levelName + "_Student_List.csv");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                        new FileChooser.ExtensionFilter("Text Files", "*.txt")
                );

                Stage stage = (Stage) principalController.printStudentDataBtn.getScene().getWindow();
                File file = fileChooser.showSaveDialog(stage);

                if (file != null) {
                    try {
                        saveStudentListToFile(students, file, levelName);
                        onSuccess.run();
                    } catch (Exception e) {
                        onFailure.run();
                    }
                } else {
                    onFailure.run();
                }
            });
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onFailure.run();
            } else {
                onFailure.run();
            }
        });

        new Thread(task).start();
    }

    public void refreshRecentFeesTransactions(String staffId,
                                              Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        feesCacheManager.clearRecentPaymentCache();

        Type listType = new TypeToken<List<RecentPaymentTable>>() {}.getType();

        Task<List<RecentPaymentTable>> task = new Task<>() {
            @Override
            protected List<RecentPaymentTable> call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/recent-fees-transactions/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .header("Accept", "application/json")
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), listType);
                } else {
                    System.out.println(response.body());
                    return null;
                }

            }
        };

        task.setOnSucceeded(event -> {
            List<RecentPaymentTable> recentPayments = task.getValue();
            if (recentPayments == null || recentPayments.isEmpty()) {
                onFailure.run();
                return;
            }

            feesCacheManager.setRecentPaymentCache(recentPayments);
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
            } else {
                onNetworkIssue.run();
            }
        });

        new Thread(task).start();
    }

    public void fetchRecentFeesTransactions(String staffId, StackPane notification, Runnable onSuccess) {

            List<RecentPaymentTable> recentPaymentsTable = feesCacheManager.getRecentPaymentCache();
            if (recentPaymentsTable != null && !recentPaymentsTable.isEmpty()) {
                if (feesCacheManager.isCachedRecentPaymentValid()) {
                    counter++;
                    checkCounter(counter, onSuccess);
                    return;
                }
            }

        Type listType = new TypeToken<List<RecentPaymentTable>>() {}.getType();

        Task<List<RecentPaymentTable>> task = new Task<>() {
            @Override
            protected List<RecentPaymentTable> call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/recent-fees-transactions/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .header("Accept", "application/json")
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), listType);
                }

                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<RecentPaymentTable> recentPayments = task.getValue();
            if (recentPayments != null || !recentPayments.isEmpty()) {
                feesCacheManager.setRecentPaymentCache(recentPayments);
            }

            counter ++;
            checkCounter(counter, onSuccess);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                showError(notification, "Cannot connect to server. Check your internet.");
            } else {
                showError(notification, "Unexpected error occurred. Please try again later.");
            }
        });

        new Thread(task).start();
    }

    public void saveGradingCriteria(GradingCriteria gradingCriteria, String staffId,
                                    Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/set-grades";

                JsonObject responseBody = new JsonObject();
                responseBody.addProperty("staffId", staffId);
                responseBody.addProperty("lowerRange", gradingCriteria.getLowerRange());
                responseBody.addProperty("upperRange", gradingCriteria.getUpperRange());
                responseBody.addProperty("grade", gradingCriteria.getGrade());
                responseBody.addProperty("gradeDescription", gradingCriteria.getGradeDescription());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .POST(HttpRequest.BodyPublishers.ofString(responseBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return response.body();
                }

                return responseBody.toString();
            }
        };

        task.setOnSucceeded(event -> {
            String responseBody = task.getValue();
            if (responseBody != null) {
                onSuccess.run();
                return;
            }

            onFailure.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
            } else {
                onNetworkIssue.run();
            }
        });

        new Thread(task).start();
    }

    public void downloadStudentReport(String staffId, String semesterId, String promotionGradeId,
                                      Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<byte[]> task = new Task<>() {
            @Override
            protected byte[] call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/generate-class-report/" +
                        staffId + "/" + semesterId + "/" + promotionGradeId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<byte[]> response = httpClient
                        .send(request, HttpResponse.BodyHandlers.ofByteArray());

                if (response.statusCode() == 200) {
                    return response.body();
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            byte[] pdfBytes = task.getValue();
            if (pdfBytes == null) {
                onFailure.run();
                return;
            }

            Platform.runLater(() -> {
                try {
                    FileChooser chooser = new FileChooser();
                    chooser.setTitle("Save Student Report");
                    chooser.getExtensionFilters().add(
                            new FileChooser.ExtensionFilter("PDF", "*.pdf")
                    );

                    chooser.setInitialFileName("Students Report.pdf");

                    File file = chooser.showSaveDialog(null);

                    if (file != null) {
                        Files.write(file.toPath(), pdfBytes);
                        onSuccess.run();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure.run();
                }
            });
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
            } else {
                onNetworkIssue.run();
            }
        });

        new Thread(task).start();
    }

    public void loadClassResults(String staffId, String semesterId,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Type listType = new TypeToken<List<GenerateStudentReport>>() {}.getType();
        Task<List<GenerateStudentReport>> task = new Task<>() {
            @Override
            protected List<GenerateStudentReport> call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/view-class-results/"
                        + staffId + "/" + semesterId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), listType);
                }

                return null;
            }
        };

        task.setOnSucceeded(event -> {
            staffController.generateStudentReports = task.getValue();
            if (staffController.generateStudentReports != null) {
                onSuccess.run();
                return;
            }

            onFailure.run();

        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
            } else {
                onNetworkIssue.run();
            }
        });

        new Thread(task).start();
    }

    public void loadClassResultsPrincipal(String levelId, String semesterId,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Type listType = new TypeToken<List<GenerateStudentReport>>() {}.getType();

        Task<List<GenerateStudentReport>> task = new Task<>() {
            @Override
            protected List<GenerateStudentReport> call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/principal-view-class-results/"
                        + levelId + "/" + semesterId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), listType);
                }

                return null;
            }
        };

        task.setOnSucceeded(event -> {
            principalController.generateStudentReport = task.getValue();
            if (principalController.generateStudentReport != null) {
                onSuccess.run();
                return;
            }

            onFailure.run();

        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
            } else {
                onNetworkIssue.run();
            }
        });

        new Thread(task).start();
    }

    public void movePassedStudents(String staffId, String semesterId, String passTotalScore, String nextLevelId,
                                              Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/generate-and-move-students/" +
                        "staffId=" + staffId +
                        "&semesterId=" + semesterId +
                        "&passTotalScore=" + passTotalScore +
                        "&nextLevelId=" + nextLevelId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return response.body();
                }

                return response.body();
            }
        };

        task.setOnSucceeded(event -> {
            staffController.passedResponse = task.getValue();
            if (staffController.passedResponse == null) {
                onFailure.run();
                return;
            }
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
            } else {
                onNetworkIssue.run();
            }
        });

        new Thread(task).start();
    }

    public void loadStudentsForAttendance(String levelId, String date,
                                          Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        if (staffController == null) return;

        Type listType = new TypeToken<List<StudentAttendance>>() {}.getType();

        Task<List<StudentAttendance>> task = new Task<>() {
            @Override
            protected List<StudentAttendance> call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/load-students-for-attendance/" +
                        levelId + "/" + date;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), listType);
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            staffController.studentAttendances = task.getValue();
            if (staffController.studentAttendances == null) {
                onFailure.run();
                return;
            }

            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
            } else {
                onNetworkIssue.run();
            }
        });

        new Thread(task).start();
    }

    public void markStudentAttendance(String studentId, String staffId, String levelId, String status,
                                      Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/mark-attendance";
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("studentId", studentId);
                requestBody.addProperty("staffId", staffId);
                requestBody.addProperty("levelId", levelId);
                requestBody.addProperty("status", status);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                errorMessageContainer = response.body();
                System.out.println(errorMessageContainer);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response == null) {
                onFailure.run();
                return;
            }

            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
            } else {
                onNetworkIssue.run();
            }
        });

        new Thread(task).start();
    }

    public void updateAttendance(String studentId, String staffId, String levelId, String status, String dateMarked,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/update-attendance?" +
                        "studentId=" + studentId +
                        "&staffId=" + staffId +
                        "&levelId=" + levelId +
                        "&status=" + status +
                        "&dateMarked=" + dateMarked;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .PUT(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }
                errorMessageContainer = response.body();
                System.out.println(errorMessageContainer);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response == null) {
                onFailure.run();
                return;
            }
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
            } else {
                onNetworkIssue.run();
            }
        });

        new Thread(task).start();
    }

    public void printSubjectList(String levelId, String levelName,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Type listType = new TypeToken<List<PrintLevelSubjects>>() {}.getType();

        Task<List<PrintLevelSubjects>> task = new Task<List<PrintLevelSubjects>>() {
            @Override
            protected List<PrintLevelSubjects> call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/print-subjects/" + levelId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), listType);
                }

                throw new Exception("Unexpected response code: " + response.statusCode());
            }
        };

        task.setOnSucceeded(event -> {
            List<PrintLevelSubjects> response = task.getValue();
            if (response == null || response.isEmpty()) {
                onFailure.run();
                return;
            }

            Platform.runLater(() -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Subject List");
                fileChooser.setInitialFileName(levelName + "_Subject_List.csv");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                        new FileChooser.ExtensionFilter("Text Files", "*.txt")
                );

                Stage stage = (Stage) staffController.printSubjectDataBtn.getScene().getWindow();
                File file = fileChooser.showSaveDialog(stage);

                if (file != null) {
                    try {
                        saveSubjectListToFile(response, file, levelName);
                        onSuccess.run();
                    } catch (Exception e) {
                        onFailure.run();
                    }
                } else {
                    onFailure.run();
                }
            });
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onFailure.run();
            } else {
                onFailure.run();
            }
        });

        new Thread(task).start();
    }

    private void saveSubjectListToFile(List<PrintLevelSubjects> subjects, File file, String levelName) throws Exception {
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

    private void saveStudentListToFile(List<StudentListPrint> students, File file, String levelName) throws Exception {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Header
            writer.println("========================================");
            writer.println("         STUDENT LIST - " + levelName);
            writer.println("========================================");
            writer.println();
            writer.println(String.format("%-5s | %-20s | %-30s", "No.", "Student ID", "Full Name"));
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

    // ==================== UTILITY METHODS ====================

    public void showError(StackPane notification, String message) {
        Platform.runLater(() -> {
            NotificationUtil.showError(notification, message);
        });
    }

    public void showSuccess(StackPane notification, String message) {
        Platform.runLater(() -> {
            NotificationUtil.showSuccess(notification, message);
        });
    }

    public void processingButton(Button button) {
        button.setText("Processing...");
        button.setDisable(true);
    }

    public void notProcessingButton(Button button, String buttonText) {
        button.setText(buttonText);
        button.setDisable(false);
    }

    public void clearSession() {

        // Clear cached backend data
        feesCacheManager.clearClassFeesSummaryCache();
        feesCacheManager.clearRecentPaymentCache();

        gradeCacheManager.clearCache();
        gradeStudentsCacheManager.clearCachedGradeStudents();
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
        report = null;
    }

}