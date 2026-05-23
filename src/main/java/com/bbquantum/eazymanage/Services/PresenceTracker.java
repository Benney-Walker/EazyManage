package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.DTO.WebSocket.ActiveSession;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.List;

public class PresenceTracker {

    private static final PresenceTracker INSTANCE = new PresenceTracker();
    public static PresenceTracker getInstance() { return INSTANCE; }

    private final ObservableList<ActiveSession> sessions = FXCollections.observableArrayList();
    private final IntegerProperty count = new SimpleIntegerProperty(0);

    private PresenceTracker() {
        sessions.addListener((ListChangeListener<ActiveSession>) c -> count.set(sessions.size()));
    }

    public ObservableList<ActiveSession> getSessions() { return sessions; }
    public ReadOnlyIntegerProperty countProperty() { return count; }
    public int count() { return count.get(); }

    public void replaceWith(List<ActiveSession> snapshot) {
        runOnFx(() -> {
            if (snapshot == null) {
                sessions.clear();
                return;
            }
            sessions.setAll(snapshot);
        });
    }

    /** Empty the list — used at logout or on disconnect. */
    public void clear() {
        runOnFx(sessions::clear);
    }

    private void runOnFx(Runnable r) {
        if (Platform.isFxApplicationThread()) r.run();
        else Platform.runLater(r);
    }
}
