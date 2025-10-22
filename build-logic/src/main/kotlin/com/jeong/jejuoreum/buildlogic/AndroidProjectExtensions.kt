package com.jeong.jejuoreum.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    commonExtension.apply {
        compileSdk = SdkVersions.compileSdk

        defaultConfig {
            minSdk = SdkVersions.minSdk
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        kotlinOptions("17")

        testOptions {
            unitTests.isReturnDefaultValues = true
            unitTests.isIncludeAndroidResources = true
        }
    }

    extensions.configure(JavaPluginExtension::class.java) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    extensions.configure(KotlinAndroidProjectExtension::class.java) {
        jvmToolchain(17)

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.addAll(
                "-opt-in=kotlin.RequiresOptIn",
                "-Xannotation-default-target=param-property",
            )
        }
    }
}

internal fun Project.libs() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal object SdkVersions {
    const val compileSdk = 36
    const val minSdk = 23
    const val targetSdk = 36
}

private fun CommonExtension<*, *, *, *, *, *>.kotlinOptions(jvmTarget: String) {
    val extensionAware = this as ExtensionAware
    val options = extensionAware.extensions.findByName("kotlinOptions") ?: return

    runCatching {
        val method = options.javaClass.getMethod("setJvmTarget", String::class.java)
        method.invoke(options, jvmTarget)
    }
}
