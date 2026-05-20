---
id: 070-virtual-threads
tier: events
order: 70
title: "Virtual Threads (Java 21)"
objectives:
  - "Launch a virtual thread with Thread.ofVirtual()"
  - "Use virtual threads for blocking I/O without pool exhaustion"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      Label result = new Label("Idle");
      Button go = new Button("Fetch (virtual thread)");
      go.setOnAction(e -> {
          Thread.ofVirtual().name("vt-fetch").start(() -> {
              // Blocking I/O is fine on a virtual thread
              try { Thread.sleep(800); } catch (InterruptedException ex) {}
              String data = "Data from virtual thread";
              Platform.runLater(() -> result.setText(data));
          });
      });
      return new VBox(10, go, result);
  }
challenges:
  - id: c1
    description: "Print the thread name inside the virtual thread to confirm it is a VirtualThread"
    assertion: containsNodeOfType(Button)
nextLesson: 071-executor-service
---

# Virtual Threads (Java 21)

Virtual threads are lightweight threads managed by the JVM. You can
create millions of them without exhausting OS resources.

## Key properties

- Blocking operations (sleep, I/O) unmount the virtual thread from
  its carrier platform thread — carrier is freed to do other work
- Ideal for tasks that spend most time waiting (HTTP, DB, files)
- Not faster for CPU-bound work — use platform threads there

## Launching

```java
Thread.ofVirtual().start(runnable);
Thread.ofVirtual().name("name").start(runnable);
```

Or use an executor:

```java
ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor();
exec.submit(() -> { ... });
```

## FX thread rule still applies

Virtual threads are still *not* the FX thread. Always use
`Platform.runLater()` to update the UI.

## Challenge

Inside the virtual thread, print
`System.out.println(Thread.currentThread())` to confirm it is a
`VirtualThread`.
