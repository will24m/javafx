---
id: 090-programmatic-vs-fxml
tier: fxml
order: 90
title: "Programmatic vs. FXML"
objectives:
  - "Apply the right tool for static vs. dynamic views"
  - "Know when each approach is preferable"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      // Programmatic: great for data-driven lists of identical rows.
      VBox rows = new VBox(4);
      String[] items = {"Apple", "Banana", "Cherry", "Date", "Elderberry"};
      for (String item : items) {
          javafx.scene.layout.HBox row = new javafx.scene.layout.HBox(8);
          row.setPadding(new javafx.geometry.Insets(4, 8, 4, 8));
          Label name = new Label(item);
          name.setMinWidth(120);
          Button pick = new Button("Pick");
          pick.setOnAction(e -> System.out.println("Picked: " + item));
          row.getChildren().addAll(name, pick);
          rows.getChildren().add(row);
      }
      return new javafx.scene.control.ScrollPane(rows);
  }
challenges:
  - id: c1
    description: "Add a Label at the top of the VBox that reads 'Fruit list'"
    assertion: containsLabeledWithText(text="Fruit list")
nextLesson: 091-timeline-keyframe
---

# Programmatic vs. FXML

Neither approach is universally better. Choose based on the nature of
the view:

| Situation | Prefer |
|---|---|
| Fixed, designer-owned layout | FXML |
| Data-driven repeated rows | Programmatic |
| Prototype / small screen | Programmatic (faster iteration) |
| Team with designers | FXML + Scene Builder |
| Complex dynamic content | Programmatic or hybrid |

## Hybrid pattern

Most real apps mix both: FXML for static shells, programmatic for
dynamic content inside them.

```java
// Controller injects the container
@FXML private VBox listContainer;

@FXML
public void initialize() {
    // Fill it programmatically from data
    for (Item item : repository.findAll()) {
        listContainer.getChildren().add(new ItemRow(item));
    }
}
```

## The tutor app is fully programmatic

This app itself uses no FXML — everything you see is built in Java.
That makes it a concrete example of the programmatic style.

## Challenge

Add a `Label("Fruit list")` at the very top of the `VBox` (before the
loop adds the rows).
