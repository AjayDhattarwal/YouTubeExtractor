package com.ar.youtubeextractor.function


import com.ar.youtubeextractor.core.DataError
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.json.Json
import com.ar.youtubeextractor.core.Result
import io.ktor.client.statement.readRawBytes

suspend inline fun <reified T> responseToResult(
    tryWithSting: Boolean = false,
    response: HttpResponse,
): Result<T, DataError.Remote> {
    return when(response.status.value){
        in 200..299 -> {
            try {
                if(tryWithSting){
                    val json = Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }

                    val responseString = response.readRawBytes().decodeToString()
                    Result.Success(json.decodeFromString(responseString))
                }else{
                    Result.Success(response.body<T>())
                }
            } catch (e: NoTransformationFoundException){
                e.printStackTrace()
                Result.Error(DataError.Remote.SERIALIZATION)
            }
        }
        408 -> Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        401 -> Result.Error(DataError.Remote.UNAUTHORIZED)
        409 -> Result.Error(DataError.Remote.CONFLICT)
        429 -> Result.Error(DataError.Remote.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(DataError.Remote.SERVER_ERROR)
        else -> Result.Error(DataError.Remote.UNKNOWN)

    }
}


suspend inline fun <reified T> safeCall(tryWithSting: Boolean = false,  execute: () -> HttpResponse): Result<T, DataError.Remote> {
    val response = try {
        execute()
    } catch (e: SocketTimeoutException){
        return Result.Error(DataError.Remote.REQUEST_TIMEOUT)
    } catch (e: UnresolvedAddressException){
        return Result.Error(DataError.Remote.NO_INTERNET)
    } catch (e: Exception){
        return Result.Error(DataError.Remote.UNKNOWN)
    }

    return responseToResult(tryWithSting = tryWithSting, response)
}
