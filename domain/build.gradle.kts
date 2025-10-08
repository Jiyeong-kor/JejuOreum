plugins {
    id("jejuoreum.kotlin.library")
}

dependencies {
    // Domain module은 외부 라이브러리만 참조할 수 없음
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
