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