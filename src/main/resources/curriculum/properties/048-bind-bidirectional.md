---
id: 048-bind-bidirectional
tier: properties
order: 48
title: "Bidirectional Binding"
objectives:
  - "Keep two properties in sync with bindBidirectional()"
  - "Know when bidirectional binding is and isn't appropriate"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      TextField field1 = new TextField("hello");
      TextField field2 = new TextField();
      field1.textProperty().bindBidirectional(field2.textProperty());
      return new VBox(10, new Label("Field 1:"), field1,
                          new Label("Field 2:"), field2);
  }
challenges:
  - id: c1
    description: "Add a third TextField also bidirectionally bound to field1"
    assertion: containsNodeOfType(TextField)
nextLesson: 049-bindings-helpers
---

# Bidirectional Binding

`a.bindBidirectional(b)` keeps both properties in sync. Changing
either one updates the other.

Unlike `bind()`, both properties remain writable.

## When to use

- Two UI controls that must mirror the same model value
- View-model synchronization where both directions are user-driven

## When NOT to use

- When one side is read-only (use `bind()` instead)
- Long chains — changes cascade back and forth, which can be confusing

## Unbinding

```java
a.unbindBidirectional(b);
```

Note: you must call `unbindBidirectional` on the *same pair* — there
is no single "unbind all".

## Challenge

Add a third `TextField field3` and call
`field1.textProperty().bindBidirectional(field3.textProperty())`.
