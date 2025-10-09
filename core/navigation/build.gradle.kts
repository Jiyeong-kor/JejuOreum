plugins {
    id("jejuoreum.android.library")
}

android {
    namespace = "com.jeong.jejuoreum.core.navigation"
}

dependencies {
    implementation(libs.androidx.navigation.runtime.ktx)
}
