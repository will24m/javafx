---
id: 065-task
tier: events
order: 65
title: "Task: Background Work"
objectives:
  - "Run long work on a background thread with Task"
  - "Update progress and message properties safely"
estimatedMinutes: 12
starterSnippet: |
  public static Parent build() {
      ProgressBar bar = new ProgressBar(0);
      Label message = new Label("Ready");
      Button start = new Button("Start");

      start.setOnAction(e -> {
          Task<Void> task = new Task<>() {
              @Override protected Void call() throws Exception {
                  for (int i = 0; i <= 100; i++) {
                      Thread.sleep(30);
                      updateProgress(i, 100);
                      updateMessage("Step " + i + " of 100");
                  }
                  return null;
              }
          };
          bar.progressProperty().bind(task.progressProperty());
          message.textProperty().bind(task.messageProperty());
          task.setOnSucceeded(ev -> message.textProperty().unbind());
          new Thread(task, "worker").start();
      });
      return new VBox(10, start, bar, message);
  }
challenges:
  - id: c1
    description: "Add a Cancel button that calls task.cancel() and shows 'Cancelled' in the label"
    assertion: containsNodeOfType(ProgressBar)
nextLesson: 066-service
---

# Task: Background Work

`Task<V>` extends `FutureTask` and adds JavaFX-safe observable
properties for progress, message, and state.

## Key methods (call inside call())

```java
updateProgress(done, total);   // sets progressProperty (0.0–1.0)
updateMessage("text");         // sets messageProperty
updateValue(v);                // sets valueProperty (partial results)
if (isCancelled()) return null; // check periodically for cancellation
```

## Thread-safety guarantee

`updateProgress`, `updateMessage`, and `updateValue` internally
dispatch to the FX thread — you can call them from your bg code.

## Lifecycle

`task.setOnSucceeded(e -> ...)`, `setOnFailed`, `setOnCancelled`.

## Challenge

Capture the `task` in a field accessible from both the start and cancel
buttons. Add `Button("Cancel")` that calls `task.cancel()`.
