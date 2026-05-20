---
id: 038-slider-progressbar
tier: controls
order: 38
title: "Slider and ProgressBar"
objectives:
  - "Bind a ProgressBar to a Slider value"
  - "Use ProgressIndicator for indeterminate state"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      Slider slider = new Slider(0, 1, 0.5);
      ProgressBar bar = new ProgressBar();
      bar.progressProperty().bind(slider.valueProperty());
      bar.setMaxWidth(Double.MAX_VALUE);
      return new VBox(12, slider, bar);
  }
challenges:
  - id: c1
    description: "Add a ProgressIndicator bound to the same slider value"
    assertion: containsNodeOfType(ProgressIndicator)
nextLesson: 039-spinner
---

# Slider and ProgressBar

## Slider

`Slider(min, max, value)` — a draggable track. Key properties:
`valueProperty()`, `majorTickUnit`, `showTickLabels`, `snapToTicks`.

## ProgressBar

Displays a 0.0–1.0 progress value as a filled bar. `-1.0` means
indeterminate (animated stripe).

## ProgressIndicator

Circular version. Set `setProgress(-1)` for the spinning animation.

## Binding

The `bind()` call keeps `bar.progressProperty` equal to
`slider.valueProperty` automatically — no listener code needed.

## Challenge

Add a `ProgressIndicator` and bind its `progressProperty()` to the
same `slider.valueProperty()`.
