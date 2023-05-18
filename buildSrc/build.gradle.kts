import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.java
import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("jvm") version "1.8.10"
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
kotlin {
    //explicitApi()
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    // Add Deps to compilation, so it will become available in main project
    sourceSets.getByName("main").kotlin.srcDir("buildSrc/src/main/kotlin")
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
    implementation("org.jetbrains.kotlin:kotlin-allopen")
    implementation("org.jetbrains.kotlin:kotlin-noarg")
    implementation(libs.dokka.gradle)
    implementation(libs.spotless)
    implementation(libs.palantir.docker)
    implementation(libs.spring.boot.gradle)
    //implementation(libs.freefair.gradle)
}
