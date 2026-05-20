---
id: 007-images
tier: foundations
order: 7
title: "Images and ImageView"
objectives:
  - "Load an image from a URL with Image"
  - "Display it with ImageView and control its size"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      Image img = new Image("https://via.placeholder.com/200", true);
      ImageView view = new ImageView(img);
      view.setFitWidth(200);
      view.setPreserveRatio(true);
      return new StackPane(view);
  }
challenges:
  - id: c1
    description: "Set fitWidth to 100 and confirm the ImageView is present"
    assertion: containsNodeOfType(ImageView)
nextLesson: 008-cursor-and-tooltip
---

# Images and ImageView

`Image` loads image data (from a URL, classpath resource, or stream).
`ImageView` is the `Node` that renders it in the scene graph.

## Background loading

Passing `true` as the second argument to `new Image(url, true)` loads
the image on a background thread. The view shows the image once it
arrives without blocking the FX thread.

## Sizing

| Property | Effect |
|---|---|
| `fitWidth` / `fitHeight` | Target render size in pixels |
| `preserveRatio` | Keep aspect ratio when only one dimension is set |
| `smooth` | Bilinear vs. nearest-neighbour scaling |

## Challenge

Change `fitWidth` to `100` and verify the `ImageView` is in the scene.
