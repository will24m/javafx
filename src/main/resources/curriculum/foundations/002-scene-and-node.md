---
id: 002-scene-and-node
tier: foundations
order: 2
title: "Scene and Node"
objectives:
  - "Understand that Scene is the container for the node tree"
  - "Add a node to a scene and observe the parent-child relationship"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      Button btn = new Button("Click me");
      return new VBox(btn);
  }
challenges:
  - id: c1
    description: "Add a Label above the button that reads 'Hello' — the VBox should have two children"
    assertion: containsNodeOfType(Label)
nextLesson: 003-stackpane-hello
---

# Scene and Node

Every visible element in JavaFX is a **Node**. Nodes are arranged in a
tree — a *scene graph*. At the root of that tree sits a `Parent`
(typically a layout pane like `VBox`). The `Scene` holds that root and
connects it to the `Stage`.

In this tutor every snippet returns a `Parent` directly. The framework
mounts it as the root of the preview pane — you are working with the
real scene graph right now.

## Parent / child

A `Parent` may have children (`getChildren()`). A `Node` always knows
its parent (`getParent()`). When a node has no parent it is *detached*
and invisible.

## Challenge

Add a `Label` with the text `"Hello"` **above** the existing button
inside the `VBox`. When the challenge checker runs it walks the live
scene graph and confirms a `Label` node is present.
