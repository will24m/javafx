---
id: 100-code-signing
tier: packaging
order: 100
title: "Code Signing and Notarization"
objectives:
  - "Understand why code signing is required on macOS and Windows"
  - "Pass signing flags to jpackage"
estimatedMinutes: 12
starterSnippet: |
  public static Parent build() {
      Label heading = new Label("Lesson 100: Code Signing");
      heading.setStyle("-fx-font-size:20px; -fx-font-weight:bold;");

      Label sub = new Label("You've reached the end of the curriculum!");
      sub.setStyle("-fx-font-size:14px; -fx-text-fill:#6ab0de;");

      Label body = new Label(
          "Code signing ties your app to a verified identity.\n" +
          "Without it:\n" +
          "  • macOS shows a Gatekeeper warning on first run\n" +
          "  • Windows SmartScreen blocks unsigned installers\n\n" +
          "With an Apple Developer ID certificate jpackage can\n" +
          "sign and notarize in one step via --mac-sign.");
      body.setWrapText(true);
      body.setStyle("-fx-text-fill:#c8c9cc;");

      javafx.scene.shape.Rectangle divider = new javafx.scene.shape.Rectangle(300, 1, Color.web("#3a3c40"));

      Label congrats = new Label("🎉 Build something great with JavaFX!");
      congrats.setStyle("-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:#6dbe6d;");

      return new VBox(12, heading, sub, divider, body, congrats);
  }
challenges:
  - id: c1
    description: "Change the congratulations message to include your own name"
    assertion: containsNodeOfType(Label)
nextLesson: null
---

# Code Signing and Notarization

## macOS

Apple requires apps distributed outside the App Store to be **signed**
with a Developer ID certificate and **notarized** (uploaded to Apple's
scan service). Unsigned apps trigger Gatekeeper.

```kotlin
runtime {
    jpackage {
        mac {
            sign = true
            signingKeyUserName = "Your Name (TEAMID)"
            // notarization uses xcrun notarytool under the hood
        }
    }
}
```

## Windows

Microsoft SmartScreen warns on unsigned `.msi`/`.exe` files. Signing
requires an EV Code Signing certificate (from DigiCert, Sectigo, etc.).

```kotlin
jpackage {
    win {
        // Pass signing args via --win-per-user-install, etc.
        // Actual signing often done post-build with signtool.exe
    }
}
```

## Self-signed (development only)

```bash
# macOS — create a self-signed cert in Keychain for testing only
security create-keychain ...
```

Self-signed certs still trigger Gatekeeper for end-users; only use
for local testing.

## You made it!

This is lesson 100 — the end of the JavaFX Tutor curriculum. You now
understand every major layer of the JavaFX platform, from `Stage` to
`jpackage`. The best next step: **build something real** and bring all
of it together.

## Challenge

Change the congratulations message text to include your own name.
