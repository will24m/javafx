---
id: 046-simple-properties
tier: properties
order: 46
title: "SimpleProperty Classes"
objectives:
  - "Create SimpleStringProperty, SimpleIntegerProperty, SimpleBooleanProperty"
  - "Use them as the backing store for a custom model"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      StringProperty name = new SimpleStringProperty("World");
      Label greeting = new Label();
      greeting.textProperty().bind(name.map(n -> "Hello, " + n + "!"));

      TextField editor = new TextField(name.get());
      editor.textProperty().addListener((obs, old, val) -> name.set(val));
      return new VBox(10, editor, greeting);
  }
challenges:
  - id: c1
    description: "Add an IntegerProperty for a visit count that increments on each keystroke"
    assertion: containsNodeOfType(Label)
nextLesson: 047-bind-one-way
---

# SimpleProperty Classes

JavaFX properties are the observable backing fields of your model.

| Class | Value type |
|---|---|
| `SimpleStringProperty` | `String` |
| `SimpleIntegerProperty` | `int` / `Integer` |
| `SimpleDoubleProperty` | `double` |
| `SimpleBooleanProperty` | `boolean` |
| `SimpleObjectProperty<T>` | any `T` |

## Canonical pattern

```java
private final StringProperty name = new SimpleStringProperty("default");
public String getName() { return name.get(); }
public void setName(String v) { name.set(v); }
public StringProperty nameProperty() { return name; }
```

This three-method pattern lets callers use the property directly for
binding, or call the getter/setter for simple access.

## Challenge

Add an `IntegerProperty visitCount = new SimpleIntegerProperty(0)`,
increment it in the `addListener`, and bind a second `Label` to display
the count.
