plugins {
    buildsrc.convention.`spotless`
    buildsrc.convention.`spring-boot`
    buildsrc.convention.`docker`
}

tasks.register<Copy>("processFrontendResources") {
    val frontendBuildDir = file("../admin-ui/dist")
    val frontendResourcesDir = file("${project.buildDir}/resources/main/static")

    group = "Frontend"
    description = "Process frontend resources"
    dependsOn(project(":admin-ui").tasks.named("assembleFrontend"))

    from(frontendBuildDir)
    into(frontendResourcesDir)
}

listOf("resolveMainClassName", "jar", "bootJar", "javadoc").forEach {
    tasks.named(it) {
        dependsOn(tasks.named("processFrontendResources"))
    }
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.spring.cloud.dependencies))
    implementation(platform(libs.spring.cloud.stream.dependencies))

    implementation("org.springframework.boot:spring-boot-starter-actuator")

    //implementation(libs.error.handling.spring.boot.starter)

    implementation(libs.spring.cloud.schema.registry.client)
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")
    implementation(libs.springdoc.openapi.starter.webflux.api)
    implementation(libs.springdoc.openapi.starter.webflux.ui)

    testImplementation(platform(libs.testcontainers.bom))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
