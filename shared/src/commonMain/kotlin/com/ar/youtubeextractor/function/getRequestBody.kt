package com.ar.youtubeextractor.function

import com.ar.youtubeextractor.model.request.ContentPlaybackContext
import com.ar.youtubeextractor.model.request.Context
import com.ar.youtubeextractor.model.request.PlaybackContext
import com.ar.youtubeextractor.model.request.RequestClient
import com.ar.youtubeextractor.model.request.VideoRequest
import io.ktor.util.Platform
import io.ktor.util.toLowerCasePreservingASCIIRules

fun getRequestBody(platform: String, videoID: String): VideoRequest{
    val key = platform.toLowerCasePreservingASCIIRules()
    println(key)
    return when(key){
        "web" -> {
            VideoRequest(
                context = Context(
                    client = RequestClient(
                        clientName = "WEB",
                        clientVersion = "2.20241126.01.00",
                        userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
                        hl = "en",
                        timeZone = "UTC",
                        utcOffsetMinutes = 0
                    )
                ),
                videoId = videoID,
                playbackContext = PlaybackContext(
                    contentPlaybackContext = ContentPlaybackContext(
                        html5Preference = "HTML5_PREF_WANTS"
                    )
                ),
                contentCheckOk = true,
                racyCheckOk = true
            )
        }

        "android" -> {
            VideoRequest(
                context = Context(
                    client = RequestClient(
                        clientName = "ANDROID",
                        clientVersion = "19.44.38",
                        userAgent = "com.google.android.youtube/19.44.38 (Linux; U; Android 11) gzip",
                        hl = "en",
                        timeZone = "UTC",
                        utcOffsetMinutes = 0,
                        androidSdkVersion = 30,
                        osVersion = "11",
                        osName = "Android"
                    )
                ),
                videoId = videoID,
                playbackContext = PlaybackContext(
                    contentPlaybackContext = ContentPlaybackContext(
                        html5Preference = "HTML5_PREF_WANTS"
                    )
                ),
                contentCheckOk = true,
                racyCheckOk = true
            )
        }

        "ios" -> {
            VideoRequest(
                context = Context(
                    client = RequestClient(
                        clientName = "IOS",
                        clientVersion = "19.45.4",
                        deviceMake = "Apple",
                        deviceModel = "iPhone16,2",
                        userAgent = "com.google.ios.youtube/19.45.4 (iPhone16,2; U; CPU iOS 18_1_0 like Mac OS X;)",
                        hl = "en",
                        timeZone = "UTC",
                        utcOffsetMinutes = 0,
                        osName = "iPhone",
                        osVersion= "18.1.0.22B83"
                    )
                ),
                videoId = videoID,
                playbackContext = PlaybackContext(
                    contentPlaybackContext = ContentPlaybackContext(
                        html5Preference = "HTML5_PREF_WANTS"
                    )
                ),
                contentCheckOk = true,
                racyCheckOk = true
            )
        }

        "mweb" -> {
            VideoRequest(
                context = Context(
                    client = RequestClient(
                        clientName = "MWEB",
                        clientVersion = "2.20241202.07.00",
                        userAgent = "Mozilla/5.0 (iPad; CPU OS 16_7_10 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1,gzip(gfe)",
                        hl = "en",
                        timeZone = "UTC",
                        utcOffsetMinutes = 0,
                    )
                ),
                videoId = videoID,
                playbackContext = PlaybackContext(
                    contentPlaybackContext = ContentPlaybackContext(
                        html5Preference = "HTML5_PREF_WANTS"
                    )
                ),
                contentCheckOk = true,
                racyCheckOk = true
            )
        }

        "tv" -> {
            VideoRequest(
                context = Context(
                    client = RequestClient(
                        clientName = "TVHTML5_SIMPLY_EMBEDDED_PLAYER",
                        clientVersion = "2.0",
                        hl = "en",
                        timeZone = "UTC",
                        utcOffsetMinutes = 0,
                    )
                ),
                videoId = videoID,
                playbackContext = PlaybackContext(
                    contentPlaybackContext = ContentPlaybackContext(
                        html5Preference = "HTML5_PREF_WANTS"
                    )
                ),
                contentCheckOk = true,
                racyCheckOk = true
            )
        }

        else -> {
            VideoRequest(
                context = Context(
                    client = RequestClient(
                        clientName = "IOS",
                        clientVersion = "19.45.4",
                        userAgent = "com.google.ios.youtube/19.45.4 (iPhone16,2; U; CPU iOS 18_1_0 like Mac OS X;)",
                        hl = "en",
                        timeZone = "UTC",
                        utcOffsetMinutes = 0
                    )
                ),
                videoId = videoID,
                playbackContext = PlaybackContext(
                    contentPlaybackContext = ContentPlaybackContext(
                        html5Preference = "HTML5_PREF_WANTS"
                    )
                ),
                contentCheckOk = true,
                racyCheckOk = true
            )
        }
    }
}