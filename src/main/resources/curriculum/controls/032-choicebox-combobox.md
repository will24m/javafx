---
id: 032-choicebox-combobox
tier: controls
order: 32
title: "ChoiceBox and ComboBox"
objectives:
  - "Let users pick from a list with ChoiceBox and ComboBox"
  - "Understand the difference between the two"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      ComboBox<String> combo = new ComboBox<>();
      combo.getItems().addAll("Red", "Green", "Blue");
      combo.setValue("Red");

      Label result = new Label();
      combo.valueProperty().addListener((obs, old, val) -> result.setText("Selected: " + val));
      return new VBox(10, combo, result);
  }
challenges:
  - id: c1
    description: "Add a ChoiceBox<String> with the same items below the ComboBox"
    assertion: containsNodeOfType(ChoiceBox)
nextLesson: 033-listview
---

# ChoiceBox and ComboBox

## ChoiceBox

A simple drop-down that shows a fixed list. Good for small, static
option sets.

```java
ChoiceBox<String> box = new ChoiceBox<>();
box.getItems().addAll("A", "B", "C");
box.setValue("A");
```

## ComboBox

More powerful: editable mode, custom cell rendering, large observable
lists. Essentially a `ChoiceBox` + optional text field + `ListCell`
factory.

```java
combo.setEditable(true);  // allow free-text input
combo.setCellFactory(...); // custom rendering
```

## Which to use?

`ChoiceBox` for ≤10 static items. `ComboBox` for larger lists,
editable input, or custom cell rendering.

## Challenge

Add a `ChoiceBox<String>` with items "Red", "Green", "Blue" below the
existing `ComboBox`.
