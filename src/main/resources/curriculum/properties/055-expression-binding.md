---
id: 055-expression-binding
tier: properties
order: 55
title: "Expression Bindings"
objectives:
  - "Use fluent binding expressions (.add, .multiply, .asString)"
  - "Create custom bindings with Bindings.createDoubleBinding"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      DoubleProperty width = new SimpleDoubleProperty(100);
      DoubleProperty height = new SimpleDoubleProperty(60);

      // area = width * height  (live)
      NumberBinding area = width.multiply(height);
      Label areaLabel = new Label();
      areaLabel.textProperty().bind(area.asString("Area: %.0f"));

      Slider wSlider = new Slider(10, 300, 100);
      width.bind(wSlider.valueProperty());
      Slider hSlider = new Slider(10, 200, 60);
      height.bind(hSlider.valueProperty());
      return new VBox(10, wSlider, hSlider, areaLabel);
  }
challenges:
  - id: c1
    description: "Add a perimeter binding (2*(w+h)) and display it in a second Label"
    assertion: containsNodeOfType(Label)
nextLesson: 056-sorting-filtering
---

# Expression Bindings

Fluent API on `NumberExpression` / `StringExpression`:

```java
property.add(5)
property.subtract(other)
property.multiply(2.0)
property.divide(other)
property.negate()
property.greaterThan(10)
property.asString("%.2f")
property.asString("Value: %s")
```

## Custom bindings

```java
DoubleBinding diag = Bindings.createDoubleBinding(
    () -> Math.sqrt(w.get()*w.get() + h.get()*h.get()),
    w, h   // dependencies — recomputed when either changes
);
```

## Challenge

Add `NumberBinding perimeter = width.add(height).multiply(2)` and
bind a second `Label` to display it.
