package com.jfxtutor.ui;

import com.jfxtutor.util.AppLog;
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
        AppLog.info("preview", "Creating preview sandbox and error overlay.");
        getStyleClass().add("preview-host");
        setId("previewHost");
        setPadding(new javafx.geometry.Insets(10));

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
        // The sandbox container is the only place user snippets are inserted.
        // Replacing its children keeps old snippet UIs from lingering after a
        // successful compile.
        AppLog.info("preview", root == null
                ? "Clearing snippet preview root."
                : "Mounting new snippet root: " + root.getClass().getSimpleName() + ".");
        this.currentRoot = root;
        if (root == null) {
            sandboxContainer.getChildren().clear();
        } else {
            sandboxContainer.getChildren().setAll(root);
        }
        clearError();
    }

    public Parent getSnippetRoot() { return currentRoot; }

    /** The inner pane that contains snippet output — used to anchor the hover overlay. */
    public StackPane getMountPane() { return sandboxContainer; }

    /** Show an error banner over the current preview without unmounting it. */
    public void showError(String message) {
        // Keeping the previous successful preview visible makes compile errors
        // less disorienting: learners can still see the last working state.
        AppLog.info("preview", "Displaying compile/runtime error overlay.");
        errorLabel.setText(message);
        errorOverlay.setVisible(true);
        errorOverlay.setManaged(true);
        errorOverlay.toFront();
    }

    public void clearError() {
        // A successful mount clears any stale error from a previous failed run.
        errorLabel.setText("");
        errorOverlay.setVisible(false);
        errorOverlay.setManaged(false);
    }
}
