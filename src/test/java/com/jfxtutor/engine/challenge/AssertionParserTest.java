package com.jfxtutor.engine.challenge;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class AssertionParserTest {

    @BeforeAll
    static void startJavaFx() throws InterruptedException {
        // Class.forName on JavaFX controls triggers the module's static initializer,
        // which requires a toolkit. Starting it here avoids ExceptionInInitializerError
        // without spinning up a full app window.
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            latch.await();
        } catch (IllegalStateException ignored) {
            // Already started — fine in a shared JVM.
        }
    }

    @Test
    void parsesContainsNodeOfType() {
        assertDoesNotThrow(() -> AssertionParser.parse("containsNodeOfType(Button)"));
    }

    @Test
    void parsesContainsNodeOfTypeFullyQualified() {
        assertDoesNotThrow(() -> AssertionParser.parse("containsNodeOfType(javafx.scene.shape.Rectangle)"));
    }

    @Test
    void parsesContainsLabeledWithText() {
        assertDoesNotThrow(() -> AssertionParser.parse("containsLabeledWithText(text=\"Reset\")"));
    }

    @Test
    void parsesContainsLabeledWithTextSpecialChars() {
        assertDoesNotThrow(() -> AssertionParser.parse("containsLabeledWithText(text=\"⏹ Stop\")"));
    }

    @Test
    void parsesContainsLabeledInside() {
        assertDoesNotThrow(() ->
                AssertionParser.parse("containsLabeledInside(text=\"Submit\", parentType=VBox)"));
    }

    @Test
    void parsesCssClassPresent() {
        assertDoesNotThrow(() ->
                AssertionParser.parse("cssClassPresent(selector=\".btn\", cssClass=\"primary\")"));
    }

    @Test
    void parsesCountOfType() {
        assertDoesNotThrow(() -> AssertionParser.parse("countOfType(RadioButton, n=3)"));
    }

    @Test
    void parsesParentChain() {
        assertDoesNotThrow(() ->
                AssertionParser.parse("parentChain(child=HBox, ancestor=VBox)"));
    }

    @Test
    void throwsOnEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> AssertionParser.parse(""));
    }

    @Test
    void throwsOnUnknownFunction() {
        assertThrows(IllegalArgumentException.class,
                () -> AssertionParser.parse("unknownFunc(Button)"));
    }

    @Test
    void throwsOnUnknownClass() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> AssertionParser.parse("containsNodeOfType(NotARealClass)"));
        assertTrue(ex.getMessage().contains("Unknown class"), ex.getMessage());
    }

    @Test
    void throwsOnMalformedSyntax() {
        assertThrows(IllegalArgumentException.class,
                () -> AssertionParser.parse("containsNodeOfType Button"));
    }

    @Test
    void parsesQuotedTextWithEmbeddedColon() {
        assertDoesNotThrow(() ->
                AssertionParser.parse("containsLabeledWithText(text=\"Build with: ./gradlew jpackage\")"));
    }
}
