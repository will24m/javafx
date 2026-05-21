---
id: 022-splitpane-scrollpane
tier: layouts
order: 22
title: "SplitPane and ScrollPane"
objectives:
  - "Build a resizable two-pane layout with SplitPane"
  - "Make content scroll with ScrollPane and setFitToWidth()"
  - "Combine the two for the classic master-detail layout"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      // Master: scrollable list of items
      VBox list = new VBox(2);
      list.setPadding(new Insets(8));
      Label detail = new Label("Select an item from the left");
      detail.setStyle("-fx-padding:16; -fx-font-size:14px;");

      for (int i = 1; i <= 30; i++) {
          int idx = i;
          Button row = new Button("Item " + i);
          row.setMaxWidth(Double.MAX_VALUE);
          row.setOnAction(e -> detail.setText("You picked item " + idx + "."));
          list.getChildren().add(row);
      }
      ScrollPane scroll = new ScrollPane(list);
      scroll.setFitToWidth(true);
      scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

      // Detail pane on the right, wrapped so we can pad it
      StackPane detailPane = new StackPane(detail);
      detailPane.setStyle("-fx-background-color:#ffffff;");

      SplitPane split = new SplitPane(scroll, detailPane);
      split.setDividerPositions(0.35);
      return split;
  }
challenges:
  - id: c1
    description: "Move the divider to 0.5 for a 50/50 split"
    assertion: 'containsNodeOfType(SplitPane)'
nextLesson: 023-padding-and-margins
---

# SplitPane and ScrollPane

Two essential layout panes that almost every non-trivial app uses.

## SplitPane

Divides space between two (or more) children with a draggable divider
the user can grab. Each child sits in its own resizable region.

```java
SplitPane split = new SplitPane(left, right);
split.setDividerPositions(0.35);               // 35% left, 65% right
split.setOrientation(Orientation.VERTICAL);    // top/bottom instead
```

You can chain more than two children — the divider count is one less
than the number of children, and `setDividerPositions(...)` accepts a
varargs of fractions.

### Locking a side

To prevent one side from being resized below a minimum:

```java
SplitPane.setResizableWithParent(child, false);   // child keeps its size
child.setMinWidth(160);                           // hard minimum
```

## ScrollPane

Wraps content that may be larger than its viewport. The user scrolls to
see the rest.

```java
ScrollPane scroll = new ScrollPane(content);
scroll.setFitToWidth(true);
scroll.setHbarPolicy(ScrollBarPolicy.NEVER);   // vertical scroll only
scroll.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
```

`setFitToWidth(true)` is what you almost always want for a vertical
list: it stretches the content to fill the viewport's width so a
horizontal scrollbar never appears. Without it, a long list of
left-aligned items will overflow horizontally if any one item is wider
than the viewport.

`setFitToHeight(true)` is the equivalent for a horizontal scroll
container.

## The master-detail pattern

The starter snippet is the **master-detail** layout, the most common
two-pane UI on desktop: a scrollable list of items on the left, the
selected item's details on the right, a draggable divider between
them. This tutor app itself uses a `SplitPane` with four children for
the same reason.

## Challenge

Move the divider position from `0.35` to `0.5` for an even split.
