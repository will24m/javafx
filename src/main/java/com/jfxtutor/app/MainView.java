package com.jfxtutor.app;

import com.jfxtutor.engine.runtime.SnippetRunner;
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
    private final SnippetRunner snippetRunner;

    public MainView() {
        getStyleClass().add("host-root");
        setId("mainView");

        this.lessonNavigator = new LessonNavigator();
        this.lessonPane = new LessonPane();
        this.editorPane = new EditorPane();
        this.previewHost = new PreviewHost();
        this.inspectorPane = new InspectorPane();

        this.snippetRunner = new SnippetRunner(
                previewHost::setSnippetRoot,
                previewHost::showError);

        SplitPane center = new SplitPane();
        center.setOrientation(Orientation.VERTICAL);
        center.getItems().addAll(editorPane, previewHost);
        center.setDividerPositions(0.45);

        SplitPane root = new SplitPane();
        root.setOrientation(Orientation.HORIZONTAL);
        root.getItems().addAll(lessonNavigator, lessonPane, center, inspectorPane);
        root.setDividerPositions(0.14, 0.36, 0.78);

        setCenter(root);

        // Live recompile on every keystroke (debounced inside SnippetRunner).
        editorPane.textProperty().addListener((obs, old, val) -> {
            if (val != null && !val.isBlank()) {
                snippetRunner.scheduleRecompile(val);
            }
        });

        // Wire navigator selection → lesson pane + editor + immediate recompile
        lessonNavigator.selectedLessonProperty().addListener((obs, old, lesson) -> {
            if (lesson == null) return;
            lessonPane.showLesson(lesson);
            String snippet = lesson.meta.starterSnippet != null
                    ? lesson.meta.starterSnippet : "";
            editorPane.setText(snippet);
            snippetRunner.recompileNow(snippet);
        });

        // Show whichever lesson the navigator auto-selected at startup
        var initial = lessonNavigator.getSelectedLesson();
        if (initial != null) {
            lessonPane.showLesson(initial);
            String snippet = initial.meta.starterSnippet != null
                    ? initial.meta.starterSnippet : "";
            editorPane.setText(snippet);
            snippetRunner.recompileNow(snippet);
        } else {
            lessonPane.showLessonStub("001-what-is-a-stage", "What is a Stage?");
            String stub = "Label l = new Label(\"Hello, JavaFX\");\n"
                    + "return new StackPane(l);\n";
            editorPane.setText(stub);
            snippetRunner.recompileNow(stub);
        }
    }

    public LessonNavigator getLessonNavigator() { return lessonNavigator; }
    public LessonPane getLessonPane() { return lessonPane; }
    public EditorPane getEditorPane() { return editorPane; }
    public PreviewHost getPreviewHost() { return previewHost; }
    public InspectorPane getInspectorPane() { return inspectorPane; }
    public SnippetRunner getSnippetRunner() { return snippetRunner; }
}
