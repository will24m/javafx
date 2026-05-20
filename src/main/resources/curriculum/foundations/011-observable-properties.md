---
id: 011-observable-properties
tier: foundations
order: 11
title: "Observable Properties"
objectives:
  - "Understand ObservableValue and ChangeListener"
  - "Distinguish between invalidation and change listeners"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      Slider slider = new Slider(0, 100, 50);
      Label value = new Label();
      slider.valueProperty().addListener((obs, old, val) ->
          value.setText(String.format("%.1f", val.doubleValue())));
      value.setText(String.format("%.1f", slider.getValue()));
      return new VBox(10, slider, value);
  }
challenges:
  - id: c1
    description: "Change the listener to an InvalidationListener instead of a ChangeListener"
    assertion: containsNodeOfType(Slider)
nextLesson: 012-group-vs-parent
---

# Observable Properties

A JavaFX property (e.g. `textProperty()`, `valueProperty()`) is an
`ObservableValue<T>`. You can attach two kinds of listeners:

## ChangeListener

```java
prop.addListener((observable, oldValue, newValue) -> { ... });
```

Fired when the value actually changes. You receive both the old and
new value.

## InvalidationListener

```java
prop.addListener(observable -> { ... });
```

Fired when the value *might* have changed (it may have been computed
lazily). Only the `Observable` is passed — you must call `getValue()`
yourself.

For bindings that involve expensive computations, `InvalidationListener`
can skip redundant recalculations.

## Challenge

Change the `valueProperty` listener from a `ChangeListener` to an
`InvalidationListener` that calls `slider.getValue()` directly.
