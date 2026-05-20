---
id: 024-growth-priorities
tier: layouts
order: 24
title: "Growth Priorities"
objectives:
  - "Make a specific child grow to fill available space with Priority.ALWAYS"
  - "Understand Priority.NEVER and Priority.SOMETIMES"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      Button left = new Button("Fixed");
      TextField center = new TextField();
      VBox.setVgrow(center, Priority.ALWAYS);
      HBox.setHgrow(center, Priority.ALWAYS);
      Button right = new Button("Fixed");

      HBox row = new HBox(8, left, center, right);
      row.setPrefHeight(40);
      return row;
  }
challenges:
  - id: c1
    description: "Change the left button to also grow with Priority.ALWAYS"
    assertion: containsNodeOfType(TextField)
nextLesson: 025-region-layout-override
---

# Growth Priorities

When a `VBox` or `HBox` has extra space, it distributes it among
children that have growth priorities:

| Priority | Effect |
|---|---|
| `ALWAYS` | Node always takes extra space |
| `SOMETIMES` | Node takes space only if no ALWAYS nodes exist |
| `NEVER` | Node never grows beyond its preferred size |

## Setting priorities

```java
HBox.setHgrow(node, Priority.ALWAYS);  // grow horizontally in HBox
VBox.setVgrow(node, Priority.ALWAYS);  // grow vertically in VBox
```

The snippet shows the classic search-bar layout: two fixed buttons
flanking a `TextField` that fills the middle.

## Challenge

Give the `left` button `Priority.ALWAYS` so it also grows.
