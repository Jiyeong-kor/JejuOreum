import java.util.Properties

private fun Properties.requireNonEmpty(key: String): String =
    getProperty(key)?.takeIf(String::isNotBlank)
        ?: error("Missing \"${'$'}key\" in local.properties")

private fun String.asBuildConfigString(): String = "\"${'$'}{replace("\"", "\\\"")}\""

plugins {
    alias(libs.plugins.google.gms.google.services)
    id("jejuoreum.android.application")
    id("jejuoreum.compose")
    id("jejuoreum.hilt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.jeong.jejuoreum.app"

    defaultConfig {
        applicationId = "com.jeong.jejuoreum"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties().apply {
            val file = rootProject.file("local.properties")
            if (file.exists()) {
                file.inputStream().use { load(it) }
            }
        }
        val appKey = localProperties.requireNonEmpty("appKey")
        buildConfigField("String", "APP_KEY", appKey.asBuildConfigString())

        val jejuOreumBaseUrl = localProperties.requireNonEmpty("jejuOreumBaseUrl")
        buildConfigField("String", "JEJU_OREUM_URL", jejuOreumBaseUrl.asBuildConfigString())
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.coil.bom))
    implementation(platform(libs.firebase.bom))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.coil3.coil)
    implementation(libs.coil3.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.timber)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore)
}
