---
id: 066-service
tier: events
order: 66
title: "Service: Restartable Background Work"
objectives:
  - "Use Service to run the same Task multiple times"
  - "Restart, cancel, and handle failure with Service"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      Service<String> service = new Service<>() {
          @Override protected Task<String> createTask() {
              return new Task<>() {
                  @Override protected String call() throws Exception {
                      Thread.sleep(1500);
                      return "Done at " + java.time.LocalTime.now();
                  }
              };
          }
      };
      Label status = new Label("Idle");
      status.textProperty().bind(service.messageProperty());
      service.setOnSucceeded(e -> status.textProperty().unbind());
      service.setOnSucceeded(e -> status.setText(service.getValue()));

      Button run = new Button("Run");
      run.setOnAction(e -> service.restart());
      return new VBox(10, run, status);
  }
challenges:
  - id: c1
    description: "Add a Cancel button and bind the run button's disable state to service.runningProperty()"
    assertion: containsNodeOfType(Button)
nextLesson: 067-scheduled-service
---

# Service: Restartable Background Work

`Service<V>` manages the lifecycle of a `Task`. Unlike a raw `Task`,
a `Service` can be restarted repeatedly — each call to `restart()`
or `start()` creates a new `Task` via `createTask()`.

## States

`READY → SCHEDULED → RUNNING → SUCCEEDED / FAILED / CANCELLED`

Calling `restart()` from any state transitions back through this cycle.

## Disabling UI while running

```java
runButton.disableProperty().bind(service.runningProperty());
```

## Error handling

```java
service.setOnFailed(e -> {
    Throwable ex = service.getException();
    showAlert(ex.getMessage());
});
```

## Challenge

Add `run.disableProperty().bind(service.runningProperty())` and a
`Button("Cancel")` that calls `service.cancel()`.
