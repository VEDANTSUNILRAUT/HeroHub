plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.vedantraut.herohub"    // App package namespace
    compileSdk = 35                         // Compile SDK version (Android 15)

    defaultConfig {
        applicationId = "com.vedantraut.herohub" // Unique app ID
        minSdk = 26                            // Minimum supported Android version
        targetSdk = 35                           // Target Android version (Android 15)

        versionCode = 1                          // App version code
        versionName = "1.0"                      // App version name

        vectorDrawables {
            useSupportLibrary = true
        }
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

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation("com.google.android.material:material:1.12.0")

    debugImplementation(libs.androidx.compose.ui.tooling)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    //SplashScreen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Coil
    implementation(libs.coil.compose)

}