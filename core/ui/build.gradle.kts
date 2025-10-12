plugins {
    id("jejuoreum.android.library")
    id("jejuoreum.compose")
}

android {
    namespace = "com.jeong.jejuoreum.core.ui"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:presentation"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
}
