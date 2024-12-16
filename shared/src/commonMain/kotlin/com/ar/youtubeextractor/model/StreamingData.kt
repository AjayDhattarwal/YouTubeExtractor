package com.ar.youtubeextractor.model


import kotlinx.serialization.Serializable

@Serializable
data class StreamingData(
    val expiresInSeconds: String? = null,
    val formats: List<Format>? = null,
    val adaptiveFormats: List<Format>? = null,
)