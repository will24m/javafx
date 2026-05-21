---
id: 092-transitions
tier: animation
order: 92
title: "Built-in Transitions"
objectives:
  - "Apply FadeTransition, ScaleTransition, and RotateTransition"
  - "Combine transitions with ParallelTransition and SequentialTransition"
estimatedMinutes: 12
starterSnippet: |
  public static Parent build() {
      javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(40, Color.TOMATO);

      javafx.animation.FadeTransition fade =
          new javafx.animation.FadeTransition(javafx.util.Duration.seconds(1), circle);
      fade.setFromValue(1.0);
      fade.setToValue(0.2);
      fade.setAutoReverse(true);
      fade.setCycleCount(javafx.animation.Animation.INDEFINITE);

      javafx.animation.ScaleTransition scale =
          new javafx.animation.ScaleTransition(javafx.util.Duration.seconds(1), circle);
      scale.setFromX(1.0); scale.setFromY(1.0);
      scale.setToX(1.5);   scale.setToY(1.5);
      scale.setAutoReverse(true);
      scale.setCycleCount(javafx.animation.Animation.INDEFINITE);

      javafx.animation.ParallelTransition pt =
          new javafx.animation.ParallelTransition(fade, scale);
      pt.play();

      return new StackPane(circle);
  }
challenges:
  - id: c1
    description: "Add a RotateTransition(2s, circle) rotating from 0 to 360 and add it to the ParallelTransition"
    assertion: containsNodeOfType(javafx.scene.shape.Circle)
nextLesson: 093-interpolators
---

# Built-in Transitions

JavaFX ships ready-made transitions for the most common animations:

| Class | Animates |
|---|---|
| `FadeTransition` | `opacity` |
| `ScaleTransition` | `scaleX` / `scaleY` |
| `RotateTransition` | `rotate` |
| `TranslateTransition` | `translateX` / `translateY` |
| `FillTransition` | `fill` (Shapes only) |
| `StrokeTransition` | `stroke` |
| `PathTransition` | moves a node along a `Shape` |

## Composing transitions

```java
// Run together:
new ParallelTransition(fade, scale, rotate).play();

// Run one after another:
new SequentialTransition(fadeIn, pause, fadeOut).play();
```

## Challenge

Add a `RotateTransition(Duration.seconds(2), circle)` from `0` to
`360`, set `cycleCount(INDEFINITE)`, and include it in the
`ParallelTransition`.
