package com.jfxtutor.engine.runtime;

import javafx.scene.Parent;

/**
 * One generation of a compiled snippet. Holds the ClassLoader that defined the
 * snippet class and the {@link Parent} it produced. Closing the session detaches
 * the Parent from any host and closes the ClassLoader so its classes can be GC'd.
 */
public final class SnippetSession implements AutoCloseable {

    private final SnippetClassLoader classLoader;
    private Parent root;

    public SnippetSession(SnippetClassLoader classLoader, Parent root) {
        this.classLoader = classLoader;
        this.root = root;
    }

    public Parent getRoot() { return root; }
    public SnippetClassLoader getClassLoader() { return classLoader; }

    @Override
    public void close() {
        if (root != null && root.getParent() instanceof javafx.scene.layout.Pane parent) {
            parent.getChildren().remove(root);
        }
        root = null;
        try {
            classLoader.close();
        } catch (Exception ignored) {
            // best-effort
        }
    }
}
