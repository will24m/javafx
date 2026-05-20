# JavaFX Tutor — A meta JavaFX learning desktop app

## Context

You want a desktop application **written in JavaFX** that teaches **JavaFX** by being a working example of itself. The "meta" part is non-trivial because of two coupled requirements:

1. **Live preview**: a user-edited JavaFX snippet must compile and render inside the host app on every keystroke (debounced), without restarting the JVM and without leaking ClassLoaders, threads, or scene-graph references.
2. **Dynamic mirror**: an inspector that, in one mode, introspects the *preview* scene graph and, in another mode, introspects the *host* scene graph — i.e. the app reflecting on itself live. The same inspector code must walk a `Parent` it owns and a `Parent` it lives inside.

These two together force the architecture: a strict separation between **host scene graph** and **sandbox scene graph**, a per-run **isolating ClassLoader**, a generic **Node introspector** that takes any `Node` root, and an FX-thread protocol that compiles off-thread and mounts on-thread.

The 100-lesson curriculum is the "content" layer that sits on top; the engine is the interesting part.

---

## Confirmed decisions

| Decision | Choice |
|---|---|
| Java version | **Temurin 21 LTS** — install via `brew install --cask temurin@21` |
| JavaFX version | **21.0.5** via Gradle JavaFX plugin |
| Build system | **Gradle 8.10 (Kotlin DSL)** with `gradlew` wrapper |
| Module system | **No JPMS for app code** — classpath app, JavaFX modules supplied by Gradle |
| Runtime compiler access | Add `--add-modules=jdk.compiler` plus javac package opens/exports to `run` and `test` JVM args |
| Persistence | **JSON in `~/.javafx-tutor/`** via Jackson |
| Code editor | **RichTextFX `CodeArea`** with Java syntax highlighting |
| Testing | **JUnit 5** for logic, **TestFX 4.0.18** for UI |

---

## Architecture (text diagram)

```
┌──────────────────────────── Host JVM (single process) ─────────────────────────────┐
│                                                                                    │
│  app.JavaFxTutorApp (extends Application)                                          │
│    └─ host Scene (FX Application Thread, "main")                                   │
│         └─ MainView (BorderPane)                                                   │
│              ├─ LessonNavigator   (left rail, list of 100 lessons)                 │
│              ├─ LessonPane        (left: markdown + objectives + challenges)       │
│              ├─ EditorPane        (center-top: RichTextFX CodeArea)                │
│              ├─ PreviewHost       (center-bottom: StackPane with a sandbox wrapper │
│              │                    that mounts the snippet's returned Parent)       │
│              └─ InspectorPane     (right: tree + properties + CSS + bounds)        │
│                                                                                    │
│  --- engine packages, no JavaFX import in `compile` package -----------------      │
│                                                                                    │
│  engine.compile.SnippetCompiler                                                    │
│    - wraps user code in a class template:                                          │
│        public class _Snippet { public static Parent build() { ... } }              │
│    - uses javax.tools.JavaCompiler + InMemoryFileManager                           │
│    - returns bytecode captured in memory plus the generated entry class name       │
│                                                                                    │
│  engine.runtime.SnippetRunner                                                      │
│    - creates a fresh SnippetClassLoader and calls #build() with a timeout          │
│    - mounts the Parent into PreviewHost on the FX thread                           │
│    - holds a single "current" SnippetSession; closes the previous one              │
│    - SnippetSession.close() detaches Parent, nulls refs, clears loader byte map    │
│                                                                                    │
│  engine.inspect.NodeInspector                                                      │
│    - root-agnostic: walks any Parent (sandbox root OR host MainView root)          │
│    - emits a TreeItem<NodeRef> for the InspectorPane                               │
│    - subscribes to widthProperty/heightProperty/styleProperty etc. and             │
│      re-publishes changes through a listener registry keyed by WeakReference       │
│                                                                                    │
│  engine.challenge.ChallengeRunner                                                  │
│    - assertions take the sandbox root and run scene-graph queries                  │
│      (e.g. lookupAll(".button") with text predicate)                               │
│    - never inspects source text                                                    │
│                                                                                    │
│  data.curriculum.CurriculumLoader                                                  │
│    - reads /resources/curriculum/**.md (frontmatter + body)                        │
│    - parses with commonmark-java, renders to JavaFX TextFlow                       │
│                                                                                    │
│  data.progress.ProgressStore                                                       │
│    - Jackson read/write to ~/.javafx-tutor/state.json                              │
│    - last-edited snippet stored per lessonId in ~/.javafx-tutor/snippets/<id>.java │
└────────────────────────────────────────────────────────────────────────────────────┘
```

