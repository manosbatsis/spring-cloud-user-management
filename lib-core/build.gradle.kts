plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.dokka)
    id("com.github.davidmc24.gradle.plugin.avro")
}

java {
    withJavadocJar()
    withSourcesJar()
}

sourceSets.main.configure {
    resources.srcDir("src/main").includes.addAll(arrayOf("avro/*.*"))
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.spring.cloud.dependencies))
    implementation(platform(libs.spring.cloud.stream.dependencies))
    implementation(libs.kotlin.utils.api)
    implementation(libs.spring.cloud.schema.registry.client)
}
