package com.ar.youtubeextractor

import com.ar.youtubeextractor.core.YouTubeExtractor
import com.ar.youtubeextractor.core.onSuccess
import kotlinx.coroutines.runBlocking

fun mainFunForTest (){
    runBlocking {
        val youTubeExtractor = YouTubeExtractor()
        youTubeExtractor.extractVideoData(
            "https://www.youtube.com/watch?v=1OAjeECW90E",
            platform = "ios"
        ).onSuccess {
            println(it.streamingData.hlsManifestUrl)

            println(it.streamingData.formats?.firstOrNull()?.url)

            println(
                it.streamingData.adaptiveFormats?.firstOrNull()?.url
            )
        }
    }
}