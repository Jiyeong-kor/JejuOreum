import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.bundling.Jar

plugins {
    id("jejuoreum.android.library")
    id("jejuoreum.hilt")
}

android {
    namespace = "com.jeong.jejuoreum.data.user"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":data:local"))
    implementation(project(":domain:user"))

    implementation(libs.androidx.datastore.preferences)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.timber)
}

tasks.withType<Jar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.matching { it.name.contains("bundleLibCompileToJarDebug") }
    .configureEach {
        mustRunAfter("clean")
    }
