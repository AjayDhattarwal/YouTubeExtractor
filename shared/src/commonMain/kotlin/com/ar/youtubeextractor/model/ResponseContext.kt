package com.ar.youtubeextractor.model

import kotlinx.serialization.Serializable

@Serializable
data class ResponseContext(
    val serviceTrackingParams: List<ServiceTrackingParams> = emptyList()
)

@Serializable
data class ServiceTrackingParams(
    val params: List<Params> = emptyList()
)

@Serializable
data class Params(
    val key: String = "",
    val value: String = ""
)