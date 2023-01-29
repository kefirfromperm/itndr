plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "3.7.0"
    id("com.google.cloud.tools.jib") version "2.8.0"
}

version = "0.1"
group = "itndr"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("io.micronaut:micronaut-http-validation")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut.gcp:micronaut-gcp-common")
    implementation("io.micronaut.gcp:micronaut-gcp-logging")
    implementation("io.micronaut.reactor:micronaut-reactor")
    implementation("io.micronaut.views:micronaut-views-freemarker")
    implementation("jakarta.annotation:jakarta.annotation-api")
    runtimeOnly("ch.qos.logback:logback-classic")
    testImplementation("org.assertj:assertj-core")
    implementation("io.micronaut:micronaut-validation")
    implementation("com.google.cloud:google-cloud-firestore:3.7.8")
}


application {
    mainClass.set("itndr.Application")
}
java {
    sourceCompatibility = JavaVersion.toVersion("11")
    targetCompatibility = JavaVersion.toVersion("11")
}

tasks {
    jib {
        to {
            image = "gcr.io/myapp/jib-image"
        }
    }
}
graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("itndr.*")
    }
}
