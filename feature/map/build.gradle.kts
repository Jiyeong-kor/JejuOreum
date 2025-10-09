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
    implementation(project(":core:navigation"))
    implementation(project(":feature:detail"))
    implementation(project(":domain:oreum"))
    implementation(project(":domain:review"))
    implementation(project(":domain:user"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kakao.v2.all)
    implementation(libs.kakao.vectormap)
    implementation(libs.play.services.location)
    implementation(libs.timber)
    implementation(platform(libs.androidx.compose.bom))
}
