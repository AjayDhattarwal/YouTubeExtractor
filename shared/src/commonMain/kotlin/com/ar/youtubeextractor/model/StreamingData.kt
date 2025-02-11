package com.ar.youtubeextractor.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamingData(
    val expiresInSeconds: String? = null,
    val formats: List<Format>? = emptyList(),
    val adaptiveFormats: List<Format>? = emptyList(),
    val dashManifestUrl: String? = null,
    val hlsManifestUrl: String? = null,
    @SerialName("hlsFormats") val hlsFormats: List<HlsFormat>? = emptyList(),
    val serverAbrStreamingUrl: String? = null
)