plugins {
    kotlin("jvm") version "2.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "bet.astral"
version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    implementation("bet.astral:more4j:1.0.2")
    implementation("bet.astral:messenger:2.3.0")

    implementation("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")

    implementation("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

val targetJavaVersion = 22
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}