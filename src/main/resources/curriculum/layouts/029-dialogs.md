---
id: 029-dialogs
tier: layouts
order: 29
title: "Dialogs and Alerts"
objectives:
  - "Show an Alert and react to which button was clicked"
  - "Collect text input with TextInputDialog"
  - "Offer a list of choices with ChoiceDialog"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      Label result = new Label("No dialog opened yet");
      result.setWrapText(true);

      Button confirmBtn = new Button("Confirm…");
      confirmBtn.setOnAction(e -> {
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
          alert.setTitle("Delete?");
          alert.setHeaderText("Delete this item?");
          alert.setContentText("This cannot be undone.");
          alert.showAndWait()
               .filter(btn -> btn == ButtonType.OK)
               .ifPresentOrElse(
                   btn -> result.setText("You clicked OK — deleting."),
                   ()  -> result.setText("Cancelled."));
      });

      Button askName = new Button("Ask name…");
      askName.setOnAction(e -> {
          TextInputDialog dlg = new TextInputDialog("Anonymous");
          dlg.setHeaderText("What is your name?");
          dlg.showAndWait().ifPresent(name ->
              result.setText("Hello, " + name + "!"));
      });

      Button pickColor = new Button("Pick color…");
      pickColor.setOnAction(e -> {
          ChoiceDialog<String> dlg = new ChoiceDialog<>("Blue",
              "Red", "Green", "Blue", "Orange");
          dlg.setHeaderText("Choose a color:");
          dlg.showAndWait().ifPresent(color ->
              result.setText("You picked: " + color));
      });

      VBox root = new VBox(12,
          new HBox(8, confirmBtn, askName, pickColor),
          result);
      root.setPadding(new Insets(16));
      return root;
  }
challenges:
  - id: c1
    description: "Add a fourth button that shows an INFORMATION Alert with text 'Hello!'"
    assertion: 'containsLabeledWithText(text="No dialog opened yet")'
nextLesson: 030-context-menu
---

# Dialogs and Alerts

`Dialog<R>` is the JavaFX modal popup. Three subclasses cover most
needs without writing any custom dialog code.

## Alert

The simplest dialog: a message, an icon, and a row of buttons.

```java
new Alert(AlertType.INFORMATION, "Saved!").showAndWait();
new Alert(AlertType.ERROR,       "Disk full").showAndWait();
new Alert(AlertType.WARNING,     "Unsaved changes").showAndWait();
new Alert(AlertType.CONFIRMATION, "Are you sure?").showAndWait()
   .filter(r -> r == ButtonType.OK)
   .ifPresent(r -> proceed());
```

`AlertType` values: `INFORMATION`, `WARNING`, `ERROR`, `CONFIRMATION`,
`NONE`. Each picks a default icon and button set.

For more control:

```java
Alert a = new Alert(AlertType.CONFIRMATION);
a.setTitle("Delete?");                         // window title
a.setHeaderText("Delete this item?");          // bold header text
a.setContentText("This cannot be undone.");    // body text
a.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
```

## TextInputDialog

Asks for one line of text. Returns `Optional<String>` — `empty` if the
user cancelled, otherwise the entered string.

```java
TextInputDialog dlg = new TextInputDialog("default value");
dlg.setHeaderText("Enter your name:");
Optional<String> result = dlg.showAndWait();
```

## ChoiceDialog

Lets the user pick one item from a list (rendered as a combo box).

```java
ChoiceDialog<String> dlg = new ChoiceDialog<>(
    "Medium",                                  // initial selection
    "Small", "Medium", "Large");
dlg.setHeaderText("Pick a size:");
Optional<String> choice = dlg.showAndWait();
```

## showAndWait vs show

- **`showAndWait()`** blocks the FX thread until the dialog closes,
  then returns the result. Use this 95% of the time.
- **`show()`** displays the dialog non-modally and returns immediately.
  Use this for modeless tool windows. You'll need to listen for the
  `setOnHidden` event to react when the user closes it.

`showAndWait()` does not freeze your UI — the FX thread spins a nested
event loop, so animations, listeners, and other UI keep ticking.

## Owner window

By default a dialog has no parent window — on some platforms it shows
no title-bar icon. To anchor it to the current window:

```java
dlg.initOwner(myButton.getScene().getWindow());
dlg.initModality(Modality.WINDOW_MODAL);       // blocks just that window
```

## Challenge

Add a fourth button "Info…" that shows
`new Alert(AlertType.INFORMATION, "Hello!").showAndWait()`.
