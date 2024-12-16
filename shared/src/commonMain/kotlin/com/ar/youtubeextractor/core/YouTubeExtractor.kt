package com.ar.youtubeextractor.core

import com.ar.youtubeextractor.function.randomUserAgent
import com.ar.youtubeextractor.model.VideoData
import com.ar.youtubeextractor.utils.DataExtractor
import com.ar.youtubeextractor.utils.DataFetcher
import com.ar.youtubeextractor.utils.Decryptor
import kotlinx.coroutines.*

class YouTubeExtractor {

    private val dataFetcher = DataFetcher()

    private val dataExtractor = DataExtractor()

    fun extractVideoData(videoUrl: String, retryCount: Int = 0): VideoData? = runBlocking {

        if (retryCount >= 3) {
            println("Max retry attempts reached.")
            return@runBlocking null
        }

        withContext(Dispatchers.IO) {
            val userAgentConfig = randomUserAgent()
            val htmlContent =  dataFetcher.fetchHtmlPage(videoUrl, userAgentConfig)

            val playerJsDeferred = async(Dispatchers.IO) {
                val playerUrl = dataExtractor.extractPlayerUrl(htmlContent, videoUrl = videoUrl, userAgent =  userAgentConfig) ?: return@async null
                dataFetcher.fetchJavaScript(playerUrl = playerUrl, refererUrl = videoUrl, userAgent = userAgentConfig)
            }

            val videoDataDeferred = async(Dispatchers.IO) {
                dataExtractor.extractVideoData(html = htmlContent, videoUrl = videoUrl, userAgent = userAgentConfig)
            }

            val playerJS = playerJsDeferred.await()

            val videoData = videoDataDeferred.await()

            if (playerJS != null && videoData != null) {

                val decryptor = Decryptor(jsCode = playerJS)

                val decryptedVideoData = decryptor.decryptVideoData(videoData)

                return@withContext decryptedVideoData

            } else{
                return@withContext extractVideoData(videoUrl, retryCount + 1)
            }
        }
    }

}

