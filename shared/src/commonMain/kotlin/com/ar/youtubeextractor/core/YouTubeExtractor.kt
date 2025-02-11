package com.ar.youtubeextractor.core

import com.ar.youtubeextractor.function.getYouTubeVideoId
import com.ar.youtubeextractor.function.randomUserAgent
import com.ar.youtubeextractor.model.VideoData
import com.ar.youtubeextractor.utils.DataExtractor
import com.ar.youtubeextractor.utils.DataFetcher
import com.ar.youtubeextractor.utils.Decryptor
import kotlinx.coroutines.*




class YouTubeExtractor {

    private val dataFetcher = DataFetcher()

    private val dataExtractor = DataExtractor()

    /**
     * Extracts video data from a given video URL.
     *
     * This function orchestrates the process of retrieving video information from a web page.
     * It first fetches the HTML content of the video's web page, extracts initial video data,
     * potentially decrypts streaming URLs found within the extracted data, and then supplements
     * this data with additional information retrieved through an external API.
     *
     * **Fallback Suggestion:**
     * If you encounter issues with specific platforms ( try with IOS and Android
     *
     * **Workflow:**
     * 1. **Fetch HTML:** Retrieves the HTML content of the provided video URL.
     * 2. **Extract Initial Data:** Parses the HTML to extract core video data like title, thumbnail, and potentially initial streaming URLs.
     * 3. **Extract Player URL & Fetch JS:** Attempts to find a JavaScript player URL within the HTML and fetches the corresponding JavaScript content. This JS content is crucial for decryption.
     * 4. **Decrypt Streaming URLs (if needed):** If a JavaScript player is found and the initial data contains encrypted streaming URLs, the function uses the player's JavaScript to decrypt them.
     * 5. **Fetch Additional Data (API):** Uses extracted identifiers (like Google visitor ID and video ID) to call an external API and retrieve supplementary information.
     * 6. **Merge Data:** Combines the initially extracted data, decrypted URLs, and API data into a final `VideoData` object.
     * 7. **Fallback:** If the API data fetching fails or crucial identifiers are missing, it defaults to returning the initially extracted video data. If no data can be extracted, returns an error.
     *
     * **Platform Specificity:**
     * The `platform` parameter allows for specifying the target platform (e.g., "ios", "android", "Mweb", "web", "tv").
     * This affects the data fetched from the API, as different platforms might provide varying data structures.
     * - **ios**: Targets iOS-specific data.
     * - **android**: Targets Android-specific data.
     * - **Mweb**: Targets mobile web-specific data.
     * - **web**: Targets desktop web-specific data.
     * - **tv**: Targets TV-specific data.
     * - **default**: Falls back to iOS platform logic if the specified platform is invalid.*/
    suspend fun extractVideoData(videoUrl: String, platform: String = "ios"): Result<VideoData, DataError> {

         return withContext(Dispatchers.IO) {
            val userAgentConfig = randomUserAgent()
             val htmlContent = dataFetcher.fetchHtmlPage(videoUrl, userAgentConfig)
                 ?: return@withContext Result.Error(DataError.Remote.UNKNOWN)

            val playerJsDeferred = async {
                val playerUrl = dataExtractor.extractPlayerUrl(
                    htmlContent,
                    videoUrl = videoUrl,
                    userAgent = userAgentConfig
                ) ?: return@async null

                dataFetcher.fetchJavaScript(
                    playerUrl = playerUrl,
                    refererUrl = videoUrl,
                    userAgent = userAgentConfig
                )
            }

            val videoDataDeferred = async {
                dataExtractor.extractVideoData(
                    html = htmlContent,
                    videoUrl = videoUrl,
                    userAgent = userAgentConfig
                )
            }

             var sampleVideoData = videoDataDeferred.await()
             val playerJS = playerJsDeferred.await()
             var decryptor: Decryptor? = null

             if(sampleVideoData != null && playerJS != null){
                 decryptor = Decryptor(jsCode = playerJS)
                 sampleVideoData = decryptor.decryptVideoData(sampleVideoData)
             }

             val googleId = sampleVideoData?.responseContext?.serviceTrackingParams?.first()?.params?.find {
                 it.key == "visitor_data"
             }?.value

             val videoId = getYouTubeVideoId(videoUrl)

             if(googleId != null && videoId != null){
                 dataFetcher.getDataByAPI(googleId = googleId , videoID = videoId, platform = platform ).map {
                     it.copy(
                         streamingData = it.streamingData.copy(
                             formats = sampleVideoData?.streamingData?.formats ?: emptyList(),
                             adaptiveFormats = it.streamingData.adaptiveFormats?.map { format->
                                 format.copy(
                                     url = format.url ?: format.signatureCipher?.let { sig -> decryptor?.getStreamingUrl(sig) }
                                 )
                             }
                         )
                     )
                 }
             }else{
                 if(sampleVideoData != null){
                     Result.Success(sampleVideoData)
                 }else{
                     Result.Error(DataError.Remote.UNKNOWN)
                 }
             }

        }
    }


}