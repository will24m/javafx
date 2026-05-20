---
id: 018-borderpane
tier: layouts
order: 18
title: "BorderPane"
objectives:
  - "Place nodes in the five regions of a BorderPane"
  - "Understand how center expands to fill remaining space"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      BorderPane bp = new BorderPane();
      bp.setTop(new Label("TOP NAV"));
      bp.setBottom(new Label("STATUS BAR"));
      bp.setLeft(new Label("NAV"));
      bp.setCenter(new Label("Content area"));
      return bp;
  }
challenges:
  - id: c1
    description: "Add a Label('INSPECTOR') to the right region"
    assertion: containsLabeledWithText(text="INSPECTOR")
nextLesson: 019-gridpane
---

# BorderPane

`BorderPane` divides space into five named regions:

```
┌────────────── TOP ───────────────┐
│ LEFT │       CENTER       │ RIGHT│
└────────────── BOTTOM ────────────┘
```

`CENTER` expands to fill all remaining space after `TOP`, `BOTTOM`,
`LEFT`, and `RIGHT` have taken their preferred sizes.

## API

```java
bp.setTop(node);    bp.setBottom(node);
bp.setLeft(node);   bp.setRight(node);
bp.setCenter(node);
```

Any region can be `null` (empty).

## Why BorderPane?

It maps naturally to the classic desktop application layout: menu bar
on top, status bar on bottom, navigation on the left, inspector on the
right, main content in the centre. That is exactly how this tutor app
is built.

## Challenge

Set the right region to `new Label("INSPECTOR")`.
