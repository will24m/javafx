---
id: 019-gridpane
tier: layouts
order: 19
title: "GridPane"
objectives:
  - "Place nodes at specific (column, row) positions"
  - "Span cells with rowspan and colspan"
  - "Constrain column widths and row heights with ColumnConstraints / RowConstraints"
estimatedMinutes: 12
starterSnippet: |
  public static Parent build() {
      GridPane grid = new GridPane();
      grid.setHgap(8);
      grid.setVgap(8);
      grid.setPadding(new Insets(16));

      // First column: labels (right-aligned, fixed natural width)
      javafx.scene.layout.ColumnConstraints labels =
          new javafx.scene.layout.ColumnConstraints();
      labels.setHalignment(javafx.geometry.HPos.RIGHT);
      // Second column: inputs (grow to fill available width)
      javafx.scene.layout.ColumnConstraints inputs =
          new javafx.scene.layout.ColumnConstraints();
      inputs.setHgrow(Priority.ALWAYS);
      grid.getColumnConstraints().addAll(labels, inputs);

      grid.add(new Label("Name:"),    0, 0);
      grid.add(new TextField(),       1, 0);
      grid.add(new Label("Email:"),   0, 1);
      grid.add(new TextField(),       1, 1);
      grid.add(new Label("Bio:"),     0, 2);
      TextArea bio = new TextArea(); bio.setPrefRowCount(3);
      grid.add(bio, 1, 2);

      Button submit = new Button("Submit");
      submit.setMaxWidth(Double.MAX_VALUE);
      // colSpan = 2 so the button spans both columns
      grid.add(submit, 0, 3, 2, 1);

      return grid;
  }
challenges:
  - id: c1
    description: "Add a third row (index 4) with a Label('Phone:') and a TextField"
    assertion: 'containsLabeledWithText(text="Phone:")'
nextLesson: 020-anchorpane
---

# GridPane

`GridPane` arranges children in a grid of rows and columns. Children
are placed at explicit `(column, row)` indices — empty cells are fine
and cost nothing. Row heights and column widths default to the largest
child in that row or column.

## Adding children

```java
grid.add(node, columnIndex, rowIndex);
grid.add(node, col, row, colSpan, rowSpan);   // span multiple cells
```

`colSpan` / `rowSpan` make a single child cover multiple cells — useful
for headings that span a full row or a submit button below a form.

## Gap and padding

```java
grid.setHgap(8);                              // gap between columns
grid.setVgap(8);                              // gap between rows
grid.setPadding(new Insets(16));              // outer padding
```

## Column and row constraints

By default columns auto-size to their largest child. To control sizing
explicitly:

```java
ColumnConstraints col = new ColumnConstraints();
col.setMinWidth(80);
col.setPrefWidth(120);
col.setHgrow(Priority.ALWAYS);                // grow with extra space
col.setHalignment(HPos.RIGHT);                // align cell contents
grid.getColumnConstraints().add(col);
```

The starter snippet uses two `ColumnConstraints` — the labels column is
right-aligned at its natural width; the inputs column grows to fill the
rest of the pane. Resize the preview to see the inputs widen.

## Alignment inside a cell

Cells are larger than their child by default. To position the child:

```java
GridPane.setHalignment(node, HPos.CENTER);
GridPane.setValignment(node, VPos.TOP);
GridPane.setMargin(node, new Insets(4));
```

## Challenge

Add a fourth field. Insert `Label("Phone:")` at `(0, 3)`, a `TextField`
at `(1, 3)`, and move the submit button down to row 4.
