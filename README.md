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
6. **Glance widgets** — Year Progress and Life Matrix, with in-app pin via
   `AppWidgetManager.requestPinAppWidget`.

## CI/CD

`.github/workflows/ci_cd.yml` runs detekt + Android Lint + unit tests + `assembleDebug`
on every push and PR; tags starting with `v` build a signed AAB.

## Documentation

See [`LEARN.md`](./LEARN.md) for the running design log and per-cycle change entries.
