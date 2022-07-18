package org.kagami.roommate.chat.util

fun String.toDigits(): String {
    return this.filter { it.isDigit() }
}