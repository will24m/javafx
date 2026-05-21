package com.jfxtutor.ui;

import com.jfxtutor.data.curriculum.Lesson;
import com.jfxtutor.engine.runtime.SnippetRunner;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Self-contained mini-IDE: editor on top, live preview underneath, plus a
 * toolbar showing the current lesson and Run/Reset controls. The IDE is
 * independent of the educational content panes — lesson selection only flows
 * in through {@link #loadLesson(Lesson)}, and the IDE manages its own editor,
 * snippet runner, and preview lifecycle.
 */
public class InteractiveIde extends VBox {

    private final EditorPane editorPane;
    private final PreviewHost previewHost;
    private final SnippetRunner snippetRunner;

    private final Label lessonChip;
    private final Label statusLabel;
    private final Button runButton;
    private final Button resetButton;

    private final ReadOnlyObjectWrapper<Lesson> currentLesson = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Parent> mountedRoot = new ReadOnlyObjectWrapper<>();
    private String starterSnippet = "";

    public InteractiveIde() {
        getStyleClass().add("ide");
        setId("interactiveIde");

        this.editorPane = new EditorPane();
        this.previewHost = new PreviewHost();

        this.snippetRunner = new SnippetRunner(
                root -> {
                    previewHost.setSnippetRoot(root);
                    mountedRoot.set(root);
                    setStatus("Ready", "ide-status-ok");
                },
                error -> {
                    previewHost.showError(error);
                    setStatus("Error", "ide-status-error");
                });

        this.lessonChip = new Label("No lesson loaded");
        lessonChip.getStyleClass().add("ide-lesson-chip");

        this.statusLabel = new Label("Idle");
        statusLabel.getStyleClass().addAll("ide-status", "ide-status-idle");

        this.runButton = new Button("Run");
        runButton.getStyleClass().add("ide-button");
        runButton.setOnAction(e -> runNow());

        this.resetButton = new Button("Reset");
        resetButton.getStyleClass().add("ide-button");
        resetButton.setOnAction(e -> resetToStarter());

        Region toolbarSpacer = new Region();
        HBox.setHgrow(toolbarSpacer, Priority.ALWAYS);

        HBox toolbar = new HBox(8,
                new Label("IDE"), lessonChip, toolbarSpacer,
                statusLabel, resetButton, runButton);
        toolbar.getStyleClass().add("ide-toolbar");
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(6, 10, 6, 10));

        Label editorHeader = new Label("editor.java");
        editorHeader.getStyleClass().add("ide-pane-header");
        VBox editorBox = new VBox(editorHeader, editorPane);
        editorBox.getStyleClass().add("ide-editor-box");
        VBox.setVgrow(editorPane, Priority.ALWAYS);

        Label previewHeader = new Label("preview");
        previewHeader.getStyleClass().add("ide-pane-header");
        VBox previewBox = new VBox(previewHeader, previewHost);
        previewBox.getStyleClass().add("ide-preview-box");
        VBox.setVgrow(previewHost, Priority.ALWAYS);

        SplitPane split = new SplitPane();
        split.setOrientation(Orientation.VERTICAL);
        split.getStyleClass().add("ide-split");
        split.getItems().addAll(editorBox, previewBox);
        split.setDividerPositions(0.5);
        VBox.setVgrow(split, Priority.ALWAYS);

        getChildren().addAll(toolbar, split);

        // Live recompile on every keystroke (SnippetRunner debounces internally).
        editorPane.textProperty().addListener((obs, old, val) -> {
            setStatus("Compiling…", "ide-status-busy");
            snippetRunner.scheduleRecompile(val);
        });
    }

    /**
     * Swap the IDE to a new lesson. The editor refills with the lesson's
     * starter snippet, the preview rebuilds immediately, and the toolbar
     * updates. Educational content stays where it is.
     */
    public void loadLesson(Lesson lesson) {
        if (lesson == null) return;
        currentLesson.set(lesson);
        this.starterSnippet = lesson.meta.starterSnippet == null
                ? "" : lesson.meta.starterSnippet;
        lessonChip.setText(lesson.meta.id + "  ·  " + lesson.meta.title);
        editorPane.setText(starterSnippet);
        runNow();
    }

    /** Restore the editor to the lesson's starter snippet. */
    public void resetToStarter() {
        editorPane.setText(starterSnippet);
        runNow();
    }

    /** Force an immediate compile of the current editor contents. */
    public void runNow() {
        setStatus("Compiling…", "ide-status-busy");
        Runnable fire = () -> snippetRunner.recompileNow(editorPane.getText());
        if (Platform.isFxApplicationThread()) {
            fire.run();
        } else {
            Platform.runLater(fire);
        }
    }

    private void setStatus(String text, String styleClass) {
        statusLabel.setText(text);
        statusLabel.getStyleClass().removeAll(
                "ide-status-idle", "ide-status-busy", "ide-status-ok", "ide-status-error");
        statusLabel.getStyleClass().add(styleClass);
    }

    public Parent getMountedRoot() { return previewHost.getSnippetRoot(); }
    public EditorPane getEditorPane() { return editorPane; }
    public PreviewHost getPreviewHost() { return previewHost; }
    public SnippetRunner getSnippetRunner() { return snippetRunner; }
    public ReadOnlyObjectProperty<Lesson> currentLessonProperty() {
        return currentLesson.getReadOnlyProperty();
    }
    /** Fires whenever a new snippet root is mounted into the preview. */
    public ReadOnlyObjectProperty<Parent> mountedRootProperty() {
        return mountedRoot.getReadOnlyProperty();
    }

    public void shutdown() {
        snippetRunner.shutdown();
    }
}
