package com.jfxtutor.engine.inspect;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class NodeInspectorTest {

    @BeforeAll
    static void startJavaFx() throws InterruptedException {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            latch.await();
        } catch (IllegalStateException ignored) {}
    }

    @Test
    void countIncludesRoot() {
        VBox root = new VBox(new Label("a"), new Label("b"));
        assertEquals(3, new NodeInspector(root).count());
    }

    @Test
    void countNestedTree() {
        VBox root = new VBox(new HBox(new Button("ok"), new Button("cancel")));
        // VBox(1) + HBox(1) + Button(1) + Button(1) = 4
        assertEquals(4, new NodeInspector(root).count());
    }

    @Test
    void findAllByType() {
        VBox root = new VBox(new Label("x"), new HBox(new Label("y"), new Button("z")));
        NodeInspector ins = new NodeInspector(root);
        List<?> labels = ins.findAll(n -> n instanceof Label);
        assertEquals(2, labels.size());
    }

    @Test
    void buildTreeStructure() {
        VBox root = new VBox(new Button("go"));
        NodeInspector.InspectorNode tree = new NodeInspector(root).buildTree();
        assertEquals("VBox", tree.ref().typeName());
        assertEquals(1, tree.children().size());
        assertEquals("Button", tree.children().get(0).ref().typeName());
    }

    @Test
    void nodeRefDisplayLabel_includesText() {
        Button b = new Button("Submit");
        b.setId("submitBtn");
        NodeRef ref = new NodeRef(b);
        assertTrue(ref.displayLabel().contains("Submit"));
        assertTrue(ref.displayLabel().contains("#submitBtn"));
    }

    @Test
    void nodeRefSizeStringForNonRegion() {
        // javafx.scene.shape.Circle is a Shape, not a Region — no size string.
        javafx.scene.shape.Circle c = new javafx.scene.shape.Circle(10);
        assertEquals("—", new NodeRef(c).sizeString());
    }
}
