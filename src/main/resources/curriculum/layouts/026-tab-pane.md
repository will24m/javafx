---
id: 026-tab-pane
tier: layouts
order: 26
title: "TabPane"
objectives:
  - "Create a tabbed interface with TabPane and Tab"
  - "Add content to each tab and handle selection changes"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      Tab tab1 = new Tab("Settings", new Label("Settings content"));
      Tab tab2 = new Tab("Preview", new Label("Preview content"));
      tab1.setClosable(false);
      tab2.setClosable(false);
      TabPane tabs = new TabPane(tab1, tab2);
      return tabs;
  }
challenges:
  - id: c1
    description: "Add a third tab 'Help' with a Label('Coming soon')"
    assertion: containsLabeledWithText(text="Coming soon")
nextLesson: 027-accordion-titled-pane
---

# TabPane

`TabPane` holds a collection of `Tab` objects. Only one tab's content
is visible at a time.

## Creating tabs

```java
Tab tab = new Tab("Title", contentNode);
tab.setClosable(false);  // prevent the user from closing it
```

## Listening to selection

```java
tabs.getSelectionModel().selectedItemProperty()
    .addListener((obs, old, newTab) -> { ... });
```

## Placement

`tabs.setSide(Side.BOTTOM)` moves the tab strip to the bottom.
Default is `Side.TOP`.

## Challenge

Add a third `Tab("Help", new Label("Coming soon"))` to the `TabPane`.
