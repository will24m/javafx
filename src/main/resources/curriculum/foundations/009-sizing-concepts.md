---
id: 009-sizing-concepts
tier: foundations
order: 9
title: "Sizing: min, pref, max"
objectives:
  - "Understand the difference between minimum, preferred, and maximum size"
  - "Override sizing hints to control layout behaviour"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      Button small = new Button("Fixed 80px");
      small.setMinWidth(80);
      small.setMaxWidth(80);

      Button grow = new Button("I grow");
      grow.setMaxWidth(Double.MAX_VALUE);

      return new VBox(10, small, grow);
  }
challenges:
  - id: c1
    description: "Set the grow button's minWidth to 120"
    assertion: containsNodeOfType(Button)
nextLesson: 010-layout-pulse
---

# Sizing: min, pref, max

Every JavaFX `Region` has three size hints that layout containers use
to allocate space:

| Property | Meaning |
|---|---|
| `minWidth` | Smallest the node will ever be |
| `prefWidth` | Ideal width the node wants |
| `maxWidth` | Largest the node will accept |

## `Double.MAX_VALUE`

Setting `maxWidth` to `Double.MAX_VALUE` signals "I will take all
available space". This is the standard way to make a control fill its
container horizontally.

## Computing vs. overriding

By default JavaFX computes these from the node's content. You override
them with `setMinWidth` / `setPrefWidth` / `setMaxWidth`.

## Challenge

Set `grow.setMinWidth(120)`.
