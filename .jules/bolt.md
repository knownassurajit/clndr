## 2024-06-27 - UI Thread Formatter Memoization
**Learning:** `DateTimeFormatter.format()` and `String.format()` calls inside Compose functions tracking live cycles (like `YearProgressScreen` ticking every second) cause heavy unnecessary UI thread allocations when the underlying string (like today's date) only changes daily.
**Action:** Use `remember` keyed by the day's percentage or actual day to memoize expensive format operations that only change per day.
