package com.jfxtutor.ui;

import com.jfxtutor.util.AppLog;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Live scene-graph inspector. Pointed at a {@link Parent} (typically the snippet
 * preview root), it builds a TreeView of every descendant Node and shows a
 * property table for whichever node is selected. The properties table re-polls
 * on every layout pulse for the selected node so sizes / bounds reflect the
 * actual rendered state instead of pre-layout zeros.
 */
public class InspectorPane extends VBox {

    private final TreeView<Node> tree;
    private final TableView<PropertyRow> table;
    private final Label summary;

    private Parent currentRoot;

    /** Listens to layout invalidations on the SELECTED node so we re-poll bounds. */
    private final InvalidationListener selectedBoundsListener = obs -> refreshTable();
    private Node observedNode;

    /** Listens to width/height of the root for the summary line. */
    private final ChangeListener<Number> rootSizeListener = (o, a, b) -> updateSummary(currentRoot);

    public InspectorPane() {
        AppLog.info("inspector", "Creating scene graph inspector tree and property table.");
        getStyleClass().add("inspector-pane");
        setId("inspectorPane");

        Label header = new Label("Inspector");
        header.getStyleClass().add("nav-header");

        this.summary = new Label("No preview mounted yet.");
        summary.getStyleClass().add("inspector-summary");
        summary.setWrapText(true);

        this.tree = new TreeView<>();
        tree.setShowRoot(true);
        tree.getStyleClass().add("inspector-tree");
        tree.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(Node node, boolean empty) {
                super.updateItem(node, empty);
                if (empty || node == null) {
                    setText(null);
                } else {
                    setText(describe(node));
                }
            }
        });
        VBox.setVgrow(tree, Priority.ALWAYS);

        this.table = new TableView<>();
        table.getStyleClass().add("inspector-table");
        table.setPlaceholder(new Label("Select a node to inspect."));
        // Constrained resize so the two columns share the table width 35/65.
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<PropertyRow, String> nameCol = new TableColumn<>("Property");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setMaxWidth(Double.MAX_VALUE);
        nameCol.setMinWidth(70);

        TableColumn<PropertyRow, String> valueCol = new TableColumn<>("Value");
        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueCol.setMaxWidth(Double.MAX_VALUE);
        valueCol.setMinWidth(120);

        table.getColumns().add(nameCol);
        table.getColumns().add(valueCol);

        // Initial weights: ~35% property, 65% value, redistributed by the constrained policy.
        nameCol.setPrefWidth(85);
        valueCol.setPrefWidth(165);

        VBox.setVgrow(table, Priority.ALWAYS);

        SplitPane split = new SplitPane(tree, table);
        split.setOrientation(javafx.geometry.Orientation.VERTICAL);
        split.setDividerPositions(0.62);   // tree gets more space than props
        VBox.setVgrow(split, Priority.ALWAYS);

        getChildren().addAll(header, summary, split);

        // Selection changes decide which node the property table polls.
        // The tree item stores the actual JavaFX Node, not a copy.
        tree.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            observeNode(sel == null ? null : sel.getValue());
            refreshTable();
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Root + selection lifecycle
    // ─────────────────────────────────────────────────────────────────────────

    /** Re-root the inspector. Call after each snippet mount. */
    public void setRoot(Parent root) {
        AppLog.info("inspector", root == null
                ? "Inspector root cleared."
                : "Inspecting new preview root: " + root.getClass().getSimpleName() + ".");
        detachRootSizeListener();
        observeNode(null);
        this.currentRoot = root;

        if (root == null) {
            tree.setRoot(null);
            summary.setText("No preview mounted.");
            table.getItems().clear();
            return;
        }

        TreeItem<Node> treeRoot = buildTree(root);
        expandAll(treeRoot);
        tree.setRoot(treeRoot);
        tree.getSelectionModel().select(treeRoot);

        attachRootSizeListener(root);
        // Defer one pulse so layout has run before we read sizes.
        Platform.runLater(() -> updateSummary(root));
    }

    private void observeNode(Node node) {
        // Remove listeners from the previous selected node before observing the
        // new one. Without this, stale nodes would continue triggering refreshes.
        if (observedNode != null) {
            observedNode.boundsInParentProperty().removeListener(selectedBoundsListener);
            observedNode.boundsInLocalProperty().removeListener(selectedBoundsListener);
            observedNode.layoutXProperty().removeListener(selectedBoundsListener);
            observedNode.layoutYProperty().removeListener(selectedBoundsListener);
        }
        observedNode = node;
        if (node != null) {
            AppLog.info("inspector", "Property table is now following node: " + describe(node));
            node.boundsInParentProperty().addListener(selectedBoundsListener);
            node.boundsInLocalProperty().addListener(selectedBoundsListener);
            node.layoutXProperty().addListener(selectedBoundsListener);
            node.layoutYProperty().addListener(selectedBoundsListener);
        }
    }

    private void attachRootSizeListener(Parent root) {
        if (root instanceof Region r) {
            r.widthProperty().addListener(rootSizeListener);
            r.heightProperty().addListener(rootSizeListener);
        }
    }

    private void detachRootSizeListener() {
        if (currentRoot instanceof Region r) {
            r.widthProperty().removeListener(rootSizeListener);
            r.heightProperty().removeListener(rootSizeListener);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tree construction
    // ─────────────────────────────────────────────────────────────────────────

    private TreeItem<Node> buildTree(Node node) {
        // Recursively mirror the JavaFX scene graph into TreeItems. Parent nodes
        // expose children through getChildrenUnmodifiable(), which is safe for
        // read-only inspection.
        TreeItem<Node> item = new TreeItem<>(node);
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                item.getChildren().add(buildTree(child));
            }
        }
        return item;
    }

    private void expandAll(TreeItem<?> item) {
        item.setExpanded(true);
        for (TreeItem<?> c : item.getChildren()) expandAll(c);
    }

    private int countDescendants(Node node) {
        // Count includes the root node itself, so a single Label preview reports
        // as one node rather than zero descendants.
        int n = 1;
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                n += countDescendants(child);
            }
        }
        return n;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Summary + table refresh
    // ─────────────────────────────────────────────────────────────────────────

    private void updateSummary(Node root) {
        // Root size is meaningful only for Regions because plain Parent does not
        // have width/height properties.
        if (root == null) {
            summary.setText("No preview mounted.");
            return;
        }
        int count = countDescendants(root);
        String size = root instanceof Region r
                ? String.format(Locale.ROOT, "%.0f × %.0f", r.getWidth(), r.getHeight())
                : "—";
        summary.setText(count + " nodes  ·  root size " + size);
    }

    private void refreshTable() {
        // Pull a fresh snapshot from the selected node. Bounds and layout values
        // can change after every JavaFX pulse, so this table is intentionally
        // rebuilt rather than cached.
        Node node = observedNode;
        List<PropertyRow> rows = new ArrayList<>();
        if (node == null) {
            table.getItems().setAll(rows);
            return;
        }

        rows.add(row("type",       node.getClass().getSimpleName()));
        rows.add(row("id",         orDash(node.getId())));
        rows.add(row("styleClass", node.getStyleClass().isEmpty()
                ? "—" : String.join(" ", node.getStyleClass())));

        // Layout bounds reflect the actual placement after layout.
        Bounds inParent = node.getBoundsInParent();
        rows.add(row("x",      formatDouble(inParent.getMinX())));
        rows.add(row("y",      formatDouble(inParent.getMinY())));
        rows.add(row("width",  formatDouble(inParent.getWidth())));
        rows.add(row("height", formatDouble(inParent.getHeight())));

        rows.add(row("visible", String.valueOf(node.isVisible())));
        rows.add(row("managed", String.valueOf(node.isManaged())));
        rows.add(row("opacity", formatDouble(node.getOpacity())));

        if (node instanceof Region region) {
            // Region-specific values matter for layout lessons because VBox,
            // HBox, BorderPane, etc. all inherit from Region.
            rows.add(row("prefSize", String.format(Locale.ROOT, "%s × %s",
                    formatPref(region.getPrefWidth()),
                    formatPref(region.getPrefHeight()))));
            rows.add(row("padding",  region.getPadding().toString()));
        }
        if (node instanceof javafx.scene.control.Labeled labeled) {
            // Labels, Buttons, Hyperlinks, and many controls expose text via
            // Labeled, so this catches the common teaching examples.
            rows.add(row("text", orDash(labeled.getText())));
        }

        table.getItems().setAll(rows);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Formatting helpers
    // ─────────────────────────────────────────────────────────────────────────

    private static PropertyRow row(String name, String value) {
        return new PropertyRow(name, value);
    }

    private static String formatDouble(double d) {
        if (Double.isNaN(d)) return "—";
        return String.format(Locale.ROOT, "%.1f", d);
    }

    private static String formatPref(double d) {
        // -1.0 means USE_COMPUTED_SIZE in JavaFX.
        if (d == Region.USE_COMPUTED_SIZE) return "auto";
        return String.format(Locale.ROOT, "%.0f", d);
    }

    private static String orDash(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }

    /**
     * Renders a node as e.g. {@code Label #title.heading "Hello"}.
     * Limits style-class display to keep the tree readable.
     */
    private static String describe(Node node) {
        StringBuilder sb = new StringBuilder(node.getClass().getSimpleName());
        if (node.getId() != null) sb.append(" #").append(node.getId());
        if (!node.getStyleClass().isEmpty()) {
            int shown = 0;
            for (String sc : node.getStyleClass()) {
                if (sc.isBlank()) continue;
                sb.append(".").append(sc);
                if (++shown >= 2) break;
            }
            if (node.getStyleClass().size() > 2) sb.append("…");
        }
        if (node instanceof javafx.scene.control.Labeled labeled
                && labeled.getText() != null
                && !labeled.getText().isBlank()) {
            String t = labeled.getText().trim();
            if (t.length() > 28) t = t.substring(0, 28) + "…";
            sb.append("  \"").append(t).append("\"");
        }
        return sb.toString();
    }

    public Parent getCurrentRoot() { return currentRoot; }

    /** Simple POJO used by the TableView's PropertyValueFactory. */
    public static class PropertyRow {
        private final ReadOnlyObjectWrapper<String> name;
        private final ReadOnlyObjectWrapper<String> value;
        public PropertyRow(String name, String value) {
            this.name = new ReadOnlyObjectWrapper<>(name);
            this.value = new ReadOnlyObjectWrapper<>(value);
        }
        public String getName()  { return name.get(); }
        public String getValue() { return value.get(); }
    }
}
