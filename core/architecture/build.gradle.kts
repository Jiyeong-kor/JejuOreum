plugins {
    id("jejuoreum.android.library")
}

android {
    namespace = "com.jeong.jejuoreum.core.architecture"
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.annotation)
}
