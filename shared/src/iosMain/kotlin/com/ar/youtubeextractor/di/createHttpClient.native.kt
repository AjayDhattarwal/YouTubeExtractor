package com.ar.youtubeextractor.di

import io.ktor.client.HttpClientConfig
import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.engine.darwin.DarwinClientEngineConfig
import io.ktor.client.engine.darwin.certificates.CertificatePinner
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import platform.CFNetwork.kCFStreamPropertySSLContext
import platform.Foundation.NSURLAuthenticationChallenge
import platform.Foundation.NSURLCredential
import platform.Foundation.NSURLSession
import platform.Foundation.NSURLSessionAuthChallengeDisposition
import platform.Foundation.NSURLSessionTask

actual fun createHttpClient(): HttpClient {
    return HttpClient(Darwin) {
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

