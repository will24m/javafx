---
id: 018-borderpane
tier: layouts
order: 18
title: "BorderPane"
objectives:
  - "Place nodes in the five regions of a BorderPane"
  - "Understand how center expands to fill remaining space"
  - "Recognise the BorderPane shape in real desktop apps (including this one)"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      Label top = new Label("  ☰  File   Edit   View   Help");
      top.setStyle("-fx-background-color:#2b2d30; -fx-text-fill:#dbe4f5; -fx-padding:6 8 6 8;");
      top.setMaxWidth(Double.MAX_VALUE);

      Label left = new Label("Sidebar");
      left.setStyle("-fx-background-color:#1e1f22; -fx-text-fill:#c8c9cc; -fx-padding:12;");
      left.setMaxHeight(Double.MAX_VALUE);

      Label right = new Label("Inspector");
      right.setStyle("-fx-background-color:#1e1f22; -fx-text-fill:#c8c9cc; -fx-padding:12;");
      right.setMaxHeight(Double.MAX_VALUE);

      Label bottom = new Label("Ready  ·  0 errors");
      bottom.setStyle("-fx-background-color:#2b2d30; -fx-text-fill:#9aa0a6; -fx-padding:4 8 4 8;");
      bottom.setMaxWidth(Double.MAX_VALUE);

      Label center = new Label("Main editor area");
      center.setStyle("-fx-background-color:#3a3c40; -fx-text-fill:white; -fx-padding:24;");
      center.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

      BorderPane bp = new BorderPane(center, top, right, bottom, left);
      bp.setPrefSize(420, 240);
      return bp;
  }
challenges:
  - id: c1
    description: "Remove the right region so the center expands further right"
    assertion: 'containsLabeledWithText(text="Main editor area")'
nextLesson: 019-gridpane
---

# BorderPane

`BorderPane` divides its space into five named regions:

```
┌───────────────── TOP ─────────────────┐
│        │                    │         │
│  LEFT  │      CENTER        │  RIGHT  │
│        │                    │         │
└──────────────── BOTTOM ───────────────┘
```

- `TOP` and `BOTTOM` get their preferred height across the full width
- `LEFT` and `RIGHT` get their preferred width across whatever vertical
  space remains
- `CENTER` fills everything that's left over

Any region can be `null` (empty) and the layout adjusts accordingly.

## API

```java
bp.setTop(node);     bp.setBottom(node);
bp.setLeft(node);    bp.setRight(node);
bp.setCenter(node);
```

Or use the five-argument constructor (center, top, right, bottom, left)
— note the unusual argument order, which matches CSS shorthand.

## Why BorderPane

It maps directly onto the classic desktop application skeleton: menu
bar on top, status bar on bottom, navigation on the left, inspector on
the right, content in the middle.

**This tutor app uses a BorderPane** as its root layout. Open
`MainView.java` — you'll see exactly this structure with a `SplitPane`
as the center child.

## Make a region fill its slot

A label only takes its preferred size by default. To make it stretch:

```java
node.setMaxWidth(Double.MAX_VALUE);   // for top/bottom rows
node.setMaxHeight(Double.MAX_VALUE);  // for left/right columns
```

That's why the starter snippet sets `maxWidth` / `maxHeight` — without
it, the colored background only paints behind the text, not the whole
strip.

## Challenge

Remove the right region (`bp.setRight(null)`) and observe how the
center label expands to fill that space.
