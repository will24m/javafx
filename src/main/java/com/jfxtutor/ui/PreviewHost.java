package com.jfxtutor.ui;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Mount point for snippet output. The compiled snippet's build() returns a
 * Parent which we mount inside a sandbox wrapper so host CSS stays scoped.
 *
 * <p>Also hosts an error overlay shown when a recompile fails; the previous
 * working preview stays mounted underneath so the user keeps visual context.
 */
public class PreviewHost extends StackPane {

    private Parent currentRoot;
    private final StackPane sandboxContainer;
    private final VBox errorOverlay;
    private final Label errorLabel;

    public PreviewHost() {
        getStyleClass().add("preview-host");
        setId("previewHost");

        this.sandboxContainer = new StackPane();
        sandboxContainer.getStyleClass().addAll("preview-sandbox", "sandbox-root");

        this.errorLabel = new Label();
        errorLabel.setWrapText(true);
        errorLabel.getStyleClass().add("preview-error-label");

        this.errorOverlay = new VBox(errorLabel);
        errorOverlay.getStyleClass().add("preview-error-overlay");
        errorOverlay.setAlignment(Pos.TOP_LEFT);
        errorOverlay.setVisible(false);
        errorOverlay.setManaged(false);
        StackPane.setAlignment(errorOverlay, Pos.BOTTOM_LEFT);

        getChildren().addAll(sandboxContainer, errorOverlay);
    }

    /** Replace the mounted snippet root. Must be called on FX thread. */
    public void setSnippetRoot(Parent root) {
        this.currentRoot = root;
        if (root == null) {
            sandboxContainer.getChildren().clear();
        } else {
            sandboxContainer.getChildren().setAll(root);
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
