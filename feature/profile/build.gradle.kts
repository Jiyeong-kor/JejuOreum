plugins {
    id("jejuoreum.android.library")
    id("jejuoreum.compose")
    id("jejuoreum.hilt")
}

android {
    namespace = "com.jeong.jejuoreum.feature.profile"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:architecture"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))
    implementation(project(":domain:oreum"))
    implementation(project(":domain:user"))
    implementation(project(":data:oreum"))
    implementation(project(":data:user"))
}
