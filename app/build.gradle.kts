import java.util.Properties

android {
    defaultConfig {
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
}

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
        //noinspection OldTargetApi
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.android)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.coil.kt.coil)
    implementation(libs.converter.gson)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.hilt.android)
    implementation(libs.kakao.v2.all)
    implementation(libs.logging.interceptor)
    implementation(libs.lottie)
    implementation(libs.material)
    implementation(libs.okhttp)
    implementation(libs.okhttp)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.retrofit)
    implementation(libs.rxbinding)
    implementation(libs.tedpermission.coroutine)
    implementation(libs.tedpermission.normal)
    implementation(platform(libs.firebase.bom))
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}