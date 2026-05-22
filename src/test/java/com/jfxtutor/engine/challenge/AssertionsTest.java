package com.jfxtutor.engine.challenge;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Assertions} factory methods.
 *
 * JavaFX nodes can be constructed off the FX thread in unit tests as long as
 * the toolkit has been started and we never attach them to a live Scene.
 */
class AssertionsTest {

    @BeforeAll
    static void startJavaFx() throws InterruptedException {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            latch.await();
        } catch (IllegalStateException ignored) {
            // Already started.
        }
    }

    // ── containsNodeOfType ────────────────────────────────────────────────────

    @Test
    void containsNodeOfType_passes_when_node_present() {
        VBox root = new VBox(new Button("OK"));
        AssertionResult r = Assertions.containsNodeOfType(Button.class).check(root);
        assertTrue(r.passed());
    }

    @Test
    void containsNodeOfType_fails_when_node_absent() {
        VBox root = new VBox(new Label("hi"));
        AssertionResult r = Assertions.containsNodeOfType(Button.class).check(root);
        assertFalse(r.passed());
        assertTrue(r.message().contains("Button"));
    }

    @Test
    void containsNodeOfType_finds_nested_node() {
        VBox outer = new VBox(new HBox(new Rectangle()));
        AssertionResult r = Assertions.containsNodeOfType(Rectangle.class).check(outer);
        assertTrue(r.passed());
    }

    // ── containsLabeledWithText ───────────────────────────────────────────────

    @Test
    void containsLabeledWithText_passes_exact_match() {
        VBox root = new VBox(new Label("Reset"));
        assertTrue(Assertions.containsLabeledWithText("Reset").check(root).passed());
    }

    @Test
    void containsLabeledWithText_case_sensitive() {
        VBox root = new VBox(new Label("reset"));
        assertFalse(Assertions.containsLabeledWithText("Reset").check(root).passed());
    }

    @Test
    void containsLabeledWithText_passes_for_button() {
        VBox root = new VBox(new Button("Submit"));
        assertTrue(Assertions.containsLabeledWithText("Submit").check(root).passed());
    }

    // ── containsLabeledInside ─────────────────────────────────────────────────

    @Test
    void containsLabeledInside_passes_when_label_in_parent() {
        VBox inner = new VBox(new Label("Submit"));
        VBox root = new VBox(inner);
        assertTrue(Assertions.containsLabeledInside("Submit", VBox.class).check(root).passed());
    }

    @Test
    void containsLabeledInside_fails_when_parent_missing() {
        VBox root = new VBox(new Label("Submit"));
        AssertionResult r = Assertions.containsLabeledInside("Submit", HBox.class).check(root);
        assertFalse(r.passed());
        assertTrue(r.message().contains("HBox"));
    }

    @Test
    void containsLabeledInside_fails_when_text_wrong() {
        VBox inner = new VBox(new Label("Cancel"));
        VBox root = new VBox(inner);
        assertFalse(Assertions.containsLabeledInside("Submit", VBox.class).check(root).passed());
    }

    // ── countOfType ───────────────────────────────────────────────────────────

    @Test
    void countOfType_passes_exact_count() {
        VBox root = new VBox(new Button("a"), new Button("b"), new Button("c"));
        assertTrue(Assertions.countOfType(Button.class, 3).check(root).passed());
    }

    @Test
    void countOfType_fails_wrong_count() {
        VBox root = new VBox(new Button("a"), new Button("b"));
        AssertionResult r = Assertions.countOfType(Button.class, 3).check(root);
        assertFalse(r.passed());
        assertTrue(r.message().contains("2"));
    }

    // ── parentChain ───────────────────────────────────────────────────────────

    @Test
    void parentChain_passes_when_child_inside_ancestor() {
        HBox hbox = new HBox(new Button("ok"));
        VBox root = new VBox(hbox);
        assertTrue(Assertions.parentChain(Button.class, HBox.class).check(root).passed());
    }

    @Test
    void parentChain_fails_when_not_nested() {
        VBox root = new VBox(new Button("ok"));
        AssertionResult r = Assertions.parentChain(Button.class, HBox.class).check(root);
        assertFalse(r.passed());
    }

    // ── cssClassPresent ───────────────────────────────────────────────────────

    @Test
    void cssClassPresent_fails_when_selector_missing() {
        // Lookup with no Scene always returns empty — good enough to verify the
        // "no node found" branch without needing a live Scene.
        VBox root = new VBox(new Button("OK"));
        AssertionResult r = Assertions.cssClassPresent("#ghost", "primary").check(root);
        assertFalse(r.passed());
        assertTrue(r.message().contains("ghost") || r.message().contains("selector"));
    }
}
