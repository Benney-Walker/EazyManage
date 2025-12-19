package com.bbquantum.eazymanage.Utitity;

import com.bbquantum.eazymanage.CacheClasses.*;
import com.bbquantum.eazymanage.Controller.*;
import com.bbquantum.eazymanage.DTO.*;
import com.bbquantum.eazymanage.DTO.Academics.*;
import com.bbquantum.eazymanage.DTO.Finance.ClassFeesSummary;
import com.bbquantum.eazymanage.DTO.Finance.FeesSummaryTable;
import com.bbquantum.eazymanage.DTO.Finance.StudentFeesPaymentDisplay;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UtilityClass {

    // Controllers - only one will be set at a time
    private final PrincipalController principalController;
    private final FinanceController financeController;
    private final StaffController staffController;
    private final AddStudentController addStudentController;
    private final AddResultsController addResultsController;

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

    // Cache Managers
    public final RecentStudentCacheManager cacheManager = RecentStudentCacheManager.getInstance();
    public final StaffCacheManager staffCacheManager = StaffCacheManager.getInstance();
    public final GradeCacheManager gradeCacheManager = GradeCacheManager.getInstance();
    public final FeesCacheManager feesCacheManager = FeesCacheManager.getInstance();
    public final SemesterCachingManager semesterCachingManager = SemesterCachingManager.getInstance();
    public final StaffListCacheManager staffListCacheManager = StaffListCacheManager.getInstance();

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
        this.addStudentController = null;
        this.addResultsController = null;
        loadPrincipalData();
    }

    // FINANCE CONSTRUCTOR
    public UtilityClass(FinanceController financeController) {
        this.financeController = financeController;
        this.principalController = null;
        this.staffController = null;
        this.addStudentController = null;
        this.addResultsController = null;
        loadFinanceData();
    }

    // STAFF CONSTRUCTOR
    public UtilityClass(StaffController staffController) {
        this.staffController = staffController;
        this.principalController = null;
        this.financeController = null;
        this.addStudentController = null;
        this.addResultsController = null;
        loadStaffData();
    }

    // ADD STUDENT CONSTRUCTOR
    public UtilityClass(AddStudentController addStudentController) {
        this.addStudentController = addStudentController;
        this.principalController = null;
        this.financeController = null;
        this.staffController = null;
        this.addResultsController = null;
    }

    // ADD RESULTS CONSTRUCTOR
    public UtilityClass(AddResultsController addResultsController) {
        this.addResultsController = addResultsController;
        this.principalController = null;
        this.financeController = null;
        this.staffController = null;
        this.addStudentController = null;
    }

    // Helper to get current staff ID based on which controller is active
    private String getCurrentStaffId() {
        if (principalController != null) {
            return principalController.currentStaffId;
        }
        // Add other controller staff ID getters as needed
        return null;
    }

    private void loadPrincipalData() {
        loadTotalFeesPaid(principalController.currentStaffId);
        loadTotalSemesterFees(principalController.currentStaffId);
        loadTotalStaff();
        loadTotalStudents();
        loadRecentlyAddedStudents();
        loadCachedStaffList();
        loadGradesFromBackend(principalController.currentStaffId);
        loadSemestersFromBackend(principalController.currentStaffId);
        loadClassSummaryFees();
    }

    private void loadFinanceData() {
        loadTotalFeesPaid(financeController.accountantId);
        loadTotalSemesterFees(financeController.accountantId);
        loadGradesFromBackend(financeController.accountantId);
        loadSemestersFromBackend(financeController.accountantId);
    }

    private void loadStaffData() {
        loadGradesFromBackend(staffController.staffId);
        loadSemestersFromBackend(staffController.staffId);
    }

    // ==================== PRINCIPAL METHODS ====================

    public void loadTotalStudents() {
        if (principalController == null) return;

        Task<Long> task = new Task<>() {
            @Override
            protected Long call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/total-students/" + principalController.currentStaffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url)).GET().build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                return Long.parseLong(response.body());
            }
        };

        task.setOnSucceeded(event -> totalStudentsFromBackend = task.getValue());

        task.setOnFailed(event -> {
            principalController.totalStudentsLabel.setText("0");
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
            System.err.println("Error: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    public void loadTotalStaff() {
        if (principalController == null) return;

        Task<Long> task = new Task<>() {
            @Override
            protected Long call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/total-staffs/" + principalController.currentStaffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                return Long.parseLong(response.body());
            }
        };

        task.setOnSucceeded(event -> totalStaffFromBackend = task.getValue());

        task.setOnFailed(event -> {

            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            }

            principalController.totalStaffLabel.setText("0");
            System.err.println("Error: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    public void loadTotalSemesterFees(String staffId) {

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

        task.setOnSucceeded(event ->
                totalSemesterFeesFromBackend = task.getValue());

        task.setOnFailed(event -> {
            principalController.totalSemesterFeesLabel.setText("0");
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
            System.err.println("Error: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    public void loadTotalFeesPaid(String staffId) {

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

        task.setOnSucceeded(event -> feesPaidFromBackend = task.getValue());

        task.setOnFailed(event -> {
            principalController.feesPaidLabel.setText("0");
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
            System.err.println("Error: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    public void loadRecentlyAddedStudents() {
        if (principalController == null) return;

        List<RecentStudentView> cachedStudents = cacheManager.getCachedStudents();
        if (cachedStudents != null && !cachedStudents.isEmpty()) {
            principalController.recentStudentsTable.getItems().setAll(cachedStudents);
            return;
        }

        Type studentListType = new TypeToken<List<RecentStudentView>>() {}.getType();

        Task<List<RecentStudentView>> task = new Task<>() {
            @Override
            protected List<RecentStudentView> call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/recently-added-students/" + principalController.currentStaffId;
                System.out.println("Recently Added Students: " + principalController.currentStaffId);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .header("Accept", "application/json")
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), studentListType);
                } else {
                    throw new Exception("Error: " + response.body());
                }
            }
        };

        task.setOnSucceeded(event -> {
            List<RecentStudentView> students = task.getValue();
            System.out.println("Recently Added Students: " + students);
            if (students != null && !students.isEmpty()) {
                cacheManager.setCachedStudents(students);
                principalController.recentStudentsTable.getItems().setAll(students);
            } else {
                principalController.recentStudentsTable.getItems().clear();
            }
        });

        task.setOnFailed(event -> {
            showError("Error: Could not load recently added students");
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
            System.out.println(task.getException().getMessage());
        });

        new Thread(task).start();
    }

    public void loadCachedStaffList() {
        if (staffCacheManager.isCacheValid()) {
            return;
        }

        Type staffListType = new TypeToken<List<StaffCaching>>() {}.getType();

        Task<List<StaffCaching>> task = new Task<>() {
            @Override
            protected List<StaffCaching> call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/load-staffs-info/" + principalController.currentStaffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .header("Accept", "application/json")
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), staffListType);
                } else {
                    throw new Exception("Error: " + response.body());
                }
            }
        };

        task.setOnSucceeded(event -> {
            List<StaffCaching> staffs = task.getValue();
            if (staffs != null) {
                staffCacheManager.setStaffList(staffs);
            }
        });

        task.setOnFailed(event -> {
            System.out.println(task.getException().getMessage());
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
        });

        new Thread(task).start();
    }

    public void loadGradesFromBackend(String staffId) {

        List<GradeCaching> cachedGrades = gradeCacheManager.getGradeCache();
        if (cachedGrades != null) {
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
                } else {
                    throw new Exception("Failed to fetch classes");
                }
            }
        };

        task.setOnSucceeded(event -> {
            List<GradeCaching> gradesList = task.getValue();
            System.out.println(gradesList);
            if (gradesList != null) {
                gradeCacheManager.setGradeCache(gradesList);
            }

        });

        task.setOnFailed(event -> {
            System.err.println("Error: " + task.getException().getMessage());
            showError("Error: Could not load grades from backend");
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
        });

        new Thread(task).start();
    }

    public void loadSemestersFromBackend(String staffId) {

        List<SemesterCaching> semesterCaching = semesterCachingManager.getCachedSemesters();
        if (semesterCaching != null) {
            return;
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
                } else {
                    throw new Exception("Failed: " + response.statusCode() + " " + response.body());
                }
            }
        };

        task.setOnSucceeded(event -> {
            List<SemesterCaching> semesterCachingList = task.getValue();
            if (semesterCachingList != null) {
                semesterCachingManager.setCachedSemesters(semesterCachingList);
            }
        });

        task.setOnFailed(event -> {
            System.err.println("Error: " + task.getException().getMessage());
            showError("Error: Could not load semesters from backend");
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
        });

        new Thread(task).start();
    }

    public void loadClassSummaryFees() {
        if (principalController == null) return;

        List<ClassFeesSummary> cachedClassfees = feesCacheManager.getClassFeesSummaryCache();
        if (cachedClassfees != null) {
            return;
        }

        Type classFeesList = new TypeToken<ArrayList<ClassFeesSummary>>() {}.getType();

        Task<List<ClassFeesSummary>> task = new Task<>() {
            @Override
            protected List<ClassFeesSummary> call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/class-summary-fees/" + principalController.currentStaffId;

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
            if (classFeesSummaryList != null) {
                feesCacheManager.setGradeCache(classFeesSummaryList);
            }
        });

        task.setOnFailed(event -> {
            System.err.println("Failed to fetch class-summary-fees data.");
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
        });

        new Thread(task).start();
    }

    // ==================== STAFF CONTROLLER METHODS ====================

    public void loadGradeNameAndSize(String staffId) {
        if (staffController == null) return;

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/load-grade-and-size/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    System.out.println(response.body());
                    return gson.fromJson(response.body(), String.class);
                } else {
                    throw new Exception("Failed: " + response.body());
                }
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response != null) {
                String[] responseSplit = response.split("_");
                if (responseSplit.length == 2) {
                    String className = responseSplit[0];
                    String classSize = responseSplit[1];
                    staffController.setGradeName(className);
                    staffController.setGradeSize(classSize);
            }

            } else {
                showError("Error loading grade name and size");
            }
        });

        task.setOnFailed(event -> {
            task.getException().printStackTrace();
            showError("Error loading grade name and size");
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
        });

        new Thread(task).start();
    }

    public void loadGradeStudents(String staffId) {
        if (staffController == null) return;

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
                } else {
                    throw new Exception("Could not fetch students");
                }
            }
        };

        task.setOnSucceeded(event -> {
            List<GradeStudentsDisplayDTO> gradeStudentsDisplayDTOS = task.getValue();
            // Use the new method that actually populates the table
            staffController.populateStudentsTable(gradeStudentsDisplayDTOS);
        });

        task.setOnFailed(event -> {
            task.getException().printStackTrace();
            showError(task.getException().getMessage());
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
        });

        new Thread(task).start();
    }

    public void enrollNewStudent(String firstName, String lastName, String levelId, String gender,
                                 LocalDate dateOfBirth, String hometown, String parentName, String guardianContact,
                                 String staffId, Runnable onSuccess, Runnable onFailure) {
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
                    return null;
                }
            }
        };

        task.setOnSucceeded(event -> {
            String studentId = task.getValue();
            if (addStudentController != null && studentId != null) {
                addStudentController.setStudentId(studentId);
                if (onSuccess != null) {
                    onSuccess.run();
                }
            }
        });

        task.setOnFailed(event -> {
            task.getException().printStackTrace();
            showError("Could not save student data");
            if (onFailure != null) {
                onFailure.run();
            }

            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
        });

        new Thread(task).start();
    }

    public void loadSubjectStudents(String subjectId) {
        if (addResultsController == null) return;

        Task<SubjectScores> task = new Task<>() {
            @Override
            protected SubjectScores call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/load-subject-students/" + subjectId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                return gson.fromJson(response.body(), SubjectScores.class);
            }
        };

        task.setOnSucceeded(event -> {
            SubjectScores subjectScores = task.getValue();
            if (subjectScores != null) {
                addResultsController.displaySubjectStudents(subjectScores);
            } else {
                addResultsController.noStudentsFound();
            }
        });

        task.setOnFailed(event -> {
            task.getException().printStackTrace();
            showError("Could not load students");
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
        });

        new Thread(task).start();
    }

    public void saveStudentScores(String studentId, String classScore, String examScore, String subjectId,
                                  Runnable onSuccess, Runnable onFailure) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/save-student-scores" +
                        "?studentId=" + studentId +
                        "&subjectId=" + subjectId +
                        "&classScore=" + classScore +
                        "&examScore=" + examScore;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                JsonObject responseBody = gson.fromJson(response.body(), JsonObject.class);
                return responseBody.get("status").getAsString();
            }
        };

        task.setOnSucceeded(event -> {
            String status = task.getValue();
            if ("success".equals(status) && onSuccess != null) {  // Safer null check
                Platform.runLater(onSuccess);  // Run on JavaFX thread
            } else if (onFailure != null) {
                Platform.runLater(onFailure);
            }
        });

        task.setOnFailed(event -> {
            if (onFailure != null) {
                Platform.runLater(onFailure);
            }
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
        });

        new Thread(task).start();
    }


    public void showAddClassDialog() {
        List<StaffCaching> staffList = staffCacheManager.getCachedList();
        if (staffList == null || staffList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot add class: Staff list is not available. Please try again later.");
            alert.showAndWait();
            return;
        }

        Dialog<GradeAssignment> dialog = new Dialog<>();
        dialog.setTitle("Add New Class and Assign Instructor");

        ButtonType addButtonType = new ButtonType("Add Class", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField classNameField = new TextField();
        classNameField.setPromptText("eg. Grade_1");

        ComboBox<StaffCaching> staffComboBox = new ComboBox<>();
        staffComboBox.setPromptText("Select Instructor");
        staffComboBox.getItems().addAll(staffList);

        grid.add(new Label("Class name:"), 0, 0);
        grid.add(classNameField, 1, 0);
        grid.add(new Label("Instructor id:"), 0, 1);
        grid.add(staffComboBox, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        classNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(newValue.trim().isEmpty() || staffComboBox.getSelectionModel().isEmpty());
        });

        staffComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(classNameField.getText().trim().isEmpty() || newValue == null);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                StaffCaching selectedStaff = staffComboBox.getSelectionModel().getSelectedItem();
                return new GradeAssignment(classNameField.getText().trim(), selectedStaff.getStaffId());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(assignment -> {
            addNewGrade(assignment.getGradeName(), assignment.getStaffId());
        });
    }

    private void addNewGrade(String gradeName, String instructorId) {
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
                showSuccess("Grade Added Successfully");
                gradeCacheManager.clearCache();
            } else {
                showError(status);
            }
        });

        task.setOnFailed(event -> {
            showError("Error: " + task.getException().getMessage());
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
        });

        new Thread(task).start();
    }

    public void recoverPassword(String staffId) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/recover-staff-password/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);

                String status = jsonResponse.get("status").getAsString();
                String message = jsonResponse.get("message").getAsString();

                if ("success".equals(status)) {
                    return message;
                } else {
                    throw new Exception("Failed to recover password");
                }
            }
        };

        task.setOnSucceeded(event -> {
            String password = task.getValue();
            Alert passwordAlert = new Alert(Alert.AlertType.INFORMATION);
            passwordAlert.setTitle("Password Recovery");
            passwordAlert.setHeaderText("Password for Staff ID: " + staffId);
            passwordAlert.setContentText("Password: " + password + "\n\nPlease save this password securely.");
            passwordAlert.showAndWait();
        });

        task.setOnFailed(event -> {
            showError("Failed to recover password");
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    public void searchFeesReport(String studentId, String semesterId, String gradeId,
                                 Runnable onSuccess, Runnable onFailure) {
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
            if (report != null && onSuccess != null) {
                onSuccess.run();
            } else if (onFailure != null) {
                onFailure.run();
            }
        });

        task.setOnFailed(event -> {
            task.getException().printStackTrace();
            if (onFailure != null) {
                onFailure.run();
            }
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
        });

        new Thread(task).start();
    }

    public void getFeesFilterData(String ownUrl, String filterName) {
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
            List<FeesSummaryTable> feesSummaryTable = task.getValue();
            String size = String.valueOf(feesSummaryTable.size());
            principalController.displayFilteredFeesTable(feesSummaryTable, filterName, size);
        });

        task.setOnFailed(event -> {
            principalController.filteredStudentsPanel.setVisible(false);
            principalController.filteredStudentsPanel.setManaged(false);
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            }
            showError("Failed to load not-paid students! Try again!");
        });

        new Thread(task).start();
    }

    public void loadAllStaffList() {
        if (principalController == null) return;

        List<RecentStaffView> staffList = staffListCacheManager.getCachedList();
        if (staffList != null) {
            Platform.runLater(() -> principalController.allStaffTable.getItems().setAll(staffList));
            return;
        }

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
            List<RecentStaffView> staffViewList = task.getValue();
            staffListCacheManager.setStaffList(staffViewList);
            principalController.allStaffTable.getItems().setAll(staffViewList);
        });

        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            }
            showError("Could not fetch staff-list data.");
        });

        new Thread(task).start();
    }

    public void searchStaffData(String staffId) {
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
            FindStaffResponse staff = task.getValue();
            if (staff != null) {
                principalController.displayStaffDetails(staff);
            } else {
                principalController.staffDetailsPanel.setVisible(false);
                principalController.staffDetailsPanel.setManaged(false);
                principalController.noStaffFoundPanel.setVisible(true);
                principalController.noStaffFoundPanel.setManaged(true);
            }
        });

        task.setOnFailed(event -> {
            showError("Error searching for staff. Please try again later.");
            task.getException().printStackTrace();
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
            System.out.println("Error: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    public void searchStudentData(String studentId) {
        if (principalController == null) return;

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
                System.out.println(principalController.updateStudentPersonalData);
                principalController.displayStudentPersonalInfo(principalController.updateStudentPersonalData);
            }
        });

        task.setOnFailed(event -> {
            showError("Error searching for student. Please try again later.");
            principalController.noStudentFoundPanel.setVisible(true);
            principalController.noStudentFoundPanel.setManaged(true);
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
            System.out.println("Error: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    public void showUpdateStudentDataDialog() {
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

        DatePicker dobPicker = new DatePicker(LocalDate.parse(principalController.updateStudentPersonalData.getDateOfBirth()));
        grid.add(new Label("Date of Birth (Editable):"), 0, 5);
        grid.add(dobPicker, 1, 5);

        TextField guardianNameField = new TextField(principalController.updateStudentPersonalData.getParentName());
        grid.add(new Label("Guardian Name (Editable):"), 0, 6);
        grid.add(guardianNameField, 1, 6);

        TextField guardianPhoneField = new TextField(principalController.updateStudentPersonalData.getParentPhoneNumber());
        grid.add(new Label("Contact (Editable):"), 0, 7);
        grid.add(guardianPhoneField, 1, 7);

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
                        principalController.updateStudentPersonalData.getGradeName()
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(this::updateStudentPersonalData);
    }

    private void updateStudentPersonalData(UpdateStudentPersonalData updateStudentPersonalData) {
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
                showSuccess("Student data updated successfully!");
                searchStudentData(updateStudentPersonalData.getStudentId());
            } else {
                showError("Failed to update student data");
            }
        });

        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
            showError("Error updating student details for " + updateStudentPersonalData.getFirstName());
        });

        new Thread(task).start();
    }

    public void SearchStudentResults(String studentId, String semesterId, String classId) {
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
            StudentResults studentResults = task.getValue();
            if (studentResults != null) {
                showSuccess("Results found for " + studentResults.getStudentName());
                principalController.displayStudentResultsPanel(studentResults);
            } else {
                principalController.noResultsFoundPanel.setVisible(true);
                principalController.noResultsFoundPanel.setManaged(true);
            }
        });

        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
            showError("Error searching student results for " + studentId);
            principalController.noResultsFoundPanel.setVisible(true);
            principalController.noResultsFoundPanel.setManaged(true);
        });

        new Thread(task).start();
    }

    public void addNewFees(String gradeId, String semesterId, String feesAmount) {
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
                    JsonObject responseBody = gson.fromJson(response.body(), JsonObject.class);
                    return responseBody.get("message").getAsString();
                }
                throw new Exception("Could not add new fees");
            }
        };

        task.setOnSucceeded(event -> showSuccess(task.getValue()));
        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
            showError("Error: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    /*public void addNewFeePayment(String studentId, String semesterId, String gradeId, String amountPaid,
                                 String payer, String payerContact) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/add-payment";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("studentId", studentId);
                requestBody.addProperty("semesterId", semesterId);
                requestBody.addProperty("gradeId", gradeId);
                requestBody.addProperty("amountPaid", amountPaid);
                requestBody.addProperty("payer", payer);
                requestBody.addProperty("payerContact", payerContact);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    JsonObject responseBody = gson.fromJson(response.body(), JsonObject.class);
                    return responseBody.get("message").getAsString();
                } else {
                    return response.body();
                }
            }
        };

        task.setOnSucceeded(event -> showSuccess(task.getValue()));

        task.setOnFailed(event -> {
            task.getException().printStackTrace();
            showError("Error adding payment for " + studentId);
        });

        new Thread(task).start();
    }*/

    public void showAddSemesterDialog(String staffId) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add New Semester");
        dialog.setHeaderText("Create a new semester");

        ButtonType addButtonType = new ButtonType("Add Semester", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField semesterNameField = new TextField();
        semesterNameField.setPromptText("e.g., First Semester");

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");

        TextField academicYearField = new TextField();
        academicYearField.setPromptText("e.g., 2024/2025");

        grid.add(new Label("Semester Name:"), 0, 0);
        grid.add(semesterNameField, 1, 0);
        grid.add(new Label("Start Date:"), 0, 1);
        grid.add(startDatePicker, 1, 1);
        grid.add(new Label("End Date:"), 0, 2);
        grid.add(endDatePicker, 1, 2);
        grid.add(new Label("Academic Year:"), 0, 3);
        grid.add(academicYearField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        Runnable validateFields = () -> {
            boolean isValid = !semesterNameField.getText().trim().isEmpty() &&
                    startDatePicker.getValue() != null &&
                    endDatePicker.getValue() != null &&
                    !academicYearField.getText().trim().isEmpty();
            addButton.setDisable(!isValid);
        };

        semesterNameField.textProperty().addListener((obs, oldVal, newVal) -> validateFields.run());
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> validateFields.run());
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> validateFields.run());
        academicYearField.textProperty().addListener((obs, oldVal, newVal) -> validateFields.run());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return semesterNameField.getText().trim();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(semesterName -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String academicYear = academicYearField.getText().trim();

            if (endDate.isBefore(startDate)) {
                showError("End date cannot be before start date!");
                return;
            }

            addNewSemester(semesterName, startDate, endDate, academicYear, staffId);
        });
    }

    public void showAddSubjectDialog() {
        Dialog<AssignSubject> dialog = new Dialog<>();
        dialog.setTitle("Add New Subject");
        dialog.setHeaderText("Create a new subject");

        ButtonType addButtonType = new ButtonType("Add Subject", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField subjectNameField = new TextField();
        subjectNameField.setPromptText("e.g., Mathematics");

        ComboBox<GradeCaching> gradeComboBox = new ComboBox<>();
        gradeComboBox.setPromptText("Select Class/Grade");
        List<GradeCaching> grades = gradeCacheManager.getGradeCache();
        if (grades != null) {
            gradeComboBox.getItems().addAll(grades);
        }

        ComboBox<SemesterCaching> semesterComboBox = new ComboBox<>();
        semesterComboBox.setPromptText("Select Semester");
        List<SemesterCaching> semesters = semesterCachingManager.getCachedSemesters();
        if (semesters != null) {
            semesterComboBox.getItems().addAll(semesters);
        }

        grid.add(new Label("Subject Name:"), 0, 0);
        grid.add(subjectNameField, 1, 0);
        grid.add(new Label("Assign Grade:"), 0, 1);
        grid.add(gradeComboBox, 1, 1);
        grid.add(new Label("Select Semester:"), 0, 2);
        grid.add(semesterComboBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        Runnable validateFields = () -> {
            boolean isValid = !subjectNameField.getText().trim().isEmpty() &&
                    gradeComboBox.getValue() != null &&
                    semesterComboBox.getValue() != null;
            addButton.setDisable(!isValid);
        };

        subjectNameField.textProperty().addListener((obs, oldVal, newVal) -> validateFields.run());
        gradeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateFields.run());
        semesterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateFields.run());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                GradeCaching selectedGrade = gradeComboBox.getSelectionModel().getSelectedItem();
                SemesterCaching selectedSemester = semesterComboBox.getSelectionModel().getSelectedItem();
                return new AssignSubject(
                        subjectNameField.getText().trim(),
                        selectedGrade.getLevelId(),
                        selectedSemester.getSemesterId()
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(this::addNewSubject);
    }

    private void addNewSemester(String semesterName, LocalDate startDate,
                                LocalDate endDate, String academicYear, String currentStaffId) {
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
                showSuccess("Semester '" + semesterName + "' added successfully!");
                semesterCachingManager.clearCache();
            } else {
                showError("Failed to add semester: " + status);
            }
        });

        task.setOnFailed(event -> showError("Error adding semester: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    private void addNewSubject(AssignSubject assignment) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = BASE_URL + API_BASE_URL + "/add-subject";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("subjectName", assignment.getSubjectName());
                requestBody.addProperty("gradeId", assignment.getGradeId());
                requestBody.addProperty("semesterId", assignment.getSemesterId());
                System.out.println(assignment.getSemesterId());
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
                showSuccess("Subject '" + assignment.getSubjectName() + "' added successfully!");
            } else {
                showError("Failed to add subject: " + status);
            }
        });

        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            }
            showError("Error adding subject");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    public void addNewFeesPayment(String studentId, String semesterId, String gradeId,
                                  String amountPaid, String payerName, String payerPhone) {
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
                showError("Failed to add fees payment: " + status);
            }

            showSuccess(status);
        });

        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }
            showError("Error adding fees payment: " + task.getException().getMessage());
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
                showError("No students found in this class");
                if (onFailure != null) onFailure.run();
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
                        showSuccess("Student list saved successfully!");
                        if (onSuccess != null) onSuccess.run();
                    } catch (Exception e) {
                        showError("Failed to save file: " + e.getMessage());
                        if (onFailure != null) onFailure.run();
                    }
                } else {
                    if (onFailure != null) onFailure.run();
                }
            });
        });

        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            String errorMessage = exception != null ? exception.getMessage() : null;

            if (exception instanceof java.net.ConnectException) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null &&
                    (errorMessage.contains("Connection refused") ||
                            errorMessage.contains("ConnectException"))) {
                showError("Check your internet connection and try again.");
            } else if (errorMessage != null) {
                showError("Error: " + errorMessage);
            } else {
                showError("Connection error. Please check if the backend is running.");
            }

            task.getException().printStackTrace();
            showError("Failed to fetch student list");
            if (onFailure != null) onFailure.run();
        });

        new Thread(task).start();
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

    public void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public void showSuccess(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText(message);
            alert.showAndWait();
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
}