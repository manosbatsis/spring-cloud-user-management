package buildsrc.convention

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.dokka.gradle.AbstractDokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.`java-library`
import org.gradle.kotlin.dsl.register
import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
    `java-library`
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.dokka")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withJavadocJar()
    withSourcesJar()
}

kotlin {
    //explicitApi()
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
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
    description = "Runs integration tests from sources in src/integrationTest. " +
        "Does *not* inherit the regular test configuration."
    group = "verification"
    useJUnitPlatform()

    testClassesDirs = integrationTest.output.classesDirs
    classpath = integrationTest.runtimeClasspath// + integrationTest.output

    testLogging.showStandardStreams = true
    testLogging.exceptionFormat = TestExceptionFormat.FULL

    shouldRunAfter(tasks.test)
}
val dokkaEnabled: Provider<Boolean> = provider {
    tasks.withType<AbstractPublishToMaven>().any { it.hasTaskActions() }
}
tasks.withType<AbstractDokkaTask>().all {
    onlyIf{ dokkaEnabled.get() }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.apply {
        jvmTarget = "17"
        apiVersion = "1.7"
        languageVersion = "1.7"
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging.showStandardStreams = true
    testLogging.exceptionFormat = TestExceptionFormat.FULL
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}
