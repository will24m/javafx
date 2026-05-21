---
id: 085-controller-wiring
tier: fxml
order: 85
title: "Controller Wiring"
objectives:
  - "Declare a controller class in FXML with fx:controller"
  - "Understand the controller lifecycle: initialize() called after load()"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      // Simulates what a controller's initialize() method does.
      Label status = new Label("Initialising...");
      Button btn = new Button("Simulate action");

      // initialize() equivalent — runs right after FXMLLoader.load()
      status.setText("Ready (initialize() has run)");

      btn.setOnAction(e -> status.setText("Action handled by @FXML method"));
      return new VBox(10, status, btn);
  }
challenges:
  - id: c1
    description: "Change the button label to 'Trigger' and update the action to set status to 'Triggered!'"
    assertion: containsLabeledWithText(text="Trigger")
nextLesson: 086-fx-id-and-injection
---

# Controller Wiring

In an FXML-backed app each view has a **controller** class, declared in
the root element:

```xml
<VBox xmlns:fx="http://javafx.com/fxml"
      fx:controller="com.example.MainController">
```

## The controller class

```java
public class MainController {
    @FXML private Label status;
    @FXML private Button btn;

    @FXML
    public void initialize() {
        // Called automatically after all @FXML fields are injected.
        status.setText("Ready");
    }

    @FXML
    private void handleClick(ActionEvent e) {
        status.setText("Clicked!");
    }
}
```

## Lifecycle order

1. FXML parsed; nodes constructed
2. `@FXML` fields injected into controller instance
3. `initialize()` called
4. `loader.getController()` returns the fully-wired controller

## Challenge

Change the button's text to `"Trigger"` and the action to set `status`
to `"Triggered!"`.
