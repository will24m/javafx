---
id: 075-fx-properties
tier: css
order: 75
title: "-fx-* CSS Properties"
objectives:
  - "Know the most important -fx-* CSS properties for Region and Text"
  - "Understand that JavaFX CSS properties differ from browser CSS"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      Label card = new Label("Card with CSS");
      card.setStyle(
          "-fx-background-color: #2b2d30;" +
          "-fx-background-radius: 8;" +
          "-fx-border-color: #4a8cff;" +
          "-fx-border-width: 1;" +
          "-fx-border-radius: 8;" +
          "-fx-padding: 16 24 16 24;" +
          "-fx-text-fill: #f0f1f3;" +
          "-fx-font-size: 16;" +
          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 2);");
      return new StackPane(card);
  }
challenges:
  - id: c1
    description: "Add -fx-font-family: 'Menlo' and -fx-font-weight: bold"
    assertion: containsNodeOfType(Label)
nextLesson: 076-theming
---

# -fx-* CSS Properties

JavaFX CSS uses `-fx-` prefixed properties instead of standard CSS.

## Common Region properties

```css
-fx-background-color: #color (or paint, gradient);
-fx-background-radius: 8;           /* corner radius */
-fx-border-color: red;
-fx-border-width: 1 2 1 2;         /* top right bottom left */
-fx-border-radius: 6;
-fx-padding: 8 16 8 16;
-fx-effect: dropshadow(gaussian, black, 10, 0, 0, 3);
```

## Common Text/Label properties

```css
-fx-text-fill: white;
-fx-font-family: "Inter";
-fx-font-size: 14px;
-fx-font-weight: bold;
-fx-font-style: italic;
```

## Challenge

Add `-fx-font-family: "Menlo"` and `-fx-font-weight: bold` to the
card label.
