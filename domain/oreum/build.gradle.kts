plugins {
    id("jejuoreum.kotlin.library")
}

group = "com.jeong.jejuoreum.domain.oreum"

dependencies {
    implementation(project(":core:common"))
    implementation(project(":domain:review"))
    implementation(project(":domain:user"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
