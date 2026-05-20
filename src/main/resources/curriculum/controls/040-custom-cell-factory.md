---
id: 040-custom-cell-factory
tier: controls
order: 40
title: "Custom Cell Factories"
objectives:
  - "Render complex list items with a custom ListCell"
  - "Understand cell reuse and the updateItem contract"
estimatedMinutes: 12
starterSnippet: |
  public static Parent build() {
      record Item(String name, String badge) {}
      ObservableList<Item> items = FXCollections.observableArrayList(
          new Item("Home", "NEW"), new Item("Settings", ""), new Item("Profile", "3"));

      ListView<Item> list = new ListView<>(items);
      list.setCellFactory(lv -> new ListCell<>() {
          final Label name = new Label();
          final Label badge = new Label();
          final HBox row = new HBox(8, name, badge);
          @Override protected void updateItem(Item item, boolean empty) {
              super.updateItem(item, empty);
              if (empty || item == null) { setGraphic(null); return; }
              name.setText(item.name());
              badge.setText(item.badge());
              badge.setVisible(!item.badge().isEmpty());
              setGraphic(row);
          }
      });
      return list;
  }
challenges:
  - id: c1
    description: "Style the badge label with a red background and white text"
    assertion: containsNodeOfType(ListView)
nextLesson: 041-pagination
---

# Custom Cell Factories

The default cell factory renders items with `toString()`. For richer
content you provide your own `ListCell<T>`.

## The updateItem contract

`updateItem(item, empty)` is called every time a cell is reused.
You **must** call `super.updateItem(item, empty)` first.

Always handle `empty == true` by clearing all graphics — cells are
reused across positions and may have stale content.

## Cell reuse

JavaFX creates only enough cells to fill the viewport plus a small
buffer. Scrolling reuses existing cells, which is why `updateItem` is
called so frequently.

## Challenge

Add `badge.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-padding: 2 6 2 6;")`.
