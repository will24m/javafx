# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Run the application
./gradlew run

# Build
./gradlew build

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.jfxtutor.ui.SmokeUiTest"

# Run a single test method
./gradlew test --tests "com.jfxtutor.ui.SmokeUiTest.windowTitleIsCorrect"
```

Tests require a display (TestFX launches a real JavaFX window). On CI or headless environments, add `-Djava.awt.headless=false -Dtestfx.robot=glass -Dtestfx.headless=true -Dprism.order=sw` to the test JVM args.

## Architecture

This is a JavaFX 21 desktop "interactive tutor" app. The user edits Java code snippets in a live editor, which are compiled and executed in-process on every keystroke.

### Compile-and-mount loop

`EditorPane` (RichTextFX `CodeArea`) → `SnippetRunner` (debounce 350ms via `PauseTransition`) → `SnippetCompiler` (background thread via single-thread `ExecutorService`) → `SnippetClassLoader` → `SnippetSession` → `PreviewHost`.

- **`SnippetCompiler`** wraps the user's snippet body in a generated class (`_gen._Snippet`) with a `public static Parent build()` method and a fixed set of JavaFX wildcard imports. It compiles using `javax.tools.JavaCompiler` with an `InMemoryJavaFileManager` — no files on disk. Compiler options forward `jdk.module.path` and `java.class.path` so the in-process compiler sees the same modules as the running JVM.
- **`SnippetClassLoader`** is a custom `ClassLoader` that calls `defineClass()` from in-memory bytes. `URLClassLoader` cannot be used because there are no file URLs — bytes live only in memory.
- **`SnippetSession`** pairs a `SnippetClassLoader` with the `Parent` it produced. When a new session mounts, the old session's `close()` removes the `Parent` from the scene and clears the classloader so classes can be GC'd.
- **`PreviewHost`** is the `StackPane` mount point. On compile error it shows an overlay banner over the last successful preview (previous output stays visible).
- **Generation counter** (`AtomicLong`) in `SnippetRunner` discards stale compile results when multiple compiles are in-flight.

### UI layout

`MainView` (BorderPane) contains a horizontal `SplitPane` with four columns:
1. `LessonNavigator` — TreeView/ListView selecting lessons
2. `LessonPane` — Markdown lesson body rendered via commonmark-java
3. Center vertical split: `EditorPane` (top) / `PreviewHost` (bottom)
4. `InspectorPane` — scene graph inspector (stub)

### Curriculum

Lessons are Markdown files with YAML frontmatter stored under `src/main/resources/curriculum/`. `curriculum/index.txt` lists classpath paths in load order. `CurriculumLoader.loadAll()` reads the index, parses each file, and sorts by `(tier, order)`.

Frontmatter fields: `id`, `tier`, `order`, `title`, `objectives`, `estimatedMinutes`, `starterSnippet`, `challenges`, `nextLesson`. Challenges carry an `assertion` string in a custom DSL (e.g., `containsLabeledInside(text="Submit", parentType=VBox)`) — the assertion evaluator is not yet implemented.

### Key packages

| Package | Responsibility |
|---|---|
| `com.jfxtutor.app` | Application entry point and root layout |
| `com.jfxtutor.engine.compile` | In-memory javac wrapper |
| `com.jfxtutor.engine.runtime` | ClassLoader, session lifecycle, debounced runner |
| `com.jfxtutor.data.curriculum` | Lesson loading and data model |
| `com.jfxtutor.ui` | All JavaFX UI components |

### Not yet implemented

- `engine/inspect/` — scene graph walker / NodeInspector
- `engine/challenge/` — assertion DSL evaluator
- `data/progress/` — user progress persistence (planned: Jackson JSON + `ATOMIC_MOVE`)
