package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.DTO.Academics.Report.ViewClassSemesterReport;
import com.bbquantum.eazymanage.DTO.Academics.Results.StudentResult;
import com.bbquantum.eazymanage.DTO.Academics.Results.StudentsScoresTable;
import com.bbquantum.eazymanage.DTO.Academics.Subject.AddNewSubject;
import com.bbquantum.eazymanage.DTO.Academics.Subject.SubjectDTO;
import com.bbquantum.eazymanage.DTO.Academics.Subject.SubjectScores;
import com.bbquantum.eazymanage.DTO.Academics.Subject.SubjectsHolder;
import com.bbquantum.eazymanage.DTO.Institution.FindAndUpdateClassInfo;
import com.bbquantum.eazymanage.DTO.Institution.FindSemester;
import com.bbquantum.eazymanage.DTO.Academics.Grading.SaveGradingCriteria;
import com.bbquantum.eazymanage.DTO.Institution.StudentAttendance;
import com.bbquantum.eazymanage.DTO.Institution.StudentsHolder;
import com.bbquantum.eazymanage.DTO.Offline.ScoresPayload;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AcademicsService {

    private final ApiCore core;

    public AcademicsService(ApiCore core) {
        this.core   = core;
    }

    // ====================================================================
    // Subjects — list, fetch, edit, delete
    // ====================================================================

    /** Load all students enrolled in one subject. */
    public void loadSubjectStudents(String subjectId, String staffId,
                                    Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<SubjectScores> task = new Task<>() {
            @Override
            protected SubjectScores call() throws Exception {
                String url = core.STUDENT_URL + core.API_VERSION_1 + "/load-subject-students/" + subjectId;

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

    /** Persist one student's exercise/test/exam scores for a subject. */
    public void saveStudentScores(StudentsScoresTable scores, String subjectId, String semesterId, String staffId,
                                  Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        // Phase 7: short-circuit to the offline queue when in offline mode.
        if (OfflineMode.getInstance().isEnabled()) {
            try {
                String payload = new com.google.gson.Gson().toJson(
                        new ScoresPayload(
                                subjectId, semesterId, staffId, scores));
                OfflineQueue.getInstance().enqueue(OfflineQueue.OP_SAVE_SCORES, payload);

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
                String url = core.STUDENT_URL + core.API_VERSION_1 + "/save-subject-scores";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("studentId", scores.getStudentId());
                requestBody.addProperty("classTest1Score", scores.getClassTest1Score());
                requestBody.addProperty("groupWorkScore", scores.getGroupWorkScore());
                requestBody.addProperty("classTest2Score", scores.getClassTest2Score());
                requestBody.addProperty("projectScore", scores.getProjectScore());
                requestBody.addProperty("classScore", scores.getClassScore());
                requestBody.addProperty("examScore", scores.getExamScore());
                requestBody.addProperty("calculatedExamScore", scores.getCalculatedExamScore());

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
                    return "Successful";
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

    /**
     * Look up one student's results for a semester/class.
     * Result is delivered to {@code facade.studentResult} before {@code onSuccess}.
     */
    public void SearchStudentResults(String studentId, String semesterId, String classId,
                                     Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<StudentResult> task = new Task<>() {
            @Override
            protected StudentResult call() throws Exception {
                String url = core.STUDENT_URL + core.API_VERSION_1 + "/search-student-results" +
                        "?studentId=" + studentId +
                        "&semesterId=" + semesterId +
                        "&classId=" + classId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + core.token)
                        .GET()
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return core.gson.fromJson(response.body(), StudentResult.class);
                }

                if (core.handleSessionExpired(response)) return null;
                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            ApiContext.getInstance().studentResult = task.getValue();
            if (ApiContext.getInstance().studentResult != null) {
                ApiContext.getInstance().successMessageContainer = "Results loaded successfully";
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
            ApiContext.getInstance().errorMessageContainer= "Could not load results";
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
                String url = core.STAFF_URL + core.API_VERSION_2 + "/view-class-results?" +
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


    public void downloadClassReportCards(String staffId, String levelId, String semesterId,
                                         Supplier<Window> parentWindow,
                                         Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {

        Task<byte[]> task = new Task<>() {
            @Override
            protected byte[] call() throws Exception {
                String url = core.ADMIN_URL + core.API_VERSION_2 + "/generate-class-report?" +
                        "&levelId=" + levelId +
                        "&semesterId=" + semesterId;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers(
                                "Authorization", "Bearer " + core.token,
                                "staffId", staffId
                        )
                        .GET()
                        .build();

                HttpResponse<byte[]> response = core.httpClient
                        .send(request, HttpResponse.BodyHandlers.ofByteArray());

                if (response.statusCode() == 200) {
                    return response.body();
                }

                if (core.handleSessionExpired(response)) return null;
                ApiContext.getInstance().errorMessageContainer= new String(response.body(), StandardCharsets.UTF_8);
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

                    Window window = parentWindow.get();
                    File file = chooser.showSaveDialog(window);

                    if (file != null) {
                        Files.write(file.toPath(), pdfBytes);
                        onSuccess.run();
                    } else {
                        onFailure.run();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ApiContext.getInstance().errorMessageContainer= e.getMessage();
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
        thread.setDaemon(true);    // legacy: this one was daemon
        thread.start();
    }

    /*/ ====================================================================
    // Teacher remarks (Phase 4)
    // ====================================================================

    public void saveSubjectTeacherRemark(String studentId, String teacherRemark,
                                         String subjectId, String semesterId, String staffId,
                                         Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.STAFF_URL + core.API_VERSION_1 + "/subject-remark";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("studentId", studentId);
                requestBody.addProperty("teacherRemark", teacherRemark);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "Content-Type", "application/json",
                                "staffId", staffId,
                                "subjectId", subjectId,
                                "semesterId", semesterId)
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String status = task.getValue();
            if (status == null) {
                onFailure.run();
                return;
            }
            ApiContext.getInstance().successMessageContainer = "Teacher remark saved";
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
            ApiContext.getInstance().errorMessageContainer= "Could not save teacher remark";
            onFailure.run();
        });

        new Thread(task).start();
    }

    public void saveSemesterTeacherRemark(String studentId, String semesterRemark,
                                          String semesterId, String staffId,
                                          Runnable onSuccess, Runnable onFailure, Runnable onNetworkIssue) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = core.STAFF_URL + core.API_VERSION_1 + "/semester-remark";

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("studentId", studentId);
                requestBody.addProperty("semesterRemark", semesterRemark);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Authorization", "Bearer " + core.token,
                                "Content-Type", "application/json",
                                "staffId", staffId,
                                "semesterId", semesterId)
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = core.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                }

                core.checkApiResponse(response);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            String status = task.getValue();
            if (status == null) {
                onFailure.run();
                return;
            }
            ApiContext.getInstance().successMessageContainer = "Semester remark saved";
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
            ApiContext.getInstance().errorMessageContainer= "Could not save semester remark";
            onFailure.run();
        });

        new Thread(task).start();
    }*/
}
