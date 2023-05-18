package buildsrc.convention

import org.gradle.kotlin.dsl.configure

plugins {
    id("com.diffplug.spotless")
}

description = "Configuration for Spotless general-purpose code formatting"

spotless {
    //active = projectDir.resolve("build.gradle.kts").exists()
    format("misc") {
        target("*.gradle", "*.md", "doc/**/*.md", "**/.gitignore")
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
            target("src/**/*.java")
            googleJavaFormat().aosp()
            // optional: you can specify a specific version (>= 1.8) and/or switch to AOSP style
            //   and/or reflow long strings
            //   and/or use custom group artifact (you probably don't need this)
            //googleJavaFormat("1.8").aosp().reflowLongStrings()
            //.groupArtifact("com.google.googlejavaformat:google-java-format")

        }
    }
}
