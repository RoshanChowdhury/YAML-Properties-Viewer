plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.2"
}

group = "com.intellij.yamlviewer"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.yaml:snakeyaml:2.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

intellij {
    version.set("2024.3.3")
    type.set("IC")
    plugins.set(listOf("com.intellij.java", "org.jetbrains.plugins.yaml"))
}

tasks {
    patchPluginXml {
        sinceBuild.set("243")
        untilBuild.set("")
    }

    buildSearchableOptions {
        enabled = false
    }
}
