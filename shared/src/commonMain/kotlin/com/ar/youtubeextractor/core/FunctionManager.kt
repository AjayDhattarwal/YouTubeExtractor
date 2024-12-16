package com.ar.youtubeextractor.core


object FunctionManager {
    private var _mainFunName: String? = null
    private var _signatureFunctionCode: Pair<String, String>? = null
    private var _nFunctionCode: Pair<String, String>? = null
    private var jsCode: String? = null
    private var playerUrl: String? = null
    private var signatureVariable: String? = null



    fun updateMainFunName(newName: String?) {
        _mainFunName = newName
    }


    fun updateSignatureFunctionCode(newCode: Pair<String, String>?) {
        _signatureFunctionCode = newCode
    }


    fun updateNFunctionCode(newCode: Pair<String, String>?) {
        _nFunctionCode = newCode
    }


    fun updateJsCode(newCode: String?) {
        jsCode = newCode
    }


    fun updatePlayerUrl(newUrl: String?) {
        playerUrl = newUrl
    }


    fun updateSignatureVariable(newVar: String?) {
        signatureVariable = newVar
    }


    fun getJsCode(): String? {
        return jsCode
    }


    fun getMainFunName(): String? {
        return _mainFunName
    }


    fun getSignatureFunctionCode(): Pair<String, String>? {
        return _signatureFunctionCode
    }


    fun getNFunctionCode():Pair<String, String>? {
        return _nFunctionCode
    }


    fun getPlayerUrl(): String? {
        return playerUrl
    }


    fun getSignatureVariable(): String? {
        return signatureVariable
    }


    fun displayStoredValues() {
        println("Main Function Name: $_mainFunName")
        println("Signature Function Code: $_signatureFunctionCode")
        println("N Function Code: $_nFunctionCode")
    }
}







//
//@Serializable
//data class FunctionManagerState(
//    val mainFunName: String? = null,
//    val signatureFunctionCode: Pair<String, String>? = null,
//    val nFunctionCode: Pair<String, String>? = null,
//    val jsCode: String? = null,
//    val playerUrl: String? = null,
//    val signatureVariable: String? = null
//)
//
//object FunctionManager {
//    private var _mainFunName: String? = null
//    private var _signatureFunctionCode: Pair<String, String>? = null
//    private var _nFunctionCode: Pair<String, String>? = null
//    private var jsCode: String? = null
//    private var playerUrl: String? = null
//    private var signatureVariable: String? = null
//
//    private val filePath = "function_manager_state.json"
//    private val json = Json { prettyPrint = true }
//
//    // Load data from file if it exists
//    init {
//        loadStateFromFile()
//    }
//
//    // Store the current state to a file
//    private fun saveStateToFile() {
//        val state = FunctionManagerState(
//            _mainFunName,
//            _signatureFunctionCode,
//            _nFunctionCode,
//            jsCode,
//            playerUrl,
//            signatureVariable
//        )
//        val jsonData = json.encodeToString(state)
//        File(filePath).writeText(jsonData)
//    }
//
//    // Load data from the file into memory
//    private fun loadStateFromFile() {
//        val file = File(filePath)
//        if (file.exists()) {
//            val jsonData = file.readText()
//            try {
//                val state = json.decodeFromString<FunctionManagerState>(jsonData)
//                _mainFunName = state.mainFunName
//                _signatureFunctionCode = state.signatureFunctionCode
//                _nFunctionCode = state.nFunctionCode
//                jsCode = state.jsCode
//                playerUrl = state.playerUrl
//                signatureVariable = state.signatureVariable
//            } catch (e: Exception) {
//                println("Error loading saved state: ${e.message}")
//            }
//        }
//    }
//
//    @Synchronized
//    fun updateMainFunName(newName: String?) {
//        _mainFunName = newName
//        saveStateToFile()  // Save to file
//    }
//
//    @Synchronized
//    fun updateSignatureFunctionCode(newCode: Pair<String, String>?) {
//        _signatureFunctionCode = newCode
//        saveStateToFile()  // Save to file
//    }
//
//    @Synchronized
//    fun updateNFunctionCode(newCode: Pair<String, String>?) {
//        _nFunctionCode = newCode
//        saveStateToFile()  // Save to file
//    }
//
//    @Synchronized
//    fun updateJsCode(newCode: String?) {
//        jsCode = newCode
//        saveStateToFile()  // Save to file
//    }
//
//    @Synchronized
//    fun updatePlayerUrl(newUrl: String?) {
//        playerUrl = newUrl
//        saveStateToFile()  // Save to file
//    }
//
//    @Synchronized
//    fun updateSignatureVariable(newVar: String?) {
//        signatureVariable = newVar
//        saveStateToFile()  // Save to file
//    }
//
//    @Synchronized
//    fun getJsCode(): String? {
//        return jsCode
//    }
//
//    @Synchronized
//    fun getMainFunName(): String? {
//        return _mainFunName
//    }
//
//    @Synchronized
//    fun getSignatureFunctionCode(): Pair<String, String>? {
//        return _signatureFunctionCode
//    }
//
//    @Synchronized
//    fun getNFunctionCode(): Pair<String, String>? {
//        return _nFunctionCode
//    }
//
//    @Synchronized
//    fun getPlayerUrl(): String? {
//        return playerUrl
//    }
//
//    @Synchronized
//    fun getSignatureVariable(): String? {
//        return signatureVariable
//    }
//
//    @Synchronized
//    fun displayStoredValues() {
//        println("Main Function Name: $_mainFunName")
//        println("Signature Function Code: $_signatureFunctionCode")
//        println("N Function Code: $_nFunctionCode")
//    }
//}
