---
id: 061-event-filters-handlers
tier: events
order: 61
title: "Event Filters and Handlers"
objectives:
  - "Understand the difference between event filters and event handlers"
  - "Add a filter on a parent to intercept events before children see them"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      Label log = new Label("Events:");
      Button inner = new Button("Click me");
      VBox outer = new VBox(10, inner);

      outer.addEventFilter(MouseEvent.MOUSE_CLICKED, e ->
          log.setText(log.getText() + "\n[filter] VBox"));
      inner.addEventHandler(MouseEvent.MOUSE_CLICKED, e ->
          log.setText(log.getText() + "\n[handler] Button"));

      return new VBox(8, outer, log);
  }
challenges:
  - id: c1
    description: "Add e.consume() in the filter to prevent the button's handler from firing"
    assertion: containsNodeOfType(Button)
nextLesson: 062-event-bubbling
---

# Event Filters and Handlers

JavaFX events travel in two phases:

1. **Capture** (root → target) — filters run here
2. **Bubble** (target → root) — handlers run here

## Filters

Added with `addEventFilter(type, handler)`. Called during capture,
before the target sees the event.

Use filters when a parent needs to intercept (or cancel) an event
before children handle it.

## Handlers

Added with `addEventHandler(type, handler)` or convenience methods
like `setOnMouseClicked`. Called during the bubble phase.

## Consuming

`e.consume()` stops propagation. No further filters or handlers on
the current node or ancestors will fire.

## Challenge

Add `e.consume()` inside the `outer` filter to stop the event from
reaching the button's handler.
