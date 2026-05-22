package com.jfxtutor.ui;

import com.jfxtutor.util.AppLog;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
    private final TextArea errorArea;
    private final Label emptyPlaceholder;

    public PreviewHost() {
        AppLog.info("preview", "Creating preview sandbox and error overlay.");
        getStyleClass().add("preview-host");
        setId("previewHost");
        setPadding(new javafx.geometry.Insets(10));
        setAccessibleRole(AccessibleRole.PARENT);
        setAccessibleText("Live preview");
        setAccessibleHelp("Shows the rendered output of the current Java snippet");

        this.sandboxContainer = new StackPane();
        sandboxContainer.getStyleClass().addAll("preview-sandbox", "sandbox-root");
        sandboxContainer.setAccessibleRole(AccessibleRole.PARENT);
        sandboxContainer.setAccessibleText("Snippet output");

        // Empty-state placeholder shown before the first successful compile.
        this.emptyPlaceholder = new Label("Edit the snippet on the left\nand the preview will appear here.");
        emptyPlaceholder.getStyleClass().add("preview-empty-label");
        emptyPlaceholder.setWrapText(true);
        emptyPlaceholder.setAccessibleRole(AccessibleRole.TEXT);
        emptyPlaceholder.setAccessibleText("Preview empty — waiting for snippet");
        sandboxContainer.getChildren().add(emptyPlaceholder);

        // Error overlay: monospace scrollable area + header label.
        Label errorHeader = new Label("⚠ Compile error");
        errorHeader.getStyleClass().add("preview-error-header");

        this.errorArea = new TextArea();
        errorArea.setEditable(false);
        errorArea.setWrapText(false);
        errorArea.getStyleClass().add("preview-error-area");
        errorArea.setPrefRowCount(6);
        errorArea.setAccessibleRole(AccessibleRole.TEXT_AREA);
        errorArea.setAccessibleText("Compile error details");

        this.errorOverlay = new VBox(4, errorHeader, errorArea);
        errorOverlay.getStyleClass().add("preview-error-overlay");
        errorOverlay.setAlignment(Pos.TOP_LEFT);
        errorOverlay.setVisible(false);
        errorOverlay.setManaged(false);
        StackPane.setAlignment(errorOverlay, Pos.BOTTOM_LEFT);

        getChildren().addAll(sandboxContainer, errorOverlay);
    }

    /** Replace the mounted snippet root. Must be called on FX thread. */
    public void setSnippetRoot(Parent root) {
        AppLog.info("preview", root == null
                ? "Clearing snippet preview root."
                : "Mounting new snippet root: " + root.getClass().getSimpleName() + ".");
        this.currentRoot = root;
        if (root == null) {
            sandboxContainer.getChildren().setAll(emptyPlaceholder);
        } else {
            sandboxContainer.getChildren().setAll(root);
        }
        clearError();
    }

    public Parent getSnippetRoot() { return currentRoot; }

    /** The inner pane that contains snippet output — used to anchor the hover overlay. */
    public StackPane getMountPane() { return sandboxContainer; }

    /** Show an error overlay with the formatted compile/runtime message. */
    public void showError(String message) {
        AppLog.info("preview", "Displaying compile/runtime error overlay.");
        errorArea.setText(message != null ? message : "");
        errorArea.setAccessibleText("Compile error: " + (message != null ? message : ""));
        errorOverlay.setVisible(true);
        errorOverlay.setManaged(true);
        errorOverlay.toFront();
    }

    public void clearError() {
        errorArea.setText("");
        errorOverlay.setVisible(false);
        errorOverlay.setManaged(false);
    }
}
