---
id: 073-css-selectors
tier: css
order: 73
title: "CSS Selectors"
objectives:
  - "Apply styles by type, class, and ID selectors"
  - "Understand selector specificity in JavaFX CSS"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      Button primary = new Button("Primary");
      primary.getStyleClass().add("btn-primary");

      Button secondary = new Button("Secondary");
      secondary.getStyleClass().add("btn-secondary");

      Label title = new Label("Selector Demo");
      title.setId("main-title");

      VBox box = new VBox(10, title, primary, secondary);
      box.setStyle(
          ".btn-primary { -fx-background-color: steelblue; -fx-text-fill: white; }" +
          ".btn-secondary { -fx-background-color: grey; -fx-text-fill: white; }" +
          "#main-title { -fx-font-size: 18; -fx-font-weight: bold; }");
      // Note: inline style strings don't work this way — see lesson body
      return box;
  }
challenges:
  - id: c1
    description: "Use getStylesheets() on a Parent to load a real CSS string"
    assertion: containsNodeOfType(Button)
nextLesson: 074-pseudo-classes
---

# CSS Selectors

JavaFX CSS uses a subset of CSS 2.1 selectors:

| Selector | Syntax | Matches |
|---|---|---|
| Type | `Button` | All `Button` nodes |
| Class | `.my-class` | Nodes with `getStyleClass().contains("my-class")` |
| ID | `#my-id` | Nodes where `getId().equals("my-id")` |
| Descendant | `.parent .child` | `.child` anywhere inside `.parent` |
| Child | `.parent > .child` | Direct child only |

## Applying a stylesheet

```java
parent.getStylesheets().add(
    getClass().getResource("/css/my.css").toExternalForm());
```

Or inline (one style per node only):

```java
node.setStyle("-fx-background-color: steelblue;");
```

## Specificity

ID > class > type, same as browser CSS.

## Challenge

Move the style rules to an actual inline CSS string loaded via
`box.getStylesheets().add(...)` using a `data:text/css` URI approach,
or create a separate CSS resource.
