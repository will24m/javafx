---
id: 057-observable-value-map
tier: properties
order: 57
title: "ObservableValue.map and flatMap"
objectives:
  - "Transform an ObservableValue with .map()"
  - "Chain observables with .flatMap()"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      TextField field = new TextField("hello");
      // .map() — transform without creating intermediate properties
      ObservableValue<String> upper = field.textProperty().map(String::toUpperCase);
      Label label = new Label();
      label.textProperty().bind(upper);
      return new VBox(10, field, label);
  }
challenges:
  - id: c1
    description: "Chain a second .map() that reverses the string"
    assertion: containsNodeOfType(Label)
nextLesson: 058-property-binding-with-converter
---

# ObservableValue.map and flatMap

Introduced in JavaFX 19, these make observable pipelines much more
readable.

## map

```java
ObservableValue<String> trimmed = field.textProperty().map(String::trim);
ObservableValue<Integer> length = field.textProperty().map(String::length);
```

## flatMap

For when the transformation itself returns an `ObservableValue`:

```java
ObjectProperty<User> user = ...;
ObservableValue<String> name = user.flatMap(u -> u.nameProperty());
// If user changes, name automatically tracks the new user's name.
```

## Challenge

Chain `.map(s -> new StringBuilder(s).reverse().toString())` after
the `toUpperCase` map and bind the result to a third `Label`.
