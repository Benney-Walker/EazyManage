package com.bbquantum.eazymanage.Shell.Notification;

import com.bbquantum.eazymanage.Constants.NotificationPriority;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.time.Instant;

public class Notification {

    private final String   id;

    private final String   title;

    private final String   body;

    private final Instant timestamp;

    private final NotificationPriority priority;

    private final BooleanProperty read = new SimpleBooleanProperty(false);

    public Notification(String id, String title, String body,
                        Instant timestamp, NotificationPriority priority, boolean read) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
        this.priority = priority;
        this.read.set(false);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public NotificationPriority getPriority() {
        return priority;
    }

    public boolean isRead() {
        return read.get();
    }

    public BooleanProperty readProperty() {
        return read;
    }

    public void markAsRead() {
        read.set(true);
    }
}
