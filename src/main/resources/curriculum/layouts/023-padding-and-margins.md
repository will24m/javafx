---
id: 023-padding-and-margins
tier: layouts
order: 23
title: "Padding and Margins"
objectives:
  - "Add inner spacing with setPadding()"
  - "Add outer spacing with VBox.setMargin() / HBox.setMargin()"
estimatedMinutes: 6
starterSnippet: |
  public static Parent build() {
      Button btn = new Button("Padded Button");
      btn.setPadding(new Insets(12, 24, 12, 24));

      VBox box = new VBox(btn);
      VBox.setMargin(btn, new Insets(20));
      box.setStyle("-fx-border-color: red;");
      return box;
  }
challenges:
  - id: c1
    description: "Change the button's padding to Insets(4)"
    assertion: containsNodeOfType(Button)
nextLesson: 024-growth-priorities
---

# Padding and Margins

## Padding

`node.setPadding(new Insets(top, right, bottom, left))` adds space
*inside* the node's border, between the border and its content.

`new Insets(n)` is shorthand for equal padding on all four sides.

## Margins

Layout panes support per-child margins via static methods:

```java
VBox.setMargin(child, new Insets(top, right, bottom, left));
HBox.setMargin(child, new Insets(...));
GridPane.setMargin(child, new Insets(...));
```

This adds space *outside* the child, between the child and its siblings.

## Challenge

Change the button's padding to `new Insets(4)` (4px on all sides).
