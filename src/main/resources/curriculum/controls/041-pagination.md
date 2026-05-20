---
id: 041-pagination
tier: controls
order: 41
title: "Pagination"
objectives:
  - "Split content across pages with Pagination"
  - "Provide a page factory that builds each page on demand"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      int pageSize = 5;
      List<String> allItems = new ArrayList<>();
      for (int i = 1; i <= 30; i++) allItems.add("Item " + i);

      Pagination pagination = new Pagination((allItems.size() + pageSize - 1) / pageSize);
      pagination.setPageFactory(pageIndex -> {
          int from = pageIndex * pageSize;
          int to = Math.min(from + pageSize, allItems.size());
          VBox page = new VBox(4);
          allItems.subList(from, to).forEach(s -> page.getChildren().add(new Label(s)));
          return page;
      });
      return pagination;
  }
challenges:
  - id: c1
    description: "Change page size to 10 items per page"
    assertion: containsNodeOfType(Pagination)
nextLesson: 042-hyperlink-and-html
---

# Pagination

`Pagination` shows one "page" of content at a time with navigation
arrows and page indicators.

## Page factory

```java
pagination.setPageFactory(index -> {
    // build and return the Node for this page
    return new Label("Page " + (index + 1));
});
```

The factory is called lazily — only when the user navigates to a page.

## Page count

Pass the total number of pages to the constructor or
`setPageCount(n)`. Use `Pagination.INDETERMINATE` if unknown.

## Challenge

Change `pageSize` to `10`.
