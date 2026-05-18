package com.bbquantum.eazymanage.Utitity;

import com.bbquantum.eazymanage.Constants.AppTheme;
import com.bbquantum.eazymanage.Listener.ThemeChangeListener;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

public class ThemeManager {

    // Singleton
    private static final ThemeManager INSTANCE = new ThemeManager();

    public static ThemeManager getInstance() {
        return INSTANCE;
    }

    // Persistence
    private static final Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);
    private static final String PREF_KEY = "eazymanage.theme";

    // Classpath resource locations for the design system files
    private static final String TOKENS_CSS     = "/com/bbquantum/eazymanage/Theme/tokens.css";
    private static final String LIGHT_CSS      = "/com/bbquantum/eazymanage/Theme/light-theme.css";
    private static final String DARK_CSS       = "/com/bbquantum/eazymanage/Theme/dark-theme.css";
    private static final String COMPONENTS_CSS = "/com/bbquantum/eazymanage/Theme/components.css";

    //State
    private AppTheme currentTheme;

    //Track scenes via WeakReference so a closed window can be garbage-collected
    private final List<WeakReference<Scene>> trackedScenes = new ArrayList<>();

    //handy for wiring a theme toggle button's icon/label to update automatically.
    private final List<ThemeChangeListener> listeners = new ArrayList<>();

    private ThemeManager() {
        this.currentTheme = AppTheme.fromString(PREF_KEY, AppTheme.LIGHT);
    }

    //Get currently active theme
    public AppTheme getCurrentTheme() {
        return currentTheme;
    }

    // True when dark mode is active
    public boolean isDark() {
        return resolve(currentTheme) == AppTheme.DARK;
    }

    /**
     * Register a Scene so it receives the current theme and any subsequent
     * theme changes. Idempotent — safe to call repeatedly on the same Scene.
     */
    public void register(Scene scene) {
        if (scene == null) return;

        purgeDeadReferences();

        if (!isTracked(scene)) {
            trackedScenes.add(new WeakReference<>(scene));
        }
        applyTo(scene, resolve(currentTheme));
    }

    // Stop tracking a Scene (e.g. before disposing a window).
    public void unregister(Scene scene) {
        if (scene == null) return;
        Iterator<WeakReference<Scene>> iterator = trackedScenes.iterator();
        while (iterator.hasNext()) {
            Scene s = iterator.next().get();
            if (s == null || s == scene) iterator.remove();
        }
    }

    // Switch to a specific theme; persist and re-apply to every tracked Scene.
    public void setTheme(AppTheme theme) {
        if (theme == null || theme == currentTheme) return;

        this.currentTheme = theme;

        prefs.put(PREF_KEY, theme.name());
        AppTheme resolved = resolve(currentTheme);
        runOnFx(() -> {
            purgeDeadReferences();
            for (WeakReference<Scene> sceneRef : trackedScenes) {
                Scene scene = sceneRef.get();
                if (scene != null) applyTo(scene, resolved);
            }

            for (ThemeChangeListener listener : listeners) {
                listener.onThemeChange(theme);
            }
        });
    }

    // Flip between LIGHT and DARK. SYSTEM resolves to its concrete value first.
    public void toggleTheme() {
        AppTheme resolved = resolve(currentTheme);

        setTheme(resolved == AppTheme.DARK ? AppTheme.LIGHT : AppTheme.DARK);
    }

    // Subscribe to theme changes (e.g. to update a toggle button's icon).
    public void addListener(ThemeChangeListener listener) {
        if (listener != null)
            listeners.add(listener);
    }

    public void removeListener(ThemeChangeListener listener) {
        listeners.remove(listener);
    }

    // Internals

    //Apply the resolved theme to one Scene.
    private void applyTo(Scene scene, AppTheme resolved) {
        ObservableList<String> sheets = scene.getStylesheets();

        String tokens = url(TOKENS_CSS);
        String themeCss = url(resolved == AppTheme.DARK ? DARK_CSS : LIGHT_CSS);
        String components = url(COMPONENTS_CSS);

        if (tokens == null || themeCss == null || components == null) {
            System.out.println("token " + tokens + " or theme " + themeCss + " or components " + components);
            System.out.println("[ThemeManager] Could not resolve one or more theme stylesheets on the classpath.");
            return;
        }

        // Strip any previously-added theme sheets so toggling doesn't accumulate.
        sheets.removeIf(s ->
                s.endsWith("tokens.css")
        || s.endsWith("light-theme.css")
        || s.endsWith("dark-theme.css")
        || s.endsWith("components.css"));

        // Prepend the new design system in dependency order:
        //  tokens (base) → theme (colors) → components (consume tokens)
        sheets.add(0, tokens);
        sheets.add(1, themeCss);
        sheets.add(2, components);
    }

    private String url(String classPath) {
        URL url = ThemeManager.class.getResource(classPath);
        return (url == null) ? null : url.toExternalForm();
    }

    private AppTheme resolve(AppTheme theme) {
        return (theme == AppTheme.SYSTEM) ? AppTheme.LIGHT : theme;
    }

    private boolean isTracked(Scene scene) {
        for (WeakReference<Scene> ref : trackedScenes) {
            if (ref.get() == scene) {
                return true;
            }
        }
        return false;
    }

    private void purgeDeadReferences() {
        trackedScenes.removeIf(ref ->
                ref.get() == null);
    }

    private void runOnFx(Runnable runnable) {
        if (Platform.isFxApplicationThread()) runnable.run();
        else Platform.runLater(runnable);
    }
}
