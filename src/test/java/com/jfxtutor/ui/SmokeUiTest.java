package com.jfxtutor.ui;

import com.jfxtutor.app.JavaFxTutorApp;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.junit.jupiter.api.extension.ExtendWith;

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
        assertEquals("What is a Stage?", title.getText());
    }

    @Test
    void previewHostContainsHelloLabel(FxRobot robot) {
        Label preview = robot.lookup("#previewHost .label").queryAs(Label.class);
        assertNotNull(preview, "No Label found inside #previewHost");
        assertEquals("Hello, JavaFX", preview.getText());
    }
}
