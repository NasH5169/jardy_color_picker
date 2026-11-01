plugins {
    id("com.android.library")
    `maven-publish`
}

android {
    namespace = "com.jardy.colorpicker"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

// No dependency on purpose: the dialog only uses framework classes and its own
// theme, so host apps (AppCompat, Material, or plain framework themes) are unaffected.

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.jardy"
                artifactId = "colorpicker"
                version = "1.0.2"
            }
        }
    }
}
