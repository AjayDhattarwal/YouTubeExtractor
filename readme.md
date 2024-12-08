
# YouTubeExtractor Library

[![](https://jitpack.io/v/AjayDhattarwal/YouTubeExtractor.svg)](https://jitpack.io/#AjayDhattarwal/YouTubeExtractor)

YouTubeExtractor is a lightweight Kotlin library for extracting video and audio URLs from YouTube. It's simple to integrate and use in your projects.



## Features

- Fetch YouTube video metadata (title, description, etc.).
- Extract and decode streaming URLs using JavaScript interpreter logic.
- Perform HTML parsing to extract embedded player links.
- Supports concurrent requests using Kotlin Coroutines.
- Built-in retry mechanism for resilient data fetching.

## Requirements

- Kotlin 1.8+ (or compatible version)
- Gradle 7.x (with Kotlin DSL or Groovy)
- GraalVM (optional for advanced JavaScript handling)
- Ktor with `CIO` engine

## Installation

## Installation

### Step 1: Add JitPack Repository
Add JitPack to your project's `repositories` section:

#### Groovy DSL
```groovy
allprojects {
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation("com.github.AjayDhattarwal:YouTubeExtractor:1.0.0")
}

```

## Option 2: Manual JAR Inclusion


#### 1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/YouTubeExtractor.git
   cd YouTubeExtractor
   ```

#### 2. Open the project in Kotlin-supported IDE

#### 3. Ensure your `build.gradle.kts` or `build.gradle` includes the following:

### Using Kotlin DSL:
   ```kotlin
   plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.1.0"
}

group = "com.ar"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-client-core:3.0.1")
    implementation("io.ktor:ktor-client-cio:3.0.1")
    implementation("io.ktor:ktor-client-json:3.0.1")
    implementation("io.ktor:ktor-client-serialization:3.0.1")
    implementation("org.graalvm.js:js:22.1.0") // js executor

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}

tasks.test {
    useJUnitPlatform()
}
```

#### 4. Sync the Gradle project to download the required dependencies.
#### 5. Run the main function to start extracting video data.


### Usage Example

To use the `YouTubeExtractor` library, call the `extractVideoData` function:

```kotlin
fun main() {

    val youtubeExtractor = YouTubeExtractor()

    val url = "https://www.youtube.com/watch?v=GT0rV3pV2fA"

    val videoData = youtubeExtractor.extractVideoData(url) 

    println("video Title = >  ${videoData?.videoDetails?.title}")
    println("video description = >  ${videoData?.videoDetails?.shortDescription}")

    println("static Format url = >  ${videoData?.streamingData?.formats?.first()}")

}
```




## License

[Unlicense](https://github.com/AjayDhattarwal/YouTubeExtractor/blob/main/LICENSE)


## 🔗 Links

[![linkedin](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/)

[![twitter](https://img.shields.io/badge/twitter-1DA1F2?style=for-the-badge&logo=twitter&logoColor=white)](https://twitter.com/)


## Feedback

If you have any feedback, please reach out to us at dhattarwal.singh@gmail.com
