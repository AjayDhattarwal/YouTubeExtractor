package com.ar.youtubeextractor

import com.ar.youtubeextractor.function.getYouTubeVideoId
import kotlinx.coroutines.runBlocking
import org.junit.Test


class MainKtTest{

    @Test
    fun `Youtube id fetch  `(){
        runBlocking {
            val urls = listOf(
                "https://www.youtube.com/watch?v=1OAjeECW90E&bpctr=9999999999&has_verified=1",
                "https://www.youtube.com/watch?v=1OAjeECW90E",
                "https://www.youtube.com/watch?bpctr=9999999999&has_verified=1&v=1OAjeECW90E",
                "https://youtu.be/1OAjeECW90E",
                "https://www.youtube.com/embed/1OAjeECW90E",
                "https://www.youtube.com/v/1OAjeECW90E"
            )

            urls.forEach { url ->
                println("URL: $url -> Video ID: ${getYouTubeVideoId(url)}")
            }
        }
    }

    @Test
    fun `fetch html page`(){
        runBlocking {
            mainFunForTest()
        }
    }
}

