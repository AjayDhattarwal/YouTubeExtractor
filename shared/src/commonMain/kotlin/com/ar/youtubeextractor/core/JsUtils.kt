package com.ar.youtubeextractor.core

object JsUtils {

    // Set of comparison operators
    val COMP_OPERATORS = setOf("===", "!==", "==", "!=", "<=", ">=", "<", ">")

    // Regular expression for matching valid variable names
    val NAME_RE = "[a-zA-Z_$][\\w$]*".toRegex()

    // Mapping for matching parentheses
    val MATCHING_PARENS = mapOf('(' to ')', '{' to '}', '[' to ']')

    // Set of quote characters
    val QUOTES = setOf('\'', '\"', '/')
}
