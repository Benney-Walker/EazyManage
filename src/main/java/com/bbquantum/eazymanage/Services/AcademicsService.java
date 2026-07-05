package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.DTO.Academics.Holiday.Holiday;
import com.bbquantum.eazymanage.DTO.Academics.Report.ViewClassSemesterReport;
import com.bbquantum.eazymanage.DTO.Academics.Results.StudentsScoresTable;
import com.bbquantum.eazymanage.DTO.Academics.Subject.AddNewSubject;
import com.bbquantum.eazymanage.DTO.Academics.Subject.SubjectDTO;
import com.bbquantum.eazymanage.DTO.Academics.Subject.SubjectScores;
import com.bbquantum.eazymanage.DTO.Academics.Subject.SubjectsHolder;
import com.bbquantum.eazymanage.DTO.System.FindAndUpdateClassInfo;
import com.bbquantum.eazymanage.DTO.System.FindSemester;
import com.bbquantum.eazymanage.DTO.Academics.Grading.SaveGradingCriteria;
import com.bbquantum.eazymanage.DTO.Academics.Students.StudentsHolder;
import com.bbquantum.eazymanage.DTO.Offline.ScoresPayload;
import com.bbquantum.eazymanage.Offline.OfflineMode;
import com.bbquantum.eazymanage.Offline.OfflineQueue;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class AcademicsService {

    private final ApiCore core;

    public AcademicsService(ApiCore core) {
        this.core   = core;
    }

    // ====================================================================
    // Subjects — list, fetch, edit, delete
    // ====================================================================

    /** Load all students enrolled in one subject. */
    public void loadSubjectStudents(String staffId, String subjectId, String semesterId,
                                    Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        System.out.println("SubjectId: " + subjectId + " staffId: " + staffId);
        Task<SubjectScores> task = new Task<>() {
            @Override
            protected SubjectScores call() throws Exception {
                String url = core.STUDENT_URL + core.API_VERSION_1 + "/load-subject-students/"
                        + subjectId + "/" + semesterId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), SubjectScores.class);
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            if (UtilityClass.subjectScores != null) {
                UtilityClass.subjectScores = null;          // legacy explicit reset
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

    /** Add a new subject and assign it to a grade/level. */
    public void addNewSubject(AddNewSubject assignment, String staffId,
                              Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_1 + "/add-subject";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("subjectName", assignment.getSubjectName());
                requestBody.addProperty("gradeId", assignment.getGradeId());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "Content-Type", "application/json",
                                "staffId",  staffId)
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);

                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response == null) {
                onFailure.run();
                return;
            }

            ApiContext.getInstance().successMessageContainer = "Subject added successfully";
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

    /** Look up the editable record for one subject. */
    public void fetchSubjectData(String staffId, String subjectId,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<SubjectDTO> task = new Task<>() {
            @Override
            protected SubjectDTO call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_1 + "/load-subject-data/" +
                        subjectId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), SubjectDTO.class);
                }

                core.checkApiResponse(response);
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

            ApiContext.getInstance().errorMessageContainer = "Could not get Records";
            onFailure.run();
        });

        new Thread(task).start();
    }

    /** Persist edits to a subject. */
    public void updateSubjectDetails(SubjectDTO data, String staffId,
                                     Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_1 + "/update-subject-details";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("subjectId", data.getSubjectId());
                requestBody.addProperty("subjectName", data.getSubjectName());
                requestBody.addProperty("levelId", data.getLevelId());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "Content-Type", "application/json",
                                "staffId", staffId)
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return "Subject successfully updated";
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {

            ApiContext.getInstance().successMessageContainer = task.getValue();
            if (ApiContext.getInstance().successMessageContainer != null) {
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

            ApiContext.getInstance().errorMessageContainer = "Could not update Records";
            onFailure.run();
        });

        new Thread(task).start();
    }

    /** Delete a subject. */
    public void deleteSubject(String staffId, String subjectId,
                              Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_1 + "/delete-subject/" + staffId + "/" + subjectId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .DELETE()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
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

            onFailure.run();
        });

        new Thread(task).start();
    }

    /** Load every subject for one class/level. */
    public void loadClassSubjects(String levelId, String staffId,
                                  Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Type listType = new TypeToken<List<SubjectsHolder>>() {}.getType();

        Task<List<SubjectsHolder>> task = new Task<>() {
            @Override
            protected List<SubjectsHolder> call() throws Exception {
                String url = core.STAFF_URL + core.API_VERSION_1 + "/load-subjects/" + levelId;

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

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            if (UtilityClass.subjectsHolder != null && !UtilityClass.subjectsHolder.isEmpty()) {
                UtilityClass.subjectsHolder.clear();
            }
            UtilityClass.subjectsHolder = task.getValue();
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

            ApiContext.getInstance().errorMessageContainer = "Could not get Records";
            onFailure.run();
        });

        new Thread(task).start();
    }

    // ====================================================================
    // Classes / Grades
    // ====================================================================

    /** Create a new grade (class/level) and assign an instructor. */
    public void addNewGrade(String staffId, String gradeName, String instructorId,
                            Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_1 + "/add-new-class" +
                        "?gradeName=" + gradeName +
                        "&instructorId=" + instructorId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return "Class added successfully";
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String status = task.getValue();
            if (status != null) {

                ApiContext.getInstance().successMessageContainer = status;
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

            ApiContext.getInstance().errorMessageContainer = "Could not add new grade";
            onFailure.run();
        });

        new Thread(task).start();
    }

    /** Look up the editable record for one class. */
    public void findClassInfo(String levelId, String staffId,
                              Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<FindAndUpdateClassInfo> task = new Task<>() {
            @Override
            protected FindAndUpdateClassInfo call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_1 + "/find-class-info/" + levelId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "staffId", staffId)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), FindAndUpdateClassInfo.class);
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
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

            ApiContext.getInstance().errorMessageContainer = "Could not fetch class Info";
            onFailure.run();
        });

        new Thread(task).start();
    }

    /** Persist edits to a class. */
    public void updateClassInfo(FindAndUpdateClassInfo updateInfo, String staffId,
                                Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_1 + "/update-class-info";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("levelId", updateInfo.getLevelId());
                requestBody.addProperty("levelName", updateInfo.getLevelName());
                requestBody.addProperty("staffId", updateInfo.getStaffId());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "Content-Type", "application/json",
                                "staffId", staffId)
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return "Class info updated successfully";
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String result = task.getValue();
            if (result == null) {
                onFailure.run();
                return;
            }

            ApiContext.getInstance().successMessageContainer = result;
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

    /** Load all students in one class. */
    public void loadClassStudents(String levelId, String staffId,
                                  Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Type listType = new TypeToken<List<StudentsHolder>>() {}.getType();

        Task<List<StudentsHolder>> task = new Task<>() {
            @Override
            protected List<StudentsHolder> call() throws Exception {
                String url = core.STUDENT_URL + core.API_VERSION_1 + "/load-students/" + levelId;

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

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            if (UtilityClass.studentsHolder != null && !UtilityClass.studentsHolder.isEmpty()) {
                UtilityClass.studentsHolder.clear();
            }
            UtilityClass.studentsHolder = task.getValue();
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

            ApiContext.getInstance().errorMessageContainer = "Could not get Records";
            onFailure.run();
        });

        new Thread(task).start();
    }

    /** Promote all passing students out of a class. */
    public void promoteStudent(String studentId, String levelId, String staffId,
                                   Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.STAFF_URL + core.API_VERSION_1 + "/promote-student/"
                        + studentId + "/" + levelId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers(
                                "Authorization", "Bearer " + core.token,
                                "staffId", staffId
                        )
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return "Promoted successfully";
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {

            ApiContext.getInstance().successMessageContainer = task.getValue();
            if (ApiContext.getInstance().successMessageContainer == null) {
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

            ApiContext.getInstance().errorMessageContainer = "Could not move passed students";
            onFailure.run();
        });

        new Thread(task).start();
    }

    // ====================================================================
    // Semesters
    // ====================================================================

    /** Create a new semester. */
    public void addNewSemester(String staffId, String semesterName, LocalDate startDate,
                               LocalDate endDate, String academicYear,
                               Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_1 + "/add-semester";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("semesterName", semesterName);
                requestBody.addProperty("startDate", startDate.toString());
                requestBody.addProperty("endDate", endDate.toString());
                requestBody.addProperty("academicYear", academicYear);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "Content-Type", "application/json",
                                "staffId", staffId)
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return "semester saved Successfully";
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
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

            ApiContext.getInstance().errorMessageContainer = "Could not save Semester";
            onFailure.run();
        });

        new Thread(task).start();
    }

    /**
     * Look up the editable record for one semester.
     *
     * <p>Legacy code wrote the parsed value to {@code adminController.semester};
     * the facade supplies that field setter via {@code onResult}.
     */
    public void searchSemester(String semesterId, String staffId,
                               Consumer<FindSemester> onResult,
                               Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<FindSemester> task = new Task<>() {
            @Override
            protected FindSemester call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_1 + "/search-semester/" + semesterId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers(
                                "Authorization", "Bearer " + core.token,
                                "staffId", staffId
                        )
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), FindSemester.class);
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            FindSemester result = task.getValue();
            onResult.accept(result);
            if (result == null) {
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

            ApiContext.getInstance().errorMessageContainer = "Could not get Semester Data";
            onFailure.run();
        });

        new Thread(task).start();
    }

    /** Persist edits to a semester. */
    public void updateSemesterInfo(FindSemester info, String staffId,
                                   Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_1 + "/update-semester-info";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("semesterID", info.getSemesterID());
                requestBody.addProperty("semesterName", info.getSemesterName());
                requestBody.addProperty("semesterStartDate", info.getSemesterStartDate());
                requestBody.addProperty("semesterEndDate", info.getSemesterEndDate());
                requestBody.addProperty("academicYear", info.getAcademicYear());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers(
                                "Authorization", "Bearer " + core.token,
                                "Content-Type", "application/json",
                                "staffId", staffId
                        )
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return "Successfully updated Semester information";
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response == null) {
                onFailure.run();
                return;
            }

            ApiContext.getInstance().successMessageContainer = response;
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
            ApiContext.getInstance().errorMessageContainer= "Could not update semester info";
            onFailure.run();
        });

        new Thread(task).start();
    }

    // ====================================================================
    // Scores, grading criteria, results
    // ====================================================================


    public void saveStudentScores(String staffId, String subjectId, String semesterId, List<StudentsScoresTable> scoresList,
                                  Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        if (OfflineMode.getInstance().isEnabled()) {
            try {
                for (StudentsScoresTable score : scoresList) {
                    String payload = new com.google.gson.Gson().toJson(
                            new ScoresPayload(
                                    subjectId, semesterId, staffId, score));
                    OfflineQueue.getInstance().enqueue(OfflineQueue.OP_SAVE_SCORES, payload);
                }

                ApiContext.getInstance().successMessageContainer = "Saved offline — will sync when online";
                Platform.runLater(onSuccess);
            } catch (Exception e) {
                e.printStackTrace();

                ApiContext.getInstance().errorMessageContainer= "Could not queue scores: " + e.getMessage();
                Platform.runLater(onFailure);
            }
            return;
        }

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.STUDENT_URL + core.API_VERSION_1 + "/save-scores";

                JsonArray requestBody = new JsonArray();
                Gson gson = new Gson();
                if (scoresList != null && !scoresList.isEmpty()) {
                    for (StudentsScoresTable scores : scoresList) {
                        requestBody.add(gson.toJsonTree(scores));
                    }
                }

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "Content-Type", "application/json",
                                "staffId", staffId,
                                "subjectId", subjectId,
                                "semesterId", semesterId)
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return "Successfully saved scores";
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
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

    /** Persist the grading criteria (letter-grade ranges). */
    public void saveGradingCriteria(String staffId, SaveGradingCriteria saveGradingCriteria,
                                    Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_1 + "/new-grading-criteria";

                JsonObject responseBody = new JsonObject();
                responseBody.addProperty("id", saveGradingCriteria.getId());
                responseBody.addProperty("lowerRange", saveGradingCriteria.getLowerRange());
                responseBody.addProperty("upperRange", saveGradingCriteria.getUpperRange());
                responseBody.addProperty("grade", saveGradingCriteria.getGrade());
                responseBody.addProperty("gradeDescription", saveGradingCriteria.getGradeDescription());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "Content-Type", "application/json",
                                "staffId", staffId)
                        .POST(HttpRequest.BodyPublishers.ofString(responseBody.toString()))
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return "Successful";
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response != null) {
                ApiContext.getInstance().successMessageContainer = response;
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
            ApiContext.getInstance().errorMessageContainer= "Could not add Grade criteria";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void loadGradingCriteria(String staffId, Consumer<List<SaveGradingCriteria>> onResult,
                                    Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Type listType = new TypeToken<List<SaveGradingCriteria>>() {}.getType();

        Task<List<SaveGradingCriteria>> task = new Task<>() {
            @Override
            protected List<SaveGradingCriteria> call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_1 + "/load-grading-criteria";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers(
                                "Authorization", "Bearer " + core.token,
                                "staffId", staffId
                        )
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), listType);
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<SaveGradingCriteria> result = task.getValue();
            onResult.accept(result);            // controller field assignment happens in the consumer
            if (result == null) {
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

    /** Load the class-level results table for one semester. */
    public void loadClassResults(String levelId, String semesterId, String staffId,
                                 Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Type listType = new TypeToken<List<ViewClassSemesterReport>>() {}.getType();

        Task<List<ViewClassSemesterReport>> task = new Task<>() {
            @Override
            protected List<ViewClassSemesterReport> call() throws Exception {
                String url = core.STAFF_URL + core.API_VERSION_1 + "/view-class-results?" +
                        "levelId=" + levelId +
                        "&semesterId=" + semesterId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers(
                                "Authorization", "Bearer " + core.token,
                                "staffId", staffId
                        )
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), listType);
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
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

    public void addNewHoliday(String staffId, Holiday holiday,
                              Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_1 + "/add-holiday";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("holidayId", holiday.getHolidayId());
                requestBody.addProperty("holidayName", holiday.getHolidayName());
                requestBody.addProperty("startDate",   holiday.getStartDate());
                requestBody.addProperty("endDate",     holiday.getEndDate());
                requestBody.addProperty("semesterId",  holiday.getSemesterId());
                requestBody.addProperty("semesterName",  holiday.getSemesterName());
                requestBody.addProperty("academicYear",  holiday.getAcademicYear());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "Content-Type", "application/json",
                                "staffId", staffId)
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return "Holiday added successfully";
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response == null) { onFailure.run(); return; }
            ApiContext.getInstance().successMessageContainer = response;
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            if (ex != null) ex.printStackTrace();
            String msg = ex == null ? null : ex.getMessage();
            if (ex instanceof java.net.ConnectException
                    || (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }
            ApiContext.getInstance().errorMessageContainer = "Could not save holiday";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void loadAllHolidays(String staffId,
                                Consumer<List<Holiday>> onResult,
                                Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Type listType = new TypeToken<List<Holiday>>(){}.getType();

        Task<List<Holiday>> task = new Task<>() {
            @Override
            protected List<Holiday> call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_1 + "/load-holidays";

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

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            List<Holiday> result = task.getValue();
            if (result != null) {
                onResult.accept(result);
                onSuccess.run();
                return;
            }
            onFailure.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            if (ex != null) ex.printStackTrace();
            String msg = ex == null ? null : ex.getMessage();
            if (ex instanceof java.net.ConnectException
                    || (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }
            ApiContext.getInstance().errorMessageContainer = "Could not load holidays";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void updateHoliday(String staffId, Holiday holiday,
                              Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_1 + "/update-holiday";

                JsonObject body = new JsonObject();
                body.addProperty("holidayId", holiday.getHolidayId());
                body.addProperty("holidayName", holiday.getHolidayName());
                body.addProperty("startDate", holiday.getStartDate());
                body.addProperty("endDate", holiday.getEndDate());
                body.addProperty("semesterId", holiday.getSemesterId());
                body.addProperty("semesterName", holiday.getSemesterName());
                body.addProperty("academicYear", holiday.getAcademicYear());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "Content-Type", "application/json",
                                "staffId", staffId)
                        .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return "Holiday updated successfully";
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String response = task.getValue();
            if (response == null) { onFailure.run(); return; }
            ApiContext.getInstance().successMessageContainer = response;
            onSuccess.run();
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            if (ex != null) ex.printStackTrace();
            String msg = ex == null ? null : ex.getMessage();
            if (ex instanceof java.net.ConnectException
                    || (msg != null && msg.contains("Connection refused"))) {
                onNetworkIssue.run();
                return;
            }
            ApiContext.getInstance().errorMessageContainer = "Could not update holiday";
            onFailure.run();
        });

        new Thread(task).start();
    }
}
