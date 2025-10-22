import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.bundling.Jar

plugins {
    id("jejuoreum.android.library")
    id("jejuoreum.compose")
    id("jejuoreum.hilt")
}

android {
    namespace = "com.jeong.jejuoreum.feature.detail"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:architecture"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))
    implementation(project(":domain:oreum"))
    implementation(project(":domain:review"))
    implementation(project(":domain:user"))
    implementation(project(":data:oreum"))
    implementation(project(":data:review"))
    implementation(project(":data:user"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.coil.bom))

    implementation(libs.androidx.foundation)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.coil3.core)
    implementation(libs.coil3.compose)
    implementation(libs.coil3.svg)
    implementation(libs.coil3.network.okhttp)
}

tasks.withType<Jar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.matching { it.name.contains("bundleLibCompileToJarDebug") }
    .configureEach {
        mustRunAfter("clean")
    }
