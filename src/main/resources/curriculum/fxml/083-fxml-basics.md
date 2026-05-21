---
id: 083-fxml-basics
tier: fxml
order: 83
title: "FXML Basics"
objectives:
  - "Understand what FXML is and why it exists"
  - "Read the structure of a simple FXML file"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      // FXML describes UI in XML. This snippet shows the equivalent
      // programmatic code for what a simple FXML file would declare.
      Label title = new Label("Hello from code");
      title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
      Button btn = new Button("Click me");
      btn.setOnAction(e -> title.setText("Clicked!"));
      VBox root = new VBox(12, title, btn);
      root.setPadding(new javafx.geometry.Insets(16));
      return root;
  }
challenges:
  - id: c1
    description: "Add a Label below the button that reads 'FXML or code — your choice'"
    assertion: containsLabeledWithText(text="FXML or code — your choice")
nextLesson: 084-fxmlloader
---

# FXML Basics

**FXML** is an XML dialect for declaring JavaFX scene graphs. Instead of
writing Java constructor calls, you write tags:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.scene.layout.VBox?>
<?import javafx.scene.control.*?>
<VBox spacing="12" xmlns:fx="http://javafx.com/fxml">
    <Label text="Hello from FXML" style="-fx-font-size: 20px;"/>
    <Button text="Click me" onAction="#handleClick"/>
</VBox>
```

## Why FXML?

| Reason | Benefit |
|---|---|
| Separation of concerns | Designers edit FXML; developers write controller logic |
| Scene Builder | Drag-and-drop visual editor reads/writes FXML |
| Less boilerplate | Nested layouts are more readable in XML than in Java |

## Why *not* FXML?

- No compile-time type checking on attribute values
- Refactoring (renames, moves) requires manual FXML edits
- Dynamic UIs built from data are awkward in XML

The snippet shows the **programmatic equivalent** of the FXML above —
both produce the same scene graph. Learning one makes the other obvious.

## Challenge

Add a `Label` below the button with the text `"FXML or code — your choice"`.
