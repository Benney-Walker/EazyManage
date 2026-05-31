# EazyManage

A JavaFX desktop client for school management. Built for Ghanaian schools to handle academics, attendance, results, conduct, finance, and reporting in one place. Talks to a Spring Boot backend over HTTP.

**Status:** v3.0.0 — modernized shell with role-based navigation, four shell-native dashboards, offline mode, conduct management, teacher remarks, reporting center, PDF/CSV exports, and a modular service architecture.

---

## Architecture Notes

### Current Communication Model

The application currently communicates with the backend using HTTP APIs only.

Realtime communication (WebSocket/STOMP), presence tracking, active session monitoring, and live dashboard updates are NOT currently implemented.

The application architecture has been organized in a way that allows these capabilities to be added later without major rewrites.

---

## Services Layer

Each backend domain has its own service. They all share an `ApiCore` (HTTP client, base URLs, Gson) and `ApiContext` (last error message singleton).

```
Services/
├── ApiCore.java
├── ApiContext.java
├── AuthService.java
├── DashboardService.java
├── AcademicsService.java
├── AttendanceService.java
├── ConductService.java
├── FinanceService.java
├── StudentService.java
├── StaffService.java
├── ReportsService.java
├── LoggingService.java
├── MessagingService.java
├── OfflineMode.java
├── OfflineQueue.java
└── OfflineReplayer.java
```

---

## Broadcasts

Broadcasts currently operate through standard backend HTTP endpoints.

The desktop application submits:

* audience selection
* role targeting
* class targeting
* email subject/body
* SMS message content

The backend is responsible for delivery.

No realtime push delivery mechanism currently exists.

---

## Offline Mode

Offline mode is fully implemented.

When enabled:

* Attendance submissions are queued locally.
* Score uploads are queued locally.
* Finance/payment mutations are queued locally.

Queued operations are stored in a local SQLite database.

```
~/.eazymanage/offline-queue.db
```

When connectivity returns:

* OfflineReplayer processes queued operations.
* Successful operations are removed from the queue.
* Failed operations remain available for retry.

This allows core school operations to continue during internet outages.

---

## Conduct Management

Conduct evaluation is fully digital.

The system supports:

* Semester-based conduct records
* Six conduct categories
* Five rating levels per category
* Persistent storage through backend APIs
* Report card integration

Teachers no longer need to manually tick printed report cards.

---

## Teacher Remarks

The application supports:

* Per-subject teacher remarks
* Semester remarks
* Report card integration

Persistence depends on backend support.

---

## Reporting Center

The Reporting Center currently supports:

* Class Results
* Student Report Cards
* SBA Reports
* Master Score Sheets
* Finance Reports
* CSV exports
* PDF exports

Reports are generated using backend-provided data and rendered through dedicated report viewers.

---

## Planned Future Enhancements

The architecture is prepared for future additions such as:

* WebSocket/STOMP realtime communication
* Principal live broadcasts
* Active session monitoring
* Presence tracking
* Live attendance updates
* Live dashboard refresh
* Push notifications

These features are not part of the current implementation.
