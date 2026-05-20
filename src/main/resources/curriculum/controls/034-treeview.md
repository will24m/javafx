---
id: 034-treeview
tier: controls
order: 34
title: "TreeView"
objectives:
  - "Build a hierarchical tree with TreeItem"
  - "Expand and collapse nodes programmatically"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      TreeItem<String> root = new TreeItem<>("Root");
      root.setExpanded(true);

      TreeItem<String> a = new TreeItem<>("Branch A");
      a.getChildren().addAll(new TreeItem<>("Leaf A1"), new TreeItem<>("Leaf A2"));

      TreeItem<String> b = new TreeItem<>("Branch B");
      b.getChildren().add(new TreeItem<>("Leaf B1"));

      root.getChildren().addAll(a, b);
      TreeView<String> tree = new TreeView<>(root);
      tree.setShowRoot(true);
      return tree;
  }
challenges:
  - id: c1
    description: "Hide the root node by calling tree.setShowRoot(false)"
    assertion: containsNodeOfType(TreeView)
nextLesson: 035-tableview
---

# TreeView

`TreeView<T>` displays a `TreeItem<T>` hierarchy. Each `TreeItem` can
have children and an expanded/collapsed state.

## TreeItem

```java
TreeItem<String> item = new TreeItem<>("label");
item.setExpanded(true);
parent.getChildren().add(item);
```

## Custom cells

```java
tree.setCellFactory(tv -> new TreeCell<String>() {
    @Override protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty ? null : item);
    }
});
```

## Show/hide root

`setShowRoot(false)` hides the root node — useful when the root is a
synthetic "container" that shouldn't appear.

## Challenge

Call `tree.setShowRoot(false)` to hide the root item.