### Threading model (strict)

| Activity | Thread | Mechanism |
|---|---|---|
| Editor keystroke → debounce → recompile | FX thread → bg | `PauseTransition` (350 ms) then a job submitted to a single-thread `ExecutorService` |
| `javax.tools` compilation | bg (compiler executor) | compile bytecode into memory |
| Snippet `build()` call | bg (per-build daemon executor) | `Future.get(2s)`; interrupt on timeout and report the error |
| Mount `Parent` into `PreviewHost` | FX thread | discard stale generations; mount only the newest successful result |
| Disposal of previous snippet | FX thread | detach node from preview wrapper, null refs, clear custom loader byte map |
| Inspector property listeners | FX thread (all reads + writes) | properties only mutate on FX |

**Why no second JVM yet**: a child JVM costs warmup time and complicates inspector/IPC. A per-run custom `SnippetClassLoader` that calls `defineClass()` from in-memory bytes keeps iteration fast and disposable. A child JVM remains the fallback for truly hostile or non-interruptible snippets, because in-process Java cannot reliably stop arbitrary code once it ignores interrupts.

### How the preview gets its own "Scene" without colliding

The snippet returns a `Parent`, **not a `Scene`**. We mount the `Parent` as a child of `PreviewHost` (a `StackPane`). It participates in the host Scene's layout pulse, gets its own coordinate space, and is fully detachable. We **never** call `snippet.start(new Stage())`. Lessons that need a Stage call `getScene().getWindow()` and are taught to treat the preview pane as their Stage equivalent — this is itself a teachable point.

---

## Data model

### Lesson files: Markdown + YAML frontmatter, one file per lesson

`src/main/resources/curriculum/<tier>/<NN>-slug.md`:

```markdown
---
id: 001-what-is-a-stage
tier: foundations
order: 1
title: "What is a Stage?"
objectives:
  - "Explain the difference between Stage, Scene, and Node"
  - "Create a Stage programmatically and show it"
estimatedMinutes: 8
starterSnippet: |
  public static Parent build() {
      Label l = new Label("Hello, JavaFX");
      return new StackPane(l);
  }
challenges:
  - id: c1
    description: "Change the label text to 'Submit' and wrap it in a VBox"
    assertion: containsLabeledInside(text="Submit", parentType=VBox)
nextLesson: 002-scene-and-node
---

# What is a Stage?

A `Stage` is a top-level window. Every JavaFX app starts with one ...
```

### Assertion DSL — minimal, scene-graph based

`engine.challenge.Assertions` exposes a fluent API used in `assertion:` frontmatter via a tiny parser. v1 supports:

- `containsNodeOfType(Class)` 
- `containsLabeledWithText(String)`
- `containsLabeledInside(text, parentType)`
- `cssClassPresent(node, "myStyle")`
- `propertyEquals(nodePath, propertyName, value)`

Parser is ~80 lines (regex `funcName(arg1=..., arg2=...)`). Anything more complex graduates to a Java file in `src/main/java/com/jfxtutor/challenges/<lessonId>.java` implementing `ChallengeAssertion`.

### Progress file `~/.javafx-tutor/state.json`

```json
{
  "version": 1,
  "lastLessonId": "012-vbox-vs-hbox",
  "completed": {
    "001-what-is-a-stage": { "completedAt": "2026-05-20T14:02:11Z", "timeSpentSec": 412 }
  },
  "preferences": { "theme": "dark", "fontSize": 13 }
}
```

