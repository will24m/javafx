---
id: 067-scheduled-service
tier: events
order: 67
title: "ScheduledService"
objectives:
  - "Run a task on a repeating schedule with ScheduledService"
  - "Configure delay, period, and restart-on-failure"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      ScheduledService<String> ticker = new ScheduledService<>() {
          @Override protected Task<String> createTask() {
              return new Task<>() {
                  @Override protected String call() {
                      return java.time.LocalTime.now().withNano(0).toString();
                  }
              };
          }
      };
      ticker.setPeriod(Duration.seconds(1));
      Label clock = new Label("--:--:--");
      ticker.setOnSucceeded(e -> clock.setText(ticker.getValue()));
      ticker.start();
      return new StackPane(clock);
  }
challenges:
  - id: c1
    description: "Add a Toggle button that starts/stops the ticker"
    assertion: containsNodeOfType(Label)
nextLesson: 068-completable-future
---

# ScheduledService

`ScheduledService<V>` extends `Service` and automatically reschedules
itself at a fixed period.

## Configuration

```java
service.setDelay(Duration.seconds(2));   // wait before first run
service.setPeriod(Duration.seconds(5));  // wait between runs
service.setRestartOnFailure(true);       // retry even after failure
service.setMaximumFailureCount(3);       // give up after N failures
service.setBackoffStrategy(ScheduledService.EXPONENTIAL_BACKOFF_STRATEGY);
```

## Stopping

`service.cancel()` stops the service. `service.restart()` resumes.

## Challenge

Add a `ToggleButton("Running")` that calls `ticker.start()` when
selected and `ticker.cancel()` when deselected.
