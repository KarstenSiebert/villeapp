plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.siehog.ville"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.siehog.ville"
        minSdk = 30
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
        viewBinding = true
    }
}

var cameraxversion = "1.4.2"

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("androidx.camera:camera-core:${cameraxversion}")
    implementation("androidx.camera:camera-camera2:${cameraxversion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxversion}")
    implementation("androidx.camera:camera-view:${cameraxversion}")
    implementation("androidx.camera:camera-extensions:${cameraxversion}")

    implementation("androidx.biometric:biometric:1.1.0")

    implementation(platform("com.squareup.okhttp3:okhttp-bom:5.3.2"))

    implementation("com.squareup.okhttp3:okhttp")

    implementation("com.google.mlkit:barcode-scanning:17.3.0")
}