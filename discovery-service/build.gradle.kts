plugins {
    buildsrc.convention.`spotless`
    buildsrc.convention.`spring-boot`
    buildsrc.convention.`docker`
}

dependencies {
    implementation(project(":lib-avro"))

    implementation(platform(libs.spring.boot.dependencies))
    implementation(platform(libs.spring.cloud.dependencies))
    implementation(platform(libs.spring.cloud.stream.dependencies))

    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
}
