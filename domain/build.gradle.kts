plugins {
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Domain module은 외부 라이브러리만 참조할 수 없음
    implementation(libs.kotlinx.coroutines.core)
}
