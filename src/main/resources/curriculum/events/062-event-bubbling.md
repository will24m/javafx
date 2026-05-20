---
id: 062-event-bubbling
tier: events
order: 62
title: "Event Bubbling"
objectives:
  - "Trace how an event bubbles up from target to root"
  - "Use event source vs. event target"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      Label log = new Label("Click anywhere");
      Button btn = new Button("Button");
      VBox inner = new VBox(btn);
      StackPane outer = new StackPane(inner);

      // One handler on the outer StackPane catches all clicks below it
      outer.setOnMouseClicked(e -> log.setText(
          "target=" + e.getTarget().getClass().getSimpleName()
          + " source=" + e.getSource().getClass().getSimpleName()));

      return new VBox(8, outer, log);
  }
challenges:
  - id: c1
    description: "Add a second handler on the Button that logs 'button handled' before bubbling"
    assertion: containsNodeOfType(Button)
nextLesson: 063-keyboard-events
---

# Event Bubbling

When an event fires on a node, it *bubbles* up through the scene graph
toward the root. Each ancestor gets a chance to handle it.

## source vs. target

- `event.getTarget()` — the node the event was originally fired on
- `event.getSource()` — the node whose handler is currently executing

As the event bubbles, `source` changes (each handler's containing node),
but `target` stays the same.

## Practical use

A single handler on a container can react to events from all its
descendants without attaching individual handlers to each.

## Challenge

Add `btn.setOnMouseClicked(e -> log.setText("button handled"))`.
Note that both the button's handler *and* the outer StackPane's handler
fire (bubbling is not consumed).
