package com.ar.youtubeextractor.utils

import com.ar.youtubeextractor.core.FunctionManager
import com.ar.youtubeextractor.model.VideoData
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.decodeURLQueryComponent
import io.ktor.http.encodeURLParameter
import io.ktor.util.flattenEntries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class Decryptor(private val jsCode: String) {

    private val jsSignatureExtractor = JsSignatureExtractor(jsCode)
    private val jsNsigExtractor = JsNsigExtractor(jsCode)


    suspend fun decryptVideoData(videoData: VideoData): VideoData = withContext(Dispatchers.IO) {

        val staticFormatsDeferred =
            videoData.streamingData.formats?.map { format ->
                async(Dispatchers.IO) {
                    format.copy(
                        streamingUrl = format.signatureCipher?.let { getStreamingUrl(signatureCipher = it) }
                    )
                }
            }


        val adaptiveFormatsDeferred =
            videoData.streamingData.adaptiveFormats?.map { format ->
                async(Dispatchers.IO) {
                    format.copy(
                        streamingUrl = format.signatureCipher?.let { getStreamingUrl(signatureCipher = it) }
                    )
                }
            }

        val staticFormats = staticFormatsDeferred?.awaitAll()
        val adaptiveFormats = adaptiveFormatsDeferred?.awaitAll()

        videoData.copy(
            streamingData = videoData.streamingData.copy(
                formats = staticFormats,
                adaptiveFormats = adaptiveFormats
            )
        )
    }


    suspend fun getStreamingUrl(signatureCipher: String): String? = withContext(Dispatchers.IO)  {

        return@withContext try {
            val regexUrlExtractor = "(\\w+)=([^&]+)".toRegex()
            val pairs = regexUrlExtractor.findAll(signatureCipher).map { matchResult ->
                matchResult.groupValues[1] to matchResult.groupValues[2]
            }.toMap()

            val encodedUrl = pairs["url"]

            val decodedUrl = encodedUrl?.decodeURLQueryComponent()?: return@withContext null
            val url = Url(decodedUrl)

            val queryParams = url.parameters.flattenEntries().associate { it.first to it.second }.toMutableMap()

            val encryptedSig = pairs["s"]?.replace("%3D","=" ) ?:  return@withContext null
            val nSignature = queryParams["n"] ?: return@withContext null

            val decryptedSignature = decryptSignature(encryptedSig)

            val decryptedNSig = decryptedNSignature(nSignature)


            val queryType = pairs["sp"]

            if (decryptedSignature != null && decryptedNSig != null && queryType != null) {
                queryParams[queryType] = decryptedSignature

                queryParams["n"] =  decryptedNSig
//                val xpc = queryParams["xpc"]+"=="
//                queryParams["xpc"] = xpc


                val newQuery = queryParams.entries.joinToString("&") { (key, value) ->
                    "${key.encodeURLParameter()}=${value.encodeURLParameter()}"
                }

                val newUri = URLBuilder(decodedUrl).apply {
                    parameters.clear()
                    newQuery.split("&").forEach { param ->
                        val (key, value) = param.split("=")
                        parameters.append(key, value)
                    }
                }.build()

                newUri.toString().replace("%25", "%")
            } else{
                null
            }
        } catch (e:Exception){
            println("error while decoding Url : $e")
            null
        }

    }



    private suspend fun decryptedNSignature(nSignature: String): String? = withContext (Dispatchers.IO){
        val nFunctionCode = FunctionManager.getNFunctionCode()
        if(nFunctionCode != null){
            return@withContext jsNsigExtractor.nFunctionResponse(nFunctionCode, nSignature)
        }

        val jsNsigExtractor = JsNsigExtractor(jsCode)

        val nFunctionName = jsNsigExtractor.extractNFunctionName()
        if(nFunctionName != null){
            val functionCode = jsNsigExtractor.extractNFunctionCode(nFunctionName)
            FunctionManager.updateNFunctionCode(functionCode)
            return@withContext jsNsigExtractor.nFunctionResponse(functionCode, nSignature)
        }
        return@withContext null
    }


    private suspend fun decryptSignature(encryptedSig: String): String? = withContext (Dispatchers.IO){

        val funcName = FunctionManager.getMainFunName() ?: return@withContext null

        val sigFunctionCode =  FunctionManager.getSignatureFunctionCode()


        if(sigFunctionCode != null){
            return@withContext jsSignatureExtractor.signatureFunctionResponse(sigFunctionCode, encryptedSig)
        }

        return@withContext try {

            val encryptedSigFunCode = jsSignatureExtractor.extractJSFunctionCode(funcName)

            FunctionManager.updateSignatureFunctionCode(encryptedSigFunCode)

            jsSignatureExtractor.signatureFunctionResponse(encryptedSigFunCode, encryptedSig)

        } catch (e: Exception) {
            println("Error processing JS code: ${e.message}")
            null
        }
    }



}
