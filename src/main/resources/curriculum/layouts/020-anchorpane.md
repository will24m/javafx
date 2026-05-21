---
id: 020-anchorpane
tier: layouts
order: 20
title: "AnchorPane"
objectives:
  - "Pin nodes to one or more edges of an AnchorPane"
  - "Use opposite anchors to make a node stretch with the container"
  - "Recognise when AnchorPane is the right tool — and when it isn't"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      AnchorPane pane = new AnchorPane();
      pane.setStyle("-fx-background-color:#e8eef7;");
      pane.setPrefSize(380, 240);

      // Top-left: pinned to the corner, fixed size
      Button topLeft = new Button("Top-Left");
      AnchorPane.setTopAnchor(topLeft, 10.0);
      AnchorPane.setLeftAnchor(topLeft, 10.0);

      // Bottom-right: floating action button style
      Button fab = new Button("+");
      fab.setStyle("-fx-font-size:18px; -fx-min-width:36; -fx-min-height:36;");
      AnchorPane.setBottomAnchor(fab, 16.0);
      AnchorPane.setRightAnchor(fab, 16.0);

      // Top strip: stretches across the full width
      Label header = new Label("  Header — stretches with pane width");
      header.setStyle("-fx-background-color:#2d4a8a; -fx-text-fill:white; -fx-padding:6;");
      header.setMaxWidth(Double.MAX_VALUE);
      AnchorPane.setTopAnchor(header, 50.0);
      AnchorPane.setLeftAnchor(header, 0.0);
      AnchorPane.setRightAnchor(header, 0.0);

      pane.getChildren().addAll(topLeft, fab, header);
      return pane;
  }
challenges:
  - id: c1
    description: "Add a Label('Status') pinned to bottom=10, left=10"
    assertion: 'containsLabeledWithText(text="Status")'
nextLesson: 021-flowpane-tilepane
---

# AnchorPane

`AnchorPane` pins nodes to its edges. Each child can have any
combination of top/right/bottom/left anchors, expressed as a distance
in pixels from that edge.

## Static methods

```java
AnchorPane.setTopAnchor(node, 10.0);
AnchorPane.setRightAnchor(node, 10.0);
AnchorPane.setBottomAnchor(node, 10.0);
AnchorPane.setLeftAnchor(node, 10.0);
```

Pass a `Double` distance or `null` to clear an anchor.

## Single anchor = "stick to that edge"

Set only `topAnchor` and `leftAnchor` and the node keeps that position
forever — even as the pane resizes around it. Classic for fixed corner
elements like a floating action button.

## Opposite anchors = "stretch"

Set **both** `leftAnchor` and `rightAnchor` and the node stretches
horizontally as the pane resizes (its width becomes
`pane.width - left - right`). Same for top + bottom. This is how you
make a header strip span the full width.

## When NOT to use AnchorPane

It's tempting to use `AnchorPane` for everything because Scene Builder
defaults to it. But:

- For aligned-content forms → `GridPane`
- For linear stacks → `VBox` / `HBox`
- For the "desktop app skeleton" → `BorderPane`
- For card-style overlays → `StackPane`

`AnchorPane` shines for **free-form pinning** that doesn't fit any of
those — floating buttons, drag handles, absolute-positioned panels.

## Challenge

Add `new Label("Status")` and pin it to `bottomAnchor=10`,
`leftAnchor=10` so it stays in the bottom-left corner.
