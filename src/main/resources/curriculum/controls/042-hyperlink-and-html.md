---
id: 042-hyperlink-and-html
tier: controls
order: 42
title: "Hyperlink and HTMLEditor"
objectives:
  - "Create a clickable Hyperlink"
  - "Edit rich text with HTMLEditor"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      Hyperlink link = new Hyperlink("javafx.dev");
      link.setOnAction(e -> System.out.println("Clicked: " + link.getText()));

      Label status = new Label("Click the link");
      link.setOnAction(e -> status.setText("Visited: " + link.getText()));
      return new VBox(10, link, status);
  }
challenges:
  - id: c1
    description: "Add a second Hyperlink with text 'GitHub' below the first"
    assertion: containsNodeOfType(Hyperlink)
nextLesson: 043-color-picker
---

# Hyperlink and HTMLEditor

## Hyperlink

A `Button` that looks like an anchor tag. It fires `ActionEvent` like
any button. After clicking, `isVisited()` returns true and the control
appears in the `:visited` CSS pseudo-class.

## HTMLEditor

`HTMLEditor` is a full WYSIWYG rich-text editor that produces HTML:

```java
HTMLEditor editor = new HTMLEditor();
editor.setHtmlText("<b>Hello</b> World");
String html = editor.getHtmlText();
```

Note: `HTMLEditor` uses an embedded WebView internally. It is heavy —
avoid creating many instances.

## Challenge

Add a second `Hyperlink("GitHub")` below the first.
