plugins {
    id("jejuoreum.android.library")
    id("jejuoreum.compose")
}

android {
    namespace = "com.jeong.jejuoreum.core.navigation"
}

dependencies {
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.hilt.android)
    implementation(libs.javax.inject)
}
