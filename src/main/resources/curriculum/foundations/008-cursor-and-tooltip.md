---
id: 008-cursor-and-tooltip
tier: foundations
order: 8
title: "Cursor and Tooltip"
objectives:
  - "Change the mouse cursor on hover"
  - "Add a Tooltip to a node"
estimatedMinutes: 6
starterSnippet: |
  public static Parent build() {
      Button btn = new Button("Hover me");
      btn.setCursor(Cursor.HAND);
      Tooltip.install(btn, new Tooltip("I am a tooltip!"));
      return new StackPane(btn);
  }
challenges:
  - id: c1
    description: "Add a second Button labeled \"Crosshair btn\" with its cursor set to Cursor.CROSSHAIR"
    assertion: countOfType(Button, n=2)
    starterSnippet: |
      public static Parent build() {
          Button btn = new Button("Hover me");
          btn.setCursor(Cursor.HAND);
          Tooltip.install(btn, new Tooltip("I am a tooltip!"));
          return new StackPane(btn);
      }
    solutionSnippet: |
      public static Parent build() {
          Button btn = new Button("Hover me");
          btn.setCursor(Cursor.HAND);
          Tooltip.install(btn, new Tooltip("I am a tooltip!"));
          Button cross = new Button("Crosshair btn");
          cross.setCursor(Cursor.CROSSHAIR);
          return new VBox(10, btn, cross);
      }
nextLesson: 009-sizing-concepts
---

# Cursor and Tooltip

Two small but important polish items every real app needs.

## Cursor

`node.setCursor(Cursor.HAND)` changes the mouse pointer when it enters
the node. Common values: `Cursor.DEFAULT`, `Cursor.HAND`,
`Cursor.CROSSHAIR`, `Cursor.TEXT`, `Cursor.WAIT`.

## Tooltip

`Tooltip.install(node, new Tooltip("text"))` attaches a floating help
bubble. Hover the node for ~700 ms to see it.

You can also set it directly on controls:

```java
button.setTooltip(new Tooltip("text"));
```

## Challenge

Change the cursor to `Cursor.CROSSHAIR`.
