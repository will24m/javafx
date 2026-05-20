---
id: 053-custom-property
tier: properties
order: 53
title: "Custom Property Classes"
objectives:
  - "Extend ReadOnlyIntegerWrapper to expose a read-only property"
  - "Build a full property with getter, setter, and property accessor"
estimatedMinutes: 12
starterSnippet: |
  public static Parent build() {
      // A model with a proper JavaFX property
      class Counter {
          private final IntegerProperty count = new SimpleIntegerProperty(0);
          public int getCount() { return count.get(); }
          public void increment() { count.set(count.get() + 1); }
          public ReadOnlyIntegerProperty countProperty() { return count; }
      }
      Counter c = new Counter();
      Label display = new Label();
      display.textProperty().bind(c.countProperty().asString("Count: %d"));
      Button btn = new Button("Increment");
      btn.setOnAction(e -> c.increment());
      return new VBox(10, display, btn);
  }
challenges:
  - id: c1
    description: "Add a reset() method to Counter and a 'Reset' button in the UI"
    assertion: containsNodeOfType(Button)
nextLesson: 054-binding-pitfalls
---

# Custom Property Classes

The three-method pattern is the standard way to expose properties from
model classes:

```java
private final StringProperty name = new SimpleStringProperty();

public String getName()             { return name.get(); }
public void setName(String v)       { name.set(v); }
public StringProperty nameProperty(){ return name; }
```

## Read-only properties

Use `ReadOnlyIntegerWrapper` (or the other `ReadOnly*Wrapper` classes)
to expose a property that callers can observe but not modify:

```java
private final ReadOnlyIntegerWrapper count = new ReadOnlyIntegerWrapper(0);
public int getCount()                    { return count.get(); }
public ReadOnlyIntegerProperty countProperty() { return count.getReadOnlyProperty(); }
private void increment()                  { count.set(count.get() + 1); }
```

## Challenge

Add `public void reset() { count.set(0); }` to the inner `Counter`
class and a `Button("Reset")` that calls `c.reset()`.
