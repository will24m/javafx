---
id: 052-weak-listeners
tier: properties
order: 52
title: "Weak Listeners and Memory Leaks"
objectives:
  - "Understand how strong listener references prevent GC"
  - "Use WeakChangeListener and WeakInvalidationListener"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      StringProperty model = new SimpleStringProperty("initial");
      // Weak listener — does not prevent the lambda from being GC'd
      WeakChangeListener<String> weak = new WeakChangeListener<>((obs, old, val) ->
          System.out.println("Changed to: " + val));
      model.addListener(weak);
      Button change = new Button("Change value");
      change.setOnAction(e -> model.set("updated " + System.currentTimeMillis()));
      return new VBox(10, change, new Label("Check console for output"));
  }
challenges:
  - id: c1
    description: "Replace WeakChangeListener with a plain ChangeListener and observe the difference"
    assertion: containsNodeOfType(Button)
nextLesson: 053-custom-property
---

# Weak Listeners and Memory Leaks

Adding a listener to a long-lived observable keeps the listener
(and everything it closes over) alive for as long as the observable
exists. In a large app this is the most common source of memory leaks.

## The problem

```java
globalModel.nameProperty().addListener((obs, old, val) -> {
    shortLivedLabel.setText(val); // label and its subtree are kept alive
});
```

`globalModel` is alive for the app lifetime → listener is alive →
`shortLivedLabel` is alive → its whole UI subtree stays in memory.

## The fix

```java
WeakChangeListener<String> weak = new WeakChangeListener<>(myListener);
property.addListener(weak);
```

The property holds only a `WeakReference` to the listener. Once no
strong reference to `myListener` exists, GC clears the weak ref and
the property stops calling it.

## Challenge

Replace the `WeakChangeListener` with a plain `ChangeListener` and
note that the behavior is identical — the difference is only in
lifetime management.
