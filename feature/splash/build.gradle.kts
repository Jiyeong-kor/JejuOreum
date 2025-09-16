import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.jeong.feature.splash"
    compileSdk = 36

    defaultConfig {
        minSdk = 23

        val localProperties = Properties().apply {
            val file = rootProject.file("local.properties")
            if (file.exists()) {
                file.inputStream().use { load(it) }
            }
        }
        val appKey = localProperties.getProperty("appKey") ?: ""
        buildConfigField("String", "APP_KEY", "\"$appKey\"")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
    jvmToolchain(17)
}

dependencies {

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.hilt.android)
    implementation(libs.kakao.vectormap)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.play.services.basement)
    implementation(libs.timber)
    implementation(project(":data"))
    implementation(project(":domain"))
    ksp(libs.hilt.android.compiler)
}
