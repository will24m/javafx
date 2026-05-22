package com.jfxtutor.ui;

import com.jfxtutor.engine.inspect.HighlightOverlay;
import com.jfxtutor.engine.inspect.NodeInspector;
import com.jfxtutor.engine.inspect.NodeRef;
import com.jfxtutor.engine.inspect.PropertyWatcher;
import com.jfxtutor.util.AppLog;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Scene-graph inspector — now backed by {@link NodeInspector}, {@link PropertyWatcher},
 * and {@link HighlightOverlay} from {@code engine/inspect/}.
 *
 * Three tabs:
 *   Properties — bounds, size, id, styleClass, text (live-updating)
 *   CSS         — CssMetaData property names for the selected node
 *   Bounds viz  — boundsInLocal vs boundsInParent as nested rectangle descriptions
 *
 * Mirror mode: toggle button re-roots the inspector on the host MainView scene graph.
 * Hover-to-highlight: when hovering over the preview (PreviewHost), draws an overlay.
 */
public class InspectorPane extends VBox {

    // ── engine layer ─────────────────────────────────────────────────────────
    private NodeInspector inspector;
    private final PropertyWatcher selectionWatcher = new PropertyWatcher();
    private final PropertyWatcher rootWatcher = new PropertyWatcher();

    // ── UI ───────────────────────────────────────────────────────────────────
    private final TreeView<Node> tree;
    private final TableView<PropertyRow> propTable;
    private final TableView<PropertyRow> cssTable;
    private final TableView<PropertyRow> boundsTable;
    private final Label summary;
    private final ToggleButton mirrorBtn;

    // ── overlay ───────────────────────────────────────────────────────────────
    private HighlightOverlay overlay;
    /** The PreviewHost's mount pane — set once by MainView. */
    private PreviewHost previewHost;

    // ── mirror mode ──────────────────────────────────────────────────────────
    /** Root of the host app's own scene — supplied by MainView for Mirror mode. */
    private Parent hostRoot;
    /** Snapshot of the snippet preview root so we can toggle back from mirror. */
    private Parent snippetRoot;
    private boolean mirrorMode = false;

