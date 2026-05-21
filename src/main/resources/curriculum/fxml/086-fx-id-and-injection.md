---
id: 086-fx-id-and-injection
tier: fxml
order: 86
title: "fx:id and @FXML Injection"
objectives:
  - "Use fx:id to name nodes in FXML"
  - "Inject them into the controller with @FXML"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      // Demonstrates injection: every node that has an fx:id in FXML
      // gets assigned to the matching @FXML field automatically.
      TextField nameField = new TextField();
      nameField.setId("nameField");          // equivalent of fx:id="nameField"
      nameField.setPromptText("Enter name");

      Label greeting = new Label("(waiting)");
      greeting.setId("greeting");

      Button go = new Button("Greet");
      go.setOnAction(e -> greeting.setText("Hello, " + nameField.getText() + "!"));

      return new VBox(10, nameField, go, greeting);
  }
challenges:
  - id: c1
    description: "Add a second Label with id 'farewell' that says 'Goodbye!' when the button is pressed"
    assertion: containsNodeOfType(Label)
nextLesson: 087-fxml-includes
---

# fx:id and @FXML Injection

`fx:id` in FXML names a node. `FXMLLoader` finds controller fields
annotated `@FXML` whose names match and injects the node reference:

```xml
<TextField fx:id="nameField" promptText="Enter name"/>
<Label fx:id="greeting"/>
<Button text="Greet" onAction="#handleGreet"/>
```

```java
@FXML private TextField nameField;  // ← injected by FXMLLoader
@FXML private Label greeting;

@FXML
private void handleGreet() {
    greeting.setText("Hello, " + nameField.getText() + "!");
}
```

## Rules

- The field name must exactly match the `fx:id` value
- Fields may be `private` — `FXMLLoader` uses reflection
- Fields are `null` until `initialize()` fires; never use them in
  the constructor

## The snippet

`Node.setId()` is the programmatic equivalent — it sets the CSS ID,
**not** the same as `fx:id`, but it shows the same naming concept.

## Challenge

Add a second `Label` with `id = "farewell"` that reads `"Goodbye!"`
when the button is pressed (add a second call inside the action handler).
