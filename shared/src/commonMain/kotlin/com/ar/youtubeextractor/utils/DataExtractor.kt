package com.ar.youtubeextractor.utils

import com.ar.youtubeextractor.model.VideoData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class DataExtractor {

    private val dataFetcher = DataFetcher()

    private fun extractValueFromHtml(html: String, pattern: String): MatchResult? {
        return try {
            val regex = Regex(pattern)
            val matchResult = regex.find(html)
            matchResult
        }catch (e: Exception) {
            println(e.message)
            null
        }
    }

    suspend fun extractVideoData(
        html: String,
        retryCount: Int = 0,
        videoUrl: String,
        userAgent: String
    ): VideoData? = withContext(Dispatchers.IO) {
        try {
            if (retryCount >= 3) {
                return@withContext null
            }
            val pattern = """<script nonce="[^"]*">var\sytInitialPlayerResponse\s*=\s*(\{.*?\})(?:;|<\/script>)"""

            val htmlMatcher = extractValueFromHtml(html, pattern)

            val jsonString = htmlMatcher?.groupValues?.getOrNull(1)

            if (jsonString != null) {
                val json = Json {
                    ignoreUnknownKeys = true;
                    isLenient = true
                }
                return@withContext json.decodeFromString<VideoData>(jsonString)
            } else {
                val refreshedHtml = dataFetcher.fetchHtmlPage(videoUrl, userAgent) ?: return@withContext null
                return@withContext extractVideoData(refreshedHtml, retryCount + 1, videoUrl, userAgent)
            }
        } catch (e: Exception) {
            return@withContext null
        }

    }

    suspend fun extractPlayerUrl(html: String, retryCount: Int = 0, videoUrl: String, userAgent: String):String? = withContext(Dispatchers.IO) {
        if (retryCount >= 3) {
            return@withContext null
        }
        val pattern = "/[^/]+/player/.*?/base\\.js"
        val htmlMatcher = extractValueFromHtml(html,pattern)

        println(htmlMatcher?.value)

        return@withContext if(htmlMatcher != null){
            val playerUrl =  "https://www.youtube.com" + htmlMatcher.value
            playerUrl
        }else{
            val refreshedHtml = dataFetcher.fetchHtmlPage(videoUrl, userAgent)?: return@withContext  null
            extractPlayerUrl(refreshedHtml , retryCount + 1, videoUrl, userAgent)
        }
    }
}
