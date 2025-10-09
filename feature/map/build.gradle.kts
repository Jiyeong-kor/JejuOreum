plugins {
    id("jejuoreum.android.library")
    id("jejuoreum.compose")
    id("jejuoreum.hilt")
}

android {
    namespace = "com.jeong.jejuoreum.feature.map"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
    implementation(project(":data:oreum"))
    implementation(project(":data:remote"))
    implementation(project(":data:review"))
    implementation(project(":data:user"))
    implementation(project(":domain:oreum"))
    implementation(project(":domain:review"))
    implementation(project(":domain:user"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.kakao.v2.all)
    implementation(libs.kakao.vectormap)
    implementation(libs.play.services.location)
    implementation(libs.timber)
    implementation(platform(libs.androidx.compose.bom))
}
