package com.jeong.jejuoreum.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import java.util.Properties
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.configure

class SecretsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val extension = extensions.create<SecretsExtension>("secrets", this)

        pluginManager.withPlugin("com.android.application") {
            extensions.configure<ApplicationExtension> {
                extension.configureWith(this)
            }
        }

        pluginManager.withPlugin("com.android.library") {
            extensions.configure<LibraryExtension> {
                extension.configureWith(this)
            }
        }
    }
}

@Suppress("UnstableApiUsage")
open class SecretsExtension(private val project: Project) {
    private val declarations = mutableListOf<SecretDeclaration>()
    private val properties: Properties by lazy { project.loadLocalProperties() }

    fun stringField(name: String, key: SecretKey) {
        declarations += SecretDeclaration(name, key, SecretFieldType.STRING)
    }

    fun configureWith(commonExtension: CommonExtension<*, *, *, *, *, *>) {
        project.afterEvaluate {
            if (declarations.isEmpty()) return@afterEvaluate

            commonExtension.defaultConfig {
                declarations.forEach { declaration ->
                    val rawValue = this@SecretsExtension.properties.requireSecret(declaration.key)

                    buildConfigField(
                        declaration.fieldType.typeName,
                        declaration.name,
                        declaration.fieldType.format(rawValue)
                    )
                }
            }
        }
    }
}

private enum class SecretFieldType(val typeName: String) {
    STRING("String");

    fun format(value: String): String = buildString {
        append('"')
        append(value.replace("\"", "\\\""))
        append('"')
    }
}

private data class SecretDeclaration(
    val name: String,
    val key: SecretKey,
    val fieldType: SecretFieldType,
)

@Suppress("UnstableApiUsage")
internal fun Project.loadLocalProperties(): Properties {
    val properties = Properties()
    val propertiesFile = rootProject.file("local.properties")
    if (!propertiesFile.exists()) {
        error("Expected local.properties to define required secrets.")
    }

    propertiesFile.inputStream().use(properties::load)
    return properties
}

enum class SecretKey(internal val propertyName: String) {
    AppKey("appKey"),
    JejuOreumBaseUrl("jejuOreumBaseUrl"),
}

private fun Properties.requireSecret(key: SecretKey): String {
    val value = get(key.propertyName) as? String
    return value?.takeIf(String::isNotBlank)
        ?: error("Missing \"${'$'}{key.propertyName}\" in local.properties")
}
