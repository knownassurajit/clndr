# Keep Room generated classes
-keep class * extends androidx.room.RoomDatabase
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp
-keep class * extends androidx.hilt.work.HiltWorkerFactory

# Kotlin coroutines
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }

# R8 in AGP 8.9 flags the java.time backport name; the AAB contains j$.time.DesugarLocalDate.
-dontwarn java.time.DesugarLocalDate
