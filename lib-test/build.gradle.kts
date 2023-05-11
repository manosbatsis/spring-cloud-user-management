plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.dokka)
}

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
//    implementation(platform(libs.spring.boot.dependencies))
//    implementation(platform(libs.spring.cloud.dependencies))
//    implementation(platform(libs.spring.cloud.stream.dependencies))
//    implementation(libs.kotlin.utils.api)

}
