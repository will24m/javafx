---
id: 054-binding-pitfalls
tier: properties
order: 54
title: "Binding Pitfalls"
objectives:
  - "Identify circular bindings and how to break them"
  - "Avoid setting a bound property"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      DoubleProperty a = new SimpleDoubleProperty(1.0);
      DoubleProperty b = new SimpleDoubleProperty(2.0);
      // Demonstrate: binding one way, then trying to set the target
      Label la = new Label();
      Label lb = new Label();
      la.textProperty().bind(a.asString("a = %.2f"));
      lb.textProperty().bind(b.asString("b = %.2f"));

      Button swapA = new Button("Set a = 5");
      swapA.setOnAction(e -> a.set(5.0));  // OK: a is not bound
      return new VBox(8, la, lb, swapA);
  }
challenges:
  - id: c1
    description: "Try calling la.textProperty().set('oops') — observe the RuntimeException"
    assertion: containsNodeOfType(Label)
nextLesson: 055-expression-binding
---

# Binding Pitfalls

## Setting a bound property

Calling `set()` on a property that has been bound with `bind()` throws:

```
java.lang.RuntimeException: A bound value cannot be set.
```

Always unbind before setting:

```java
prop.unbind();
prop.set("new value");
```

## Circular bindings

```java
a.bind(b);
b.bind(a); // throws: A bound value cannot be set.
```

JavaFX detects direct circular binds at bind-time.

## Accidental strong captures

Lambdas inside bindings capture `this`, keeping the UI node alive.
Prefer `Bindings.createStringBinding(() -> ..., dep)` with explicit
dependencies over long anonymous expressions that capture large scopes.

## Challenge

After the `swapA` button, add a button that calls
`la.textProperty().set("oops")` — it will throw. Wrap it in a
`try/catch` and show the exception message in a `Label`.