Last-edited snippet per lesson: `~/.javafx-tutor/snippets/<lessonId>.java` (raw text, not JSON-escaped — easier to grep).

---

## Project skeleton (files to create in Phase 0)

```
javafx/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradle/wrapper/                          (gradle wrapper)
├── gradlew, gradlew.bat
├── README.md                                (update)
├── .gitignore                               (extend: build/, .gradle/, .idea/)
└── src/
    ├── main/
    │   ├── java/com/jfxtutor/
    │   │   ├── app/
    │   │   │   ├── JavaFxTutorApp.java          (Application subclass, main())
    │   │   │   └── MainView.java                (BorderPane composition)
    │   │   ├── ui/
    │   │   │   ├── LessonNavigator.java
    │   │   │   ├── LessonPane.java
    │   │   │   ├── EditorPane.java              (wraps RichTextFX CodeArea)
    │   │   │   ├── PreviewHost.java             (StackPane mount point)
    │   │   │   └── InspectorPane.java
    │   │   ├── engine/
    │   │   │   ├── compile/
    │   │   │   │   ├── SnippetCompiler.java
    │   │   │   │   ├── CompileResult.java
    │   │   │   │   ├── InMemoryJavaFileManager.java
    │   │   │   │   ├── InMemorySourceFile.java
    │   │   │   │   └── InMemoryClassFile.java
    │   │   │   ├── runtime/
    │   │   │   │   ├── SnippetClassLoader.java
    │   │   │   │   ├── SnippetRunner.java
    │   │   │   │   └── SnippetSession.java      (AutoCloseable; CL + Parent)
    │   │   │   ├── inspect/
    │   │   │   │   ├── NodeInspector.java
    │   │   │   │   ├── NodeRef.java
    │   │   │   │   └── PropertyWatcher.java
    │   │   │   └── challenge/
    │   │   │       ├── ChallengeRunner.java
    │   │   │       ├── Assertions.java
    │   │   │       └── AssertionParser.java
    │   │   └── data/
    │   │       ├── curriculum/
    │   │       │   ├── Lesson.java
    │   │       │   ├── LessonFrontmatter.java
    │   │       │   └── CurriculumLoader.java
    │   │       └── progress/
    │   │           └── ProgressStore.java
    │   └── resources/
    │       ├── css/
    │       │   ├── app.css
    │       │   └── editor-java.css              (RichTextFX token styles)
    │       └── curriculum/
    │           └── foundations/
    │               ├── 001-what-is-a-stage.md
    │               ├── 002-scene-and-node.md
    │               └── 003-stackpane-hello.md
    └── test/
        ├── java/com/jfxtutor/
        │   ├── engine/compile/SnippetCompilerTest.java
        │   ├── engine/challenge/AssertionsTest.java
        │   ├── data/curriculum/CurriculumLoaderTest.java
        │   └── ui/SmokeUiTest.java               (TestFX)
        └── resources/curriculum-test/
            └── 999-fixture.md
```

### `build.gradle.kts` (the load-bearing parts)

```kotlin
plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.jfxtutor"
version = "0.1.0"

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

javafx {
    version = "21.0.5"
    modules = listOf("javafx.controls", "javafx.fxml")
}

val compilerAccessJvmArgs = listOf(
    "--add-modules=jdk.compiler",
    "--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
    "--add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED"
)

application {
    mainClass.set("com.jfxtutor.app.JavaFxTutorApp")
}

dependencies {
    implementation("org.fxmisc.richtext:richtextfx:0.11.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.2")
    implementation("org.commonmark:commonmark:0.22.0")
    implementation("org.slf4j:slf4j-simple:2.0.13")

    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.testfx:testfx-core:4.0.18")
    testImplementation("org.testfx:testfx-junit5:4.0.18")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs(compilerAccessJvmArgs)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    jvmArgs(compilerAccessJvmArgs)
}
```

---

## Phased implementation roadmap

### Phase 0 — Skeleton + Hello World lesson end-to-end (target: 1 day)

