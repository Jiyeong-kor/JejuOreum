plugins {
    id("jejuoreum.android.library")
    id("jejuoreum.hilt")
}

kotlin {
    explicitApi()
}

android {
    namespace = "com.jeong.jejuoreum.core.presentation"
}

dependencies {

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(project(":core:common"))
    testImplementation(project(":core:testing"))
}
