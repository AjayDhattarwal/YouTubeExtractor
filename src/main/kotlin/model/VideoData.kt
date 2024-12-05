package com.ar.model

import kotlinx.serialization.Serializable

@Serializable
data class VideoData(
    val streamingData: StreamingData,
    val videoDetails: VideoDetails
)