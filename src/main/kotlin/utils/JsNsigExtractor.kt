package com.ar.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mozilla.javascript.Context
import org.mozilla.javascript.NativeArray
import org.mozilla.javascript.Scriptable

class JsNsigExtractor(private val jsCode: String) {

    private suspend fun captureReturnFromEval(formattedFunction: String): Any? = withContext(Dispatchers.IO) {
        val context = Context.enter()

        return@withContext try {
            val scope: Scriptable = context.initStandardObjects()

            val result = context.evaluateString(scope, formattedFunction, "<cmd>", 1, null)
            if (result is NativeArray) {
                val arrayList = ArrayList<Any>()
                for (i in 0 until result.length) {
                    arrayList.add(result.get(i))
                }
                return@withContext arrayList
            }
            result
        } catch (e: Exception) {

            null
        } finally {
            Context.exit()
        }
    }

    fun extractObjectName(code: String): String? {
        val objectPattern = Regex("""([A-Za-z][A-Za-z]+)\.\w+\(""")
        return objectPattern.find(code)?.groupValues?.get(1)
    }

    fun extractVariableAssignment(code: String): String? {
        val variableName = extractObjectName(code) ?: return null


        val variablePattern = Regex("""var\s+$variableName\s*=\s*\{([\s\S]*?)\};""")

        return variablePattern.find(jsCode)?.value
    }

    fun extractNFunctionName():String? {
        val pattern =
            """(?:\.get\("n"\)\)&&\(b=|
        (?:b=String\.fromCharCode\(110\)|
        ([a-zA-Z0-9_$.]+)&&\(b="nn"\[\+\1\])
        )(?:,[a-zA-Z0-9_$]+\(a\))?,c=a\.
        (?:get\(b\)|
        [a-zA-Z0-9_$]+\[b\]\|\|null)\)&&\(c=[A-Za-z0-9]+\[
        """.trimIndent()

        val regex = Regex(pattern, RegexOption.COMMENTS)

        val variableName = regex.find(jsCode)?.value?.substringAfterLast("=")?.substringBefore("[")


        if(variableName != null){
            val variablePattern = Regex("""var\s+$variableName=\[(\w+)\];""")
            return variablePattern.find(jsCode)?.groups?.get(1)?.value
        }else{
            return null
        }

    }

    fun extractNFunctionCode(functionName: String): Pair<String, String>? {
        val funcNameEscaped = Regex.escape(functionName)
        val pattern = """
        (?x)                             # Ignore whitespace and comments in the pattern
        (?:
            function\s+${funcNameEscaped}\s*|            # Match function declaration like "function gna"
            [{;,]\s*${funcNameEscaped}\s*=\s*function\s*|  # Match "gna = function"
            (?:var|const|let)\s+${funcNameEscaped}\s*=\s*function\s* # Match "var gna = function"
        )
        \(
            ([^)]*)
        \)
        \{
            (.*?)
            .join\(""\)
        \}
    """.trimIndent()


        val regex = Regex(pattern, setOf(RegexOption.DOT_MATCHES_ALL))
        val matchResult = regex.find(jsCode)
            ?: throw Exception("Could not find JS function \"$functionName\"")

        val args = matchResult.groups[1]?.value ?: ""
        val code = matchResult.groups[2]?.value ?: ""


        return Pair(args, code)
    }

    suspend fun nFunctionResponse(functionCode: Pair<String, String>?, nSignature: String): String{
        val args = functionCode?.first
        val code = functionCode?.second
        val formattedFunction = "(function(${args}) { ${code} }(\"$nSignature\"))"
        val list = captureReturnFromEval(formattedFunction) as ArrayList<String>

        return list.joinToString("")
    }



}