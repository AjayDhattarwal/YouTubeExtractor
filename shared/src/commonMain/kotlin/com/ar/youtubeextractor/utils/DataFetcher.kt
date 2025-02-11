package com.ar.youtubeextractor.utils

import com.ar.youtubeextractor.core.DataError
import com.ar.youtubeextractor.core.FunctionManager
import com.ar.youtubeextractor.core.Result
import com.ar.youtubeextractor.di.createHttpClient
import com.ar.youtubeextractor.function.getRequestBody
import com.ar.youtubeextractor.function.safeCall
import com.ar.youtubeextractor.model.VideoData
import com.ar.youtubeextractor.model.request.toJson
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class DataFetcher {

    private val httpClient = createHttpClient()

    suspend fun fetchHtmlPage(url: String , userAgent: String, tryCount: Int = 0): String? {
        return withContext(Dispatchers.IO) {
            try {
                val document = httpClient.get(url){
                    headers {
                        append("Accept-Language" , "en-US,en;q=0.9")
                        append(HttpHeaders.Origin, "https://www.youtube.com")
                        append(HttpHeaders.Referrer, url)
                        append("Sec-Fetch-Mode", "navigate")
                        append("Accept-Encoding", "gzip, deflate")
                    }
                }
                document.readRawBytes().decodeToString()
            }catch (e: Exception){
                if(tryCount > 2){
                    println(e.message)
                    null
                }else{
                    fetchHtmlPage(url, userAgent, tryCount + 1)
                }
            }
        }

    }

    suspend fun fetchJavaScript(playerUrl: String, refererUrl: String, userAgent: String): String? {

        try {
            val response: HttpResponse = httpClient.get(playerUrl){
                headers {
                    append("Accept-Language" , "en-US,en;q=0.9")
                    append(HttpHeaders.Origin, "https://www.youtube.com")
                    append(HttpHeaders.Referrer, refererUrl)
                    append("Sec-Fetch-Mode", "navigate")
                    append("Accept-Encoding", "gzip, deflate")
                }
            }

            if (response.status == HttpStatusCode.OK) {
                val jsCode = response.readRawBytes().decodeToString()
                FunctionManager.updateJsCode(jsCode)
                if(fetchRequirements(jsCode)) {
                    return jsCode
                }else{
                    println("requirement failed")
                    return null
                }
            } else {
                println("Failed to fetch JavaScript. HTTP Status: ${response.status}")
                return null
            }
        } catch (e: Exception) {
            println("Error fetching JavaScript: ${e.message}")
            return null
        }
    }

    private fun fetchRequirements(jsCode: String): Boolean{


        val patterns = listOf(
            """\b[A-Za-z0-9_]+&&\([A-Za-z]=([A-Za-z0-9_+]{2,})\(decodeURIComponent\([A-Za-z]\)\)""",
            """([a-zA-Z0-9_$]+)\s*=\s*function\(\s*([a-zA-Z0-9_$]+)\s*\)\s*\{\s*\2\s*=\s*\2\.split\(\s*""\s*\)\s*;\s*[^}]+;\s*return\s+\2\.join\(\s*""\s*\)""",
            """(?:\b|[^a-zA-Z0-9_$])([a-zA-Z0-9_$]{2,})\s*=\s*function\(\s*a\s*\)\s*\{\s*a\s*=\s*a\.split\(\s*""\s*\)(?:;[a-zA-Z0-9_$]{2}\.[a-zA-Z0-9_$]{2}\(a,\d+\))?""",

            """\b[cs]\s*&&\s*[adf]\.set\([^,]+\s*,\s*encodeURIComponent\s*\(\s*([a-zA-Z0-9$]+)\(""",
            """\b[a-zA-Z0-9]+\s*&&\s*[a-zA-Z0-9]+\.set\([^,]+\s*,\s*encodeURIComponent\s*\(\s*([a-zA-Z0-9$]+)\(""",
            """\bm=([a-zA-Z0-9$]{2,})\(decodeURIComponent\(h\.s\)\)""",
            // not work always
            """(["'])signature\1\s*,\s*([a-zA-Z0-9$]+)\(""",
            """\.sig\|\|([a-zA-Z0-9$]+)\(""",
            """yt\.akamaized\.net/\)\s*\|\|\s*.*?\s*[cs]\s*&&\s*[adf]\.set\([^,]+\s*,\s*(?:encodeURIComponent\s*\()?\s*([a-zA-Z0-9$]+)\(""",
            """\b[cs]\s*&&\s*[adf]\.set\([^,]+\s*,\s*([a-zA-Z0-9$]+)\(""",
            """\bc\s*&&\s*[a-zA-Z0-9]+\.set\([^,]+\s*,\s*\([^)]*\)\s*\(\s*([a-zA-Z0-9$]+)\("""

        )

        try {
            for (pattern in patterns) {
                val regex = Regex(pattern)
                val match = regex.find(jsCode)
                if (match != null) {
                    val funcName = match.groups.get(1)?.value
                    if(funcName != null){
                        FunctionManager.updateMainFunName(funcName)
                        break
                    }
                }
            }
            return true
        }catch (e: Exception){
            println("error: found >> $e")
            return false
        }
    }

    suspend fun getDataByAPI(googleId: String, videoID:String, platform: String): Result<VideoData,DataError> {

        val requestBody = getRequestBody(platform, videoID)

        return safeCall{
            val response = httpClient.post("https://www.youtube.com/youtubei/v1/player?prettyPrint=false") {
                headers {
                    append("Accept-Language", "en-US,en;q=0.5")
                    append("Sec-Fetch-Mode", "navigate")
                    append("Accept-Encoding", "gzip, deflate")
                    append("Origin", "https://www.youtube.com")
                    requestBody.context?.client?.userAgent?.let{
                        append(HttpHeaders.UserAgent,  it)
                    }
                    append("Accept", "text/html,application/xhtml+xml,application/xml")
                    append("Content-Type", "application/json")
                    append("X-Goog-Visitor-Id", googleId)
                }
                setBody(toJson(requestBody))
            }


            response
        }
    }

}

