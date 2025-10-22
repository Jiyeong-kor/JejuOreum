import com.jeong.jejuoreum.buildlogic.SecretKey
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.bundling.Jar

plugins {
    alias(libs.plugins.google.gms.google.services)
    id("jejuoreum.android.application")
    id("jejuoreum.compose")
    id("jejuoreum.hilt")
    id("kotlin-parcelize")
    id("jejuoreum.secrets")
}

android {
    namespace = "com.jeong.jejuoreum.app"

    defaultConfig {
        applicationId = "com.jeong.jejuoreum"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
    implementation(project(":feature:map"))
    implementation(project(":feature:detail"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:splash"))

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

secrets {
    stringField(name = "APP_KEY", key = SecretKey.AppKey)
    stringField(name = "JEJU_OREUM_URL", key = SecretKey.JejuOreumBaseUrl)
}

tasks.withType<Jar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.matching { it.name.contains("bundleLibCompileToJarDebug") }
    .configureEach {
        mustRunAfter("clean")
    }
