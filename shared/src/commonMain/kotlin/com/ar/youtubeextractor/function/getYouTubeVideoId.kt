package com.ar.youtubeextractor.function

fun getYouTubeVideoId(url: String): String? {
    val regex = Regex("(?:v=|\\/)([0-9A-Za-z_-]{11})")
    return regex.find(url)?.groupValues?.get(1)
}