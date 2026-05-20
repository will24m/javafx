---
id: 014-colors-and-paint
tier: foundations
order: 14
title: "Color and Paint"
objectives:
  - "Create colors with Color.rgb(), Color.hsb(), and web hex strings"
  - "Use LinearGradient and RadialGradient as fills"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(200, 100);
      rect.setFill(new LinearGradient(
          0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
          new Stop(0, Color.STEELBLUE),
          new Stop(1, Color.MEDIUMPURPLE)));
      return new StackPane(rect);
  }
challenges:
  - id: c1
    description: "Change to a vertical gradient (0,0 → 0,1) that goes from TOMATO to GOLD"
    assertion: containsNodeOfType(javafx.scene.shape.Rectangle)
nextLesson: 015-fonts-and-text
---

# Color and Paint

`Paint` is the base type for anything you can assign to `fill` or
`stroke`. JavaFX provides three concrete kinds:

## Color

```java
Color.RED                          // named constant
Color.rgb(255, 128, 0)             // 8-bit RGB
Color.rgb(255, 128, 0, 0.5)        // with alpha
Color.hsb(120, 1.0, 0.8)          // hue/saturation/brightness
Color.web("#ff8800")               // hex string
```

## LinearGradient

Arguments: `startX, startY, endX, endY, proportional, cycleMethod, Stop...`

When `proportional = true` coordinates are 0–1 (relative to the shape).

## RadialGradient

Similar, but radiates from a center point outward.

## Challenge

Change the gradient to vertical (`startX=0, startY=0, endX=0, endY=1`)
running from `Color.TOMATO` to `Color.GOLD`.
