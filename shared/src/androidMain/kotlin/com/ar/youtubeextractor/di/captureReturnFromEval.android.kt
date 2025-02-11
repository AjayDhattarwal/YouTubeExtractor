package com.ar.youtubeextractor.di

import com.squareup.duktape.Duktape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual suspend fun captureReturnFromEval(formattedFunction: String): Any? = withContext(Dispatchers.IO) {
    val duktape = Duktape.create()
    val result = duktape.evaluate(formattedFunction)
    duktape.close()
    return@withContext result
}


