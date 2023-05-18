plugins {
    buildsrc.convention.`jvm-toolchain`
    buildsrc.convention.`spotless`
    alias(libs.plugins.avro)
}
sourceSets.main.configure {
    resources.srcDir("src/main").includes.addAll(arrayOf("avro/*.*"))
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.spring.cloud.dependencies))
    implementation(platform(libs.spring.cloud.stream.dependencies))
    implementation(libs.spring.cloud.schema.registry.client)

    implementation("org.springframework:spring-webmvc")

}
