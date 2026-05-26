plugins {
    alias(libs.plugins.android.application) // Android application plugin
    alias(libs.plugins.kotlin.android)      // Kotlin Android support
    alias(libs.plugins.kotlin.compose)      // Compose compiler plugin (Kotlin 2.x)
    alias(libs.plugins.hilt.android)        // Hilt dependency injection plugin
    kotlin("kapt")                          // Annotation processing (for Hilt)
}

android {
    namespace = "com.vedantraut.herohub"    // App package namespace
    compileSdk = 34                         // Compile SDK version

    defaultConfig {
        applicationId = "com.vedantraut.herohub" // Unique app ID
        minSdk = 24                              // Minimum supported Android version
        targetSdk = 34                           // Target Android version

        versionCode = 1                          // App version code
        versionName = "1.0"                      // App version name
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 // Java source compatibility
        targetCompatibility = JavaVersion.VERSION_17 // Java target compatibility
    }

    kotlinOptions {
        jvmTarget = "17"                         // Kotlin JVM target version
    }

    buildFeatures {
        compose = true                          // Enable Jetpack Compose
    }
}

kapt {
    correctErrorTypes = true                    // Fixes generated code type issues (Hilt)
}

dependencies {

    implementation(platform(libs.androidx.compose.bom)) // Compose BOM for version management

    implementation(libs.androidx.core.ktx)              // Core Kotlin extensions
    implementation(libs.androidx.lifecycle.runtime.ktx) // Lifecycle runtime support
    implementation(libs.androidx.activity.compose)      // Activity integration with Compose

    implementation(libs.androidx.compose.ui)            // Core Compose UI
    implementation(libs.androidx.compose.ui.graphics)   // Compose graphics support
    implementation(libs.androidx.compose.material3)     // Material 3 components
    implementation(libs.androidx.compose.ui.tooling.preview) // Preview support

    debugImplementation(libs.androidx.compose.ui.tooling) // Debug UI tools

    implementation(libs.hilt.android)                   // Hilt core library
    kapt(libs.hilt.compiler)                            // Hilt annotation processor
}