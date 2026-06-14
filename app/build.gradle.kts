plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "com.example.appdortarchile20"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.appdortarchile20"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Base
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    kspAndroidTest("androidx.room:room-compiler:2.6.1") // 🟢 LA NUEVA LÍNEA PARA QUE ROOM COMPILE EN PRUEBAS

    // Navegación
    implementation(libs.androidx.navigation.compose)

    // Coil
    implementation(libs.coil.compose)

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // Material Icons Extended
    implementation("androidx.compose.material:material-icons-extended")

    // OpenStreetMap
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    // GPS
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // SplashScreen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // ==========================================
    // 🛠️ LIBRERÍAS DE TESTING (Unificadas)
    // ==========================================

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // UI Tooling nativo (de tu versión anterior)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // 🟢 Refuerzo manual para quitar los rojos de Compose Test (createComposeRule, assertIsDisplayed, etc.)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.8")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.8")

    // 🟢 Refuerzo manual para quitar los rojos de JUnit (@Test, @RunWith, @Rule)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:runner:1.5.2")

    // 🟢 Dependencia necesaria para que Room funcione correctamente en la carpeta androidTest
    androidTestImplementation("androidx.room:room-testing:2.6.1")
    // 🟢 Agrega estas líneas al final de tus dependencias
    androidTestImplementation("androidx.compose.runtime:runtime:1.6.8")
    androidTestImplementation("androidx.compose.foundation:foundation:1.6.8")
    androidTestImplementation("androidx.compose.material3:material3:1.2.1")
}