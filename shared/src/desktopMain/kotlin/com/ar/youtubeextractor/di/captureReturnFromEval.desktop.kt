package com.ar.youtubeextractor.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable



actual suspend fun captureReturnFromEval(formattedFunction: String): Any? = withContext(Dispatchers.IO) {
    // Create a new context to run JavaScript
    val context = Context.enter()

    return@withContext try {
        val scope: Scriptable = context.initStandardObjects()

        val result = context.evaluateString(scope, formattedFunction, "<cmd>", 1, null)

        result
    } catch (e: Exception) {

        null
    } finally {
        Context.exit()
    }
}