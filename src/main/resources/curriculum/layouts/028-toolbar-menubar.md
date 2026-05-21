---
id: 028-toolbar-menubar
tier: layouts
order: 28
title: "ToolBar and MenuBar"
objectives:
  - "Build a horizontal action strip with ToolBar"
  - "Create a classic application menu bar with Menu and MenuItem"
  - "Wire actions, accelerators, and separators"
estimatedMinutes: 12
starterSnippet: |
  public static Parent build() {
      Label status = new Label("Ready");
      status.setStyle("-fx-padding:6 12 6 12; -fx-background-color:#2b2d30;"
                    + " -fx-text-fill:#dbe4f5;");
      status.setMaxWidth(Double.MAX_VALUE);

      // --- Menu bar ---
      MenuItem newDoc  = new MenuItem("New");
      newDoc.setAccelerator(javafx.scene.input.KeyCombination.keyCombination("Shortcut+N"));
      newDoc.setOnAction(e -> status.setText("File → New"));

      MenuItem openDoc = new MenuItem("Open…");
      openDoc.setOnAction(e -> status.setText("File → Open"));

      MenuItem saveDoc = new MenuItem("Save");
      saveDoc.setAccelerator(javafx.scene.input.KeyCombination.keyCombination("Shortcut+S"));
      saveDoc.setOnAction(e -> status.setText("File → Save"));

      Menu file = new Menu("File");
      file.getItems().addAll(newDoc, openDoc, new SeparatorMenuItem(), saveDoc);

      Menu edit = new Menu("Edit");
      edit.getItems().addAll(new MenuItem("Undo"), new MenuItem("Redo"));

      MenuBar menuBar = new MenuBar(file, edit);

      // --- Tool bar ---
      Button bNew  = new Button("New");
      Button bOpen = new Button("Open");
      Button bSave = new Button("Save");
      bNew.setOnAction(e -> status.setText("Toolbar → New"));
      bOpen.setOnAction(e -> status.setText("Toolbar → Open"));
      bSave.setOnAction(e -> status.setText("Toolbar → Save"));

      ToolBar toolbar = new ToolBar(bNew, bOpen, new Separator(), bSave);

      VBox root = new VBox(menuBar, toolbar, status);
      VBox.setVgrow(status, Priority.NEVER);
      return root;
  }
challenges:
  - id: c1
    description: "Add a 'View' menu with a 'Zoom In' MenuItem to the MenuBar"
    assertion: 'containsNodeOfType(MenuBar)'
nextLesson: 029-dialogs
---

# ToolBar and MenuBar

Two complementary controls for offering actions to the user.

## ToolBar

A horizontal strip of arbitrary nodes — usually `Button`, but it
accepts anything: `ComboBox`, `TextField`, `ToggleButton`, custom
widgets.

```java
ToolBar bar = new ToolBar(
    new Button("New"),
    new Button("Open"),
    new Separator(),                           // vertical line
    new Button("Save"),
    new Region() {{ HBox.setHgrow(this, Priority.ALWAYS); }},   // spacer
    new TextField()
);
```

Use `Separator` to group related items. The pattern *button-button-
separator-button-spacer-search* is how IDE and browser toolbars are
built.

`bar.setOrientation(Orientation.VERTICAL)` flips it sideways for a
left-rail toolbar.

## MenuBar

A traditional application menu bar. On macOS it can be promoted to
the system menu bar by calling `menuBar.setUseSystemMenuBar(true)` —
the strip then disappears from the window and appears at the top of
the screen.

```java
MenuItem item = new MenuItem("Save");
item.setOnAction(e -> save());
item.setAccelerator(KeyCombination.keyCombination("Shortcut+S"));

Menu file = new Menu("File");
file.getItems().addAll(
    new MenuItem("New"),
    new MenuItem("Open…"),
    new SeparatorMenuItem(),                   // horizontal divider
    item
);

MenuBar bar = new MenuBar(file, editMenu, viewMenu);
```

### Accelerators

`KeyCombination.keyCombination("Shortcut+S")` is the platform-aware
shortcut: `Cmd+S` on macOS, `Ctrl+S` on Windows/Linux. Always prefer
`Shortcut` over `Ctrl` so your app feels native everywhere.

### Specialized menu items

- `SeparatorMenuItem` — divider line between groups
- `CheckMenuItem` — toggleable, has `selectedProperty()`
- `RadioMenuItem` — like `RadioButton`, group with `ToggleGroup`
- `Menu` inside `Menu` — nested submenus

## Toolbar vs menu — both?

Real apps usually expose the same actions in **both** the toolbar and
the menu bar. Don't duplicate `setOnAction` logic — extract it into a
method or a JavaFX `Action`-style class so both UIs trigger the same
code path.

## Challenge

Add `Menu view = new Menu("View")` with a single `MenuItem("Zoom In")`,
then add it to the `MenuBar`.
