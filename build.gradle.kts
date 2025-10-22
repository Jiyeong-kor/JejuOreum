import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.tasks.Delete
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper
import org.gradle.kotlin.dsl.registering

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

private val purgeBuildArtifacts by tasks.registering(Delete::class) {
    group = "build setup"
    description = "Removes build outputs and Gradle caches that may remain locked on Windows hosts."

    val isWindows = System.getProperty("os.name").orEmpty().lowercase().contains("windows")

    delete(layout.buildDirectory)

    if (isWindows) {
        delete(layout.projectDirectory.dir(".gradle"))
    }

    gradle.allprojects.forEach { project ->
        delete(project.layout.buildDirectory)

        if (isWindows) {
            delete(project.layout.projectDirectory.dir(".gradle"))
        }
    }
}

private val clean by tasks.registering(Delete::class) {
    delete(layout.buildDirectory)
}

clean.configure {
    dependsOn(purgeBuildArtifacts)
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

    tasks.matching { task -> task.name == "bundleLibCompileToJarDebug" }.configureEach {
        dependsOn(rootProject.tasks.named("purgeBuildArtifacts"))
        outputs.cacheIf("Disable build cache for bundleLibCompileToJarDebug to avoid file locking issues on Windows") {
            false
        }
    }

    tasks.matching { task -> task.name.startsWith("ksp") }.configureEach {
        dependsOn(rootProject.tasks.named("purgeBuildArtifacts"))
    }
}
