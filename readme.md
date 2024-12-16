
# YouTubeExtractor Library


![Maven](https://img.shields.io/maven-central/v/io.github.ajaydhattarwal/youtube-extractor.svg)
![Kotlin](https://img.shields.io/badge/Kotlin%20Multiplatform-%E2%9C%94-green)

A Kotlin Multiplatform (KMP) library for extracting YouTube video information. Supports Android, Desktop, and other KMP-compatible platforms. iOS support is planned but not yet implemented.



## Features

- Kotlin Multiplatform (KMP) support.
- Fetch YouTube video metadata (title, description, Streaming Url , etc.).
- Ready for Android and Desktop platforms.
- Built-in retry mechanism for resilient data fetching.

## Requirements

- Kotlin 2.1+ (or compatible version)
- Gradle 7.x (with Kotlin DSL or Groovy)


---

## Setup

### Step 1: Add Maven Repository
Add the Maven repository to your `repositories` section in your project's build script.


In your `build.gradle.kts` file:

```kotlin
repositories {
    mavenCentral()
    google() //android only
}
```





---
### Add Dependency
![Maven](https://img.shields.io/maven-central/v/io.github.ajaydhattarwal/youtube-extractor.svg)

Add the library dependency to your respective modules.

#### For Android:
```kotlin
dependencies {
    implementation("io.github.ajaydhattarwal:youtube-extractor-android:1.0.0")
}
```

#### For Desktop:
```kotlin
dependencies {
    implementation("io.github.ajaydhattarwal:youtube-extractor-desktop:1.0.0")
}
```

#### For KMP (shared module):
```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.ajaydhattarwal:youtube-extractor:1.0.0")
            }
        }
    }
}
```

---



## Usage Example

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
## Planned Features
- Full iOS support.
- Additional configuration options.
- Improved error handling and logging.

## Contributing
We welcome contributions! Feel free to submit issues or pull requests to improve the library.



## License

[Unlicense](https://github.com/AjayDhattarwal/YouTubeExtractor/blob/main/LICENSE)


## 🔗 Links

[![linkedin](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/ajaydhattarwal)

[![twitter](https://img.shields.io/badge/twitter-1DA1F2?style=for-the-badge&logo=twitter&logoColor=white)](https://twitter.com/)


## Feedback

If you have any feedback, please reach out to us at dhattarwal.singh@gmail.com