**Goal**: Launch the app, see a static lesson on the left, a non-editable preview showing the hardcoded starter snippet rendered into `PreviewHost`. No compilation yet. No inspector. No editor interactivity.

Files to create:
- `build.gradle.kts`, `settings.gradle.kts`, `gradle/wrapper/*`, `gradlew`
- `src/main/java/com/jfxtutor/app/JavaFxTutorApp.java`
- `src/main/java/com/jfxtutor/app/MainView.java`
- `src/main/java/com/jfxtutor/ui/{LessonPane,EditorPane,PreviewHost,InspectorPane,LessonNavigator}.java` (placeholder content)
- `src/main/resources/css/app.css`
- `src/main/resources/curriculum/foundations/001-what-is-a-stage.md`

**Smallest demonstrable milestone**: `./gradlew run` opens a window with the four panes laid out and lesson 001's text visible.

### Phase 1 — Curriculum data model + lesson navigator (target: 1 day)

- `data/curriculum/{Lesson,LessonFrontmatter,CurriculumLoader}.java`
- `LessonNavigator` reads all `curriculum/**/*.md` at startup, groups by tier, fires selection events
- `LessonPane` renders markdown body via commonmark-java → `TextFlow`
- Write 2 more foundations lessons (`002`, `003`) so the navigator is non-trivial

**Milestone**: click any of three lessons in the left rail, see its markdown body rendered.

### Phase 2 — Embedded editor + safe live preview (target: 2-3 weeks, the heart of the project)

- `engine/compile/InMemoryClassFile.java` — `JavaFileObject` backed by `ByteArrayOutputStream`
- `engine/compile/InMemorySourceFile.java` — source string as a `JavaFileObject`
- `engine/compile/InMemoryJavaFileManager.java` — `ForwardingJavaFileManager` that captures `output()` calls
- `engine/compile/SnippetCompiler.java`:
  - wraps user body in `package _gen; import javafx.scene.*; ... public class _Snippet { public static Parent build() { <USER_BODY> } }`
  - calls `ToolProvider.getSystemJavaCompiler().getTask(...)` with our file manager
  - forwards runtime classpath/module-path and remaps generated diagnostics back to user snippet lines
  - on success, returns `CompileResult(Map<String, byte[]>, "_gen._Snippet")`; on failure returns diagnostics or a clear compiler error message
- `engine/runtime/SnippetClassLoader.java` — custom loader that calls `defineClass()` from the in-memory byte map; `URLClassLoader` is not appropriate because there are no class files or jars on disk
- `engine/runtime/SnippetSession.java` — `AutoCloseable`, holds `SnippetClassLoader` and the mounted `Parent`; `close()` detaches Parent from its parent on FX and clears loader byte refs
- `engine/runtime/SnippetRunner.java`:
  - debounced via `PauseTransition`
  - submits compile to `Executors.newSingleThreadExecutor()` (single thread guarantees ordering)
  - invokes `build()` on a per-generation daemon executor with a 2-second `Future.get` timeout and `shutdownNow()` on timeout
  - discards stale generations before mounting so older compiles cannot overwrite newer edits
  - on success: dispose previous session, mount new Parent in `PreviewHost`
  - on failure: render diagnostics into a small banner overlay on `PreviewHost`
- `EditorPane` becomes a real `CodeArea` with RichTextFX Java highlighting (use the standard `RegexJavaHighlighter` pattern from RichTextFX demos)
- `editor-java.css` defines the token styles

**Milestone**: edit a `Label` text in the editor, see the preview update within ~400 ms.

### Phase 3 — Challenge runner MVP + progress persistence (target: 1 week)

- `engine/challenge/{Assertions,ChallengeRunner}.java` lands before the inspector so learners can validate work early
- v1 can use hardcoded Java assertion classes per lesson before the DSL exists
- `AssertionParser` follows with explicit `ClassResolver` scope:
  - maps common short names such as `VBox`, `HBox`, `StackPane`, `Label`, and `Button` to JavaFX classes
  - rejects ambiguous or unknown class names with lesson-author-facing errors like `Unknown class 'Vbox'; did you mean 'VBox'?`
  - keeps a hardcoded import map first; reflection-based lookup is a later convenience
