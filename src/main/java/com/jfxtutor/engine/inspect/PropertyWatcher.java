package com.jfxtutor.engine.inspect;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages bound listeners on a single observed node so they can all be
 * removed in one call without leaking references.
 *
 * Attach one set of listeners (bounds + layout) per node selection;
 * detach the full set when the selection changes.
 */
public class PropertyWatcher {

    private final List<Runnable> detachers = new ArrayList<>();
    private WeakReference<Node> observed = new WeakReference<>(null);

    /**
     * Stop watching the current node and start watching {@code node}.
     * Calls {@code onChange} whenever the node's layout-relevant properties change.
     */
    public void watch(Node node, Runnable onChange) {
        detachAll();
        if (node == null) return;
        observed = new WeakReference<>(node);

        InvalidationListener il = obs -> onChange.run();
        node.boundsInParentProperty().addListener(il);
        detachers.add(() -> node.boundsInParentProperty().removeListener(il));

        node.boundsInLocalProperty().addListener(il);
        detachers.add(() -> node.boundsInLocalProperty().removeListener(il));

        node.layoutXProperty().addListener(il);
        detachers.add(() -> node.layoutXProperty().removeListener(il));

        node.layoutYProperty().addListener(il);
        detachers.add(() -> node.layoutYProperty().removeListener(il));

        node.visibleProperty().addListener(il);
        detachers.add(() -> node.visibleProperty().removeListener(il));

        if (node instanceof Region r) {
            ChangeListener<Number> cl = (o, a, b) -> onChange.run();
            r.widthProperty().addListener(cl);
            detachers.add(() -> r.widthProperty().removeListener(cl));
            r.heightProperty().addListener(cl);
            detachers.add(() -> r.heightProperty().removeListener(cl));
        }
    }

    /** Watch root size only (for the summary line). */
    public void watchRoot(Node root, Runnable onChange) {
        if (root instanceof Region r) {
            ChangeListener<Number> cl = (o, a, b) -> onChange.run();
            r.widthProperty().addListener(cl);
            detachers.add(() -> r.widthProperty().removeListener(cl));
            r.heightProperty().addListener(cl);
            detachers.add(() -> r.heightProperty().removeListener(cl));
        }
    }

    public void detachAll() {
        detachers.forEach(Runnable::run);
        detachers.clear();
        observed.clear();
    }

    public Node getObserved() { return observed.get(); }
}
