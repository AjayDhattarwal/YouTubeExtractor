package com.ar.youtubeextractor.model

import kotlinx.serialization.Serializable

@Serializable
data class VideoData(
    val responseContext: ResponseContext = ResponseContext(),
    val streamingData: StreamingData = StreamingData(),
    val videoDetails: VideoDetails = VideoDetails(),
)

