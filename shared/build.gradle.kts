import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("maven-publish")
    alias(libs.plugins.kotlin.serialization)
    id("com.vanniktech.maven.publish") version "0.28.0"
}



kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    targetHierarchy.default()
    androidTarget {
        publishLibraryVariants("release")
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
        }
        publishLibraryVariants("release", "debug")
    }

//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64()
//    ).forEach {
//        it.binaries.framework {
//            baseName = "shared"
//            isStatic = true
//        }
//    }
    jvm("desktop") {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
        }
    }


    sourceSets {
        val desktopMain by getting{
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")
                implementation("io.ktor:ktor-client-cio:3.0.2")
                implementation ("org.mozilla:rhino:1.7.13")
            }
        }
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.coroutines.core)
//            implementation("io.ktor:ktor-client-cio:3.0.2")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
//        nativeMain.dependencies {
//            implementation("io.ktor:ktor-client-cio:3.0.2")
//        }
        androidMain.dependencies {
            implementation("com.squareup.duktape:duktape-android:1.4.0")
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
            implementation("androidx.javascriptengine:javascriptengine:1.0.0-beta01")
        }
    }
}


android {
    namespace = "com.ar.youtubeextractor"
    compileSdk = 35
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    beforeEvaluate {
        libraryVariants.all {
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17 // Or VERSION_17 in your case
                targetCompatibility = JavaVersion.VERSION_17
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
        version = "1.0.0"
    )

    // Define POM metadata
    pom {
        name.set("KMP Library for Youtube video Data Extractor With Streaming Url")
        description.set("This library can be used by Android, iOS, and JVM targets for the shared functionality Youtube video Data Extractor With Streaming Url")
        inceptionYear.set("2024")
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
