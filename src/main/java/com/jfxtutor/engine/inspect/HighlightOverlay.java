package com.jfxtutor.engine.inspect;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Translucent selection/hover overlay drawn on top of a mount pane.
 *
 * Usage:
 *   HighlightOverlay overlay = new HighlightOverlay(mountPane);
 *   overlay.highlight(someNode, "Label");   // draw
 *   overlay.clear();                        // remove
 *
 * The overlay pane is sized to fill the mount pane via min/max bindings.
 * It must be added as the topmost child of the mount pane by the caller.
 * Mouse events pass through (TRANSPARENT pick mode) so normal interaction
 * with the preview continues uninterrupted.
 */
public class HighlightOverlay {

    private final Pane overlayPane;
    private final Rectangle selBox;
    private final Text nameTag;

    public HighlightOverlay(Pane mountPane) {
        selBox = new Rectangle();
        selBox.setFill(Color.rgb(110, 168, 255, 0.12));
        selBox.setStroke(Color.rgb(110, 168, 255, 0.85));
        selBox.setStrokeWidth(1.5);
        selBox.setArcWidth(3);
        selBox.setArcHeight(3);
        selBox.setVisible(false);
        selBox.setMouseTransparent(true);

        nameTag = new Text();
        nameTag.setFont(Font.font("Menlo", FontWeight.NORMAL, 10));
        nameTag.setFill(Color.rgb(200, 220, 255, 0.95));
        nameTag.setVisible(false);
        nameTag.setMouseTransparent(true);

        overlayPane = new Pane(selBox, nameTag);
        overlayPane.setPickOnBounds(false);
        overlayPane.setMouseTransparent(true);
        overlayPane.minWidthProperty().bind(mountPane.widthProperty());
        overlayPane.minHeightProperty().bind(mountPane.heightProperty());
        overlayPane.maxWidthProperty().bind(mountPane.widthProperty());
        overlayPane.maxHeightProperty().bind(mountPane.heightProperty());
    }

    /**
     * Draw the overlay around {@code node} with a label tag.
     * {@code label} may be null to skip the name tag.
     */
    public void highlight(Node node, String label) {
        if (node == null || overlayPane.getScene() == null) {
            clear();
            return;
        }
        Bounds sceneBounds = node.localToScene(node.getBoundsInLocal());
        Bounds localBounds = overlayPane.sceneToLocal(sceneBounds);

        double x = localBounds.getMinX() - 2;
        double y = localBounds.getMinY() - 2;
        double w = localBounds.getWidth() + 4;
        double h = localBounds.getHeight() + 4;

        selBox.setX(x);
        selBox.setY(y);
        selBox.setWidth(Math.max(w, 4));
        selBox.setHeight(Math.max(h, 4));
        selBox.setVisible(true);

        if (label != null && !label.isBlank()) {
            nameTag.setText(label);
            double tagX = Math.max(x, 2);
            double tagY = Math.max(y - 3, 10);
            nameTag.setX(tagX);
            nameTag.setY(tagY);
            nameTag.setVisible(true);
        } else {
            nameTag.setVisible(false);
        }
    }

    public void clear() {
        selBox.setVisible(false);
        nameTag.setVisible(false);
    }

    public Pane getPane() { return overlayPane; }
}
