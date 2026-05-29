# EazyManage

A JavaFX desktop client for school management. Built for Ghanaian schools to handle academics, attendance, results, conduct, finance, and reporting in one place. Talks to a Spring Boot backend over HTTP + WebSocket.

**Status:** v3.0.0 — modernized shell with role-based navigation, four shell-native dashboards, real-time WebSocket presence, offline mode, and PDF/CSV report exports.

---

## What it does

Five roles, four dashboards, a shared shell:

| Role | Sees |
|---|---|
| **Principal** | Everything except the Admin-only audit log. Cross-cutting KPIs (students, staff, fees, collection rate), absentees today, all reports |
| **Administrator** | Academic Setup, staff management, full audit logs, broadcasts, settings. No finance |
| **Accountant** | Finance tab (fees setup, payments, class+student payment reports), dashboard with collection progress, finance reports in Reporting Center |
| **Teaching Staff** | Their assigned class — Attendance, Results entry, Conduct, Students view. Limited dashboard showing class roster preview |
| **General Staff** | Light surface; communication-focused (broadcasts, settings) |

Each role lands on a dashboard tailored to their work, with a sidebar showing only the tabs they have access to.

---

## Quick start

### Prerequisites

- **JDK 21+** (the project targets Java 24 features but compiles on 21; check the `<release>` value in `pom.xml`)
- **Maven 3.9+**
- A running backend at `http://localhost:8080/api` (default). The backend is in a separate Spring Boot repo.

### Run from source

```bash
mvn clean javafx:run
```

That builds and launches the JavaFX app via the `javafx-maven-plugin`.

### Package as a runnable jar

```bash
mvn clean package
java -jar target/EazyManage-3.0.0.jar
```

### Point at a different backend

The backend URL is set in one place: `src/main/java/com/bbquantum/eazymanage/Utitity/UtilityClass.java`:

```java
public static final String BASE_URL = "http://localhost:8080/api";
```

Change that constant and rebuild. (A future improvement is to read it from `~/.eazymanage/config.properties` so re-deploys don't require a rebuild — currently it's a constant.)

### First-time flow

1. Launch the app — the auth screen appears.
2. If your school isn't registered yet: **Register institution** → enter institution name + subscription code → you'll receive a principal-enrollment link.
3. Sign in with the Staff ID + password issued for your role.
4. If your account has multiple roles, the app shows a role-picker before the shell loads. You can switch active role anytime from **Settings**.

---

## Architecture at a glance

### The shell

Everything visual lives in the shell (`com.bbquantum.eazymanage.Shell`). It's a single-window app with a sidebar nav, a top bar, and a workspace area that loads tabs lazily.

```
Shell/
├── AppShellController.java    # Sidebar render, tab dispatch, identity, theming
├── ShellLauncher.java         # Builds the shell stage at relaunch (role switch)
├── Navigation/                # NavRegistry — single source of truth for tabs + role gating
├── Notification/              # NotificationCenter (sidebar bell + history)
├── Dashboard/                 # Four role-specific shell-native dashboards + router
└── Settings/                  # Theme toggle, offline toggle, switch role, sign out
```

The `NavRegistry` lists every tab (`dashboard`, `attendance`, `results`, `students`, `staff`, `conduct`, `reporting-center`, `audit-logs`, `broadcasts`, `finance`, `academic-setup`, `settings`) with a `Set<String>` of allowed roles. The sidebar is rebuilt from this list on every identity change.

### Services layer

Each backend domain has its own service. They all share an `ApiCore` (HTTP client, base URLs, Gson) and `ApiContext` (last error message singleton). Calls return through `Consumer<T>` for success and `Runnable` for failure / network — never via callbacks-into-globals (except the older `AcademicsService.loadClassStudents` which writes `UtilityClass.studentsHolder` for legacy reasons).

```
Services/
├── ApiCore.java              # HttpClient, base URLs, Gson
├── ApiContext.java           # Last error message singleton
├── AuthService.java          # Login, role list, subscription, enrollment
├── DashboardService.java     # Batched loaders for all four role dashboards
├── AcademicsService.java     # Grades, semesters, subjects, scores
├── AttendanceService.java    # Mark/save daily attendance + absentees
├── ConductService.java       # Save/load per-student conduct ratings
├── FinanceService.java       # Fees setup, payment records, fee reports
├── StudentService.java       # Add/edit/list students per class
├── StaffService.java         # Staff CRUD + role assignment
├── ReportsService.java       # Generate PDF/CSV reports server-side
├── LoggingService.java       # Send + fetch audit log entries
├── WebSocketService.java     # STOMP connection, presence tracking
├── PresenceTracker.java      # Live online/offline status per staff
├── OfflineMode.java          # Toggle + state holder
├── OfflineQueue.java         # SQLite-backed queue for offline writes
└── OfflineReplayer.java      # Drains the queue when connectivity returns
```

