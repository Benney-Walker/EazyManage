package com.bbquantum.eazymanage.Shell;

import com.bbquantum.eazymanage.Constants.NavItemStatus;
import com.bbquantum.eazymanage.Controller.Academics.AcademicSetupTabController;
import com.bbquantum.eazymanage.Controller.Attendance.AttendanceTabController;
import com.bbquantum.eazymanage.Controller.AuditLogs.AuditLogsController;
import com.bbquantum.eazymanage.Controller.Conduct.ConductController;
import com.bbquantum.eazymanage.Controller.Finance.FinanceTabController;
import com.bbquantum.eazymanage.Controller.Messaging.BroadcastTabController;
import com.bbquantum.eazymanage.Controller.Report.ReportingCenterController;
import com.bbquantum.eazymanage.Controller.Results.ResultsTabController;
import com.bbquantum.eazymanage.Controller.Settings.SettingsController;
import com.bbquantum.eazymanage.Controller.Staff.StaffTabController;
import com.bbquantum.eazymanage.Controller.Students.StudentsTabController;
import com.bbquantum.eazymanage.EazyManage;
import com.bbquantum.eazymanage.Records.NavItem;
import com.bbquantum.eazymanage.Records.Section;
import com.bbquantum.eazymanage.Services.ApiContext;
import com.bbquantum.eazymanage.Offline.OfflineMode;
import com.bbquantum.eazymanage.Offline.OfflineQueue;
import com.bbquantum.eazymanage.Services.OfflineReplayer;
import com.bbquantum.eazymanage.Shell.Navigation.NavRegistry;
import com.bbquantum.eazymanage.Shell.Navigation.WorkSpaceTab;
import com.bbquantum.eazymanage.Shell.Notification.Notification;
import com.bbquantum.eazymanage.Shell.Notification.NotificationCenter;
import com.bbquantum.eazymanage.Utitity.ThemeManager;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AppShellController {

    // ====================================================================
    // FXML injected nodes
    // ====================================================================

    @FXML private VBox sidebar;
    @FXML private Label      brandLabel;
    @FXML private Label orgLabel;
    @FXML private ScrollPane navScroll;
    @FXML private VBox       navContainer;
    @FXML private Label      userInitialsLabel;
    @FXML private Label      userNameLabel;
    @FXML private Label      userRoleLabel;
    @FXML private Button logoutButton;

    @FXML private Button    sidebarToggle;
    @FXML private TextField searchField;
    @FXML private StackPane notifBellWrapper;
    @FXML private Button    notifBell;
    @FXML private Label     notifBellCount;
    @FXML private Button    themeToggle;
    @FXML private Button    offlineToggle;

    @FXML private HBox tabBar;
    @FXML private StackPane contentArea;

    // ====================================================================
    // Identity (set via setIdentity before boot())
    // ====================================================================

    private String role;
    private String staffId;
    private String organizationName;
    private String username;

    /** Polling timer; nullable so we can defensively stop it during logout. */
    private Timeline sessionWatcher;

    private boolean sessionExpiryHandled = false;

    // ====================================================================
    // Internal state
    // ====================================================================

    /** Open tabs in insertion order. LinkedHashMap preserves the visible order. */
    private final Map<String, WorkSpaceTab> openTabs = new LinkedHashMap<>();

    /** The currently focused tab's id, or null if no tab is open. */
    private String activeTabId;

    /** Cached rendered tab-bar buttons for each open tab id (so we can re-style on focus changes). */
    private final Map<String, Node> tabHeaderNodes = new HashMap<>();

    /** Cached rendered sidebar buttons keyed by NavItem id (for active-state updates). */
    private final Map<String, Button> sidebarItemNodes = new HashMap<>();

    /** Lazily constructed notification popup. */
    private Popup notifPopup;

    /** Tracks whether the sidebar is currently in its collapsed state. */
    private boolean sidebarCollapsed = false;

    @FXML
    private void initialize() {
        // Bell badge — bound to NotificationCenter.unreadCount.
        // Number renders as the literal value up to 9, "9+" beyond, hidden when 0.

        NotificationCenter notificationCenter = NotificationCenter.getInstance();
        notifBellCount.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    int count = notificationCenter.unreadCount();
                    if (count <= 0) return "";
                    if (count > 9) return "9+";
                    return Integer.toString(count);
                },
                notificationCenter.unreadCountProperty()
        ));

        notifBellCount.visibleProperty().bind(notificationCenter.unreadCountProperty().greaterThan(0));
        notifBellCount.managedProperty().bind(notifBellCount.visibleProperty());

        // Theme toggle icon stays in sync with the actual current theme.
        updateThemeToggleGlyph();
        ThemeManager.getInstance().addListener(t -> updateThemeToggleGlyph());

        // Phase 7: bind the offline toggle. Label reflects mode + pending count;
        // styleClass switches so the badge can be styled differently in each state.
        OfflineMode offline = OfflineMode.getInstance();
        OfflineQueue queue = OfflineQueue.getInstance();

        offlineToggle.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    int n = queue.pendingCount();
                    String base = offline.isEnabled() ? "Offline" : "Online";
                    return n > 0 ? base + " · " + n + " pending" : base;
                },
                offline.enabledProperty(), queue.pendingCountProperty()));

        // Drive style via a single style-class swap. The CSS in shell.css
        // hooks .topbar-offline-toggle-on for the offline state.
        Runnable refreshStyle = () -> {
            offlineToggle.getStyleClass().removeAll("topbar-offline-toggle-on");
            if (offline.isEnabled()) offlineToggle.getStyleClass().add("topbar-offline-toggle-on");
        };
        refreshStyle.run();
        offline.enabledProperty().addListener((o, was, is) -> {
            refreshStyle.run();
            // Coming back online: drain whatever's queued.
            if (was && !is) {
                OfflineReplayer.getInstance().setStaffId(staffId);
                OfflineReplayer.getInstance().drainNow();
            }
        });

        searchField.setDisable(true);
        searchField.setPromptText("Global search (coming soon)");
    }

    @FXML
    private void onToggleOffline() {
        OfflineMode.getInstance().toggle();
    }

    public void setIdentity(String role, String staffId, String organizationName, String username) {
        this.role             = role;
        this.staffId          = staffId;
        this.organizationName = organizationName;
        this.username         = username;
    }

    public void boot() {
        if (role == null) {
            throw new IllegalStateException("AppShellController.boot() called before setIdentity().");
        }

        // Header / footer identity labels
        brandLabel.setText("EazyManage");
        orgLabel.setText(safeText(organizationName));
        userNameLabel.setText(safeText(username));
        userRoleLabel.setText(prettifyRole(role));
        userInitialsLabel.setText(initialsOf(username));

        // Build the role-filtered sidebar
        renderSidebar();

        // Open the dashboard as the first tab
        NavRegistry.findById("dashboard").ifPresent(this::openTab);

        ApiContext.getInstance().sessionExpired = false;
        sessionExpiryHandled = false;
        startSessionWatcher();
    }

    /** Re-route to the auth screen after clearing the session. */
    @FXML
    private void onLogout() {
        UtilityClass.clearSession();
        routeToAuthScreen();
    }

    private void routeToAuthScreen() {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(
                    EazyManage.class.getResource("Shell/Auth/auth-container.fxml"));
            Scene authScene = new Scene(loader.load());
            ThemeManager.getInstance().register(authScene);
            stage.setTitle("EAZY MANAGE");
            stage.getIcons().clear();
            stage.getIcons().add(new Image(
                    EazyManage.class.getResourceAsStream("Icons/EazyManage.png")));
            stage.setScene(authScene);
            stage.setMaximized(false);
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ====================================================================
    // Session-expiry watcher
    // ====================================================================

    private void startSessionWatcher() {
        sessionWatcher = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), e -> {
            if (sessionExpiryHandled) return;
            if (ApiContext.getInstance().sessionExpired) {
                sessionExpiryHandled = true;
                handleSessionExpiry();
            }
        }));
        sessionWatcher.setCycleCount(Timeline.INDEFINITE);
        sessionWatcher.play();
    }

    private void handleSessionExpiry() {
        if (sessionWatcher != null) sessionWatcher.stop();

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Session expired");
            alert.setHeaderText("Your session has expired");
            alert.setContentText("For your security, you've been signed out after the maximum session length. "
                    + "Please log in again to continue.");
            alert.getButtonTypes().setAll(ButtonType.OK);
            alert.showAndWait();

            ApiContext.getInstance().sessionExpired = false;
            UtilityClass.clearSession();
            routeToAuthScreen();
        });
    }

    // ====================================================================
    // Sidebar
    // ====================================================================
    private void renderSidebar() {
        navContainer.getChildren().clear();
        sidebarItemNodes.clear();

        for (Section section : NavRegistry.sectionsFor(role)) {
            VBox group = new VBox(2);
            group.getStyleClass().add("sidebar-section");

            Label title = new Label(section.title());
            title.getStyleClass().add("sidebar-section-title");
            group.getChildren().add(title);

            for (NavItem item : section.items()) {
                Button btn = buildSidebarItem(item);
                group.getChildren().add(btn);
                sidebarItemNodes.put(item.id(), btn);
            }
            navContainer.getChildren().add(group);
        }
    }

    private Button buildSidebarItem(NavItem item) {
        // Icon glyph + label rendered as two children of a HBox graphic
        Label icon  = new Label(item.icon());
        icon.getStyleClass().addAll("nav-icon");

        Label label = new Label(item.label());
        label.getStyleClass().add("sidebar-item-label");

        HBox content = new HBox(12, icon, label);
        content.setAlignment(Pos.CENTER_LEFT);

        Button btn = new Button();
        btn.setGraphic(content);
        btn.getStyleClass().add("sidebar-item");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> openTab(item));

        return btn;
    }

    /** Re-apply active styling on every sidebar entry based on activeTabId. */
    private void refreshSidebarActiveState() {
        for (Map.Entry<String, Button> entry : sidebarItemNodes.entrySet()) {
            boolean isActive = entry.getKey().equals(activeTabId);
            Button b = entry.getValue();
            b.getStyleClass().removeAll("sidebar-item-active");
            if (isActive) b.getStyleClass().add("sidebar-item-active");
        }
    }

    @FXML
    private void onToggleSidebar() {
        sidebarCollapsed = !sidebarCollapsed;
        if (sidebarCollapsed) {
            if (!sidebar.getStyleClass().contains("sidebar-collapsed")) {
                sidebar.getStyleClass().add("sidebar-collapsed");
            }
        } else {
            sidebar.getStyleClass().remove("sidebar-collapsed");
        }
    }

    // ====================================================================
    // Workspace tabs
    // ====================================================================
    /**
     * Open the tab corresponding to a nav item.
     * If already open, just focus it. Otherwise lazy-construct it and focus.
     */
    public void openTab(NavItem item) {
        if (openTabs.containsKey(item.id())) {
            focusTab(item.id());
            return;
        }

        WorkSpaceTab tab = new WorkSpaceTab(
                item.id(),
                item.label(),
                () -> contentLoaderFor(item),
                /* closable = */ !"dashboard".equals(item.id())); // dashboard always available

        openTabs.put(item.id(), tab);
        renderTabBar();
        focusTab(item.id());
    }

    public void closeTab(String tabId) {
        WorkSpaceTab tab = openTabs.remove(tabId);
        if (tab == null) return;
        tab.dispose();
        renderTabBar();

        // If we closed the active tab, focus the last remaining one (if any).
        if (tabId.equals(activeTabId)) {
            activeTabId = null;
            String fallback = openTabs.keySet().stream().reduce((a, b) -> b).orElse(null);
            if (fallback != null) focusTab(fallback);
            else {
                contentArea.getChildren().clear();
                refreshSidebarActiveState();
            }
        }
    }

    private void focusTab(String tabId) {
        WorkSpaceTab tab = openTabs.get(tabId);
        if (tab == null) return;

        activeTabId = tabId;
        contentArea.getChildren().setAll(tab.content());
        // Make the embedded Parent fill the StackPane
        Parent content = tab.content();
        if (content instanceof Region r) {
            r.setMaxWidth(Double.MAX_VALUE);
            r.setMaxHeight(Double.MAX_VALUE);
        }
        StackPane.setMargin(content, Insets.EMPTY);

        refreshTabHeaderStates();
        refreshSidebarActiveState();
    }

    private void renderTabBar() {
        tabBar.getChildren().clear();
        tabHeaderNodes.clear();
        for (WorkSpaceTab tab : openTabs.values()) {
            Node header = buildTabHeader(tab);
            tabBar.getChildren().add(header);
            tabHeaderNodes.put(tab.getId(), header);
        }
        refreshTabHeaderStates();
    }

    private Node buildTabHeader(WorkSpaceTab tab) {
        Label titleLabel = new Label(tab.getTitle());

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("workspace-tab");
        header.getChildren().add(titleLabel);

        if (tab.isClosable()) {
            Button close = new Button("×");
            close.getStyleClass().add("workspace-tab-close");
            close.setOnAction(e -> closeTab(tab.getId()));
            // Consume the MOUSE_CLICKED on the close button so it does not
            // bubble up to the HBox header's focusTab handler — otherwise
            // clicking close would momentarily focus the tab being destroyed.
            close.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseEvent::consume);
            header.getChildren().add(close);
        }

        header.setOnMouseClicked(e -> focusTab(tab.getId()));
        return header;
    }

    private void refreshTabHeaderStates() {
        for (Map.Entry<String, Node> entry : tabHeaderNodes.entrySet()) {
            Node n = entry.getValue();
            boolean active = entry.getKey().equals(activeTabId);
            n.getStyleClass().removeAll("workspace-tab-active");
            if (active) n.getStyleClass().add("workspace-tab-active");
        }
    }

    private Parent contentLoaderFor(NavItem item) {
        if (item.status() == NavItemStatus.FUNCTIONAL && "dashboard".equals(item.id())) {
            return loadShellDashboard();
        }
        if (item.status() == NavItemStatus.FUNCTIONAL && "conduct".equals(item.id())) {
            return loadConductScreen();
        }
        if (item.status() == NavItemStatus.FUNCTIONAL && "attendance".equals(item.id())) {
            return loadAttendanceScreen();
        }
        if (item.status() == NavItemStatus.FUNCTIONAL && "results".equals(item.id())) {
            return loadResultsScreen();
        }
        if (item.status() == NavItemStatus.FUNCTIONAL && "students".equals(item.id())) {
            return loadStudentsScreen();
        }
        if (item.status() == NavItemStatus.FUNCTIONAL && "staff".equals(item.id())) {
            return loadStaffScreen();
        }
        if (item.status() == NavItemStatus.FUNCTIONAL && "audit-logs".equals(item.id())) {
            return loadAuditLogsScreen();
        }
        if (item.status() == NavItemStatus.FUNCTIONAL && "finance".equals(item.id())) {
            return loadFinanceScreen();
        }
        if (item.status() == NavItemStatus.FUNCTIONAL && "academic-setup".equals(item.id())) {
            return loadAcademicSetupScreen();
        }
        if (item.status() == NavItemStatus.FUNCTIONAL && "settings".equals(item.id())) {
            return loadSettingsScreen();
        }
        if (item.status() == NavItemStatus.FUNCTIONAL && "reports".equals(item.id())) {
            return loadReportsScreen();
        }
        if (item.status() == NavItemStatus.FUNCTIONAL && "broadcasts".equals(item.id())) {
            return loadBroadcastsScreen();
        }
        if (item.status() == NavItemStatus.FUNCTIONAL && "offline".equals(item.id())) {
            return loadOfflineSettingsScreen();
        }
        return placeholderFor(item);
    }

    private Parent loadShellDashboard() {
        Consumer<String> navigateToTab = tabId ->
                NavRegistry.findById(tabId).ifPresent(this::openTab);
        return DashboardRouter.load(
                role, staffId, username, organizationName, navigateToTab);
    }

    private Parent loadConductScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/Shell/Conduct/Conduct.fxml"));
            Parent root = loader.load();
            ConductController controller = loader.getController();
            controller.setIdentity(staffId, role);
            return root;
        } catch (Exception e) {
            e.printStackTrace();
            return errorPlaceholder("Could not load Conduct screen",
                    e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
    }

    private Parent loadSettingsScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Settings/Settings.fxml"));
            Parent root = loader.load();
            SettingsController controller = loader.getController();
            controller.attach(role, staffId, organizationName, username);
            return root;
        } catch (Exception e) {
            e.printStackTrace();
            return errorPlaceholder("Could not load Settings",
                    e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
    }

    /**
     * Load the Phase 9a Attendance shell tab. Host controller manages the
     * entry-picker → inner-tabs transition; inner controllers lazy-load.
     */
    private Parent loadAttendanceScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Attendance/AttendanceTab.fxml"));
            Parent root = loader.load();
            AttendanceTabController controller = loader.getController();
            controller.attach(role, staffId);
            return root;
        } catch (Exception e) {
            e.printStackTrace();
            return errorPlaceholder("Could not load Attendance screen",
                    e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
    }

    /**
     * Load the Phase 9b Results shell tab. Host manages picker → inner
     * tabs; role filtering hides the Enter Scores card/tab for Principal.
     */
    private Parent loadResultsScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Results/ResultsTab.fxml"));
            Parent root = loader.load();
            ResultsTabController controller = loader.getController();
            controller.attach(role, staffId);
            return root;
        } catch (Exception e) {
            e.printStackTrace();
            return errorPlaceholder("Could not load Results screen",
                    e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
    }

    private Parent loadStudentsScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Students/StudentsTab.fxml"));
            Parent root = loader.load();
            StudentsTabController controller = loader.getController();
            controller.attach(role, staffId);
            return root;
        } catch (Exception e) {
            e.printStackTrace();
            return errorPlaceholder("Could not load Students screen",
                    e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
    }

    private Parent loadStaffScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Staff/StaffTab.fxml"));
            Parent root = loader.load();
            StaffTabController controller = loader.getController();
            controller.attach(role, staffId, organizationName);
            return root;
        } catch (Exception e) {
            e.printStackTrace();
            return errorPlaceholder("Could not load Staff screen",
                    e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
    }

    private Parent loadFinanceScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Finance/FinanceTab.fxml"));
            Parent root = loader.load();
            FinanceTabController controller = loader.getController();
            controller.attach(role, staffId);
            return root;
        } catch (Exception e) {
            e.printStackTrace();
            return errorPlaceholder("Could not load Finance screen",
                    e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
    }

    private Parent loadAuditLogsScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/AuditLogs/AuditLogsTab.fxml"));
            Parent root = loader.load();
            AuditLogsController controller = loader.getController();
            controller.attach(role, staffId);
            return root;
        } catch (Exception e) {
            e.printStackTrace();
            return errorPlaceholder("Could not load Audit Logs",
                    e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
    }


    private Parent loadAcademicSetupScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Academics/AcademicSetupTab.fxml"));
            Parent root = loader.load();
            AcademicSetupTabController controller = loader.getController();
            controller.attach(role, staffId);
            return root;
        } catch (Exception e) {
            e.printStackTrace();
            return errorPlaceholder("Could not load Academic Setup",
                    e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
    }

    private Parent loadReportsScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Reporting/ReportingCenter.fxml"));
            Parent indexRoot = loader.load();

            StackPane host = new StackPane(indexRoot);
            host.getStyleClass().add("workspace-content");

            ReportingCenterController controller = loader.getController();
            controller.setContext(staffId, role, host, indexRoot);

            return host;
        } catch (Exception e) {
            e.printStackTrace();
            return errorPlaceholder("Could not load Reporting Center",
                    e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
    }

    /** Load the Phase 6 Broadcasts composer. Identity drives the role gate. */
    private Parent loadBroadcastsScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Messaging/BroadcastTab.fxml"));
            Parent root = loader.load();
            BroadcastTabController controller = loader.getController();
            controller.attach(staffId, role);
            return root;
        } catch (Exception e) {
            e.printStackTrace();
            return errorPlaceholder("Could not load Broadcasts",
                    e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
    }

    private Parent loadOfflineSettingsScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Settings/OfflineSettings.fxml"));
            OfflineReplayer.getInstance().setStaffId(staffId);
            return loader.load();
        } catch (Exception e) {
            e.printStackTrace();
            return errorPlaceholder("Could not load Offline Mode",
                    e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
    }

    private Parent placeholderFor(NavItem item) {
        VBox box = new VBox();
        box.getStyleClass().add("placeholder-screen");
        box.setAlignment(Pos.CENTER);

        Label icon = new Label(item.icon());
        icon.getStyleClass().add("placeholder-icon");

        Label title = new Label(item.label());
        title.getStyleClass().add("placeholder-title");

        String phase = (item.phase() == null || item.phase().isEmpty())
                ? "in a later phase"
                : "in " + item.phase();
        Label body = new Label(
                "This workspace will become available " + phase + " of the modernization. "
                        + "Until then, continue using the Dashboard tab to access existing workflows."
        );
        body.getStyleClass().add("placeholder-body");
        body.setWrapText(true);
        body.setMaxWidth(440);

        box.getChildren().addAll(icon, title, body);
        return box;
    }

    private Parent errorPlaceholder(String title, String detail) {
        VBox box = new VBox(12);
        box.getStyleClass().add("placeholder-screen");
        box.setAlignment(Pos.CENTER);

        Label t = new Label(title);
        t.getStyleClass().add("placeholder-title");
        Label d = new Label(detail);
        d.getStyleClass().add("placeholder-body");
        d.setWrapText(true);
        d.setMaxWidth(520);

        box.getChildren().addAll(t, d);
        return box;
    }

    // ====================================================================
    // Notification center popup
    // ====================================================================
    @FXML
    private void onOpenNotifications() {
        Popup p = getOrCreateNotifPopup();
        if (p.isShowing()) {
            p.hide();
            return;
        }
        // Position just below and right-aligned with the bell.
        Window w = notifBell.getScene().getWindow();
        if (w == null) return;
        double anchorX = notifBell.localToScreen(0, 0).getX();
        double anchorY = notifBell.localToScreen(0, 0).getY();
        // Pop centre stays roughly under the bell; popup is 380 wide.
        p.show(w, anchorX - 320, anchorY + notifBell.getHeight() + 8);
    }

    private Popup getOrCreateNotifPopup() {
        if (notifPopup != null) return notifPopup;
        notifPopup = new Popup();
        notifPopup.setAutoHide(true);
        notifPopup.setAutoFix(true);
        notifPopup.setHideOnEscape(true);

        VBox root = new VBox();
        root.getStyleClass().add("notif-popup-root");

        VBox card = new VBox();
        card.getStyleClass().add("notif-center");

        // Header
        HBox header = new HBox(8);
        header.getStyleClass().add("notif-center-header");
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Notifications");
        title.getStyleClass().add("notif-center-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button markAll = new Button("Mark all read");
        markAll.getStyleClass().add("notif-mark-all");
        markAll.setOnAction(e -> NotificationCenter.getInstance().markAllRead());
        header.getChildren().addAll(title, spacer, markAll);

        // Body — scrollable list, observed against the NotificationCenter list
        VBox list = new VBox();
        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(360);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("scroll-pane");

        ObservableList<Notification> source = NotificationCenter.getInstance().getNotifications();
        rebuildNotifList(list, source);
        source.addListener((ListChangeListener<Notification>) c -> rebuildNotifList(list, source));

        card.getChildren().addAll(header, scroll);
        root.getChildren().add(card);
        notifPopup.getContent().add(root);
        return notifPopup;
    }

    private void rebuildNotifList(VBox container, ObservableList<Notification> source) {
        container.getChildren().clear();
        if (source.isEmpty()) {
            Label empty = new Label("You're all caught up.");
            empty.getStyleClass().add("notif-center-empty");
            VBox wrap = new VBox(empty);
            wrap.setAlignment(Pos.CENTER);
            wrap.setPadding(new Insets(32));
            container.getChildren().add(wrap);
            return;
        }
        for (Notification n : source) {
            container.getChildren().add(buildNotificationItem(n));
        }
    }

    private Node buildNotificationItem(Notification notification) {
        VBox box = new VBox(4);
        box.getStyleClass().add("notif-item");
        if (!notification.isRead()) box.getStyleClass().add("notif-item-unread");

        // Header line: priority pill + title + unread dot
        HBox topLine = new HBox(8);
        topLine.setAlignment(Pos.CENTER_LEFT);

        Label priorityPill = new Label(switch (notification.getPriority()) {
            case INFO -> "info";
            case WARNING -> "warning";
            case URGENT -> "urgent";
        });
        priorityPill.getStyleClass().add(switch (notification.getPriority()) {
            case INFO -> "priority-info";
            case WARNING -> "priority-warning";
            case URGENT -> "priority-urgent";
        });

        Label title = new Label(notification.getTitle());
        title.getStyleClass().add("notif-item-title");
        title.setWrapText(true);
        HBox.setHgrow(title, Priority.ALWAYS);
        title.setMaxWidth(Double.MAX_VALUE);

        topLine.getChildren().addAll(priorityPill, title);
        if (!notification.isRead()) {
            Region dot = new Region();
            dot.getStyleClass().add("notif-unread-dot");
            topLine.getChildren().add(dot);
        }

        Label body = new Label(notification.getBody());
        body.getStyleClass().add("notif-item-body");
        body.setWrapText(true);

        Label ts = new Label(formatRelative(notification.getTimestamp()));
        ts.getStyleClass().add("notif-item-timestamp");

        box.getChildren().addAll(topLine, body, ts);
        box.setOnMouseClicked(e -> NotificationCenter.getInstance().markRead(notification.getId()));
        return box;
    }

    // ====================================================================
    // Theme toggle
    // ====================================================================
    @FXML
    private void onToggleTheme() {
        ThemeManager.getInstance().toggleTheme();
    }

    private void updateThemeToggleGlyph() {
        // Half-disc that visually flips between dark/light states.
        // Doesn't rely on emoji — uses Unicode geometric shape so rendering is consistent.

        boolean dark = ThemeManager.getInstance().isDark();
        themeToggle.setText(dark ? "◑" : "◐");
    }

    // ====================================================================
    // Utility
    // ====================================================================

    private static String safeText(String text) {
        return (text == null || text.isBlank()) ? "" : text;
    }

    private static String initialsOf(String fullName) {
        if (fullName == null || fullName.isBlank()) return "?";

        String[] parts = fullName.trim().split("\\s+");

        if (parts.length == 1)
            return parts[0].substring(0, 1).toUpperCase();

        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    private static String prettifyRole(String role) {
        if (role == null) return "";
        return switch (role) {
            case "PRINCIPAL"      -> "Principal";
            case "ADMINISTRATOR"  -> "Administrator";
            case "ACCOUNTANT"     -> "Accountant";
            case "TEACHING_STAFF" -> "Teacher";
            default -> role;
        };
    }

    private static String formatRelative(Instant when) {
        if (when == null) return "";
        Duration d = Duration.between(when, Instant.now());
        long s = d.getSeconds();
        if (s < 60) return "just now";
        if (s < 3600) return (s / 60) + "m ago";
        if (s < 86400) return (s / 3600) + "h ago";
        return (s / 86400) + "d ago";
    }
}
