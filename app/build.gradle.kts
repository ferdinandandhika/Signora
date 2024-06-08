plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.capstone.signora"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.capstone.signora"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation ("com.airbnb.android:lottie:6.4.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation (libs.firebase.auth.v2101)
    implementation (libs.firebase.core)
    implementation (libs.firebase.firestore)
    implementation(libs.firebase.auth.ktx)
    implementation (libs.firebase.storage)
    implementation(libs.circle.image.view)
    implementation(libs.firebase.database)
    implementation (libs.firebase.appcheck.safetynet)
    implementation (libs.firebase.appcheck)
    implementation (libs.firebase.appcheck.playintegrity)
    testImplementation(libs.junit)
    implementation (libs.glide)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    annotationProcessor (libs.compiler.v4120)
    annotationProcessor (libs.compiler)
}