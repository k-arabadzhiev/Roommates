package org.diploma.util

fun getRandomString(length: Int = 6): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9') + '_'
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}