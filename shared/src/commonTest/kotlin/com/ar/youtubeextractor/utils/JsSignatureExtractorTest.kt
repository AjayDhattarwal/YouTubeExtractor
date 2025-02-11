package com.ar.youtubeextractor.utils

import com.ar.youtubeextractor.di.createHttpClient
import com.ar.youtubeextractor.readFile
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.readRawBytes
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals


class JsSignatureExtractorTest() {



//    for IOS Testing

    @Test
    fun `extractJSFunctionCode Test for Android and desktop`() {
        val  jsDownloader = JsFileDownloader(createHttpClient())

        runBlocking {
            val jsCode = jsDownloader.loadJsCode("https://www.youtube.com/s/player/9c6dfc4a/player_ias.vflset/en_US/base.js")
            val jsSignatureExtractor = JsSignatureExtractor(jsCode)
            val result = jsSignatureExtractor.extractJSFunctionCode("nO6")
            assertEquals<String>("(P, P=P.split(\"\");Ug.YD(P,79);Ug.Kr(P,38);Ug.R9(P,2);Ug.YD(P,74);Ug.R9(P,3);Ug.Kr(P,17);Ug.YD(P,71);return P.join(\"\"))", result.toString())
        }
    }

    @Test
    fun `extractNFunctionCode Test for IOS`() {
        runBlocking {
            val jsCode = readFile()
            val jsSignatureExtractor = JsNsigExtractor(jsCode)
            val result = jsSignatureExtractor.extractNFunctionCode("h5w")
            println(result)
        }
    }
}



private class JsFileDownloader(private val client: HttpClient) {

    suspend fun loadJsCode(url: String): String {
        return client.get(url) {
            headers {
                append("origin", "https://www.youtube.com")
            }
        }.readRawBytes().decodeToString()
    }
}
