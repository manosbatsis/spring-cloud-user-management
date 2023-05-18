
rootProject.name = "spring-cloud-user-management"

// Find and include modules
fileTree(".")
    .matching {
        exclude("buildSrc/**", "**/src/**", "**/build/**", "**/.*")
        include("**/build.gradle", "**/build.gradle.kts")
    }.map {
        it.parentFile.toRelativeString(rootProject.projectDir)
    }.forEach {
        include(":${it.replace('/', ':')}")
    }

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

apply(from = "./gradle/repositories.settings.gradle.kts")

@Suppress("UnstableApiUsage") // Central declaration of repositories is an incubating feature
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
}
