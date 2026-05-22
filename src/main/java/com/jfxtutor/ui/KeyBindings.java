package com.jfxtutor.ui;

import com.jfxtutor.util.AppLog;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

/**
 * Centralized keyboard shortcut registration for the host application.
 *
 * All accelerators are registered on the Scene's key-filter so they fire
 * regardless of which node has focus. Each shortcut calls a provided Runnable
 * (typically a lambda in MainView) so this class has zero circular deps.
 *
 * Registered shortcuts:
 *   Cmd/Ctrl + ]       → next lesson
 *   Cmd/Ctrl + [       → prev lesson
 *   Cmd/Ctrl + R       → recompile snippet
 *   Cmd/Ctrl + I       → toggle inspector
 *   Cmd/Ctrl + T       → cycle theme (dark → light → hc → dark)
 *   Cmd/Ctrl + /       → toggle AI tutor (reserved — no-op until B2)
 *   Cmd/Ctrl + Shift+P → command palette (reserved — no-op until B4)
 *   Cmd/Ctrl + F       → find in lesson (reserved — no-op until B4)
 */
public class KeyBindings {

    private static final KeyCombination.Modifier SHORTCUT = KeyCombination.SHORTCUT_DOWN;
    private static final KeyCombination.Modifier SHIFT    = KeyCombination.SHIFT_DOWN;

    private final Scene scene;

    // Required actions
    private Runnable onNextLesson    = () -> {};
    private Runnable onPrevLesson    = () -> {};
    private Runnable onRecompile     = () -> {};
    private Runnable onToggleInspector = () -> {};
    private Runnable onCycleTheme    = () -> {};
    // Reserved stubs
    private Runnable onToggleTutor   = () -> {};
    private Runnable onCommandPalette = () -> {};
    private Runnable onFind          = () -> {};

    public KeyBindings(Scene scene) {
        this.scene = scene;
        register();
        AppLog.info("keys", "Keyboard shortcuts registered.");
    }

    // ── setters ───────────────────────────────────────────────────────────────

    public void onNextLesson(Runnable r)      { onNextLesson = r; }
    public void onPrevLesson(Runnable r)      { onPrevLesson = r; }
    public void onRecompile(Runnable r)       { onRecompile = r; }
    public void onToggleInspector(Runnable r) { onToggleInspector = r; }
    public void onCycleTheme(Runnable r)      { onCycleTheme = r; }
    public void onToggleTutor(Runnable r)     { onToggleTutor = r; }
    public void onCommandPalette(Runnable r)  { onCommandPalette = r; }
    public void onFind(Runnable r)            { onFind = r; }

    // ── registration ─────────────────────────────────────────────────────────

    private void register() {
        var nextLesson     = combo(KeyCode.CLOSE_BRACKET, SHORTCUT);
        var prevLesson     = combo(KeyCode.OPEN_BRACKET, SHORTCUT);
        var recompile      = combo(KeyCode.R, SHORTCUT);
        var toggleInspect  = combo(KeyCode.I, SHORTCUT);
        var cycleTheme     = combo(KeyCode.T, SHORTCUT);
        var toggleTutor    = combo(KeyCode.SLASH, SHORTCUT);
        var cmdPalette     = combo(KeyCode.P, SHORTCUT, SHIFT);
        var find           = combo(KeyCode.F, SHORTCUT);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (nextLesson.match(e))    { fire(onNextLesson,     "next-lesson", e);    }
            else if (prevLesson.match(e))    { fire(onPrevLesson,     "prev-lesson", e);    }
            else if (recompile.match(e))     { fire(onRecompile,      "recompile", e);      }
            else if (toggleInspect.match(e)) { fire(onToggleInspector,"inspect", e);        }
            else if (cycleTheme.match(e))    { fire(onCycleTheme,     "cycle-theme", e);    }
            else if (toggleTutor.match(e))   { fire(onToggleTutor,    "tutor", e);          }
            else if (cmdPalette.match(e))    { fire(onCommandPalette, "cmd-palette", e);    }
            else if (find.match(e))          { fire(onFind,           "find", e);           }
        });
    }

    private void fire(Runnable action, String name, KeyEvent e) {
        AppLog.info("keys", "Shortcut fired: " + name);
        e.consume();
        action.run();
    }

    private static KeyCodeCombination combo(KeyCode code, KeyCombination.Modifier... mods) {
        return new KeyCodeCombination(code, mods);
    }
}
