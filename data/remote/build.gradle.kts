import java.util.Properties
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.bundling.Jar

plugins {
    id("jejuoreum.android.library")
    id("jejuoreum.hilt")
}

android {
    namespace = "com.jeong.jejuoreum.data.remote"

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
        val jejuOreumBaseUrl = localProperties.getProperty("jejuOreumBaseUrl") ?: ""
        buildConfigField("String", "JEJU_OREUM_URL", "\"$jejuOreumBaseUrl\"")
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":domain:oreum"))

    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
}

tasks.withType<Jar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.matching { it.name.contains("bundleLibCompileToJarDebug") }
    .configureEach {
        mustRunAfter("clean")
    }
