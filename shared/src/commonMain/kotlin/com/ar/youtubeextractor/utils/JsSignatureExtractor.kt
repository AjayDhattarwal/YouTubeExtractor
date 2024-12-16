package com.ar.youtubeextractor.utils

import com.ar.youtubeextractor.core.FunctionManager
import com.ar.youtubeextractor.core.JsUtils.MATCHING_PARENS
import com.ar.youtubeextractor.core.JsUtils.QUOTES
import com.ar.youtubeextractor.di.captureReturnFromEval
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class JsSignatureExtractor(private val jsCode: String) {



    private suspend fun captureReturn(formattedFunction: String): String? = withContext(Dispatchers.IO) {

        return@withContext try {
            val result = captureReturnFromEval(formattedFunction)  as String?
            result
        }catch (e: Exception){
            println("error: >> ${e.message}")
            null
        }
    }

    suspend fun signatureFunctionResponse(functionCode: Pair<String, String>, encryptedSig: String): String{

        val jsNsigExtractor = JsNsigExtractor(jsCode)

        val extractInternalValue = if(FunctionManager.getSignatureVariable() != null){
            FunctionManager.getSignatureVariable()
        }else{
            val value = jsNsigExtractor.extractVariableAssignment(code = functionCode.second)
            FunctionManager.updateSignatureVariable(value)
            value

        }


        val script = extractInternalValue + functionCode.second

        val formattedFunction = "(function(${functionCode.first}) { ${script} }(\"$encryptedSig\"))"

        return captureReturn(formattedFunction) ?: ""
    }



    fun extractJSFunctionCode(funcName: String):Pair<String,String> {
        val funcNameEscaped = Regex.escape(funcName)
        val pattern = """
        (?x)
        (?:
            function\s+$funcNameEscaped|
            [\{;,]\s*$funcNameEscaped\s*=\s*function|
            (?:var|const|let)\s+$funcNameEscaped\s*=\s*function
        )\s*
        \((?<args>[^)]*)\)\s*
        (?<code>\{.+\})
    """.trimIndent()
        val regex = Regex(pattern)
        val matchResult = regex.find(jsCode)
            ?: throw Exception("Could not find JS function \"$funcName\"")

        val args = matchResult.groups["args"]?.value ?: ""
        val code = matchResult.groups["code"]?.value ?: ""


        val separatedCode = separateAtParen(code)
        val value = Pair(args.split(",").map { it.trim() }, separatedCode)

        return Pair(value.first.get(0), value.second.first)

    }



    private fun separateAtParen(expr: String, delim: String? = null): Pair<String, String> {
        val effectiveDelim = delim ?: MATCHING_PARENS[expr.first()] ?: throw Exception("No matching parenthesis for $expr")
        val separated = separate(expr, delim = effectiveDelim.toString(), maxSplit = 1).toList()

        if (separated.size < 2) {
            throw Exception("No terminating paren $delim in $expr")
        }
        return separated[0].drop(1).trim() to separated[1].trim()
    }


    private fun separate(expr: String, delim: String = ",", maxSplit: Int? = null): Sequence<String> {
        val opChars = "+-*/%&|^=<>!,;{}:["
        // Counters to track nested parentheses
        val counters = mutableMapOf<Char, Int>().apply {
            MATCHING_PARENS.values.forEach { this[it] = 0 }
        }

        var start = 0
        var splits = 0
        var pos = 0
        val delimLen = delim.length - 1
        var inQuote: Char? = null
        var escaping = false
        var afterOp = true
        var inRegexCharGroup = false

        return sequence {
            for (idx in expr.indices) {
                val char = expr[idx]

                // Handle parenthesis balancing
                if (inQuote == null && char in MATCHING_PARENS.keys) {
                    counters[MATCHING_PARENS[char] ?: continue] = counters[MATCHING_PARENS[char] ?: continue]!! + 1
                } else if (inQuote == null && char in MATCHING_PARENS.values) {
                    // Ensure that counters do not go negative
                    if (counters[char] != 0) {
                        counters[char] = counters[char]!! - 1
                    }
                } else if (!escaping) {
                    // Handle quotes
                    if (char in QUOTES && (inQuote == null || inQuote == char)) {
                        if (inQuote != null || afterOp || char != '/') {
                            inQuote = if (inQuote != null && !inRegexCharGroup) null else char
                        }
                    }
                    // Handle regex group
                    else if (inQuote == '/' && char in "[]") {
                        inRegexCharGroup = char == '['
                    }
                }

                // Handle escape characters
                escaping = escaping.not() && inQuote != null && char == '\\'

                // Check if current character is a unary operator
                val inUnaryOp = inQuote == null && !inRegexCharGroup && afterOp !in listOf(true, false) && char in "-+"
                afterOp = if (inQuote == null && char in opChars) true else char.isWhitespace() && afterOp

                // Handle delimiter, ensuring it's not inside parentheses or quotes
                if (char != delim[pos] || counters.values.any { it != 0 } || inQuote != null || inUnaryOp) {
                    pos = 0
                    continue
                } else if (pos != delimLen) {
                    pos++
                    continue
                }

                // Yield current split
                yield(expr.substring(start, idx - delimLen))
                start = idx + 1
                pos = 0
                splits++

                // Stop splitting if max splits is reached
                if (maxSplit != null && splits >= maxSplit) {
                    break
                }
            }
            // Yield the remaining part of the string
            yield(expr.substring(start))
        }
    }


}