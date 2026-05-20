---
id: 037-datepicker
tier: controls
order: 37
title: "DatePicker"
objectives:
  - "Let users pick a date with DatePicker"
  - "Read and format the selected LocalDate"
estimatedMinutes: 6
starterSnippet: |
  public static Parent build() {
      DatePicker picker = new DatePicker(java.time.LocalDate.now());
      Label result = new Label();
      picker.valueProperty().addListener((obs, old, date) ->
          result.setText(date != null ? date.toString() : "none"));
      result.setText(picker.getValue().toString());
      return new VBox(10, picker, result);
  }
challenges:
  - id: c1
    description: "Format the date as 'Month d, yyyy' using DateTimeFormatter"
    assertion: containsNodeOfType(DatePicker)
nextLesson: 038-slider-progressbar
---

# DatePicker

`DatePicker` shows a text field with a calendar popup. Its value is a
`java.time.LocalDate`.

## Restricting dates

```java
picker.setDayCellFactory(p -> new DateCell() {
    @Override public void updateItem(LocalDate date, boolean empty) {
        super.updateItem(date, empty);
        setDisable(date.isBefore(LocalDate.now()));
    }
});
```

## Formatting the display

```java
picker.setConverter(new LocalDateStringConverter(
    DateTimeFormatter.ofPattern("dd/MM/yyyy"),
    DateTimeFormatter.ofPattern("dd/MM/yyyy")));
```

## Challenge

Format the label using
`DateTimeFormatter.ofPattern("MMMM d, yyyy").format(date)`.
