---
id: 019-gridpane
tier: layouts
order: 19
title: "GridPane"
objectives:
  - "Place nodes at specific row/column positions"
  - "Span cells with rowspan and colspan"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      GridPane grid = new GridPane();
      grid.setHgap(8);
      grid.setVgap(8);
      grid.setPadding(new Insets(12));

      grid.add(new Label("Name:"), 0, 0);
      grid.add(new TextField(), 1, 0);
      grid.add(new Label("Email:"), 0, 1);
      grid.add(new TextField(), 1, 1);
      grid.add(new Button("Submit"), 1, 2);
      return grid;
  }
challenges:
  - id: c1
    description: "Make the Submit button span 2 columns"
    assertion: containsNodeOfType(Button)
nextLesson: 020-anchorpane
---

# GridPane

`GridPane` arranges children in rows and columns. Cells can be empty.
Row heights and column widths are determined by the largest child in
that row/column (or you can set constraints explicitly).

## Adding children

```java
grid.add(node, columnIndex, rowIndex);
grid.add(node, col, row, colSpan, rowSpan);
```

## Gap and padding

`setHgap(n)` — horizontal gap between columns
`setVgap(n)` — vertical gap between rows
`setPadding(new Insets(top, right, bottom, left))` — outer padding

## Challenge

Change the `Submit` button to span two columns:
`grid.add(button, 0, 2, 2, 1)`.
