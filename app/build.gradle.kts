import java.util.Properties

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
        val appKey = localProperties.getProperty("appKey") ?: ""
        buildConfigField("String", "APP_KEY", "\"$appKey\"")

        val jejuOreumBaseUrl = localProperties.getProperty("jejuOreumBaseUrl") ?: ""
        buildConfigField("String", "JEJU_OREUM_URL", "\"$jejuOreumBaseUrl\"")
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
    implementation(project(":feature:detail"))
    implementation(project(":feature:map"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:splash"))
    implementation(project(":data:local"))
    implementation(project(":data:oreum"))
    implementation(project(":data:remote"))
    implementation(project(":data:review"))
    implementation(project(":data:user"))

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
    implementation(libs.timber)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore)
}
