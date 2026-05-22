package com.jfxtutor.ui;

import com.jfxtutor.app.JavaFxTutorApp;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class SmokeUiTest {

    @Start
    void start(Stage stage) throws Exception {
        new JavaFxTutorApp().start(stage);
    }

    @Test
    void windowTitleIsCorrect(FxRobot robot) {
        Stage stage = (Stage) robot.listWindows().iterator().next();
        assertEquals("JavaFX Tutor", stage.getTitle());
    }

    @Test
    void lessonTitleVisible(FxRobot robot) {
        Label title = robot.lookup(".lesson-title").queryAs(Label.class);
        assertNotNull(title, ".lesson-title label not found in scene");
        assertFalse(title.getText().isBlank(), "lesson title should not be blank");
    }

    @Test
    void previewHostContainsLabel(FxRobot robot) throws Exception {
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS,
                () -> !robot.lookup("#previewHost .label").queryAllAs(Label.class).isEmpty());
        Label preview = robot.lookup("#previewHost .label").queryAs(Label.class);
        assertNotNull(preview, "No Label found inside #previewHost");
        assertFalse(preview.getText().isBlank(), "preview label should not be blank");
    }

    @Test
    void blankEditorShowsCompileError(FxRobot robot) throws Exception {
        EditorPane editor = robot.lookup("#editorPane").queryAs(EditorPane.class);
        TextArea error = robot.lookup("#previewErrorArea").queryAs(TextArea.class);

        robot.interact(() -> editor.setText(""));
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () -> !error.getText().isBlank());

        assertTrue(error.getText().contains("ERROR"), error.getText());
    }

    @Test
    void slowSnippetShowsTimeoutError(FxRobot robot) throws Exception {
        EditorPane editor = robot.lookup("#editorPane").queryAs(EditorPane.class);
        TextArea error = robot.lookup("#previewErrorArea").queryAs(TextArea.class);

        robot.interact(() -> editor.setText("""
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return new StackPane();
                """));
        WaitForAsyncUtils.waitFor(6, TimeUnit.SECONDS, () -> error.getText().contains("timed out"));

        assertTrue(error.getText().contains("timed out"), error.getText());
    }
}
