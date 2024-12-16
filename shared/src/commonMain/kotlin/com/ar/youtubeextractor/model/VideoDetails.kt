package com.ar.youtubeextractor.model


import kotlinx.serialization.Serializable

@Serializable
data class VideoDetails(
    val videoId: String ? = null,
    val title: String ? = null,
    val lengthSeconds: String? = null,
    val keywords: List<String>? = null,
    val channelId: String? = null,
    val isOwnerViewing: Boolean? = null,
    val shortDescription: String? = null
)