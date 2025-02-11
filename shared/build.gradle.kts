@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("maven-publish")
    alias(libs.plugins.kotlin.serialization)
    id("com.vanniktech.maven.publish") version "0.30.0"
}



kotlin {
    applyDefaultHierarchyTemplate()
    androidTarget {
        publishLibraryVariants("release")
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
        publishLibraryVariants("release", "debug")
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }


    jvm("desktop") {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }


    sourceSets {
        val desktopMain by getting{
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation (libs.rhino)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.ktor.client.content.encoding)
            }
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.content.encoding)
            implementation(libs.ktor.client.content.negotiation)
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.3")
            implementation("io.ktor:ktor-client-logging:3.0.3")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(kotlin("test-annotations-common"))
            implementation(libs.assertk)
            implementation(libs.ktor.client.mock)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        androidMain.dependencies {
            implementation(libs.duktape.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
//            implementation("androidx.javascriptengine:javascriptengine:1.0.0-beta01")
        }
    }
}


android {
    namespace = "com.ar.youtubeextractor"
    compileSdk = 35
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    beforeEvaluate {
        libraryVariants.all {
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_1_8 // Or VERSION_17 in your case
                targetCompatibility = JavaVersion.VERSION_1_8
            }
        }
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }

}

mavenPublishing {
    coordinates(
        groupId = "io.github.ajaydhattarwal",
        artifactId = "youtube-extractor",
        version = "1.0.2"
    )

    // Define POM metadata
    pom {
        name.set("KMP Library for Youtube video Data Extractor With Streaming Url")
        description.set("This library can be used by Android, iOS, and Desktop(JVM) targets for the shared functionality Youtube video Data Extractor With Streaming Url")
        inceptionYear.set("2025")
        url.set("https://github.com/AjayDhattarwal/YouTubeExtractor")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set("ajaydhattarwal")
                name.set("Ajay Dhattarwal")
                email.set("dhattarwal.singh@gmail.com")
            }
        }

        scm {
            url.set("https://github.com/AjayDhattarwal/YouTubeExtractor")
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

}
