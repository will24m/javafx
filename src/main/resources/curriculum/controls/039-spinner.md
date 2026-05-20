---
id: 039-spinner
tier: controls
order: 39
title: "Spinner"
objectives:
  - "Create an integer Spinner with min/max/step"
  - "Make it editable and validate committed values"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      Spinner<Integer> spinner = new Spinner<>(1, 100, 10, 1);
      spinner.setEditable(true);
      Label display = new Label("Value: 10");
      spinner.valueProperty().addListener((obs, old, val) ->
          display.setText("Value: " + val));
      return new VBox(10, spinner, display);
  }
challenges:
  - id: c1
    description: "Change the step to 5 and the initial value to 50"
    assertion: containsNodeOfType(Spinner)
nextLesson: 040-custom-cell-factory
---

# Spinner

`Spinner<T>` lets users pick a value from an ordered sequence by
clicking arrow buttons or typing.

## Integer spinner

```java
new Spinner<>(min, max, initialValue, step)
```

## Double spinner

```java
new Spinner<Double>(0.0, 1.0, 0.5, 0.1)
```

## Editable

`setEditable(true)` allows typing a value. Pressing Enter or clicking
away commits it. `amountToStepBy` controls the arrow increment.

## SpinnerValueFactory

For fully custom sequences:
```java
spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10));
```

## Challenge

Change step to `5` and initial value to `50`:
`new Spinner<>(1, 100, 50, 5)`.
