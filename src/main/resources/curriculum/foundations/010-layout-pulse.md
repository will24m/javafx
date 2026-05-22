---
id: 010-layout-pulse
tier: foundations
order: 10
title: "The Layout Pulse"
objectives:
  - "Understand when JavaFX performs layout and CSS passes"
  - "Use requestLayout() and applyCss() deliberately"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      Label info = new Label("Resize the pane to trigger layout");
      info.widthProperty().addListener((obs, old, w) ->
          info.setText("label width = " + w.intValue()));
      return new StackPane(info);
  }
challenges:
  - id: c1
    description: "Add a second Label that shows the StackPane's width instead"
    assertion: countOfType(Label, n=2)
    starterSnippet: |
      public static Parent build() {
          Label info = new Label("Resize the pane to trigger layout");
          info.widthProperty().addListener((obs, old, w) ->
              info.setText("label width = " + w.intValue()));
          return new StackPane(info);
      }
    solutionSnippet: |
      public static Parent build() {
          StackPane root = new StackPane();
          Label info = new Label("Resize the pane to trigger layout");
          info.widthProperty().addListener((obs, old, w) ->
              info.setText("label width = " + w.intValue()));
          Label paneWidth = new Label("pane width = 0");
          root.widthProperty().addListener((obs, old, w) ->
              paneWidth.setText("pane width = " + w.intValue()));
          VBox vb = new VBox(4, info, paneWidth);
          root.getChildren().add(vb);
          return root;
      }
nextLesson: 011-observable-properties
---

# The Layout Pulse

JavaFX batches layout work into *pulses* — roughly every frame (60 fps).
A pulse runs three passes in order:

1. **CSS** — compute style properties from stylesheets
2. **Layout** — measure and position children
3. **Paint** — render to the screen

## Why batching matters

If you set 10 properties in a loop, JavaFX does not re-layout after
each one. It marks the node *dirty* and defers until the next pulse.
This is why rapid property changes are cheap.

## Forcing a pass

`node.requestLayout()` marks the node dirty so the next pulse
re-layouts it. `node.applyCss()` runs the CSS pass immediately (useful
when you need a computed value before the next frame, e.g. in tests).

## Challenge

Add a second `Label` that listens to the `StackPane`'s `widthProperty()`
and displays its integer value.
