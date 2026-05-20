---
id: 074-pseudo-classes
tier: css
order: 74
title: "CSS Pseudo-Classes"
objectives:
  - "Style nodes on :hover, :focused, :disabled, and :selected"
  - "Define and activate a custom pseudo-class"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      Button btn = new Button("Hover and Focus me");
      btn.setStyle(
          "-fx-background-color: steelblue; -fx-text-fill: white;");
      // Pseudo-class styles must come from a stylesheet, not inline setStyle
      // This lesson shows the pattern; use getStylesheets() in a real app
      Label hint = new Label("Pseudo-classes: :hover :focused :pressed :disabled");
      btn.setOnAction(e -> btn.setDisable(!btn.isDisabled()));
      return new VBox(10, btn, hint);
  }
challenges:
  - id: c1
    description: "Add a custom :error pseudo-class that turns the button red"
    assertion: containsNodeOfType(Button)
nextLesson: 075-fx-properties
---

# CSS Pseudo-Classes

JavaFX supports standard CSS pseudo-classes:

| Pseudo-class | Condition |
|---|---|
| `:hover` | Mouse cursor over the node |
| `:focused` | Node has keyboard focus |
| `:pressed` | Mouse button held down |
| `:disabled` | `node.isDisabled()` is true |
| `:selected` | Control's selection model has this item selected |
| `:empty` | `ListCell` / `TableCell` represents empty space |

In CSS:
```css
.button:hover { -fx-background-color: darkblue; }
.button:focused { -fx-border-color: gold; -fx-border-width: 2; }
```

## Custom pseudo-classes

```java
PseudoClass ERROR = PseudoClass.getPseudoClass("error");
node.pseudoClassStateChanged(ERROR, true);
```

CSS: `.button:error { -fx-background-color: red; }`

## Challenge

Implement a custom `:error` pseudo-class on the button and toggle it
with a second button.
