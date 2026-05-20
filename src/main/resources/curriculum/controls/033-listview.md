---
id: 033-listview
tier: controls
order: 33
title: "ListView"
objectives:
  - "Display a scrollable list of items"
  - "React to selection and provide a custom cell factory"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      ObservableList<String> items = FXCollections.observableArrayList(
          "Apple", "Banana", "Cherry", "Date", "Elderberry");
      ListView<String> list = new ListView<>(items);
      list.setPrefHeight(160);

      Label selection = new Label("Nothing selected");
      list.getSelectionModel().selectedItemProperty()
          .addListener((obs, old, val) -> selection.setText("Selected: " + val));
      return new VBox(8, list, selection);
  }
challenges:
  - id: c1
    description: "Enable multiple selection mode"
    assertion: containsNodeOfType(ListView)
nextLesson: 034-treeview
---

# ListView

`ListView<T>` shows a scrollable, virtualized list. *Virtualized* means
only the visible cells are constructed — it can handle thousands of
items without performance issues.

## Selection modes

```java
list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
```

Default is `SINGLE`.

## Custom cells

```java
list.setCellFactory(lv -> new ListCell<String>() {
    @Override protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty ? null : item.toUpperCase());
    }
});
```

## Challenge

Enable multiple selection:
`list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE)`.
