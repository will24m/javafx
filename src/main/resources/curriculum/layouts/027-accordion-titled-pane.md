---
id: 027-accordion-titled-pane
tier: layouts
order: 27
title: "Accordion and TitledPane"
objectives:
  - "Collapse and expand sections with TitledPane"
  - "Group TitledPanes into an Accordion"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      TitledPane t1 = new TitledPane("Section A", new Label("Content A"));
      TitledPane t2 = new TitledPane("Section B", new Label("Content B"));
      TitledPane t3 = new TitledPane("Section C", new Label("Content C"));
      Accordion accordion = new Accordion(t1, t2, t3);
      accordion.setExpandedPane(t1);
      return accordion;
  }
challenges:
  - id: c1
    description: "Start with t2 expanded instead of t1"
    assertion: containsNodeOfType(Accordion)
nextLesson: 028-toolbar-menubar
---

# Accordion and TitledPane

`TitledPane` is a collapsible panel with a title bar. Click the title
to toggle it open or closed.

`Accordion` groups multiple `TitledPane` instances so that at most one
is expanded at a time (clicking one collapses the others).

## Standalone TitledPane

You don't need an `Accordion`. A `TitledPane` works on its own as a
collapsible section.

```java
TitledPane pane = new TitledPane("Options", content);
pane.setExpanded(false); // start collapsed
pane.setCollapsible(true);
```

## Challenge

Change `setExpandedPane(t1)` to `setExpandedPane(t2)` so the second
section starts open.
