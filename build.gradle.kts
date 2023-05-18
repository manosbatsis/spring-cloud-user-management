plugins {
    buildsrc.convention.`spotless`
}

tasks.wrapper {
    gradleVersion = "8.1.1"
    distributionType = Wrapper.DistributionType.ALL
}

subprojects {
    group = rootProject.group
    version = rootProject.version
}
