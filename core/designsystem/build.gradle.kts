plugins {
    id("jejuoreum.android.library")
    id("jejuoreum.compose")
}

android {
    namespace = "com.jeong.jejuoreum.core.designsystem"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
}
