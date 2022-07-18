package org.kagami.roommate.chat.util

import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun ZonedDateTime.format(): String {
    val currentTime = ZonedDateTime.now()
    val formatter = if (Duration.between(this, currentTime).toDays() >= 1) {
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
    } else {
        DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    }
    return this.format(formatter)
}