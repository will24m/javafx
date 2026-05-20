---
id: 022-splitpane-scrollpane
tier: layouts
order: 22
title: "SplitPane and ScrollPane"
objectives:
  - "Create a resizable split layout with SplitPane"
  - "Make content scrollable with ScrollPane"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      VBox left = new VBox(8);
      for (int i = 1; i <= 20; i++) left.getChildren().add(new Label("Item " + i));
      ScrollPane scroll = new ScrollPane(left);
      scroll.setFitToWidth(true);

      Label right = new Label("Select an item");
      SplitPane split = new SplitPane(scroll, right);
      split.setDividerPositions(0.35);
      return split;
  }
challenges:
  - id: c1
    description: "Move the divider to 0.5 (50/50 split)"
    assertion: containsNodeOfType(SplitPane)
nextLesson: 023-padding-and-margins
---

# SplitPane and ScrollPane

## SplitPane

Divides space between two (or more) children with a draggable divider.

```java
SplitPane split = new SplitPane(left, right);
split.setDividerPositions(0.35); // 35% left, 65% right
```

## ScrollPane

Wraps content that may be larger than the viewport. The user scrolls
to see the rest.

```java
ScrollPane scroll = new ScrollPane(content);
scroll.setFitToWidth(true);  // content fills horizontal width
```

`setFitToWidth(true)` is almost always what you want for a vertical
list — it prevents a horizontal scrollbar from appearing.

## Challenge

Move the divider to `0.5` for a 50/50 split.
