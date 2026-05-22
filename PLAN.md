# JavaFX Tutor — Roadmap to 1.0

_Last updated: 2026-05-22. Reflects code state after Sprint 2._

---

## What's shipped

| Area | Status | Key files |
|---|---|---|
| In-memory compile pipeline | ✅ Done | [SnippetCompiler.java](src/main/java/com/jfxtutor/engine/compile/SnippetCompiler.java), `InMemoryJavaFileManager`, `SnippetClassLoader`, `SnippetSession` |
| Debounced runner + generation counter | ✅ Done | [SnippetRunner.java](src/main/java/com/jfxtutor/engine/runtime/SnippetRunner.java) — 350 ms debounce, `forceRun()`, build timeout, FX-thread mount |
| Live editor (RichTextFX) | ✅ Done | [EditorPane.java](src/main/java/com/jfxtutor/ui/EditorPane.java) |
| 100-lesson curriculum loader | ✅ Done | [CurriculumLoader.java](src/main/java/com/jfxtutor/data/curriculum/CurriculumLoader.java), `curriculum/index.txt`, all 100 `.md` files |
| Challenge runner + assertion DSL | ✅ Done | `engine/challenge/` — `ChallengeAssertion`, `AssertionResult`, `Assertions`, `AssertionParser`, `ClassResolver`, `ChallengeRunner` |
| Challenge UI in LessonPane | ✅ Done | [LessonPane.java](src/main/java/com/jfxtutor/ui/LessonPane.java) — Check button, ○/✓/✗ icons, feedback label, pass state restored from progress |
| Progress persistence | ✅ Done | `data/progress/` — `ProgressStore`, `Progress`, `LessonRecord`, `SnippetStore`, `ProgressMigrator`; atomic writes; schema versioning; `~/.javafx-tutor/` |
| Completion badges in navigator | ✅ Done | [LessonNavigator.java](src/main/java/com/jfxtutor/ui/LessonNavigator.java) — ✓ badge, `refreshBadges()`, `selectNext/Prev()`, `findById()` |
| Snippet restore on lesson select | ✅ Done | [MainView.java](src/main/java/com/jfxtutor/app/MainView.java) — saves/restores per-lesson snippets, restores last lesson on launch |
| Inspector v2 — engine layer | ✅ Done | [NodeRef.java](src/main/java/com/jfxtutor/engine/inspect/NodeRef.java), [NodeInspector.java](src/main/java/com/jfxtutor/engine/inspect/NodeInspector.java), [PropertyWatcher.java](src/main/java/com/jfxtutor/engine/inspect/PropertyWatcher.java), [HighlightOverlay.java](src/main/java/com/jfxtutor/engine/inspect/HighlightOverlay.java) |
| Inspector — Properties / CSS / Bounds tabs | ✅ Done | [InspectorPane.java](src/main/java/com/jfxtutor/ui/InspectorPane.java) — three tabs, live-updating on selection |
| Inspector — hover-to-highlight | ✅ Done | Mouse move over preview → translucent blue overlay + name tag; click-to-select in tree |
| Inspector — Mirror mode | ✅ Done | Toggle button re-roots inspector on the host's own scene graph |
| Theme system (dark / light / HC) | ✅ Done | [ThemeManager.java](src/main/java/com/jfxtutor/ui/ThemeManager.java), `theme-dark/light/hc.css`, Cmd+T cycles, persisted in ProgressStore |
| Keyboard shortcuts shell | ✅ Done | [KeyBindings.java](src/main/java/com/jfxtutor/ui/KeyBindings.java) — Cmd+]/[ next/prev, Cmd+R recompile, Cmd+I inspector, Cmd+T theme; stubs for tutor/palette/find |
| Test suite (144 tests) | ✅ Done | `AssertionParserTest`, `AssertionsTest`, `ChallengeAssertionsCoverageTest`, `NodeInspectorTest`, `AllStarterSnippetsCompileTest`, `SmokeUiTest` |

---

## What's in the plan but NOT yet in the codebase

Everything below is unbuilt. Sprint order matches the original execution plan.

---

### Sprint 3 — Track 1: B2a — AI Tutor skeleton + first-run onboarding

**New files:**

`src/main/java/com/jfxtutor/data/config/AppConfig.java`
Jackson-backed `~/.javafx-tutor/config.json`. Fields: `apiKey`, `model` (default `"claude-haiku-4-5-20251001"`), `tutorEnabled`, `checkForUpdates`, `costThisMonth`. Atomic write via `.tmp` + `ATOMIC_MOVE`.

`src/main/java/com/jfxtutor/ai/AnthropicClient.java`
Wraps `com.anthropic:anthropic-java` SDK. `streamMessage(params, systemPrompt, onToken, onDone, onError)`. Generation-counter pattern (from `SnippetRunner`) discards stale replies on lesson navigation. Prompt caching: wraps lesson-body block with `cache_control: {type: "ephemeral"}`.

`src/main/java/com/jfxtutor/ai/SystemPromptBuilder.java`
Assembles system prompt: lesson markdown body (cached block), user snippet (≤4 KB), latest compile diagnostics, failed assertion message + DSL string, current lesson id.

`src/main/java/com/jfxtutor/ai/CostMeter.java`
Tracks `inputTokens`, `outputTokens`, `cacheReadTokens` per session; aggregates monthly cost in `AppConfig.costThisMonth`.

`src/main/java/com/jfxtutor/ui/FirstRunWizard.java`
Modal dialog shown on first launch (or when `apiKey` blank). Steps: (1) welcome + link, (2) paste key → validate with cheap ping, (3) model selector (Haiku / Sonnet / Opus), (4) confirm or Skip (requires second click).

`src/main/java/com/jfxtutor/ui/TutorPane.java`
Right-pane panel (next to Inspector, toggled with Cmd+/). Contains: quick-action buttons row ("Why doesn't this compile?", "Why does my assertion fail?", "Hint please", "Explain this", "Next step", "Review my code"), scrollable message history rendered via commonmark-java → TextFlow, text input + Send button, `CostMeterBar` footer. Streaming tokens appear via `Platform.runLater`. Any ` ```java ` block in a reply gets Copy + "Apply to editor" buttons.

`src/main/java/com/jfxtutor/ui/CostMeterBar.java`
`HBox` label: `"Session: 1.2K tok · Month: $0.04"`. Updates after each reply.

**Modify:**
- [build.gradle.kts](build.gradle.kts) — add `implementation("com.anthropic:anthropic-java:2.+")`
- [MainView.java](src/main/java/com/jfxtutor/app/MainView.java) — add `TutorPane` as fourth right-pane tab; wire Cmd+/ toggle; pass `AppConfig` + `AnthropicClient` + `SystemPromptBuilder`
- [JavaFxTutorApp.java](src/main/java/com/jfxtutor/app/JavaFxTutorApp.java) — load `AppConfig`; show `FirstRunWizard` if `apiKey` blank
- [KeyBindings.java](src/main/java/com/jfxtutor/ui/KeyBindings.java) — wire `onToggleTutor` stub to real toggle

**Tests:** `AnthropicClientTest` (mock HTTP, verify cache header on second call, verify stale-gen discard), `SystemPromptBuilderTest` (golden prompt for known inputs)

---

### Sprint 3 — Track 2: B1a — Hints + solution fields + SolutionDiffView

**New files:**

`src/main/java/com/jfxtutor/ui/SolutionDiffView.java`
Side-by-side diff panel. Left = user snippet, right = reference solution. DIY LCS line-diff (~60 lines): green gutter for added lines, red for removed, white for unchanged. "Apply solution" button replaces editor content after a confirmation dialog. No external diff library.

**Modify:**
- [LessonFrontmatter.java](src/main/java/com/jfxtutor/data/curriculum/LessonFrontmatter.java) — add `List<String> hints` and `String solution` (Jackson `@JsonProperty`; both nullable)
- [LessonRecord.java](src/main/java/com/jfxtutor/data/progress/LessonRecord.java) — add `int hintsUsed` and `boolean viewedSolution`
- [LessonPane.java](src/main/java/com/jfxtutor/ui/LessonPane.java) — below challenge section add: "Get a hint" button (reveals one hint at a time, tracked in `LessonRecord.hintsUsed`, label becomes "Hint 2/3"); "Show solution" button (confirmation dialog warns this marks solved-with-help, then opens `SolutionDiffView`)

**Content:** author `hints` (3 per lesson) + `solution` for lessons 001–010 as template; remaining 90 done in Sprint 5/7/9.

**Tests:** `SolutionDiffViewTest` (golden diffs for all-same / all-different / mixed), `LessonFrontmatterTest` (hints + solution parse from YAML)

---

### Sprint 4 — Track 1: B2b — Tutor tool use + ProposedEditView

**New files:**

`src/main/java/com/jfxtutor/ai/Tools.java`
Tool schema list for Anthropic API: `read_lesson(id)`, `read_user_snippet()`, `read_compile_errors()`, `read_assertion_result()`, `propose_edit(diff)`. Host fulfills tool calls; `propose_edit` opens `ProposedEditView` and blocks until user decides.

`src/main/java/com/jfxtutor/ui/ProposedEditView.java`
Modal diff panel (reuses `SolutionDiffView` renderer). "Accept" applies diff to editor. "Reject" sends rejection message to continue conversation.

**Modify:**
- `AnthropicClient.java` — add `streamMessageWithTools(...)`: handles `tool_use` stop reason, calls tool handler, sends `tool_result`, continues streaming
- `TutorPane.java` — wire quick-action buttons to real sends; add privacy banner on first send (stored in `AppConfig`); update cost meter after each reply

---

### Sprint 4 — Track 2: B3b — Theme polish + accessibility + system pref detection

**Modify:**

`ThemeManager.java` — add `detectSystemTheme()`: on macOS exec `defaults read -g AppleInterfaceStyle`; returns `"dark"` or `"light"`; called at startup if no saved theme in `AppConfig`.

`JavaFxTutorApp.java` — call `themeManager.detectAndApplySystemTheme()` before `primaryStage.show()`.

Accessibility (Java, not CSS):
- All `Button`, `ToggleButton` — `setAccessibleText(label)`
- All `TreeView`, `TableView` — `setAccessibleRole(...)`
- Challenge check buttons — `setAccessibleText("Check: " + def.description)`

**New files:**

`src/main/java/com/jfxtutor/ui/PreferencesDialog.java`
`Dialog<Void>` with: font-size slider (10–18 px, persisted to `AppConfig.fontSize`), theme radio buttons (Dark / Light / High Contrast), "Reset snippet" button for current lesson.

Add `AppConfig.fontSize` field; `ThemeManager.applyFontSize(double)` sets inline `-fx-font-size` on root node.

**Collapsible panes:** each SplitPane column header gets a `◀`/`▶` chevron; click collapses/restores; divider positions saved to `ProgressStore` preferences.

**CSS hot reload (dev mode):** `ThemeManager.watchThemeFile()` — `WatchService` on `~/.javafx-tutor/dev-theme.css`; reloads within 500 ms; only active when `-Ddev=true` JVM flag is set. Matches lesson 077.

---

### Sprint 5 — Track 1: B4 — Search + command palette + outline navigator

**New files:**

`src/main/java/com/jfxtutor/ui/SearchService.java`
Offline scored token-match index. Indexes all lessons at startup by: title tokens, tier, id, objectives, first 200 chars of body. `search(String query)` → `List<SearchResult>` sorted by score (exact id > title token > body token). No external dep, ~80 lines.

`src/main/java/com/jfxtutor/ui/CommandPalette.java`
Undecorated popup `Stage` centered on screen. Text field → live `ListView<SearchResult>`. Enter/click navigates to lesson via `LessonNavigator.selectLesson()`. Escape / focus-lost closes. Also accepts commands: `"theme dark"`, `"theme light"`, `"next"`, `"prev"`, `"inspector"`, `"tutor"`. Opened by Cmd+Shift+P (already stubbed in KeyBindings).

**Modify [LessonNavigator.java](src/main/java/com/jfxtutor/ui/LessonNavigator.java):**
- Tier rows show completion counts: `"Controls  8 / 15"` in muted text
- Recent lessons ring: last 5 selected lessons under a "Recent" pseudo-tier at the top
- Bookmark toggle: star icon per row; stored in `ProgressStore` preferences as comma-separated id list

**Modify [MainView.java](src/main/java/com/jfxtutor/app/MainView.java)** — instantiate `CommandPalette`; pass `SearchService` + `LessonNavigator`; wire Cmd+Shift+P.

**Modify [KeyBindings.java](src/main/java/com/jfxtutor/ui/KeyBindings.java)** — wire `onCommandPalette` stub to real `CommandPalette.show()`.

**Tests:** `SearchServiceTest` — exact-id match ranks first; fuzzy title match; empty query returns all lessons ordered by id.

---

### Sprint 5 — Track 2: B9a — Curriculum quality, lessons 001–030

Add `hints:` (3 items) and `solution: |` to frontmatter of lessons 001–030. Tighten `assertion:` strings — upgrade bare `containsNodeOfType` to `containsLabeledWithText` or `parentChain` where appropriate. Add `## Common Mistakes` section (2–3 bullets) to each lesson body.

**Tests:** extend `ChallengeAssertionsCoverageTest` to assert `hints` non-null and `solution` non-blank for lessons 001–030.

---

### Sprint 6 — Track 1: B10a — HeapWatcher + CrashReporter + verbose logging

**New files:**

`src/main/java/com/jfxtutor/engine/runtime/HeapWatcher.java`
Daemon `ScheduledExecutorService`, 30-second interval. Reads `MemoryMXBean.getHeapMemoryUsage().getUsed()`. Logs `[WARN]` if heap grows >20 MB for 3 consecutive ticks. `snapshot()` for tests.

`src/main/java/com/jfxtutor/util/CrashReporter.java`
Installed via `Thread.setDefaultUncaughtExceptionHandler` + FX-thread override. On uncaught exception: writes `~/.javafx-tutor/crash-<timestamp>.log` (stack trace, last snippet, lesson id, Java version, OS). Shows non-blocking alert with "Open" + "Dismiss" buttons.

**Modify:**
- [JavaFxTutorApp.java](src/main/java/com/jfxtutor/app/JavaFxTutorApp.java) — install `CrashReporter`; start/stop `HeapWatcher`
- [SnippetRunner.java](src/main/java/com/jfxtutor/engine/runtime/SnippetRunner.java) — call `HeapWatcher.hint()` on each session close
- [AppLog.java](src/main/java/com/jfxtutor/util/AppLog.java) — add `verbose(tag, msg)` (gated by `AppConfig.verboseLog`); add file sink appending to `~/.javafx-tutor/log.txt` with 5 MB rotation

**Tests:** `HeapWatcherTest` (recompile 200 snippets, GC, assert heap delta < 50 MB), `CrashReporterTest` (fake uncaught exception, verify crash log created)

---

### Sprint 6 — Track 2: B5 — Achievements, streaks, time tracking

**New files:**

`src/main/java/com/jfxtutor/data/progress/Streaks.java`
Reads `Progress.completedAt` timestamps. `currentStreak()` — consecutive calendar days with ≥1 lesson completed. `longestStreak()` — all-time best. `todayCount()` — lessons completed today.

`src/main/java/com/jfxtutor/ui/AchievementsPane.java`
Non-modal `Stage`, opened by Cmd+D. Sections: streak counter, tier completion grid (9 tiles, "Master" badge at 100%), time breakdown per tier, all-time stats (lessons, challenges, hints, solutions).

**Modify:**
- [MainView.java](src/main/java/com/jfxtutor/app/MainView.java) — record `System.currentTimeMillis()` on lesson open; add elapsed seconds to `LessonRecord.timeSpentSec` on lesson switch/close; status bar shows streak: `"3-day streak  ·  Lesson 7 of 100"`
- [LessonNavigator.java](src/main/java/com/jfxtutor/ui/LessonNavigator.java) — tier rows show "Master" when completion = 100%
- [KeyBindings.java](src/main/java/com/jfxtutor/ui/KeyBindings.java) — add Cmd+D → `AchievementsPane.show()`

**Tests:** `StreaksTest` — known timestamps → assert `currentStreak()`, `longestStreak()`, edge cases (no completions, gap in middle, all today)

---

### Sprint 7 — Track 1: B7.1 — Visual editor, read-only property grid

**New packages:** `src/main/java/com/jfxtutor/design/` and `src/main/java/com/jfxtutor/ui/design/`

**New files:**

`src/main/java/com/jfxtutor/ui/design/DesignTab.java`
`ToggleButton` in the `InteractiveIde` toolbar. When on: overlays selection handles; opens `PropertyGrid` side panel. When off: removes overlay, hides grid.

`src/main/java/com/jfxtutor/ui/design/PropertyGrid.java`
`TableView<PropertyRow>` (read-only this sprint). Shows `getCssMetaData()` + common properties for selected node. Groups: Layout, Appearance, Text, Behavior. Reuses `InspectorPane.PropertyRow`.

`src/main/java/com/jfxtutor/ui/design/WidgetPaletteView.java`
Collapsible tree of 30 widget tiles (Layouts, Controls, Shapes). Tiles visible; drag not wired until Sprint 9.

`src/main/java/com/jfxtutor/design/WidgetPalette.java`
Static registry of the 30-widget set: Button, Label, TextField, CheckBox, RadioButton, VBox, HBox, StackPane, BorderPane, GridPane, Rectangle, Circle, Line, Ellipse, Polygon, Arc, Text, ImageView, ListView, TableView, TreeView, ComboBox, ChoiceBox, Slider, ProgressBar, Spinner, DatePicker, ColorPicker, TabPane, Accordion.

**Modify:**
- [PreviewHost.java](src/main/java/com/jfxtutor/ui/PreviewHost.java) — `setDesignMode(boolean)`: in design mode adds selection handles via `HighlightOverlay`; click fires `Consumer<Node>` to update `PropertyGrid`
- [InteractiveIde.java](src/main/java/com/jfxtutor/ui/InteractiveIde.java) — add `DesignTab` toggle to toolbar; instantiate `PropertyGrid`; wire selection callback

---

### Sprint 7 — Track 2: B9b — Curriculum quality, lessons 031–065

Same pattern as B9a for the Controls and Properties tiers. Tighten assertions on all 35 controls-tier lessons (most use bare `containsNodeOfType`).

---

### Sprint 8 — Track 1: B7.2 — Round-trip codegen from property grid

**New files:**

`src/main/java/com/jfxtutor/design/SourceMutator.java`
Parses snippet with JavaParser. `mutate(source, nodeId, property, newValue)` → modified source. Location strategy: id → styleClass → positional index. Ambiguous → insert `// editor-target: <uuid>` comment and anchor to it.

`src/main/java/com/jfxtutor/design/ConstructionLocator.java`
Traverses JavaParser AST to find `ObjectCreationExpr` or `MethodCallExpr` matching a live node. Heuristics: exact id → styleClass → positional index among same-type siblings.

`src/main/java/com/jfxtutor/design/LiteralFormatter.java`
Formats values as Java source: `new Insets(8,12,8,12)`, `Color.web("#3366ff")`, `Pos.CENTER`, `"text"`, `true`, `42.0`.

**Modify:**
- `PropertyGrid.java` — make cells editable; on commit call `SourceMutator.mutate()` → `EditorPane.setTextProgrammatic()` → debounced recompile; lock editor during mutation
- [EditorPane.java](src/main/java/com/jfxtutor/ui/EditorPane.java) — add `setTextProgrammatic(String)` (single undo entry); add `lock()`/`unlock()` for concurrent-edit guard
- [build.gradle.kts](build.gradle.kts) — add `implementation("com.github.javaparser:javaparser-core:3.26.+")`

**Tests:** `SourceMutatorTest` (change button text / padding / alignment on known snippets; verify output compiles), `ConstructionLocatorTest` (simple / ambiguous / positional cases)

---

### Sprint 8 — Track 2: B6 — Gist export/import + Copy as Markdown

**New files:**

`src/main/java/com/jfxtutor/share/GistClient.java`
GitHub Gist API v3 via `java.net.http.HttpClient`. `createGist(filename, content)` → URL. `fetchGist(url)` → content. PAT stored in `AppConfig.githubToken`.

`src/main/java/com/jfxtutor/share/SnippetExporter.java`
`exportToGist(source, lessonId)` — creates gist, copies URL to clipboard, shows toast. `copyAsMarkdown(source, lessonTitle, previewRoot)` — `Node.snapshot()` → Base64 PNG; wraps snippet in fenced code block; copies to clipboard.

`src/main/java/com/jfxtutor/share/SnippetImporter.java`
`importFromUrl(url)` — detects gist.github.com; fetches raw content; validates it compiles; loads into editor.

**Modify:**
- [EditorPane.java](src/main/java/com/jfxtutor/ui/EditorPane.java) — add "Share" toolbar button (hidden if no `githubToken`); "Copy as Markdown" in same menu
- [AppConfig.java](src/main/java/com/jfxtutor/data/config/AppConfig.java) — add `githubToken: String`
- [MainView.java](src/main/java/com/jfxtutor/app/MainView.java) — add "Import from URL" item; opens small paste dialog

---

### Sprint 9 — Track 1: B7.3 — Drag-from-palette codegen

**Modify:**
- `WidgetPaletteView.java` — wire `setOnDragDetected` with widget class name in `Dragboard`
- [PreviewHost.java](src/main/java/com/jfxtutor/ui/PreviewHost.java) — `setOnDragOver`/`setOnDragDropped`: pick container node at drop position; call `SourceMutator.insertChild(containerRef, widgetSource, insertIndex)`
- `SourceMutator.java` — add `insertChild(NodeRef, String, int)`: finds container in AST, inserts child expression; if container children are anonymous (`new VBox(a, b, c)`), refactors to named local vars first
- [EditorPane.java](src/main/java/com/jfxtutor/ui/EditorPane.java) — extend lock/unlock to drag operations; single undo entry on commit

**New in DesignTab:**
- Unsupported-widget tooltip: widget not in first-30 set + lesson > 030 → "Code-only for this widget", cancel drop
- "Best learned in code" banner: if lesson uses anonymous inner classes/custom controls (detected by `ConstructionLocator`), show non-blocking banner at top of Design tab

_Prototype branch `feature/visual-editor` merges to `main` after manual QA across first 30 lessons._

---

### Sprint 9 — Track 2: B9c — Curriculum quality, lessons 066–100

Hints/solutions for Events, CSS, FXML, Animation, Packaging tiers. Full assertion tightening pass across all 100. Add `nextLesson` link verification test in CI: every lesson's `nextLesson` must resolve to a real id.

---

### Sprint 10 — Track 1: B10b — Child-JVM sandbox + perf budget

**New files:**

`src/main/java/com/jfxtutor/engine/runtime/ChildJvmRunner.java`
`ProcessBuilder`-based child JVM. Sends compiled bytecodes over stdin as JSON; receives node-tree description over stdout. IPC: JSON lines — `{"type":"mount","nodes":[...]}` or `{"type":"error","message":"..."}`. `Process.destroy()` kills runaway snippets.

`src/test/java/com/jfxtutor/engine/runtime/PerformanceBudgetTest.java`
Measures cold-start and warm-recompile latency. Asserts p50 warm recompile < 250 ms.

**Modify [SnippetRunner.java](src/main/java/com/jfxtutor/engine/runtime/SnippetRunner.java):**
- Add strict-mode flag (from `AppConfig.strictSandbox`): when on, delegates to `ChildJvmRunner` instead of in-process compile
- Auto-enable strict mode after 3 consecutive `HeapWatcher` leak warnings

---

### Sprint 10 — Track 2: B3b — Accessibility audit + snapshot tests

Accessibility (Java code, not CSS):
- All interactive controls in `LessonPane`, `InspectorPane`, `TutorPane`, `LessonNavigator` get `setAccessibleText(...)` + `setAccessibleRole(...)`
- Tab traversal audit: `setFocusTraversable(true/false)` on all nodes; verify natural reading order

Snapshot tests:
- `Node.snapshot(SnapshotParameters, WritableImage)` → PNG; golden images in `src/test/resources/snapshots/`
- Capture: LessonPane challenge row (idle / pass / fail), InspectorPane with node selected, TutorPane with a reply rendered
- CI compares pixel-by-pixel (1% diff tolerance for font rendering)

---

### Sprint 11 — Both tracks: B8 — Packaging + signing + auto-update + OSS release prep

**Modify [build.gradle.kts](build.gradle.kts):**
```kotlin
plugins { id("org.beryx.runtime") version "1.13.1" }
runtime {
    jpackage {
        imageName = "JavaFX Tutor"
        appVersion = project.version.toString()
        vendor = "JavaFX Tutor Project"
        imageOptions = listOf("--mac-package-identifier", "com.jfxtutor.app")
        installerOptions = listOf("--win-dir-chooser", "--linux-shortcut")
    }
}
```

**New files:**
- `.github/workflows/release.yml` — matrix: `macos-14`, `windows-2022`, `ubuntu-22.04`; triggers on `v*.*.*` tag; uploads `.dmg`/`.msi`/`.deb` to GitHub Release
- `src/main/resources/icons/app.icns`, `app.ico`, `app.png` — 512×512 app icons
- `src/main/java/com/jfxtutor/ui/UpdateChecker.java` — `CompletableFuture` fetches GitHub releases API on startup (skipped if `AppConfig.checkForUpdates == false`); shows dismissable banner if newer version found; no silent install
- `RELEASING.md`, `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md`, `LICENSE` (MIT)

**Modify [AppConfig.java](src/main/java/com/jfxtutor/data/config/AppConfig.java):**
Add `checkForUpdates: boolean` (default true), `dismissedUpdateVersion: String`.

**README rewrite:** animated GIF, install links, feature list, "Build from source" one-liner.

---

### Sprint 12 — Release stabilization + 1.0

- Heap-leak stress test: 200 snippets, GC, assert heap delta < 50 MB
- Full manual QA: all 100 lessons, all challenges, all hints/solutions
- Fix all P0/P1 bugs
- Tag `v1.0.0` → CI builds signed installers → GitHub Release
- Post: Reddit r/JavaFX, Hacker News Show HN, dev.to

---

## Execution model (locked decisions)

1. **Arcs A and B run in parallel every sprint.**
2. **AI tutor is a 1.0 gate** — first-run wizard is required.
3. **Visual editor ships with full round-trip codegen** — B7.1 prototype at S7, full at S9.
4. **Open-source community release** — MIT, signed installers, no telemetry (or opt-in only).
5. **All progress is local** — no cloud sync, no external analytics.

---

## Out of scope

- Multi-user / cloud sync
- Mobile or web
- Translations / i18n
- IDE-style refactoring (rename, extract method)
- Multi-file snippet projects
- Third-party lesson packs / plugin system (reconsider after 1.0)

---

## Running gap table (update each sprint)

| Phase | Status |
|---|---|
| A1 — Challenge runner + assertion DSL | ✅ Done |
| A2 — Inspector v2 (engine/inspect/, hover, mirror, CSS tab, bounds tab) | ✅ Done |
| A3 — Progress persistence (ProgressStore, SnippetStore, atomic writes) | ✅ Done |
| B3a — ThemeManager, dark/light/HC CSS, KeyBindings shell | ✅ Done |
| **B1a** — Hints/solution frontmatter + LessonPane UI + SolutionDiffView | ❌ Sprint 3 |
| **B2a** — AppConfig, AnthropicClient, TutorPane, FirstRunWizard, CostMeterBar | ❌ Sprint 3 |
| **B2b** — Tool use (Tools.java, ProposedEditView), streaming polish | ❌ Sprint 4 |
| **B3b (part 1)** — System theme detect, accessibility, font-size pref, collapsible panes, CSS hot reload, PreferencesDialog | ❌ Sprint 4 |
| **B4** — SearchService, CommandPalette, recent lessons, bookmarks, tier counts | ❌ Sprint 5 |
| **B9a** — Hints/solutions + tightened assertions for lessons 001–030 | ❌ Sprint 5 |
| **B10a** — HeapWatcher, CrashReporter, verbose log + AppLog file sink | ❌ Sprint 6 |
| **B5** — Streaks.java, AchievementsPane, time tracking wired into MainView | ❌ Sprint 6 |
| **B7.1** — DesignTab, PropertyGrid (read-only), WidgetPaletteView, WidgetPalette | ❌ Sprint 7 |
| **B9b** — Hints/solutions for lessons 031–065 | ❌ Sprint 7 |
| **B7.2** — SourceMutator, ConstructionLocator, LiteralFormatter, codegen from PropertyGrid | ❌ Sprint 8 |
| **B6** — GistClient, SnippetExporter, SnippetImporter | ❌ Sprint 8 |
| **B7.3** — Drag-from-palette codegen, unsupported-widget guard | ❌ Sprint 9 |
| **B9c** — Hints/solutions for lessons 066–100, nextLesson link verification | ❌ Sprint 9 |
| **B10b** — ChildJvmRunner, strict-mode sandbox, PerformanceBudgetTest | ❌ Sprint 10 |
| **B3b (part 2)** — A11y audit, snapshot tests | ❌ Sprint 10 |
| **B8** — jpackage Gradle plugin, GitHub Actions matrix, UpdateChecker, OSS files | ❌ Sprint 11 |
| **S12** — Release stabilization + v1.0.0 tag | ❌ Sprint 12 |
