---
id: 080-drop-shadow-blur
tier: css
order: 80
title: "Effects: DropShadow and GaussianBlur"
objectives:
  - "Apply DropShadow and GaussianBlur effects to nodes"
  - "Compose effects with the setEffect API"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      Label card = new Label("Elevated card");
      card.setPadding(new Insets(20 ,32, 20, 32));
      card.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
      card.setEffect(new DropShadow(16, 0, 4, Color.color(0, 0, 0, 0.25)));

      javafx.scene.shape.Rectangle blurred = new javafx.scene.shape.Rectangle(100, 40, Color.STEELBLUE);
      blurred.setEffect(new GaussianBlur(6));

      return new VBox(20, card, blurred);
  }
challenges:
  - id: c1
    description: "Chain a ColorAdjust effect on the rectangle (use Blend or multiple effects)"
    assertion: containsNodeOfType(Label)
nextLesson: 081-clip-and-shape
---

# Effects: DropShadow and GaussianBlur

JavaFX effects are post-processing passes applied after a node renders.
Set with `node.setEffect(effect)`.

## DropShadow

```java
new DropShadow(radius, offsetX, offsetY, color)
```

`radius` — blur radius of the shadow
`offsetX/Y` — shadow displacement

## GaussianBlur

```java
new GaussianBlur(radius)
```

Blurs the node itself. Often used for background blur (frosted glass).

## InnerShadow

```java
new InnerShadow(radius, color)
```

Shadow inside the boundary — good for inset buttons and recessed areas.

## Chaining effects

Effects can be chained with `effect.setInput(otherEffect)`.

## Challenge

Add a `ColorAdjust` effect with `setSaturation(0.5)` as the input to
the `GaussianBlur`.
