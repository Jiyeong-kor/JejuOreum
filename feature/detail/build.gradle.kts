plugins {
    id("jejuoreum.android.library")
    id("jejuoreum.compose")
    id("jejuoreum.hilt")
}

android {
    namespace = "com.jeong.jejuoreum.feature.detail"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:presentation"))
    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))
    implementation(project(":domain:oreum"))
    implementation(project(":domain:review"))
    implementation(project(":domain:user"))
}
