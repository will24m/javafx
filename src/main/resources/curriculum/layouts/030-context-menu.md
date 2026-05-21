---
id: 030-context-menu
tier: layouts
order: 30
title: "ContextMenu"
objectives:
  - "Attach a right-click context menu to a node"
  - "Populate menu items dynamically based on what was clicked"
  - "Use setContextMenu() on Controls instead of manually showing"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      Label target = new Label("Right-click me");
      target.setStyle("-fx-background-color:#eef0f4; -fx-padding:24;"
                    + " -fx-font-size:14px; -fx-border-color:#bbb;"
                    + " -fx-border-style:dashed;");
      Label log = new Label("Menu actions appear here.");
      log.setStyle("-fx-padding:8 12 8 12;");

      MenuItem copy   = new MenuItem("Copy");
      MenuItem rename = new MenuItem("Rename…");
      MenuItem delete = new MenuItem("Delete");
      delete.setStyle("-fx-text-fill:#a02020;");

      copy.setOnAction(e   -> log.setText("Copied."));
      rename.setOnAction(e -> log.setText("Rename dialog would open here."));
      delete.setOnAction(e -> log.setText("Deleted!"));

      ContextMenu menu = new ContextMenu(copy, rename, new SeparatorMenuItem(), delete);

      // setOnContextMenuRequested fires on right-click, two-finger tap,
      // and the platform's context-menu key — never use setOnMouseClicked.
      target.setOnContextMenuRequested(e ->
          menu.show(target, e.getScreenX(), e.getScreenY()));

      return new VBox(8, target, log);
  }
challenges:
  - id: c1
    description: "Add a 'Duplicate' MenuItem between Rename and the separator"
    assertion: 'containsLabeledWithText(text="Right-click me")'
nextLesson: 031-button-variants
---

# ContextMenu

A `ContextMenu` is a floating list of `MenuItem`s that appears on
right-click and dismisses when the user clicks elsewhere or presses
Escape. It's the same widget the OS shows when you right-click a file
in Finder or Explorer.

## Showing manually

```java
ContextMenu menu = new ContextMenu(item1, item2, item3);
node.setOnContextMenuRequested(e ->
    menu.show(node, e.getScreenX(), e.getScreenY()));
```

`ContextMenuEvent` carries both screen coordinates and the node-local
coordinates of the click. Pass `screenX/Y` to `show()` because the menu
is a separate top-level popup window.

## Always use `setOnContextMenuRequested`

Not `setOnMouseClicked` checking for `MouseButton.SECONDARY`. Reasons:

1. The OS context-menu **keyboard** key fires `ContextMenuEvent` but
   not `MouseEvent` — your keyboard users would be locked out.
2. On macOS, **two-finger tap** on a trackpad fires
   `ContextMenuEvent` but registers as a primary mouse click.
3. The platform sometimes opens the menu at a different position
   (e.g., centered on the focused node when triggered by keyboard).
   The default event handles this correctly.

## Controls have built-in support

Most JavaFX controls (`TextField`, `Button`, `Label`, `ListView`, etc.)
have a `contextMenu` property — you don't need to wire the event
yourself:

```java
ListView<String> list = new ListView<>();
list.setContextMenu(new ContextMenu(
    new MenuItem("Rename"),
    new MenuItem("Delete")));
```

JavaFX shows it automatically on right-click of the control.

## Dynamic items

You can clear and rebuild the menu inside the request handler to make
it context-sensitive — e.g., a list whose menu changes based on the
selected row:

```java
node.setOnContextMenuRequested(e -> {
    menu.getItems().setAll(buildItemsFor(currentSelection()));
    menu.show(node, e.getScreenX(), e.getScreenY());
});
```

## Specialized menu items

Same set as `MenuBar`:

- `SeparatorMenuItem` — visual divider
- `CheckMenuItem` — toggleable, has `selectedProperty()`
- `RadioMenuItem` — for mutually exclusive options
- `CustomMenuItem` — wraps any `Node` inside a menu row (sliders,
  color pickers, etc.)

## Challenge

Insert a `MenuItem("Duplicate")` after `rename` and before the
`SeparatorMenuItem`. Wire its action to set the log to
`"Duplicated."`.
