package com.tungtata.usbdebugauto.repository

import com.tungtata.usbdebugauto.model.LogEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LogRepository {
    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()

    fun addLog(message: String, level: String = "INFO") {
        val newLog = LogEntry(
            timestamp = System.currentTimeMillis(),
            level = level,
            message = message
        )
        val currentLogs = _logs.value.toMutableList()
        currentLogs.add(newLog)

        // Keep only the last 100 logs in memory
        if (currentLogs.size > 100) {
            currentLogs.removeAt(0)
        }

        _logs.value = currentLogs
    }

    fun addError(message: String) {
        addLog(message, "ERROR")
    }

    fun addWarning(message: String) {
        addLog(message, "WARN")
    }

    fun clearLogs() {
        _logs.value = emptyList()
    }

    fun getLogs(): List<LogEntry> = _logs.value
}
