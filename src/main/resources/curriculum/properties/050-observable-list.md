---
id: 050-observable-list
tier: properties
order: 50
title: "ObservableList"
objectives:
  - "Understand how FXCollections.observableArrayList differs from ArrayList"
  - "Listen to list changes with ListChangeListener"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      ObservableList<String> items = FXCollections.observableArrayList("a", "b", "c");
      Label sizeLabel = new Label("Size: " + items.size());
      items.addListener((ListChangeListener<String>) change -> {
          while (change.next()) {
              if (change.wasAdded()) sizeLabel.setText("Added: " + change.getAddedSubList());
              if (change.wasRemoved()) sizeLabel.setText("Removed: " + change.getRemoved());
          }
      });
      Button add = new Button("Add item");
      add.setOnAction(e -> items.add("item " + items.size()));
      Button remove = new Button("Remove last");
      remove.setOnAction(e -> { if (!items.isEmpty()) items.remove(items.size()-1); });
      return new VBox(8, sizeLabel, new HBox(8, add, remove));
  }
challenges:
  - id: c1
    description: "Bind a ListView to the ObservableList so it updates automatically"
    assertion: containsNodeOfType(ListView)
nextLesson: 051-observable-map-set
---

# ObservableList

`FXCollections.observableArrayList()` returns a `List` that fires
change events when items are added, removed, or replaced.

Controls like `ListView`, `ComboBox`, and `TableView` accept
`ObservableList` directly — they update automatically without manual
refresh.

## ListChangeListener

```java
list.addListener((ListChangeListener<T>) change -> {
    while (change.next()) {
        if (change.wasAdded()) { ... change.getAddedSubList() ... }
        if (change.wasRemoved()) { ... change.getRemoved() ... }
        if (change.wasUpdated()) { ... }
        if (change.wasMoved()) { ... }
    }
});
```

You must call `change.next()` in a loop — a single event can describe
multiple changes.

## Challenge

Bind a `ListView<String>` to the `items` list and add it to the layout.
