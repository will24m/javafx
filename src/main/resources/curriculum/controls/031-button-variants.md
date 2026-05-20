---
id: 031-button-variants
tier: controls
order: 31
title: "Button Variants"
objectives:
  - "Use ToggleButton, RadioButton, and CheckBox"
  - "Group radio buttons with ToggleGroup"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      ToggleGroup group = new ToggleGroup();
      RadioButton r1 = new RadioButton("Option A");
      RadioButton r2 = new RadioButton("Option B");
      RadioButton r3 = new RadioButton("Option C");
      r1.setToggleGroup(group);
      r2.setToggleGroup(group);
      r3.setToggleGroup(group);
      r1.setSelected(true);

      CheckBox check = new CheckBox("I agree");
      return new VBox(8, r1, r2, r3, new Separator(), check);
  }
challenges:
  - id: c1
    description: "Add a Label that shows which RadioButton is currently selected"
    assertion: containsNodeOfType(Label)
nextLesson: 032-choicebox-combobox
---

# Button Variants

## ToggleButton

A button that stays pressed. `isSelected()` returns its state.

## RadioButton

A `ToggleButton` styled as a circle. Add to a `ToggleGroup` so
selecting one deselects the others.

## CheckBox

An independent on/off toggle. `isSelected()` returns the state.
`isIndeterminate()` is true when `allowIndeterminate` is set and the
user has cycled to the middle state.

## ToggleGroup

```java
ToggleGroup group = new ToggleGroup();
r1.setToggleGroup(group);
r2.setToggleGroup(group);
// group.selectedToggleProperty() tracks the selected one
```

## Challenge

Add a `Label` below the separator that reads the text of the currently
selected `RadioButton` using
`group.selectedToggleProperty().addListener(...)`.
