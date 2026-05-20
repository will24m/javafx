---
id: 079-css-transitions
tier: css
order: 79
title: "CSS Transitions (JavaFX 24+)"
objectives:
  - "Animate property changes with -fx-transition"
  - "Understand which properties are animatable via CSS"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      // CSS transitions were added in JavaFX 24.
      // If running on JavaFX 21, use FadeTransition / ScaleTransition instead.
      Button btn = new Button("Hover for transition");
      btn.setStyle(
          "-fx-background-color: steelblue; -fx-text-fill: white; -fx-padding: 8 20 8 20;" +
          "-fx-scale-x: 1; -fx-scale-y: 1;");
      // Simulate with a property listener since we're on JavaFX 21
      btn.hoverProperty().addListener((obs, old, hovered) -> {
          btn.setScaleX(hovered ? 1.08 : 1.0);
          btn.setScaleY(hovered ? 1.08 : 1.0);
      });
      return new StackPane(btn);
  }
challenges:
  - id: c1
    description: "Add a ScaleTransition to animate smoothly instead of instant scale change"
    assertion: containsNodeOfType(Button)
nextLesson: 080-drop-shadow-blur
---

# CSS Transitions (JavaFX 24+)

JavaFX 24 added CSS transitions:

```css
.button {
    -fx-background-color: steelblue;
    -fx-transition: -fx-background-color 200ms ease;
}
.button:hover {
    -fx-background-color: darkblue;
}
```

On JavaFX 21 (this tutor), use `ScaleTransition`, `FadeTransition`,
or `Timeline` to achieve the same effect programmatically.

## Animatable properties (JavaFX 24+)

`-fx-background-color`, `-fx-opacity`, `-fx-scale-x/y`, `-fx-rotate`,
`-fx-translate-x/y`.

## Challenge

Replace the instant scale change with a `ScaleTransition(Duration.millis(150), btn)`:
`st.setToX(1.08); st.setToY(1.08); st.play();` on hover,
`st.setToX(1.0); st.setToY(1.0); st.play();` on un-hover.
