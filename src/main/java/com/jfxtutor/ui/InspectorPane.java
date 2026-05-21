package com.jfxtutor.ui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Live scene-graph inspector. Pointed at a {@link Parent} (typically the snippet
 * preview root), it builds a TreeView of every descendant node and displays a
 * properties table for whichever node is selected. The tree refreshes whenever
 * a new root is set; selection-driven property updates re-poll on demand.
 */
public class InspectorPane extends VBox {

    private final TreeView<Node> tree;
    private final TableView<PropertyRow> table;
    private final Label header;
    private final Label summary;
    private Parent currentRoot;
    private ChangeListener<Number> sizeListener;
    private Node listenedNode;

    public InspectorPane() {
        getStyleClass().add("inspector-pane");
        setId("inspectorPane");

        this.header = new Label("Inspector");
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
                    setStyle("");
                } else {
                    setText(describe(node));
                    setStyle("");
                }
            }
        });
        VBox.setVgrow(tree, Priority.ALWAYS);

        this.table = new TableView<>();
        table.getStyleClass().add("inspector-table");
        table.setPlaceholder(new Label("Select a node to inspect."));

        TableColumn<PropertyRow, String> nameCol = new TableColumn<>("Property");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(110);
        TableColumn<PropertyRow, String> valueCol = new TableColumn<>("Value");
        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueCol.setPrefWidth(180);
        table.getColumns().add(nameCol);
        table.getColumns().add(valueCol);
        VBox.setVgrow(table, Priority.ALWAYS);

        SplitPane split = new SplitPane(tree, table);
        split.setOrientation(javafx.geometry.Orientation.VERTICAL);
        split.setDividerPositions(0.55);
        VBox.setVgrow(split, Priority.ALWAYS);

        getChildren().addAll(header, summary, split);

        tree.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> populateTable(sel == null ? null : sel.getValue()));
    }

    /** Re-root the inspector. Call after each snippet mount. */
    public void setRoot(Parent root) {
        detachSizeListener();
        this.currentRoot = root;
        if (root == null) {
            tree.setRoot(null);
            summary.setText("No preview mounted.");
            table.getItems().clear();
            return;
        }
        TreeItem<Node> treeRoot = buildTree(root);
        treeRoot.setExpanded(true);
        tree.setRoot(treeRoot);
        tree.getSelectionModel().select(treeRoot);
        attachSizeListener(root);
        updateSummary(root);
    }

    private void attachSizeListener(Parent root) {
        listenedNode = root;
        sizeListener = (obs, old, val) -> updateSummary(listenedNode);
        if (root instanceof javafx.scene.layout.Region region) {
            region.widthProperty().addListener(sizeListener);
            region.heightProperty().addListener(sizeListener);
        }
    }

    private void detachSizeListener() {
        if (listenedNode instanceof javafx.scene.layout.Region region && sizeListener != null) {
            region.widthProperty().removeListener(sizeListener);
            region.heightProperty().removeListener(sizeListener);
        }
        sizeListener = null;
        listenedNode = null;
    }

    private void updateSummary(Node root) {
        if (root == null) return;
        int count = countDescendants(root);
        String size = root instanceof javafx.scene.layout.Region r
                ? String.format(Locale.ROOT, "%.0f × %.0f", r.getWidth(), r.getHeight())
                : "—";
        summary.setText(count + " nodes  ·  root size " + size);
    }

    private TreeItem<Node> buildTree(Node node) {
        TreeItem<Node> item = new TreeItem<>(node);
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                item.getChildren().add(buildTree(child));
            }
        }
        return item;
    }

    private int countDescendants(Node node) {
        int n = 1;
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                n += countDescendants(child);
            }
        }
        return n;
    }

    private void populateTable(Node node) {
        List<PropertyRow> rows = new ArrayList<>();
        if (node == null) {
            table.getItems().setAll(rows);
            return;
        }
        rows.add(row("type",      node.getClass().getSimpleName()));
        rows.add(row("id",        node.getId() == null ? "—" : node.getId()));
        rows.add(row("styleClass", node.getStyleClass().isEmpty()
                ? "—" : String.join(" ", node.getStyleClass())));
        rows.add(row("layoutX",   formatDouble(node.getLayoutX())));
        rows.add(row("layoutY",   formatDouble(node.getLayoutY())));

        var bounds = node.getBoundsInLocal();
        rows.add(row("width",     formatDouble(bounds.getWidth())));
        rows.add(row("height",    formatDouble(bounds.getHeight())));

        rows.add(row("visible",   String.valueOf(node.isVisible())));
        rows.add(row("managed",   String.valueOf(node.isManaged())));
        rows.add(row("opacity",   formatDouble(node.getOpacity())));

        if (node instanceof javafx.scene.layout.Region region) {
            rows.add(row("prefWidth",  formatDouble(region.getPrefWidth())));
            rows.add(row("prefHeight", formatDouble(region.getPrefHeight())));
            rows.add(row("padding",    region.getPadding().toString()));
        }
        if (node instanceof javafx.scene.control.Labeled labeled) {
            String text = labeled.getText();
            rows.add(row("text", text == null ? "—" : text));
        }

        table.getItems().setAll(rows);
    }

    private static PropertyRow row(String name, String value) {
        return new PropertyRow(name, value);
    }

    private static String formatDouble(double d) {
        if (Double.isNaN(d) || d == -1.0) return "—";
        return String.format(Locale.ROOT, "%.1f", d);
    }

    private static String describe(Node node) {
        StringBuilder sb = new StringBuilder(node.getClass().getSimpleName());
        if (node.getId() != null) sb.append(" #").append(node.getId());
        if (!node.getStyleClass().isEmpty()) {
            for (String sc : node.getStyleClass()) sb.append(".").append(sc);
        }
        if (node instanceof javafx.scene.control.Labeled labeled
                && labeled.getText() != null
                && !labeled.getText().isBlank()) {
            String t = labeled.getText().trim();
            if (t.length() > 24) t = t.substring(0, 24) + "…";
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
