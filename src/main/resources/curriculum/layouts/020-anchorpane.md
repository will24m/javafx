---
id: 020-anchorpane
tier: layouts
order: 20
title: "AnchorPane"
objectives:
  - "Pin nodes to the edges of an AnchorPane"
  - "Understand how anchors cause nodes to resize with the container"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      AnchorPane pane = new AnchorPane();
      Button topLeft = new Button("Top-Left");
      AnchorPane.setTopAnchor(topLeft, 10.0);
      AnchorPane.setLeftAnchor(topLeft, 10.0);

      Button bottomRight = new Button("Bottom-Right");
      AnchorPane.setBottomAnchor(bottomRight, 10.0);
      AnchorPane.setRightAnchor(bottomRight, 10.0);

      pane.getChildren().addAll(topLeft, bottomRight);
      return pane;
  }
challenges:
  - id: c1
    description: "Pin a Label('Centre-Left') to left=10, top and bottom = 0"
    assertion: containsNodeOfType(Label)
nextLesson: 021-flowpane-tilepane
---

# AnchorPane

`AnchorPane` pins nodes to its edges. Setting an anchor on a side
locks that distance from the corresponding edge, even as the pane
resizes.

## Static methods

```java
AnchorPane.setTopAnchor(node, 10.0);
AnchorPane.setRightAnchor(node, 10.0);
AnchorPane.setBottomAnchor(node, 10.0);
AnchorPane.setLeftAnchor(node, 10.0);
```

If you set both `top` and `bottom`, the node stretches vertically when
the pane is resized. Same for `left` + `right`.

## Use case

Classic tool for floating action buttons, resize-aware toolbars, or
any UI where you want "stick to corner" behaviour.

## Challenge

Add a `Label("Centre-Left")` pinned to `left=10.0`, `top=0.0`,
`bottom=0.0` so it stretches full height on the left edge.
