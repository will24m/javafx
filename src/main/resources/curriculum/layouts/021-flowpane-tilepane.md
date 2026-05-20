---
id: 021-flowpane-tilepane
tier: layouts
order: 21
title: "FlowPane and TilePane"
objectives:
  - "Wrap children automatically with FlowPane"
  - "Arrange fixed-size tiles with TilePane"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      FlowPane flow = new FlowPane(8, 8);
      for (int i = 1; i <= 12; i++) {
          flow.getChildren().add(new Button("Item " + i));
      }
      return flow;
  }
challenges:
  - id: c1
    description: "Change FlowPane to a TilePane with prefTileWidth=80"
    assertion: containsNodeOfType(Button)
nextLesson: 022-splitpane-scrollpane
---

# FlowPane and TilePane

## FlowPane

Children flow left-to-right (or top-to-bottom) and wrap to the next
line when they run out of space. Each child keeps its preferred size.
Resize the preview pane to see wrapping in action.

```java
new FlowPane(hgap, vgap)
```

## TilePane

Like `FlowPane` but all tiles have the same size (the size of the
largest child, or `prefTileWidth`/`prefTileHeight` if set).

```java
TilePane tiles = new TilePane(8, 8);
tiles.setPrefTileWidth(80);
```

## Challenge

Replace `FlowPane` with a `TilePane` that has `prefTileWidth = 80`.
