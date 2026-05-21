---
id: 017-stackpane-alignment
tier: layouts
order: 17
title: "StackPane and Alignment"
objectives:
  - "Layer children on top of each other with StackPane"
  - "Position individual children with StackPane.setAlignment()"
  - "Use StackPane for backgrounds, badges, and overlays"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      javafx.scene.shape.Rectangle bg = new javafx.scene.shape.Rectangle(280, 160);
      bg.setFill(Color.web("#2d4a8a"));
      bg.setArcWidth(12); bg.setArcHeight(12);

      Label title = new Label("Card title");
      title.setStyle("-fx-text-fill:white; -fx-font-size:18px; -fx-font-weight:bold;");
      StackPane.setAlignment(title, Pos.TOP_LEFT);
      StackPane.setMargin(title, new Insets(12, 0, 0, 14));

      Label body = new Label("Subtitle text — centered by default");
      body.setStyle("-fx-text-fill:#dbe4f5;");

      javafx.scene.shape.Circle badge = new javafx.scene.shape.Circle(10, Color.TOMATO);
      StackPane.setAlignment(badge, Pos.TOP_RIGHT);
      StackPane.setMargin(badge, new Insets(10, 12, 0, 0));

      return new StackPane(bg, title, body, badge);
  }
challenges:
  - id: c1
    description: "Add a Label('v1.0') aligned to Pos.BOTTOM_RIGHT with a 10px margin"
    assertion: 'containsLabeledWithText(text="v1.0")'
nextLesson: 018-borderpane
---

# StackPane and Alignment

`StackPane` layers all its children on top of each other. The first
child you add is on the **bottom**; each subsequent child paints over
the previous one. By default every child is centered.

This makes `StackPane` the natural choice for any "thing on top of
another thing" UI: a card with a colored background, a label with a
notification badge, a chart with a hover tooltip, a button with a
floating action icon.

## Per-child alignment

```java
StackPane.setAlignment(node, Pos.TOP_RIGHT);
```

Overrides the stack's default centering for that one child. The other
children stay centered (or wherever their own per-child alignment puts
them).

## Per-child margin

```java
StackPane.setMargin(node, new Insets(10, 12, 0, 0));
```

Adds outer spacing for that one child. Combined with alignment this
gives precise corner placement: `TOP_RIGHT` + 10/12 inset = "10px down,
12px in from the top-right corner."

## Common recipes

| Recipe | First child | Overlay children |
|---|---|---|
| Card | colored `Rectangle` with rounded corners | title, body, badge |
| Hero banner | `ImageView` | overlay text with semi-transparent background |
| Loading state | content `Region` | centered `ProgressIndicator` |
| Toast | spacer `Region` | message `Label` aligned `BOTTOM_CENTER` |

## Z-order

Reorder children to change layering: `pane.getChildren().add(0, node)`
puts the node at the bottom. `node.toFront()` brings a node to the top.

## Challenge

Add a `Label("v1.0")` to the card, aligned to `Pos.BOTTOM_RIGHT` with a
margin of `new Insets(0, 12, 10, 0)`.
