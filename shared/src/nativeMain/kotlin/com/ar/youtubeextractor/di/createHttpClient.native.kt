package com.ar.youtubeextractor.di

import io.ktor.client.*
import io.ktor.client.engine.darwin.*

actual fun createHttpClient(): HttpClient {
    return HttpClient(Darwin)
}