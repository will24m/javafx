package com.jfxtutor.engine.inspect;

import javafx.scene.Node;
import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Root-agnostic scene-graph walker.
 * Builds a tree of {@link NodeRef}s and supports DFS iteration.
 * No JavaFX UI imports — pure model logic reusable from tests and challenge assertions.
 */
public class NodeInspector {

    private final Parent root;

    public NodeInspector(Parent root) {
        this.root = root;
    }

    public Parent getRoot() { return root; }

    /** Build the full subtree rooted at {@code root}. */
    public InspectorNode buildTree() {
        return buildNode(root);
    }

    /** Walk every node in DFS order, calling {@code visitor} for each. */
    public void walk(Consumer<Node> visitor) {
        walkNode(root, visitor);
    }

    /** Collect all nodes matching the predicate. */
    public List<Node> findAll(java.util.function.Predicate<Node> pred) {
        List<Node> result = new ArrayList<>();
        walk(n -> { if (pred.test(n)) result.add(n); });
        return result;
    }

    /** Count all nodes in the subtree (including root). */
    public int count() {
        int[] n = {0};
        walk(node -> n[0]++);
        return n[0];
    }

    // ── internals ─────────────────────────────────────────────────────────────

    private InspectorNode buildNode(Node node) {
        InspectorNode in = new InspectorNode(new NodeRef(node));
        if (node instanceof Parent p) {
            for (Node child : p.getChildrenUnmodifiable()) {
                in.children().add(buildNode(child));
            }
        }
        return in;
    }

    private void walkNode(Node node, Consumer<Node> visitor) {
        visitor.accept(node);
        if (node instanceof Parent p) {
            for (Node child : p.getChildrenUnmodifiable()) {
                walkNode(child, visitor);
            }
        }
    }

    /** Mutable tree node wrapping a {@link NodeRef}. */
    public static class InspectorNode {
        private final NodeRef ref;
        private final List<InspectorNode> children = new ArrayList<>();

        public InspectorNode(NodeRef ref) { this.ref = ref; }
        public NodeRef ref() { return ref; }
        public List<InspectorNode> children() { return children; }
    }
}
