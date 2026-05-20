---
id: 051-observable-map-set
tier: properties
order: 51
title: "ObservableMap and ObservableSet"
objectives:
  - "Use FXCollections.observableHashMap and observableSet"
  - "Attach MapChangeListener and SetChangeListener"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      ObservableMap<String, Integer> scores = FXCollections.observableHashMap();
      scores.put("Alice", 100);
      Label display = new Label(scores.toString());
      scores.addListener((MapChangeListener<String, Integer>) change -> {
          display.setText(scores.toString());
      });
      Button add = new Button("Add Bob: 90");
      add.setOnAction(e -> scores.put("Bob", 90));
      return new VBox(10, display, add);
  }
challenges:
  - id: c1
    description: "Add a button that removes 'Alice' from the map"
    assertion: containsNodeOfType(Button)
nextLesson: 052-weak-listeners
---

# ObservableMap and ObservableSet

## ObservableMap

```java
ObservableMap<K, V> map = FXCollections.observableHashMap();
map.addListener((MapChangeListener<K, V>) change -> {
    if (change.wasAdded()) { ... change.getKey() ... change.getValueAdded() ... }
    if (change.wasRemoved()) { ... change.getValueRemoved() ... }
});
```

## ObservableSet

```java
ObservableSet<T> set = FXCollections.observableSet();
set.addListener((SetChangeListener<T>) change -> {
    if (change.wasAdded()) { ... change.getElementAdded() ... }
    if (change.wasRemoved()) { ... change.getElementRemoved() ... }
});
```

## When to use

Use these when you need reactive map/set semantics — e.g. tracking
which items are "selected" across multiple views, or a reactive
configuration store.

## Challenge

Add a `Button("Remove Alice")` that calls `scores.remove("Alice")`.
