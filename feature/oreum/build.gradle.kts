plugins {
    id("jejuoreum.android.library")
    id("jejuoreum.compose")
    id("jejuoreum.hilt")
    kotlin("plugin.parcelize")
}

android {
    namespace = "com.jeong.jejuoreum.feature.oreum"
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
    implementation(libs.hilt.navigation.compose)
    implementation(libs.kakao.vectormap)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.timber)
    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.coil.bom))
    implementation(project(":core:ui"))
    implementation(project(":core:utils"))
    implementation(project(":domain"))
    testImplementation(libs.junit)
}
