---
id: 025-region-layout-override
tier: layouts
order: 25
title: "Custom Layout with Region"
objectives:
  - "Override layoutChildren() to implement a layout no built-in pane offers"
  - "Override computePrefWidth/Height to report size hints to the parent"
  - "Know when a custom Region is the right tool"
estimatedMinutes: 12
starterSnippet: |
  public static Parent build() {
      // A custom proportional split: left child takes 1/3, right takes 2/3.
      // Works at any width because we compute positions every layout pass.
      class TwoThirds extends javafx.scene.layout.Region {
          final Label left = new Label("1/3");
          final Label right = new Label("2/3");
          TwoThirds() {
              left.setStyle("-fx-background-color:#2d4a8a; -fx-text-fill:white;"
                          + " -fx-alignment:center; -fx-padding:24;");
              right.setStyle("-fx-background-color:#6dbe6d; -fx-text-fill:white;"
                          + " -fx-alignment:center; -fx-padding:24;");
              left.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
              right.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
              getChildren().addAll(left, right);
          }
          @Override protected double computePrefWidth(double height) { return 360; }
          @Override protected double computePrefHeight(double width)  { return 120; }
          @Override protected void layoutChildren() {
              double w = getWidth(), h = getHeight();
              left.resizeRelocate(0,      0, w / 3.0,     h);
              right.resizeRelocate(w / 3, 0, w * 2 / 3.0, h);
          }
      }
      return new TwoThirds();
  }
challenges:
  - id: c1
    description: "Flip the split — make left take 2/3 and right take 1/3"
    assertion: 'containsNodeOfType(javafx.scene.layout.Region)'
nextLesson: 026-tab-pane
---

# Custom Layout with Region

When no built-in pane fits, extend `Region` (or `Pane`) and override
`layoutChildren()` for full control over child placement.

## The contract

Inside `layoutChildren()` your only job is to size and position each
child by calling:

```java
child.resizeRelocate(x, y, width, height);
```

(Equivalent to `child.relocate(x, y)` + `child.resize(width, height)`.)
JavaFX runs `layoutChildren()` once per layout pulse for any node that
was marked dirty — which happens automatically whenever the region's
size changes.

## Size hints

The parent layout asks your region "how big do you want to be?"
Override these to answer:

```java
@Override protected double computePrefWidth(double height)  { return 360; }
@Override protected double computePrefHeight(double width)  { return 120; }
@Override protected double computeMinWidth(double height)   { return 100; }
@Override protected double computeMaxHeight(double width)   { return Double.MAX_VALUE; }
```

`computePrefWidth` gets called with a known height (and vice versa) so
you can size aspect-ratio-aware layouts that need to know one dimension
to compute the other.

## Pane vs Region

- **`Region`** has *protected* access to `getChildren()` — children are
  hidden from the outside world. Use this for self-contained layouts.
- **`Pane`** exposes `getChildren()` publicly, like `VBox` or `HBox`.
  Use this when callers should add their own children.

## When to use a custom layout

| Need | Pick |
|---|---|
| Aspect-ratio-locked tiles | custom `Region` |
| Children positioned by data values (charts, timelines) | custom `Pane` |
| Proportional splits not expressible in `GridPane` | custom `Region` |
| Animated layouts where positions interpolate over time | custom `Pane` + `Timeline` |
| Reusable layout component for a UI library | custom `Region` |

## A word on layoutChildren()

It runs *every* pulse the region is dirty. Keep it fast — no
allocations, no expensive math. If you need to do work once, cache it
and invalidate the cache from a property listener.

## Challenge

In `layoutChildren()`, swap the two children's proportions:

```java
left.resizeRelocate(0,        0, w * 2 / 3.0, h);
right.resizeRelocate(w * 2/3, 0, w / 3.0,     h);
```

Update the labels' text to match (`"2/3"` and `"1/3"`).
