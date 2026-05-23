package com.bbquantum.eazymanage.Shell;

import com.bbquantum.eazymanage.Constants.NavItemStatus;
import com.bbquantum.eazymanage.Records.NavItem;
import com.bbquantum.eazymanage.Records.Section;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class NavRegistry {

    // ---- role bundles (read-only) ----
    private static final Set<String> ALL                  = Set.of("PRINCIPAL", "ADMINISTRATOR", "ACCOUNTANT", "TEACHING_STAFF");
    private static final Set<String> PRINCIPAL_ADMIN      = Set.of("PRINCIPAL", "ADMINISTRATOR");
    private static final Set<String> ADMIN_ONLY           = Set.of("ADMINISTRATOR");
    private static final Set<String> ACCOUNTANT_PRINCIPAL = Set.of("PRINCIPAL", "ACCOUNTANT");
    private static final Set<String> NOT_ACCOUNTANT       = Set.of("PRINCIPAL", "ADMINISTRATOR", "TEACHING_STAFF");

    private static final List<Section> SECTIONS = List.of(
            new Section("OPERATIONS", List.of(
                    new NavItem("dashboard", "Dashboard", "\u25A4", ALL, NavItemStatus.FUNCTIONAL, ""),
                    new NavItem("attendance",  "Attendance",  "\u2713", NOT_ACCOUNTANT,       NavItemStatus.PLACEHOLDER, "Phase 8"),
                    new NavItem("results",     "Results",     "\u2605", NOT_ACCOUNTANT,       NavItemStatus.PLACEHOLDER, "Phase 4–5"),
                    new NavItem("finance",     "Finance",     "\u25C6", ACCOUNTANT_PRINCIPAL, NavItemStatus.PLACEHOLDER, "Phase 7–8"),
                    new NavItem("students",    "Students",    "\u25CF", ADMIN_ONLY,           NavItemStatus.PLACEHOLDER, "Phase 8"),
                    new NavItem("staff",       "Staff",       "\u25B2", ADMIN_ONLY,           NavItemStatus.PLACEHOLDER, "Phase 8"),
                    new NavItem("conduct",     "Conduct",     "\u2691", NOT_ACCOUNTANT,       NavItemStatus.FUNCTIONAL,  "")
            )),

            new Section("RECORDS", List.of(
                    new NavItem("reports",     "Reporting Center", "\u25A6", ALL,             NavItemStatus.PLACEHOLDER, "Phase 5"),
                    new NavItem("audit-logs",  "Audit Logs",  "\u25C8", ADMIN_ONLY,           NavItemStatus.PLACEHOLDER, "Phase 6")
            )),

            new Section("COMMUNICATION", List.of(
                    new NavItem("broadcasts",      "Broadcasts",      "\u25CA", PRINCIPAL_ADMIN, NavItemStatus.FUNCTIONAL, ""),
                    new NavItem("active-sessions", "Active Sessions", "\u25CB", ALL,             NavItemStatus.FUNCTIONAL, "")
            )),

            new Section("SYSTEM", List.of(
                    new NavItem("settings",    "Settings",    "\u2699", ALL,                  NavItemStatus.PLACEHOLDER, "Phase 8")
            ))
    );

    private NavRegistry() {}

    /**
     * Sections containing only items visible to the given role.
     * A section that becomes empty after filtering is dropped entirely so the
     * sidebar never renders a heading over zero items.
     */
    public static List<Section> sectionsFor(String role) {
        if (role == null) return List.of();

        List<Section> result = new ArrayList<>();
        for (Section section : SECTIONS) {
            List<NavItem> visible = section.items().stream()
                    .filter(i -> i.availableFor(role))
                    .toList();

            if (!visible.isEmpty()) {
                result.add(new Section(section.title(), visible));
            }
        }

        return result;
    }

    // Lookup by id — used when re-opening a tab from a notification or shortcut.
    public static Optional<NavItem> findById(String id) {
        if (id == null) return Optional.empty();

        for (Section section : SECTIONS) {
            for (NavItem item : section.items()) {
                if (item.id().equals(id)) {
                    return Optional.of(item);
                }
            }
        }

        return Optional.empty();
    }
}
