---
id: 077-css-hot-reload
tier: css
order: 77
title: "CSS Hot-Reload"
objectives:
  - "Reload a stylesheet at runtime without restarting"
  - "Use a file watcher to trigger reload automatically"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      Label demo = new Label("CSS hot-reload demo");
      demo.getStyleClass().add("hot-label");
      Button reload = new Button("Reload CSS");
      // In a real app you'd watch the file and trigger this automatically
      reload.setOnAction(e -> {
          var sheets = demo.getScene() != null
              ? demo.getScene().getStylesheets() : demo.getStylesheets();
          String url = sheets.isEmpty() ? null : sheets.get(0);
          if (url != null) {
              sheets.clear();
              sheets.add(url);
          }
      });
      return new VBox(10, demo, reload);
  }
challenges:
  - id: c1
    description: "Add a stylesheet string and demonstrate the reload button clears and re-adds it"
    assertion: containsNodeOfType(Button)
nextLesson: 078-modena-override
---

# CSS Hot-Reload

JavaFX caches stylesheet content. To force a reload you must:

1. Remove the stylesheet URL from `getStylesheets()`
2. Add it back

```java
ObservableList<String> sheets = scene.getStylesheets();
String url = sheets.get(0);
sheets.clear();
sheets.add(url);
```

## File watcher pattern

Use `WatchService` on a background thread to detect file changes, then
`Platform.runLater()` to trigger the reload above. This gives
**sub-second CSS iteration** without touching the running app.

## Challenge

Add a `demo.getStylesheets().add(cssUrl)` call on startup and verify
the `reload` button actually clears and re-adds it.
