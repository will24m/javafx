# javafx-tutor

A desktop application **written in JavaFX** that teaches **JavaFX** by being
a working example of itself.

See [PLAN.md](PLAN.md) for the full design.

## Prerequisites

- macOS / Linux / Windows
- **Temurin 21 LTS** (`brew install --cask temurin@21` on macOS)

Gradle is not required — the wrapper (`./gradlew`) bootstraps it.

## Run

```bash
./gradlew run
```

## Test

```bash
./gradlew test
```

## Current status

Phase 2 in progress: curriculum loading, markdown rendering, RichTextFX editing,
and live in-memory snippet compilation are present. See [PLAN.md](PLAN.md) for
the phased roadmap and remaining risks.
