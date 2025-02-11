package com.ar.youtubeextractor.model.request

data class InnerTubeClient(
    val innerTubeContext: InnerTubeContext,
    val innerTubeContextClientName: Int,
    val requirePoToken: Boolean = false,
    val requireJsPlayer: Boolean = false,
    val requireAuth: Boolean = false,
    val supportsCookies: Boolean = false,
    val innerTubeHost: String? = null
)

data class InnerTubeContext(
    val client: Client
)

data class Client(
    val clientName: String,
    val clientVersion: String,
    val userAgent: String? = null,
    val androidSdkVersion: Int? = null,
    val osName: String? = null,
    val osVersion: String? = null,
    val deviceMake: String? = null,
    val deviceModel: String? = null
)