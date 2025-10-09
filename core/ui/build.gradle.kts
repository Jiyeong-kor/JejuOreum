plugins {
    id("jejuoreum.android.library")
    id("jejuoreum.compose")
    id("jejuoreum.hilt")
}

android {
    namespace = "com.jeong.jejuoreum.core.ui"
}

dependencies {
    implementation(project(":core:designsystem"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.coil3.coil)
    implementation(libs.coil3.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.coil.bom))
}
