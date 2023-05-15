plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.dokka)
    alias(libs.plugins.buildconfig)
    id("com.github.davidmc24.gradle.plugin.avro")
}

java {
    withJavadocJar()
    withSourcesJar()
}

sourceSets.main.configure {
    resources.srcDir("src/main").includes.addAll(arrayOf("avro/*.*"))
}

buildConfig {
    useJavaOutput()
    className("BuildConfig")   // forces the class name. Defaults to 'BuildConfig'
    packageName("com.github.manosbatsis.lib.core")  // forces the package. Defaults to '${project.group}'
    buildConfigField("String", "APP_NAME", "\"${rootProject.name}\"")
    buildConfigField("String", "APP_VERSION", provider { "\"${rootProject.version}\"" })
    buildConfigField("long", "BUILD_TIME", "${System.currentTimeMillis()}L")
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.spring.cloud.dependencies))
    implementation(platform(libs.spring.cloud.stream.dependencies))
    implementation(libs.spring.cloud.schema.registry.client)

    implementation("org.springframework:spring-webmvc")

}
