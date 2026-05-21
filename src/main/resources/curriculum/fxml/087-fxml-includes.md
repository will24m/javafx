---
id: 087-fxml-includes
tier: fxml
order: 87
title: "FXML Includes"
objectives:
  - "Compose views with <fx:include>"
  - "Access the nested controller from the parent controller"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      // Simulates composing views: a header pane and a content pane
      // that would each be separate .fxml files in a real project.
      Label header = new Label("Header (from header.fxml)");
      header.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-padding:8;");
      header.setMaxWidth(Double.MAX_VALUE);
      header.setStyle("-fx-background-color:#2b2d30; -fx-text-fill:#f0f1f3; -fx-font-size:16px; -fx-padding:8;");

      Label content = new Label("Content area (from content.fxml)");
      content.setPadding(new javafx.geometry.Insets(16));

      return new VBox(header, content);
  }
challenges:
  - id: c1
    description: "Add a third Label representing a footer region that reads 'Footer'"
    assertion: containsLabeledWithText(text="Footer")
nextLesson: 088-custom-fxml-controls
---

# FXML Includes

Large UIs are split into multiple FXML files and composed with
`<fx:include>`:

```xml
<!-- Main.fxml -->
<VBox xmlns:fx="http://javafx.com/fxml"
      fx:controller="com.example.MainController">
    <fx:include fx:id="header" source="Header.fxml"/>
    <fx:include fx:id="content" source="Content.fxml"/>
</VBox>
```

```java
public class MainController {
    @FXML private HeaderController headerController;  // auto-injected
    @FXML private ContentController contentController;

    @FXML
    public void initialize() {
        // both sub-controllers are ready here
        headerController.setTitle("My App");
    }
}
```

## Naming convention

`FXMLLoader` injects the nested controller under the name
`<fx:id>Controller`. If `fx:id="header"`, the field is
`headerController`.

## Benefits of splitting

- Each view unit is independently editable in Scene Builder
- Controllers stay small and focused
- Sub-views can be reused across parent views

## Challenge

Add a third `Label` representing a footer with the text `"Footer"`.
