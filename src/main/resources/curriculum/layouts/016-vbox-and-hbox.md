---
id: 016-vbox-and-hbox
tier: layouts
order: 16
title: "VBox and HBox"
objectives:
  - "Stack children vertically with VBox and horizontally with HBox"
  - "Control spacing and alignment"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      Button b1 = new Button("One");
      Button b2 = new Button("Two");
      Button b3 = new Button("Three");
      HBox row = new HBox(8, b1, b2, b3);
      row.setAlignment(Pos.CENTER);
      return new VBox(16, new Label("Button Row"), row);
  }
challenges:
  - id: c1
    description: "Change HBox alignment to Pos.CENTER_RIGHT"
    assertion: containsNodeOfType(HBox)
nextLesson: 017-stackpane-alignment
---

# VBox and HBox

`VBox` stacks children **vertically**. `HBox` lays them out
**horizontally**. Both are the bread-and-butter layout panes for
linear flows.

## Constructor shorthand

```java
new VBox(spacing, child1, child2, ...);
new HBox(spacing, child1, child2, ...);
```

## Alignment

`setAlignment(Pos.CENTER)` aligns all children as a group within the
container. Individual children can override with
`VBox.setMargin(child, new Insets(...))`.

## Challenge

Change the `HBox` alignment to `Pos.CENTER_RIGHT`.
