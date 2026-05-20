---
id: 015-fonts-and-text
tier: foundations
order: 15
title: "Fonts and Text"
objectives:
  - "Create a Text node and set its Font"
  - "Use FontWeight and FontPosture"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      Text heading = new Text("JavaFX Text Node");
      heading.setFont(Font.font("Arial", FontWeight.BOLD, 24));
      heading.setFill(Color.STEELBLUE);

      Text body = new Text("Regular body text at 14px.");
      body.setFont(Font.font(null, FontWeight.NORMAL, 14));

      return new VBox(8, heading, body);
  }
challenges:
  - id: c1
    description: "Set the body text to italic using FontPosture.ITALIC"
    assertion: containsNodeOfType(javafx.scene.text.Text)
nextLesson: 016-vbox-and-hbox
---

# Fonts and Text

`Text` is a low-level `Node` for rendering a string. Unlike `Label` it
does not have padding or a background — it is a pure typographic shape.

## Font.font()

```java
Font.font("family", weight, size)
Font.font("family", posture, size)
Font.font("family", weight, posture, size)
Font.font(size)          // default family
Font.font(null, ...)     // system default family
```

Available weights: `FontWeight.THIN` … `FontWeight.BLACK`
Available postures: `FontPosture.REGULAR`, `FontPosture.ITALIC`

## Text vs. Label

| | `Text` | `Label` |
|---|---|---|
| Layout | flow / absolute | control box with padding |
| Background | none | optional |
| Wrapping | `setWrappingWidth()` | `setWrapText(true)` |

## Challenge

Set `body`'s font to italic using
`Font.font(null, FontPosture.ITALIC, 14)`.
