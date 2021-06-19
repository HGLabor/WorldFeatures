plugins {
    java
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "de.hglabor"
version = "1.0-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_16
java.targetCompatibility = JavaVersion.VERSION_16

repositories {
    jcenter()
    mavenLocal()
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    shadowJar {
        dependencies {
            exclude { it.moduleGroup == "org.jetbrains.kotlin" || it.moduleGroup == "org.jetbrains.kotlinx" }
        }
    }
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.2")
    compileOnly("org.bukkit","craftbukkit","1.16.5-R0.1-SNAPSHOT")
    compileOnly("net.axay:kspigot:1.17.1")
    compileOnly("de.hglabor:hglabor-utils:0.0.11")
    implementation("net.axay:BlueUtils:1.0.2")
    implementation("org.litote.kmongo:kmongo-core:4.2.3")
    implementation("org.litote.kmongo:kmongo-serialization-mapping:4.2.3")
    compileOnly("org.bukkit","craftbukkit","1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol", "ProtocolLib", "4.6.0")
}
