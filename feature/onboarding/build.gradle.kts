plugins {
    id("jejuoreum.android.library")
    id("jejuoreum.compose")
    id("jejuoreum.hilt")
}

android {
    namespace = "com.jeong.jejuoreum.feature.onboarding"
}

dependencies {
    implementation(project(":core:navigation"))
    implementation(project(":domain:user"))
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.coroutines.android)
}
