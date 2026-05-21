---
id: 089-scene-builder-workflow
tier: fxml
order: 89
title: "Scene Builder Workflow"
objectives:
  - "Understand what Scene Builder does and does not manage"
  - "Round-trip an FXML file between Scene Builder and code"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      // This is a typical Scene Builder output: a form with labels and inputs.
      Label nameLabel = new Label("Name:");
      TextField nameField = new TextField();
      Label emailLabel = new Label("Email:");
      TextField emailField = new TextField();
      Button submit = new Button("Submit");
      submit.setDefaultButton(true);
      submit.setMaxWidth(Double.MAX_VALUE);

      javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
      grid.setHgap(10);
      grid.setVgap(8);
      grid.setPadding(new javafx.geometry.Insets(16));
      grid.addRow(0, nameLabel, nameField);
      grid.addRow(1, emailLabel, emailField);
      javafx.scene.layout.GridPane.setColumnSpan(submit, 2);
      grid.addRow(2, submit);
      return grid;
  }
challenges:
  - id: c1
    description: "Add a third row with a Label 'Phone:' and a TextField"
    assertion: containsNodeOfType(javafx.scene.layout.GridPane)
nextLesson: 090-programmatic-vs-fxml
---

# Scene Builder Workflow

**Scene Builder** (by Gluon, free) is a drag-and-drop FXML editor. The
typical workflow:

1. Open/create `.fxml` in Scene Builder
2. Drag controls from the palette; set properties in the Inspector panel
3. Assign `fx:id` values so you can inject them in Java
4. Save — Scene Builder writes valid FXML
5. Open the FXML in your IDE; implement the controller

## What Scene Builder does NOT manage

- Controller class creation (you write that)
- `@FXML` annotations (you add those)
- Event handler method bodies (you implement those)
- Build configuration (Gradle/Maven)

## Editing round-trip

The generated FXML is plain XML. You can edit it by hand — Scene Builder
re-imports hand-edited FXML fine as long as the XML is valid.

## Challenge

Add a third row to the grid: `Label("Phone:")` and a blank `TextField`.
