package com.ar.youtubeextractor.model

import kotlinx.serialization.Serializable

@Serializable
data class HlsFormat(
    val itag: String,
    val url: String,
    val mimeType: String? = null,
    val bitrate: Long? = null,
    val width: Int,
    val height: Int,
    val lastModified: Long? = null,
    val quality: String,
    val fps: Int? = null,
    val qualityLabel: String? = null,
    val projectionType: String? = null,
)