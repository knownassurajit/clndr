# clndr

A minimal, monochrome, widget-first Android calendar focused on **macro time perspective**:
a 110-year life grid, your year in progress, and the milestones that shape it.

## Stack

- **Kotlin 2.0**, **Jetpack Compose** (Material 3), **Jetpack Glance** for home-screen widgets.
- **Room 2.6** for persistence, **Hilt 2.52** for DI.
- **`java.time`** with core-library desugaring (works on `minSdk 26`).
- **AGP 8.6**, **Gradle 8.9**, KSP for codegen.

## Modules

```
:app
 ├─ :feature:lifegrid, :feature:milestones, :feature:widgets
 ├─ :core:designsystem, :core:domain
:feature:* ─> :core:{designsystem, domain, datetime}
:core:domain ─> :core:{database, datetime}
:core:database   (Room library)
:core:designsystem (Compose + M3)
:core:datetime    (pure-JVM math; tested without Android)
```

## Build

```bash
./gradlew assembleDebug                 # build the APK
./gradlew :app:installDebug             # install on a connected device/emulator
./gradlew test                          # all unit tests across modules
./gradlew :core:datetime:test           # pure-JVM math engine (fast)
./gradlew connectedDebugAndroidTest     # Room DAO tests on emulator
./gradlew detekt lintDebug              # static analysis
```

JDK 17 (Temurin) is required.

## Features

1. **110-year life grid** at four granularities — Days (~40 k cells), Weeks, Months, Years.
   Days use a single `Canvas` with viewport-clipped drawing; the rest use `LazyVerticalGrid`.
2. **Year calendar** — leap-year-aware 12-month matrix with past/present/future states.
3. **Year in progress** — Era / Decade / Year / Month / Week / Day progress, ticking once
   per wall-clock second on `Dispatchers.Default`.
4. **Milestones** — Room-backed events with `AlarmManager` exact-alarm reminders, boot
   re-scheduling via `WorkManager`, optional `CalendarContract` mirror.
5. **Strict monochrome theme** with optional sunrise/sunset auto-switch (NOAA simplified
   formula — no location permission required).
6. **Glance widgets** — Year Progress, Year Calendar, Life Matrix, and Goals. Compact / medium / expanded layouts, in-app Light/Dark (or system) theming, 15-minute refresh, and pin from Settings.

## CI/CD

A single workflow, `.github/workflows/ci-cd.yml`, drives the whole pipeline in four jobs:

- **`test`** — runs on every push to `develop`/`master` and on all pull requests. Runs inside a containerized `eclipse-temurin:17-jdk-jammy` job (`unzip`/`curl` are installed first so `android-actions/setup-android@v3` can fetch the SDK) for a reproducible, host-independent build environment. Runs detekt, Android Lint, unit tests, the pure-JVM `:core:datetime:test` suite, and `assembleDebug`; uploads the debug APK (on `develop` success), the detekt report (always), and test reports (on failure).
- **`debug-release`** — on push to `develop`, after `test` passes: extracts the version, builds a changelog from recent commits, decodes the signing keystore from `KEYSTORE_B64` (falling back to an unsigned build if the secret is absent), runs `assembleDebug`, and publishes a GitHub pre-release tagged `v$versionName-dev.$RUN_NUMBER`.
- **`pr-summary`** — on pull requests targeting `master`: re-runs the containerized checks to capture per-check pass/fail status and detekt finding counts, computes the version delta and commit count since the last stable tag, and posts a detailed summary both to the workflow's step summary and as a comment on the PR (created or updated via `actions/github-script@v7`). This is the pre-merge visibility step before code reaches `master`.
- **`stable-release`** — on push to `master`, after `test` passes: re-runs checks, builds a signed release APK + AAB, force-moves a `release/clndr/$versionName` branch to the built commit, publishes a stable GitHub release with both artifacts attached, and (only when the `PLAY_CONSOLE_JSON` secret is non-empty) uploads the AAB to the Google Play **internal** track.

Required secrets: `KEYSTORE_B64`, `CLNDR_STORE_PASSWORD`, `CLNDR_KEY_ALIAS`, `CLNDR_KEY_PASSWORD` for signing, and optionally `PLAY_CONSOLE_JSON` for Play Store uploads.

## Documentation

See [`LEARN.md`](./LEARN.md) for the running design log and per-cycle change entries.
