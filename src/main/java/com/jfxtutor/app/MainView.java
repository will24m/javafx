package com.jfxtutor.app;

import com.jfxtutor.ui.EditorPane;
import com.jfxtutor.ui.InspectorPane;
import com.jfxtutor.ui.LessonNavigator;
import com.jfxtutor.ui.LessonPane;
import com.jfxtutor.ui.PreviewHost;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;

public class MainView extends BorderPane {

    private final LessonNavigator lessonNavigator;
    private final LessonPane lessonPane;
    private final EditorPane editorPane;
    private final PreviewHost previewHost;
    private final InspectorPane inspectorPane;

    public MainView() {
        getStyleClass().add("host-root");
        setId("mainView");

        this.lessonNavigator = new LessonNavigator();
        this.lessonPane = new LessonPane();
        this.editorPane = new EditorPane();
        this.previewHost = new PreviewHost();
        this.inspectorPane = new InspectorPane();

        SplitPane center = new SplitPane();
        center.setOrientation(Orientation.VERTICAL);
        center.getItems().addAll(editorPane, previewHost);
        center.setDividerPositions(0.45);

        SplitPane root = new SplitPane();
        root.setOrientation(Orientation.HORIZONTAL);
        root.getItems().addAll(lessonNavigator, lessonPane, center, inspectorPane);
        root.setDividerPositions(0.14, 0.36, 0.78);

        setCenter(root);

        // Phase 0 stub: render Hello World in the preview pane on startup.
        previewHost.showHelloWorldStub();

        // Wire navigator selection → lesson pane + editor
        lessonNavigator.selectedLessonProperty().addListener((obs, old, lesson) -> {
            if (lesson == null) return;
            lessonPane.showLesson(lesson);
            String snippet = lesson.meta.starterSnippet != null
                    ? lesson.meta.starterSnippet : "";
            editorPane.showStarterStub(snippet);
        });

        // Show whichever lesson the navigator auto-selected at startup
        var initial = lessonNavigator.getSelectedLesson();
        if (initial != null) {
            lessonPane.showLesson(initial);
            String snippet = initial.meta.starterSnippet != null
                    ? initial.meta.starterSnippet : "";
            editorPane.showStarterStub(snippet);
        } else {
            // Fallback if curriculum is empty
            lessonPane.showLessonStub("001-what-is-a-stage", "What is a Stage?");
            editorPane.showStarterStub(
                    "public static Parent build() {\n"
                            + "    Label l = new Label(\"Hello, JavaFX\");\n"
                            + "    return new StackPane(l);\n"
                            + "}\n");
        }
    }

    public LessonNavigator getLessonNavigator() { return lessonNavigator; }
    public LessonPane getLessonPane() { return lessonPane; }
    public EditorPane getEditorPane() { return editorPane; }
    public PreviewHost getPreviewHost() { return previewHost; }
    public InspectorPane getInspectorPane() { return inspectorPane; }
}
