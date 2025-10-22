plugins {
    id("jejuoreum.kotlin.library")
    id("jejuoreum.hilt")
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hilt.navigation.compose)
}
