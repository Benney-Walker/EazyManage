package com.bbquantum.eazymanage.Reports;

import com.bbquantum.eazymanage.Constants.ReportStatus;
import com.bbquantum.eazymanage.Records.ReportDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ReportCatalogue {

    private static final Set<String> ALL            = Set.of("PRINCIPAL", "ADMINISTRATOR", "ACCOUNTANT", "TEACHING_STAFF");
    private static final Set<String> NOT_ACCOUNTANT = Set.of("PRINCIPAL", "ADMINISTRATOR", "TEACHING_STAFF");

    private ReportCatalogue() {}

    private static final List<ReportDefinition> REPORTS = List.of(
            new ReportDefinition(
                    "class-results",
                    "Class Results",
                    "End-of-term performance summary for one class and semester. Includes per-student totals, position, and per-subject breakdown.",
                    "\u25A6",
                    NOT_ACCOUNTANT,
                    ReportStatus.AVAILABLE,
                    "",
                    "/com/bbquantum/eazymanage/Shell/ClassResultsReport.fxml"
            ),
            new ReportDefinition(
                    "sba",
                    "School-Based Assessment (SBA)",
                    "Continuous-assessment record per student per semester: Class Test 1, Group Work, Class Test 2, Project, Class Score, Exam, and Calculated Exam Score.",
                    "\u25A4",
                    NOT_ACCOUNTANT,
                    ReportStatus.AVAILABLE,
                    "Phase 5b",
                    "/com/bbquantum/eazymanage/Shell/SbaReport.fxml"
            ),
            new ReportDefinition(
                    "master-score-sheet",
                    "Master Score Sheet",
                    "Wide spreadsheet view of every student in a class with every subject's total, used by class teachers at end-of-term.",
                    "\u25A8",
                    NOT_ACCOUNTANT,
                    ReportStatus.AVAILABLE,
                    "",
                    "/com/bbquantum/eazymanage/Shell/MasterSheetReport.fxml"
            ),
            new ReportDefinition(
                    "report-card",
                    "Student Report Card",
                    "Individual report card for one student for one semester, with conduct, teacher remarks, and headmaster sign-off.",
                    "\u25A1",
                    NOT_ACCOUNTANT,
                    ReportStatus.FUTURE,
                    "Phase 5d",
                    null
            )
    );

    /** Subset visible to the given role. Future cards are kept so users see what's coming. */
    public static List<ReportDefinition> reportsFor(String role) {
        if (role == null) return List.of();
        List<ReportDefinition> visible = new ArrayList<>();
        for (ReportDefinition r : REPORTS) {
            if (r.availableFor(role)) visible.add(r);
        }
        return visible;
    }

    public static Optional<ReportDefinition> findById(String id) {
        if (id == null) return Optional.empty();
        for (ReportDefinition r : REPORTS) {
            if (r.id().equals(id)) return Optional.of(r);
        }
        return Optional.empty();
    }
}
