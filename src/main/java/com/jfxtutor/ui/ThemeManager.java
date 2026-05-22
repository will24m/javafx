package com.jfxtutor.ui;

import com.jfxtutor.data.progress.ProgressStore;
import com.jfxtutor.util.AppLog;
import javafx.scene.Scene;

/**
 * Manages the active visual theme for the host application.
 *
 * Themes are additional CSS files loaded on top of the base {@code app.css}.
 * Switching themes removes the previous theme stylesheet and adds the new one
 * without restarting or re-building any UI.
 *
 * Supported themes:
 *   dark  — default, same dark palette already in app.css
 *   light — light background, dark text
 *   hc    — high-contrast for accessibility
 *
 * The chosen theme key is persisted in {@link ProgressStore} preferences
 * under {@code "theme"} so the user's choice survives restarts.
 */
public class ThemeManager {

    public enum Theme { DARK, LIGHT, HIGH_CONTRAST }

    private static final String PREF_KEY = "theme";

    private static final String DARK_CSS  = ThemeManager.class.getResource(
            "/css/theme-dark.css").toExternalForm();
    private static final String LIGHT_CSS = ThemeManager.class.getResource(
            "/css/theme-light.css").toExternalForm();
    private static final String HC_CSS    = ThemeManager.class.getResource(
            "/css/theme-hc.css").toExternalForm();

    private final Scene scene;
    private final ProgressStore store;
    private Theme current;

    public ThemeManager(Scene scene, ProgressStore store) {
        this.scene = scene;
        this.store = store;
        String saved = store.getPreference(PREF_KEY, "dark");
        this.current = parseTheme(saved);
        apply(current);
    }

    public Theme getCurrent() { return current; }

    public void setTheme(Theme theme) {
        if (theme == current) return;
        AppLog.info("theme", "Switching theme: " + current + " → " + theme);
        current = theme;
        apply(theme);
        store.setPreference(PREF_KEY, themeKey(theme));
        store.flush();
    }

    public void toggle() {
        setTheme(current == Theme.DARK ? Theme.LIGHT : Theme.DARK);
    }

    /** Cycle dark → light → hc → dark. */
    public void cycleTheme() {
        setTheme(switch (current) {
            case DARK  -> Theme.LIGHT;
            case LIGHT -> Theme.HIGH_CONTRAST;
            case HIGH_CONTRAST -> Theme.DARK;
        });
    }

    // ── internals ─────────────────────────────────────────────────────────────

    private void apply(Theme theme) {
        var sheets = scene.getStylesheets();
        sheets.removeAll(DARK_CSS, LIGHT_CSS, HC_CSS);
        switch (theme) {
            case LIGHT         -> sheets.add(LIGHT_CSS);
            case HIGH_CONTRAST -> sheets.add(HC_CSS);
            case DARK          -> sheets.add(DARK_CSS);
        }
        AppLog.info("theme", "Applied theme stylesheet: " + theme);
    }

    private static Theme parseTheme(String key) {
        return switch (key == null ? "dark" : key.toLowerCase()) {
            case "light" -> Theme.LIGHT;
            case "hc", "high-contrast" -> Theme.HIGH_CONTRAST;
            default -> Theme.DARK;
        };
    }

    private static String themeKey(Theme t) {
        return switch (t) {
            case LIGHT -> "light";
            case HIGH_CONTRAST -> "hc";
            default -> "dark";
        };
    }
}
