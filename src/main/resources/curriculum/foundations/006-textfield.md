---
id: 006-textfield
tier: foundations
order: 6
title: "TextField and TextArea"
objectives:
  - "Collect user text input with TextField"
  - "Display multi-line text with TextArea"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      TextField field = new TextField();
      field.setPromptText("Type something...");
      Label echo = new Label("(nothing yet)");
      field.textProperty().addListener((obs, old, val) -> echo.setText(val));
      return new VBox(10, field, echo);
  }
challenges:
  - id: c1
    description: "Add a TextArea below the echo label that also mirrors the field text"
    assertion: containsNodeOfType(TextArea)
nextLesson: 007-images
---

# TextField and TextArea

`TextField` is a single-line text input. `TextArea` is multi-line.
Both expose `textProperty()` — an `ObservableValue<String>` you can
listen to or bind.

## Prompt text

`setPromptText("hint")` shows grey placeholder text when the field is
empty. It disappears as soon as the user types.

## Live echo

The snippet wires a `ChangeListener` to `textProperty()`. Every
keystroke fires the listener with the new value.

## Challenge

Add a `TextArea` below the echo label and set its text to match the
field on every keystroke.