- `data/progress/ProgressStore.java` with Jackson, including `jackson-datatype-jsr310` for `Instant`
- Atomic writes create `state.json.tmp` inside `~/.javafx-tutor/` before `Files.move(..., ATOMIC_MOVE)`, because atomic moves only work reliably within the same filesystem
- `LessonPane` gains a "Check challenge" button per challenge, badge turns green on pass
- On lesson switch: save current snippet; reload destination lesson's snippet if previously edited

**Milestone**: complete lesson 001's challenge, close and reopen the app, see the green check and your edited snippet still there.

### Phase 4 — Inspector + dynamic mirror mode (target: 1-2 weeks)

- `engine/inspect/NodeRef.java` — immutable handle: `Node`, computed path, snapshot of CSS classes, bounds
- `engine/inspect/PropertyWatcher.java` — `Map<WeakReference<Node>, List<ChangeListener>>`; method `watch(Node, propertyName)` returns an `ObservableValue` proxy
- `engine/inspect/NodeInspector.java`:
  - `setRoot(Parent)` rebuilds the TreeView
  - hover on preview → highlight overlay (translucent rectangle over `localToScene(getBoundsInLocal())`)
  - selected node → property table on the right with live values
- `InspectorPane` has a mode toggle: "Preview" (root = `PreviewHost.snippetRoot`) vs. "Mirror" (root = `MainView` itself)
- In Mirror mode, selection is opt-in through an "inspect pointer" toggle plus Ctrl-Alt-click. The overlay must be click-through when inactive, and when active it should show the deepest pick result plus its parent chain so scroll panes, cells, and border-pane empty regions are not ambiguous.
- Hover highlight in Mirror mode is throttled and disabled while controls are being dragged or text is being edited, so the inspector does not fight normal app interaction.

**Milestone**: drag the host window edge, watch the host MainView's `widthProperty` tick in the inspector in Mirror mode.

### Phase 5 — Curriculum content (target: beta at 20 lessons; full product at 100)

The engine is only useful once the content is shippable. Completion criteria:

- **Alpha**: 5 foundations lessons, all with starter snippets and passing challenge assertions.
- **Beta / minimum viable curriculum**: 20 lessons: 10 foundations, 6 layouts, 4 controls. These must teach enough JavaFX for a learner to build a small form-based app.
- **1.0**: all 100 lessons, each with frontmatter, starter snippet, one primary challenge, one stretch challenge, and a reviewer pass for code accuracy.

Content workflow: draft lessons in batches of 5, run every starter snippet through the live preview, run every challenge assertion against both the starter and expected solution, then review for learner pacing before merging.

Tier file counts and themes:

| Tier | Range | Count | What fills it |
|---|---|---|---|
| Foundations | 001–015 | 15 | `Application`, `Stage`, `Scene`, `Group` vs `Parent`, `Node` hierarchy, FX thread, `Platform.runLater`, basic `Label`/`Button`/`TextField`, `Image`, `Cursor`, sizing concepts, the layout pulse |
| Layouts | 016–030 | 15 | `StackPane`, `VBox`, `HBox`, `BorderPane`, `GridPane`, `AnchorPane`, `FlowPane`, `TilePane`, alignment, padding, `setMinWidth`/`pref`/`max`, growth priorities, `Region.layoutChildren` override |
| Controls | 031–045 | 15 | `Button`, `ToggleButton`, `CheckBox`, `RadioButton`, `ChoiceBox`, `ComboBox`, `ListView`, `TreeView`, `TableView`, `TextArea`, `TextField` validation, `DatePicker`, `Slider`, `ProgressBar`, custom cell factories |
| Properties & Bindings | 046–060 | 15 | `SimpleStringProperty`, `IntegerProperty`, `bind()` vs `bindBidirectional()`, `Bindings` helpers, `ObservableList`, `FXCollections`, weak listeners, common binding pitfalls, custom property classes |
| Events & Concurrency | 061–072 | 12 | event filters vs handlers, bubbling, `Task`, `Service`, `WorkerStateEvent`, `ScheduledService`, cancellation, FX-thread invariant, async patterns with `CompletableFuture` |
| CSS & Theming | 073–082 | 10 | selectors, pseudo-classes (`:hover`, `:focused`), `-fx-*` properties, theme stylesheets, `getStyleClass()`, hot-reload of CSS, dark/light theme switching, `Modena` overrides |
| FXML & MVC | 083–090 | 8 | `FXMLLoader`, `@FXML`, controller wiring, `fx:id`, includes, custom controls, Scene Builder workflow, when to prefer programmatic |
| Animation & Media | 091–096 | 6 | `Timeline`, `KeyFrame`, transitions (`FadeTransition` et al), interpolators, `MediaView`, `Canvas` animation |
| Packaging & Deployment | 097–100 | 4 | jpackage flags, app icons, native installers, code-signing tradeoffs |
| **Total** | | **100** | |

