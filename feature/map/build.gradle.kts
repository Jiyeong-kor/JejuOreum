import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.bundling.Jar

plugins {
    id("jejuoreum.android.library")
    id("jejuoreum.compose")
    id("jejuoreum.hilt")
}

android {
    namespace = "com.jeong.jejuoreum.feature.map"
}

val useKakaoStub = providers.environmentVariable("USE_KAKAO_STUB").orNull?.toBoolean()
    ?: (rootProject.findProperty("useKakaoStub")?.toString()?.toBoolean())
    ?: false

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))
    implementation(project(":core:architecture"))
    implementation(project(":domain:oreum"))
    implementation(project(":data:oreum"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(platform(libs.coil.bom))
    implementation(libs.coil3.coil)
    implementation(libs.coil3.coil.compose)
    implementation(libs.coil.network.okhttp)
    if (!useKakaoStub) {
        implementation(libs.kakao.v2.all)
        implementation(libs.kakao.vectormap)
    }
    implementation(libs.play.services.location)
    implementation(libs.timber)
    implementation(platform(libs.androidx.compose.bom))
}

tasks.withType<Jar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.matching { it.name.contains("bundleLibCompileToJarDebug") }
    .configureEach {
        mustRunAfter("clean")
    }
