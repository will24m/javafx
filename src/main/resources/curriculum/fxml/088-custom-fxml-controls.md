---
id: 088-custom-fxml-controls
tier: fxml
order: 88
title: "Custom FXML Controls"
objectives:
  - "Build a reusable control backed by an FXML file"
  - "Use fx:root to make a class both the root node and the controller"
estimatedMinutes: 12
starterSnippet: |
  public static Parent build() {
      // Simulates a custom "BadgeLabel" control: an HBox wrapping a
      // coloured circle and a Label — packaged as a reusable component.
      javafx.scene.shape.Circle dot = new javafx.scene.shape.Circle(6, Color.LIMEGREEN);
      Label text = new Label("Online");
      javafx.scene.layout.HBox badge = new javafx.scene.layout.HBox(6, dot, text);
      badge.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
      badge.setPadding(new javafx.geometry.Insets(4, 8, 4, 8));
      badge.setStyle("-fx-background-color:#2a3a2a; -fx-background-radius:12;");
      return new StackPane(badge);
  }
challenges:
  - id: c1
    description: "Change the dot color to Color.TOMATO and the label to 'Offline'"
    assertion: containsLabeledWithText(text="Offline")
nextLesson: 089-scene-builder-workflow
---

# Custom FXML Controls

A **custom control** packages an FXML file and a Java class into a single
reusable unit. The trick is `fx:root`:

```xml
<!-- BadgeLabel.fxml -->
<fx:root type="HBox" xmlns:fx="http://javafx.com/fxml">
    <Circle fx:id="dot" radius="6"/>
    <Label fx:id="label" text="Status"/>
</fx:root>
```

```java
public class BadgeLabel extends HBox {
    @FXML private Circle dot;
    @FXML private Label label;

    public BadgeLabel() {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/controls/BadgeLabel.fxml"));
        loader.setRoot(this);       // ← this object IS the root
        loader.setController(this); // ← this object IS the controller
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setStatus(String text, Color color) {
        label.setText(text);
        dot.setFill(color);
    }
}
```

Using it in another FXML:
```xml
<BadgeLabel fx:id="status"/>
```

## Challenge

Change the dot color to `Color.TOMATO` and the label text to `"Offline"`.