    public InspectorPane() {
        AppLog.info("inspector", "Creating scene graph inspector (v2: engine/inspect + Mirror mode).");
        getStyleClass().add("inspector-pane");
        setId("inspectorPane");

        // ── header row ────────────────────────────────────────────────────────
        Label header = new Label("Inspector");
        header.getStyleClass().add("nav-header");

        mirrorBtn = new ToggleButton("Mirror");
        mirrorBtn.getStyleClass().addAll("ide-button", "inspector-mirror-btn");
        mirrorBtn.setOnAction(e -> toggleMirrorMode());
        mirrorBtn.setAccessibleRole(AccessibleRole.TOGGLE_BUTTON);
        mirrorBtn.setAccessibleText("Mirror mode");
        mirrorBtn.setAccessibleHelp("When on, the inspector reflects the application's own scene graph instead of the snippet preview");

        HBox headerRow = new HBox(6, header, mirrorBtn);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(header, Priority.ALWAYS);

        this.summary = new Label("No preview mounted yet.");
        summary.getStyleClass().add("inspector-summary");
        summary.setWrapText(true);
        summary.setPadding(new Insets(0, 8, 6, 8));

        // ── tree ─────────────────────────────────────────────────────────────
        this.tree = new TreeView<>();
        tree.setShowRoot(true);
        tree.getStyleClass().add("inspector-tree");
        tree.setAccessibleRole(AccessibleRole.TREE_VIEW);
        tree.setAccessibleText("Scene graph tree");
        tree.setAccessibleHelp("Shows the JavaFX node hierarchy of the current preview");
        tree.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(Node node, boolean empty) {
                super.updateItem(node, empty);
                if (empty || node == null) { setText(null); return; }
                setText(new NodeRef(node).displayLabel());
            }
        });
        VBox.setVgrow(tree, Priority.ALWAYS);

        // ── Properties tab ───────────────────────────────────────────────────
        this.propTable = makeTable();
        Tab propTab = new Tab("Properties", propTable);
        propTab.setClosable(false);

        // ── CSS tab ───────────────────────────────────────────────────────────
        this.cssTable = makeTable();
        cssTable.setPlaceholder(new Label("Select a node to see CSS metadata."));
        ScrollPane cssScroll = new ScrollPane(cssTable);
        cssScroll.setFitToWidth(true);
        Tab cssTab = new Tab("CSS", cssTable);
        cssTab.setClosable(false);

        // ── Bounds tab ────────────────────────────────────────────────────────
        this.boundsTable = makeTable();
        boundsTable.setPlaceholder(new Label("Select a node to see bounds."));
        Tab boundsTab = new Tab("Bounds", boundsTable);
        boundsTab.setClosable(false);

        TabPane tabs = new TabPane(propTab, cssTab, boundsTab);
        tabs.getStyleClass().add("inspector-tabs");
        tabs.setAccessibleRole(AccessibleRole.TAB_PANE);
        tabs.setAccessibleText("Node detail tabs");
        tabs.setAccessibleHelp("Properties, CSS metadata, and bounds information for the selected node");
        VBox.setVgrow(tabs, Priority.ALWAYS);

        // ── split: tree on top, tabs on bottom ───────────────────────────────
        SplitPane split = new SplitPane(tree, tabs);
        split.setOrientation(Orientation.VERTICAL);
        split.setDividerPositions(0.55);
        VBox.setVgrow(split, Priority.ALWAYS);

        getChildren().addAll(headerRow, summary, split);

        // ── selection listener ────────────────────────────────────────────────
        tree.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            Node node = sel == null ? null : sel.getValue();
            selectionWatcher.watch(node, this::refreshAllTabs);
            refreshAllTabs();
            if (overlay != null && node != null && !mirrorMode) {
                overlay.highlight(node, new NodeRef(node).typeName());
            } else if (overlay != null) {
                overlay.clear();
            }
        });
    }

    // ── public API ───────────────────────────────────────────────────────────

    /**
     * Called by MainView after the preview pane is constructed.
     * Sets up hover-to-highlight over the preview.
     */
    public void setPreviewHost(PreviewHost host) {
        this.previewHost = host;
        overlay = new HighlightOverlay(host.getMountPane());
        host.getMountPane().getChildren().add(overlay.getPane());

        host.getMountPane().setOnMouseMoved(e -> {
            if (mirrorMode || inspector == null) return;
            Node hit = pickNode(inspector.getRoot(), e.getX(), e.getY());
            if (hit != null) {
                overlay.highlight(hit, new NodeRef(hit).typeName());
            } else {
                overlay.clear();
            }
        });
        host.getMountPane().setOnMouseExited(e -> {
            if (overlay != null) overlay.clear();
        });
        host.getMountPane().setOnMouseClicked(e -> {
            if (mirrorMode || inspector == null) return;
            Node hit = pickNode(inspector.getRoot(), e.getX(), e.getY());
            if (hit != null) selectNodeInTree(hit);
        });
    }

    /** Pass the host's own scene root so Mirror mode can inspect the app itself. */
    public void setHostRoot(Parent root) {
        this.hostRoot = root;
    }

    /** Re-root the inspector on a new snippet preview root. */
    public void setRoot(Parent root) {
        AppLog.info("inspector", root == null
                ? "Inspector root cleared."
                : "Inspecting: " + root.getClass().getSimpleName());
        this.snippetRoot = root;
        if (!mirrorMode) applyRoot(root);
    }

    public Parent getCurrentRoot() {
        return inspector == null ? null : inspector.getRoot();
    }

    // ── mirror mode ──────────────────────────────────────────────────────────

    private void toggleMirrorMode() {
        mirrorMode = mirrorBtn.isSelected();
        if (mirrorMode) {
            AppLog.info("inspector", "Mirror mode ON — inspecting host scene graph.");
            if (overlay != null) overlay.clear();
            applyRoot(hostRoot);
        } else {
            AppLog.info("inspector", "Mirror mode OFF — back to snippet root.");
            applyRoot(snippetRoot);
        }
    }

    private void applyRoot(Parent root) {
        rootWatcher.detachAll();
        selectionWatcher.detachAll();
        if (overlay != null) overlay.clear();

        if (root == null) {
            inspector = null;
            tree.setRoot(null);
            summary.setText("No preview mounted.");
            propTable.getItems().clear();
            cssTable.getItems().clear();
            boundsTable.getItems().clear();
            return;
        }

        inspector = new NodeInspector(root);
        TreeItem<Node> treeRoot = buildTreeItem(inspector.buildTree());
        expandAll(treeRoot);
        tree.setRoot(treeRoot);
        tree.getSelectionModel().select(treeRoot);

        rootWatcher.watchRoot(root, () -> Platform.runLater(() -> updateSummary(root)));
        Platform.runLater(() -> updateSummary(root));
    }

    // ── tree construction ─────────────────────────────────────────────────────

    private TreeItem<Node> buildTreeItem(NodeInspector.InspectorNode in) {
        TreeItem<Node> item = new TreeItem<>(in.ref().node());
        for (NodeInspector.InspectorNode child : in.children()) {
            item.getChildren().add(buildTreeItem(child));
        }
        return item;
    }

    private void expandAll(TreeItem<?> item) {
        item.setExpanded(true);
        for (TreeItem<?> c : item.getChildren()) expandAll(c);
    }

    // ── hover pick ────────────────────────────────────────────────────────────

    /**
     * Picks the deepest node whose scene bounds contain (sceneX, sceneY).
     * localX/Y are relative to the mount pane.
     */
    private Node pickNode(Parent root, double localX, double localY) {
        if (root == null || previewHost == null) return null;
        // Convert mount-pane-local coords to scene coords.
        javafx.geometry.Point2D scene = previewHost.getMountPane().localToScene(localX, localY);
        return pickDeepest(root, scene.getX(), scene.getY());
    }

    private Node pickDeepest(Node node, double sceneX, double sceneY) {
        if (!node.isVisible()) return null;
        Bounds sceneBounds = node.localToScene(node.getBoundsInLocal());
        if (!sceneBounds.contains(sceneX, sceneY)) return null;
        if (node instanceof Parent p) {
            // Prefer the deepest (last rendered on top) matching child.
            List<Node> children = p.getChildrenUnmodifiable();
            for (int i = children.size() - 1; i >= 0; i--) {
                Node hit = pickDeepest(children.get(i), sceneX, sceneY);
                if (hit != null) return hit;
            }
        }
        return node;
    }

    private void selectNodeInTree(Node target) {
        selectInTree(tree.getRoot(), target);
    }

    private boolean selectInTree(TreeItem<Node> item, Node target) {
        if (item == null) return false;
        if (item.getValue() == target) {
            tree.getSelectionModel().select(item);
            int idx = tree.getRow(item);
            tree.scrollTo(idx);
            return true;
        }
        for (TreeItem<Node> child : item.getChildren()) {
            if (selectInTree(child, target)) return true;
        }
        return false;
    }

    // ── summary ───────────────────────────────────────────────────────────────

    private void updateSummary(Node root) {
        if (root == null) { summary.setText("No preview mounted."); return; }
        String mode = mirrorMode ? " [MIRROR]" : "";
        int count = inspector == null ? 0 : inspector.count();
        String size = new NodeRef((Node) root).sizeString();
        summary.setText(count + " nodes  ·  " + size + mode);
    }

    // ── tab refresh ───────────────────────────────────────────────────────────

    private void refreshAllTabs() {
        Node node = selectionWatcher.getObserved();
        refreshPropTable(node);
        refreshCssTable(node);
        refreshBoundsTable(node);
    }

    private void refreshPropTable(Node node) {
        List<PropertyRow> rows = new ArrayList<>();
        if (node == null) { propTable.getItems().setAll(rows); return; }
        NodeRef ref = new NodeRef(node);
        rows.add(row("type",       ref.typeName()));
        rows.add(row("id",         orDash(ref.id())));
        rows.add(row("styleClass", orDash(ref.styleClasses())));
        rows.add(row("visible",    String.valueOf(node.isVisible())));
        rows.add(row("managed",    String.valueOf(node.isManaged())));
        rows.add(row("opacity",    fmt(node.getOpacity())));
        Bounds inParent = node.getBoundsInParent();
        rows.add(row("x",      fmt(inParent.getMinX())));
        rows.add(row("y",      fmt(inParent.getMinY())));
        rows.add(row("width",  fmt(inParent.getWidth())));
        rows.add(row("height", fmt(inParent.getHeight())));
        if (node instanceof Region r) {
            rows.add(row("prefWidth",  fmtPref(r.getPrefWidth())));
            rows.add(row("prefHeight", fmtPref(r.getPrefHeight())));
            rows.add(row("padding",    r.getPadding().toString()));
        }
        if (node instanceof javafx.scene.control.Labeled l && l.getText() != null) {
            rows.add(row("text", orDash(l.getText())));
        }
        propTable.getItems().setAll(rows);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void refreshCssTable(Node node) {
        List<PropertyRow> rows = new ArrayList<>();
        if (node instanceof Styleable s) {
            List<CssMetaData<? extends Styleable, ?>> meta = s.getCssMetaData();
            for (CssMetaData<?, ?> m : meta) {
                String val = "—";
                try {
                    var sp = ((CssMetaData) m).getStyleableProperty((Styleable) node);
                    if (sp != null && sp.getValue() != null) {
                        val = sp.getValue().toString();
                        if (val.length() > 60) val = val.substring(0, 60) + "…";
                    }
                } catch (Exception ignored) {}
                rows.add(row(m.getProperty(), val));
            }
        }
        cssTable.getItems().setAll(rows);
    }

    private void refreshBoundsTable(Node node) {
        List<PropertyRow> rows = new ArrayList<>();
        if (node == null) { boundsTable.getItems().setAll(rows); return; }
        Bounds local  = node.getBoundsInLocal();
        Bounds parent = node.getBoundsInParent();
        Bounds layout = node.getLayoutBounds();
        rows.add(row("layoutBounds",    fmtBounds(layout)));
        rows.add(row("boundsInLocal",   fmtBounds(local)));
        rows.add(row("boundsInParent",  fmtBounds(parent)));
        rows.add(row("localW×H",        fmt(local.getWidth()) + " × " + fmt(local.getHeight())));
        rows.add(row("parentW×H",       fmt(parent.getWidth()) + " × " + fmt(parent.getHeight())));
        rows.add(row("layoutX",         fmt(node.getLayoutX())));
        rows.add(row("layoutY",         fmt(node.getLayoutY())));
        rows.add(row("translateX",      fmt(node.getTranslateX())));
        rows.add(row("translateY",      fmt(node.getTranslateY())));
        boundsTable.getItems().setAll(rows);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private TableView<PropertyRow> makeTable() {
        TableView<PropertyRow> t = new TableView<>();
        t.getStyleClass().add("inspector-table");
        t.setPlaceholder(new Label("Select a node to inspect."));
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        TableColumn<PropertyRow, String> nameCol = new TableColumn<>("Property");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(90);
        TableColumn<PropertyRow, String> valueCol = new TableColumn<>("Value");
        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueCol.setPrefWidth(170);
        t.getColumns().add(nameCol);
        t.getColumns().add(valueCol);
        VBox.setVgrow(t, Priority.ALWAYS);
        return t;
    }

    private static PropertyRow row(String name, String value) {
        return new PropertyRow(name, value);
    }

    private static String fmt(double d) {
        if (Double.isNaN(d)) return "—";
        return String.format(Locale.ROOT, "%.1f", d);
    }

    private static String fmtPref(double d) {
        return d == Region.USE_COMPUTED_SIZE ? "auto" : String.format(Locale.ROOT, "%.0f", d);
    }

    private static String fmtBounds(Bounds b) {
        return String.format(Locale.ROOT, "(%.1f, %.1f) %.1f×%.1f",
                b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    private static String orDash(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }

    /** Simple POJO for the TableView's PropertyValueFactory. */
    public static class PropertyRow {
        private final ReadOnlyObjectWrapper<String> name;
        private final ReadOnlyObjectWrapper<String> value;
        public PropertyRow(String n, String v) {
            this.name  = new ReadOnlyObjectWrapper<>(n);
            this.value = new ReadOnlyObjectWrapper<>(v);
        }
        public String getName()  { return name.get(); }
        public String getValue() { return value.get(); }
    }
}
