---
id: 093-interpolators
tier: animation
order: 93
title: "Interpolators"
objectives:
  - "Understand linear vs. easing interpolation"
  - "Apply Interpolator.EASE_BOTH and a custom spline"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      javafx.scene.shape.Rectangle r = new javafx.scene.shape.Rectangle(50, 50, Color.STEELBLUE);

      javafx.animation.TranslateTransition tt =
          new javafx.animation.TranslateTransition(javafx.util.Duration.seconds(1.5), r);
      tt.setFromX(0);
      tt.setToX(250);
      tt.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);
      tt.setAutoReverse(true);
      tt.setCycleCount(javafx.animation.Animation.INDEFINITE);
      tt.play();

      return new StackPane(r);
  }
challenges:
  - id: c1
    description: "Change the interpolator to Interpolator.LINEAR and observe the difference"
    assertion: containsNodeOfType(javafx.scene.shape.Rectangle)
nextLesson: 094-animation-timer
---

# Interpolators

An `Interpolator` controls how a property value moves between its start
and end values over the duration of a `KeyFrame` interval.

## Built-in interpolators

| Constant | Curve shape |
|---|---|
| `Interpolator.LINEAR` | Constant speed |
| `Interpolator.EASE_IN` | Slow start, fast end |
| `Interpolator.EASE_OUT` | Fast start, slow end |
| `Interpolator.EASE_BOTH` | Slow start and end (most natural) |
| `Interpolator.DISCRETE` | Jump at the end (no gradual change) |

## Custom spline

```java
Interpolator.SPLINE(0.25, 0.1, 0.25, 1.0)
// CSS cubic-bezier equivalent
```

## Applying to a KeyValue

```java
new KeyValue(rect.translateXProperty(), 200, Interpolator.EASE_BOTH)
```

## Challenge

Change `EASE_BOTH` to `Interpolator.LINEAR` and observe how the motion
feels different — no longer eases at the ends.
