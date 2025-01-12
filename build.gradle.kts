plugins {
    `java-library`
    `maven-publish`
    signing
    jacoco
    kotlin("jvm") version "2.1.0"
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

description = "CDN | Parent"

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "signing")
    apply(plugin = "maven-publish")

    group = "net.dzikoysk"
    version = "1.14.6"

    repositories {
        maven("https://maven.reposilite.com/maven-central")
    }

    publishing {
        repositories {
            maven {
                name = "reposilite-repository"
                url = uri("https://maven.reposilite.com/${if (version.toString().endsWith("-SNAPSHOT")) "snapshots" else "releases"}")

                credentials {
                    username = getEnvOrProperty("MAVEN_NAME", "mavenUser")
                    password = getEnvOrProperty("MAVEN_TOKEN", "mavenPassword")
                }
            }
        }
    }

    afterEvaluate {
        description
            ?.takeIf { it.isNotEmpty() }
            ?.split("|")
            ?.let { (projectName, projectDescription) ->
                publishing {
                    publications {
                        create<MavenPublication>("library") {
                            pom {
                                name.set(projectName)
                                description.set(projectDescription)
                                url.set("https://github.com/dzikoysk/cdn")

                                licenses {
                                    license {
                                        name.set("The Apache License, Version 2.0")
                                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                                    }
                                }
                                developers {
                                    developer {
                                        id.set("dzikoysk")
                                        name.set("dzikoysk")
                                        email.set("dzikoysk@dzikoysk.net")
                                    }
                                }
                                scm {
                                    connection.set("scm:git:git://github.com/dzikoysk/cdn.git")
                                    developerConnection.set("scm:git:ssh://github.com/dzikoysk/cdn.git")
                                    url.set("https://github.com/dzikoysk/cdn.git")
                                }
                            }

                            from(components.getByName("java"))
                        }
                    }
                }

                if (findProperty("signing.keyId").takeIf { it?.toString()?.trim()?.isNotEmpty() == true } != null) {
                    signing {
                        sign(publishing.publications.getByName("library"))
                    }
                }
            }
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

subprojects {
    dependencies {
        val junit = "5.8.2"
        testImplementation("org.junit.jupiter:junit-jupiter-params:$junit")
        testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:$junit")
        testImplementation("org.assertj:assertj-core:3.23.1")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.register("release") {
        dependsOn(
            "publishAllPublicationsToReposilite-repositoryRepository",
            "publishToSonatype",
        )
    }
}

tasks.register("release") {
    dependsOn(
        "clean", "build",
        "publishAllPublicationsToReposilite-repositoryRepository",
        "publishAllPublicationsToSonatypeRepository",
        "closeAndReleaseSonatypeStagingRepository"
    )
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            username.set(getEnvOrProperty("SONATYPE_USER", "sonatypeUser"))
            password.set(getEnvOrProperty("SONATYPE_PASSWORD", "sonatypePassword"))
        }
    }
}

fun getEnvOrProperty(env: String, property: String): String? =
    System.getenv(env) ?: findProperty(property)?.toString()