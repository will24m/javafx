---
id: 043-color-picker
tier: controls
order: 43
title: "ColorPicker"
objectives:
  - "Let users choose a color with ColorPicker"
  - "Apply the chosen color to another node"
estimatedMinutes: 6
starterSnippet: |
  public static Parent build() {
      ColorPicker picker = new ColorPicker(Color.STEELBLUE);
      javafx.scene.shape.Rectangle swatch = new javafx.scene.shape.Rectangle(120, 60);
      swatch.fillProperty().bind(picker.valueProperty());
      return new VBox(10, picker, swatch);
  }
challenges:
  - id: c1
    description: "Also update the VBox background color to match the picker"
    assertion: containsNodeOfType(ColorPicker)
nextLesson: 044-file-chooser
---

# ColorPicker

`ColorPicker` shows a button that opens a color selection popup. Its
`valueProperty()` holds the selected `Color`.

## Binding

Because `fillProperty()` and `valueProperty()` are both
`ObjectProperty<Color>`, you can bind them directly with no converter.

## Custom color palette

```java
picker.getCustomColors().add(Color.web("#ff6b6b"));
```

## String representation

```java
Color c = picker.getValue();
String hex = String.format("#%02X%02X%02X",
    (int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255));
```

## Challenge

Use a `ChangeListener` on `picker.valueProperty()` to also set the
`VBox` background: `vbox.setStyle("-fx-background-color: " + hex)`.
