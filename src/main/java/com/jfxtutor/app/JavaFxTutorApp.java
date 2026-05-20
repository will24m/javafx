package com.jfxtutor.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFxTutorApp extends Application {

    private MainView mainView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        mainView = new MainView();
        Scene scene = new Scene(mainView, 1200, 800);
        scene.getStylesheets().add(
                getClass().getResource("/css/app.css").toExternalForm());

        primaryStage.setTitle("JavaFX Tutor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (mainView != null) {
            mainView.shutdown();
        }
    }
}
