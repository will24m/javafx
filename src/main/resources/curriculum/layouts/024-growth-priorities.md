---
id: 024-growth-priorities
tier: layouts
order: 24
title: "Growth Priorities"
objectives:
  - "Distribute extra space with HBox.setHgrow / VBox.setVgrow"
  - "Understand Priority.ALWAYS, SOMETIMES, and NEVER"
  - "Make a Region actually grow by setting setMaxWidth/Height"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      // Classic browser-style URL bar
      Button back = new Button("◀");
      Button forward = new Button("▶");
      TextField url = new TextField("https://example.com");
      url.setMaxWidth(Double.MAX_VALUE);
      HBox.setHgrow(url, Priority.ALWAYS);
      Button go = new Button("Go");

      HBox toolbar = new HBox(6, back, forward, url, go);
      toolbar.setPadding(new Insets(8));
      toolbar.setAlignment(Pos.CENTER_LEFT);

      // Filler so vertical stretching is visible
      Label body = new Label("Body — fills the rest of the height");
      body.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
      body.setStyle("-fx-background-color:#eef0f4; -fx-padding:24; -fx-alignment:center;");
      VBox.setVgrow(body, Priority.ALWAYS);

      VBox root = new VBox(toolbar, body);
      root.setPrefSize(420, 240);
      return root;
  }
challenges:
  - id: c1
    description: "Set HBox.setHgrow on the 'back' button to Priority.ALWAYS so it also stretches"
    assertion: 'containsNodeOfType(TextField)'
nextLesson: 025-region-layout-override
---

# Growth Priorities

When a `VBox` or `HBox` has more space than its children need, it
distributes the leftover among children that have a **grow priority**.

| Priority | Behaviour |
|---|---|
| `ALWAYS` | Always takes extra space (split equally among siblings with `ALWAYS`) |
| `SOMETIMES` | Takes extra space only if no `ALWAYS` siblings exist |
| `NEVER` | Never grows beyond preferred size (the default) |

## Setting the priority

```java
HBox.setHgrow(node, Priority.ALWAYS);   // grow horizontally inside an HBox
VBox.setVgrow(node, Priority.ALWAYS);   // grow vertically inside a VBox
GridPane.setHgrow(node, Priority.ALWAYS);   // also works on grid children
```

## The maxWidth/maxHeight gotcha

This is the #1 layout confusion in JavaFX:

> Setting `Hgrow=ALWAYS` is **necessary but not sufficient**.

A `Region` will not actually expand unless its `maxWidth` /
`maxHeight` allows it. By default many controls cap themselves at their
preferred size.

```java
TextField url = new TextField();
HBox.setHgrow(url, Priority.ALWAYS);     // tells the layout "let me grow"
url.setMaxWidth(Double.MAX_VALUE);       // tells the node "I'll accept any width"
```

`Region.USE_COMPUTED_SIZE` (the default for `maxWidth`) effectively
locks the max to the preferred size — which is why your `Hgrow=ALWAYS`
nodes refuse to stretch until you explicitly raise the ceiling.

## The starter snippet

The toolbar is a typical browser URL bar: two fixed nav buttons on the
left, a fixed "Go" button on the right, and a `TextField` in the middle
with `Hgrow=ALWAYS` + `maxWidth=MAX_VALUE` so it eats the leftover.

The body label below uses the same pattern vertically: `Vgrow=ALWAYS`
plus `maxHeight=MAX_VALUE` so it claims all the leftover height.

## Splitting space proportionally

If two children both have `Priority.ALWAYS`, they split the leftover
**equally**. To get a 2:1 split you need to use `GridPane` with
`ColumnConstraints.setPercentWidth()` instead.

## Challenge

Add `HBox.setHgrow(back, Priority.ALWAYS)` to the `back` button.
Because buttons have `maxWidth = USE_COMPUTED_SIZE` by default, you'll
also need `back.setMaxWidth(Double.MAX_VALUE)` for it to visibly grow.
