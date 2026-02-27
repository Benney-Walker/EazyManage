package com.bbquantum.eazymanage.Controller;

import com.bbquantum.eazymanage.Constants.StudentStatus;
import com.bbquantum.eazymanage.DTO.Academics.StudentsScoresTable;
import com.bbquantum.eazymanage.DTO.Academics.SubjectsHolder;
import com.bbquantum.eazymanage.DTO.Institution.GradeCaching;
import com.bbquantum.eazymanage.DTO.Institution.LogsDetails;
import com.bbquantum.eazymanage.DTO.Institution.SemesterCaching;
import com.bbquantum.eazymanage.DTO.Institution.StudentsHolder;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class AdminController {

    @FXML public StackPane notification;

    // NAV SIDE
    @FXML public Label versionLabel;

    @FXML private Button dashboardButton;
    @FXML private Button studentNavBtn;
    @FXML private Button scoresNavBtn;
    @FXML private Button subjectNavBtn;
    @FXML private Button reportsNavBtn;
    @FXML private Button staffNavButton;
    @FXML private Button logsNavBtn;

    @FXML private Label adminNameLabel;
    @FXML private Label institutionalNameLabel;
    @FXML private Button logoutButton;

    //MAIN CONTENT
    @FXML private StackPane contentArea;

    //DASHBOARD VIEW
    @FXML private ScrollPane dashboardView;
    @FXML private Label dateTimeLabel;
    @FXML private Label activeSessionsLabel;
    @FXML private Label notificationsLabel;
    @FXML private TableView<LogsDetails> recentActivitiesTable;
    @FXML private TableColumn<LogsDetails, String> activitiesIdCol;
    @FXML private TableColumn<LogsDetails, String> activityTypeCol;
    @FXML private TableColumn<LogsDetails, String> createdByCol;
    @FXML private TableColumn<LogsDetails, String> activityDateCol;
    @FXML private TableColumn<LogsDetails, String> activityTimeCol;
    @FXML private TableColumn<LogsDetails, String> actionCol;
    @FXML private TableColumn<LogsDetails, String> activityStatusCol;

    //STUDENTS PAGE
    @FXML private ScrollPane addNewStudentView;

    @FXML private Button addStudentToggleBtn;
    @FXML private Button updateStudentToggleBtn;

    @FXML private StackPane studentInfoCardContainer;

    @FXML private VBox addStudentCard;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private TextField homeTownField;
    @FXML private TextField parentNameField;
    @FXML private TextField parentPhoneField;
    @FXML private ComboBox<GradeCaching> classComboBox;
    @FXML private Button saveAndClearBtn;

    @FXML private VBox updateStudentInfoCard;
    @FXML private VBox studentSearchFieldBox;
    @FXML private ComboBox<StudentsHolder> student_SelectionBox;
    @FXML private ComboBox<StudentsHolder> student_classSelectionBox;
    @FXML private Button findStudentBtn;
    @FXML private VBox personalInfo;
    @FXML private Label studentIdLabel;
    @FXML private Label studentStatusLabel;
    @FXML private Label updateGradeNameLabel;
    @FXML private TextField updateFirstNameField;
    @FXML private TextField updateLastNameField;
    @FXML private ComboBox<String> updateGenderComboBox;
    @FXML private DatePicker updateDateOfBirthPicker;
    @FXML private TextField updateHomeTownField;
    @FXML private VBox guardianInfo;
    @FXML private TextField updateParentNameField;
    @FXML private TextField updateParentPhoneField;
    @FXML private VBox gradeInfo;
    @FXML private ComboBox<GradeCaching> updateClassComboBox;
    @FXML private VBox statusInfo;
    @FXML private ComboBox<StudentStatus> updateStatusComboBox;
    @FXML private HBox updateButtonVBox;
    @FXML private Button updateStudentInfoBtn;

    //SUBJECT SCORES VIEW
    @FXML private ScrollPane subjectScoresView;

    @FXML private VBox subjectResultSearchBox;
    @FXML private ComboBox<SubjectsHolder> score_selectSubjectBox;
    @FXML private ComboBox<GradeCaching> score_SelectClassBox;
    @FXML private Button score_LoadStudentsBtn;

    @FXML private HBox subjectInfoBox;
    @FXML private Label subjectNameLabel;
    @FXML private Label subjectCodeLabel;

    @FXML private VBox noStudentsBox;

    @FXML private VBox resultsTableContainer;
    @FXML public TableView<StudentsScoresTable> studentsScoresTable;
    @FXML private TableColumn<StudentsScoresTable, String> studentIdColumn;
    @FXML private TableColumn<StudentsScoresTable, String> studentNameColumn;
    @FXML private TableColumn<StudentsScoresTable, String> exercise1Column;
    @FXML private TableColumn<StudentsScoresTable, String> classTestColumn;
    @FXML private TableColumn<StudentsScoresTable, String> exercise2Column;
    @FXML private TableColumn<StudentsScoresTable, String> projectColumn;
    @FXML private TableColumn<StudentsScoresTable, String> classScoreColumn;
    @FXML private TableColumn<StudentsScoresTable, String> examScoreColumn;
    @FXML private TableColumn<StudentsScoresTable, String> calExamScoreColumn;
    @FXML private Button saveScores;

    @FXML private VBox saveScoresSemesterVBox;
    @FXML private ComboBox<SemesterCaching> saveScoresSemesterComboBox;

    //SUBJECT PAGE
    @FXML private ScrollPane manageSubjectView;

    @FXML private Button addSubjectToggleBtn;
    @FXML private Button updateSubjectToggleBtn;

    @FXML private StackPane subjectCardContainer;

    @FXML private VBox addNewSubjectCard;
    @FXML private TextField subjectNameField;
    @FXML private ComboBox<GradeCaching> gradeComboBox;
    @FXML private Button saveSubjectBtn;

    @FXML private VBox updateSubjectsCard;
    @FXML private VBox subjectSearchBox;
    @FXML private ComboBox<SubjectsHolder> subjectSelectionBox;
    @FXML private ComboBox<GradeCaching> subject_gradeSelectionBox;
    @FXML public Button fetchSubjectBtn;

    @FXML private VBox subjectSearchResponsePanel;
    @FXML private Label subjectIdLabel;
    @FXML private TextField subject_updateNameField;
    @FXML private ComboBox<GradeCaching> subject_updateGradeBox;
    
}
