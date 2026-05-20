---
id: 068-completable-future
tier: events
order: 68
title: "CompletableFuture with Platform.runLater"
objectives:
  - "Chain async operations with CompletableFuture"
  - "Bridge results back to the FX thread with runLater"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      Label result = new Label("Waiting...");
      Button fetch = new Button("Fetch");
      fetch.setOnAction(e -> {
          CompletableFuture
              .supplyAsync(() -> {
                  try { Thread.sleep(1000); } catch (InterruptedException ex) {}
                  return "Hello from background";
              })
              .thenAccept(val -> Platform.runLater(() -> result.setText(val)));
      });
      return new VBox(10, fetch, result);
  }
challenges:
  - id: c1
    description: "Chain .exceptionally() to show an error message if the background code throws"
    assertion: containsNodeOfType(Button)
nextLesson: 069-fx-thread-invariant
---

# CompletableFuture with Platform.runLater

`CompletableFuture` chains async steps elegantly. The key rule: any
step that touches the scene graph must be wrapped in
`Platform.runLater()`.

```java
CompletableFuture.supplyAsync(() -> fetchData())       // bg thread
    .thenApplyAsync(data -> process(data))              // bg thread
    .thenAccept(result ->
        Platform.runLater(() -> label.setText(result))); // FX thread
```

## Error handling

```java
.exceptionally(ex -> {
    Platform.runLater(() -> label.setText("Error: " + ex.getMessage()));
    return null;
});
```

## Challenge

Add `.exceptionally(ex -> { Platform.runLater(() -> result.setText("Error: " + ex.getMessage())); return null; })` to the chain.
