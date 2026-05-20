---
id: 082-looked-up-colors
tier: css
order: 82
title: "Looked-Up Colors"
objectives:
  - "Define named colors with looked-up color syntax"
  - "Override named colors to re-theme components"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      // Looked-up colors are defined in CSS, not here.
      // This snippet shows the Java side: setting a root style that defines them.
      VBox root = new VBox(10);
      root.setStyle(
          "-brand-primary: steelblue;" +
          "-brand-secondary: tomato;");
      Button b1 = new Button("Primary action");
      b1.setStyle("-fx-background-color: -brand-primary; -fx-text-fill: white;");
      Button b2 = new Button("Secondary action");
      b2.setStyle("-fx-background-color: -brand-secondary; -fx-text-fill: white;");
      root.getChildren().addAll(b1, b2);
      return root;
  }
challenges:
  - id: c1
    description: "Change -brand-primary to mediumpurple and observe both buttons update"
    assertion: containsNodeOfType(Button)
nextLesson: 083-fxml-loader
---

# Looked-Up Colors

JavaFX CSS supports *named colors* (looked-up colors). You define them
with a leading hyphen and reference them later:

```css
/* Definition (on :root or any ancestor) */
-my-accent: #4a8cff;

/* Usage */
.button { -fx-background-color: -my-accent; }
```

## Java equivalent

```java
root.setStyle("-my-accent: steelblue;");
```

## Re-theming

Change the definition in one place and every rule that references it
updates automatically. This is the CSS variable pattern — JavaFX
supported it long before browser `var()`.

## Challenge

Change `-brand-primary` to `mediumpurple` and observe both buttons
update because both reference the same looked-up color.
