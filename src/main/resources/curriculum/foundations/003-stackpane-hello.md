---
id: 003-stackpane-hello
tier: foundations
order: 3
title: "Your First Layout: StackPane"
objectives:
  - "Centre a node inside a StackPane"
  - "Use setAlignment to move a child off-centre"
estimatedMinutes: 6
starterSnippet: |
  public static Parent build() {
      Label l = new Label("Centred");
      return new StackPane(l);
  }
challenges:
  - id: c1
    description: "Change the label text to 'Top-left' and align it to the top-left corner of the StackPane"
    assertion: containsLabeledWithText(text="Top-left")
nextLesson: 004-vbox-and-hbox
---

# Your First Layout: StackPane

`StackPane` stacks its children on top of one another and centres each
one by default. It is the simplest layout pane and perfect for overlays,
splash screens, and centred content.

```java
StackPane sp = new StackPane();
sp.getChildren().add(new Label("Hello"));
```

## Alignment

`StackPane.setAlignment(node, Pos.TOP_LEFT)` pins a specific child to a
corner without affecting the others.

## Challenge

Change the label text to `"Top-left"` and use `StackPane.setAlignment`
to push it to `Pos.TOP_LEFT`. The challenge checker confirms the label
text is correct; try it and watch the preview update live.
