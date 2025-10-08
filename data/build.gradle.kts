plugins {
    id("jejuoreum.android.library")
    id("jejuoreum.hilt")
}

android {
    namespace = "com.jeong.jejuoreum.data.legacy"
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.converter.gson)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.play.services.location)
    implementation(libs.retrofit)
    implementation(libs.timber)
    implementation(project(":core:utils"))
    implementation(project(":domain"))
    testImplementation(libs.junit)
}
