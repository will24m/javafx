package com.jfxtutor.ui;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Mount point for snippet output. The compiled snippet's build() returns a
 * Parent which we set as the single child. The wrapping .sandbox-root style
 * class scopes user-provided CSS so it does not leak into the host scene.
 *
 * <p>Also hosts an error overlay shown when a recompile fails — the previous
 * working preview stays mounted underneath so the user keeps visual context.
 */
public class PreviewHost extends StackPane {

    private Parent currentRoot;
    private final VBox errorOverlay;
    private final Label errorLabel;

    public PreviewHost() {
        getStyleClass().addAll("preview-host", "sandbox-root");
        setId("previewHost");

        this.errorLabel = new Label();
        errorLabel.setWrapText(true);
        errorLabel.getStyleClass().add("preview-error-label");

        this.errorOverlay = new VBox(errorLabel);
        errorOverlay.getStyleClass().add("preview-error-overlay");
        errorOverlay.setAlignment(Pos.TOP_LEFT);
        errorOverlay.setVisible(false);
        errorOverlay.setManaged(false);
        StackPane.setAlignment(errorOverlay, Pos.BOTTOM_LEFT);

        getChildren().add(errorOverlay);
    }

    /** Phase 0 stub: hardcoded Hello, JavaFX as if the snippet had run. */
    public void showHelloWorldStub() {
        StackPane stub = new StackPane(new Label("Hello, JavaFX"));
        stub.getStyleClass().add("preview-stub");
        setSnippetRoot(stub);
    }

    /** Replace the mounted snippet root. Must be called on FX thread. */
    public void setSnippetRoot(Parent root) {
        getChildren().removeIf(n -> n != errorOverlay);
        this.currentRoot = root;
        if (root != null) {
            getChildren().add(0, root);
        }
        clearError();
    }

    public Parent getSnippetRoot() { return currentRoot; }

    /** Show an error banner over the current preview without unmounting it. */
    public void showError(String message) {
        errorLabel.setText(message);
        errorOverlay.setVisible(true);
        errorOverlay.setManaged(true);
        errorOverlay.toFront();
    }

    public void clearError() {
        errorLabel.setText("");
        errorOverlay.setVisible(false);
        errorOverlay.setManaged(false);
    }
}
