package com.bbquantum.eazymanage.Shell;

import com.bbquantum.eazymanage.Constants.NavItemStatus;
import com.bbquantum.eazymanage.Controller.Report.ReportingCenterController;
import com.bbquantum.eazymanage.Controller.WebSocket.BroadcastsController;
import com.bbquantum.eazymanage.EazyManage;
import com.bbquantum.eazymanage.Records.NavItem;
import com.bbquantum.eazymanage.Records.Section;
import com.bbquantum.eazymanage.Utitity.ThemeManager;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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

    @FXML private HBox tabBar;
    @FXML private StackPane contentArea;

    // ====================================================================
    // Identity (set via setIdentity before boot())
    // ====================================================================

    private String role;
    private String staffId;
    private String organizationName;
    private String username;

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

        searchField.setDisable(true);
        searchField.setPromptText("Global search (coming soon)");
    }

    // ====================================================================
    // Public API — called from EazyManage.java after FXMLLoader.load()
    // ====================================================================
    /** Inject the post-login identity. Must be called before {@link #boot()}. */
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
    }

    /** Re-route to the auth screen after clearing the session. */
    @FXML
    private void onLogout() {
        UtilityClass.clearSession();
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(
                    EazyManage.class.getResource("Authentication/auth-container.fxml"));
            Scene authScene = new Scene(loader.load());
            ThemeManager.getInstance().register(authScene);
            stage.setTitle("EAZY MANAGE - Authentication");
            stage.getIcons().clear();
            stage.getIcons().add(new Image(
                    EazyManage.class.getResourceAsStream("Assets/EazyManage.png")));
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
            return loadLegacyDashboard();
        }
        if (item.status() == NavItemStatus.FUNCTIONAL && "conduct".equals(item.id())) {
            return loadConductScreen();
        }
        if (item.status() == NavItemStatus.FUNCTIONAL && "reports".equals(item.id())) {
            return loadReportsScreen();
        }
        if (item.status() == NavItemStatus.FUNCTIONAL && "broadcasts".equals(item.id())) {
            return loadBroadcastsScreen();
        }
        if (item.status() == NavItemStatus.FUNCTIONAL && "active-sessions".equals(item.id())) {
            return loadActiveSessionsScreen();
        }
        return placeholderFor(item);
    }

    private Parent loadLegacyDashboard() {
        String fxml = switch (role) {
            case "PRINCIPAL"      -> "/com/bbquantum/eazymanage/Principal/principal-main-dashboard.fxml";
            case "ACCOUNTANT"     -> "/com/bbquantum/eazymanage/Accountant/accountant-dashboard.fxml";
            case "TEACHING_STAFF" -> "/com/bbquantum/eazymanage/Staff/staff-dashboard.fxml";
            case "ADMINISTRATOR"  -> "/com/bbquantum/eazymanage/Admin/Admin-dashboard.fxml";
            default -> null;
        };
        if (fxml == null) {
            return errorPlaceholder("Unknown role", "No dashboard mapping for role '" + role + "'.");
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            // Drive the existing init path exactly as before — preserves every
            // legacy workflow (login data, caches, post-login initialise).
            UtilityClass.findController(role, staffId, organizationName, username, loader);
            return root;
        } catch (Exception e) {
            e.printStackTrace();
            return errorPlaceholder("Could not load dashboard",
                    e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
    }

    private Parent loadConductScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbquantum/eazymanage/Shell/Conduct.fxml"));
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

    /**
     * Load the Phase 5a Reporting Center.
     *
     * <p>The index FXML is wrapped in a fresh {@link StackPane} so the index
     * controller can swap a viewer FXML over it without involving the
     * workspace tab system. The viewer's back button swaps the index back in.
     */
    private Parent loadReportsScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/Reports/ReportingCenter.fxml"));
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
                    "/com/bbquantum/eazymanage/Shell/Broadcasts.fxml"));
            Parent root = loader.load();
            BroadcastsController controller = loader.getController();
            controller.setIdentity(role);
            return root;
        } catch (Exception e) {
            e.printStackTrace();
            return errorPlaceholder("Could not load Broadcasts",
                    e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
    }

    /** Load the Phase 6 Active Sessions view. No identity injection needed. */
    private Parent loadActiveSessionsScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bbquantum/eazymanage/Shell/ActiveSessions.fxml"));
            return loader.load();
        } catch (Exception e) {
            e.printStackTrace();
            return errorPlaceholder("Could not load Active Sessions",
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
