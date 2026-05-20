---
id: 064-drag-and-drop
tier: events
order: 64
title: "Drag and Drop"
objectives:
  - "Implement a basic drag-and-drop gesture between two nodes"
  - "Use Dragboard to transfer data"
estimatedMinutes: 12
starterSnippet: |
  public static Parent build() {
      Label source = new Label("Drag me!");
      source.setStyle("-fx-border-color: steelblue; -fx-padding: 8;");
      source.setOnDragDetected(e -> {
          Dragboard db = source.startDragAndDrop(TransferMode.COPY);
          ClipboardContent content = new ClipboardContent();
          content.putString(source.getText());
          db.setContent(content);
          e.consume();
      });

      Label target = new Label("Drop here");
      target.setStyle("-fx-border-color: tomato; -fx-padding: 8;");
      target.setOnDragOver(e -> {
          if (e.getDragboard().hasString()) e.acceptTransferModes(TransferMode.COPY);
          e.consume();
      });
      target.setOnDragDropped(e -> {
          target.setText("Got: " + e.getDragboard().getString());
          e.setDropCompleted(true);
          e.consume();
      });
      return new HBox(20, source, target);
  }
challenges:
  - id: c1
    description: "Change the source to transfer an image instead of a string"
    assertion: containsNodeOfType(Label)
nextLesson: 065-task
---

# Drag and Drop

JavaFX drag-and-drop uses a `Dragboard` to transfer data. You need
four event handlers:

| Event | Node | Purpose |
|---|---|---|
| `DRAG_DETECTED` | source | start gesture, put data in `Dragboard` |
| `DRAG_OVER` | target | accept transfer modes |
| `DRAG_DROPPED` | target | read data, call `setDropCompleted(true)` |
| `DRAG_DONE` | source | react to completion (optional) |

## TransferMode

`COPY` — data is copied. `MOVE` — data is moved (source should
delete). `LINK` — platform-specific linking.

## Challenge

In `setOnDragDetected`, use `content.putImage(...)` instead of
`content.putString(...)` and adjust the drop handler to read
`getDragboard().getImage()`.
