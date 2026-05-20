---
id: 017-stackpane-alignment
tier: layouts
order: 17
title: "StackPane and Alignment"
objectives:
  - "Layer children with StackPane"
  - "Position individual children with StackPane.setAlignment()"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      javafx.scene.shape.Rectangle bg = new javafx.scene.shape.Rectangle(200, 120, Color.STEELBLUE);
      Label centre = new Label("Centre");
      Label topRight = new Label("Top-Right");
      StackPane.setAlignment(topRight, Pos.TOP_RIGHT);
      return new StackPane(bg, centre, topRight);
  }
challenges:
  - id: c1
    description: "Add a Label('Bottom-Left') aligned to Pos.BOTTOM_LEFT"
    assertion: containsLabeledWithText(text="Bottom-Left")
nextLesson: 018-borderpane
---

# StackPane and Alignment

`StackPane` layers all its children on top of each other, centred by
default. The last child added is on top.

## Per-child alignment

`StackPane.setAlignment(node, Pos.TOP_RIGHT)` overrides the stack
alignment for that specific child. The other children remain centred.

## Common patterns

- Background `Rectangle` or `ImageView` as first child
- Overlay text or controls as subsequent children
- Badges, notification dots, floating action buttons

## Challenge

Add a `Label("Bottom-Left")` and align it to `Pos.BOTTOM_LEFT` inside
the `StackPane`.
