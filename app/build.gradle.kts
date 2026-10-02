plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.knownassurajit.clndr_widget.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.knownassurajit.clndr_widget.app"
        minSdk = 26
        targetSdk = 36

        // Deterministic versioning formula. Local builds use this versionCode.
        // CI may set CLNDR_VERSION_CODE (absolute, used for rollbacks) or
        // CLNDR_VERSION_CODE_OFFSET (added so each master merge is newer on Play).
        val major = 0
        val minor = 0
        val patch = 0
        val build = 5

        val computedVersionCode = major * 1_000_000 + minor * 10_000 + patch * 100 + build
        val versionCodeOverride = System.getenv("CLNDR_VERSION_CODE")?.toIntOrNull()?.takeIf { it > 0 }
        val versionCodeOffset = System.getenv("CLNDR_VERSION_CODE_OFFSET")?.toIntOrNull()?.takeIf { it > 0 } ?: 0
        versionCode = versionCodeOverride ?: (computedVersionCode + versionCodeOffset)
        versionName = "$major.$minor.$patch.$build"

        resourceConfigurations += listOf("en", "ar", "de", "es-rES", "es-rUS", "fr", "he", "hr", "hu", "in", "it", "ja", "nl", "pl", "pt-rBR", "ru-rRU", "sv", "tr", "uk", "zh")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val releaseStoreFile = System.getenv("CLNDR_STORE_FILE")
    val hasReleaseKeystore = !releaseStoreFile.isNullOrEmpty() && file(releaseStoreFile).exists()
    val requireReleaseSigning = System.getenv("CLNDR_REQUIRE_RELEASE_SIGNING") == "true"
    if (requireReleaseSigning && !hasReleaseKeystore) {
        error(
            "Signed release requires CLNDR_STORE_FILE to point at a keystore. " +
                "CI secrets: KEYSTORE_B64, CLNDR_STORE_PASSWORD, CLNDR_KEY_ALIAS, CLNDR_KEY_PASSWORD.",
        )
    }

    signingConfigs {
        create("release") {
            releaseStoreFile?.takeIf { hasReleaseKeystore }?.let { path ->
                storeFile = file(path)
                storePassword = System.getenv("CLNDR_STORE_PASSWORD")
                keyAlias = System.getenv("CLNDR_KEY_ALIAS")
                keyPassword = System.getenv("CLNDR_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            if (hasReleaseKeystore || requireReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    dependenciesInfo {
        includeInApk = true
        includeInBundle = true
    }
    buildToolsVersion = "36.0.0"
}

dependencies {
    implementation(project(":core:datetime"))
    implementation(project(":core:database"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:domain"))
    implementation(project(":feature:lifegrid"))
    implementation(project(":feature:milestones"))
    implementation(project(":feature:widgets"))

    implementation(libs.glance.appwidget)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.glance.appwidget)

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.work)
    ksp(libs.hilt.compiler)
    ksp(libs.hilt.work.compiler)

    implementation(libs.work.runtime.ktx)
    implementation(libs.kotlinx.coroutines.android)

    coreLibraryDesugaring(libs.android.desugarjdklibs)

    testImplementation(libs.junit)
    testImplementation(libs.google.truth)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.runner)
}

tasks.withType<Test> {
    testLogging {
        showStandardStreams = true
    }
}
