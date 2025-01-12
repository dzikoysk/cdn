plugins {
    kotlin("jvm")
}

dependencies {
    testImplementation(project(":cdn"))
    testImplementation(project(":cdn-kt"))

    testImplementation(kotlin("stdlib-jdk8"))

    testImplementation("org.panda-lang:expressible-kt:1.2.9")
    testImplementation("org.panda-lang:expressible-junit:1.2.9")

    testImplementation("org.openjdk.jmh:jmh-core:1.35")
    testImplementation("org.openjdk.jmh:jmh-generator-annprocess:1.35")

    testImplementation("org.yaml:snakeyaml:1.33")
    testImplementation("com.google.code.gson:gson:2.10.1")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.14.0")
}