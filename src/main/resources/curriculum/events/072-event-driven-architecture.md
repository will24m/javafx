---
id: 072-event-driven-architecture
tier: events
order: 72
title: "Event-Driven Architecture"
objectives:
  - "Decouple components with a simple event bus"
  - "Use ObservableValue subscriptions instead of direct references"
estimatedMinutes: 12
starterSnippet: |
  public static Parent build() {
      // Simple event bus via ObservableValue
      StringProperty eventBus = new SimpleStringProperty("");

      // Producer
      Button send = new Button("Publish event");
      int[] seq = {0};
      send.setOnAction(e -> eventBus.set("event-" + (++seq[0])));

      // Consumer A
      Label consumerA = new Label("A: waiting");
      eventBus.addListener((obs, old, val) -> consumerA.setText("A received: " + val));

      // Consumer B
      Label consumerB = new Label("B: waiting");
      eventBus.addListener((obs, old, val) -> consumerB.setText("B received: " + val));

      return new VBox(10, send, consumerA, consumerB);
  }
challenges:
  - id: c1
    description: "Add a third Consumer C that only reacts to even-numbered events"
    assertion: containsNodeOfType(Label)
nextLesson: 073-css-selectors
---

# Event-Driven Architecture

Event-driven design decouples *producers* (things that emit events)
from *consumers* (things that react). Neither needs to know about the
other directly.

## ObservableValue as an event bus

A `StringProperty` (or `ObjectProperty<MyEvent>`) is a simple single-
topic event bus. Producers call `set()`; consumers call `addListener()`.

## Advantages

- No direct imports between unrelated modules
- Easy to add new consumers without changing the producer
- Observable — you can bind UI state to it directly

## More robust approaches

For a real app, consider a typed `record` for the event payload,
a `Queue<Event>` for replay, or a library like RxJava. The pattern
above is the zero-dependency baseline.

## Challenge

Add `Consumer C` that parses the sequence number from `val` and only
updates its label when the number is even.
