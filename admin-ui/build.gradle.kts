import org.siouan.frontendgradleplugin.infrastructure.gradle.CleanTask
import org.siouan.frontendgradleplugin.infrastructure.gradle.RunNpm

plugins {
    id("org.siouan.frontend-jdk11") version "6.0.0"
}

frontend {
    nodeDistributionProvided.set(false)
    nodeVersion.set("18.15.0")
    assembleScript.set("run build") // "run build --mode development"
    checkScript.set("run lint --no-fix --max-warnings")

}
tasks.named("spotlessMisc") {
    mustRunAfter(tasks.named("installNode"))
}
tasks.register<Copy>("copyDist") {
    from("$projectDir/dist")
    into("../gateway-service/src/main/resources/web/dist/")
}


tasks.register<RunNpm>("start") {
    group = "application"
    description = "Runs this web application in development mode."
    dependsOn("installNode", "installYarn", "installFrontend")

    script.set("start")
}

tasks.named<CleanTask>("cleanFrontend") {
    doLast {
        delete(buildDir)
    }
}