**Authored as exemplars in this plan (full content lives in resource files, not here)**:
1. `001-what-is-a-stage.md` — frontmatter shown above; body explains Stage/Scene/Node and challenge asks to add a `VBox` around the label.
2. `017-vbox-vs-hbox.md` — starter is a `VBox` with two buttons; challenge converts to `HBox` and verifies via `containsNodeOfType(HBox.class)`.
3. `048-bind-label-to-slider.md` — starter shows a `Slider` and a `Label`; challenge requires `label.textProperty().bind(slider.valueProperty().asString("%.1f"))`, verified via `propertyEquals("Label#out", "text", "0.0")` after a programmatic slider set.

### Phase 6 — Packaging via jpackage (target: 1 day)

- Add `org.beryx.runtime` Gradle plugin (works without JPMS by bundling a full JRE)
- `runtime { jpackage { ... } }` config: app name, vendor, icon (`.icns` on macOS), `--mac-package-identifier`
- Output: `build/jpackage/JavaFxTutor.app` and `JavaFxTutor-0.1.0.dmg`
- **Milestone**: drag the `.app` to Applications, launch from Spotlight, complete a lesson.

---

## Risks (top 5) and mitigations

1. **ClassLoader leaks from per-edit recompiles**
   - *Mitigation*: every `SnippetSession` is `AutoCloseable`; `SnippetRunner` holds at most one. Detach `Parent` from scene graph before nulling refs (otherwise the scene retains it). Clear the custom `SnippetClassLoader` byte map. Add a heap-watch dev mode: log `MemoryMXBean.getHeapMemoryUsage()` every 30 s; if it grows monotonically across 50 recompiles, fall back to **child-JVM** model.
   - *Detection test*: a JUnit test that recompiles 200 distinct snippets and asserts `WeakReference<ClassLoader>` gets GC'd after `System.gc()`.

2. **`Platform.runLater` deadlocks or ordering bugs**
   - *Mitigation*: never block the FX thread waiting for compile or `build()`. Compile and build off-thread, then mount only the newest generation on the FX thread. Listener wiring and node disposal stay on FX.
   - *Detection*: TestFX test that fires 20 rapid edits and asserts the final preview matches the last edit (proves single-thread executor ordering).

3. **Security of executing arbitrary user-written code**
   - *Reality check*: this is a **local single-user** tool. The user is writing code to teach themselves; the threat model is "I accidentally wrote `System.exit(0)`", not malice. Therefore: no SecurityManager (deprecated in 21 anyway). Mitigations: (a) preflight reject obvious process-exit calls; (b) catch `Throwable` around `build()` and render in error banner; (c) run `build()` through a `Future` on a daemon executor, use `get(2s)`, `cancel(true)`, and `shutdownNow()` on timeout; (d) explicit default imports for common JavaFX APIs.
   - *Remaining risk*: Java cannot safely kill a non-interruptible in-process infinite loop. If this becomes common, move snippet execution to a child JVM and communicate the scene description or screenshot back to the host.

