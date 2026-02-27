plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.imagetopdf"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.imagetopdf"
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // 1. Navigation (For moving between Home, Settings, Account)
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // 2. Extended Icons (For the specific icons like "Settings", "Person", "Edit")
    // The basic material library only has a few icons. This one has ALL of them.
    implementation("androidx.compose.material:material-icons-extended")

    // 3. Image Loading (Coil)
    // You need this to load images from the user's gallery efficiently.
    implementation("io.coil-kt:coil-compose:2.6.0")

    // 4. ViewModel (For managing your app's data/state)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // 5. PDF Viewer (Optional - for viewing the PDFs you create)
    // There isn't a native Compose PDF viewer yet, but this wraps the Android native one nicely.
    // Use this later when you actually build the viewer screen.
    // implementation("io.github.grizeldi:studypdf:1.0.0") // Example, or use AndroidPdfViewer

    // 6. Supabase
    implementation(libs.supabase.gotrue)
    implementation(libs.ktor.client.android)
}