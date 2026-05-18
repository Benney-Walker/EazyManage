package com.bbquantum.eazymanage.Shell;

import com.bbquantum.eazymanage.Constants.NotificationPriority;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.time.Instant;
import java.util.UUID;

public class NotificationCenter {

    private static final NotificationCenter INSTANCE = new NotificationCenter();

    public static NotificationCenter getInstance() {
        return INSTANCE;
    }

    private final ObservableList<Notification> notifications =
            FXCollections.observableArrayList();

    private final IntegerProperty unreadCount = new SimpleIntegerProperty(0);

    private NotificationCenter() {
        notifications.addListener((ListChangeListener<Notification>)
                c -> recomputeUnread());
        seedDemo();
        recomputeUnread();
    }

    //Live, observable list of notifications, newest first.
    public ObservableList<Notification> getNotifications() {
        return notifications;
    }

    //Unread count
    public ReadOnlyIntegerProperty unreadCountProperty() {
        return unreadCount;
    }

    public int unreadCount() {
        return unreadCount.get();
    }

    //Insert a new notification at the top of the list.
    public void post(String title, String content, NotificationPriority priority) {
        Notification notify = new Notification(
                UUID.randomUUID().toString(),
                title,
                content,
                Instant.now(),
                priority,
                false
        );

        runOnFx(() -> notifications.addFirst(notify));
    }

    public void markRead(String id) {
        if (id == null) return;
        runOnFx(() -> notifications.stream()
                .filter(n -> id.equals(n.getId()) && !n.isRead())
                .findFirst()
                .ifPresent(Notification::markAsRead));
    }

    public void markAllRead() {
        runOnFx(() -> notifications.forEach(Notification::markAsRead));
    }

    public void clearAll() {
        runOnFx(notifications::clear);
    }

    // ---- internals --------------------------------------------------------

    private void recomputeUnread() {
        int c = 0;
        for (Notification n : notifications) if (!n.isRead()) c++;
        unreadCount.set(c);
    }

    private void seedDemo() {
        // A single welcome entry so the empty state is visible on first run
        // but doesn't dominate the UI. Remove this once Phase 6 ships a live feed.
        notifications.add(new Notification(
                UUID.randomUUID().toString(),
                "Welcome to the modernized workspace",
                "Use the sidebar to switch screens. Open multiple tabs at once to keep workflows in context.",
                Instant.now(),
                NotificationPriority.INFO,
                false));
    }

    private void runOnFx(Runnable r) {
        if (Platform.isFxApplicationThread()) r.run();
        else Platform.runLater(r);
    }
}
