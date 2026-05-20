---
id: 001-what-is-a-stage
tier: foundations
order: 1
title: "What is a Stage?"
objectives:
  - "Explain the difference between Stage, Scene, and Node"
  - "Create a Stage programmatically and show it"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      Label l = new Label("Hello, JavaFX");
      return new StackPane(l);
  }
challenges:
  - id: c1
    description: "Change the label text to 'Submit' and wrap it in a VBox"
    assertion: containsLabeledInside(text="Submit", parentType=VBox)
nextLesson: 002-scene-and-node
---

# What is a Stage?

A `Stage` is a top-level window. Every JavaFX application starts with one,
created for you by the framework and passed to `Application#start`. The
`Scene` holds the visual content; the `Stage` holds the `Scene` and gives
it a window with a title bar, close button, and frame.

In this tutor app the preview pane below acts as your Stage equivalent —
your snippet's `Parent` is mounted into it on every recompile. You will
not call `stage.show()` yourself in lessons; instead you return the root
`Parent` from `build()` and the framework mounts it for you.

## Your first challenge

Edit the snippet above so that the label reads **"Submit"** and lives
inside a `VBox` instead of a `StackPane`. The challenge runner checks the
scene graph directly — it does not read your code.