4. **CSS reload pitfalls + dynamic mirror collisions**
   - The host app and the sandbox share a `Scene` (the sandbox `Parent` is mounted inside the host scene). Sandbox CSS via `Parent.getStylesheets().add(...)` is scoped to that subtree — good. But a user's `Node.setStyle(...)` with an `-fx-base` change can bleed into the host through `:root` cascades.
   - *Mitigation*: mount the sandbox `Parent` inside a dedicated wrapper with `getStyleClass().add("sandbox-root")` and write host CSS rules to use `.host-root` selectors. Document: "lesson CSS is scoped under `.sandbox-root`".

5. **RichTextFX + JavaFX 21 compatibility on Apple Silicon**
   - RichTextFX 0.11.2 supports JavaFX 21 but historically has had thread-assertion issues. The `flowless` transitive dep occasionally errors on macOS retina with certain font configs.
   - *Mitigation*: pin to `0.11.2`, keep a `SmokeUiTest` that opens a `CodeArea`, loads the starter snippet, and exercises compile/error paths. If it fails on arm64, fallback is `org.fxmisc.richtext:richtextfx:0.11.0` or — last resort — replacing with a `TextArea` + manual highlighting.

---

## Verification — current baseline

Run from repo root:

```bash
brew install --cask temurin@21              # one time
./gradlew --version                          # confirms wrapper works
./gradlew test --rerun-tasks --warning-mode all
./gradlew run                                # launches the app
```

You should see:
- A window ~1200×800 titled "JavaFX Tutor".
- Four panes: left rail with "001 — What is a Stage?" highlighted; left-center showing the markdown body of lesson 001; center-top showing an editable `CodeArea` with syntax highlighting; center-bottom showing live preview output from the compiled starter snippet; right pane showing a placeholder "Inspector".
- Blank or invalid editor contents should show an error overlay without unmounting the last good preview.
- Slow `build()` snippets should time out and show an error instead of freezing the host UI.
- Window resizes cleanly; no exceptions in console.

`SmokeUiTest`, `SnippetCompilerTest`, and `CurriculumLoaderTest` must pass before expanding the lesson set or inspector work.

---

## Libraries to use vs build

| Concern | Use | Why |
|---|---|---|
| Code editor with syntax highlighting | **RichTextFX `CodeArea`** + the demo Java highlighter | Best-in-class for JavaFX; the standard answer. Writing our own styled text area would be weeks of work. |
| Markdown rendering | **commonmark-java** | Pure Java, no JS deps. Render to your own JavaFX nodes via custom `Visitor` → `TextFlow`. |
| JSON persistence | **Jackson databind** | Industry standard, handles `Instant`/records cleanly with `jackson-datatype-jsr310`. |
| In-memory Java compile | **`javax.tools.JavaCompiler`** (JDK builtin) | Zero deps. Don't pull in Janino — too restrictive on Java 21 syntax. |
| UI smoke testing | **TestFX 4.0.18 + JUnit 5** | Confirmed by your spec; matches JavaFX 21. |
| Logging | **slf4j-simple** | Trivial; replaceable later if you want logback. |
| Packaging | **`org.beryx.runtime`** Gradle plugin (NOT badass-jlink) | Works without JPMS; wraps jpackage cleanly. |
| Inspector reference (read source, don't depend on) | **ScenicView** (BSD) | Don't ship as dep — incompatible licensing/maintenance burden. Read its source for the `localToScene` overlay pattern. |
| **Build, do not pull in**: ControlsFX, TilesFX, FormsFX | — | Out of scope for v1; would muddy "this app is itself a JavaFX example written from the standard widgets". |

---

## Open items I flagged but did not resolve

- **App icon**: needed for jpackage in Phase 6. I assumed you'll draw / generate one before Phase 6. Not blocking earlier phases.
- **Theme**: dark theme included in CSS, but no theme switcher built in Phase 0–4. Lesson 077 will add it.
- **Internationalization**: out of scope. English-only. Lessons that mention `ResourceBundle` show the API without translating the app.
