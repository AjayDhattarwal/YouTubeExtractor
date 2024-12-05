package com.ar.utils

import com.ar.model.VideoData
import kotlinx.coroutines.Dispatchers
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

    suspend fun extractVideoData(html: String, retryCount: Int = 0): VideoData? = withContext(Dispatchers.IO) {
        if (retryCount >= 3) {
            return@withContext null
        }

        val pattern = """<script nonce="[^"]*">var\sytInitialPlayerResponse\s*=\s*(\{.*?})(?:;|</script>)"""
        val htmlMatcher = extractValueFromHtml(html,pattern)

        val jsonString = htmlMatcher?.groupValues?.getOrNull(1)


        if(jsonString!= null){
            val json = Json {
                ignoreUnknownKeys = true
            }
            val videoData = json.decodeFromString<VideoData>(jsonString)
            return@withContext videoData
        }else{
            val refreshedHtml = dataFetcher.fetchHtmlPage("https://www.youtube.com/watch?v=MFIYbZyyUf8")
            return@withContext extractVideoData(refreshedHtml, retryCount + 1)
        }

    }

    suspend fun extractPlayerUrl(html: String, retryCount: Int = 0):String? = withContext(Dispatchers.IO) {
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
            val refreshedHtml = dataFetcher.fetchHtmlPage("https://www.youtube.com/watch?v=MFIYbZyyUf8")
            extractPlayerUrl(refreshedHtml , retryCount + 1)
        }
    }
}
