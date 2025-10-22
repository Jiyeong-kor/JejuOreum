import java.util.Properties
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.bundling.Jar

plugins {
    id("jejuoreum.android.library")
    id("jejuoreum.compose")
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
    implementation(project(":core:navigation"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:architecture"))
    implementation(project(":core:ui"))
    implementation(project(":domain:oreum"))
    implementation(project(":domain:user"))
    implementation(project(":data:oreum"))
    implementation(project(":data:user"))

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.foundation)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    if (!useKakaoStub) {
        implementation(libs.kakao.vectormap)
    }
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.play.services.basement)
    implementation(libs.timber)
}

tasks.withType<Jar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.matching { it.name.contains("bundleLibCompileToJarDebug") }
    .configureEach {
        mustRunAfter("clean")
    }
