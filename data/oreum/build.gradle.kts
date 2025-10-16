import java.util.Properties
import org.gradle.api.GradleException

plugins {
    id("jejuoreum.android.library")
    id("jejuoreum.hilt")
}

android {
    namespace = "com.jeong.jejuoreum.data.oreum"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        val localProperties = Properties().apply {
            val file = rootProject.file("local.properties")
            if (file.exists()) {
                file.inputStream().use { load(it) }
            }
        }

        val apiBaseUrl = localProperties.getProperty("jejuOreumBaseUrl")
            ?: throw GradleException("Missing jejuOreumBaseUrl in local.properties")
        buildConfigField("String", "OREUM_BASE_URL", "\"$apiBaseUrl\"")
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":domain:oreum"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
}
