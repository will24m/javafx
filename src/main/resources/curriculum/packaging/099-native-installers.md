---
id: 099-native-installers
tier: packaging
order: 99
title: "Native Installers"
objectives:
  - "Generate a .dmg (macOS) or .msi/.exe (Windows) with jpackage"
  - "Understand installer types and when to choose each"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      Label heading = new Label("Native installer types");
      heading.setStyle("-fx-font-size:16px; -fx-font-weight:bold;");

      String[][] rows = {
          {"macOS", ".app", "App bundle — drag to /Applications"},
          {"macOS", ".dmg", "Disk image — standard distribution format"},
          {"Windows", ".msi", "Windows Installer — silent install, IT friendly"},
          {"Windows", ".exe", "Self-extracting — simpler, user friendly"},
          {"Linux", ".deb", "Debian/Ubuntu package manager"},
          {"Linux", ".rpm", "RHEL/Fedora package manager"},
      };

      javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
      grid.setHgap(12); grid.setVgap(4);
      grid.setPadding(new javafx.geometry.Insets(8));
      for (int i = 0; i < rows.length; i++) {
          for (int j = 0; j < rows[i].length; j++) {
              Label cell = new Label(rows[i][j]);
              if (j == 0) cell.setStyle("-fx-font-weight:bold;");
              grid.add(cell, j, i);
          }
      }
      return new VBox(10, heading, grid);
  }
challenges:
  - id: c1
    description: "Add a Label below the grid that reads 'Build with: ./gradlew jpackage'"
    assertion: 'containsLabeledWithText(text="Build with: ./gradlew jpackage")'
nextLesson: 100-code-signing
---

# Native Installers

## macOS

```bash
./gradlew jpackage
# Produces build/jpackage/JavaFXTutor-1.0.0.dmg
```

jpackage calls the system's `pkgbuild`/`productbuild` under the hood.
The app is placed at `/Applications/JavaFXTutor.app` after installation.

## Windows

Requires the **WiX Toolset** (v3.x, free) to build `.msi`.
jpackage looks for `candle.exe` and `light.exe` on the PATH.

```bat
.\gradlew.bat jpackage
```

## Linux

Requires `dpkg` (for .deb) or `rpm` (for .rpm) on the build machine.

## Choosing installer type

```kotlin
runtime {
    jpackage {
        installerType = "dmg"   // or "msi", "deb", "rpm", "exe", "pkg"
    }
}
```

## Challenge

Add a `Label("Build with: ./gradlew jpackage")` below the grid.
