package buildsrc.convention

import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("buildsrc.convention.jvm-toolchain")
    id("org.springframework.boot")
    id("org.jetbrains.kotlin.plugin.spring")
    id("org.jetbrains.kotlin.plugin.jpa")
}

description = "Configuration for Spring Boot modules"
