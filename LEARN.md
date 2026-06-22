# LEARN.md

A running log of design choices, gotchas, and "next time, do this differently" notes for the
**clndr** Android app. Every implementation cycle appends a Section 2 entry.

## 0. Architecture invariants

- **Date math lives in `:core:datetime`.** UI must never call `LocalDate.now()` directly —
  always go through a `Clock` injected from `DomainModule`. This keeps tests deterministic.
- **Persistence flows through `:core:database` DAOs accessed via `:core:domain` repositories.**
  Feature modules never import Room types directly.
- **Strict monochrome.** Non-grayscale `Color(...)` literals in `:feature:*` modules are a
  detekt violation (see `config/detekt/detekt.yml`).
- **Glance widgets do not import `MaterialTheme`.** They use `WidgetTheme.colors(context)`
  which maps the M3 `ColorScheme` to Glance `ColorProviders`.
- **Hilt-Glance boundary.** Hilt injects only into `GlanceAppWidgetReceiver`; the
  `GlanceAppWidget` instance is constructed by hand and receives dependencies via its
  constructor or top-level Glance state.

## 1. Hard-won facts

- Kotlin **2.0** ships the Compose Compiler via the `org.jetbrains.kotlin.plugin.compose`
  plugin. Do **not** pin a separate `androidx.compose.compiler:compiler` version — that's
  the historical #1 build break.
- KSP version must match Kotlin exactly. Catalog pin is `2.0.21-1.0.27` for Kotlin
  `2.0.21`; if you bump Kotlin, bump KSP in lockstep.
- `java.time` on `minSdk 26` requires `coreLibraryDesugaring(libs.android.desugarjdklibs)`
  in every Android module and `isCoreLibraryDesugaringEnabled = true`. Forgetting this
  crashes `LocalDate.now()` at runtime, not compile time.
- Glance 1.1 `glance-material3` interop ships its own `ColorProviders`. M3 `MaterialTheme`
  is silently ignored inside `provideContent { ... }`.
- `AlarmManager.canScheduleExactAlarms()` is API 31+. Code must fall back to
  `setAndAllowWhileIdle` when exact alarms are denied and deep-link the user to
  `Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM`.
- `requestPinAppWidget` is launcher-dependent. Always gate on
  `AppWidgetManager.isRequestPinAppWidgetSupported` and provide a toast fallback.

## 2. Cycle log

### 2026-05-31 — Initial Android rewrite

**Scope**: Replaced the React/Vite web app at the repo root with a multi-module Android
project (`com.knownassurajit.clndr_widget.app`). All eight modules created: `:app`, `:core:{datetime, database,
designsystem, domain}`, `:feature:{lifegrid, milestones, widgets}`. Bootstrapped version
catalog, Gradle wrapper 8.9, AGP 8.6.1, Kotlin 2.0.21, Hilt 2.52, Room 2.6.1, Glance 1.1.1.

**Key decisions**
- `minSdk = 26`, `targetSdk = 35`, `compileSdk = 35`. Justification: spec says "Android 14+";
  Glance + AlarmManager `setExactAndAllowWhileIdle` work on 26+.
- Days granularity (~40 k cells) renders in a single `Canvas` composable with a packed
  `IntArray` (2 bits per cell) + viewport-clipped drawing. Pan/zoom updates a
  `MutableFloatState` so only the Canvas recomposes.
- `ProgressEngine.observe(...)` aligns its tick to wall-clock seconds via
  `delay(1_000 - currentMillis % 1000)` on `Dispatchers.Default` to keep the UI thread
  free.
- Past milestones are filtered, not deleted — they convert to a count-up tracker. The
  `delete` DAO method exists only for explicit user removal.

**Blueprint (data flow)**

```
SettingsRepository (DataStore) ──┐
                                 │
LifeGridCalculator (java.time) ──┤
                                 ├──> GetLifeGridUseCase ──> LifeGridViewModel ──> LifeGridScreen ──> DaysCanvasGrid / LowDensityGrid
                                 │
ProgressEngine (1 Hz tick) ──────┴──> GetYearProgressUseCase ──> YearProgressViewModel ──> YearProgressScreen

MilestonesRepository (Room) ──> MilestonesListViewModel ──> MilestonesListScreen
                            └─> MilestoneEditViewModel  ──> MilestoneEditScreen
                            └─> MilestoneReminderScheduler ──> AlarmManager ──> MilestoneReminderReceiver ──> NotificationManager

SunriseSunsetEngine ──> rememberSunIsUp() ──> ClndrTheme(SUNRISE_AUTO) ──┐
SettingsRepository.themeMode ──────────────────────────────────────────┴─> WidgetUpdater.updateAll() ──> Glance widgets
```

**Open follow-ups**
- Wire the SettingsSheet date picker (`onEditBirthDate`) — current stub is a no-op.
- Replace the system-font typography fallback in `:core:designsystem` with the Poppins +
  JetBrains Mono downloadable fonts.
- Surface exact-alarm permission denial via `MilestoneEditEffect.RequestExactAlarmPermission`
  in MainActivity.
- Mirror DataStore birthday into a sidecar `SharedPreferences` so Glance widgets can read
  it without depending on `:app`.
- Add Room schema export `1.json` once Room runs locally.

## 3. Update rule

After every CI-green merge to `develop`, the implementing agent MUST append a Section 2
entry summarising:

1. Scope of change.
2. Any deviation from the plan and why.
3. New gotchas discovered (promote structural ones to Section 1).
4. Next-cycle followups.

## 4. Decisions deferred

- **Widget update cadence.** Glance widgets currently re-compose only on broadcast. If
  battery is fine, a 15-minute `PeriodicWorkRequest` may give livelier widgets. Decide
  after first dogfooding pass.
- **`USE_EXACT_ALARM` Play console categorisation.** The manifest declares it for
  reminder/calendar use, but Play may require justification at publish time. Re-evaluate
  before the first store upload.
- **DataStore ↔ Glance bridge.** WidgetSettings reads SharedPreferences as a placeholder;
  decide whether to migrate to a multi-process DataStore or push state via Glance state
  store.
