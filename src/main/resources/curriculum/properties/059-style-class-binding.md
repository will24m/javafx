---
id: 059-style-class-binding
tier: properties
order: 59
title: "Dynamic Style Classes"
objectives:
  - "Toggle CSS classes programmatically with getStyleClass()"
  - "Use PseudoClass for custom pseudo-states"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      Label badge = new Label("Status: OK");
      badge.getStyleClass().add("badge-ok");

      Button toggle = new Button("Toggle error");
      toggle.setOnAction(e -> {
          boolean isError = badge.getStyleClass().contains("badge-error");
          badge.getStyleClass().removeAll("badge-ok", "badge-error");
          badge.getStyleClass().add(isError ? "badge-ok" : "badge-error");
          badge.setText(isError ? "Status: OK" : "Status: ERROR");
      });

      badge.setStyle(""); // styles come from CSS in a real app
      badge.getStyleClass().stream().forEach(System.out::println);
      return new VBox(10, badge, toggle);
  }
challenges:
  - id: c1
    description: "Use PseudoClass.getPseudoClass('error') and pseudoClassStateChanged() instead"
    assertion: containsNodeOfType(Label)
nextLesson: 060-computed-binding-lazy
---

# Dynamic Style Classes

## getStyleClass()

`node.getStyleClass()` returns a mutable `ObservableList<String>`.
Add/remove strings to apply/remove CSS rules that target `.class-name`.

```java
node.getStyleClass().add("highlighted");
node.getStyleClass().remove("highlighted");
```

## PseudoClass

For custom `:pseudo-class` selectors in CSS:

```java
PseudoClass ERROR = PseudoClass.getPseudoClass("error");
node.pseudoClassStateChanged(ERROR, true);  // activates :error
node.pseudoClassStateChanged(ERROR, false); // deactivates
```

In CSS: `.my-label:error { -fx-text-fill: red; }`

## Challenge

Replace the style-class toggling with `PseudoClass` so the CSS rule
targets `:error` instead of `.badge-error`.
