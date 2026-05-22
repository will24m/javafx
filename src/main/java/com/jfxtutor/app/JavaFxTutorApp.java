package com.jfxtutor.app;

import com.jfxtutor.data.progress.ProgressStore;
import com.jfxtutor.util.AppLog;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX's launcher-facing entry point.
 *
 * JavaFX applications have two starts:
 * - main(...) is the normal JVM entry when Gradle launches the app.
 * - start(...) is called later by the JavaFX runtime on the JavaFX Application Thread.
 *
 * Keeping this class small makes the app lifecycle easy to see: build the root
 * view, wrap it in a Scene, attach CSS, show the Stage, and clean up on exit.
 */
public class JavaFxTutorApp extends Application {

    private MainView mainView;
    private ProgressStore progressStore;

    public static void main(String[] args) {
        AppLog.info("app", "Gradle handed control to main(); asking JavaFX to launch the application.");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        AppLog.info("app", "JavaFX runtime is ready; loading progress and building the main view tree.");
        this.progressStore = new ProgressStore().load();
        mainView = new MainView(progressStore);

        // A Scene is the top-level container for all visible JavaFX Nodes.
        // The MainView is a BorderPane, so it becomes the root of the entire UI.
        AppLog.info("app", "Creating Scene at 1200x800 and attaching application stylesheet.");
        Scene scene = new Scene(mainView, 1200, 800);
        scene.getStylesheets().add(
                getClass().getResource("/css/app.css").toExternalForm());

        // The Stage is the native OS window. Once show() returns, user events
        // and layout pulses drive the rest of the application.
        AppLog.info("app", "Configuring primary Stage and showing the JavaFX Tutor window.");
        primaryStage.setTitle("JavaFX Tutor");
        primaryStage.setScene(scene);
        primaryStage.show();
        AppLog.info("app", "Window is visible. The tutor is ready for lesson selection and live coding.");
    }

    @Override
    public void stop() {
        AppLog.info("app", "JavaFX is stopping; asking the main view to release background resources.");
        if (mainView != null) {
            mainView.shutdown();
        }
        if (progressStore != null) {
            AppLog.info("app", "Flushing progress to disk before exit.");
            progressStore.flush();
        }
        AppLog.info("app", "Shutdown complete.");
    }
}
