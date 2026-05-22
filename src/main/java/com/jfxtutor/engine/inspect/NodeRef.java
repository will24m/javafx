package com.jfxtutor.engine.inspect;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Region;

import java.util.Locale;

/**
 * Immutable snapshot describing a single node in the scene graph.
 * Separates "what we want to display about a node" from the live node itself.
 */
public record NodeRef(Node node) {

    public String typeName() {
        return node.getClass().getSimpleName();
    }

    public String id() {
        String id = node.getId();
        return (id == null || id.isBlank()) ? "" : id;
    }

    public String styleClasses() {
        if (node.getStyleClass().isEmpty()) return "";
        int shown = 0;
        StringBuilder sb = new StringBuilder();
        for (String sc : node.getStyleClass()) {
            if (sc.isBlank()) continue;
            sb.append(".").append(sc);
            if (++shown >= 2) break;
        }
        if (node.getStyleClass().size() > 2) sb.append("…");
        return sb.toString();
    }

    public String labelText() {
        if (node instanceof Labeled l && l.getText() != null && !l.getText().isBlank()) {
            String t = l.getText().trim();
            return t.length() > 28 ? t.substring(0, 28) + "…" : t;
        }
        return "";
    }

    public String displayLabel() {
        StringBuilder sb = new StringBuilder(typeName());
        String id = id();
        if (!id.isEmpty()) sb.append(" #").append(id);
        sb.append(styleClasses());
        String text = labelText();
        if (!text.isEmpty()) sb.append("  \"").append(text).append("\"");
        return sb.toString();
    }

    public boolean isParent() {
        return node instanceof Parent;
    }

    public String sizeString() {
        if (node instanceof Region r) {
            return String.format(Locale.ROOT, "%.0f × %.0f", r.getWidth(), r.getHeight());
        }
        return "—";
    }
}
