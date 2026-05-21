package com.jfxtutor.app;

import com.jfxtutor.data.curriculum.Lesson;
import com.jfxtutor.engine.runtime.SnippetRunner;
import com.jfxtutor.ui.EditorPane;
import com.jfxtutor.ui.InspectorPane;
import com.jfxtutor.ui.InteractiveIde;
import com.jfxtutor.ui.LessonNavigator;
import com.jfxtutor.ui.LessonPane;
import com.jfxtutor.ui.PreviewHost;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class MainView extends BorderPane {

    private final LessonNavigator lessonNavigator;
    private final LessonPane lessonPane;
    private final InteractiveIde ide;
    private final InspectorPane inspectorPane;

    private final Label appBreadcrumb;
    private final Label statusLeft;
    private final Label statusRight;
    private int totalLessons;

    public MainView() {
        getStyleClass().add("host-root");
        setId("mainView");

        this.lessonNavigator = new LessonNavigator();
        this.lessonPane = new LessonPane();
        this.ide = new InteractiveIde();
        this.inspectorPane = new InspectorPane();

        setTop(buildHeader());
        setCenter(buildWorkspace());
        setBottom(buildStatusBar());

        this.appBreadcrumb = (Label) lookup("#appBreadcrumb");
        this.statusLeft    = (Label) lookup("#statusLeft");
        this.statusRight   = (Label) lookup("#statusRight");

        // ---- Wire selection events ----
        lessonNavigator.selectedLessonProperty().addListener((obs, old, lesson) -> {
            if (lesson == null) return;
            lessonPane.showLesson(lesson);
            ide.loadLesson(lesson);
            updateHeaderAndStatus(lesson);
        });
        ide.mountedRootProperty().addListener((obs, old, root) ->
                inspectorPane.setRoot(root));

        // ---- Initial state ----
        this.totalLessons = lessonNavigator.getLessonCount();
        Lesson initial = lessonNavigator.getSelectedLesson();
        if (initial != null) {
            lessonPane.showLesson(initial);
            ide.loadLesson(initial);
            updateHeaderAndStatus(initial);
        } else {
            lessonPane.showLessonStub("001-what-is-a-stage", "What is a Stage?");
            statusRight.setText(totalLessons + " lessons loaded");
        }

        // ---- Keyboard shortcuts (registered once the Scene is attached) ----
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) installAccelerators(newScene);
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Layout pieces
    // ─────────────────────────────────────────────────────────────────────────

    private HBox buildHeader() {
        Label brand = new Label("JavaFX Tutor");
        brand.getStyleClass().add("app-brand");

        Label sep = new Label("•");
        sep.getStyleClass().add("app-header-sep");

        Label crumb = new Label("Select a lesson");
        crumb.setId("appBreadcrumb");
        crumb.getStyleClass().add("app-crumbs");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label shortcuts = new Label("⏎ Run  ·  ⇧⌘R Reset  ·  ⌘F Search");
        shortcuts.getStyleClass().add("app-shortcuts");

        HBox header = new HBox(10, brand, sep, crumb, spacer, shortcuts);
        header.getStyleClass().add("app-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 16, 10, 16));
        return header;
    }

    private SplitPane buildWorkspace() {
        SplitPane workspace = new SplitPane();
        workspace.setOrientation(Orientation.HORIZONTAL);
        workspace.getStyleClass().add("workspace-split");
        workspace.getItems().addAll(lessonNavigator, lessonPane, ide, inspectorPane);
        // Wider navigator + lesson body so titles never truncate at sane sizes.
        workspace.setDividerPositions(0.18, 0.42, 0.76);
        lessonNavigator.setMinWidth(200);
        lessonPane.setMinWidth(280);
        ide.setMinWidth(360);
        inspectorPane.setMinWidth(240);
        return workspace;
    }

    private HBox buildStatusBar() {
        Label left = new Label("Ready");
        left.setId("statusLeft");
        left.getStyleClass().add("status-text");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label right = new Label("0 lessons loaded");
        right.setId("statusRight");
        right.getStyleClass().add("status-text");

        HBox bar = new HBox(12, left, spacer, right);
        bar.getStyleClass().add("app-status-bar");
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(5, 16, 5, 16));
        return bar;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Accelerators
    // ─────────────────────────────────────────────────────────────────────────

    private void installAccelerators(Scene scene) {
        // ⌘+Enter — Run snippet now
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHORTCUT_DOWN),
                ide::runNow);
        // ⇧+⌘+R — Reset to starter
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.R,
                        KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN),
                ide::resetToStarter);
        // ⌘+F — Focus the lesson search box
        scene.getAccelerators().put(
                new KeyCharacterCombination("f", KeyCombination.SHORTCUT_DOWN),
                lessonNavigator::focusSearch);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // State updates
    // ─────────────────────────────────────────────────────────────────────────

    private void updateHeaderAndStatus(Lesson lesson) {
        String tier = capitalize(lesson.meta.tier);
        appBreadcrumb.setText(tier + "  ›  " + lesson.meta.title);
        statusLeft.setText("Lesson " + lesson.meta.order + " / " + totalLessons
                + "  ·  " + lesson.meta.estimatedMinutes + " min");
        statusRight.setText(totalLessons + " lessons loaded");
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Public accessors (kept for tests + future wiring)
    // ─────────────────────────────────────────────────────────────────────────

    public LessonNavigator getLessonNavigator() { return lessonNavigator; }
    public LessonPane getLessonPane() { return lessonPane; }
    public InteractiveIde getIde() { return ide; }
    public EditorPane getEditorPane() { return ide.getEditorPane(); }
    public PreviewHost getPreviewHost() { return ide.getPreviewHost(); }
    public InspectorPane getInspectorPane() { return inspectorPane; }
    public SnippetRunner getSnippetRunner() { return ide.getSnippetRunner(); }

    public void shutdown() {
        ide.shutdown();
    }
}
