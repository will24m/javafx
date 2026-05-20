package com.jfxtutor.ui;

import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Phase 0: TextArea placeholder. Phase 2 swaps in a RichTextFX CodeArea
 * with Java syntax highlighting.
 */
public class EditorPane extends VBox {

    private final TextArea textArea;

    public EditorPane() {
        getStyleClass().add("editor-pane");
        setId("editorPane");

        this.textArea = new TextArea();
        textArea.setEditable(false);
        textArea.getStyleClass().add("editor-textarea");
        VBox.setVgrow(textArea, Priority.ALWAYS);

        getChildren().add(textArea);
    }

    public void showStarterStub(String code) {
        textArea.setText(code);
    }

    public TextArea getTextArea() { return textArea; }
}
