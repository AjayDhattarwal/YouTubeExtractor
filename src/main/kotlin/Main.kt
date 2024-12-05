package com.ar

import com.ar.core.YouTubeExtractor

fun main() {
    System.setProperty("polyglot.engine.WarnInterpreterOnly", "false")       // hide unwanted warnings

    val youtubeExtractor = YouTubeExtractor()

    val url = "https://www.youtube.com/watch?v=GT0rV3pV2fA"

    val videoData = youtubeExtractor.extractVideoData(url)
//    videoData?.streamingData?.adaptiveFormats?.forEach { streamingData ->
//        println(streamingData.streamingUrl)
//    }

    println("video Id = >  ${videoData?.videoDetails?.videoId}")
    println("video Title = >  ${videoData?.videoDetails?.title}")
    println("video description = >  ${videoData?.videoDetails?.shortDescription}")

    println("static Format url = >  ${videoData?.streamingData?.formats?.first()}")


}