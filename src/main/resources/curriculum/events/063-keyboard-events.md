---
id: 063-keyboard-events
tier: events
order: 63
title: "Keyboard Events and KeyCombination"
objectives:
  - "Handle key presses with setOnKeyPressed"
  - "Define keyboard shortcuts with KeyCombination"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      Label log = new Label("Press a key (click pane first)");
      StackPane pane = new StackPane(log);
      pane.setFocusTraversable(true);
      pane.setOnKeyPressed(e -> log.setText(
          "Key: " + e.getCode() + "  text: " + e.getText()
          + "  shift=" + e.isShiftDown()));
      pane.requestFocus();
      return pane;
  }
challenges:
  - id: c1
    description: "React specifically to Ctrl+S by checking KeyCombination.keyCombination('Ctrl+S').match(e)"
    assertion: containsNodeOfType(Label)
nextLesson: 064-drag-and-drop
---

# Keyboard Events

`KeyEvent` fires on `KEY_PRESSED`, `KEY_RELEASED`, and `KEY_TYPED`.

## Key properties

```java
e.getCode()        // KeyCode enum (e.g. KeyCode.ENTER, KeyCode.A)
e.getText()        // character string (KEY_TYPED only)
e.isControlDown()  // Ctrl held?
e.isShiftDown()    // Shift held?
e.isAltDown()
e.isMetaDown()     // Cmd on macOS
```

## KeyCombination

```java
KeyCombination ctrlS = KeyCombination.keyCombination("Ctrl+S");
if (ctrlS.match(e)) { ... }
```

Or attach directly to a Scene:

```java
scene.getAccelerators().put(ctrlS, () -> save());
```

## Challenge

Inside `setOnKeyPressed`, check `KeyCombination.keyCombination("Ctrl+S").match(e)`
and set the label to "Saved!" when true.
