package com.jfxtutor.engine.runtime;

import javafx.scene.Parent;

/**
 * One generation of a compiled snippet.
 *
 * The session holds the ClassLoader that defined the snippet class and the
 * Parent produced by build(). Closing the session detaches the Parent from
 * simple preview containers and clears the loader's retained byte arrays.
 */
public final class SnippetSession implements AutoCloseable {

    private final SnippetClassLoader classLoader;
    private Parent root;

    public SnippetSession(SnippetClassLoader classLoader, Parent root) {
        // A session ties together the UI node and the loader that defined the
        // class which created it. Replacing a preview closes the whole session.
        this.classLoader = classLoader;
        this.root = root;
    }

    public Parent getRoot() { return root; }
    public SnippetClassLoader getClassLoader() { return classLoader; }

    @Override
    public void close() {
        if (root != null) {
            // If the preview root is still attached to a simple container,
            // remove it explicitly so the old scene graph can be collected.
            javafx.scene.Parent parent = root.getParent();
            if (parent instanceof javafx.scene.layout.Pane pane) {
                pane.getChildren().remove(root);
            } else if (parent instanceof javafx.scene.Group group) {
                group.getChildren().remove(root);
            }
            // Other Parent subclasses (Control, etc.) own their children
            // internally; releasing our reference is enough for GC.
        }
        root = null;
        try {
            // Closing here means "release our byte arrays"; generated classes
            // become collectable once nothing else references them.
            classLoader.close();
        } catch (Exception ignored) {
            // best-effort
        }
    }
}
