---
id: 058-property-binding-with-converter
tier: properties
order: 58
title: "Binding with StringConverter"
objectives:
  - "Convert between String and a domain type in a binding"
  - "Use Bindings.bindBidirectional with a converter"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      IntegerProperty age = new SimpleIntegerProperty(25);
      TextField field = new TextField();

      StringConverter<Number> converter = new StringConverter<>() {
          public String toString(Number n) { return n == null ? "" : n.intValue() + ""; }
          public Number fromString(String s) {
              try { return Integer.parseInt(s.trim()); }
              catch (NumberFormatException e) { return age.get(); }
          }
      };
      Bindings.bindBidirectional(field.textProperty(), age, converter);

      Label display = new Label();
      display.textProperty().bind(age.asString("Age: %d"));
      return new VBox(10, field, display);
  }
challenges:
  - id: c1
    description: "Clamp the parsed value to 0-120 in the fromString converter"
    assertion: containsNodeOfType(TextField)
nextLesson: 059-style-class-binding
---

# Binding with StringConverter

`StringConverter<T>` bridges a domain type and its `String`
representation for use in controls (`ComboBox`, `DatePicker`, etc.)
and bidirectional bindings.

```java
Bindings.bindBidirectional(
    textField.textProperty(),
    someProperty,
    myConverter);
```

This keeps a `String` property (the text field) and a typed property
(e.g. `IntegerProperty`) in sync through the converter.

## Challenge

In `fromString`, clamp the result:
`return Math.max(0, Math.min(120, Integer.parseInt(s.trim())))`.
