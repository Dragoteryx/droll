plugins {
    kotlin("jvm") version "2.0.21"
}

group = "xyz.dragoteryx"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("dev.kord:kord-core:0.15.0")
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest { attributes["Main-Class"] = "xyz.dragoteryx.droll.DrollKt" }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
}