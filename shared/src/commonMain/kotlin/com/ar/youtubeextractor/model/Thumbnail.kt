package com.ar.youtubeextractor.model

import kotlinx.serialization.Serializable

@Serializable
data class Thumbnail(
    val thumbnails: List<ThumbnailItem> = emptyList()
)

@Serializable
data class ThumbnailItem(
    val url : String,
    val width : Int,
    val height : Int
)