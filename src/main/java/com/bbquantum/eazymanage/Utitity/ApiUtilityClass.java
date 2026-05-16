package com.bbquantum.eazymanage.Utitity;

import com.bbquantum.eazymanage.Controller.*;
import com.bbquantum.eazymanage.DTO.Academics.*;
import com.bbquantum.eazymanage.DTO.Finance.*;
import com.bbquantum.eazymanage.DTO.Institution.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    // OWN HttpClient and Gson - don't rely on controllers
    private final HttpClient httpClient = SSLHelper.createTrustAllHttpClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    // Backend Configuration
    public final String BASE_URL = UtilityClass.BASE_URL;

    private final String FINANCE_URL = BASE_URL + "/finance";
    private final String STAFF_URL = BASE_URL + "/staff";
    private final String STUDENT_URL = BASE_URL + "/student";
    private final String ADMIN_URL = BASE_URL + "/admin";

    public final String API_VERSION_1 = "/v1";
    public final String API_VERSION_2 = "/v2";

    // Store student results
    public StudentResult studentResult;

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
        loadTotalFeesPaid(principalId, notification, onSuccess,
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });

        loadTotalSemesterFees(principalId, notification, onSuccess,
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });

        loadTotalStaff(principalId, notification, onSuccess,
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });

        loadTotalTeachingStaff(principalId, notification, onSuccess,
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });

        loadTotalStudents(principalId, notification, onSuccess,
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });

        loadAbsentStudents(principalId, notification, onSuccess,
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });

        loadCachedStaffList(principalId, notification, onSuccess,
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });

        loadGradesFromBackend(principalId, notification, onSuccess,
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });

        loadSemestersFromBackend(principalId, notification, onSuccess,
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });

        loadClassSummaryFees(principalId, notification, onSuccess,
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });

        loadAllStaffList(principalId, notification, onSuccess,
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });
    }

    public void loadFinanceDashboardData(String financeId, StackPane notification, Runnable onSuccess) {
        loadTotalFeesPaid(financeId, notification, onSuccess,
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });

        loadTotalSemesterFees(financeId, notification, onSuccess,
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });

        loadGradesFromBackend(financeId, notification, onSuccess,
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });

        loadSemestersFromBackend(financeId, notification, onSuccess,
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });

        fetchRecentFeesTransactions(financeId, notification, onSuccess,
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });
    }

    public void loadStaffDashboardData(String staffId, StackPane notification, Runnable onSuccess) {

        loadGradesFromBackend(staffId, notification, onSuccess,
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });

        loadSemestersFromBackend(staffId, notification, onSuccess,
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });

        loadStaffGrades(staffId, notification, onSuccess,
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });
    }

    public void loadAdminDashboard(String staffId, StackPane notification, Runnable onSuccess) {

        fetchRecentSystemLogs(staffId, notification, onSuccess,
                () -> {
            //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });

        loadCachedStaffList(staffId, notification, onSuccess,
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });

        loadGradesFromBackend(staffId, notification, onSuccess,
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });

        loadSemestersFromBackend(staffId, notification, onSuccess,
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });

        loadAllStaffList(staffId, notification, onSuccess,
                () -> {
                    //On Failure
                    Platform.runLater(() -> {
                        UtilityClass.showError(notification, errorMessageContainer);
                    });
                });
    }

    // ==================== PRINCIPAL METHODS ====================

    public void loadTotalStudents(String staffId, StackPane notification,
                                  Runnable onSuccess, Runnable onFailure) {

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = STUDENT_URL + API_VERSION_1 + "/total-students/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                errorMessageContainer = "";
                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response != null) {
                UtilityClass.totalStudentsFromBackend = Long.parseLong(response);
                counter++;
                checkCounter(counter, onSuccess, null);
                return;
            }

            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }

            counter ++;
            checkCounter(counter, onFailure, onFailure);
        });

        new Thread(task).start();
    }

    public void loadTotalStaff(String staffId,  StackPane notification,
                               Runnable onSuccess, Runnable onFailure) {

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = STAFF_URL + API_VERSION_1 + "/total-staffs/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Message: " + response.body());
                if (response.statusCode() ==  200) {
                    return response.body();
                }

                errorMessageContainer = "";
                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response != null) {
                UtilityClass.totalStaffFromBackend = Long.parseLong(response);
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }

            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }

            counter ++;
            checkCounter(counter, onFailure, onFailure);
        });

        new Thread(task).start();
    }

    public void loadTotalTeachingStaff(String staffId,  StackPane notification,
                               Runnable onSuccess, Runnable onFailure) {

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = STAFF_URL + API_VERSION_1 + "/total-teaching-staffs/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() ==  200) {
                    return response.body();
                }

                errorMessageContainer = "";
                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response != null) {
                UtilityClass.totalTeachingStaff = Long.parseLong(response);
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }

            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }

            counter ++;
            checkCounter(counter, onFailure, onFailure);
        });

        new Thread(task).start();
    }

    public void loadTotalSemesterFees(String staffId, StackPane notification,
                                      Runnable onSuccess, Runnable onFailure) {

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = FINANCE_URL + API_VERSION_1 + "/total-fees";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() ==  200) {
                    return response.body();
                }

                errorMessageContainer = "";
                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response != null) {
                UtilityClass.totalSemesterFeesFromBackend = Double.parseDouble(response);
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }

            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }

            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        new Thread(task).start();
    }

    public void loadTotalFeesPaid(String staffId, StackPane notification,
                                  Runnable onSuccess, Runnable onFailure) {

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = FINANCE_URL + API_VERSION_1 + "/fees-paid";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() ==  200) {
                    return response.body();
                }

                errorMessageContainer = "";
                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response != null) {
                UtilityClass.feesPaidFromBackend =  Double.parseDouble(response);
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }

            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet connection.");
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }

            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        new Thread(task).start();
    }

    public void loadAbsentStudents(String staffId, StackPane notification,
                                   Runnable onSuccess, Runnable onFailure) {

        List<AbsenteesView> cachedStudents = UtilityClass.cacheManager.getCachedAbsenteeStudents();
        if (cachedStudents != null && !cachedStudents.isEmpty()) {
            if (UtilityClass.cacheManager.isCacheValid()) {
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }
        }

        Type studentListType = new TypeToken<List<AbsenteesView>>() {}.getType();

        Task<List<AbsenteesView>> task = new Task<>() {
            @Override
            protected List<AbsenteesView> call() throws Exception {
                String url = STUDENT_URL + API_VERSION_1 + "/absent-students/" + staffId;
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), studentListType);
                }

                errorMessageContainer = "";
                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<AbsenteesView> students = task.getValue();
            if (students == null) {
                counter ++;
                checkCounter(counter, onSuccess, onFailure);
                return;
            }

            if (students.isEmpty()) {
                successMessageContainer = "Today's attendance not recorded";
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }

            UtilityClass.cacheManager.setCachedAbsenteeStudents(students);
            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }

            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        new Thread(task).start();
    }

    public void loadCachedStaffList(String staffId, StackPane notification,
                                    Runnable onSuccess, Runnable onFailure) {

        Type staffListType = new TypeToken<List<StaffCaching>>() {}.getType();

        Task<List<StaffCaching>> task = new Task<>() {
            @Override
            protected List<StaffCaching> call() throws Exception {
                String url = STAFF_URL + API_VERSION_1 + "/load-staffs-info";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.body());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), staffListType);
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<StaffCaching> staffs = task.getValue();
            if (staffs != null) {
                UtilityClass.staffCacheManager.setStaffList(staffs);
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }

            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }
            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        new Thread(task).start();
    }

    public void loadGradesFromBackend(String staffId, StackPane notification,
                                      Runnable onSuccess, Runnable onFailure) {

        List<GradeCaching> cachedGrades = UtilityClass.gradeCacheManager.getGradesCache();
        if (cachedGrades != null && !cachedGrades.isEmpty() && UtilityClass.gradeCacheManager.isCachedGradesValid()) {
            counter++;
            checkCounter(counter, onSuccess, null);
            return;
        }


        Type classListType = new TypeToken<List<GradeCaching>>() {}.getType();

        Task<List<GradeCaching>> task = new Task<>() {
            @Override
            protected List<GradeCaching> call() throws Exception {
                String url = STAFF_URL + API_VERSION_1 + "/load-levels";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), classListType);
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<GradeCaching> gradesList = task.getValue();
            System.out.println("success");

            if (gradesList != null && !gradesList.isEmpty()) {
                UtilityClass.gradeCacheManager.setCachedGrades(gradesList);
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }

            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }

            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        new Thread(task).start();
    }

    public void loadSemestersFromBackend(String staffId, StackPane notification,
                                         Runnable onSuccess, Runnable onFailure) {

        List<SemesterCaching> semesterCaching = UtilityClass.semesterCachingManager.getCachedSemesters();
        if (semesterCaching != null && !semesterCaching.isEmpty()) {
            if (UtilityClass.semesterCachingManager.isSemesterCacheValid()) {
                counter++;
                checkCounter(counter, onSuccess, null);
                return;
            }
        }

        Type semesterListType = new TypeToken<ArrayList<SemesterCaching>>() {}.getType();

        Task<List<SemesterCaching>> task = new Task<>() {
            @Override
            protected List<SemesterCaching> call() throws Exception {
                String url = STAFF_URL + API_VERSION_1 + "/load-semesters";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), semesterListType);
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<SemesterCaching> semesterCachingList = task.getValue();

            if (semesterCachingList != null && !semesterCachingList.isEmpty()) {
                UtilityClass.semesterCachingManager.setCachedSemesters(semesterCachingList);
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }
            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }

            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        new Thread(task).start();
    }

    public void loadClassSummaryFees(String staffId, StackPane notification,
                                     Runnable onSuccess, Runnable onFailure) {


        Type classFeesList = new TypeToken<ArrayList<ClassFeesSummary>>() {}.getType();

        Task<List<ClassFeesSummary>> task = new Task<>() {
            @Override
            protected List<ClassFeesSummary> call() throws Exception {
                String url = FINANCE_URL + API_VERSION_1 + "/class-summary-fees/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), classFeesList);
                }

                errorMessageContainer = "";
                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<ClassFeesSummary> classFeesSummaryList = task.getValue();
            if (classFeesSummaryList != null && !classFeesSummaryList.isEmpty()) {
                UtilityClass.feesCacheManager.setClassFeesSummaryCache(classFeesSummaryList);
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }
            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }

            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        new Thread(task).start();
    }

    public void loadStaffGrades(String staffId, StackPane notification,
                                Runnable onSuccess, Runnable onFailure) {

        Type studentList = new TypeToken<ArrayList<GradeInformation>>() {}.getType();

        Task<List<GradeInformation>> task = new Task<>() {
            @Override
            protected List<GradeInformation> call() throws Exception {
                String url = STAFF_URL + API_VERSION_1 + "/load-staff-grades/" + staffId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), studentList);
                }

                errorMessageContainer = "";
                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<GradeInformation> gradesInformation = task.getValue();
            if (gradesInformation != null && !gradesInformation.isEmpty()) {
                UtilityClass.gradeInfoCacheManager.setGradesInformation(gradesInformation);
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }
            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }

            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        new Thread(task).start();
    }

    public void enrollNewStudent(String staffId, String firstName, String lastName, String levelId, String gender,
                                 LocalDate dateOfBirth, String hometown, String parentName, String guardianContact,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = STUDENT_URL + API_VERSION_1 + "/add-new-student";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("firstName", firstName);
                requestBody.addProperty("lastName", lastName);
                requestBody.addProperty("levelId", levelId);
                requestBody.addProperty("gender", gender);
                requestBody.addProperty("dateOfBirth", dateOfBirth.toString());
                requestBody.addProperty("hometown", hometown);
                requestBody.addProperty("parentName", parentName);
                requestBody.addProperty("guardianContact", guardianContact);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "Content-Type", "application/json",
                                "staffId", staffId)
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response != null) {
                successMessageContainer = "";
                successMessageContainer = response;
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
                return;
            }

            onFailure.run();
        });

        new Thread(task).start();
    }

    public void loadSubjectStudents(String subjectId, String staffId,
                                    Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Task<SubjectScores> task = new Task<>() {
            @Override
            protected SubjectScores call() throws Exception {
                String url = STUDENT_URL + API_VERSION_1 + "/load-subject-students/" + subjectId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), SubjectScores.class);
                }

                errorMessageContainer = "";
                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            if (UtilityClass.subjectScores != null) {
                UtilityClass.subjectScores = null;
            }

            UtilityClass.subjectScores = task.getValue();
            if (UtilityClass.subjectScores == null) {
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
                return;
            }
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void saveStudentScores(StudentsScoresTable scores, String subjectId, String semesterId, String staffId,
                                  Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = STUDENT_URL + API_VERSION_1 + "/save-subject-scores";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("studentId", scores.getStudentId());
                requestBody.addProperty("exercise1Score", scores.getExercise1Score());
                requestBody.addProperty("classTestScore", scores.getClassTestScore());
                requestBody.addProperty("exercise2Score", scores.getExercise2Score());
                requestBody.addProperty("projectScore", scores.getProjectScore());
                requestBody.addProperty("classScore", scores.getClassScore());
                requestBody.addProperty("examScore", scores.getExamScore());
                requestBody.addProperty("calculatedExamScore", scores.getCalculatedExamScore());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "Content-Type", "application/json",
                                "staffId", staffId,
                                "subjectId", subjectId,
                                "semesterId", semesterId)
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return "Successful";
                }

                errorMessageContainer = "";
                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String status = task.getValue();
            if (status != null) {
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
                return;
            }

            onFailure.run();
        });

        new Thread(task).start();
    }

    public void addNewGrade(String staffId, String gradeName, String instructorId,
                             Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = ADMIN_URL + API_VERSION_1 + "/add-new-class" +
                        "?gradeName=" + gradeName +
                        "&instructorId=" + instructorId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "staffId", staffId)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return "Class added successfully";
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String status = task.getValue();
            if (status != null) {
                successMessageContainer = status;
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
                return;
            }

            errorMessageContainer = "Could not add new grade";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void recoverPassword(String staffId, String newPasswordStaffId, String newPassword,
                                Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = ADMIN_URL + API_VERSION_1 + "/reset-staff-password?" +
                        "staffId=" + staffId +
                        "&newPasswordStaffId=" + newPasswordStaffId +
                        "&newPassword=" + newPassword;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return response.body();
                }

                errorMessageContainer = "";
                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String status = task.getValue();
            if (status == null) {
                onFailure.run();
                return;
            }

            successMessageContainer = status;
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }

            onFailure.run();
        });

        new Thread(task).start();
    }

    public void fetchStudentPaymentRecords(String staffId, String studentId, String semesterId, String gradeId,
                                           Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<StudentFeesPaymentDisplay> task = new Task<>() {
            @Override
            protected StudentFeesPaymentDisplay call() throws Exception {
                String url = FINANCE_URL + API_VERSION_1 + "/search-fees-report" +
                        "?studentId=" + studentId +
                        "&semesterId=" + semesterId +
                        "&gradeId=" + gradeId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    System.out.println("Response: " + response.body());
                    return gson.fromJson(response.body(), StudentFeesPaymentDisplay.class);
                }

                errorMessageContainer = "";
                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            UtilityClass.paymentRecords = task.getValue();
            if (UtilityClass.paymentRecords != null) {
                successMessageContainer = "Successfully loaded report";
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
                return;
            }

            errorMessageContainer = "Could not fetch report";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void getFeesFilterData(String ownUrl,
                                  Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Type fullYPaidList = new TypeToken<List<FeesSummaryTable>>() {}.getType();

        Task<List<FeesSummaryTable>> task = new Task<>() {
            @Override
            protected List<FeesSummaryTable> call() throws Exception {
                String url = FINANCE_URL + API_VERSION_1 + ownUrl;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), fullYPaidList);
                }

                errorMessageContainer = "";
                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            principalController.feesSummaryTable = task.getValue();
            if (principalController.feesSummaryTable != null) {
                successMessageContainer = "Successfully loaded report";
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
                return;
            }

            errorMessageContainer = "Could not load report";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void loadAllStaffList(String staffId, StackPane notification,
                                 Runnable onSuccess, Runnable onFailure) {

        Type staffListList = new TypeToken<ArrayList<ViewStaffList>>() {}.getType();

        Task<List<ViewStaffList>> task = new Task<>() {
            @Override
            protected List<ViewStaffList> call() throws Exception {
                String url = STAFF_URL + API_VERSION_1 + "/staff-list";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), staffListList);
                }

                errorMessageContainer = "";
                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<ViewStaffList> staffViewList = task.getValue();
            if (staffViewList != null && !staffViewList.isEmpty()) {
                UtilityClass.staffListCacheManager.setStaffList(staffViewList);
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }
            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter ++;
                checkCounter(counter, onFailure, null);
                return;
            }

            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        new Thread(task).start();
    }

    public void searchStaffData(String searchId, String staffId,
                                Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Task<FindStaffResponse> task = new Task<>() {
            @Override
            protected FindStaffResponse call() throws Exception {
                String url = STAFF_URL + API_VERSION_1 + "/find-staff-by-id/" + searchId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), FindStaffResponse.class);
                }

                errorMessageContainer = "";
                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            findStaffResponse = task.getValue();
            if (findStaffResponse != null) {
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
                return;
            }

            onFailure.run();
        });

        new Thread(task).start();
    }

    public void searchStudentData(String staffId, String studentId,
                                  Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Task<UpdateStudentPersonalData> task = new Task<>() {
            @Override
            protected UpdateStudentPersonalData call() throws Exception {
                String url = STUDENT_URL + API_VERSION_1 + "/find-student/" + studentId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), UpdateStudentPersonalData.class);
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
             UtilityClass.updateStudentPersonalData = task.getValue();
            if(UtilityClass.updateStudentPersonalData == null) {
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
                return;
            }

            errorMessageContainer = "Could load student data";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void updateStudentPersonalData(UpdateStudentPersonalData data, String staffId,
                                           Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = STUDENT_URL + API_VERSION_1 + "/update-student-data";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("studentId", data.getStudentId());
                requestBody.addProperty("gender", data.getGender());
                requestBody.addProperty("dateOfBirth", data.getDateOfBirth());
                requestBody.addProperty("parentName", data.getParentName());
                requestBody.addProperty("parentPhoneNumber", data.getParentPhoneNumber());
                requestBody.addProperty("status", data.getStatus());
                requestBody.addProperty("firstName", data.getFirstName());
                requestBody.addProperty("lastName", data.getLastName());
                requestBody.addProperty("gradeId", data.getGradeId());
                requestBody.addProperty("homeTown", data.getHomeTown());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "Content-Type", "application/json",
                                "staffId", staffId)
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                errorMessageContainer = "";
                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String status = task.getValue();
            if (status != null) {
                successMessageContainer = status;
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
                return;
            }

            onFailure.run();
        });

        new Thread(task).start();
    }

    public void SearchStudentResults(String studentId, String semesterId, String classId,
                                     Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Task<StudentResult> task = new Task<>() {
            @Override
            protected StudentResult call() throws Exception {
                String url = STUDENT_URL + API_VERSION_1 + "/search-student-results" +
                        "?studentId=" + studentId +
                        "&semesterId=" + semesterId +
                        "&classId=" + classId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), StudentResult.class);
                }

                errorMessageContainer = "";
                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            studentResult = task.getValue();
            if (studentResult != null) {
                successMessageContainer = "Results loaded successfully";
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
                return;
            }

            errorMessageContainer = "Could not load results";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void addNewFees(String staffId, String gradeId, String semesterId, String feesAmount,
                           Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = FINANCE_URL + API_VERSION_1 + "/add-new-fees" +
                        "?gradeId=" + gradeId +
                        "&semesterId=" + semesterId +
                        "&feesAmount=" + feesAmount;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "staffId", staffId)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                errorMessageContainer = "";
                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String result = task.getValue();
            if (result != null) {
                successMessageContainer = result;
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
                return;
            }

            errorMessageContainer = "Could not save Fees";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void addNewSemester(String staffId, String semesterName, LocalDate startDate,
                                LocalDate endDate, String academicYear,
                                Runnable onSuccess,  Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = ADMIN_URL + API_VERSION_1 + "/add-semester";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("semesterName", semesterName);
                requestBody.addProperty("startDate", startDate.toString());
                requestBody.addProperty("endDate", endDate.toString());
                requestBody.addProperty("academicYear", academicYear);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "Content-Type", "application/json",
                                "staffId", staffId)
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return "Successfull";
                }

                errorMessageContainer = response.body();
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
                return;
            }

            errorMessageContainer = "Could not save Semester";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void addNewSubject(AssignSubject assignment,
                               Runnable onSuccess,  Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = ADMIN_URL + API_VERSION_1 + "/add-subject";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("subjectName", assignment.getSubjectName());
                requestBody.addProperty("gradeId", assignment.getGradeId());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response == null) {
                onFailure.run();
                return;
            }

            successMessageContainer = response;
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }

            onFailure.run();
        });

        new Thread(task).start();
    }

    public void addNewFeesPayment(String staffId, String studentId, String semesterId,
                                  String amountPaid, String payerName, String payerPhone,
                                  Runnable onSuccess,  Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = FINANCE_URL + API_VERSION_2 + "/add-fees-payment";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("studentId", studentId);
                requestBody.addProperty("semesterId", semesterId);
                requestBody.addProperty("amountPaid", amountPaid);
                requestBody.addProperty("payerName", payerName);
                requestBody.addProperty("payerPhone", payerPhone);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "Content-Type", "application/json",
                                "staffId", staffId)
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String status = task.getValue();
            if (status == null) {
                onFailure.run();
                return;
            }

            successMessageContainer = status;
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }

            errorMessageContainer = "Could not add Payment";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void fetchRecentFeesTransactions(String staffId, StackPane notification,
                                            Runnable onSuccess, Runnable onFailure) {


        Type listType = new TypeToken<List<RecentPaymentTable>>() {}.getType();

        Task<List<RecentPaymentTable>> task = new Task<>() {
            @Override
            protected List<RecentPaymentTable> call() throws Exception {
                String url = FINANCE_URL + API_VERSION_1 + "/recent-fees-transactions";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "staffId", staffId)
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
            List<RecentPaymentTable> recentPayments = task.getValue();
            if (recentPayments != null && !recentPayments.isEmpty()) {
                UtilityClass.feesCacheManager.setRecentPaymentCache(recentPayments);
                counter++;
                checkCounter(counter, onSuccess, null);
                return;
            }

            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                counter ++;
                checkCounter(counter, onFailure, null);
                return;
            }

            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        new Thread(task).start();
    }

    public void saveGradingCriteria(GradingCriteria gradingCriteria, String staffId,
                                    Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = ADMIN_URL + API_VERSION_1 + "/set-grades";

                JsonObject responseBody = new JsonObject();
                responseBody.addProperty("lowerRange", gradingCriteria.getLowerRange());
                responseBody.addProperty("upperRange", gradingCriteria.getUpperRange());
                responseBody.addProperty("grade", gradingCriteria.getGrade());
                responseBody.addProperty("gradeDescription", gradingCriteria.getGradeDescription());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "Content-Type", "application/json",
                                "staffId", staffId)
                        .POST(HttpRequest.BodyPublishers.ofString(responseBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return "Successful";
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response != null) {
                successMessageContainer =  response;
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
                return;
            }

            errorMessageContainer = "Could not add Grade criteria";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void downloadStudentsReport(String staffId, String levelId, String semesterId, String promotionGradeId, String passScore,
                                       Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<byte[]> task = new Task<>() {
            @Override
            protected byte[] call() throws Exception {
                String url = ADMIN_URL + API_VERSION_2 + "/generate-class-report?" +
                        "staffId=" + staffId +
                        "&levelId=" + levelId +
                        "&semesterId=" + semesterId +
                        "&promotionGradeId=" + promotionGradeId +
                        "&passScore=" + passScore;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                HttpResponse<byte[]> response = httpClient
                        .send(request, HttpResponse.BodyHandlers.ofByteArray());

                if (response.statusCode() == 200) {
                    return response.body();
                }

                String errorMsg = new String(response.body(), StandardCharsets.UTF_8);
                errorMessageContainer = errorMsg;
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

                    Window window = adminController.generateReportBtn.getScene().getWindow();
                    File file = chooser.showSaveDialog(window);


                    if (file != null) {
                        Files.write(file.toPath(), pdfBytes);
                        onSuccess.run();
                    }else {
                        onFailure.run();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMessageContainer = "";
                    errorMessageContainer = e.getMessage();
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
                return;
            }

            onFailure.run();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void loadClassResults(String levelId, String semesterId,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Type listType = new TypeToken<List<ViewClassSemesterReport>>() {}.getType();
        Task<List<ViewClassSemesterReport>> task = new Task<>() {
            @Override
            protected List<ViewClassSemesterReport> call() throws Exception {
                String url = STAFF_URL + API_VERSION_2 + "/view-class-results?" +
                        "levelId=" + levelId +
                        "&semesterId=" + semesterId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), listType);
                }

                errorMessageContainer = "";
                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            UtilityClass.classResults = task.getValue();
            if (UtilityClass.classResults != null) {
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
                return;
            }

            onFailure.run();
        });

        new Thread(task).start();
    }

    public void movePassedStudents(String levelId,
                                   Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = STAFF_URL + API_VERSION_1 + "/move-students/" + levelId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return response.body();
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            successMessageContainer = task.getValue();
            if (successMessageContainer == null) {
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
                return;
            }

            errorMessageContainer = "Could not move passed students";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void loadStudentsForAttendance(String levelId, String date,
                                          Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Type listType = new TypeToken<List<StudentAttendance>>() {}.getType();

        Task<List<StudentAttendance>> task = new Task<>() {
            @Override
            protected List<StudentAttendance> call() throws Exception {
                String url = STUDENT_URL + API_VERSION_1 + "/load-students-for-attendance/" +
                        levelId + "/" + date;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
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
                return;
            }

            onFailure.run();
        });

        new Thread(task).start();
    }

    public void markStudentAttendance(String studentId, String staffId, String levelId, String status,
                                      Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = STAFF_URL + API_VERSION_1 + "/mark-attendance";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("studentId", studentId);
                requestBody.addProperty("staffId", staffId);
                requestBody.addProperty("levelId", levelId);
                requestBody.addProperty("status", status);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                errorMessageContainer = response.body();
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
                return;
            }

            onFailure.run();
        });

        new Thread(task).start();
    }

    public void updateAttendance(String studentId, String staffId, String levelId, String status, String dateMarked,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = STAFF_URL + API_VERSION_1 + "/update-attendance?" +
                        "studentId=" + studentId +
                        "&staffId=" + staffId +
                        "&levelId=" + levelId +
                        "&status=" + status +
                        "&dateMarked=" + dateMarked;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
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

    public void findClassInfo(String levelId, String staffId,
                              Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<FindAndUpdateClassInfo> task = new Task<>() {
            @Override
            protected FindAndUpdateClassInfo call() throws Exception {
                String url = ADMIN_URL + API_VERSION_1 + "/find-class-info/" +
                        levelId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), FindAndUpdateClassInfo.class);
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            UtilityClass.findAndUpdateClassInfo = task.getValue();
            if (UtilityClass.findAndUpdateClassInfo == null) {
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
                return;
            }

            errorMessageContainer = "Could not fetch class Info";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void updateClassInfo(FindAndUpdateClassInfo updateInfo, String staffId,
                                Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = ADMIN_URL + API_VERSION_1 + "/update-class-info";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("levelId", updateInfo.getLevelId());
                requestBody.addProperty("levelName", updateInfo.getLevelName());
                requestBody.addProperty("staffId", updateInfo.getStaffId());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "Content-Type", "application/json",
                                "staffId", staffId)
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String result = task.getValue();
            if (result == null) {
                onFailure.run();
                return;
            }
            successMessageContainer = result;

            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }

            errorMessageContainer = "Could not update grade info";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void updateStaffInfo(FindStaffResponse info, String staffId,
                                Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = ADMIN_URL + API_VERSION_1 + "/update-staff-info";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("staffId", info.getStaffId());
                requestBody.addProperty("firstName", info.getFirstName());
                requestBody.addProperty("surname", info.getSurname());
                requestBody.addProperty("gender", info.getGender());
                requestBody.addProperty("dateOfBirth", info.getDateOfBirth());
                requestBody.addProperty("email", info.getEmail());
                requestBody.addProperty("phoneNumber", info.getPhoneNumber());

                JsonArray rolesArray = new JsonArray();
                if (info.getStaffRoles() != null) {
                    info.getStaffRoles().forEach(rolesArray::add);
                }

                requestBody.add("staffRoles", rolesArray);
                requestBody.addProperty("staffStatus",  info.getStaffStatus());
                requestBody.addProperty("dateOfRegistration", info.getDateOfRegistration());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "Content-Type", "application/json",
                                "staffId", staffId)
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return "Successfull";
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String result = task.getValue();
            if (result == null) {
                onFailure.run();
                return;
            }

            successMessageContainer = result;
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }

            errorMessageContainer = "Could update info";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void searchSemester(String semesterId,
                               Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<FindSemester> task = new Task<>() {
            @Override
            protected FindSemester call() throws Exception {
                String url = ADMIN_URL + API_VERSION_1 + "/search-semester/" + semesterId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), FindSemester.class);
                }

                errorMessageContainer = "";
                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            adminController.semester = task.getValue();
            if (adminController.semester == null) {
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
                return;
            }

            errorMessageContainer = "Could not get Semester Data";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void updateSemesterInfo(FindSemester info,
                                   Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = ADMIN_URL + API_VERSION_1 + "/update-semester-info";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("semesterID", info.getSemesterID());
                requestBody.addProperty("semesterName", info.getSemesterName());
                requestBody.addProperty("semesterStartDate", info.getSemesterStartDate());
                requestBody.addProperty("semesterEndDate", info.getSemesterEndDate());
                requestBody.addProperty("academicYear", info.getAcademicYear());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                errorMessageContainer = "";
                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response == null) {
                onFailure.run();
                return;
            }

            successMessageContainer = response;
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }

            errorMessageContainer = "Could not update semester info";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void fetchFeesDetails(String staffId, String semesterId, String levelId,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<FetchFeesDetails> task = new Task<>() {
            @Override
            protected FetchFeesDetails call() throws Exception {
                String url = FINANCE_URL + API_VERSION_1 + "/fetch-fees-details/" +
                        semesterId + "/" + levelId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), FetchFeesDetails.class);
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            financeController.feesDetails = task.getValue();
            if (financeController.feesDetails == null) {
                onFailure.run();
                return;
            }

            successMessageContainer = "Fees Details fetched successfully";
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }

            errorMessageContainer = "Could not fetch fees details";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void updateSemesterFees(String staffId, FetchFeesDetails update,
                                   Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = FINANCE_URL + API_VERSION_1 + "/update-semester-fees";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("amount", update.getAmount());
                requestBody.addProperty("semesterId", update.getSemesterId());
                requestBody.addProperty("levelId", update.getLevelId());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "Content-Type", "application/json",
                                "staffid", staffId)
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response == null) {
                onFailure.run();
                return;
            }

            successMessageContainer = response;
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }

            errorMessageContainer = "Could not update Fees details";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void fetchPaymentRecord(String staffId, String studentId, String levelId, String semesterId,
                                   Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Type listType = new TypeToken<List<StudentPaymentRecords>>() {}.getType();

        Task<List<StudentPaymentRecords>> task = new Task<>() {
            @Override
            protected List<StudentPaymentRecords> call() throws Exception {
                String url = FINANCE_URL + API_VERSION_1 + "/fetch-payment-records?"
                        + "studentId=" + studentId
                        + "&levelId=" + levelId
                        + "&semesterId=" + semesterId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "staffId", staffId)
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
            financeController.studentPaymentRecords = task.getValue();
            if (financeController.studentPaymentRecords == null) {
                onFailure.run();
                return;
            }

            successMessageContainer = "Payment records fetched successfully";
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }

            errorMessageContainer = "Could not fetch payment records";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void deletePaymentRecord(String staffId, String transactionId,
                                    Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = FINANCE_URL + API_VERSION_1 + "/delete-payment-record/"
                        + transactionId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "staffId", staffId)
                        .DELETE()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response == null) {
                onFailure.run();
                return;
            }

            successMessageContainer = response;
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }

            errorMessageContainer = "Could not delete record";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void updatePaymentDetails(String staffId, StudentPaymentRecords update,
                                     Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = FINANCE_URL + API_VERSION_1 + "/update-payment-details";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("paymentId", update.getPaymentId());
                requestBody.addProperty("dateOfPayment", update.getDateOfPayment());
                requestBody.addProperty("studentId", update.getStudentId());
                requestBody.addProperty("studentName", update.getStudentName());
                requestBody.addProperty("amount", update.getAmount());
                requestBody.addProperty("semesterId", update.getSemesterId());
                requestBody.addProperty("payerName", update.getPayerName());
                requestBody.addProperty("payerContact", update.getPayerContact());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "Content-Type", "application/json",
                                "staffId", staffId)
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response == null) {
                onFailure.run();
                return;
            }

            successMessageContainer = response;
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }

            errorMessageContainer = "Could not update payment records";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void getStudentName(String studentId,
                               Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = BASE_URL + API_VERSION_2 + "/get-student-name/" + studentId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response == null) {
                onFailure.run();
                return;
            }

            successMessageContainer = response;
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }

            errorMessageContainer = "Could not get Student name";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void fetchGradeFeesRecords(String staffId, String levelId, String semesterId,
                                      Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Type listType = new TypeToken<List<GradeFeesReport>>() {}.getType();

        Task<List<GradeFeesReport>> task = new Task<>() {
            @Override
            protected List<GradeFeesReport> call() throws Exception {
                String url = FINANCE_URL + API_VERSION_1 + "/fetch-grade-fees-report/"
                        + levelId + "/" + semesterId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "staffid", staffId)
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
            financeController.gradeFeesReport = task.getValue();
            if (financeController.gradeFeesReport == null) {
                onFailure.run();
                return;
            }

            successMessageContainer = "Payment records Loaded Successfully";
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }

            errorMessageContainer = "Could not get Records";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void fetchRecentSystemLogs(String staffId, StackPane notification,
                                      Runnable onSuccess, Runnable onFailure) {
        Type listType = new TypeToken<List<LogsDetails>>() {}.getType();

        Task<List<LogsDetails>> task = new Task<>() {
            @Override
            protected List<LogsDetails> call() throws Exception {
                String url = ADMIN_URL + API_VERSION_1 + "/recentLogs";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "staffId", staffId)
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
            List<LogsDetails> response = task.getValue();
            if (response != null) {
                UtilityClass.systemLogsCacheManager.setCachedSystemLogs(response);
                counter++;
                checkCounter(counter, onSuccess, null);
                return;
            }

            counter++;
            checkCounter(counter, onSuccess, onFailure);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();

            String msg = ex.getMessage();

            if (ex instanceof java.net.ConnectException ||
                    (msg != null && msg.contains("Connection refused"))) {
                UtilityClass.showError(notification, "Cannot connect to server. Check your internet.");
                counter ++;
                checkCounter(counter, onSuccess, null);
                return;
            }

            counter ++;
            checkCounter(counter, onSuccess, onFailure);
        });

        new Thread(task).start();
    }

    public void loadClassStudents(String levelId, String staffId,
                                  Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Type listType = new TypeToken<List<StudentsHolder>>() {}.getType();

        Task<List<StudentsHolder>> task = new Task<>() {
            @Override
            protected List<StudentsHolder> call() throws Exception {
                String url = STUDENT_URL + API_VERSION_1 + "/load-students/" + levelId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "staffId", staffId)
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
            if (UtilityClass.studentsHolder != null && !UtilityClass.studentsHolder.isEmpty()) {
                UtilityClass.studentsHolder.clear();
            }

            UtilityClass.studentsHolder  = task.getValue();
            if (UtilityClass.studentsHolder != null && !UtilityClass.studentsHolder.isEmpty()) {
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
                return;
            }

            errorMessageContainer = "Could not get Records";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void loadClassSubjects(String levelId, String staffId,
                                  Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Type listType = new TypeToken<List<SubjectsHolder>>() {}.getType();

        Task<List<SubjectsHolder>> task = new Task<>() {
            @Override
            protected List<SubjectsHolder> call() throws Exception {
                String url = STAFF_URL + API_VERSION_1 + "/load-subjects/" + levelId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "staffid", staffId)
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
            if (UtilityClass.subjectsHolder != null && !UtilityClass.subjectsHolder.isEmpty()) {
                UtilityClass.subjectsHolder.clear();
            }

            UtilityClass.subjectsHolder  = task.getValue();
            if (UtilityClass.subjectsHolder == null || UtilityClass.subjectsHolder.isEmpty()) {
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
                return;
            }

            errorMessageContainer = "Could not get Records";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void fetchSubjectData(String staffId, String subjectId,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Task<SubjectDTO> task = new Task<>() {
            @Override
            protected SubjectDTO call() throws Exception {
                String url = ADMIN_URL + API_VERSION_1 + "/load-subject-data/" + staffId + "/" + subjectId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), SubjectDTO.class);
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            UtilityClass.subjectData = task.getValue();
            if (UtilityClass.subjectData != null) {
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
                return;
            }

            errorMessageContainer = "Could not get Records";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void updateSubjectDetails(SubjectDTO data,
                                     Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                String url = ADMIN_URL + API_VERSION_1 + "/update-subject-details";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("subjectId", data.getSubjectId());
                requestBody.addProperty("subjectName", data.getSubjectName());
                requestBody.addProperty("levelId", data.getLevelId());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + token,
                                "Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            successMessageContainer = task.getValue();
            if (successMessageContainer != null) {
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
                return;
            }

            errorMessageContainer = "Could not update Records";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void deleteSubject(String staffId, String subjectId,
                              Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                String url = ADMIN_URL + API_VERSION_1 + "/delete-subject/" + staffId + "/" + subjectId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
                        .DELETE()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                errorMessageContainer = response.body();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String  response = task.getValue();
            if (response != null) {
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
                return;
            }

            errorMessageContainer = "Could not delete Record";
            onFailure.run();
        });

        new Thread(task).start();
    }

    private void checkCounter(int counter, Runnable onSuccess, Runnable onFailure) {
        if (principalController != null && financeController == null
                && staffController == null && adminController == null) {
            int initialCounter = 11;
            if (counter == initialCounter) {
                onSuccess.run();
                this.counter = 0;
            }
            if (onFailure != null) {
                onFailure.run();
            }
        } else if (principalController == null && financeController != null
                && staffController == null && adminController == null) {
            int initialCounter = 5;
            if (counter == initialCounter) {
                onSuccess.run();
                this.counter = 0;
            }
            if (onFailure != null) {
                onFailure.run();
            }
        } else if (principalController == null && financeController == null
                && staffController != null && adminController == null) {
            int initialCounter = 3;
            if (counter == initialCounter) {
                onSuccess.run();
                this.counter = 0;
            }
            if (onFailure != null) {
                onFailure.run();
            }
        } else if (principalController == null && financeController == null
                && staffController == null && adminController != null) {
            int initialCounter = 5;
            if (counter == initialCounter) {
                onSuccess.run();
                this.counter = 0;
            }
            if (onFailure != null) {
                onFailure.run();
            }
        }
    }

}