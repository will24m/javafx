---
id: 029-dialogs
tier: layouts
order: 29
title: "Dialogs and Alerts"
objectives:
  - "Show an Alert dialog and read the user's response"
  - "Use TextInputDialog and ChoiceDialog"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      Label result = new Label("No answer yet");
      Button ask = new Button("Ask");
      ask.setOnAction(e -> {
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Proceed?");
          alert.showAndWait().ifPresent(btn -> result.setText(btn.getText()));
      });
      return new VBox(10, ask, result);
  }
challenges:
  - id: c1
    description: "Change to a TextInputDialog that asks for the user's name"
    assertion: containsNodeOfType(Button)
nextLesson: 030-context-menu
---

# Dialogs and Alerts

`Alert` is the quickest way to show a modal message or confirmation.

```java
new Alert(AlertType.INFORMATION, "message").showAndWait();
new Alert(AlertType.ERROR, "Something went wrong").showAndWait();
new Alert(AlertType.CONFIRMATION, "Are you sure?").showAndWait()
    .filter(r -> r == ButtonType.OK)
    .ifPresent(r -> doSomething());
```

## TextInputDialog

```java
TextInputDialog dialog = new TextInputDialog("default");
dialog.setHeaderText("Enter your name:");
Optional<String> result = dialog.showAndWait();
```

## ChoiceDialog

```java
ChoiceDialog<String> d = new ChoiceDialog<>("Option A", "Option A", "Option B");
Optional<String> choice = d.showAndWait();
```

## Challenge

Replace the `Alert` with a `TextInputDialog` that asks "What is your
name?" and shows the entered text in `result`.
