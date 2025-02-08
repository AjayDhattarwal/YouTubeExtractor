package com.ar.youtubeextractor.di

import platform.JavaScriptCore.JSContext
import platform.JavaScriptCore.JSValue

actual suspend fun captureReturnFromEval(formattedFunction: String): Any? {
    val context = JSContext()
    val result: JSValue? = context.evaluateScript(formattedFunction)

    return when {
        result == null -> null
        result.isArray -> result.toArray()
        result.isString -> result.toString()
        result.isBoolean -> result.toBool()
        result.isNumber -> result.toDouble()
        else -> result.toObject()
    }
}

//private fun JSValue.toArray(): List<Any?> {
//    val length = this.context?.evaluateScript("this.length")?.toInt32() ?: 0
//    return (0 until length).map { this.context?.evaluateScript("this[$it]") }
//}