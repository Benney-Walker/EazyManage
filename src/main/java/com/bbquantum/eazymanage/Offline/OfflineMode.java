package com.bbquantum.eazymanage.Offline;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.prefs.Preferences;

public class OfflineMode {

    private static final String PREF_KEY = "eazymanage.offline.enabled";

    private static final OfflineMode INSTANCE = new OfflineMode();

    public static OfflineMode getInstance() {
        return INSTANCE;
    }

    private final Preferences prefs;

    private final BooleanProperty enabled;

    private OfflineMode() {
        this.prefs = Preferences.userRoot().node("com/bbquantum/eazymanage");
        boolean initial = prefs.getBoolean(PREF_KEY, false);
        this.enabled = new SimpleBooleanProperty(initial);

        // Persist on every change so the toggle is reflected immediately;
        // no explicit save() needed by callers.
        this.enabled.addListener((obs, oldVal, newVal) -> {
            prefs.putBoolean(PREF_KEY, newVal);
        });
    }

    public boolean isEnabled() {
        return enabled.get();
    }
    public BooleanProperty enabledProperty() {
        return enabled;
    }

    public void setEnabled(boolean value) {
        enabled.set(value);
    }
    public void toggle() {
        enabled.set(!enabled.get());
    }
}
