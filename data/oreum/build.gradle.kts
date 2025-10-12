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

        val imageBaseUrl = localProperties.getProperty("jejuOreumImageBaseUrl")
            ?: throw GradleException("Missing jejuOreumImageBaseUrl in local.properties")
        buildConfigField("String", "OREUM_IMAGE_BASE_URL", "\"$imageBaseUrl\"")
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":domain:oreum"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
}
