plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "3.7.0"
    id("com.github.ben-manes.versions") version "0.45.0"
}

version = "0.1"
group = "com.itndr"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("io.micronaut:micronaut-http-validation")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut.gcp:micronaut-gcp-common")
    implementation("io.micronaut.gcp:micronaut-gcp-logging")
    implementation("io.micronaut.reactor:micronaut-reactor")
    implementation("io.micronaut.views:micronaut-views-thymeleaf")
    implementation("jakarta.annotation:jakarta.annotation-api")
    runtimeOnly("ch.qos.logback:logback-classic:1.4.5")
    implementation("io.micronaut:micronaut-validation")
    implementation("com.google.cloud:google-cloud-firestore:3.7.10")
    testImplementation("org.jsoup:jsoup:1.15.3")
}


application {
    mainClass.set("com.itndr.Application")
}
java {
    sourceCompatibility = JavaVersion.toVersion("11")
    targetCompatibility = JavaVersion.toVersion("11")
}

tasks {
    dockerBuild {
        images.set(listOf("${System.getenv("DOCKER_IMAGE") ?: project.name}:${project.version}"))
    }

    dockerBuildNative {
        images.set(listOf("${System.getenv("DOCKER_IMAGE") ?: project.name}:${project.version}"))
    }
    named(
        "dependencyUpdates",
        com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask::class.java
    ).configure {
        rejectVersionIf {
            candidate.version.contains(Regex("-((m\\d+)|(rc(-|\\d)*)|(beta))", RegexOption.IGNORE_CASE))
        }
    }
}
graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.itndr.*")
    }
}
