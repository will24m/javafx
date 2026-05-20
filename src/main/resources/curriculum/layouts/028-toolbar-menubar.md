---
id: 028-toolbar-menubar
tier: layouts
order: 28
title: "ToolBar and MenuBar"
objectives:
  - "Build a tool bar with buttons and separators"
  - "Create a menu bar with menus and menu items"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      ToolBar toolbar = new ToolBar(
          new Button("New"),
          new Button("Open"),
          new Separator(),
          new Button("Save")
      );

      Menu file = new Menu("File");
      file.getItems().addAll(new MenuItem("New"), new MenuItem("Open"), new MenuItem("Save"));
      MenuBar menuBar = new MenuBar(file);

      return new VBox(menuBar, toolbar, new Label("Content area"));
  }
challenges:
  - id: c1
    description: "Add an 'Edit' menu with a 'Cut' MenuItem"
    assertion: containsNodeOfType(MenuBar)
nextLesson: 029-dialogs
---

# ToolBar and MenuBar

## ToolBar

A horizontal strip of controls. Accepts any `Node` — typically
`Button`, `Separator`, `ComboBox`, `TextField`.

```java
new ToolBar(node1, node2, new Separator(), node3)
```

## MenuBar

A traditional application menu bar. `Menu` is a top-level entry;
`MenuItem` is a clickable item inside it.

```java
MenuItem saveItem = new MenuItem("Save");
saveItem.setOnAction(e -> { ... });
```

Sub-menus: put a `Menu` inside another `Menu`.

## Challenge

Add a second `Menu("Edit")` with a `MenuItem("Cut")` to the `MenuBar`.
