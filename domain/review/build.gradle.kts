plugins {
    id("jejuoreum.kotlin.library")
}

group = "com.jeong.jejuoreum.domain.review"

dependencies {
    implementation(project(":core:common"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
