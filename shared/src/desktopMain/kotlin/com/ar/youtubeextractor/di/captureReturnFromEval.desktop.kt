package com.ar.youtubeextractor.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mozilla.javascript.Context
import org.mozilla.javascript.NativeArray
import org.mozilla.javascript.Scriptable



actual suspend fun captureReturnFromEval(formattedFunction: String): Any? = withContext(Dispatchers.IO) {

    val context = Context.enter()

    return@withContext try {
        val scope: Scriptable = context.initStandardObjects()

        val result = context.evaluateString(scope, formattedFunction, "<cmd>", 1, null)

        if (result is NativeArray) {
            val kotlinList = mutableListOf<Any>()

            for (id in result.ids) {
                if (id is Number) {
                    kotlinList.add(result.get(id.toInt(), result))
                }
            }
            kotlinList
        } else{
            result
        }
    } catch (e: Exception) {
        null
    } finally {
        Context.exit()
    }
}