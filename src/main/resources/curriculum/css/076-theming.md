---
id: 076-theming
tier: css
order: 76
title: "Theming: Light and Dark"
objectives:
  - "Switch between light and dark themes at runtime"
  - "Use -fx-base and color-derive() for consistent palettes"
estimatedMinutes: 12
starterSnippet: |
  public static Parent build() {
      Label content = new Label("Themed content");
      VBox root = new VBox(10, content);
      root.setPadding(new Insets(20));

      Button toggle = new Button("Toggle theme");
      toggle.setOnAction(e -> {
          var sheets = root.getStylesheets();
          if (sheets.isEmpty()) {
              root.setStyle("-fx-background-color: #1e1f22;");
              content.setStyle("-fx-text-fill: #f0f1f3;");
              toggle.setText("Switch to light");
          } else {
              root.setStyle("-fx-background-color: #f5f5f5;");
              content.setStyle("-fx-text-fill: #1e1f22;");
              toggle.setText("Switch to dark");
          }
      });
      root.getChildren().add(0, toggle);
      return root;
  }
challenges:
  - id: c1
    description: "Extract the theme CSS into two stylesheet strings and swap getStylesheets() instead of inline styles"
    assertion: containsNodeOfType(Button)
nextLesson: 077-css-hot-reload
---

# Theming: Light and Dark

JavaFX's default stylesheet is **Modena** (light). Theming strategies:

## Strategy 1: Swap top-level stylesheets

```java
scene.getStylesheets().clear();
scene.getStylesheets().add(darkCssUrl);
```

## Strategy 2: -fx-base

Modena derives most colours from `-fx-base`. Setting it on a root
node cascades everywhere:

```css
.root { -fx-base: #1e1f22; }  /* dark */
.root { -fx-base: #ececec; }  /* light (Modena default) */
```

## Strategy 3: Color lookup functions

```css
-fx-background-color: derive(-fx-base, -10%);
```

`derive(color, %)` lightens/darkens relative to the lookup value.

## Challenge

Replace `setStyle()` calls with two String constants holding full CSS
and swap `root.getStylesheets()` on toggle.
