@file:OptIn(ExperimentalForeignApi::class)

package com.ar.youtubeextractor

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfFile
import platform.Foundation.stringWithContentsOfURL

@OptIn(ExperimentalForeignApi::class)
actual fun readFile(): String {
    val resourcePath = NSBundle.mainBundle.resourcePath
        ?: throw IllegalStateException("Resource directory not found")

    val fullPath = NSBundle.mainBundle.bundlePath + "/" + "base.js"


    val fileContent = NSString.stringWithContentsOfFile(fullPath, NSUTF8StringEncoding, null)
    return fileContent?.toString() ?: " empty content "
}