### DTOs

Plain getter/setter pojos deserialized by Gson. Organized by domain:

```
DTO/
├── Academics/    # Scores, conduct, subjects, classes, semesters, attendance
├── Auth/         # Credentials, role list, subscription
├── Finance/      # Fees, payments, payer info, reports
├── Institution/ # Grades, students, staff, organization
├── Offline/      # Queued mutation envelopes
└── WebSocket/    # Presence + broadcast frames
```

### Theming

Light and dark themes, tokenized:

```
Theme/
├── tokens.css         # Spacing, radii, font sizes (theme-independent)
├── light-theme.css    # -color-* variables for light mode
├── dark-theme.css     # -color-* variables for dark mode
└── components.css     # Shared component classes (.btn, .input, .field-label, .modern-table, …)
```

`ThemeManager` injects `tokens + active theme + components` into every registered Scene. Switching themes from Settings re-applies to all open windows live. Feature-specific stylesheets (`results.css`, `attendance.css`, `finance.css`, etc.) only add bespoke pieces using the shared tokens — so they theme automatically.

---

## Feature tour

### Dashboard
Four role-specific dashboards (one per role) sharing the same KPI/card chrome. Lightweight, real-data, no placeholders. Refresh button reloads from backend; data flows through `DashboardService.load*DashboardData(...)`.

### Attendance
Class+semester picker → daily roster → mark Present/Absent per student. Bulk-mark buttons, save reads back the absentees list for the principal dashboard.

### Results
Three inner tabs: **Enter Scores**, **View Results**, **Class Performance**.
- **Enter Scores** is spreadsheet-style: tab/enter/arrow keys traverse editable cells (CT1, Group Work, CT2, Project, Exam — each /100). Two derived columns (Class 50%, Exam 50%) auto-recompute on every edit and are read-only.
- **View Results** is the read-only viewer with CSV export.
- **Class Performance** shows aggregates per subject.

### Students
Browse by class, add new students, edit details. Class picker cascades into a student picker where applicable.

### Staff
Administrator-only. Browse all staff, add new staff, assign roles + assigned class for teaching staff.

### Conduct
**Six categories × five rating levels** matching the school's paper report card rubric (Regular, Punctual, Physical Appearance, Social, Emotional, Cognitive Skills × Excellent / Very Good / Good / Fair / Not to Expectation). Click a cell to rate. The selected cell fills emerald with a checkmark. Backend renders the PDF.

### Finance (Accountant + Principal only)
Single tab with four cards: **Fees Setup**, **Record Payments**, **Class Payments**, **Student Payments**.
- Fees setup: per class + semester, with both add + update flows on one screen.
- Record payments: class → student cascade picker (no more typing student IDs), amount + payer name + phone. Load existing payments to amend or delete.
- Class/Student payment reports: full tables with CSV export.

### Reporting Center
Catalog of named reports (Class Results, Student Report Card, Grade Payment Records, Student Payment Records, etc.) gated by role. Each report opens its own viewer with class/semester filters and a back button.

### Audit Logs (Administrator only)
Full audit trail with client-side filtering by date, actor, action type, status. Pages through large result sets.

### Broadcasts
Real-time messages via STOMP WebSocket. Role-scoped sending and presence indicators.

### Offline mode
Toggle in Settings. When enabled, mutations (record attendance, save scores, log a payment, etc.) are queued to a local SQLite database (`~/.eazymanage/offline-queue.db`) instead of hitting the backend. `OfflineReplayer` drains the queue in order when you switch back online or when network returns. Reads from the in-memory cache; you can't browse new data while offline.

---

## Project layout

