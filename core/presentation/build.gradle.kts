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
    implementation(project(":core:common"))
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(project(":core:testing"))
    testImplementation(libs.kotlinx.coroutines.test)
}
