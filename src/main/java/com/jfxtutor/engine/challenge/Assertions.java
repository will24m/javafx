package com.jfxtutor.engine.challenge;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Labeled;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory methods for the v1 assertion DSL.
 *
 * Each method returns a {@link ChallengeAssertion} that walks the snippet's scene
 * graph. Assertions are pure: they read node state, never mutate it.
 */
public final class Assertions {

    private Assertions() {}

    // ── DSL factory methods ───────────────────────────────────────────────────

    /**
     * Passes when at least one node of {@code nodeClass} exists in the tree.
     * Example DSL: {@code containsNodeOfType(Button)}
     */
    public static ChallengeAssertion containsNodeOfType(Class<?> nodeClass) {
        return root -> {
            List<Node> found = findAll(root, n -> nodeClass.isInstance(n));
            if (!found.isEmpty()) {
                return AssertionResult.pass(
                        "Found " + nodeClass.getSimpleName() + " in the preview.");
            }
            return AssertionResult.fail(
                    "Expected a " + nodeClass.getSimpleName()
                            + " somewhere in the scene, but none was found.");
        };
    }

    /**
     * Passes when any {@link Labeled} node has text equal to {@code text}.
     * Example DSL: {@code containsLabeledWithText(text="Reset")}
     */
    public static ChallengeAssertion containsLabeledWithText(String text) {
        return root -> {
            boolean found = findAll(root, n -> n instanceof Labeled l
                    && text.equals(l.getText()))
                    .size() > 0;
            if (found) {
                return AssertionResult.pass(
                        "Found a labeled node with text \"" + text + "\".");
            }
            return AssertionResult.fail(
                    "Expected a Label/Button/CheckBox (etc.) with text \""
                            + text + "\", but none was found.");
        };
    }

    /**
     * Passes when a {@link Labeled} with {@code text} is a descendant of a node
     * of type {@code parentClass}.
     * Example DSL: {@code containsLabeledInside(text="Submit", parentType=VBox)}
     */
    public static ChallengeAssertion containsLabeledInside(String text, Class<?> parentClass) {
        return root -> {
            List<Node> parents = findAll(root, n -> parentClass.isInstance(n));
            for (Node p : parents) {
                if (p instanceof Parent container) {
                    boolean found = findAll(container, n -> n instanceof Labeled l
                            && text.equals(l.getText()))
                            .size() > 0;
                    if (found) {
                        return AssertionResult.pass(
                                "Found \"" + text + "\" inside a "
                                        + parentClass.getSimpleName() + ".");
                    }
                }
            }
            if (parents.isEmpty()) {
                return AssertionResult.fail(
                        "Expected a " + parentClass.getSimpleName()
                                + " containing \"" + text + "\", but no "
                                + parentClass.getSimpleName() + " was found.");
            }
            return AssertionResult.fail(
                    "Found " + parentClass.getSimpleName()
                            + " but it does not contain a labeled node with text \""
                            + text + "\".");
        };
    }

    /**
     * Passes when a node matching {@code selector} has style class {@code cssClass}.
     * Example DSL: {@code cssClassPresent(selector=".my-button", cssClass="primary")}
     */
    public static ChallengeAssertion cssClassPresent(String selector, String cssClass) {
        return root -> {
            List<Node> matches = new ArrayList<>(root.lookupAll(selector));
            for (Node n : matches) {
                if (n.getStyleClass().contains(cssClass)) {
                    return AssertionResult.pass(
                            "Node matching \"" + selector
                                    + "\" has CSS class \"" + cssClass + "\".");
                }
            }
            if (matches.isEmpty()) {
                return AssertionResult.fail(
                        "No node matching selector \"" + selector + "\" was found.");
            }
            return AssertionResult.fail(
                    "Node matching \"" + selector + "\" does not have CSS class \""
                            + cssClass + "\".");
        };
    }

    /**
     * Passes when the count of nodes with type {@code nodeClass} exactly equals {@code n}.
     * Example DSL: {@code countOfType(RadioButton, n=3)}
     */
    public static ChallengeAssertion countOfType(Class<?> nodeClass, int n) {
        return root -> {
            int count = findAll(root, node -> nodeClass.isInstance(node)).size();
            if (count == n) {
                return AssertionResult.pass(
                        "Found exactly " + n + " " + nodeClass.getSimpleName()
                                + " node" + (n == 1 ? "" : "s") + ".");
            }
            return AssertionResult.fail(
                    "Expected " + n + " " + nodeClass.getSimpleName()
                            + " node" + (n == 1 ? "" : "s")
                            + ", but found " + count + ".");
        };
    }

    /**
     * Passes when a node of type {@code childClass} has an ancestor of type {@code ancestorClass}.
     * Example DSL: {@code parentChain(child=HBox, ancestor=VBox)}
     */
    public static ChallengeAssertion parentChain(Class<?> childClass, Class<?> ancestorClass) {
        return root -> {
            List<Node> children = findAll(root, n -> childClass.isInstance(n));
            for (Node child : children) {
                javafx.scene.Node cursor = child.getParent();
                while (cursor != null) {
                    if (ancestorClass.isInstance(cursor)) {
                        return AssertionResult.pass(
                                "Found " + childClass.getSimpleName()
                                        + " inside " + ancestorClass.getSimpleName() + ".");
                    }
                    cursor = cursor.getParent();
                }
            }
            if (children.isEmpty()) {
                return AssertionResult.fail(
                        "No " + childClass.getSimpleName() + " found in the scene.");
            }
            return AssertionResult.fail(
                    "Found " + childClass.getSimpleName()
                            + " but it is not inside a " + ancestorClass.getSimpleName() + ".");
        };
    }

    // ── tree walk ─────────────────────────────────────────────────────────────

    /** DFS walk of the scene tree; returns all nodes matching {@code pred}. */
    public static List<Node> findAll(Node root, java.util.function.Predicate<Node> pred) {
        List<Node> result = new ArrayList<>();
        collectAll(root, pred, result);
        return result;
    }

    private static void collectAll(Node node,
                                   java.util.function.Predicate<Node> pred,
                                   List<Node> result) {
        if (node == null) return;
        if (pred.test(node)) result.add(node);
        if (node instanceof Parent p) {
            for (Node child : p.getChildrenUnmodifiable()) {
                collectAll(child, pred, result);
            }
        }
    }
}
