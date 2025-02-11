package com.ar.youtubeextractor.di

import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual fun createHttpClient(): HttpClient {
    return HttpClient(OkHttp){
        followRedirects = true

//        install(Logging){
//            level = LogLevel.ALL
//        }

        install(ContentEncoding) {
            deflate(1.0F)
            gzip()
        }

        install(HttpCookies){
            storage = AcceptAllCookiesStorage()

        }
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }

    }

}