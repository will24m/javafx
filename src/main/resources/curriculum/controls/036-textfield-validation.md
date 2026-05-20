---
id: 036-textfield-validation
tier: controls
order: 36
title: "TextField Validation"
objectives:
  - "Validate input as the user types with a TextFormatter"
  - "Show visual feedback for invalid input"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      TextField field = new TextField();
      field.setTextFormatter(new TextFormatter<>(change -> {
          // Only allow digits
          if (change.getText().matches("[0-9]*")) return change;
          return null;
      }));
      Label hint = new Label("Numbers only");
      hint.setStyle("-fx-text-fill: grey;");
      return new VBox(8, field, hint);
  }
challenges:
  - id: c1
    description: "Change the filter to allow only letters (a-zA-Z)"
    assertion: containsNodeOfType(TextField)
nextLesson: 037-datepicker
---

# TextField Validation

`TextFormatter` intercepts every change before it is committed to the
field. Returning `null` rejects the change; returning the `change`
object accepts it.

## TextFormatter change object

```java
change.getText()       // the text being added
change.getControlText() // full text after the change
change.setRange(...)   // modify what gets replaced
change.setText(...)    // modify what is inserted
```

## Visual feedback

Apply a CSS pseudo-class or `setStyle` to indicate invalid state:

```java
field.setStyle("-fx-border-color: red;");
```

## Challenge

Change the regex to `[a-zA-Z]*` to allow only letters.
