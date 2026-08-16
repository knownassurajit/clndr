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
  which maps the M3 palettes to Glance `ColorProvider`s, honoring in-app Light/Dark when
  "Widgets follow app theme" is on.
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
- Do **not** commit `gradle/gradle-daemon-jvm.properties` with a vendor pin. Microsoft 21
  works locally here; GitHub Actions installs Temurin 17. The file made every CI run fail
  with `Cannot find a Java installation … vendor matching('microsoft')`. Let the wrapper
  use `JAVA_HOME`; modules still compile with `jvmToolchain(17)`.

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
- Mirror DataStore birthday **and** theme into a sidecar `SharedPreferences` so Glance widgets can read
  them without depending on `:app`.
- Add Room schema export `1.json` once Room runs locally.

### 2026-08-16 — Icons, widgets, system tools, CI

**Scope**: Adaptive/round/monochrome launcher icons + density PNGs; Glance `SizeMode.Responsive` layouts and in-app widget theme; calendar upsert + Clock `ACTION_SET_ALARM`; nav/theme/performance polish; CI daemon JVM 17; `ci_cd.yml` pre-releases only from develop; `play-release.yml` is the sole stable GitHub + Play publisher.

**Deviations**
- Widget expanded year calendar is a 12-month strip, not a 372-cell day matrix (Glance box count).
- Clock alarms are fire-and-forget into the system Clock app (no alarm id to persist).

**Gotchas**
- Play step `if: env.PLAY_CONSOLE_JSON != ''` is false unless the secret is copied onto **job-level** `env`.
- `CalendarMirror` must query a writable calendar; hardcoded id `1` fails on most devices.
- Gradle daemon JVM criteria are independent of `jvmToolchain(17)` — mismatch reds all CI.

**Follow-ups**
- Dogfood 15-minute widget `PeriodicWorkRequest` vs battery.
- Play Console justification for `USE_EXACT_ALARM`.
- Optional multi-process DataStore instead of the SharedPreferences sidecar.

## 3. Update rule

After every CI-green merge to `develop`, the implementing agent MUST append a Section 2
entry summarising:

1. Scope of change.
2. Any deviation from the plan and why.
3. New gotchas discovered (promote structural ones to Section 1).
4. Next-cycle followups.

## 4. Decisions deferred

- **`USE_EXACT_ALARM` Play console categorisation.** The manifest declares it for
  reminder/calendar use, but Play may require justification at publish time. Re-evaluate
  before the first store upload.
- **DataStore ↔ Glance bridge.** WidgetSettings still reads a SharedPreferences sidecar;
  a multi-process DataStore remains optional.
