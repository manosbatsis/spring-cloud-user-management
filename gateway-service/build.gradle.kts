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
    implementation(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.spring.cloud.dependencies))
    implementation(platform(libs.spring.cloud.stream.dependencies))
    implementation(libs.kotlin.utils.api)

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
    tag("latest", "$projectName:${project.version}")
    pull(true)
    setDockerfile(file(dockerFilePath))
    files(bootJarTask.outputs)
    buildArgs(dockerBuildArgs)
}
