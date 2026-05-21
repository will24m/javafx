---
id: 021-flowpane-tilepane
tier: layouts
order: 21
title: "FlowPane and TilePane"
objectives:
  - "Wrap children automatically with FlowPane"
  - "Arrange equal-sized tiles in a uniform grid with TilePane"
  - "Choose between them: variable vs uniform child sizes"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      // FlowPane — children keep their natural size, wrap when row fills
      FlowPane chips = new FlowPane(8, 8);
      chips.setPadding(new Insets(10));
      String[] tags = {"design", "javafx", "ui", "css", "components",
                       "layout", "testing", "performance"};
      for (String tag : tags) {
          Label chip = new Label("#" + tag);
          chip.setStyle("-fx-background-color:#2d4a8a; -fx-text-fill:white; "
                      + "-fx-padding:4 10 4 10; -fx-background-radius:12;");
          chips.getChildren().add(chip);
      }

      // TilePane — uniform-sized cells in a regular grid
      TilePane tiles = new TilePane(8, 8);
      tiles.setPadding(new Insets(10));
      tiles.setPrefTileWidth(70);
      tiles.setPrefTileHeight(50);
      for (int i = 1; i <= 9; i++) {
          Button b = new Button(String.valueOf(i));
          b.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
          tiles.getChildren().add(b);
      }

      VBox stack = new VBox(8,
          new Label("FlowPane — wraps based on natural sizes"), chips,
          new Label("TilePane — uniform cells"), tiles);
      stack.setPadding(new Insets(10));
      return stack;
  }
challenges:
  - id: c1
    description: "Add a 10th button to the TilePane labeled '0'"
    assertion: 'containsLabeledWithText(text="0")'
nextLesson: 022-splitpane-scrollpane
---

# FlowPane and TilePane

Both panes wrap children to multiple lines. The difference is whether
each cell has its **own** size or a **uniform** size.

## FlowPane

Children flow left-to-right (or top-to-bottom) and wrap to the next
line when they run out of room. Each child keeps its preferred size —
some can be wide, some narrow. Perfect for tag clouds, chip lists,
toolbars that overflow, and any "natural reading order" layout.

```java
FlowPane flow = new FlowPane(hgap, vgap);
flow.setOrientation(Orientation.HORIZONTAL);   // or VERTICAL
flow.setAlignment(Pos.TOP_LEFT);
```

Resize the preview pane to see the chips re-wrap.

## TilePane

Like `FlowPane` but every cell is the **same size** — by default the
size of the largest child. Useful when you want a regular grid:
photo grids, calendar days, sample swatches, keypads.

```java
TilePane tiles = new TilePane(8, 8);
tiles.setPrefTileWidth(70);
tiles.setPrefTileHeight(50);
tiles.setPrefColumns(3);                       // optional column count
```

## Which to use

| Need | Use |
|---|---|
| Children with different natural widths | `FlowPane` |
| Children with identical sizing | `TilePane` |
| Strictly aligned rows AND columns | `TilePane` or `GridPane` |
| Maximum flexibility, irregular layout | `FlowPane` |

## Difference from GridPane

`GridPane` takes explicit `(column, row)` coordinates from you.
`FlowPane` / `TilePane` decide placement automatically based on
available space — you just hand them children in order.

## Challenge

Add a 10th button labeled `"0"` to the `TilePane`. It should wrap to a
fourth row (since the default column count is 3 tiles wide here).
