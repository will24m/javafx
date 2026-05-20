---
id: 035-tableview
tier: controls
order: 35
title: "TableView"
objectives:
  - "Display tabular data with TableView and TableColumn"
  - "Use PropertyValueFactory for simple POJO binding"
estimatedMinutes: 12
starterSnippet: |
  public static Parent build() {
      record Person(String name, int age) {}

      TableView<Person> table = new TableView<>();
      TableColumn<Person, String> nameCol = new TableColumn<>("Name");
      nameCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().name()));

      TableColumn<Person, Number> ageCol = new TableColumn<>("Age");
      ageCol.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().age()));

      table.getColumns().addAll(nameCol, ageCol);
      table.getItems().addAll(new Person("Alice", 30), new Person("Bob", 25));
      return table;
  }
challenges:
  - id: c1
    description: "Add a third row: Person('Carol', 28)"
    assertion: containsNodeOfType(TableView)
nextLesson: 036-textfield-validation
---

# TableView

`TableView<T>` renders a 2-D table from a list of model objects.
Each `TableColumn<T, V>` maps one field of `T` to a cell value.

## Cell value factories

```java
col.setCellValueFactory(cellData ->
    new SimpleStringProperty(cellData.getValue().getName()));
```

## Editing

```java
table.setEditable(true);
col.setCellFactory(TextFieldTableCell.forTableColumn());
col.setOnEditCommit(e -> e.getRowValue().setName(e.getNewValue()));
```

## Sorting and selection

Clicking a column header sorts by that column. Multiple-column sort
with Shift-click.

## Challenge

Add a third row `new Person("Carol", 28)` to the table.
