import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
    java
    application
}
group = "me.user"
version = "0.9"

repositories {
    mavenCentral()
}
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.2.0")
    testImplementation(group = "org.assertj", name = "assertj-core", version = "3.13.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.2.0")
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

application {
    mainClassName = "MainKt"
}
