package com.tungtata.usbdebugauto

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class LogEntry(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val message: String
) {
    fun toDisplayString(): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return "[${timestamp.format(formatter)}] $message"
    }
}

enum class DetectionMode {
    STRICT, BALANCED, LOOSE
}
