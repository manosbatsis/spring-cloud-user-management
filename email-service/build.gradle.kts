plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.dokka)
    id("org.springframework.boot")
    id("com.palantir.docker")
}

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    implementation(project(":lib-core"))
    implementation(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.spring.cloud.dependencies))
    implementation(platform(libs.spring.cloud.stream.dependencies))
    implementation(libs.kotlin.utils.api)

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    //implementation("org.thymeleaf:thymeleaf-spring5")

    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    implementation(libs.spring.cloud.schema.registry.client)
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-stream-kafka")

    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation(libs.mapstruct.core)
    annotationProcessor(libs.mapstruct.processor)

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
    testImplementation(libs.javafaker)
    testImplementation(libs.greenmail.junit5)

}

val bootJarTask = tasks.bootJar.get()
val archivePath = bootJarTask.archiveFileName.get()
val dockerFilePath = "${projectDir.path}/Dockerfile"
val projectName = "${project.group}/${project.name}"
val fullName = "$projectName:${project.version}"
val dockerBuildArgs = mapOf("JAR_FILE" to archivePath)

// workaround from https://github.com/palantir/gradle-docker/issues/413
tasks.docker {
    inputs.file(dockerFilePath)
}

docker {
    name = fullName
    tag("latest", "$projectName:latest")
    pull(true)
    setDockerfile(file(dockerFilePath))
    files(bootJarTask.outputs)
    buildArgs(dockerBuildArgs)
}
