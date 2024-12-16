package com.ar.youtubeextractor.model


import com.ar.youtubeextractor.model.Range
import kotlinx.serialization.Serializable

@Serializable
data class Format(
    val itag: Int? = null,
//    val url: String? = null,   // not in use
    val mimeType: String? = null,
    val bitrate: Int? = null,
    val width: Int?= null,
    val height: Int? = null,
    val initRange: Range? = null,
    val indexRange: Range? = null,
    val lastModified: String? = null,
    val contentLength: String? = null,
    val quality: String? = null,
    val fps: Int? = null,
    val qualityLabel: String? = null,
    val projectionType: String? = null,
    val averageBitrate: Int? = null,
    val audioQuality: String? = null,
    val approxDurationMs: String,
    val audioSampleRate: String? = null,
    val audioChannels: Int? = null,
    val signatureCipher: String? = null,
    val loudnessDb: Float? = null,
    val streamingUrl: String? = null,
){
    val isAudioOnly: Boolean =
        height == null


    val isVideoAudioBoth: Boolean =
        width != null && height != null && initRange == null && indexRange == null

    val isVideoOnly: Boolean =
        width != null && height != null && initRange != null && indexRange != null

    val isDecoded: Boolean = streamingUrl != null

}