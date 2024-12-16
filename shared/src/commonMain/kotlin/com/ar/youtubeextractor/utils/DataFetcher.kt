package com.ar.youtubeextractor.utils

import com.ar.youtubeextractor.core.FunctionManager
import com.ar.youtubeextractor.di.createHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class DataFetcher {


    suspend fun fetchHtmlPage(url: String , userAgent: String): String {
        return withContext(Dispatchers.IO) {
            val client = createHttpClient()
            val document = client.get(url){
                headers {
                    append("Accept-Language" , "en-us,en;q=0.5")
                    append("X-YouTube-Client-Name", "IOS")
                    append("X-YouTube-Client-Version", "16.48.4")
                    append("origin", "https://www.youtube.com")
                    append("referer", url)
                    append("User-Agent",userAgent)
                }
            }
            document.bodyAsText()
        }

    }

    suspend fun fetchJavaScript(playerUrl: String, refererUrl: String, userAgent: String ): String? {
        val client = createHttpClient()
        try {
            val response: HttpResponse = client.get(playerUrl){
                headers {
                    append("Accept-Language" , "en-us,en;q=0.5")
                    append("X-YouTube-Client-Name", "IOS")
                    append("X-YouTube-Client-Version", "16.48.4")
                    append("origin", "https://www.youtube.com")
                    append("referer", refererUrl)
                    append("User-Agent",userAgent)
                }
            }

            if (response.status == HttpStatusCode.OK) {
                val jsCode = response.bodyAsText()
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
        } finally {
//            client.close()
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

}
