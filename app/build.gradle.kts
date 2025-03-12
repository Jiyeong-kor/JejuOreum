plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.jeong.jjoreum"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jeong.jjoreum"
        minSdk = 23
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.firebase.firestore)
    implementation(platform(libs.firebase.bom))
    implementation(libs.rxbinding)
    implementation(libs.lottie)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.play.services.location)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.converter.gson)
    implementation(libs.retrofit)
    implementation(libs.android)
    implementation(libs.kakao.v2.all)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.tedpermission.coroutine)
    implementation(libs.tedpermission.normal)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.coil.kt.coil)
    implementation(libs.okhttp)
    implementation(libs.play.services.maps)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}