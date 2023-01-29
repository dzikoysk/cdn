import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

description = "CDN | CDN extensions for Kotlin"

dependencies {
    api(project(":cdn"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.7"
        freeCompilerArgs = listOf(
            "-Xjvm-default=all", // For generating default methods in interfaces
            // "-Xcontext-receivers"
        )
    }
}