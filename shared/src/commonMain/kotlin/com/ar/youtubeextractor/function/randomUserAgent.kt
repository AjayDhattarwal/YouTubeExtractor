package com.ar.youtubeextractor.function

import kotlin.random.Random

fun randomUserAgent(): String {
    val userAgentTemplate = "Mozilla/5.0 (iPhone; CPU iPhone OS %s like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/%s Mobile/15E148 Safari/604.1"
    val iOSVersions = listOf(
        "14_6",
        "14_7",
        "14_8",
        "15_0",
        "15_1",
        "15_2",
        "15_3",
        "15_4",
        "15_5",
        "16_0",
        "16_1",
        "16_2",
        "16_3",
        "16_4",
        "17_0"
    )
    val safariVersions = listOf(
        "14.1.1",
        "14.1.2",
        "15.0",
        "15.1",
        "15.2",
        "15.3",
        "15.4",
        "15.5",
        "16.0",
        "16.1",
        "16.2",
        "16.3",
        "16.4",
        "17.0"
    )
    val randomIOSVersion = iOSVersions[Random.nextInt(iOSVersions.size)]
    val randomSafariVersion = safariVersions[Random.nextInt(safariVersions.size)]
    return userAgentTemplate.format(randomIOSVersion, randomSafariVersion)
}

fun String.format(vararg args: Any?): String {
    var result = this
    args.forEach { arg ->
        result = result.replaceFirst("%s", arg.toString(), ignoreCase = true)
    }
    return result
}
