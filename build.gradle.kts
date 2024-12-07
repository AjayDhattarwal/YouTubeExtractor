plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.1.0"
    id("maven-publish")
}

group = "com.ar"
version = "1.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-client-core:3.0.1")
    implementation("io.ktor:ktor-client-cio:3.0.1")
    implementation("io.ktor:ktor-client-json:3.0.1")
    implementation("io.ktor:ktor-client-serialization:3.0.1")
    implementation ("org.mozilla:rhino:1.7.13") // js executor
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
            groupId = "com.ar"
            artifactId = "YouTubeExtractor"
            version = "1.0.0"
        }
    }
}