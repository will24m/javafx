---
id: 096-canvas-animation
tier: animation
order: 96
title: "Canvas Drawing"
objectives:
  - "Draw shapes, paths, and text on a Canvas with GraphicsContext"
  - "Clear and redraw each frame for animation"
estimatedMinutes: 12
starterSnippet: |
  public static Parent build() {
      javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(320, 200);
      javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();

      // Draw a simple bar chart
      String[] labels = {"Mon", "Tue", "Wed", "Thu", "Fri"};
      double[] values = {60, 90, 45, 120, 80};
      Color[] colors = {Color.STEELBLUE, Color.TOMATO, Color.LIMEGREEN, Color.GOLD, Color.MEDIUMPURPLE};

      gc.setFill(Color.web("#2b2d30"));
      gc.fillRect(0, 0, 320, 200);

      for (int i = 0; i < values.length; i++) {
          gc.setFill(colors[i]);
          double barH = values[i];
          gc.fillRect(20 + i * 58, 180 - barH, 40, barH);
          gc.setFill(Color.WHITE);
          gc.fillText(labels[i], 24 + i * 58, 195);
      }
      return new StackPane(canvas);
  }
challenges:
  - id: c1
    description: "Add a sixth bar 'Sat' with value 100 and Color.CORAL"
    assertion: containsNodeOfType(javafx.scene.canvas.Canvas)
nextLesson: 097-jpackage-basics
---

# Canvas Drawing

`Canvas` is a blank pixel surface. You draw on it via `GraphicsContext`:

## Drawing primitives

```java
GraphicsContext gc = canvas.getGraphicsContext2D();

// Shapes
gc.setFill(Color.STEELBLUE);
gc.fillRect(x, y, w, h);
gc.fillOval(x, y, w, h);

// Strokes
gc.setStroke(Color.WHITE);
gc.setLineWidth(2);
gc.strokeLine(x1, y1, x2, y2);

// Text
gc.setFill(Color.WHITE);
gc.setFont(Font.font(14));
gc.fillText("Hello", x, y);

// Paths
gc.beginPath();
gc.moveTo(0, 0);
gc.lineTo(100, 50);
gc.closePath();
gc.fill();
```

## Clearing and redrawing

```java
gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
```

Call this at the start of each frame in an `AnimationTimer` to prevent
ghost images from the previous frame.

## Challenge

Add a sixth bar `"Sat"` at index 5 with `values[5] = 100` and
`Color.CORAL`.
