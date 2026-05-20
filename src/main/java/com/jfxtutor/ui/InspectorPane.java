package com.jfxtutor.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Phase 0: static placeholder. Phase 3 fills this in with TreeView + property
 * table + Preview/Mirror mode toggle.
 */
public class InspectorPane extends VBox {

    public InspectorPane() {
        getStyleClass().add("inspector-pane");
        setId("inspectorPane");

        Label header = new Label("Inspector");
        header.getStyleClass().add("nav-header");

        Label placeholder = new Label("Node tree + properties arrive in Phase 3.");
        placeholder.setWrapText(true);
        placeholder.getStyleClass().add("inspector-placeholder");

        getChildren().addAll(header, placeholder);
    }
}
