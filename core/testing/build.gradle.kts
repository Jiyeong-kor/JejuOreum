plugins {
    id("jejuoreum.android.library")
}

android {
    namespace = "com.jeong.jejuoreum.core.testing"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":domain:oreum"))
    implementation(project(":domain:user"))
    implementation(project(":domain:review"))
    implementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
