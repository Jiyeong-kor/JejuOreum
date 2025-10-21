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

val useKakaoStub = providers.environmentVariable("USE_KAKAO_STUB").orNull?.toBoolean()
    ?: (rootProject.findProperty("useKakaoStub")?.toString()?.toBoolean())
    ?: false

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:presentation"))
    implementation(project(":core:ui"))
    implementation(project(":domain:oreum"))

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    if (!useKakaoStub) {
        implementation(libs.kakao.vectormap)
    }
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.play.services.basement)
    implementation(libs.timber)
}
