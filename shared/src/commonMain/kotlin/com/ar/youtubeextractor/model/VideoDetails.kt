package com.ar.youtubeextractor.model


import kotlinx.serialization.Serializable

@Serializable
data class VideoDetails(
    val videoId: String ? = null,
    val title: String ? = null,
    val lengthSeconds: Long? = null,
    val keywords: List<String>? = null,
    val channelId: String? = null,
    val isOwnerViewing: Boolean? = null,
    val shortDescription: String? = null,
    val isLive: Boolean = false,
    val isLiveDvrEnabled: Boolean = false,
    val thumbnail: Thumbnail? = null,
    val author: String? = null,
    val isLiveContent: Boolean = false,
    val viewCount: Long? = null
)