---
id: 044-file-chooser
tier: controls
order: 44
title: "FileChooser and DirectoryChooser"
objectives:
  - "Open a native file picker with FileChooser"
  - "Filter by extension and read the chosen path"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      Label path = new Label("No file chosen");
      Button open = new Button("Open File...");
      open.setOnAction(e -> {
          FileChooser fc = new FileChooser();
          fc.setTitle("Open");
          fc.getExtensionFilters().add(
              new FileChooser.ExtensionFilter("Text Files", "*.txt"));
          java.io.File file = fc.showOpenDialog(open.getScene().getWindow());
          if (file != null) path.setText(file.getAbsolutePath());
      });
      return new VBox(10, open, path);
  }
challenges:
  - id: c1
    description: "Add a second button that opens a DirectoryChooser instead"
    assertion: containsNodeOfType(Button)
nextLesson: 045-web-view
---

# FileChooser and DirectoryChooser

Both open the OS-native file picker — the same dialog the user sees in
every other application.

## FileChooser

```java
FileChooser fc = new FileChooser();
fc.getExtensionFilters().addAll(
    new ExtensionFilter("Images", "*.png", "*.jpg"),
    new ExtensionFilter("All Files", "*.*"));
File file = fc.showOpenDialog(window);    // null if cancelled
File file = fc.showSaveDialog(window);
List<File> files = fc.showOpenMultipleDialog(window);
```

## DirectoryChooser

```java
DirectoryChooser dc = new DirectoryChooser();
File dir = dc.showDialog(window);
```

## Challenge

Add a `Button("Open Folder...")` that opens a `DirectoryChooser` and
shows the chosen path in a new `Label`.