```
EazyManage/
├── pom.xml
├── README.md                                       (this file)
├── src/
│   ├── main/
│   │   ├── java/com/bbquantum/eazymanage/
│   │   │   ├── EazyManage.java                     # Application entry point
│   │   │   ├── CacheClasses/                       # In-memory caches (grades, semesters, staff, …)
│   │   │   ├── Constants/                          # App-wide constants
│   │   │   ├── Controller/                         # FXML controllers, organized by feature
│   │   │   │   ├── Academics/  Attendance/  AuditLogs/
│   │   │   │   ├── Conduct/    Dashboard/   Finance/
│   │   │   │   ├── Report/     Results/     Settings/
│   │   │   │   ├── Staff/      Students/    WebSocket/
│   │   │   │   └── AuthController.java
│   │   │   ├── DTO/                                # Wire-format pojos
│   │   │   ├── Listener/                           # Custom event listeners
│   │   │   ├── Records/                            # Java records (immutable value types)
│   │   │   ├── Reports/                            # PDF generation helpers
│   │   │   ├── Services/                           # Backend integration services
│   │   │   ├── Shell/                              # Shell-native UI (sidebar, dashboards, settings)
│   │   │   ├── Utitity/                            # ThemeManager, NotificationUtil, UtilityClass (legacy globals)
│   │   │   ├── WebSocket/                          # STOMP client wiring
│   │   │   └── module-info.java                    # Java Platform Module System descriptor
│   │   └── resources/com/bbquantum/eazymanage/
│   │       ├── Authentication/                     # auth-container.fxml + auth.css
│   │       ├── Shell/                              # All shell-native FXMLs (grouped by feature)
│   │       ├── Theme/                              # tokens.css, light-theme.css, dark-theme.css, components.css
│   │       └── Assets/                             # Logos, icons
│   └── test/                                       # JUnit 5 tests (sparse)
└── target/                                         # Build output (not in VCS)
```

> **Note:** the package is `com.bbquantum.eazymanage.Utitity` — typo preserved for compatibility (a rename would touch every controller's imports).

---

## Tech stack

- **Java 21+** (records, sealed types, pattern matching, switch expressions)
- **JavaFX 21.0.6** — controls, FXML, CSS
- **Maven** — build, dependency management, `javafx-maven-plugin` for run/package
- **Gson 2.10.1** — JSON serialization
- **Spring messaging + WebSocket** (client-side) — STOMP-over-WebSocket
- **Jackson databind** — used by Spring WebSocket
- **SQLite (xerial JDBC)** — local offline queue
- **JUnit 5** — tests

---

## Conventions that matter

- **Roles are strings**, not enums: `"PRINCIPAL"`, `"ADMINISTRATOR"`, `"ACCOUNTANT"`, `"TEACHING_STAFF"`, `"GENERAL_STAFF"`. Match these exactly when adding role gates.
- **All service methods take `staffId` as the first arg** and follow `(args…, Consumer<T> onSuccess, Runnable onFailure, Runnable onNetworkIssue)` or a similar callback shape. Failures populate `ApiContext.getInstance().errorMessageContainer`.
- **Caches live in `UtilityClass`** as static singletons (`gradeCacheManager`, `semesterCachingManager`, `staffCacheManager`, etc.). Controllers read them directly. This is legacy and being phased out — newer code prefers Consumer callbacks.
- **Tab IDs in `NavRegistry`** are the canonical handle: `"dashboard"`, `"finance"`, `"results"`, … Use these for cross-tab navigation via `AppShellController.openTab(NavRegistry.findById(id).get())`.
- **Long-running calls go through `Platform.runLater`** when updating UI from the service's Task callbacks. ApiCore handles the Task threading; callbacks marshal back to the FX thread.
- **CSS classes** — prefer the shared `.btn`, `.btn-primary`, `.btn-ghost`, `.btn-lg`, `.input`, `.field-label`, `.modern-table`, `.page-title`, `.page-subtitle`, `.card`, `.kpi-card`, `.save-status`, `.empty-state` from `components.css` over bespoke ones. Use `-color-*` tokens for any color so the surface follows light/dark mode.

---

## Where to look when…

| Task | Where |
|---|---|
| Add a new sidebar tab | `Shell/Navigation/NavRegistry.java`, then add dispatch in `AppShellController.contentLoaderFor` |
| Add a new role gate | Update the `Set<String>` in the relevant `NavItem` and the `ReportCatalogue` entry if it's a report |
| Add a new dashboard KPI | The role's controller in `Shell/Dashboard/`, plus the matching FXML |
| Add a new report | `Shell/Reports/ReportCatalogue.java` + new viewer FXML + controller implementing `ReportViewerController` |
| Add a new theme token | `Theme/light-theme.css` AND `Theme/dark-theme.css` (both, same name, different value) |
| Change the backend URL | `Utitity/UtilityClass.java` → `BASE_URL` |
| Add a new offline-queueable mutation | `Services/OfflineQueue.java` (envelope), `OfflineReplayer.java` (replay), and have the caller check `OfflineMode.getInstance().isEnabled()` before hitting the network |

---

## License

Proprietary. Internal to Bynex Technologies.

## Maintainer

Bynex Technologies — `com.bbquantum.eazymanage` is the canonical package.
