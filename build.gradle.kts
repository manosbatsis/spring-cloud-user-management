import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.dokka.gradle.AbstractDokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import java.io.File

plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.dokka)
    id("io.freefair.lombok") version "8.0.1" apply false
    id("com.github.davidmc24.gradle.plugin.avro") version "1.2.1" apply false
    id("com.palantir.docker") version "0.35.0" apply false
    id("com.palantir.docker-run") version "0.35.0" apply false

}

@Suppress("PropertyName")
val release_version: String by project
version = release_version

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
}
val dokkaEnabled: Provider<Boolean> = provider {
    tasks.withType<AbstractPublishToMaven>().any { it.hasTaskActions() }
}
tasks.dokkaHtmlMultiModule.configure {
    onlyIf{ dokkaEnabled.get() }
    includes.from("README.md")
    outputDirectory.set(buildDir.resolve("docs/apidoc"))
}

tasks.wrapper {
    gradleVersion = "7.5"
    distributionType = Wrapper.DistributionType.ALL
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "io.freefair.lombok")

    group = rootProject.group
    version = rootProject.version

    spotless {
        //active = projectDir.resolve("build.gradle.kts").exists()
        format("misc") {
            target("**/*.gradle", "**/*.md", "**/.gitignore")

            trimTrailingWhitespace()
            indentWithTabs() // or spaces. Takes an integer argument if you don't like 4
            endWithNewline()
        }
        configure<com.diffplug.gradle.spotless.SpotlessExtension> {
            kotlin {
                target("src/**/*.kt")
                // by default the target is every '.kt' and '.kts` file in the java sourcesets
                ktfmt() // has its own section below
                ktlint().userData(mapOf("disabled_rules" to "no-wildcard-imports"))
                    .setEditorConfigPath("${rootProject.projectDir}/.editorconfig")
                // diktat() // has its own section below
                // prettier(mapOf("prettier" to "2.0.5", "prettier-plugin-kotlin" to "2.1.0"))
                // .config(mapOf("parser" to "kotlin", "tabWidth" to 4))
                // make sure every file has the following copyright header.
                // licenseHeaderFile(rootProject.projectDir.resolve("etc/source-header/header.txt"))
            }
            java {
                target(project.fileTree("src/java") {
                    include("**/*.java")
                    exclude("build/**/*.*")
                })
                googleJavaFormat()
                // optional: you can specify a specific version (>= 1.8) and/or switch to AOSP style
                //   and/or reflow long strings
                //   and/or use custom group artifact (you probably don't need this)
                //googleJavaFormat("1.8").aosp().reflowLongStrings()
                //.groupArtifact("com.google.googlejavaformat:google-java-format")

            }
        }
    }

    tasks.withType<Javadoc>().configureEach {
        options {
            (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
        }
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

    configurations.all {
        // https://github.com/DiUS/java-faker/issues/327
        resolutionStrategy.eachDependency {
            if(requested.module.toString() == "org.yaml:snakeyaml") {
                artifactSelection {
                    selectArtifact(DependencyArtifact.DEFAULT_TYPE, null, null)
                }
            }
        }
    }

}
