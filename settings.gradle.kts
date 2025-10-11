@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = java.net.URI("https://devrepo.kakao.com/nexus/repository/kakaomap-releases/")
        }
        maven {
            url = java.net.URI("https://devrepo.kakao.com/nexus/content/groups/public/")
        }
    }
}
rootProject.name = "JejuOreum"

include(
    ":app",
    ":core:common",
    ":core:designsystem",
    ":core:navigation",
    ":core:presentation",
    ":core:testing",
    ":core:ui",
    ":data:local",
    ":data:oreum",
    ":data:remote",
    ":data:review",
    ":data:user",
    ":domain:oreum",
    ":domain:review",
    ":domain:user",
    ":feature:detail",
    ":feature:map",
    ":feature:onboarding",
    ":feature:profile",
    ":feature:splash"
)
