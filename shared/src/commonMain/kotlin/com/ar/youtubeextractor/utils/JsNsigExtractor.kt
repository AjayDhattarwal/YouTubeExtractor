package com.ar.youtubeextractor.utils

import com.ar.youtubeextractor.di.captureReturnFromEval
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class JsNsigExtractor(private val jsCode: String) {


    private suspend fun captureReturn(formattedFunction: String): ArrayList<String> = withContext(Dispatchers.IO) {
        try{
            val result = captureReturnFromEval(formattedFunction)
            if (result is Array<*>) {
                // If the array is of a specific type, cast it
                val objectArray = result as Array<Any>

                // Convert to List
                val arrayList = objectArray.toList()
                return@withContext arrayList as ArrayList<String>
            } else {
                return@withContext result as ArrayList<String>
            }
        }catch (e: Exception){
            throw Exception("capture return error : ${e.message}")
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
        val regex = Regex("""([a-zA-Z0-9_${'$'}\{}`]+)=([a-zA-Z0-9_${'$'}\{}`]+)\[\d+\]\([a-zA-Z0-9_${'$'}\{}`]+\),([a-zA-Z0-9_${'$'}\{}`]+)\.set\(([^,]+),([a-zA-Z0-9_${'$'}\{}`]+)\)""")

        // Compile the regex pattern
        val matcher = regex.find(jsCode)

        val variableName = matcher?.groups?.get(2)?.value
        if(variableName != null){
            val variablePattern = Regex("""var\s+$variableName=\[(\w+)\];""")
            return variablePattern.find(jsCode)?.groups?.get(1)?.value
        }else{
            return null
        }

    }

    fun extractNFunctionCode(funcName: String): Pair<String, String>? {
        val funcNameEscaped = Regex.escape(funcName)
        val pattern = """
        (?x)                       
        (?:
            function\s+${funcNameEscaped}\s*|         
            [{;,]\s*${funcNameEscaped}\s*=\s*function\s*| 
            (?:var|const|let)\s+${funcNameEscaped}\s*=\s*function\s* 
        )
        \(
            ([^)]*)
        \)
        \{
            (.*?)
            .join\(""\)
        \}
    """.trimIndent().replace(".", "[\\s\\S]")

        val regex = Regex(pattern)
        val matchResult = regex.find(jsCode)
            ?: throw Exception("Could not find JS N signature function \"$funcName\"")

        val args = matchResult.groups[1]?.value ?: ""
        val code = matchResult.groups[2]?.value ?: ""


        val fixedCode  = fixupNFunctionCode(args.split(","), code)

        return Pair(fixedCode.first.joinToString(","), fixedCode.second)
    }

    private fun fixupNFunctionCode(argNames: List<String>, code: String): Pair<List<String>, String> {
        val regex = Regex(
            """;\s*if\s*\(\s*typeof\s+[a-zA-Z0-9_$]+\s*===?\s*(['"])undefined\1\s*\)\s*return\s+${argNames[0]};"""
        )
        val fixedCode = code.replace(regex, ";")
        return argNames to fixedCode
    }

    suspend fun nFunctionResponse(functionCode: Pair<String, String>?, nSignature: String): String{
        val args = functionCode?.first
        val code = functionCode?.second
        val formattedFunction = "(function(${args}) { ${code} }(\"$nSignature\"))"
        val list = captureReturn(formattedFunction)

        return list.joinToString("")
    }



}