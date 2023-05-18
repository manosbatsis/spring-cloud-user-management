plugins {
    buildsrc.convention.`spring-boot`
    buildsrc.convention.`docker`
}

tasks.getByName<Test>("integrationTest") {
    dependsOn(
        ":config-service:docker",
        ":discovery-service:docker",
        ":event-service:docker",
        ":email-service:docker"
    )
}

dependencies {
    implementation(project(":lib-core"))
    implementation(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.spring.cloud.dependencies))
    implementation(platform(libs.spring.cloud.stream.dependencies))

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation(libs.error.handling.spring.boot.starter)
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    implementation(libs.spring.cloud.schema.registry.client)
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    implementation("org.springframework.cloud:spring-cloud-stream")
    implementation("org.springframework.cloud:spring-cloud-starter-stream-kafka")

    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation(libs.mapstruct.core)
    annotationProcessor(libs.mapstruct.processor)

    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("com.h2database:h2")

    testImplementation(project(":lib-test"))
    testImplementation(platform(libs.testcontainers.bom))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.cloud:spring-cloud-stream-test-binder")
    testImplementation(libs.awaitility)
    testImplementation(libs.awaitility.proxy)
    testImplementation(libs.javafaker){ exclude("org.yaml", "snakeyaml") }
    testImplementation("org.yaml:snakeyaml")
    testImplementation(libs.greenmail.junit5)

    "integrationTestImplementation"(project(":lib-test"))
    "integrationTestImplementation"("org.springframework.boot:spring-boot-starter-test")
    "integrationTestImplementation"(platform(libs.testcontainers.bom))
    "integrationTestImplementation"("org.testcontainers:kafka")
    "integrationTestImplementation"("org.testcontainers:mysql")
    "integrationTestImplementation"("org.testcontainers:cassandra")
    "integrationTestImplementation"("org.testcontainers:junit-jupiter")
    "integrationTestImplementation"(libs.awaitility)
    "integrationTestImplementation"(libs.awaitility.proxy)
    "integrationTestImplementation"(libs.javafaker){ exclude("org.yaml", "snakeyaml") }
    "integrationTestImplementation"("org.yaml:snakeyaml")
}
