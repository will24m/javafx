package com.jfxtutor.engine.challenge;

import javafx.scene.Node;

import java.util.Collections;
import java.util.List;

/**
 * Outcome of evaluating one challenge assertion against the current preview root.
 *
 * @param passed     true when the assertion's condition is satisfied
 * @param message    human-readable explanation — success confirmation or failure hint
 * @param highlights nodes in the preview that should be visually flagged
 */
public record AssertionResult(boolean passed, String message, List<Node> highlights) {

    public static AssertionResult pass(String message) {
        return new AssertionResult(true, message, Collections.emptyList());
    }

    public static AssertionResult fail(String message) {
        return new AssertionResult(false, message, Collections.emptyList());
    }

    public static AssertionResult fail(String message, List<Node> highlights) {
        return new AssertionResult(false, message, List.copyOf(highlights));
    }
}
