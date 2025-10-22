import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.bundling.Jar

plugins {
    id("jejuoreum.android.library")
    id("jejuoreum.compose")
    id("jejuoreum.hilt")
    id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
    namespace = "com.jeong.jejuoreum.core.ui"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:architecture"))
    implementation(project(":core:common"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.coil.bom))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.coil3.core)
    implementation(libs.coil3.compose)
    implementation(libs.coil3.svg)
    implementation(libs.coil3.network.okhttp)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.okhttp)
}

tasks.withType<Jar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.matching { it.name.contains("bundleLibCompileToJarDebug") }
    .configureEach {
        mustRunAfter("clean")
    }
