plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

fun prop(name: String, default: String): String =
    (project.findProperty(name) as String?)?.takeIf { it.isNotBlank() } ?: default

android {
    namespace = "app.masolo.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "app.masolo.android"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    signingConfigs {
        create("release") {
            val ks = project.findProperty("MASOLO_KEYSTORE") as String?
            if (!ks.isNullOrBlank()) {
                storeFile = file(ks)
                storePassword = project.findProperty("MASOLO_KEYSTORE_PASSWORD") as String?
                keyAlias = project.findProperty("MASOLO_KEY_ALIAS") as String?
                keyPassword = project.findProperty("MASOLO_KEY_PASSWORD") as String?
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            buildConfigField("String", "BASE_URL", "\"${prop("MASOLO_DEBUG_URL", "http://10.0.2.2/masolo/")}\"")
            manifestPlaceholders["usesCleartext"] = "true"      // http autorisé en développement uniquement
        }
        release {
            isMinifyEnabled = false
            buildConfigField("String", "BASE_URL", "\"${prop("MASOLO_PROD_URL", "https://masolo.example.com/")}\"")
            manifestPlaceholders["usesCleartext"] = "false"
            val hasKeystore = !(project.findProperty("MASOLO_KEYSTORE") as String?).isNullOrBlank()
            signingConfig = if (hasKeystore) signingConfigs.getByName("release") else signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
}
