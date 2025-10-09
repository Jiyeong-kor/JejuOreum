import java.util.Properties

plugins {
    id("jejuoreum.android.library")
    id("jejuoreum.hilt")
}

android {
    namespace = "com.jeong.jejuoreum.feature.splash"

    defaultConfig {
        val localProperties = Properties().apply {
            val file = rootProject.file("local.properties")
            if (file.exists()) {
                file.inputStream().use { load(it) }
            }
        }
        val appKey = localProperties.getProperty("appKey") ?: ""
        buildConfigField("String", "APP_KEY", "\"$appKey\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain:oreum"))
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.kakao.vectormap)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.play.services.basement)
    implementation(libs.timber)
}
