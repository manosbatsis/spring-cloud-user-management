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

val integrationTest by sourceSets.creating{
    java.srcDir(projectDir.resolve("src/integrationTest/java"))
    resources.srcDir(projectDir.resolve("src/integrationTest/resources"))
    compileClasspath += sourceSets.main.get().output// + sourceSets.test.get().output
    runtimeClasspath += sourceSets.main.get().output// + sourceSets.test.get().output
}
configurations[integrationTest.apiConfigurationName].extendsFrom(configurations.api.get())
configurations[integrationTest.implementationConfigurationName].extendsFrom(configurations.implementation.get())
configurations[integrationTest.runtimeOnlyConfigurationName].extendsFrom(configurations.runtimeOnly.get())
configurations[integrationTest.compileOnlyConfigurationName].extendsFrom(configurations.compileOnly.get())
//configurations[integrationTest.compileClasspathConfigurationName].extendsFrom(configurations.compileClasspath.get())

val integrationTestTask = tasks.register<Test>("integrationTest") {
    dependsOn(
        ":config-service:docker",
        ":discovery-service:docker",
        ":event-service:docker",
        ":email-service:docker")
    description = "Runs integration tests."
    group = "verification"
    useJUnitPlatform()

    testClassesDirs = integrationTest.output.classesDirs
    classpath = integrationTest.runtimeClasspath// + integrationTest.output

    shouldRunAfter(tasks.test)
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.spring.cloud.dependencies))
    implementation(platform(libs.spring.cloud.stream.dependencies))
    implementation(libs.kotlin.utils.api)

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation(libs.error.handling.spring.boot.starter)
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    implementation(libs.spring.cloud.schema.registry.client)
    //implementation("org.springframework.cloud:spring-cloud-schema-registry-client")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-stream")
    //implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka-streams")
    implementation("org.springframework.cloud:spring-cloud-starter-stream-kafka")
    //implementation("org.springframework.kafka:spring-kafka")

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
    testImplementation(libs.javafaker)
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
    "integrationTestImplementation"(libs.javafaker)
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
