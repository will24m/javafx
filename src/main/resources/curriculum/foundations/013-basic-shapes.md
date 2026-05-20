---
id: 013-basic-shapes
tier: foundations
order: 13
title: "Basic Shapes"
objectives:
  - "Draw Rectangle, Circle, and Line nodes"
  - "Set fill, stroke, and stroke width"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(100, 60);
      rect.setFill(Color.STEELBLUE);
      rect.setStroke(Color.WHITE);
      rect.setStrokeWidth(2);

      javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(30);
      circle.setFill(Color.TOMATO);

      return new HBox(20, rect, circle);
  }
challenges:
  - id: c1
    description: "Add a Line from (0,0) to (100,60) with a white stroke"
    assertion: containsNodeOfType(javafx.scene.shape.Line)
nextLesson: 014-colors-and-paint
---

# Basic Shapes

JavaFX ships with a set of shape nodes under `javafx.scene.shape`:
`Rectangle`, `Circle`, `Ellipse`, `Line`, `Polygon`, `Polyline`,
`Arc`, `Path`, and more.

## Common properties

| Property | Type | Effect |
|---|---|---|
| `fill` | `Paint` | Interior color |
| `stroke` | `Paint` | Border color |
| `strokeWidth` | `double` | Border thickness |

## Shape vs. Canvas

Shapes are scene-graph nodes — they respond to events, CSS, and
transformations. `Canvas` is a pixel buffer you draw into imperatively.
Use shapes when you need interaction; use `Canvas` for high-performance
custom drawing.

## Challenge

Add a `Line` from `(0, 0)` to `(100, 60)` with
`setStroke(Color.WHITE)`.
