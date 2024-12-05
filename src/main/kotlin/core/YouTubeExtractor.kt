package com.ar.core

import com.ar.model.VideoData
import com.ar.utils.DataExtractor
import com.ar.utils.DataFetcher
import com.ar.utils.Decryptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

class YouTubeExtractor {

    private val dataFetcher = DataFetcher()

    private val dataExtractor = DataExtractor()

    fun extractVideoData(videoUrl: String, retryCount: Int = 0): VideoData? = runBlocking {

        if (retryCount >= 3) {
            println("Max retry attempts reached.")
            return@runBlocking null
        }

        coroutineScope {
            val htmlContent =  dataFetcher.fetchHtmlPage(videoUrl)

            val playerJsDeferred = async(Dispatchers.IO) {
                val playerUrl = dataExtractor.extractPlayerUrl(htmlContent) ?: return@async null
                dataFetcher.fetchJavaScript(playerUrl = playerUrl, refererUrl = videoUrl)
            }

            val videoDataDeferred = async(Dispatchers.IO) {
                dataExtractor.extractVideoData(htmlContent)
            }

            val playerJS = playerJsDeferred.await()
            val videoData = videoDataDeferred.await()

            if (playerJS != null && videoData != null) {

                val decryptor = Decryptor(jsCode = playerJS)

                val decryptedVideoData = decryptor.decryptVideoData(videoData)

                return@coroutineScope decryptedVideoData

            } else{
                return@coroutineScope extractVideoData(videoUrl, retryCount + 1)
            }
        }
    }

}

