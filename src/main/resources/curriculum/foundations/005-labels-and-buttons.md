---
id: 005-labels-and-buttons
tier: foundations
order: 5
title: "Labels and Buttons"
objectives:
  - "Create and style a Label and a Button"
  - "Attach an action handler to a Button"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      Label counter = new Label("0");
      Button inc = new Button("Increment");
      inc.setOnAction(e -> {
          int n = Integer.parseInt(counter.getText());
          counter.setText(String.valueOf(n + 1));
      });
      return new VBox(10, counter, inc);
  }
challenges:
  - id: c1
    description: "Add a second 'Reset' button that sets the counter back to 0"
    assertion: containsLabeledWithText(text="Reset")
nextLesson: 006-textfield
---

# Labels and Buttons

`Label` displays read-only text. `Button` fires an `ActionEvent` when
clicked. Together they are the foundation of almost every interactive
JavaFX UI.

## Key API

| Class | Main property | Event |
|---|---|---|
| `Label` | `textProperty()` | — |
| `Button` | `textProperty()` | `setOnAction(EventHandler<ActionEvent>)` |

## The snippet

The snippet above is a live click counter. Every time you press
*Increment* the label text is parsed, incremented, and written back.

## Challenge

Add a `Button("Reset")` whose action sets `counter.setText("0")`.
