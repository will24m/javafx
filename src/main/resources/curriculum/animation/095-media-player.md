---
id: 095-media-player
tier: animation
order: 95
title: "Media and MediaPlayer"
objectives:
  - "Load and play audio/video with Media and MediaPlayer"
  - "Display video with MediaView"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      // MediaPlayer requires a real media URL — we simulate the UI layout here.
      Label note = new Label("MediaPlayer plays audio and video from a URL or file.");
      note.setWrapText(true);

      Label code = new Label(
          "Media m = new Media(\"https://example.com/clip.mp4\");\n" +
          "MediaPlayer mp = new MediaPlayer(m);\n" +
          "MediaView mv = new MediaView(mp);\n" +
          "mp.play();");
      code.setStyle("-fx-font-family: monospace; -fx-font-size: 12px; -fx-text-fill: #6ab0de;");

      Button playBtn = new Button("▶  Play");
      Button pauseBtn = new Button("⏸  Pause");
      // In a real snippet: playBtn.setOnAction(e -> mp.play());

      javafx.scene.layout.HBox controls = new javafx.scene.layout.HBox(8, playBtn, pauseBtn);
      return new VBox(12, note, code, controls);
  }
challenges:
  - id: c1
    description: "Add a third Button '⏹ Stop' to the HBox"
    assertion: containsLabeledWithText(text="⏹ Stop")
nextLesson: 096-canvas-animation
---

# Media and MediaPlayer

JavaFX can play audio (MP3, AAC) and video (MP4/H.264) natively.

## Core classes

```java
// 1. Declare the media source
Media media = new Media("https://example.com/clip.mp4");

// 2. Create the player
MediaPlayer player = new MediaPlayer(media);
player.setAutoPlay(true);
player.setVolume(0.8);      // 0.0–1.0

// 3. Display video (omit for audio-only)
MediaView view = new MediaView(player);
view.setFitWidth(640);
view.setPreserveRatio(true);
```

## Playback control

```java
player.play();
player.pause();
player.stop();
player.seek(Duration.seconds(30));
player.currentTimeProperty().addListener(...); // progress bar
```

## Platform support

Media playback depends on native codecs. On macOS and Windows MP4/H.264
works out of the box. Linux requires GStreamer.

## Challenge

Add a `Button("⏹ Stop")` to the `HBox` controls.
