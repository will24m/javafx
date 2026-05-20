---
id: 012-group-vs-parent
tier: foundations
order: 12
title: "Group vs. Parent"
objectives:
  - "Understand when to use Group instead of a layout pane"
  - "Know that Group does not resize its children"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      javafx.scene.shape.Rectangle r = new javafx.scene.shape.Rectangle(60, 40, Color.STEELBLUE);
      javafx.scene.shape.Circle c = new javafx.scene.shape.Circle(30, Color.TOMATO);
      c.setTranslateX(80);
      Group g = new Group(r, c);
      return new StackPane(g);
  }
challenges:
  - id: c1
    description: "Add a third shape — a Rectangle(40,40) — translated to x=160"
    assertion: containsNodeOfType(Group)
nextLesson: 013-basic-shapes
---

# Group vs. Parent

`Group` is the simplest `Parent`. Unlike `VBox` or `StackPane` it does
**not** perform layout — children are positioned by their own
`translateX`/`translateY` and `layoutX`/`layoutY`. The group's bounds
are exactly the union of its children's bounds.

## When to use Group

- 2-D drawing / canvas-like composition
- Grouping nodes for a shared transformation (rotate, scale, clip)
- Avoiding layout overhead for a large set of absolutely-positioned nodes

## When NOT to use Group

Use a layout pane (`VBox`, `HBox`, `GridPane`, etc.) whenever you need
automatic sizing or alignment.

## Challenge

Add a third `Rectangle(40, 40, Color.CORAL)` translated to `x = 160`.
