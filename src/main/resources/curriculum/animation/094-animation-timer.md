---
id: 094-animation-timer
tier: animation
order: 94
title: "AnimationTimer"
objectives:
  - "Run custom code on every frame with AnimationTimer"
  - "Use the nanosecond timestamp to compute delta time"
estimatedMinutes: 12
starterSnippet: |
  public static Parent build() {
      javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(300, 200);
      javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();

      double[] x = {0};
      double[] dx = {2};

      javafx.animation.AnimationTimer timer = new javafx.animation.AnimationTimer() {
          @Override
          public void handle(long now) {
              gc.clearRect(0, 0, 300, 200);
              gc.setFill(Color.STEELBLUE);
              gc.fillOval(x[0], 80, 40, 40);
              x[0] += dx[0];
              if (x[0] > 260 || x[0] < 0) dx[0] = -dx[0];
          }
      };
      timer.start();

      return new StackPane(canvas);
  }
challenges:
  - id: c1
    description: "Change the fill color to Color.TOMATO"
    assertion: containsNodeOfType(javafx.scene.canvas.Canvas)
nextLesson: 095-media-player
---

# AnimationTimer

`AnimationTimer` calls your `handle(long now)` method **every frame**
(up to 60 times per second). Unlike `Timeline`, it gives you raw
nanosecond timestamps and full control over drawing.

```java
AnimationTimer timer = new AnimationTimer() {
    long last = 0;
    @Override
    public void handle(long now) {
        double deltaSeconds = (now - last) / 1_000_000_000.0;
        last = now;
        // update state proportional to deltaSeconds
    }
};
timer.start();
```

## Always stop when done

Call `timer.stop()` when the view is destroyed. An `AnimationTimer` that
holds a reference to a node keeps the scene graph alive even after the
node is removed.

## Use cases

- Game loops
- Real-time data charts
- Canvas-based custom rendering
- Particle systems

## Canvas vs. nodes

`AnimationTimer` + `Canvas` is faster than animating hundreds of
individual `Node` objects because Canvas bypasses the scene graph.

## Challenge

Change `gc.setFill(Color.STEELBLUE)` to `Color.TOMATO`.
