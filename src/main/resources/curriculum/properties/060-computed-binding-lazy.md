---
id: 060-computed-binding-lazy
tier: properties
order: 60
title: "Lazy Computed Bindings"
objectives:
  - "Build an ObjectBinding with multiple dependencies"
  - "Understand lazy evaluation and when computeValue is called"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      DoubleProperty r = new SimpleDoubleProperty(5.0);
      // Custom binding: area of a circle
      ObjectBinding<String> areaText = Bindings.createObjectBinding(
          () -> String.format("Circle r=%.1f  area=%.2f", r.get(), Math.PI * r.get() * r.get()),
          r);

      Slider slider = new Slider(1, 50, 5);
      r.bind(slider.valueProperty());
      Label label = new Label();
      label.textProperty().bind(areaText);
      return new VBox(10, slider, label);
  }
challenges:
  - id: c1
    description: "Add a second binding for circumference and display it in another Label"
    assertion: containsNodeOfType(Label)
nextLesson: 061-event-filters-handlers
---

# Lazy Computed Bindings

`ObjectBinding<T>` (and the `DoubleBinding`, `StringBinding`, etc.
siblings) computes its value lazily: only when the value is actually
read after a dependency changes.

## createObjectBinding

```java
ObjectBinding<T> binding = Bindings.createObjectBinding(
    () -> /* compute T */,
    dep1, dep2, dep3);   // dependencies
```

The lambda runs when any dependency is invalidated *and* the value
is requested.

## Subclassing for reuse

```java
DoubleBinding area = new DoubleBinding() {
    { super.bind(r); } // bind dependencies
    @Override protected double computeValue() {
        return Math.PI * r.get() * r.get();
    }
};
```

## Challenge

Add `ObjectBinding<String> circumText = Bindings.createObjectBinding(() -> String.format("Circumference: %.2f", 2 * Math.PI * r.get()), r)` and bind a new `Label` to it.
