package com.ar.utils

import com.ar.core.FunctionManager
import com.ar.model.VideoData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder

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

            val decodedUrl = URLDecoder.decode(encodedUrl, "UTF-8")
            val uri = URI(decodedUrl)

            val queryParams = uri.query.split("&").associate {
                val (key, value) = it.split("=")
                key to value
            }.toMutableMap()

            val encryptedSig = pairs["s"]?.replace("%3D","=" ) ?:  return@withContext null
            val nSignature = queryParams["n"] ?: return@withContext null

            val decryptedSignature = decryptSignature(encryptedSig)

            val decryptedNSig = decryptedNSignature(nSignature)


            val queryType = pairs["sp"]

            if (decryptedSignature != null && decryptedNSig != null && queryType != null) {
                queryParams[queryType] = decryptedSignature
                queryParams.replace("n", decryptedNSig)
                val xpc = queryParams["xpc"]+"=="
                queryParams.replace("xpc", xpc)

                val newQuery = queryParams.entries.joinToString("&") { (key, value) ->
                    "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
                }

                val newUri = URI(
                    uri.scheme,
                    uri.authority,
                    uri.path,
                    newQuery,
                    uri.fragment
                )
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
