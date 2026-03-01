pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
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
    }
}

rootProject.name = "PingMap"
include(":app")
include(":core:ui")
include(":core:network")
include(":core:permissions")
include(":data:local")
include(":data:preferences")
include(":core:di")
include(":feature:wifi-scanner")
include(":feature:speed-test")
include(":feature:device-discovery")
include(":feature:ping")
include(":feature:port-scanner")
include(":feature:signal-map")
