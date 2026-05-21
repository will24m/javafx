---
id: 004-fx-thread
tier: foundations
order: 4
title: "The JavaFX Application Thread"
objectives:
  - "Understand why JavaFX has a single UI thread"
  - "Use Platform.runLater() to safely update the UI from a background thread"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      Label status = new Label("Waiting...");
      Button start = new Button("Start background task");
      start.setOnAction(e -> {
          new Thread(() -> {
              try { Thread.sleep(1000); } catch (InterruptedException ex) {}
              // TODO: update status safely
              status.setText("Done!");
          }).start();
      });
      return new VBox(10, status, start);
  }
challenges:
  - id: c1
    description: "Wrap the status.setText() call in Platform.runLater() so it runs on the FX thread"
    assertion: containsNodeOfType(Label)
nextLesson: 005-labels-and-buttons
---

# The JavaFX Application Thread

JavaFX uses a **single thread** — the FX Application Thread — for all
scene-graph mutations. Reading or writing a node property from any other
thread is undefined behavior: you may see no effect, a flicker, or a crash.

## Why one thread?

Layout, CSS, and rendering all happen in lock-step on the FX thread.
A single thread eliminates the need for locks on every property, which
would be both slow and error-prone.

## The danger

Click "Start background task" in the snippet. You will see the label
update — but `setText` is being called from a raw `Thread`. This is a
*data race*. On a fast machine it often appears to work; under load it
can corrupt the scene graph silently.

## The fix: `Platform.runLater()`

```java
Platform.runLater(() -> status.setText("Done!"));
```

This queues the lambda onto the FX thread. It returns immediately so
your background thread is not blocked.

## Challenge

Fix the snippet: wrap the `status.setText("Done!")` call inside
`Platform.runLater(() -> { ... })`.
