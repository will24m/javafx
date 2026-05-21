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
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class MainView extends BorderPane {

    private final LessonNavigator lessonNavigator;
    private final LessonPane lessonPane;
    private final InteractiveIde ide;
    private final InspectorPane inspectorPane;

    private final Label appHeaderCrumbs;
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

        // ---- Top: app header ----
        Label brand = new Label("JavaFX Tutor");
        brand.getStyleClass().add("app-brand");
        Label dot = new Label("·");
        dot.getStyleClass().add("app-header-sep");
        this.appHeaderCrumbs = new Label("Select a lesson");
        appHeaderCrumbs.getStyleClass().add("app-crumbs");
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        Label tagline = new Label("Learn JavaFX by editing JavaFX");
        tagline.getStyleClass().add("app-tagline");

        HBox header = new HBox(10, brand, dot, appHeaderCrumbs, headerSpacer, tagline);
        header.getStyleClass().add("app-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(8, 14, 8, 14));
        setTop(header);

        // ---- Center: four-column workspace ----
        SplitPane workspace = new SplitPane();
        workspace.setOrientation(Orientation.HORIZONTAL);
        workspace.getItems().addAll(lessonNavigator, lessonPane, ide, inspectorPane);
        // Slightly wider navigator + inspector so titles & properties don't truncate.
        workspace.setDividerPositions(0.19, 0.40, 0.76);
        lessonNavigator.setMinWidth(180);
        lessonPane.setMinWidth(260);
        ide.setMinWidth(320);
        inspectorPane.setMinWidth(220);
        setCenter(workspace);

        // ---- Bottom: status bar ----
        this.statusLeft = new Label("Ready");
        statusLeft.getStyleClass().add("status-text");
        Region statusSpacer = new Region();
        HBox.setHgrow(statusSpacer, Priority.ALWAYS);
        this.statusRight = new Label("0 lessons loaded");
        statusRight.getStyleClass().add("status-text");

        HBox statusBar = new HBox(12, statusLeft, statusSpacer, statusRight);
        statusBar.getStyleClass().add("app-status-bar");
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setPadding(new Insets(4, 14, 4, 14));
        setBottom(statusBar);

        // ---- Wire selection events ----
        lessonNavigator.selectedLessonProperty().addListener((obs, old, lesson) -> {
            if (lesson == null) return;
            lessonPane.showLesson(lesson);
            ide.loadLesson(lesson);
            updateHeaderAndStatus(lesson);
        });

        // Refresh the Inspector whenever a new snippet root is mounted.
        ide.mountedRootProperty().addListener((obs, old, root) ->
                inspectorPane.setRoot(root));

        // Initial state
        this.totalLessons = lessonNavigator.getLessonCount();
        var initial = lessonNavigator.getSelectedLesson();
        if (initial != null) {
            lessonPane.showLesson(initial);
            ide.loadLesson(initial);
            updateHeaderAndStatus(initial);
        } else {
            lessonPane.showLessonStub("001-what-is-a-stage", "What is a Stage?");
            statusRight.setText(totalLessons + " lessons loaded");
        }
    }

    private void updateHeaderAndStatus(Lesson lesson) {
        String tier = capitalize(lesson.meta.tier);
        appHeaderCrumbs.setText(tier + "  /  " + lesson.meta.id + "  ·  " + lesson.meta.title);
        statusLeft.setText("Lesson " + lesson.meta.order + " of " + totalLessons
                + "  ·  " + lesson.meta.estimatedMinutes + " min");
        statusRight.setText(totalLessons + " lessons loaded");
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

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
