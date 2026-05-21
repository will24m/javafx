---
id: 091-timeline-keyframe
tier: animation
order: 91
title: "Timeline and KeyFrame"
objectives:
  - "Create a Timeline with KeyFrames to animate a property"
  - "Control playback with play(), pause(), stop()"
estimatedMinutes: 12
starterSnippet: |
  public static Parent build() {
      javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(60, 60, Color.STEELBLUE);
      rect.setArcWidth(8);
      rect.setArcHeight(8);

      javafx.animation.Timeline tl = new javafx.animation.Timeline(
          new javafx.animation.KeyFrame(javafx.util.Duration.ZERO,
              new javafx.animation.KeyValue(rect.translateXProperty(), 0)),
          new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1),
              new javafx.animation.KeyValue(rect.translateXProperty(), 200))
      );
      tl.setAutoReverse(true);
      tl.setCycleCount(javafx.animation.Animation.INDEFINITE);
      tl.play();

      Button toggle = new Button("Pause");
      toggle.setOnAction(e -> {
          if (tl.getStatus() == javafx.animation.Animation.Status.RUNNING) {
              tl.pause(); toggle.setText("Play");
          } else {
              tl.play(); toggle.setText("Pause");
          }
      });
      return new VBox(16, new StackPane(rect), toggle);
  }
challenges:
  - id: c1
    description: "Change the animation duration to 2 seconds"
    assertion: containsNodeOfType(javafx.scene.shape.Rectangle)
nextLesson: 092-transitions
---

# Timeline and KeyFrame

`Timeline` is JavaFX's general-purpose animation engine. It interpolates
properties between `KeyFrame` checkpoints over time.

## Structure

```java
Timeline tl = new Timeline(
    new KeyFrame(Duration.ZERO,
        new KeyValue(node.opacityProperty(), 0.0)),
    new KeyFrame(Duration.seconds(1),
        new KeyValue(node.opacityProperty(), 1.0))
);
```

- `KeyFrame` — a point in time with a set of target values
- `KeyValue` — one property + target value at that KeyFrame
- Multiple `KeyValue`s per `KeyFrame` animate several properties in sync

## Cycle control

| Method | Effect |
|---|---|
| `setCycleCount(n)` | Repeat n times; `INDEFINITE` = forever |
| `setAutoReverse(true)` | Reverse direction each cycle |
| `play()` / `pause()` / `stop()` | Playback control |

## Challenge

Change `Duration.seconds(1)` to `Duration.seconds(2)` to slow the
animation down.
