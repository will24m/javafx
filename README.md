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

Phase 0 (skeleton + hardcoded Hello World lesson) — see [PLAN.md](PLAN.md)
for the phased roadmap.
