package com.ar.utils

import com.ar.core.FunctionManager
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataFetcher {

    suspend fun fetchHtmlPage(url: String): String {
        return withContext(Dispatchers.IO) {
            val client = HttpClient(CIO)
            val document = client.get(url){
                headers {
                    append("X-YouTube-Client-Name", "MWEB")
                    append("X-YouTube-Client-Version", "16.48.4")
                    append("origin", "https://www.youtube.com")
                    append("referer", url)
                    append("User-Agent","Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.101 Mobile Safari/537.36")
                }
            }
            document.bodyAsText()
        }

    }

    suspend fun fetchJavaScript(playerUrl: String, refererUrl: String ): String? {
        val client = HttpClient(CIO)

        try {
            val response: HttpResponse = client.get(playerUrl){
                headers {
                    append("X-YouTube-Client-Name", "MWEB")
                    append("X-YouTube-Client-Version", "16.48.4")
                    append("origin", "https://www.youtube.com")
                    append("referer", refererUrl)
                    append("User-Agent","Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.101 Mobile Safari/537.36")
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
            client.close()
        }
    }

    private fun fetchRequirements(jsCode: String): Boolean{
        val patterns = listOf(
            """\b[cs]\s*&&\s*[adf]\.set\([^,]+\s*,\s*encodeURIComponent\s*\(\s*([a-zA-Z0-9$]+)\(""",
            """\b[a-zA-Z0-9]+\s*&&\s*[a-zA-Z0-9]+\.set\([^,]+\s*,\s*encodeURIComponent\s*\(\s*([a-zA-Z0-9$]+)\(""",
            """\bm=([a-zA-Z0-9$]{2,})\(decodeURIComponent\(h\.s\)\)""",
            """\bc&&\(c=([a-zA-Z0-9$]{2,})\(decodeURIComponent\(c\)\)""",
            """(?:\b|[^a-zA-Z0-9${'$'}])([a-zA-Z0-9${'$'}]{2,})\s*=\s*function\(\s*a\s*\)\s*\{\s*a\s*=\s*a\.split\(\s*"""",
            """(?<sig>[a-zA-Z0-9${'$'}]+)\s*=\s*function\(\s*a\s*\)\s*\{\s*a\s*=\s*a\.split\(\s*"""",
            """(["'])signature\1\s*,\s*([a-zA-Z0-9$]+)\(""",
            """\.sig\|\|([a-zA-Z0-9$]+)\(""",
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
