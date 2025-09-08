package org.malv.utils

fun String.between(start: String, end: String, ignoreCase: Boolean = true): String? {
    val startIndex = indexOf(start, ignoreCase = ignoreCase)
    if (startIndex == -1) {
        return null
    }
    val endIndex = indexOf(end, startIndex + 1, ignoreCase = ignoreCase)
    if (endIndex == -1) {
        return null
    }
    return substring(startIndex + start.length, endIndex)
}
