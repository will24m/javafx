---
id: 098-app-icons
tier: packaging
order: 98
title: "App Icons"
objectives:
  - "Prepare icon files for macOS (.icns), Windows (.ico), and Linux (.png)"
  - "Wire the icon into jpackage and the Stage"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      javafx.scene.shape.Rectangle bg = new javafx.scene.shape.Rectangle(80, 80, Color.STEELBLUE);
      bg.setArcWidth(16); bg.setArcHeight(16);
      javafx.scene.text.Text letter = new javafx.scene.text.Text("FX");
      letter.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 28));
      letter.setFill(Color.WHITE);
      StackPane icon = new StackPane(bg, letter);

      Label caption = new Label("A simple icon: coloured rounded square + text.");
      caption.setWrapText(true);
      return new VBox(12, icon, caption);
  }
challenges:
  - id: c1
    description: "Change the icon background color to Color.DARKORANGE"
    assertion: containsNodeOfType(javafx.scene.text.Text)
nextLesson: 099-native-installers
---

# App Icons

Native bundles need platform-specific icon files:

| Platform | Format | Size |
|---|---|---|
| macOS | `.icns` (Apple Icon) | 512×512 minimum |
| Windows | `.ico` (multi-resolution) | 16, 32, 48, 256 px layers |
| Linux | `.png` | 48×48 or 512×512 |

## Creating icon files

1. Start with a **1024×1024 PNG** (Figma, Sketch, or Inkscape)
2. macOS: use `iconutil` or the free app **Image2Icon**
3. Windows: use **IcoFX** or `convert` (ImageMagick)

## Wiring into the Stage

```java
// In Application.start():
Image icon = new Image(
    getClass().getResourceAsStream("/icons/app-icon.png"));
primaryStage.getIcons().add(icon);
```

## Wiring into jpackage (Gradle)

```kotlin
runtime {
    jpackage {
        icon = "src/main/resources/icons/app.icns"  // macOS
        // icon = "src/main/resources/icons/app.ico" // Windows
    }
}
```

## Challenge

Change `Color.STEELBLUE` to `Color.DARKORANGE` in the icon preview.
