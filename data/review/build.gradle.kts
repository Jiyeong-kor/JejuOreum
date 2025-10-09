plugins {
    id("jejuoreum.android.library")
    id("jejuoreum.hilt")
}

android {
    namespace = "com.jeong.jejuoreum.data.review"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":domain:review"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.kotlinx.coroutines.play.services)
}
