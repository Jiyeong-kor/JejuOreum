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
    implementation(project(":data:remote"))
    implementation(project(":domain:user"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.timber)
}
