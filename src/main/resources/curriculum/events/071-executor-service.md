---
id: 071-executor-service
tier: events
order: 71
title: "ExecutorService Patterns"
objectives:
  - "Use a bounded thread pool for concurrent work"
  - "Shut down an executor cleanly on app exit"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      ExecutorService pool = Executors.newFixedThreadPool(3);
      Label log = new Label("Submitted: 0");
      Button submit = new Button("Submit task");
      int[] count = {0};
      submit.setOnAction(e -> {
          int n = ++count[0];
          pool.submit(() -> {
              try { Thread.sleep(2000); } catch (InterruptedException ex) {}
              Platform.runLater(() -> log.setText("Task " + n + " done"));
          });
          log.setText("Submitted: " + n);
      });
      return new VBox(10, submit, log);
  }
challenges:
  - id: c1
    description: "Switch to Executors.newCachedThreadPool() and observe the difference"
    assertion: containsNodeOfType(Button)
nextLesson: 072-event-driven-architecture
---

# ExecutorService Patterns

## Pool types

| Factory | Behaviour |
|---|---|
| `newFixedThreadPool(n)` | At most n concurrent tasks; extras queued |
| `newCachedThreadPool()` | Grows as needed; idle threads recycled |
| `newSingleThreadExecutor()` | One thread; tasks serialized in order |
| `newVirtualThreadPerTaskExecutor()` | One virtual thread per task |

## Shutdown

Always shut down executors when the app closes, or background threads
will prevent JVM exit:

```java
pool.shutdownNow();
// Or in Application.stop():
pool.shutdown();
pool.awaitTermination(5, TimeUnit.SECONDS);
```

## Challenge

Replace `newFixedThreadPool(3)` with `Executors.newCachedThreadPool()`
and submit several tasks quickly to observe the pool growing.
