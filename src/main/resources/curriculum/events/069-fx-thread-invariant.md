---
id: 069-fx-thread-invariant
tier: events
order: 69
title: "FX Thread Invariant"
objectives:
  - "Assert correct thread usage with Platform.isFxApplicationThread()"
  - "Understand why thread violations are hard to detect"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      Label threadLabel = new Label("Thread: " + Thread.currentThread().getName());
      Label isFX = new Label("isFxThread: " + Platform.isFxApplicationThread());

      Button bgButton = new Button("Check from bg thread");
      bgButton.setOnAction(e -> new Thread(() -> {
          boolean fx = Platform.isFxApplicationThread();
          Platform.runLater(() ->
              isFX.setText("isFxThread from bg: " + fx + " (should be false)"));
      }).start());

      return new VBox(10, threadLabel, isFX, bgButton);
  }
challenges:
  - id: c1
    description: "Add an assertion that throws if called off the FX thread"
    assertion: containsNodeOfType(Label)
nextLesson: 070-virtual-threads
---

# FX Thread Invariant

The single most important rule in JavaFX: **only touch the scene graph
from the FX Application Thread**.

## Detecting violations

`Platform.isFxApplicationThread()` returns `true` only on the FX
thread. Use it for assertions:

```java
if (!Platform.isFxApplicationThread()) {
    throw new IllegalStateException("Not on FX thread!");
}
```

## Why violations are subtle

JavaFX does not throw on every cross-thread access. Many mutations
silently corrupt internal state, causing crashes or visual glitches
that appear seconds later with no obvious cause.

## Challenge

Add `assert Platform.isFxApplicationThread() : "Off FX thread!"` at
the top of `build()`. (Assertions are disabled by default — add `-ea`
to JVM args to enable them.)
