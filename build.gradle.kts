import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.kotlin.plugin.compose) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.detekt) apply false
}

subprojects {
    plugins.withType<KotlinBasePluginWrapper> {
        apply(plugin = "io.gitlab.arturbosch.detekt")

        extensions.configure<DetektExtension>("detekt") {
            buildUponDefaultConfig = false
            autoCorrect = false
            config.setFrom(rootProject.files("config/detekt/detekt.yml"))
        }

        tasks.withType<Detekt>().configureEach {
            jvmTarget = "17"
        }
    }
}
