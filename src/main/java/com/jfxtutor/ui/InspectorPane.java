package com.jfxtutor.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/** Placeholder for the future TreeView, property table, and Preview/Mirror mode toggle. */
public class InspectorPane extends VBox {

    public InspectorPane() {
        getStyleClass().add("inspector-pane");
        setId("inspectorPane");

        Label header = new Label("Inspector");
        header.getStyleClass().add("nav-header");

        Label placeholder = new Label("Node tree + properties arrive in the inspector phase.");
        placeholder.setWrapText(true);
        placeholder.getStyleClass().add("inspector-placeholder");

        getChildren().addAll(header, placeholder);
    }
}
