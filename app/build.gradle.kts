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

var cameraversion = "1.5.3"

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.legacy.support.v4)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("androidx.camera:camera-core:${cameraversion}")
    implementation("androidx.camera:camera-camera2:${cameraversion}")
    implementation("androidx.camera:camera-lifecycle:${cameraversion}")
    implementation("androidx.camera:camera-view:${cameraversion}")
    implementation("androidx.camera:camera-extensions:${cameraversion}")

    implementation("androidx.biometric:biometric:1.1.0")

    implementation(platform("com.squareup.okhttp3:okhttp-bom:5.3.2"))

    implementation("com.squareup.okhttp3:okhttp")

    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    implementation("com.google.android.gms:play-services-location:21.3.0")

    implementation("com.squareup.okhttp3:okhttp-urlconnection:5.3.2")
}