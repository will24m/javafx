package com.jfxtutor.ui;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Mount point for snippet output. The compiled snippet's build() returns a
 * Parent which we set as the single child. The wrapping .sandbox-root style
 * class scopes user-provided CSS so it does not leak into the host scene.
 */
public class PreviewHost extends StackPane {

    private Parent currentRoot;

    public PreviewHost() {
        getStyleClass().addAll("preview-host", "sandbox-root");
        setId("previewHost");
    }

    /** Phase 0 stub: hardcoded Hello, JavaFX as if the snippet had run. */
    public void showHelloWorldStub() {
        StackPane stub = new StackPane(new Label("Hello, JavaFX"));
        stub.getStyleClass().add("preview-stub");
        setSnippetRoot(stub);
    }

    /** Replace the mounted snippet root. Must be called on FX thread. */
    public void setSnippetRoot(Parent root) {
        getChildren().clear();
        this.currentRoot = root;
        if (root != null) {
            getChildren().add(root);
        }
    }

    public Parent getSnippetRoot() { return currentRoot; }
}
