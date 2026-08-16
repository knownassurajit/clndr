# 📱 Clndr App Module (`clndr/app`)

The `app` module serves as the primary entry point for the Clndr Android widget & calendar application. It orchestrates dependency injection, Glance widget host services, activity navigation, and theme initialization.

---

## 🏗️ Architecture & Component Layout

```text
app/
├── src/main/
│   ├── java/com/knownassurajit/clndr/
│   │   ├── MainActivity.kt        # Primary Jetpack Compose entry activity
│   │   ├── ClndrApplication.kt    # Application subclass initializing timber logging & DI
│   │   └── ui/                    # App-level Compose theme tokens & scaffolds
│   ├── res/                       # Android XML drawables, values, strings, & launcher assets
│   └── AndroidManifest.xml        # App permissions, widget receiver declarations, entry points
├── build.gradle.kts               # Android App module dependencies & packaging rules
└── proguard-rules.pro             # R8 / ProGuard obfuscation & code shrinking rules
```

---

## ⚙️ Build & Verification Commands

```bash
# Build debug APK
./gradlew :app:assembleDebug

# Run app module unit tests
./gradlew :app:testDebugUnitTest
```
