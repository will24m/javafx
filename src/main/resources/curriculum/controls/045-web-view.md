---
id: 045-web-view
tier: controls
order: 45
title: "WebView"
objectives:
  - "Embed a WebView and load a URL"
  - "Execute JavaScript and receive results"
estimatedMinutes: 10
starterSnippet: |
  public static Parent build() {
      WebView webView = new WebView();
      webView.getEngine().loadContent(
          "<html><body style='font-family:sans-serif;padding:20px'>"
          + "<h2>Hello from WebView</h2>"
          + "<p>This is rendered by WebKit inside JavaFX.</p>"
          + "</body></html>");
      return webView;
  }
challenges:
  - id: c1
    description: "Add a TextField and Button to navigate to a user-entered URL"
    assertion: containsNodeOfType(WebView)
nextLesson: 046-simple-properties
---

# WebView

`WebView` embeds a full WebKit browser engine. It renders HTML, CSS,
and JavaScript just like a real browser tab.

## Loading content

```java
engine.load("https://example.com");         // URL
engine.loadContent("<h1>Hello</h1>");        // inline HTML
```

## Executing JavaScript

```java
engine.executeScript("document.title");
engine.executeScript("window.scrollTo(0, 500)");
```

## Listen to load state

```java
engine.getLoadWorker().stateProperty().addListener((obs, old, state) -> {
    if (state == Worker.State.SUCCEEDED) { /* page loaded */ }
});
```

## Challenge

Add a `TextField` for URL input and a `Button("Go")` that calls
`engine.load(textField.getText())`.
