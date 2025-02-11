package com.ar.youtubeextractor.model.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@Serializable
data class VideoRequest(
    val context: Context? = null,
    val videoId: String? = null,
    val playbackContext: PlaybackContext? = null,
    val contentCheckOk: Boolean? = null,
    val racyCheckOk: Boolean? = null
)

@Serializable
data class Context(
    val client: RequestClient? = null
)

@Serializable
data class RequestClient(
    val clientName: String? = null,
    val clientVersion: String? = null,
    val deviceMake: String? = null,
    val deviceModel: String? = null,
    val userAgent: String? = null,
    val osName: String? = null,
    val osVersion: String? = null,
    val hl: String? = null,
    val timeZone: String? = null,
    val utcOffsetMinutes: Int? = null,
    val androidSdkVersion: Int? = null
)

@Serializable
data class PlaybackContext(
    val contentPlaybackContext: ContentPlaybackContext? = null
)

@Serializable
data class ContentPlaybackContext(
    val html5Preference: String? = null,
    val signatureTimestamp: Int? = null
)


fun toJson(request: VideoRequest): String {
    val json = Json {
        encodeDefaults = false
        explicitNulls = false
    }
    return json.encodeToString(request)
}
