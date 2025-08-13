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
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.plugin.compose)
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
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        // optIn.add("kotlin.RequiresOptIn") 등 필요 시 추가
    }
    jvmToolchain(17)
}

dependencies {

    //hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

    //compose기반
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)

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
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.navigation.fragment.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}