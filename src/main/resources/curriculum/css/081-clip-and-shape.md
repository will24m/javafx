---
id: 081-clip-and-shape
tier: css
order: 81
title: "Clipping Nodes"
objectives:
  - "Clip a node to a shape with setClip()"
  - "Create circular avatars and rounded images"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      // Circular clip for an avatar-style rectangle
      javafx.scene.shape.Rectangle avatar = new javafx.scene.shape.Rectangle(80, 80, Color.STEELBLUE);
      javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(40, 40, 40);
      avatar.setClip(clip);

      Label label = new Label("Clipped avatar");
      return new VBox(12, avatar, label);
  }
challenges:
  - id: c1
    description: "Apply a rounded-rectangle clip with Rectangle(arcWidth=16, arcHeight=16)"
    assertion: containsNodeOfType(javafx.scene.shape.Rectangle)
nextLesson: 082-looked-up-colors
---

# Clipping Nodes

`node.setClip(shape)` restricts the rendering of `node` to the area
of `shape`. Only pixels within the clip shape are drawn.

## Common patterns

**Circular image/avatar:**
```java
ImageView img = new ImageView(image);
img.setFitWidth(80); img.setFitHeight(80);
Circle clip = new Circle(40, 40, 40);
img.setClip(clip);
```

**Rounded corners on any node:**
```java
Rectangle clip = new Rectangle(w, h);
clip.setArcWidth(16); clip.setArcHeight(16);
node.setClip(clip);
```

## Note

The clip does not produce rounded corners on the node's background —
use `-fx-background-radius` in CSS for that. Clipping affects the
*rendered* pixels, including shadows and effects.

## Challenge

Change the `Circle` clip to a `Rectangle(80, 80)` with `arcWidth=16`
and `arcHeight=16` for a rounded-rectangle clip.
