import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.jeong.jjoreum"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.jeong.jjoreum"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties().apply {
            val file = rootProject.file("local.properties")
            if (file.exists()) {
                file.inputStream().use { load(it) }
            }
        }
        val appKey = localProperties.getProperty("appKey") ?: ""
        buildConfigField("String", "APP_KEY", "\"$appKey\"")

        val jejuOreumBaseUrl = localProperties.getProperty("jejuOreumBaseUrl") ?: ""
        buildConfigField("String", "JEJU_OREUM_URL", "\"$jejuOreumBaseUrl\"")
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(21)
}
dependencies {

    ksp(libs.hilt.compiler)

    // Platform (BOM) - Firebase
    implementation(platform(libs.firebase.bom))

    // UI/Android Core
    implementation(libs.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.splashscreen)

    // Architecture Components (Lifecycle, ViewModel, LiveData)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Navigation
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui.ktx)

    // DI - Hilt
    implementation(libs.hilt.android)

    // Networking - Retrofit, OkHttp, Gson
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.converter.gson)

    // Firebase
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore)

    // Google Services (Maps, Location)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // Utility/Third-party Libraries (Coil, Lottie, Kakao, Permission)
    implementation(libs.coil.kt.coil)
    implementation(libs.lottie)
    implementation(libs.kakao.v2.all)
    implementation(libs.tedpermission.normal)
    implementation(libs.tedpermission.coroutine)
    implementation(libs.rxbinding)

    // Preferences
    implementation(libs.androidx.preference.ktx)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
