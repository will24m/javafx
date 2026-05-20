---
id: 049-bindings-helpers
tier: properties
order: 49
title: "Bindings Utility Class"
objectives:
  - "Build compound expressions with Bindings helpers"
  - "Use when/then/otherwise for conditional bindings"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      CheckBox darkMode = new CheckBox("Dark mode");
      StringProperty bg = new SimpleStringProperty("#ffffff");
      bg.bind(Bindings.when(darkMode.selectedProperty())
                      .then("#1e1f22")
                      .otherwise("#ffffff"));
      Label preview = new Label("Preview");
      bg.addListener((obs, old, val) ->
          preview.setStyle("-fx-background-color: " + val + "; -fx-padding: 20;"));
      preview.setStyle("-fx-background-color: #ffffff; -fx-padding: 20;");
      return new VBox(10, darkMode, preview);
  }
challenges:
  - id: c1
    description: "Also bind the text fill: black in light mode, white in dark mode"
    assertion: containsNodeOfType(CheckBox)
nextLesson: 050-observable-list
---

# Bindings Utility Class

The `Bindings` class has ~100 static factory methods for building
observable expressions.

## Common helpers

```java
Bindings.add(a, b)           // a + b
Bindings.subtract(a, b)
Bindings.multiply(a, b)
Bindings.divide(a, b)
Bindings.max(a, b)
Bindings.concat(s1, s2)      // String concatenation
Bindings.format("%.2f", p)   // formatted string
Bindings.when(cond).then(v1).otherwise(v2)  // ternary
Bindings.createBooleanBinding(() -> expr, dep1, dep2)  // custom
```

## Challenge

Add a second `StringProperty` for text fill and bind it using
`Bindings.when(darkMode.selectedProperty()).then("white").otherwise("black")`.
