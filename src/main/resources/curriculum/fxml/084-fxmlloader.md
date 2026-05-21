---
id: 084-fxmlloader
tier: fxml
order: 84
title: "FXMLLoader"
objectives:
  - "Load an FXML file at runtime with FXMLLoader"
  - "Understand getResource() vs getResourceAsStream()"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      // Demonstrates the FXMLLoader pattern programmatically.
      // In real code you would call:
      //   FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Main.fxml"));
      //   Parent root = loader.load();
      Label note = new Label("FXMLLoader.load() returns the root node.");
      note.setWrapText(true);
      Label path = new Label("Resource path: /views/Main.fxml");
      path.setStyle("-fx-text-fill: steelblue;");
      return new VBox(10, note, path);
  }
challenges:
  - id: c1
    description: "Add a Label that shows the text 'Controller: MyController'"
    assertion: 'containsLabeledWithText(text="Controller: MyController")'
nextLesson: 085-controller-wiring
---

# FXMLLoader

`FXMLLoader` is the bridge between an `.fxml` file and your running
JavaFX application:

```java
FXMLLoader loader = new FXMLLoader(
        getClass().getResource("/views/Main.fxml"));
Parent root = loader.load();          // parses XML, constructs nodes
MyController ctrl = loader.getController(); // typed access to the controller
```

## Resource paths

FXML files live in `src/main/resources/` and are referenced with an
absolute classpath path starting with `/`:

```
src/main/resources/views/Main.fxml  →  /views/Main.fxml
```

Use `getClass().getResource(path)` — never string-concatenate paths.

## Calling `load()` twice

Each call to `loader.load()` produces a **new** scene graph. If you
want multiple independent instances of the same FXML, call `load()`
again; the controller is also a fresh instance.

## Challenge

Add a `Label("Controller: MyController")` to the snippet.
