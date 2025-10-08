package com.jeong.jejuoreum.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("com.google.dagger.hilt.android")

            val libs = libs()
            val hiltAndroid = libs.findLibrary("hilt-android").orElse(null)
            val hiltCompiler = libs.findLibrary("hilt-android-compiler").orElse(null)

            hiltAndroid?.let { dependencies.add("implementation", it.get()) }
            hiltCompiler?.let { dependencies.add("ksp", it.get()) }
        }
    }
}
