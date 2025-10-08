import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    `kotlin-dsl`
}

group = "com.jeong.jejuoreum.buildlogic"

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    compileOnly(libs.findLibrary("android-gradle-plugin").get())
    compileOnly(libs.findLibrary("kotlin-gradle-plugin").get())
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "jejuoreum.android.application"
            implementationClass =
                "com.jeong.jejuoreum.buildlogic.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "jejuoreum.android.library"
            implementationClass = "com.jeong.jejuoreum.buildlogic.AndroidLibraryConventionPlugin"
        }
        register("kotlinLibrary") {
            id = "jejuoreum.kotlin.library"
            implementationClass = "com.jeong.jejuoreum.buildlogic.KotlinLibraryConventionPlugin"
        }
        register("compose") {
            id = "jejuoreum.compose"
            implementationClass = "com.jeong.jejuoreum.buildlogic.ComposeConventionPlugin"
        }
        register("hilt") {
            id = "jejuoreum.hilt"
            implementationClass = "com.jeong.jejuoreum.buildlogic.HiltConventionPlugin"
        }
    }
}
