plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.knownassurajit.clndr_widget.feature.widgets"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
}

dependencies {
    implementation(project(":core:datetime"))
    implementation(project(":core:domain"))
    implementation(project(":core:designsystem"))

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.glance.appwidget)
    implementation(libs.glance.material3)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)

    coreLibraryDesugaring(libs.android.desugarjdklibs)

    androidTestImplementation(libs.androidx.test.runner)
}
