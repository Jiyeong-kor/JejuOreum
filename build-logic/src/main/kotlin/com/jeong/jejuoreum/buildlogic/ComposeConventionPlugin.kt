package com.jeong.jejuoreum.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            val libs = libs()
            val composeCompilerVersion = libs.findVersion("compose-compiler").get().requiredVersion

            pluginManager.withPlugin("com.android.application") {
                extensions.configure<ApplicationExtension> {
                    buildFeatures.compose = true
                    composeOptions.kotlinCompilerExtensionVersion = composeCompilerVersion
                }
            }
            pluginManager.withPlugin("com.android.library") {
                extensions.configure<LibraryExtension> {
                    buildFeatures.compose = true
                    composeOptions.kotlinCompilerExtensionVersion = composeCompilerVersion
                }
            }
        }
    }
}
