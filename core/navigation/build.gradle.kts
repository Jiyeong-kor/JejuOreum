plugins {
    id("jejuoreum.android.library")
    id("jejuoreum.compose")
}

android {
    namespace = "com.jeong.jejuoreum.core.navigation"
}

dependencies {
    implementation(project(":feature:detail"))
    implementation(project(":feature:map"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:profile"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui)
    implementation(libs.hilt.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
}
