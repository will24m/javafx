---
id: 097-jpackage-basics
tier: packaging
order: 97
title: "jpackage Basics"
objectives:
  - "Understand what jpackage produces"
  - "Configure the org.beryx.runtime Gradle plugin for jpackage"
estimatedMinutes: 12
starterSnippet: |
  public static Parent build() {
      Label heading = new Label("Packaging with jpackage");
      heading.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");

      Label desc = new Label(
          "jpackage bundles your app + a JRE into a native installer.\n" +
          "On macOS: .app bundle and .dmg\n" +
          "On Windows: .msi or .exe\n" +
          "On Linux: .deb or .rpm");
      desc.setWrapText(true);
      desc.setStyle("-fx-text-fill:#c8c9cc;");

      return new VBox(12, heading, desc);
  }
challenges:
  - id: c1
    description: "Add a Label listing your target platform (macOS, Windows, or Linux)"
    assertion: containsNodeOfType(Label)
nextLesson: 098-app-icons
---

# jpackage Basics

`jpackage` (bundled with JDK 14+) creates a native application package
that includes your JAR and a trimmed JRE — users don't need Java
installed.

## Gradle config with `org.beryx.runtime`

```kotlin
// build.gradle.kts
plugins {
    id("org.beryx.runtime") version "1.13.1"
}

runtime {
    options.addAll("--strip-debug", "--compress", "2", "--no-header-files",
                   "--no-man-pages")
    modules.addAll("java.desktop", "java.logging", "javafx.controls")

    jpackage {
        appVersion = "1.0.0"
        vendor = "My Company"
        imageName = "JavaFXTutor"
        installerName = "JavaFXTutor"
        // macOS specific:
        mac {
            packageIdentifier = "com.jfxtutor.app"
        }
    }
}
```

```bash
./gradlew jpackage
# Output: build/jpackage/JavaFXTutor.app  (macOS)
#         build/jpackage/JavaFXTutor.dmg
```

## No JPMS required

The `org.beryx.runtime` plugin works with plain classpath apps (no
`module-info.java`). It bundles the full JRE when `--add-modules` cannot
be determined statically.

## Challenge

Add a `Label` naming your development platform (macOS / Windows / Linux).
