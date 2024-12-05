package com.ar.model

import kotlinx.serialization.Serializable

@Serializable
data class Range(
    val start: String,
    val end: String
)