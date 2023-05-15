plugins {
    buildsrc.convention.`spotless`
    buildsrc.convention.`spring-boot`
    buildsrc.convention.`docker`
}

dependencies {
    implementation(project(":lib-avro"))
    implementation(project(":lib-core"))
    implementation(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.spring.cloud.dependencies))
    implementation(platform(libs.spring.cloud.stream.dependencies))

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    //implementation("org.thymeleaf:thymeleaf-spring5")
    implementation("org.slf4j:slf4j-api")

    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    implementation(libs.spring.cloud.schema.registry.client)
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-stream-kafka")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation(libs.mapstruct.core)
    kapt(libs.mapstruct.processor)

    testImplementation(project(":lib-test"))
    testImplementation(platform(libs.testcontainers.bom))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.cloud:spring-cloud-stream-test-binder")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation(libs.cassandra.unit.spring)
    testImplementation(libs.greenmail.junit5)
    testImplementation(libs.awaitility)
    testImplementation(libs.awaitility.proxy)
    testImplementation(libs.javafaker){ exclude("org.yaml", "snakeyaml") }
    testImplementation("org.yaml:snakeyaml")
    testImplementation(libs.greenmail.junit5)

}
