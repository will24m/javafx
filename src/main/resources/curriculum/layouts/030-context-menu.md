---
id: 030-context-menu
tier: layouts
order: 30
title: "ContextMenu"
objectives:
  - "Attach a right-click context menu to a node"
  - "Dynamically populate menu items based on context"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      Label target = new Label("Right-click me");
      target.setPadding(new Insets(20));

      ContextMenu menu = new ContextMenu();
      MenuItem copy = new MenuItem("Copy");
      MenuItem delete = new MenuItem("Delete");
      menu.getItems().addAll(copy, delete);

      target.setOnContextMenuRequested(e ->
          menu.show(target, e.getScreenX(), e.getScreenY()));
      return new StackPane(target);
  }
challenges:
  - id: c1
    description: "Add a 'Rename' MenuItem between Copy and Delete"
    assertion: containsNodeOfType(Label)
nextLesson: 031-button-variants
---

# ContextMenu

A `ContextMenu` appears on right-click and dismisses when the user
clicks elsewhere or presses Escape.

## Showing

```java
menu.show(ownerNode, screenX, screenY);
```

`setOnContextMenuRequested` fires on right-click, two-finger tap, or
the platform's context-menu key. Always use this event rather than
`setOnMouseClicked` so keyboard users are supported too.

## Dynamic items

You can clear and rebuild `menu.getItems()` inside the event handler
to show context-sensitive options based on what was clicked.

## Challenge

Insert a `MenuItem("Rename")` between `Copy` and `Delete`.
