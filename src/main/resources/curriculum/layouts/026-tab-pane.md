---
id: 026-tab-pane
tier: layouts
order: 26
title: "TabPane"
objectives:
  - "Create a tabbed interface with TabPane and Tab"
  - "Listen to selection changes via selectedItemProperty"
  - "Control closability, side placement, and tab-min/max widths"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      Label statusLabel = new Label("Selected: Overview");
      statusLabel.setStyle("-fx-padding:6 12 6 12; -fx-background-color:#eef0f4;");

      Tab overview = new Tab("Overview", new Label("Welcome — pick a tab"));
      Tab data     = new Tab("Data",     buildDataPane());
      Tab settings = new Tab("Settings", new Label("Settings live here"));
      overview.setClosable(false);
      data.setClosable(false);
      settings.setClosable(false);

      TabPane tabs = new TabPane(overview, data, settings);
      tabs.getSelectionModel().selectedItemProperty().addListener(
          (obs, old, sel) -> statusLabel.setText("Selected: " + sel.getText()));

      VBox root = new VBox(statusLabel, tabs);
      VBox.setVgrow(tabs, Priority.ALWAYS);
      return root;
  }

  private static Parent buildDataPane() {
      VBox box = new VBox(4,
          new Label("Row 1 — 42"),
          new Label("Row 2 — 7"),
          new Label("Row 3 — 119"));
      box.setPadding(new Insets(12));
      return box;
  }
challenges:
  - id: c1
    description: "Add a fourth Tab 'Help' with a Label('Coming soon')"
    assertion: 'containsLabeledWithText(text="Coming soon")'
nextLesson: 027-accordion-titled-pane
---

# TabPane

`TabPane` holds a collection of `Tab` objects, only one of which is
visible at a time. The strip of tab headers lets the user switch
between them.

## Creating tabs

```java
Tab tab = new Tab("Title", contentNode);
tab.setClosable(false);                        // hide the × button
tab.setGraphic(new ImageView(icon));           // optional icon
tab.setTooltip(new Tooltip("Hover text"));
```

`Tab` is **not** a `Node` — it's a model object held by the `TabPane`.
That's why `Tab` doesn't extend any of the layout panes; its content
lives in the `content` property.

## Listening for selection

```java
TabPane tabs = ...;
tabs.getSelectionModel().selectedItemProperty()
    .addListener((obs, old, newTab) -> {
        System.out.println("Switched to " + newTab.getText());
    });
```

`selectedItemProperty()` gives you the `Tab` object;
`selectedIndexProperty()` gives you the integer index. Both are
read-only properties you can bind to.

## Programmatic switching

```java
tabs.getSelectionModel().select(2);            // select by index
tabs.getSelectionModel().select(myTab);        // select by reference
```

## Layout knobs

```java
tabs.setSide(Side.BOTTOM);                     // tab strip on bottom
tabs.setTabMinWidth(80);                       // uniform tab widths
tabs.setTabMaxWidth(80);
tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);  // no × on any tab
```

`TabClosingPolicy` values: `ALL_TABS` (default), `SELECTED_TAB`,
`UNAVAILABLE`.

## Tab content lifecycle

The content node of an unselected tab is **still in the scene graph**
— it's just not visible. CSS, listeners, and animations on it stay
active. If a tab is expensive to populate, lazy-build it inside a
`selectedItemProperty` listener so you only pay for tabs the user
actually opens.

## Challenge

Add `new Tab("Help", new Label("Coming soon"))` to the `TabPane`.
Make sure to also call `setClosable(false)` on it to match the others.
