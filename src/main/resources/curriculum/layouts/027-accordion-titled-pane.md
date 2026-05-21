---
id: 027-accordion-titled-pane
tier: layouts
order: 27
title: "Accordion and TitledPane"
objectives:
  - "Collapse and expand sections with TitledPane"
  - "Group TitledPanes into an Accordion (mutually exclusive expansion)"
  - "React to expansion state via expandedProperty"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      TitledPane general = new TitledPane("General",
          new VBox(6,
              new CheckBox("Enable notifications"),
              new CheckBox("Start at login")));
      TitledPane editor = new TitledPane("Editor",
          new VBox(6,
              new Label("Font size"),
              new Slider(10, 20, 13)));
      TitledPane account = new TitledPane("Account",
          new VBox(6,
              new Label("Email"),
              new TextField("you@example.com")));

      // Pad each pane's content
      ((VBox) general.getContent()).setPadding(new Insets(8));
      ((VBox) editor.getContent()).setPadding(new Insets(8));
      ((VBox) account.getContent()).setPadding(new Insets(8));

      Accordion accordion = new Accordion(general, editor, account);
      accordion.setExpandedPane(general);

      Label status = new Label("Open: General");
      status.setStyle("-fx-padding:6 8 6 8;");
      accordion.expandedPaneProperty().addListener((obs, old, sel) ->
          status.setText("Open: " + (sel == null ? "(none)" : sel.getText())));

      return new VBox(8, status, accordion);
  }
challenges:
  - id: c1
    description: "Start with the 'Account' pane expanded instead of 'General'"
    assertion: 'containsNodeOfType(Accordion)'
nextLesson: 028-toolbar-menubar
---

# Accordion and TitledPane

`TitledPane` is a collapsible panel: a title bar on top, content below,
a disclosure arrow that toggles between expanded and collapsed.

`Accordion` groups multiple `TitledPane`s with one rule: **at most one
can be expanded at a time**. Opening one collapses the others. This is
the standard pattern for settings panels and grouped filter UIs.

## Standalone TitledPane

You don't need an `Accordion` to use `TitledPane` — it works on its own
as a single collapsible section, useful for showing/hiding advanced
options.

```java
TitledPane pane = new TitledPane("Advanced", advancedContent);
pane.setExpanded(false);                 // start collapsed
pane.setCollapsible(true);               // user can toggle
pane.setAnimated(true);                  // slide animation
```

`setCollapsible(false)` removes the disclosure arrow and locks the pane
open — useful when you want the visual chrome of a titled panel without
the toggle behavior.

## Tracking the expanded pane

```java
accordion.expandedPaneProperty().addListener((obs, old, sel) -> {
    if (sel != null) System.out.println("Now showing " + sel.getText());
});
```

`expandedPaneProperty()` is `null` when every pane is collapsed (the
user clicked the currently-open pane to close it — `Accordion` allows
this).

## Why not just use TabPane?

`Accordion` and `TabPane` solve similar problems (pick one of N
sections to show), but with different tradeoffs:

| | `Accordion` | `TabPane` |
|---|---|---|
| Layout | vertical stack | horizontal strip |
| Visible labels | always (titles) | always (tab text) |
| Best for | many sections of varying height | a few peer-equal views |
| Mobile feel | similar to OS settings menus | similar to browser tabs |

## Challenge

Change `setExpandedPane(general)` to `setExpandedPane(account)` so the
Account section is open first when the snippet loads.
