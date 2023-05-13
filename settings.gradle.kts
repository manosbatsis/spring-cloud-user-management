
rootProject.name = "spring-cloud-user-management"

include(
    "config-service",
    "discovery-service",
    "lib-core",
    "lib-test",
    "user-service",
    "email-service",
    "event-service",
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

apply(from = "./gradle/repositories.settings.gradle.kts")

@Suppress("UnstableApiUsage") // Central declaration of repositories is an incubating feature
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
}
