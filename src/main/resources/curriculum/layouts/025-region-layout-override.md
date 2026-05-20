---
id: 025-region-layout-override
tier: layouts
order: 25
title: "Custom Layout with Region"
objectives:
  - "Override layoutChildren() to implement a custom layout"
  - "Use computePrefWidth/Height to report size hints"
estimatedMinutes: 12
starterSnippet: |
  public static Parent build() {
      // A simple custom layout that places two children side-by-side
      // with the first child taking exactly half the width.
      javafx.scene.layout.Region custom = new javafx.scene.layout.Region() {
          final Label left = new Label("Left half");
          final Label right = new Label("Right half");
          { getChildren().addAll(left, right); }

          @Override protected void layoutChildren() {
              double w = getWidth(), h = getHeight();
              left.resizeRelocate(0, 0, w / 2, h);
              right.resizeRelocate(w / 2, 0, w / 2, h);
          }
      };
      custom.setPrefSize(300, 60);
      return custom;
  }
challenges:
  - id: c1
    description: "Change the split to 1/3 left, 2/3 right"
    assertion: containsNodeOfType(javafx.scene.layout.Region)
nextLesson: 026-tab-pane
---

# Custom Layout with Region

Sometimes no built-in pane fits. Extending `Region` and overriding
`layoutChildren()` gives you full control.

## The contract

Inside `layoutChildren()` your job is to call
`child.resizeRelocate(x, y, width, height)` for every child.
JavaFX will then do a single layout pass for that pulse.

## Size hints

Override `computePrefWidth(height)` and `computePrefHeight(width)` to
tell the parent container how big you want to be.

## When to use this

- Complex proportional layouts (e.g. dashboard tiles that must maintain
  aspect ratios)
- Layouts that depend on live measurements unavailable at construction time
- Reusable layout components you want to expose as a library

## Challenge

Change the split so the left half is `w / 3` and the right half is
`w * 2 / 3`.
