---
id: 016-vbox-and-hbox
tier: layouts
order: 16
title: "VBox and HBox"
objectives:
  - "Stack children vertically with VBox and horizontally with HBox"
  - "Control spacing, alignment, and per-child margins"
  - "Compose VBox + HBox to build a classic two-row form"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      Label title = new Label("Sign in");
      title.setStyle("-fx-font-size:16px; -fx-font-weight:bold;");

      TextField user = new TextField();
      user.setPromptText("username");
      user.setPrefWidth(180);

      PasswordField pw = new PasswordField();
      pw.setPromptText("password");
      pw.setPrefWidth(180);

      Button submit = new Button("Submit");
      Button cancel = new Button("Cancel");
      HBox actions = new HBox(8, cancel, submit);
      actions.setAlignment(Pos.CENTER_RIGHT);

      VBox form = new VBox(10, title, user, pw, actions);
      form.setPadding(new Insets(16));
      form.setAlignment(Pos.CENTER);
      return form;
  }
challenges:
  - id: c1
    description: "Make the actions row right-aligned (Pos.CENTER_RIGHT — already done — verify)"
    assertion: 'containsLabeledWithText(text="Submit")'
nextLesson: 017-stackpane-alignment
---

# VBox and HBox

`VBox` stacks children **vertically** (top to bottom). `HBox` arranges
them **horizontally** (left to right). Together they are the bread-and-
butter of every JavaFX layout you'll write — most non-trivial UIs are
just nested boxes.

## Constructor shorthand

```java
new VBox(spacing, child1, child2, ...);
new HBox(spacing, child1, child2, ...);
```

`spacing` is the gap (in pixels) between adjacent children. Omit it for
zero spacing.

## Alignment

`setAlignment(Pos)` controls where children are positioned **as a
group** when the box is larger than the sum of its children. For a
right-aligned button bar use `Pos.CENTER_RIGHT`; for a vertically
centered column inside a tall pane use `Pos.CENTER`.

The 9 most useful positions: `TOP_LEFT`, `TOP_CENTER`, `TOP_RIGHT`,
`CENTER_LEFT`, `CENTER`, `CENTER_RIGHT`, `BOTTOM_LEFT`, `BOTTOM_CENTER`,
`BOTTOM_RIGHT`.

## Per-child margin

```java
VBox.setMargin(node, new Insets(0, 0, 12, 0));   // 12px below
HBox.setMargin(node, new Insets(0, 8, 0, 0));    // 8px right
```

Margins live *outside* the child, distinct from padding which lives
*inside* it. The static method is on the parent class because it stores
the value as a layout constraint on the child.

## Composition pattern

The starter snippet is a real sign-in form: an outer `VBox` stacks the
title, two text inputs, and an action bar; the action bar is itself an
`HBox` aligned to the right. This **VBox-of-rows** pattern is how 80%
of forms in JavaFX are built.

## Challenge

The action row already aligns to `Pos.CENTER_RIGHT`. Verify the layout
looks correct, then experiment: change `Pos.CENTER_RIGHT` to
`Pos.CENTER_LEFT` and watch the button bar jump to the left edge.
