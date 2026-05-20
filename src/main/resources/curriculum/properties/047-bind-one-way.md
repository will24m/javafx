---
id: 047-bind-one-way
tier: properties
order: 47
title: "One-Way Binding"
objectives:
  - "Bind a target property to a source with bind()"
  - "Understand that the target becomes read-only while bound"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      Slider slider = new Slider(0, 200, 100);
      javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(100, 40, Color.STEELBLUE);
      rect.widthProperty().bind(slider.valueProperty());
      return new VBox(16, slider, new StackPane(rect));
  }
challenges:
  - id: c1
    description: "Also bind the rectangle's height to half the slider value using Bindings.divide"
    assertion: containsNodeOfType(javafx.scene.shape.Rectangle)
nextLesson: 048-bind-bidirectional
---

# One-Way Binding

`target.bind(source)` keeps `target` equal to `source` automatically.
The target becomes **read-only** while bound — calling `target.set()`
throws an exception.

```java
label.textProperty().bind(model.nameProperty());
rect.widthProperty().bind(slider.valueProperty());
```

## Unbinding

```java
target.unbind();
target.set("now writable again");
```

## Computed bindings

Use `Bindings` helpers or fluent API:

```java
rect.widthProperty().bind(slider.valueProperty().multiply(2));
rect.heightProperty().bind(Bindings.divide(slider.valueProperty(), 2));
```

## Challenge

Add `rect.heightProperty().bind(Bindings.divide(slider.valueProperty(), 2))`.
