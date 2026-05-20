---
id: 056-sorting-filtering
tier: properties
order: 56
title: "Sorting and Filtering with SortedList and FilteredList"
objectives:
  - "Wrap an ObservableList in FilteredList to show a subset"
  - "Use SortedList to keep items sorted reactively"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      ObservableList<String> source = FXCollections.observableArrayList(
          "Apple", "Banana", "Avocado", "Blueberry", "Apricot", "Cherry");

      TextField search = new TextField();
      search.setPromptText("Filter...");

      FilteredList<String> filtered = new FilteredList<>(source);
      search.textProperty().addListener((obs, old, q) ->
          filtered.setPredicate(s -> q.isEmpty() || s.toLowerCase().contains(q.toLowerCase())));

      ListView<String> list = new ListView<>(new SortedList<>(filtered, Comparator.naturalOrder()));
      return new VBox(8, search, list);
  }
challenges:
  - id: c1
    description: "Add a ToggleButton that switches between ascending and descending sort"
    assertion: containsNodeOfType(ListView)
nextLesson: 057-observable-value-map
---

# Sorting and Filtering

## FilteredList

Wraps an `ObservableList` and exposes only items matching a
`Predicate`. The predicate can be changed at any time.

```java
FilteredList<T> filtered = new FilteredList<>(source, item -> true);
filtered.setPredicate(item -> item.startsWith("A"));
```

## SortedList

Maintains items in sorted order as the underlying list changes.

```java
SortedList<T> sorted = new SortedList<>(filtered, comparator);
// Bind to a TableView's comparator:
sorted.comparatorProperty().bind(table.comparatorProperty());
```

## Challenge

Add a `ToggleButton("A→Z / Z→A")` that flips the `SortedList`'s
`Comparator` between `naturalOrder()` and `reverseOrder()`.
