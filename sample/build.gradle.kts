plugins {
    id("com.android.application")
}

android {
    namespace = "com.jardy.colorpicker.sample"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.jardy.colorpicker.sample"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":colorpicker"))
    // The sample deliberately uses a plain AppCompat theme (not Material) to prove the dialog copes.
    implementation("androidx.appcompat:appcompat:1.7.1")
}
