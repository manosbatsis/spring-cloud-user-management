package buildsrc.convention

import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("com.palantir.docker")
}

description = "Configuration for creating docker containers"

val dockerRepositoryName = "${properties["app.dockerRepositoryName"]?:project.group}"

val jarTask = tasks.findByName("bootJar") as BootJar? ?: tasks.getByName<Jar>("jar")
val archivePath = jarTask.archiveFileName.get()
val dockerFilePath = "${projectDir.path}/Dockerfile"
val projectName = "${dockerRepositoryName}/${project.name}"
val fullName = "$projectName:${project.version}"
val dockerBuildArgs = mapOf("JAR_FILE" to archivePath)

// workaround from https://github.com/palantir/gradle-docker/issues/413
tasks.docker {
    inputs.file(dockerFilePath)
    dependsOn()
}
docker {
    name = fullName
    tag("latest", fullName)
    pull(true)
    setDockerfile(file(dockerFilePath))
    files(jarTask.outputs)
    buildArgs(dockerBuildArgs)
}
