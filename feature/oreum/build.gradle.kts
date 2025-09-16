plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.plugin.compose)
}

android {
    namespace = "com.jeong.feature.oreum"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) }
    jvmToolchain(17)
}

dependencies {

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui)
    implementation(libs.coil3.coil)
    implementation(libs.coil3.coil.compose)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.hilt.android)
    implementation(libs.kakao.vectormap)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.timber)
    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.coil.bom))
    implementation(project(":data"))
    implementation(project(":domain"))
    ksp(libs.hilt.android.compiler)
    testImplementation(libs.junit)
}
