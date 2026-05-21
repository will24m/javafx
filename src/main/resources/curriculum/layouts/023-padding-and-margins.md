---
id: 023-padding-and-margins
tier: layouts
order: 23
title: "Padding and Margins"
objectives:
  - "Add inner spacing with setPadding()"
  - "Add outer spacing per-child with VBox/HBox/GridPane.setMargin()"
  - "Know all four Insets constructors and when to use each"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      // Inner padding — visible because the button has a colored background
      Button padded = new Button("Padded button");
      padded.setStyle("-fx-background-color:#2d4a8a; -fx-text-fill:white;");
      padded.setPadding(new Insets(10, 20, 10, 20));   // top, right, bottom, left

      // Outer margin via VBox.setMargin — pushes the row away from siblings
      Label tag = new Label("MARGIN demo");
      tag.setStyle("-fx-background-color:#fde68a; -fx-padding:4 8 4 8;");

      VBox box = new VBox(0, padded, tag);
      VBox.setMargin(tag, new Insets(20, 0, 0, 40));

      box.setPadding(new Insets(16));
      box.setStyle("-fx-background-color:#e8eef7; -fx-border-color:#888;");
      return box;
  }
challenges:
  - id: c1
    description: "Increase the button's padding to Insets(20)"
    assertion: 'containsNodeOfType(Button)'
nextLesson: 024-growth-priorities
---

# Padding and Margins

Two different things people often confuse:

| Spacing | Lives | Set with |
|---|---|---|
| **Padding** | *inside* the node, between its border and its content | `node.setPadding(...)` |
| **Margin** | *outside* the node, between it and its siblings | `LayoutPane.setMargin(node, ...)` |

If the node has a visible background or border, the difference is
obvious — padding extends the colored area, margin does not.

## Insets constructors

```java
new Insets(10);                       // 10px on all four sides
new Insets(10, 20);                   // top/bottom 10, left/right 20
new Insets(10, 20, 30, 40);           // top, right, bottom, left  (CSS order)
Insets.EMPTY;                         // zero padding sentinel
```

The four-argument constructor matches the CSS shorthand: top, right,
bottom, left (clockwise from 12).

## Padding

Every `Region` (which is every layout pane and most controls) has a
`paddingProperty()`:

```java
container.setPadding(new Insets(16));     // shrink the usable area by 16
```

Padding is consumed *before* children are laid out — a `VBox` with
`Insets(16)` padding and a single 100-wide child will report a preferred
width of `100 + 16 + 16 = 132`.

## Margins (per-child)

Layout panes accept per-child margins via static methods on the parent
class. The margin is stored as a layout constraint on the child:

```java
VBox.setMargin(child, new Insets(0, 0, 12, 0));    // push next child 12 down
HBox.setMargin(child, new Insets(0, 8, 0, 0));     // push next child 8 right
GridPane.setMargin(child, new Insets(4));          // cell padding-of-one
StackPane.setMargin(child, new Insets(10));        // breathing room in stack
```

Margins respect alignment — a `VBox.setMargin` with `Insets(0, 0, 0, 40)`
on a `Pos.CENTER_LEFT`-aligned child shifts it 40px to the right of the
left edge.

## When to use which

- **Padding** for the container's internal breathing room
- **Padding** on a control (`Button.setPadding`) to make it visually larger
- **Margin** to space one specific child differently from its siblings
- **Spacing** (the `VBox`/`HBox` constructor argument) for uniform gaps
  between every adjacent pair of children

## Challenge

Change the button's padding from `Insets(10, 20, 10, 20)` to
`Insets(20)` — equal 20px padding on every side.
