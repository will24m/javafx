---
id: 078-modena-override
tier: css
order: 78
title: "Overriding Modena"
objectives:
  - "Override specific Modena rules without replacing the whole theme"
  - "Use user-agent stylesheet vs. author stylesheet precedence"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      // Override just the button default background without a full theme swap
      Button btn = new Button("Modena-overridden Button");
      btn.setStyle(
          "-fx-background-color: linear-gradient(to bottom, #6a8cff, #3a5cbf);" +
          "-fx-text-fill: white;" +
          "-fx-background-radius: 4;" +
          "-fx-padding: 8 20 8 20;");
      return new StackPane(btn);
  }
challenges:
  - id: c1
    description: "Add :hover override using a stylesheet (not inline setStyle)"
    assertion: containsNodeOfType(Button)
nextLesson: 079-css-transitions
---

# Overriding Modena

Modena is the user-agent stylesheet. It has the *lowest* precedence.
Your stylesheets always win.

## Targeted override

Add a stylesheet after Modena:

```java
scene.getStylesheets().add(myOverride.toExternalForm());
```

In `myOverride.css`:

```css
.button { -fx-background-color: steelblue; -fx-text-fill: white; }
.button:hover { -fx-background-color: darkblue; }
```

## Removing Modena entirely

```java
Application.setUserAgentStylesheet(null); // removes Modena
```

Or switch to Caspian:

```java
Application.setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);
```

## Challenge

Move the inline `setStyle()` to a `getStylesheets()` entry (as a CSS
string) and add a `:hover` rule that lightens the background.
