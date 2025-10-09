plugins {
    id("jejuoreum.kotlin.library")
}

group = "com.jeong.jejuoreum.domain.user"

dependencies {
    implementation(project(":core:common"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
